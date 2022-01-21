package dev.pns.tntrun.game;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dev.pns.tntrun.TNTRun;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GameManager {
    private final TNTRun core;
    @Getter
    private final List<Game> games = new ArrayList<>();

    /**
     *  Creates a new game
     * @param player the player who owns the game
     */
    public void createGame(Player player) {
        Game game = new Game(core, player.getName() + "'s Game", "A game created by " + player.getName(), player);
        game.joinGame(player);
        games.add(game);
    }

    /**
     * Destroys a game
     * @param game the game
     */
    public void destroyGame(Game game) {
        games.remove(game);
        Iterable<GamePlayer> allPlayers = Iterables.unmodifiableIterable(Iterables.concat(game.getPlayers(), game.getSpectators()));
        allPlayers.forEach(game::removeFromGame);
        game.setGameState(GameState.DESTROYED);
        game.getListeners().forEach(HandlerList::unregisterAll);
        if (game.getWorld() != null) Bukkit.unloadWorld(game.getWorld(), false);
    }

    /**
     * Gets a game by its id
     * @param uuid the id of the game
     * @return the game
     */
    public Game getGame(UUID uuid) {
        for(Game game : games) {
            if (game.getGameID().equals(uuid))
                return game;
        }
        return null;
    }

    /**
     * Gets a game by the player in it
     * @param player the player
     * @return the game
     */
    public GamePlayer getGamePlayer(Player player) {
        for(Game game : games) {
            GamePlayer gamePlayer = game.getGamePlayer(player);
            if (gamePlayer != null) return gamePlayer;
        }
        return null;
    }

}
