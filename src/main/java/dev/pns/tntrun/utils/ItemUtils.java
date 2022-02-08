package dev.pns.tntrun.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

import static dev.pns.tntrun.utils.ChatUtils.formatMessage;

public class ItemUtils {
    public static ItemStack createCustomSkull(String texture) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (texture.isEmpty()) return skull;
        UUID hashAsId = new UUID(texture.hashCode(), texture.hashCode());
        Bukkit.getUnsafe().modifyItemStack(skull, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + texture + "\"}]}}}");
        return skull;
    }

    public static ItemStack itemFactory(Material material, String name, List<String> lore){
        return itemFactory(material, 1, name, lore);
    }

    public static ItemStack itemFactory(Material material, Integer amount, String name, List<String> lore){
        return itemFactory(new ItemStack(material, amount), name, lore);
    }

    public static ItemStack itemFactory(ItemStack item, String name, List<String> lore){
        ItemMeta itemMeta = item.getItemMeta();
        if (name == null && lore == null) return item;
        if (name != null) itemMeta.setDisplayName(formatMessage(name));
        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, formatMessage(lore.get(i)));
            }
            itemMeta.setLore(lore);
        }
        item.setItemMeta(itemMeta);
        return item;
    }
}
