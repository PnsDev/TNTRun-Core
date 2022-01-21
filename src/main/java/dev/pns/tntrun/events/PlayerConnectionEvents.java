package dev.pns.tntrun.events;

import dev.pns.tntrun.TNTRun;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.GamePlayer;
import dev.pns.tntrun.utils.BarUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionEvents implements Listener {
    private final TNTRun core;

    public PlayerConnectionEvents(TNTRun core) {
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
            else e.getPlayer().sendMessage("§7[§a+§7] §a" + e.getPlayer().getName());
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage("");
        BarUtil.removeBar(e.getPlayer());
        if (core.getLobby().isPlayerInLobby(e.getPlayer())) {
            core.getLobby().getPlayers().forEach(p -> p.sendMessage("§7[§c-§7] §c" + e.getPlayer().getName()));
            return;
        }

        // Remove player from their game
        GamePlayer gamePlayer = core.getGameManager().getGamePlayer(e.getPlayer());
        if (gamePlayer == null) return;
        gamePlayer.getGame().removeFromGame(gamePlayer);
    }
}
