package dev.pns.tntrun.utils.gui;

import dev.pns.tntrun.utils.gui.misc.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class MenuInterface {
    protected final GuiManager manager;
    protected final Map<Integer, MenuInterfaceLimb> buttons;
    protected final Inventory inventory;

    protected boolean movementLocked = true;

    protected OnClose executeOnClose;
    protected OnClick generalClick;

    public MenuInterface(GuiManager manager, String name, int size) {
        this.manager = manager;
        buttons = new HashMap<>();
        inventory = Bukkit.createInventory(null, size, name);
    }

    public void open(Player player) {
        Validate.notNull(player, "player cannot be null");

        player.closeInventory();
        manager.addInventory(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public void open(CommandSender sender) {
        Validate.notNull(sender, "sender cannot be null");

        if (sender instanceof Player) {
            ((Player) sender).closeInventory();
            manager.addInventory(((Player) sender).getUniqueId(), this);
            ((Player) sender).openInventory(inventory);
        }
    }

    public void add(MenuInterfaceLimb limb) {
        Validate.notNull(limb, "limb cannot be null");

        if (inventory.firstEmpty() != -1) {
            inventory.setItem(inventory.firstEmpty(), limb.getItem());
        } else {
            throw new ArrayIndexOutOfBoundsException("gui is full");
        }

        if (limb instanceof MenuInterfaceButton) {
            buttons.put(inventory.firstEmpty(), limb);
        }
    }

    public void set(int slot, MenuInterfaceLimb button) {
        if (button == null) {
            buttons.remove(slot);
            inventory.setItem(slot, new ItemStack(Material.AIR));
            return;
        }

        if (button instanceof MenuInterfaceButton) {
            buttons.remove(slot);
            buttons.put(slot, button);
            inventory.setItem(slot, button.getItem());
        }
    }

    protected void clicked(InventoryInteractEvent rawEvent) {

        if (rawEvent instanceof InventoryClickEvent) {
            InventoryClickEvent event = (InventoryClickEvent) rawEvent;
            MenuInterfaceLimb button = buttons.get(event.getRawSlot());
            if (movementLocked) {
                event.setCancelled(true);
            }

            if (button != null) {
                if (button instanceof MenuInterfaceButton) {
                    event.setCancelled(true);
                    MenuInterfaceButton btn = (MenuInterfaceButton) button;
                    if (btn.getClick() != null) {
                        OnClick.ButtonAction action = btn.getClick().onClick(event.getWhoClicked(), event.getCurrentItem(), this, event);
                        if (action == OnClick.ButtonAction.CLOSE) {
                            event.getWhoClicked().closeInventory();
                        }
                    }
                } else if (button instanceof ChangeableMenuInterfaceSlot) {
                    ChangeableMenuInterfaceSlot btn = (ChangeableMenuInterfaceSlot) button;
                    if (btn.getChange() != null) {
                        btn.getChange().onChange(this, (Player) event.getWhoClicked(), event);
                    }
                }
            }

            if (generalClick != null) {
                generalClick.onClick(event.getWhoClicked(), event.getCurrentItem(), this, event);
            }
        } else if (rawEvent instanceof InventoryDragEvent) {
            InventoryDragEvent event = (InventoryDragEvent) rawEvent;
            for (int i : event.getRawSlots()) {
                MenuInterfaceLimb button = buttons.get(i);
                if (movementLocked) {
                    event.setCancelled(true);
                }

                if (button != null) {
                    if (button instanceof MenuInterfaceButton) {
                        event.setCancelled(true);
                        MenuInterfaceButton btn = (MenuInterfaceButton) button;
                        if (btn.getClick() != null) {
                            OnClick.ButtonAction action = btn.getClick().onClick(event.getWhoClicked(), event.getNewItems().getOrDefault(i, new ItemStack(Material.AIR)), this, new InventoryClickEvent(
                                    event.getView(),
                                    InventoryType.SlotType.RESULT,
                                    i,
                                    event.getType() == DragType.SINGLE ? ClickType.RIGHT : ClickType.LEFT,
                                    event.getType() == DragType.SINGLE ? InventoryAction.PLACE_ONE : InventoryAction.PLACE_SOME
                            ));
                            if (action == OnClick.ButtonAction.CLOSE) {
                                event.getWhoClicked().closeInventory();
                            }
                        }
                    } else if (button instanceof ChangeableMenuInterfaceSlot) {
                        ChangeableMenuInterfaceSlot btn = (ChangeableMenuInterfaceSlot) button;
                        if (btn.getChange() != null) {
                            btn.getChange().onChange(this, (Player) event.getWhoClicked(), new InventoryClickEvent(
                                    event.getView(),
                                    InventoryType.SlotType.RESULT,
                                    i,
                                    event.getType() == DragType.SINGLE ? ClickType.RIGHT : ClickType.LEFT,
                                    event.getType() == DragType.SINGLE ? InventoryAction.PLACE_ONE : InventoryAction.PLACE_SOME
                            ));
                        }
                    }
                }

                if (generalClick != null) {
                    generalClick.onClick(event.getWhoClicked(), event.getNewItems().getOrDefault(i, new ItemStack(Material.AIR)), this, new InventoryClickEvent(
                            event.getView(),
                            InventoryType.SlotType.RESULT,
                            i,
                            event.getType() == DragType.SINGLE ? ClickType.RIGHT : ClickType.LEFT,
                            event.getType() == DragType.SINGLE ? InventoryAction.PLACE_ONE : InventoryAction.PLACE_SOME
                    ));
                }
            }
        }
    }

    public void executeOnClose(OnClose executeOnClose) {
        Validate.notNull(executeOnClose, "executeOnClose cannot be null");
        this.executeOnClose = executeOnClose;
    }

    protected void closed(InventoryCloseEvent event) {
        if (executeOnClose != null) {
            executeOnClose.onClose(event);
        }
    }

    public void borderise(Material material) {
        ItemStack spacer = new ItemStack(material);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName(ChatColor.BLACK + "_");
        spacer.setItemMeta(spacerMeta);

        for (int i = 0; i < 9; i++) {
            set(i, new MenuInterfaceButton(spacer));
            set(i + inventory.getSize() - 9, new MenuInterfaceButton(spacer));
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % 9 == 0 || (i + 1) % 9 == 0) {
                set(i, new MenuInterfaceButton(spacer));
            }
        }
    }

    public void fill(Material material, int... negate) {
        ItemStack spacer = new ItemStack(material);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName(ChatColor.BLACK + "_");
        spacer.setItemMeta(spacerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            boolean dontDo = false;
            for (int neg : negate) {
                if (neg == i) {
                    dontDo = true;
                    break;
                }
            }

            if (!dontDo) {
                if (inventory.getItem(i) == null) {
                    set(i, new MenuInterfaceButton(spacer));
                }
            }
        }
    }

    public ItemStack[] getContents() {
        return inventory.getContents();
    }

    @Deprecated
    public void setContents(ItemStack[] stacks) {
        inventory.setContents(stacks);
    }

    public void clear() {
        inventory.clear();
        buttons.clear();
    }

    protected void update() { }

    private boolean containsLimbOfType(Class<? extends MenuInterfaceLimb> type) {
        for (MenuInterfaceLimb limb : buttons.values()) {
            if (limb.getClass().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
