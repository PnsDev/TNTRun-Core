package dev.pns.tntrun;

import com.grinderwolf.swm.api.SlimePlugin;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.tasks.TimerEventRunnable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.pns.tntrun.utils.SlimeWorldUtils.loadMap;

public final class TNTRun extends JavaPlugin {
    private SlimePlugin slimeWorldLoader;
    @Getter
    private World lobbyWorld;
    private List<Game> games = new ArrayList<>();
    private boolean openForPlayers = false;

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, new TimerEventRunnable(), 0, 1);
        slimeWorldLoader = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");

        long i = System.currentTimeMillis();
        CompletableFuture<Void> mapFuture = loadMap(slimeWorldLoader, "lobby", "lobbyWorld");
        assert mapFuture != null;
        mapFuture.whenComplete((unused, throwable) -> {
            if (throwable != null || Bukkit.getWorld("lobbyWorld") == null) {
                Bukkit.getLogger().warning("Lobby world could not be generated!");
                this.setEnabled(false);
                return;
            }
            lobbyWorld = Bukkit.getWorld("lobbyWorld");
            Bukkit.getLogger().info("Lobby world generated in " + (System.currentTimeMillis() - i) + "ms");
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("Server core is "));
        openForPlayers = false; // Stop players from logging in
        if (lobbyWorld != null) Bukkit.getServer().unloadWorld(lobbyWorld, false);
        slimeWorldLoader = null;
        games.clear();
        games = null;
    }
}
