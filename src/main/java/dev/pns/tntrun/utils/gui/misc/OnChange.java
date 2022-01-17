package dev.pns.tntrun.utils.gui.misc;

import dev.pns.tntrun.utils.gui.MenuInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface OnChange {
    void onChange(MenuInterface menu, Player player, InventoryClickEvent event);
}
