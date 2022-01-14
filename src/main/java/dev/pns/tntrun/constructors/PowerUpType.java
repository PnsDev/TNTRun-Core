package dev.pns.tntrun.constructors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
            (Player player) -> {
                player.sendMessage("a");
                ////player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ))
            }
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
