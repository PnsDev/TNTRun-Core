package dev.pns.tntrun.events.gameWorlds;

import dev.pns.tntrun.misc.Lobby;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class PistonGadget implements Listener {
    private final Lobby lobby;
    private final Map<Block, Integer> pistonList = new HashMap<>();

    @EventHandler
    public void onPistonPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getWorld().equals(lobby.getWorld())) return;
        Block blockPlaced = e.getBlockPlaced();
        if (!blockPlaced.getType().equals(Material.PISTON_BASE)) return;

        /**
         * Bug fix: PistonGadget will not work if the block is placed on a
         * block that is already a piston due to the redstone block powering
         * it up.
         */
        if (blockPlaced.getWorld().getBlockAt(blockPlaced.getLocation().add(0, -1, 0)).getType().equals(Material.PISTON_STICKY_BASE)){
            e.setCancelled(true);
            return;
        }

        // make piston always face upwards when placed
        blockPlaced.setData((byte) 0x1);
        pistonList.put(blockPlaced, 0);
    }

    @EventHandler
    public void onTick(TimerEvent e){
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        // Get all pistons that are waiting to be deployed
        Iterator<Map.Entry<Block, Integer>> iterator = pistonList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Block, Integer> entry = iterator.next();
            Block block = entry.getKey();
            Block blockAbove = block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));

            entry.setValue(entry.getValue() + 1);

            switch (entry.getValue()) {
                case 10 -> {
                    block.getWorld().playSound(block.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
                    for (Player p : block.getWorld().getPlayers()) {
                        if (p.getLocation().distance(block.getLocation().add(0.5, 0.5, 0.5)) > 1.7) continue;
                        p.setVelocity(p.getVelocity().setY(2));
                    }
                }
                case 12 -> {
                    blockAbove.setType(Material.PISTON_EXTENSION);
                    PistonExtensionMaterial pe = (PistonExtensionMaterial) blockAbove.getState().getData();
                    blockAbove.getState().setRawData(pe.getData());

                    PistonBaseMaterial piston = (PistonBaseMaterial) block.getState().getData();
                    piston.setPowered(true);
                    block.setData(piston.getData());
                }
                case 20 -> {
                    block.setType(Material.AIR);
                    block.getWorld().playEffect(block.getLocation(), org.bukkit.Effect.STEP_SOUND, Material.PISTON_STICKY_BASE);
                    iterator.remove();
                }
            }
        }
    }
}
