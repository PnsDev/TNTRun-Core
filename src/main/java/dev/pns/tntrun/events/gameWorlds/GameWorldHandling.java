package dev.pns.tntrun.events.gameWorlds;

import dev.pns.tntrun.misc.Lobby;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@RequiredArgsConstructor
public class GameWorldHandling implements Listener {
    private final Lobby lobby;

    @EventHandler(priority= EventPriority.LOWEST)
    public void onFallingBlockLand(EntityChangeBlockEvent e) {
        if (e.getEntity().getWorld().equals(lobby.getWorld())) return;
        Entity ent = e.getEntity();
        if (e.getEntityType() == EntityType.FALLING_BLOCK && ent.hasMetadata("dontPlaceMe")) {
            e.setCancelled(true);
            ent.remove();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if (e.getBlock().getWorld().equals(lobby.getWorld()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onSandFall(BlockPhysicsEvent e){
        if (e.getBlock().getWorld().equals(lobby.getWorld())) return;
        if (e.getBlock().getType().toString().contains("PISTON")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onTNT(BlockIgniteEvent e){
        if (e.getBlock().getWorld().equals(lobby.getWorld())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onTNTGoEntity(EntityBlockFormEvent e){
        if (e.getBlock().getWorld().equals(lobby.getWorld())) return;
        if (!e.getEntity().getType().equals(EntityType.PRIMED_TNT)) return;
        e.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent e){
        if (e.getEntity().getWorld().equals(lobby.getWorld())) return;
        e.setCancelled(true);
    }

}
