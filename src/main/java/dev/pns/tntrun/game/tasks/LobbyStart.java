package dev.pns.tntrun.game.tasks;

import com.google.common.collect.Iterables;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.game.constructors.GameState;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import static dev.pns.tntrun.utils.ChatUtils.sendActionBar;

public class LobbyStart implements Listener {
    private final Game game;
    private final int startTicks;
    private int ticksPassed = 0;

    public LobbyStart(Game game, int startTicks) {
        this.game = game;
        this.startTicks = startTicks;
    }

    @EventHandler
    public void onTick(TimerEvent e){
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        if (!game.getState().equals(GameState.STARTING)) {
            HandlerList.unregisterAll(this);
            return;
        }

        Iterable<GamePlayer> allPlayers = Iterables.unmodifiableIterable(Iterables.concat(game.getPlayers(), game.getSpectators()));

        ticksPassed++;
        if (ticksPassed == startTicks) game.setGameState(GameState.STARTED);
        else {
            String text = "§9Starting in §f§l" + Math.round((double) ((startTicks - ticksPassed) / 20)) + "s";
            allPlayers.forEach(gamePlayer -> {
                sendActionBar(gamePlayer.getPlayer(), text);
            });
        }
    }
}
