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
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import static dev.pns.tntrun.utils.SlimeWorldUtils.unzipFile;

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
            if (!mapFolder.exists()) mapFolder.mkdir();

            // Download the lobby file
            if (!new File(mapFolder, "lobby").exists()) {
                File temp = File.createTempFile("", ".zip");
                Files.copy(getResource("lobby.zip"), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                FileInputStream input = new FileInputStream(temp);
                unzipFile(input, mapFolder);
                temp.delete();
            }

            // Download the default map
            if (!new File(mapFolder, "demo").exists()) {
                File temp = File.createTempFile("", ".zip");
                Files.copy(getResource("demo.zip"), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                FileInputStream input = new FileInputStream(temp);
                unzipFile(input, mapFolder);
                temp.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.setEnabled(false);
        }

    }

    public void disablePlugin() {
        this.setEnabled(false);
    }
}
