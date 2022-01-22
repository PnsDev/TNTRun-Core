package dev.pns.tntrun.events.lobbyWorld;

import dev.pns.tntrun.misc.Lobby;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@RequiredArgsConstructor
public class LobbyHandling implements Listener {
    private final Lobby lobby;

    /*
     * Prevents map editing in lobbies (regardless of if the player is in the lobby or not)
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if (!e.getPlayer().getWorld().equals(lobby.getWorld()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        e.setCancelled(true);
    }
    @EventHandler
    public void onPlace(BlockBreakEvent e){
        if (!e.getPlayer().getWorld().equals(lobby.getWorld()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        e.setCancelled(true);
    }

    /*
     * Fixes deaths? Should not happen but still
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        if (!e.getPlayer().getWorld().equals(lobby.getWorld())) return;
        e.setRespawnLocation(lobby.getMap().getSpawnPoints().get(0).toLocation(lobby.getWorld()));
    }

    /*
     * No Item dropping/Picking
     */
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        if (!e.getPlayer().getWorld().equals(lobby.getWorld()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        e.setCancelled(true);
    }
    @EventHandler
    public void onItemDrop(PlayerPickupItemEvent e){
        if (!e.getPlayer().getWorld().equals(lobby.getWorld()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        e.setCancelled(true);
    }

    /*
     * No Inventory Stuff
     */
    @EventHandler
    public void onItemDrop(InventoryClickEvent e){
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (!lobby.isPlayerInLobby(p) || p.getGameMode().equals(GameMode.CREATIVE)) return;
        e.setCancelled(true);
    }

    /*
     * No Interact with stuff
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if (!e.getPlayer().getWorld().equals(lobby.getWorld()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        e.setCancelled(true);
    }

    /*
     * Prevents falling off and damage from anything
     */
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!lobby.isPlayerInLobby(p)) return;
        e.setCancelled(true);
        if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) p.teleport(lobby.getMap().getSpawnPoints().get(0).toLocation(lobby.getWorld()));
    }
}
