package dev.pns.tntrun.game.tasks;

import dev.pns.tntrun.game.constructors.PowerUpType;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.game.constructors.GameState;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import static dev.pns.tntrun.utils.ItemUtils.createCustomSkull;

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


        World world = ((CraftWorld) spawnLocation.getWorld()).getHandle();
        EntityArmorStand nmsArmorStand = new EntityArmorStand(world, spawnLocation.getX() + 0.5, spawnLocation.getY(), spawnLocation.getZ() + 0.5);
        nmsArmorStand.setInvisible(true);
        nmsArmorStand.setGravity(false);
        nmsArmorStand.setCustomNameVisible(true);
        nmsArmorStand.setCustomName(powerUpType.getColor() + powerUpType.getName());
        nmsArmorStand.setEquipment(4, CraftItemStack.asNMSCopy(createCustomSkull(powerUpType.getTexture())));
        world.addEntity(nmsArmorStand);

        armorStand = (ArmorStand) nmsArmorStand.getBukkitEntity();
    }

    @EventHandler
    public void onTick(TimerEvent e){
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        if (!game.getState().equals(GameState.STARTED)) {
            destroy();
            return;
        }

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
