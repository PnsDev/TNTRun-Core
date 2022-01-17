package dev.pns.tntrun.constructors;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.GamePlayer;
import dev.pns.tntrun.misc.TickTimer;
import dev.pns.tntrun.misc.TimerEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class PowerUp implements Listener {
    private final Game game;
    @Getter
    private final PowerUpType powerUpType;
    private final Location spawnLocation;
    private boolean goingUp = false;
    private final ArmorStand armorStand;

    private PowerUp(Game game, PowerUpType powerUpType, Location spawnLocation) {
        this.game = game;
        this.powerUpType = powerUpType;
        this.spawnLocation = spawnLocation;
        this.armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
    }

    @EventHandler
    public void onTick(TimerEvent e){
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;

        if (armorStand.getLocation().getY() <= spawnLocation.getY() - 1) goingUp = true;
        else if (armorStand.getLocation().getY() >= spawnLocation.getY()) goingUp = false;

        // Spin animation
        Location newLocation = armorStand.getLocation();
        newLocation.add(0, goingUp ? .01 : -.01, 0);
        newLocation.setYaw(newLocation.getYaw() + 7.5F);
        armorStand.teleport(newLocation);

        // Look for players
        for (Entity entity : armorStand.getNearbyEntities(.8, .6, .8)) {
            if (!entity.getType().equals(EntityType.PLAYER)) continue;
            GamePlayer gamePlayer = game.getGamePlayer((Player) entity);
            if (gamePlayer == null || !game.getPlayers().contains(gamePlayer)) continue;
            powerUpType.getOnPickup().method(gamePlayer.getPlayer());
            destroy();
            //TODO: Add sound and update tab
        }

        if (armorStand.isDead()) HandlerList.unregisterAll(this);
    }

    public void destroy() {
       armorStand.remove();
       HandlerList.unregisterAll(this);
    }

    public static PowerUp spawnPowerUp(Game game, PowerUpType powerUpType, Location spawnLocation) {
        PowerUp powerUp = new PowerUp(game, powerUpType, spawnLocation);
        Bukkit.getPluginManager().registerEvents(powerUp, game.getCore());
        return powerUp;
    }
}
