package dev.pns.tntrun.utils;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import dev.pns.tntrun.constructors.SlimeFileLoader;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.concurrent.CompletableFuture;

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


    public static void copyFileUsingStream(InputStream is, File dest) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

}
