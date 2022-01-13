package dev.pns.tntrun;

import com.grinderwolf.swm.api.SlimePlugin;
import dev.pns.tntrun.constructors.Lobby;
import dev.pns.tntrun.events.PlayerConnectionEvents;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.tasks.TimerEventRunnable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
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

        // Register main events
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionEvents(this), this);

        System.out.println("1");
        loadDefaultMaps();

        System.out.println("3");
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
        File mapFolder = new File("maps");
        if (!mapFolder.exists()) mapFolder.mkdir();
        File lobbyFolder = new File(mapFolder, "lobby");
        if (!lobbyFolder.exists()) lobbyFolder.mkdir();
        try {
            File mapFile = new File(lobbyFolder, "region.slime");
            File dataFile = new File(lobbyFolder, "data.yml");
            if (!mapFile.exists()) Files.copy(getResource("lobby/region.slime"), mapFile.toPath());
            if (!dataFile.exists()) Files.copy(getResource("lobby/data.yml"), dataFile.toPath());

            // Load demo map
            if (mapFolder.listFiles().length < 2) {
                File demoMapFile = new File(mapFolder, "demo");
                if (!demoMapFile.exists()) demoMapFile.mkdir();
                Files.copy(getResource("demo/region.slime"), new File(demoMapFile, "region.slime").toPath());
                Files.copy(getResource("demo/data.yml"), new File(demoMapFile, "data.yml").toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.setEnabled(false);
        }
        System.out.println("2");

    }

    public void disablePlugin() {
        this.setEnabled(false);
    }
}
