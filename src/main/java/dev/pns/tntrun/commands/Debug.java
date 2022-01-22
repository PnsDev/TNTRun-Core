package dev.pns.tntrun.commands;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class Debug implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command");
            return false;
        }

        Player player = (Player) commandSender;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityArmorStand nmsArmorStand = new EntityArmorStand(world, player.getLocation().getX() + 0.5, player.getLocation().getY() + 1, player.getLocation().getZ() + 0.5);
        nmsArmorStand.setInvisible(true);
        nmsArmorStand.setGravity(false);
        nmsArmorStand.setCustomNameVisible(true);
        nmsArmorStand.setEquipment(Integer.parseInt(args[0]), CraftItemStack.asNMSCopy(new ItemStack(Material.SKULL_ITEM, 1, (short) 3)));
        world.addEntity(nmsArmorStand);


        return false;
    }
}
