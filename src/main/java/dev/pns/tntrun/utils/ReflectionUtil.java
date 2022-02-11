package dev.pns.tntrun.utils;

import org.bukkit.Bukkit;

public class ReflectionUtil {
    private static String mcPath = null;
    private static String craftBukkitPath = null;

    public static Class<?> getMinecraftClass(String className) throws ClassNotFoundException {
        if (mcPath == null) mcPath = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName(mcPath + "." + className);
    }

    public static Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException {
        if (craftBukkitPath == null) craftBukkitPath = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName(craftBukkitPath + "." + className);
    }

}
