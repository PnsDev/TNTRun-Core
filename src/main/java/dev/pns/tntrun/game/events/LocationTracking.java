package dev.pns.tntrun.game.events;

import dev.pns.tntrun.constructors.*;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.GamePlayer;
import dev.pns.tntrun.misc.TickTimer;
import dev.pns.tntrun.misc.TimerEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class LocationTracking implements Listener {
    private final Game game;
    @Getter
    private final Map<GamePlayer, TickPosition> antiFreeze = new HashMap<>();
    @Getter
    private final Map<Block, Long> toBeRemoved = new HashMap<>();

    /* Internal Game Settings */
    private static final double antiFreezeDistance = .2; // Distance needed to move every tick to be considered not frozen
    private static final double feetBoundingBoxSize = 0.301; // How big is the feet bounding box that deletes blocks
    private static final int allowedFlyTicks = 110; // Used for how many ticks a player can fail anti-freeze before being killed
    private static final List<Material> breakableBlockTypes = Arrays.asList(Material.SAND, Material.TNT, Material.GRAVEL, Material.QUARTZ_BLOCK); // What block types can be broken by walking on them
    private static final List<Material> allowedBlockTypes = Arrays.asList(Material.PISTON_BASE, Material.PISTON_EXTENSION, Material.PISTON_MOVING_PIECE, Material.PISTON_STICKY_BASE);

    public LocationTracking(Game game) {
        this.game = game;
        game.getPlayers().forEach(gamePlayer -> antiFreeze.put(gamePlayer, new TickPosition(gamePlayer.getPlayer().getLocation())));
    }

    @EventHandler
    public void playerTracker(TimerEvent e) {
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;

        game.getPlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.getPlayer();
            Block blockUnder = player.getWorld().getBlockAt(player.getLocation().subtract(0, .65, 0));

            /*
             * Anti-freeze:
             * Sometimes in testing players spam the f5 key to
             * cause their client to lag therefore making them
             * fly for a duration of time. (This can also be
             * caused by right-clicking the top of their minecraft
             * tab).
             * This is a measure to kill players doing this.
             */
            if (!blockUnder.getType().equals(Material.AIR) && !player.getGameMode().equals(GameMode.CREATIVE)) {
                TickPosition tp = antiFreeze.get(gamePlayer);
                if (player.getLocation().distance(tp.getLocation()) <= antiFreezeDistance) tp.addTick();
                if (tp.getTicks() >= allowedFlyTicks) {
                    System.out.println("cheating");
                    game.makeSpectator(gamePlayer);
                }
                return;
            }
            antiFreeze.get(gamePlayer).reset(player.getLocation());

            /*
             * Check if the player is standing on something they
             * shouldn't be (avoiding the anti-freeze and death)
             */
            if (blockUnder.getType().isSolid() && !allowedBlockTypes.contains(blockUnder.getType()) && !breakableBlockTypes.contains(blockUnder.getType())) {
                System.out.println("cheating");
                game.makeSpectator(gamePlayer);
                return;
            }

            /*
             * Kill player is they're outside the map.
             * This is either because they fell out or got out of the map.
             */
            if (!game.getMap().isLocationInMap(player.getLocation())) {
                game.makeSpectator(gamePlayer);
                return;
            }

            /*
             * Block Removal:
             * Involves adding blocks to the removal section.
             * Mainly checks to see if the player is edging
             * a block, or they're standing on a block.
             */
            Set<Block> blocks = new HashSet<Block>();
            if (blockUnder.getType().equals(Material.AIR)) {
                blocks.add(blockUnder.getLocation().clone().add(feetBoundingBoxSize, 0, -feetBoundingBoxSize).getBlock());
                blocks.add(blockUnder.getLocation().add(feetBoundingBoxSize, 0, feetBoundingBoxSize).getBlock());
                blocks.add(blockUnder.getLocation().add(-feetBoundingBoxSize, 0, feetBoundingBoxSize).getBlock());
                blocks.add(blockUnder.getLocation().add(-feetBoundingBoxSize, 0, -feetBoundingBoxSize).getBlock());
                blocks.add(blockUnder.getLocation().add(0, 0, feetBoundingBoxSize).getBlock());
                blocks.add(blockUnder.getLocation().add(0, 0, -feetBoundingBoxSize).getBlock());
                blocks.add(blockUnder.getLocation().add(feetBoundingBoxSize, 0, 0).getBlock());
                blocks.add(blockUnder.getLocation().add(-feetBoundingBoxSize, 0, 0).getBlock());
                blocks.removeIf(block -> !breakableBlockTypes.contains(block.getType()) || toBeRemoved.containsKey(block));
            } else if (breakableBlockTypes.contains(blockUnder.getType()) || !toBeRemoved.containsKey(blockUnder)) blocks.add(blockUnder);

            // Add blocks to be removed
            blocks.forEach(block -> toBeRemoved.put(block, System.currentTimeMillis()));
        });
    }
}
