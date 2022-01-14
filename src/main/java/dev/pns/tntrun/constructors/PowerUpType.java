package dev.pns.tntrun.constructors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor @Getter
public enum PowerUpType {

    Speed2(
            "Speed 2 Potion",
            "Grants the person who picks this powerup a speed two potion effect",
            "➔",
            ChatColor.AQUA,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMxMDRmMTlhOTQ1YzYyZTEwMzJkZTZlNmM2MzQyMDY2NDdkOTRlZDljMGE1ODRlNmQ2YjZkM2E0NzVmNTIifX19",
            (Player player) -> {player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3000, 1));}
    ),
    ExtraDJ(
            "Extra DoubleJump",
            "Grants the person who picks this up an extra double jump",
            "⬆",
            ChatColor.GREEN,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNiZGU0MzExMWY2OWE3ZmRhNmVjNmZhZjIyNjNjODI3OTYxZjM5MGQ3YzYxNjNlZDEyMzEwMzVkMWIwYjkifX19",
            (Player player) -> {player.setLevel(player.getLevel() + 1);}
    ),
    Blocks(
            "Placeable Blocks",
            "Grants the person who picks this up 3 placeable blocks",
            "❏",
            ChatColor.RED,
            "", // todo: texture
            (Player player) -> {player.getInventory().addItem(new ItemStack(Material.QUARTZ_BLOCK, 3));}
    ),
    Invisibility(
            "Invisibility",
            "Grants the person who picks this up an invisibility for 12 seconds",
            "☯",
            ChatColor.WHITE,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0=",
            (Player player) -> {player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 240, 1));}
    ),
    JumpPad(
            "Jump Pad",
            "Grants the person who picks this up a jump pad",
            "➚",
            ChatColor.YELLOW,
            "",
            (Player player) -> {player.getInventory().addItem(new ItemStack(Material.PISTON_BASE, 3));}
    );


    private final String name;
    private final String description;
    private final String symbol;
    private final ChatColor color;
    private final String texture;
    private final OnPickup onPickup;

    public interface OnPickup {
        void method(Player player);
    }
}
