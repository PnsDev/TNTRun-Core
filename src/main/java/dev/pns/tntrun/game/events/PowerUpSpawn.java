package dev.pns.tntrun.game.events;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.constructors.Coordinates;
import dev.pns.tntrun.game.constructors.GameMap;
import dev.pns.tntrun.game.constructors.PowerUpType;
import dev.pns.tntrun.game.tasks.PowerUp;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PowerUpSpawn implements Listener {
    private int ticksPassed = 0;
    private final Game game;
    private final List<Block> possibleBlocks;

    // TODO: make this work with the loading animation
    //  to not lag the server with a large process
    //  200 / (minx-maxx) * (miny-maxy) * (minz-maxz)

    public PowerUpSpawn(Game game) {
        this.game = game;
        game.setLastPowerUpSpawn(System.currentTimeMillis());

        GameMap gameMap = game.getMap();
        possibleBlocks = new ArrayList<>();
        Coordinates min = gameMap.getMinMapCorner();
        Coordinates max = gameMap.getMaxMapCorner();
        // Find all blocks that could be used as powerup holders
        for (int x = (int) Math.ceil(min.getX()); x <= max.getX(); x++) {
            for (int z = (int) Math.ceil(min.getZ()); z <= max.getZ(); z++) {
                for (int y = (int) Math.ceil(min.getY()); y <= max.getY(); y++) {
                    Block block = game.getWorld().getBlockAt(x, y, z);
                    if (block.getType().isSolid() && LocationTracking.getBreakableBlockTypes().contains(block.getType()) && !block.getType().equals(Material.TNT))
                        possibleBlocks.add(block);
                }
            }
        }
        Collections.shuffle(possibleBlocks);
    }

    @EventHandler
    public void onTick(TimerEvent e) {
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        ticksPassed++;
        if (ticksPassed >= game.getPowerUpRate()) {
            ticksPassed = 0;
            Block block = findSuitableBlock();
            PowerUpType powerUpType = PowerUpType.getRandomFiltered(game.getDisabledPowerups());
            if (block == null || powerUpType == null) return;
            PowerUp.spawnPowerUp(game, powerUpType, block.getLocation().add(0.5, 1.5, 0.5));
            game.setLastPowerUpSpawn(System.currentTimeMillis());
        }
    }

    // Used by BlockRemoval to remove the block from the
    // list of eligible blocks as they're removed from
    // the map.
    /**
     * Remove a block from the list of possible blocks for powerups to spawn on.
     * @param block The block to remove.
     */
    public void removeBlock(Block block) {
        possibleBlocks.remove(block);
    }

    /**
     * Find a suitable block to spawn a powerup on.
     * @return The block to spawn a powerup on.
     */
    private Block findSuitableBlock() {
        Iterator<Block> iterator = possibleBlocks.iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            iterator.remove();
            if (block.getType().isSolid() || LocationTracking.getBreakableBlockTypes().contains(block.getType()))
                return block;
        }
        return null;
    }
}
