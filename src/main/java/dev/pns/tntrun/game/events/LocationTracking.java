package dev.pns.tntrun.game.events;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.game.constructors.TickPosition;
import dev.pns.tntrun.misc.Title;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

import static dev.pns.tntrun.utils.ChatUtils.sendActionBar;

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
    @Getter
    private static final List<Material> breakableBlockTypes = Arrays.asList(Material.SAND, Material.TNT, Material.GRAVEL, Material.QUARTZ_BLOCK); // What block types can be broken by walking on them
    @Getter
    private static final List<Material> allowedBlockTypes = Arrays.asList(Material.PISTON_BASE, Material.PISTON_EXTENSION, Material.PISTON_MOVING_PIECE, Material.PISTON_STICKY_BASE);

    public LocationTracking(Game game) {
        this.game = game;
        game.getPlayers().forEach(gamePlayer -> antiFreeze.put(gamePlayer, new TickPosition(gamePlayer.getPlayer().getLocation())));
    }

    @EventHandler
    public void playerTracker(TimerEvent e) {
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;

        for (GamePlayer gamePlayer : new ArrayList<>(game.getPlayers())) {
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
                    killForCheating(gamePlayer);
                    continue;
                }
            }
            antiFreeze.get(gamePlayer).reset(player.getLocation());

            /*
             * Check if player is standing on something
             * to toggle double jump.
             * (Uses cast to avoid client calculation).
             */
            if (((HumanEntity) player).isOnGround() && !player.getAllowFlight()) player.setAllowFlight(true);

            /*
             * Shows locations of players to the individual players.
             * (Helps a lot with positioning).
             */
            Integer[] spaceLocation = {0, 0, 0};
            game.getPlayers().forEach(gp -> {
                if (!gp.equals(gamePlayer)) {
                    int yDiff = player.getLocation().getBlockY() - gp.getPlayer().getLocation().getBlockY();
                    if (yDiff > 4) spaceLocation[2]++;
                    else if (yDiff < -4) spaceLocation[0]++;
                    else spaceLocation[1]++;
                }
            });
            sendActionBar(player, "§fAbove: §a" + spaceLocation[0] + " §7-§f Same: §a" + spaceLocation[1] + " §7-§f Below:§a " + spaceLocation[2]);

            /*
             * Check if the player is standing on something they
             * shouldn't be (avoiding the anti-freeze and death)
             */
            if (blockUnder.getType().isSolid() && !allowedBlockTypes.contains(blockUnder.getType()) && !breakableBlockTypes.contains(blockUnder.getType())) {
                killForCheating(gamePlayer);
                continue;
            }

            /*
             * Kill player is they're outside the map.
             * This is either because they fell out or got out of the map.
             */
            if (!game.getMap().isLocationInMap(player.getLocation())) {
                new Title("&e&lYou've Died", "&7You're are now a spectator", 5, 20, 5).send(player);
                game.makeSpectator(gamePlayer);
                continue;
            }

            /*
             * Block Removal:
             * Involves adding blocks to the removal section.
             * Mainly checks to see if the player is edging
             * a block, or they're standing on a block.
             */
            Set<Block> blocks = new HashSet<Block>(Collections.singletonList(blockUnder));
            if (blockUnder.getType().equals(Material.AIR)) {
                blocks.add(player.getLocation().clone().add(feetBoundingBoxSize, -0.01, -feetBoundingBoxSize).getBlock());
                blocks.add(player.getLocation().add(feetBoundingBoxSize, -0.01, feetBoundingBoxSize).getBlock());
                blocks.add(player.getLocation().add(-feetBoundingBoxSize, -0.01, feetBoundingBoxSize).getBlock());
                blocks.add(player.getLocation().add(-feetBoundingBoxSize, -0.01, -feetBoundingBoxSize).getBlock());
                blocks.add(player.getLocation().add(0, -0.01, feetBoundingBoxSize).getBlock());
                blocks.add(player.getLocation().add(0, -0.01, -feetBoundingBoxSize).getBlock());
                blocks.add(player.getLocation().add(feetBoundingBoxSize, -0.01, 0).getBlock());
                blocks.add(player.getLocation().add(-feetBoundingBoxSize, -0.01, 0).getBlock());
            } else if (!breakableBlockTypes.contains(blockUnder.getType()) || toBeRemoved.containsKey(blockUnder)) continue;

            // Add blocks to be removed
            for (Block block : blocks) {
                if (toBeRemoved.containsKey(block) || !breakableBlockTypes.contains(block.getType())) continue;
                toBeRemoved.put(block, System.currentTimeMillis());

                if (block.getType().equals(Material.TNT)) {
                    Block above = block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
                    if (breakableBlockTypes.contains(above.getType())) toBeRemoved.put(above, System.currentTimeMillis());
                    continue;
                }

                Block under = block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ());
                if (breakableBlockTypes.contains(under.getType())) toBeRemoved.put(under, System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void spectatorFlight(TimerEvent e) {
        if (!e.getTimer().equals(TickTimer.TICK_6)) return;
        for (GamePlayer gamePlayer : game.getSpectators()) {
            Player player = gamePlayer.getPlayer();
            if (player.getAllowFlight()) continue;
            player.setAllowFlight(true);
        }
    }

    private final static Title cheatingTitle = new Title("&c&lCheating Detected", "&7You're using an unfair advantage and have been killed!", 5, 20, 5);
    private void killForCheating(GamePlayer gamePlayer) {
        game.makeSpectator(gamePlayer);
        cheatingTitle.send(gamePlayer.getPlayer());
        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.GHAST_DEATH, 1, 1);
    }
}
