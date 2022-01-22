package dev.pns.tntrun.game.tasks;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.events.BlockRemoval;
import dev.pns.tntrun.game.events.LocationTracking;
import dev.pns.tntrun.game.events.PowerUpSpawn;
import dev.pns.tntrun.misc.timer.TimerEvent;
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
            System.out.println("Game started");
            HandlerList.unregisterAll(this);

            PowerUpSpawn powerUpSpawn = new PowerUpSpawn(game);
            Bukkit.getPluginManager().registerEvents(powerUpSpawn, game.getCore());
            game.getListeners().add(powerUpSpawn);

            LocationTracking locationTracking = new LocationTracking(game);
            Bukkit.getPluginManager().registerEvents(locationTracking, game.getCore());
            game.getListeners().add(locationTracking);

            BlockRemoval blockRemoval = new BlockRemoval(locationTracking, powerUpSpawn);
            Bukkit.getPluginManager().registerEvents(blockRemoval, game.getCore());
            game.getListeners().add(blockRemoval);
        }
    }
}
