package dev.pns.tntrun;

import com.grinderwolf.swm.api.SlimePlugin;
import dev.pns.tntrun.constructors.Lobby;
import dev.pns.tntrun.events.PlayerConnectionEvents;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.tasks.TimerEventRunnable;
import dev.pns.tntrun.utils.SlimeWorldUtils;
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

            SlimeWorldUtils.saveResource(this, "region.slime", mapFolder, true);

            //Files.copy(getResource("region.slime"), Paths.get(mapFolder.getAbsolutePath() + "\\region.slime"), StandardCopyOption.REPLACE_EXISTING);

            // Download the lobby file
            //if (!new File(mapFolder, "lobby").exists()) {


                //Files.copy(getResource("region.slime"), Paths.get(mapFolder.getAbsolutePath() + "\\region.slime"), StandardCopyOption.REPLACE_EXISTING);

                /*
                System.out.println(mapFolder.getAbsolutePath() + "\\lobby.zip");
                Files.copy(getResource("lobby.zip"), Paths.get(mapFolder.getAbsolutePath() + "\\lobby.zip"), StandardCopyOption.REPLACE_EXISTING);
                File lobbyFile = new File(mapFolder, "lobby.zip");
                FileInputStream input = new FileInputStream(lobbyFile);
                unzipFile(input, mapFolder);
                input.close();
                lobbyFile.delete();*/
            //}

            // Download the default map
            /*
            if (!new File(mapFolder, "demo").exists()) {
                Path temp = Files.createTempFile("temp", ".zip");
                Files.copy(getResource("demo.zip"), temp);
                FileInputStream input = new FileInputStream(temp.toFile());
                unzipFile(input, mapFolder);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            this.setEnabled(false);
        }

    }

    public void disablePlugin() {
        this.setEnabled(false);
    }
}
