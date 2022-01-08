package dev.pns.tntrun.utils;

import org.bukkit.ChatColor;

public final class ChatUtils {
    public static String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
