package dev.pns.tntrun.game.tasks;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.GameState;
import dev.pns.tntrun.misc.TickTimer;
import dev.pns.tntrun.misc.TimerEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class LobbyStart implements Listener {
    private final Game game;
    private final int startTicks;
    private int ticksPassed = 0;

    @EventHandler
    public void onTick(TimerEvent e){
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        if (!game.getState().equals(GameState.STARTING)) {
            HandlerList.unregisterAll(this);
            return;
        }
        ticksPassed++;
        // TODO: messages or something
        if (ticksPassed == startTicks) game.setGameState(GameState.STARTED);
    }
}
