package dev.pns.tntrun.utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class GuiManager implements Listener {

    private final Map<UUID, MenuInterface> inventories;

    public GuiManager(JavaPlugin core) {
        this.inventories = new HashMap<>();
        getServer().getPluginManager().registerEvents(this, core);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (inventories.get(event.getWhoClicked().getUniqueId()) != null && event.getWhoClicked() instanceof Player) {
            inventories.get(event.getWhoClicked().getUniqueId()).clicked(event);
        }
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        if (inventories.get(event.getWhoClicked().getUniqueId()) != null && event.getWhoClicked() instanceof Player) {
            inventories.get(event.getWhoClicked().getUniqueId()).clicked(event);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (inventories.get(event.getPlayer().getUniqueId()) != null && event.getPlayer() instanceof Player) {
            inventories.get(event.getPlayer().getUniqueId()).closed(event);
            removeInventory(event.getPlayer().getUniqueId());
        }
    }

    public void addInventory(UUID uuid, MenuInterface inventory) {
        inventories.put(uuid, inventory);
    }

    public void removeInventory(UUID uuid) {
        inventories.remove(uuid);
    }
}
