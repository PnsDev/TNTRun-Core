package dev.pns.tntrun.events;

import dev.pns.tntrun.constructors.TickTimer;
import dev.pns.tntrun.constructors.TimerEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class BlockRemoval implements Listener {
    private final LocationTracking locationTracking;
    private static final int blockRemovalTime = 300; // How many milliseconds should block stay after they've been walked on

    /*
     Interacts with the LocationTracking class to
     remove blocks that have been walked on
     */
    @EventHandler
    public void blockRemover(TimerEvent e) {
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        Iterator<Map.Entry<Block, Long>> it = locationTracking.getToBeRemoved().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Block, Long> entry = it.next();
            if (System.currentTimeMillis() - entry.getValue() < blockRemovalTime) continue;
            entry.getKey().setType(Material.AIR);
            it.remove();
            // TODO: cosmetics
        }
    }

}