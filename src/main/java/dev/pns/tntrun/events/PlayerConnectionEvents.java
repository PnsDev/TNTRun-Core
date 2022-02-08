package dev.pns.tntrun.events;

import dev.pns.tntrun.Core;
import dev.pns.tntrun.game.constructors.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionEvents implements Listener {
    private final Core core;

    public PlayerConnectionEvents(Core core) {
        this.core = core;
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
        if (core.isOpenForPlayers()) return;
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cThis server is not accepting players right now.");
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        core.getLobby().addPlayer(e.getPlayer());
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!core.getLobby().isPlayerInLobby(p)) e.getPlayer().hidePlayer(p);
            else p.sendMessage("§7[§a+§7] §7" + e.getPlayer().getName());
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage("");
        if (core.getLobby().isPlayerInLobby(e.getPlayer())) {
            core.getLobby().getPlayers().forEach(p -> p.sendMessage("§7[§c-§7] §7" + e.getPlayer().getName()));
            return;
        }

        // Remove player from their game
        GamePlayer gamePlayer = core.getGameManager().getGamePlayer(e.getPlayer());
        if (gamePlayer == null) return;
        gamePlayer.getGame().removeFromGame(gamePlayer);
    }
}
