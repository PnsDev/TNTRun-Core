package dev.pns.tntrun;

import com.grinderwolf.swm.api.SlimePlugin;
import dev.pns.tntrun.commands.Debug;
import dev.pns.tntrun.commands.GameCmd;
import dev.pns.tntrun.misc.Lobby;
import dev.pns.tntrun.events.lobbyWorld.LobbyHandling;
import dev.pns.tntrun.events.PlayerConnectionEvents;
import dev.pns.tntrun.game.GameManager;
import dev.pns.tntrun.tasks.TimerEventRunnable;
import dev.pns.tntrun.utils.SlimeWorldUtils;
import dev.pns.tntrun.utils.gui.GuiManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class Core extends JavaPlugin {
    private SlimePlugin slimeWorldLoader;
    private Lobby lobby;
    private GameManager gameManager;
    private GuiManager guiManager;
    @Setter
    private boolean openForPlayers = false;

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, new TimerEventRunnable(), 0, 1);
        slimeWorldLoader = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        gameManager = new GameManager(this);
        guiManager = new GuiManager(this);

        Bukkit.getPluginManager().registerEvents(new PlayerConnectionEvents(this), this);

        Bukkit.getPluginCommand("game").setExecutor(new GameCmd(this));
        Bukkit.getPluginCommand("debug").setExecutor(new Debug());

        loadDefaultMaps();

        lobby = new Lobby(this, slimeWorldLoader);

        Bukkit.getPluginManager().registerEvents(new LobbyHandling(lobby), this);
    }

    @Override
    public void onDisable() {
        openForPlayers = false; // Stop players from logging in (Shouldn't be needed)
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cServer core is restarting"));
        // Unload all games
        gameManager.getGames().forEach(gameManager::destroyGame);
        if (lobby != null && lobby.getWorld() != null) Bukkit.getServer().unloadWorld(lobby.getWorld(), false);
        slimeWorldLoader = null;
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
            if (mapFolder.list().length > 2) return;
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
