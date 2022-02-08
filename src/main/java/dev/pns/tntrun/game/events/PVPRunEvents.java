package dev.pns.tntrun.game.events;

import dev.pns.tntrun.game.Game;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@RequiredArgsConstructor
public class PVPRunEvents implements Listener {
    private final Game game;

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent e) {
        if (!e.getDamager().getWorld().equals(game.getWorld())) return;
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;
        Player damager = (Player) e.getDamager();
        Player damaged = (Player) e.getEntity();

        e.setDamage(game.getPvpDamage());

        if (!game.isPlayer(damaged) || !game.isPlayer(damager)) {
            e.setCancelled(true);
            return;
        }

        /* Atempt to deal the damage to the damaged player if it won't kill them*/
        e.setCancelled(false);
        if (e.getDamage() < damaged.getHealth()) return;

        e.setCancelled(true);
        game.makeSpectator(game.getGamePlayer(damaged));
    }
}
