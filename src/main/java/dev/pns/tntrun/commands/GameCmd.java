package dev.pns.tntrun.commands;

import dev.pns.tntrun.TNTRun;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.game.constructors.GameState;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class GameCmd implements CommandExecutor {
    private final TNTRun core;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command");
            return false;
        }

        Player player = (Player) commandSender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /game <make|info|start|stop|join|leave>");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "make": {
                if (core.getGameManager().getGamePlayer(player) != null) {
                    player.sendMessage(ChatColor.RED + "You are already in a game");
                    return false;
                }
                core.getGameManager().createGame(player);
                return false;
            }
            case "info": {
                if (core.getGameManager().getGamePlayer(player) == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a game");
                    return false;
                }
                Game game = core.getGameManager().getGamePlayer(player).getGame();
                player.sendMessage(ChatColor.GREEN + "Game info:");
                player.sendMessage(ChatColor.GREEN + "UUID: " + game.getGameID());
                player.sendMessage(ChatColor.GREEN + "Name: " + game.getName());
                player.sendMessage(ChatColor.GREEN + "Status: " + game.getState().name());
                player.sendMessage(ChatColor.GREEN + "World Loaded: " + (Bukkit.getWorld(game.getGameID().toString()) == null ? "No" : "Yes"));
                player.sendMessage(ChatColor.GREEN + "Players: " + game.getPlayers().size());
                player.sendMessage(ChatColor.GREEN + "Spectators: " + game.getSpectators().size());
                return false;
            }
            case "join": {
                if (core.getGameManager().getGamePlayer(player) != null) {
                    player.sendMessage(ChatColor.RED + "You are already in a game");
                    return false;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /game join <game-id|player>");
                    return false;
                }
                Game game = null;
                Player targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer != null) {
                    GamePlayer gamePlayer = core.getGameManager().getGamePlayer(targetPlayer);
                    if (gamePlayer != null) game = gamePlayer.getGame();
                } else {
                    try {
                        game = core.getGameManager().getGame(UUID.fromString(args[1]));
                    } catch (Exception ignored) {}
                }

                if (game == null) {
                    player.sendMessage(ChatColor.RED + "Game not found");
                    return false;
                }

                game.joinGame(player);
                return false;
            }
            case "leave": {
                GamePlayer gamePlayer = core.getGameManager().getGamePlayer(player);
                if (gamePlayer == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a game");
                    return false;
                }
                gamePlayer.getGame().removeFromGame(gamePlayer);
                return false;
            }
            case "stop":
            case "start": {
                GamePlayer gamePlayer = core.getGameManager().getGamePlayer(player);
                if (gamePlayer == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a game");
                    return false;
                }
                gamePlayer.getGame().setGameState(args[0].equalsIgnoreCase("start") ? GameState.STARTING : GameState.LOBBY);
                return false;
            }
        }


        return false;
    }
}
