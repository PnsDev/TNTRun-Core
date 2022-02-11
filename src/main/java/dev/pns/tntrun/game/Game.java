package dev.pns.tntrun.game;

import com.google.common.collect.Iterables;
import dev.pns.tntrun.Core;
import dev.pns.tntrun.game.constructors.GameMap;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.game.constructors.GameState;
import dev.pns.tntrun.game.constructors.PowerUpType;
import dev.pns.tntrun.game.tasks.GameEnd;
import dev.pns.tntrun.game.tasks.GameStart;
import dev.pns.tntrun.game.tasks.LobbyStart;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static dev.pns.tntrun.utils.ChatUtils.formatMessage;
import static dev.pns.tntrun.utils.ChatUtils.getCenteredMessage;
import static dev.pns.tntrun.utils.SlimeWorldUtils.loadMap;

@Data
public class Game {
    private final Core core;

    @Setter(AccessLevel.NONE)
    private UUID gameID = UUID.randomUUID();
    private String name;
    private String description;
    private int maxPlayers = 16;

    // Game data
    private long gameStart = System.currentTimeMillis();
    private long lastPowerUpSpawn = System.currentTimeMillis();
    private final List<GamePlayer> deathOrder = new ArrayList<>();

    // Players should not be in both sections at the same time
    private final List<GamePlayer> players = new ArrayList<>();
    private final List<GamePlayer> spectators = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private GameState state = GameState.LOBBY;

    private GameMap map = GameMap.getRandomMap();

    // Used for unscheduling events
    private final List<Listener> listeners = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private World world = null;

    private Player owner;
    private final List<Player> moderators = new ArrayList<>();

    // Game Settings
    private boolean randomGameMaps = true;
    private boolean powerUpsEnabled = true;
    private final List<PowerUpType> disabledPowerups = new ArrayList<>();
    private final List<PotionEffect> enabledPotionEffects = new ArrayList<>();
    private int powerUpRate = 1200;
    private int blockBreakSpeed = 6;
    private int speedPotionAmount = 3;
    private int slowPotionAmount = 0;
    private int doubleJumpAmount = 10;
    private boolean pvpEnabled = false;
    private int pvpDamage = 0;
    private int pvpKillDJReward = 1;

    /*
     * TODO:
     * Scoreboard should have
     * game timer
     * player alive
     * double jumps
     * powerUp timer
     */

    public Game(Core core, String name, String description, Player owner) {
        this.core = core;
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    /**
     * Changes the state of the game
     * @param newState The new state
     */
    public void setGameState(GameState newState) {
        switch (newState) {
            case LOBBY:
                if (!state.equals(GameState.STARTING) && !state.equals(GameState.ENDING)) return;

                if (state.equals(GameState.ENDING)) {
                    deathOrder.clear();
                    if (randomGameMaps) map = GameMap.getRandomMap();

                    // Reset all spectators to players
                    Iterator<GamePlayer> iterator = spectators.iterator();
                    while (iterator.hasNext()) {
                        this.players.add(iterator.next());
                        iterator.remove();
                    }
                    // Teleport all players to lobby & deal with visibility
                    Location spawn = core.getLobby().getMap().getSpawnPoints().get(0).toLocation(core.getLobby().getWorld());
                    this.players.forEach(targetPlayer -> {
                        clearPlayer(targetPlayer.getPlayer());
                        targetPlayer.getPlayer().teleport(spawn);
                        this.players.forEach(toBeDisplayed -> targetPlayer.getPlayer().showPlayer(toBeDisplayed.getPlayer()));
                    });

                    Bukkit.unloadWorld(world, false);
                }

                if (world != null && Bukkit.getWorld(world.getName()) != null) Bukkit.unloadWorld(world, false);
                break;
            case STARTING:
                if (!state.equals(GameState.LOBBY)) return;
                loadMap(core.getSlimeWorldLoader(), map.getSlimeWorld(), gameID.toString()).whenComplete((world, throwable) -> {
                    if (throwable != null) return;
                    this.world = Bukkit.getWorld(gameID.toString());
                });
                Bukkit.getPluginManager().registerEvents(new LobbyStart(this, 200), core);
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
                    player.getPlayer().setExp(0);
                    player.getPlayer().setGameMode(GameMode.ADVENTURE);
                    players.forEach(toBeDisplayed -> player.getPlayer().showPlayer(toBeDisplayed.getPlayer()));
                    i = (map.getSpawnPoints().size() > i + 1) ? i + 1 : 0;
                }
                //Anouncement
                sendMessage("&9&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                sendMessage(getCenteredMessage("&e&lTNTRun"));
                sendMessage(" \n");
                sendMessage(getCenteredMessage("&fThe blocks behind you are &cfalling&f!"));
                sendMessage(getCenteredMessage("&fDon't fall into the void."));
                sendMessage(getCenteredMessage("&fLast player standing &awins&f!"));
                sendMessage(" \n");
                sendMessage(getCenteredMessage("&f&lMap&8:"));
                sendMessage(getCenteredMessage("&f" + this.map.getName() + " &7created by &f" + String.join("&8,&f ", this.map.getBuilders())));
                sendMessage("&9&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

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

                sendMessage("&9&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                sendMessage(getCenteredMessage("&e&lTNTRun"));
                sendMessage(" \n");
                sendMessage(getCenteredMessage(players.size() >= 1 ? "&c&l1st Place&r " + players.get(0).getPlayer().getName() : "Uh something broke lmao"));
                if (deathOrder.size() >= 1) sendMessage(getCenteredMessage("&6&l2nd Place&r " + deathOrder.get(deathOrder.size() - 1).getPlayer().getName()));
                if (deathOrder.size() >= 2) sendMessage(getCenteredMessage("&e&l3rd Place&r " + deathOrder.get(deathOrder.size() - 2).getPlayer().getName()));
                sendMessage(" \n");
                sendMessage(getCenteredMessage("&f&lMap&8:"));
                sendMessage(getCenteredMessage("&f" + this.map.getName() + " &7created by &f" + String.join("&8,&f ", this.map.getBuilders())));
                sendMessage("&9&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

                // TODO:
                //  win effects?
                //  update stats?
                Bukkit.getPluginManager().registerEvents(new GameEnd(this), core);
                break;
        }
        state = newState;
    }

    /**
     * Adds a player to the game
     * @param player The player to add
     * @return Whether the player was added
     */
    public boolean joinGame(Player player) {
        if (players.size() >= maxPlayers) return false; //TODO: add bypass permission?
        core.getLobby().getPlayers().remove(player);
        player.getInventory().clear();
        sendMessage("&7[&a+&7] &7" + player.getName());
        if (state.equals(GameState.STARTED) || state.equals(GameState.ENDING)) {
            makeSpectator(new GamePlayer(player, this));
            return true;
        }
        for(GamePlayer gamePlayer : players) {
            gamePlayer.getPlayer().showPlayer(player);
        }
        this.players.add(new GamePlayer(player, this));
        return true;
    }

    /**
     * Gets a gameplayer from the game
     * @param player
     * @return
     */
    public GamePlayer getGamePlayer(Player player) {
        for (GamePlayer gamePlayer : getAllPlayers()) {
            if (gamePlayer.getPlayer().equals(player)) return gamePlayer;
        }
        return null;
    }

    /**
     * Is in game as player
     * @param player
     * @return Whether the player is in the game
     */
    public boolean isPlayer(Player player) {
        GamePlayer gamePlayer = getGamePlayer(player);
        return gamePlayer != null && players.contains(gamePlayer);
    }

    /**
     * Removes a player from the game
     * @param gamePlayer The player to remove
     */
    public void removeFromGame(GamePlayer gamePlayer) {
        if (players.contains(gamePlayer) && state.equals(GameState.STARTED)) makeSpectator(gamePlayer);
        else players.remove(gamePlayer);
        sendMessage("&7[&c-&7] &7" + gamePlayer.getPlayer().getName());
        if (!gamePlayer.getPlayer().isOnline()) {
            spectators.remove(gamePlayer);
            return;
        }
        core.getLobby().getPlayers().add(gamePlayer.getPlayer());
        gamePlayer.getPlayer().teleport(core.getLobby().getMap().getSpawnPoints().get(0).toLocation(core.getLobby().getWorld()));
    }

    /**
     * Makes a player a spectator
     * @param gamePlayer The player to make a spectator
     */
    public void makeSpectator(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();
        player.setHealth(player.getMaxHealth());
        player.setAllowFlight(true);
        player.setFlying(true);
        clearPlayer(player);

        if (players.contains(gamePlayer)) {
            players.remove(gamePlayer);
            sendMessage("&c&lF &f" + player.getName() + " &7has done the rip.");
            deathOrder.add(gamePlayer);
        }
        spectators.add(gamePlayer);
        if (!player.isOnline()) return;
        if (state.equals(GameState.STARTED) || state.equals(GameState.ENDING)) {
            players.forEach(targetPlayer -> targetPlayer.getPlayer().hidePlayer(player));
            spectators.forEach(targetPlayer -> player.showPlayer(targetPlayer.getPlayer()));
            player.teleport(map.getSpawnPoints().get(0).toLocation(world));
        }
        if (players.size() <= 1) setGameState(GameState.ENDING);
    }
    
    public void sendMessage(String message) {
        final String formattedMessage = formatMessage(message);
        getAllPlayers().forEach(gamePlayer -> gamePlayer.getPlayer().sendMessage(formattedMessage));
    }

    public void playSound(Sound sound, float volume, float pitch) {
        getAllPlayers().forEach(gamePlayer -> gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), sound, volume, pitch));
    }

    public Iterable<GamePlayer> getAllPlayers() {
        return Iterables.unmodifiableIterable(Iterables.concat(players, spectators));
    }

    private void clearPlayer(Player player) {
        player.setExp(0);
        player.setLevel(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.setSaturation(40);
        player.setHealth(20);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }


}
