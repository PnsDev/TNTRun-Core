package dev.pns.tntrun.events.gameWorlds;

import de.tr7zw.nbtapi.NBTItem;
import dev.pns.tntrun.misc.Lobby;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class DoubleJump implements Listener {
    private final Lobby lobby;

    @EventHandler
    public void doubleJump(PlayerInteractEvent e) {
        if (e.getPlayer().getWorld().equals(lobby.getWorld())) return;
        if (e.getItem() == null || e.getAction().equals(Action.PHYSICAL)) return;
        if (!e.getItem().getType().equals(Material.FEATHER)) return;
        e.setCancelled(true);
        Player p = e.getPlayer();
        NBTItem nbt = new NBTItem(e.getItem());
        Long coolDown = nbt.getLong("coolDown");
        if ((coolDown != 0 && System.currentTimeMillis() - coolDown < 800) || p.getLevel() <= 0) return;
        nbt.setLong("coolDown", System.currentTimeMillis());
        nbt.applyNBT(e.getItem());
        makeDoubleJump(p);
    }

    @EventHandler
    public void onSpaceDoubleJump(PlayerToggleFlightEvent e) {
        if (e.getPlayer().getWorld().equals(lobby.getWorld())) return;
        if (!e.isFlying() || !e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
        e.setCancelled(true);
        e.getPlayer().setAllowFlight(false);
        if (e.getPlayer().getLevel() <= 0) return;
        makeDoubleJump(e.getPlayer());

    }

    private void makeDoubleJump(Player p) {
        p.setAllowFlight(false);
        p.setLevel(p.getLevel() - 1);
        p.setExp(0);
        p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 1, 1);
        p.setVelocity(p.getLocation().getDirection().multiply(.8).add(new Vector(0, .5, 0)));
    }
}
