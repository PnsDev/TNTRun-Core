package dev.pns.tntrun;

import com.grinderwolf.swm.api.SlimePlugin;
import dev.pns.tntrun.constructors.Lobby;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.tasks.TimerEventRunnable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class TNTRun extends JavaPlugin {
    private SlimePlugin slimeWorldLoader;
    private Lobby lobby;
    private List<Game> games = new ArrayList<>();
    @Setter
    private boolean openForPlayers = false;

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, new TimerEventRunnable(), 0, 1);
        slimeWorldLoader = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        lobby = new Lobby(this, slimeWorldLoader);
    }

    @Override
    public void onDisable() {
        openForPlayers = false; // Stop players from logging in (Shouldn't be needed)
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cServer core is restarting"));
        // Unload all game worlds
        games.forEach(game -> {
            if (game.getWorld() != null) Bukkit.unloadWorld(game.getWorld(), false);
        });
        if (lobby != null && lobby.getWorld() != null) Bukkit.getServer().unloadWorld(lobby.getWorld(), false);
        slimeWorldLoader = null;
        games.clear();
        games = null;
    }

    public void disablePlugin() {
        this.setEnabled(false);
    }
}
