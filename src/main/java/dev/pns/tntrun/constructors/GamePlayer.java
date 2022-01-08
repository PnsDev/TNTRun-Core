package dev.pns.tntrun.constructors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static dev.pns.tntrun.utils.ChatUtils.formatMessage;

@RequiredArgsConstructor @Getter
public class GamePlayer {
    private final Player player;
    private final Game game; // Used to easily interact with the game

    /**
     * Makes the player a spectator in the game
     */
    public void makeSpectator() {
        // TODO: make spec
    }

    /**
     * Teleports the player to the given location
     * @param location the location to teleport to
     */
    public void teleport(Location location){
        player.teleport(location);
    }

    /**
     * Sends a formatted message to the player
     * @param message the message to send
     */
    public void sendMessage(String message) {
        player.sendMessage(formatMessage(message));
    }
}
