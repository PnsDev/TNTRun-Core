package dev.pns.tntrun.game.tasks;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import dev.pns.tntrun.utils.ScoreHelper;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static dev.pns.tntrun.utils.ChatUtils.prettyTimeFormat;
import static dev.pns.tntrun.utils.ScoreHelper.createScore;
import static dev.pns.tntrun.utils.ScoreHelper.getByPlayer;

@RequiredArgsConstructor
public class ScoreboardUpdater implements Listener {
    private final Game game;

    @EventHandler
    public void playerTracker(TimerEvent e) {
        if (!e.getTimer().equals(TickTimer.TICK_2)) return;
        String durationString = prettyTimeFormat(System.currentTimeMillis() - game.getGameStart());
        String powerUpInString = prettyTimeFormat(((game.getPowerUpRate() * 50L) + 1000 + game.getLastPowerUpSpawn()) - System.currentTimeMillis());
        String date = DateTimeFormatter.ofPattern("dd/MM/yy").format(LocalDateTime.now());

        game.getAllPlayers().forEach(gamePlayer -> {
            ScoreHelper scoreHelper = getByPlayer(gamePlayer.getPlayer());
            if (scoreHelper == null) scoreHelper = createScore(gamePlayer.getPlayer());
            int dj = gamePlayer.getPlayer().getLevel();
            scoreHelper.setTitle("&c&lTNT Labs");
            scoreHelper.setSlotsFromList(Arrays.asList(
                    "§7Duration: " + durationString,
                    "",
                    "Double Jump: " + ((dj > Math.floor(dj / 2f) ? "§a" :(dj > 0 ? "§e" : "§c")) + dj),
                    "",
                    "§fPlayers Alive: §a" + game.getPlayers().size(),
                    "",
                    "PowerUp: §d" + powerUpInString,
                    "",
                    "§7" + date + " §8debug"));
        });
    }
}
