package dev.pns.tntrun.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static dev.pns.tntrun.utils.ChatUtils.formatMessage;
import static dev.pns.tntrun.utils.ReflectionUtil.getCraftBukkitClass;
import static dev.pns.tntrun.utils.ReflectionUtil.getMinecraftClass;

public class ItemUtils {
    // Used for the method `makePlaceableOnMap`
    private static Object placeableTagCompound = null;

    /**
     * Creates a custom skull item with a specific texture.
     * @param texture The texture of the skull.
     * @return The custom skull item.
     */
    public static ItemStack createCustomSkull(String texture) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (texture.isEmpty()) return skull;
        UUID hashAsId = new UUID(texture.hashCode(), texture.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(skull, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + texture + "\"}]}}}");
    }

    /**
     * Allows for easy creation of custom items.
     * @param material The material of the item.
     * @param name The name of the item.
     * @param lore The lore of the item.
     * @return The custom item.
     */
    public static ItemStack itemFactory(Material material, String name, List<String> lore){
        return itemFactory(material, 1, name, lore);
    }

    /**
     * Allows for easy creation of custom items.
     * @param material The material of the item.
     * @param amount The amount of the item.
     * @param name The name of the item.
     * @param lore The lore of the item.
     * @return The custom item.
     */
    public static ItemStack itemFactory(Material material, Integer amount, String name, List<String> lore){
        return itemFactory(new ItemStack(material, amount), name, lore);
    }

    /**
     * Allows for easy creation of custom items.
     * @param item The item.
     * @param name The name of the item.
     * @param lore The lore of the item.
     * @return The custom item.
     */
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

    /**
     * Makes an item placeable on the ground by a player
     * in spectator mode.
     * @param item The item to make placeable.
     * @return The item with the placeable flag.
     */
    public static ItemStack makePlaceableOnMap(ItemStack item){
        try {
            Class<?> craftItemStack = getCraftBukkitClass("inventory.CraftItemStack");
            Object nmsItem = craftItemStack.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class).invoke(null, item);
            if (placeableTagCompound == null) {
                placeableTagCompound = getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
                Object placeable = getMinecraftClass("NBTTagList").getConstructor().newInstance();
                Class<?> nbtTagStringClass = getMinecraftClass("NBTTagString");
                Constructor<?> tagConstructor = nbtTagStringClass.getConstructor(String.class);
                Method add = placeable.getClass().getMethod("add", getMinecraftClass("NBTBase"));
                for (Material material : Material.values()) {
                    add.invoke(placeable, tagConstructor.newInstance("minecraft:" + material.name().toLowerCase()));
                }
                placeableTagCompound.getClass().getMethod("set", String.class, getMinecraftClass("NBTBase")).invoke(placeableTagCompound, "CanPlaceOn", placeable);
            }
            nmsItem.getClass().getMethod("setTag", placeableTagCompound.getClass()).invoke(nmsItem, placeableTagCompound);
            item = (ItemStack) craftItemStack.getMethod("asCraftMirror", nmsItem.getClass()).invoke(null, nmsItem);
            ItemMeta im = item.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            item.setItemMeta(im);
        } catch (Exception e) {e.printStackTrace();}
        return item;

    }
}
