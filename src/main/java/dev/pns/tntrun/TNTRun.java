package dev.pns.tntrun;

import com.grinderwolf.swm.api.SlimePlugin;
import dev.pns.tntrun.constructors.Lobby;
import dev.pns.tntrun.events.PlayerConnectionEvents;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.tasks.TimerEventRunnable;
import dev.pns.tntrun.utils.SlimeWorldUtils;
import dev.pns.tntrun.utils.gui.GuiManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



@Getter
public final class TNTRun extends JavaPlugin {
    private SlimePlugin slimeWorldLoader;
    private Lobby lobby;
    private List<Game> games = new ArrayList<>();
    private GuiManager guiManager;
    @Setter
    private boolean openForPlayers = false;

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, new TimerEventRunnable(), 0, 1);
        slimeWorldLoader = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        guiManager = new GuiManager(this);

        // Register main events
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionEvents(this), this);

        loadDefaultMaps();

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

    private void loadDefaultMaps() {
        try {
            File mapFolder = new File("maps");
            if (!mapFolder.exists() && !mapFolder.mkdir()) throw new Exception("Failed to create maps folder");

            // Load lobby if does not exist
            File lobbyFolder = new File(mapFolder, "lobby");
            if (!lobbyFolder.exists()) lobbyFolder.mkdir();
            File mapFile = new File(lobbyFolder, "region.slime");
            File dataFile = new File(lobbyFolder, "data.yml");
            if (!mapFile.exists()) SlimeWorldUtils.saveResource(this, "lobby/region.slime", mapFolder, true);
            if (!dataFile.exists()) SlimeWorldUtils.saveResource(this, "lobby/data.yml", mapFolder, true);

            // Load default map if no other maps
            if (mapFolder.listFiles().length < 2) return;
            File demoFolder = new File(mapFolder, "oceania");
            demoFolder.mkdir();
            mapFile = new File(demoFolder, "region.slime");
            dataFile = new File(demoFolder, "data.yml");
            if (!mapFile.exists()) SlimeWorldUtils.saveResource(this, "oceania/region.slime", mapFolder, true);
            if (!dataFile.exists()) SlimeWorldUtils.saveResource(this, "oceania/data.yml", mapFolder, true);

        } catch (Exception e) {
            e.printStackTrace();
            this.setEnabled(false);
        }
    }

    public void disablePlugin() {
        this.setEnabled(false);
    }
}
