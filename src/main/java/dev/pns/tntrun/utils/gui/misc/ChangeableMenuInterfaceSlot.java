package dev.pns.tntrun.utils.gui.misc;

import org.bukkit.inventory.ItemStack;

public class ChangeableMenuInterfaceSlot implements MenuInterfaceLimb {

    public ItemStack item;
    public OnChange change;

    public ChangeableMenuInterfaceSlot(ItemStack item, OnChange change) {
        this.item = item;
        this.change = change;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    public OnChange getChange() {
        return change;
    }
}
