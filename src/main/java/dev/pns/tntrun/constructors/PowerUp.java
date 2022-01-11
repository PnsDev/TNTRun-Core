package dev.pns.tntrun.constructors;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum PowerUp {
    Test("a", "a", "a", (player -> {
        player.sendMessage("test");
    }));

    private final String name;
    private final String description;
    private final String texture;
    private final OnPickup onPickup;

    public interface OnPickup {
        void method(Player player);
    }
}
