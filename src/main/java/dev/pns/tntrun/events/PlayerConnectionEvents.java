package dev.pns.tntrun.events;

import dev.pns.tntrun.TNTRun;
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
        e.setJoinMessage("§8[§a+§8] §7" + e.getPlayer().getName());
        core.getLobby().addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage("§8[§c-§8] §7" + e.getPlayer().getName());
        if (core.getLobby().isPlayerInLobby(e.getPlayer())) return;
        // TODO: make spectator in case game is active
    }
}
