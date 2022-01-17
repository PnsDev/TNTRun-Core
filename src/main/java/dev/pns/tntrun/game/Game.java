package dev.pns.tntrun.game;

import dev.pns.tntrun.TNTRun;
import dev.pns.tntrun.constructors.PowerUpType;
import dev.pns.tntrun.game.tasks.GameStart;
import dev.pns.tntrun.game.tasks.LobbyStart;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static dev.pns.tntrun.utils.SlimeWorldUtils.loadMap;

@Getter
public class Game {
    private final TNTRun core;

    private UUID gameID = UUID.randomUUID();
    @Setter
    private String name;
    @Setter
    private String description;
    @Setter
    private int maxPlayers = 16;

    // Players should not be in both sections at the same time
    private final List<GamePlayer> players = new ArrayList<>();
    private final List<GamePlayer> spectators = new ArrayList<>();

    private GameState state = GameState.LOBBY;

    @Setter
    private GameMap map = GameMap.getRandomMap();

    // Used for unscheduling events
    private List<Listener> listeners = new ArrayList<>();


    private World world = null;

    @Setter
    private Player owner;
    private final List<Player> moderators = new ArrayList<>();

    // Game Settings
    @Setter
    private boolean randomGameMaps = true;
    @Setter
    private boolean powerupsEnabled = true;
    private List<PowerUpType> disabledPowerups = new ArrayList<>();
    @Setter
    private double powerupRate =  60;
    @Setter
    private int blockBreakSpeed = 6;
    @Setter
    private int speedPotionAmount = 3;
    @Setter
    private int slowPotionAmount = 0;
    @Setter
    private int doubleJumpAmount = 10;
    @Setter
    private boolean pvpEnabled = false;
    @Setter
    private int pvpDamage = 0;

    public Game(TNTRun core, String name, String description, Player owner) {
        this.core = core;
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public void setGameState(GameState newState) {
        switch (newState) {
            case LOBBY:
                if (!state.equals(GameState.STARTING) && !state.equals(GameState.ENDING)) return;

                if (state.equals(GameState.ENDING)) {
                    if (randomGameMaps) map = GameMap.getRandomMap();

                    // Reset all spectators to players
                    Iterator<GamePlayer> iterator = players.iterator();
                    while (iterator.hasNext()) {
                        this.players.add(iterator.next());
                        iterator.remove();
                    }
                    // Teleport all players to lobby & deal with visibility
                    Location spawn = core.getLobby().getMap().getSpawnPoints().get(0).toLocation(core.getLobby().getWorld());
                    this.players.forEach(targetPlayer -> {
                        targetPlayer.getPlayer().teleport(spawn);
                        this.players.forEach(toBeDisplayed -> {
                            if (toBeDisplayed.equals(targetPlayer))
                                targetPlayer.getPlayer().showPlayer(toBeDisplayed.getPlayer());
                        });
                    });

                    Bukkit.unloadWorld(world, false);
                }

                if (world != null) Bukkit.unloadWorld(world, false);
                break;
            case STARTING:
                if (!state.equals(GameState.LOBBY)) return;
                loadMap(core.getSlimeWorldLoader(), map.getSlimeWorld(), gameID.toString()).whenComplete((world, throwable) -> {
                    if (throwable != null) return;
                    this.world = Bukkit.getWorld(gameID.toString());
                });
                Bukkit.getPluginManager().registerEvents(new LobbyStart(this, 100), core);
                break;
            case STARTED:
                if (!state.equals(GameState.STARTING)) return;
                if (world == null) {
                    setGameState(GameState.LOBBY);
                    return;
                }

                int i = 0;
                for (GamePlayer player : players) {
                    player.getPlayer().teleport(map.getSpawnPoints().get(i).toLocation(world));
                    i = (map.getSpawnPoints().size() > i + 1) ? i + 1 : 0;
                }

                Bukkit.getPluginManager().registerEvents(new GameStart(this), core);
                break;
            case ENDING:
                if (!state.equals(GameState.STARTED)) return;

                // Unregister events
                Iterator<Listener> it = listeners.iterator();
                while (it.hasNext()) {
                    HandlerList.unregisterAll(it.next());
                    it.remove();
                }
                // TODO:
                //  win effects?
                break;
        }
        state = newState;
    }

    public boolean joinGame(Player player) {
        if (players.size() >= maxPlayers) return false; //TODO: add bypass permission?
        core.getLobby().getPlayers().remove(player);
        if (state.equals(GameState.STARTED) || state.equals(GameState.ENDING)) {
            makeSpectator(new GamePlayer(player, this));
            return true;
        }
        this.players.add(new GamePlayer(player, this));
        return true;
    }

    public GamePlayer getGamePlayer(Player player) {
        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.getPlayer().equals(player)) return gamePlayer;
        }
        for (GamePlayer gamePlayer : spectators) {
            if (gamePlayer.getPlayer().equals(player)) return gamePlayer;
        }
        return null;
    }

    public void removeFromGame(GamePlayer gamePlayer) {
        if (players.contains(gamePlayer)) makeSpectator(gamePlayer);
        if (!gamePlayer.getPlayer().isOnline()) {
            spectators.remove(gamePlayer);
            return;
        }
        gamePlayer.getPlayer().teleport(core.getLobby().getMap().getSpawnPoints().get(0).toLocation(core.getLobby().getWorld()));
    }

    public void makeSpectator(GamePlayer gamePlayer) {
        if (players.contains(gamePlayer)) {
            players.remove(gamePlayer);
            // TODO: death message
        }
        spectators.add(gamePlayer);
        if (!gamePlayer.getPlayer().isOnline()) return;
        if (state.equals(GameState.STARTED) || state.equals(GameState.ENDING)) {
            players.forEach(targetPlayer -> targetPlayer.getPlayer().hidePlayer(gamePlayer.getPlayer()));
            spectators.forEach(targetPlayer -> gamePlayer.getPlayer().showPlayer(targetPlayer.getPlayer()));
            gamePlayer.getPlayer().teleport(map.getSpawnPoints().get(0).toLocation(world));
        }
    }


}
