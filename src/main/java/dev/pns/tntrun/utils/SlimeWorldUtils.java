package dev.pns.tntrun.utils;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

import java.util.concurrent.CompletableFuture;

public final class SlimeWorldUtils {
    private static final SlimePropertyMap properties = new SlimePropertyMap();
    public static CompletableFuture<Void> loadMap(SlimePlugin slimeWorldLoader, String mapName, String worldName) {
        SlimeLoader fileLoader =  slimeWorldLoader.getLoader("file");
        try {
            CompletableFuture<SlimeWorld> futureSlimeWorld = CompletableFuture.completedFuture(slimeWorldLoader.loadWorld(fileLoader, mapName, true, properties));
            SlimeWorld copiedWorld = futureSlimeWorld.get().clone(worldName);
            return CompletableFuture.runAsync(() -> slimeWorldLoader.generateWorld(copiedWorld));
        } catch (Exception a) {
            /* Exception handling */
            a.printStackTrace();
            return null;
        }
    }
}
