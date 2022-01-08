package dev.pns.tntrun.constructors;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {
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

    private Player owner;
    private final List<Player> moderators = new ArrayList<>();

    public Game(String name, String description, Player owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public void setGameState(GameState newState) {
        switch (newState) {
            case LOBBY:
                if (!state.equals(GameState.STARTING) && !state.equals(GameState.ENDING)) return;
                // TODO:
                //  Unload the game map
                break;
            case STARTING:
                if (!state.equals(GameState.LOBBY)) return;
                // TODO:
                //  register timer
                //  load the game map
                break;
            case STARTED:
                if (!state.equals(GameState.STARTING)) return;
                // TODO:
                //  make sure map is loaded
                //  teleport players to the game map
                //  show game description animation
                break;
            case ENDING:
                if (!state.equals(GameState.STARTED)) return;
                // TODO:
                //  unregister events
                //  win effects?
                break;
        }
        state = newState;
    }


}
