package dev.pns.tntrun.utils;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import dev.pns.tntrun.constructors.SlimeFileLoader;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class SlimeWorldUtils {
    private static final SlimePropertyMap properties = new SlimePropertyMap();

    /**
     * Loads the world with the given name.
     * KEEP IN MIND: The file you pass in must be a valid folder (and contain the two required files).
     * @param slimeWorldLoader The loader to use.
     * @param file The folder to load.
     * @param worldName The name of the world.
     * @return The loaded world future.
     */
    public static CompletableFuture<Void> loadMap(SlimePlugin slimeWorldLoader, File file, String worldName) {
        try {
            SlimeFileLoader fileLoader = new SlimeFileLoader(file.getParentFile());
            CompletableFuture<SlimeWorld> futureSlimeWorld = CompletableFuture.completedFuture(slimeWorldLoader.loadWorld(fileLoader, file.getName().replace(".slime", ""), true, properties));
            SlimeWorld copiedWorld = futureSlimeWorld.get().clone(worldName);
            return CompletableFuture.runAsync(() -> slimeWorldLoader.generateWorld(copiedWorld));
        } catch (Exception a) {
            a.printStackTrace();
            return null;
        }
    }


    public static void unzipFile(FileInputStream is, File destDir) throws IOException {

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
