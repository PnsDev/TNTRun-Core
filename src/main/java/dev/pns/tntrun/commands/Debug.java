package dev.pns.tntrun.commands;

import dev.pns.tntrun.Core;
import dev.pns.tntrun.game.guis.NPCMenu;
import dev.pns.tntrun.game.guis.OpenServers;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class Debug implements CommandExecutor {
    private final Core core;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command");
            return false;
        }

        new NPCMenu(core, (Player) commandSender).open(commandSender);
        return true;
    }
}
