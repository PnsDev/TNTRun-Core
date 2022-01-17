package dev.pns.tntrun.utils.gui.misc;

import org.bukkit.inventory.ItemStack;

public class MenuInterfaceButton implements MenuInterfaceLimb {
    private final ItemStack item;
    private final OnClick click;

    public MenuInterfaceButton(ItemStack item) {
        this.item = item;
        this.click = null;
    }

    public MenuInterfaceButton(ItemStack item, OnClick click) {
        this.item = item;
        this.click = click;
    }

    public ItemStack getItem() {
        return item;
    }

    public OnClick getClick() {
        return click;
    }
}
