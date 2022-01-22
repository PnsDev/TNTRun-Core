package dev.pns.tntrun.utils;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import dev.pns.tntrun.TNTRun;
import dev.pns.tntrun.misc.SlimeFileLoader;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

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


    public static void saveResource(TNTRun core, String resourcePath, File destinationFolder, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = core.getResource(resourcePath);
        if (in == null) throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");

        File outFile = new File(destinationFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(destinationFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) outDir.mkdirs();

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

}
