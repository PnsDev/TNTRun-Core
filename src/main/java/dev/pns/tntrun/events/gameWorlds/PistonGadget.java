package dev.pns.tntrun.events.gameWorlds;

import dev.pns.tntrun.misc.Lobby;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.*;

@RequiredArgsConstructor
public class PistonGadget implements Listener {
    private final Lobby lobby;
    private final List<PistonData> pistonDataList = new ArrayList<>();

    public void onPistonPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getWorld().equals(lobby.getWorld())) return;
        Block blockPlaced = e.getBlockPlaced();
        if (!blockPlaced.getType().equals(Material.PISTON_STICKY_BASE)) return;

        //Check block under is not piston to stop extension bug
        if (blockPlaced.getWorld().getBlockAt(blockPlaced.getLocation().add(0, -1, 0)).getType().equals(Material.PISTON_STICKY_BASE)){
            e.setCancelled(true);
            return;
        }

        // make piston face upwards when placed
        blockPlaced.setData((byte) 0x8);
        pistonDataList.add(new PistonData(blockPlaced, blockPlaced.getWorld().getBlockAt(blockPlaced.getLocation().subtract(0, 1, 0)).getType()));
    }

    @EventHandler
    public void onTick(TimerEvent e){
       if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        // Get all pistons that are waiting to be deployed
        Iterator<PistonData> iterator = pistonDataList.iterator();
        while (iterator.hasNext()) {
            PistonData pistonData = iterator.next();
            Block block = pistonData.getBlock();

            pistonData.setTicksPassed(pistonData.getTicksPassed() + 1);
            switch (pistonData.getTicksPassed()) {
                case 10: {
                    block.getWorld().playSound(block.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
                    for (Player p : block.getWorld().getPlayers()) {
                        if (p.getLocation().distance(block.getLocation()) > 1.5) continue;
                        p.setVelocity(p.getVelocity().setY(2));
                    }
                    break;
                }
                case 12: {
                    Block blockUnder = block.getWorld().getBlockAt(block.getLocation().subtract(0, 1, 0));
                }

            }



        }
    }



    @Data
    private class PistonData {
        private final Block block;
        private final Material materialUnder;
        private int ticksPassed = 0;
    }


}
