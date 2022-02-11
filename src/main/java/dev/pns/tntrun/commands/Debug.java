package dev.pns.tntrun.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dev.pns.tntrun.utils.ItemUtils.makePlaceableOnMap;

@RequiredArgsConstructor
public class Debug implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command");
            return false;
        }

        Player player = (Player) commandSender;

        player.setItemInHand(makePlaceableOnMap(player.getItemInHand()));



        return false;
    }
}
