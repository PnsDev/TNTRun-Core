package dev.pns.tntrun.commands;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
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
        World world = Bukkit.getWorld("world");
        player.teleport(world.getSpawnLocation());

        player.setLevel(1000);
        player.getInventory().setItem(0, new ItemStack(Material.FEATHER));


        return false;
    }
}
