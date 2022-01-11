package dev.pns.tntrun.constructors;

import com.grinderwolf.swm.api.SlimePlugin;
import dev.pns.tntrun.TNTRun;
import dev.pns.tntrun.game.GameMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.pns.tntrun.utils.SlimeWorldUtils.loadMap;

public class Lobby {
    @Getter
    private World world;
    @Getter
    private GameMap map = null;
    private final List<Player> players = new ArrayList<>();

    public Lobby(TNTRun core, SlimePlugin slimeWorldLoader) {
        try {
            map = new GameMap(new File("/maps/lobby/"));
        } catch (Exception e) {
            e.printStackTrace();
            core.disablePlugin();
            return;
        }

        long i = System.currentTimeMillis();
        CompletableFuture<Void> mapFuture = loadMap(slimeWorldLoader, map.getSlimeWorld(), "lobbyWorld");
        assert mapFuture != null;
        mapFuture.whenComplete((unused, throwable) -> {
            if (throwable != null || Bukkit.getWorld("lobbyWorld") == null) {
                Bukkit.getLogger().warning("Lobby world could not be generated!");
                core.setOpenForPlayers(true);
                return;
            }
            this.world = Bukkit.getWorld("lobbyWorld");
            Bukkit.getLogger().info("Lobby world generated in " + (System.currentTimeMillis() - i) + "ms");
        });
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.teleport(map.getSpawnPoints().get(0).toLocation(world));
        //TODO: Makes scoreboard and inventory
    }

    public boolean isPlayerInLobby(Player player) {
        return players.contains(player);
    }
}