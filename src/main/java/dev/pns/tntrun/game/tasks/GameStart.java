package dev.pns.tntrun.game.tasks;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.events.BlockRemoval;
import dev.pns.tntrun.game.events.LocationTracking;
import dev.pns.tntrun.misc.TimerEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class GameStart implements Listener {
    private final Game game;
    private int ticksPassed = 0;

    @EventHandler
    public void onTick(TimerEvent e){
        //todo: game start animation
        ticksPassed++;
        if (ticksPassed == 201) {
            HandlerList.unregisterAll(this);

            LocationTracking locationTracking = new LocationTracking(game);
            Bukkit.getPluginManager().registerEvents(locationTracking, game.getCore());
            game.getListeners().add(locationTracking);

            BlockRemoval blockRemoval = new BlockRemoval(locationTracking);
            Bukkit.getPluginManager().registerEvents(blockRemoval, game.getCore());
            game.getListeners().add(blockRemoval);

            // TODO: powerup timer
            return;
        }
    }
}
