package dev.pns.tntrun.game.tasks;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.constructors.GameState;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class GameEnd implements Listener {
    private final Game game;
    private int ticksPassed = 0;

    @EventHandler
    public void onTick(TimerEvent e){
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        ticksPassed++;
        if (ticksPassed >= 200) {
            game.setGameState(GameState.LOBBY);
            HandlerList.unregisterAll(this);
        }
    }
}
