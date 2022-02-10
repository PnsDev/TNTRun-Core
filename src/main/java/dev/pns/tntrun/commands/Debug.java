package dev.pns.tntrun.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class Debug implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command");
            return false;
        }

        Player player = (Player) commandSender;
        World world = Bukkit.getWorld("world");
        player.teleport(world.getSpawnLocation());

        Location loc = player.getLocation();

        try {
            Object mcWorld = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());
            String mcPackage = mcWorld.getClass().getPackage().getName();
            Class<?> nmsItemStack = Class.forName(mcPackage + ".ItemStack");
            Class<?> craftItemStack = Class.forName(mcPackage + ".inventory.CraftItemStack");
            Class<?> armorStandClass = Class.forName(mcPackage + ".EntityArmorStand");
            Object armorStand = armorStandClass.getConstructor(mcWorld.getClass(), double.class, double.class, double.class).newInstance(mcWorld, loc.getX(), loc.getY(), loc.getZ());
            armorStand.getClass().getMethod("setInvisible", boolean.class).invoke(armorStand, true);
            armorStand.getClass().getMethod("setCustomNameVisible", boolean.class).invoke(armorStand, true);
            armorStand.getClass().getMethod("setCustomName", String.class).invoke(armorStand, "odd");

            Object nmsItem = craftItemStack.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class).invoke(null, player.getInventory().getItemInHand());
            armorStand.getClass().getMethod("setEquipment", int.class, nmsItemStack).invoke(armorStand, 4, nmsItem);

            Method addEntityMethod = mcWorld.getClass().getMethod("addEntity", armorStandClass.getSuperclass().getSuperclass());
            addEntityMethod.invoke(mcWorld, armorStand);

            ArmorStand armorStand1 = (ArmorStand) armorStand.getClass().getMethod("getBukkitEntity").invoke(armorStand);
            armorStand1.setGravity(false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }
}
