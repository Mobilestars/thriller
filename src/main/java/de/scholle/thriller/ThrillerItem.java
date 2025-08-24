package de.scholle.thriller;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ThrillerItem {

    private static final NamespacedKey KEY = new NamespacedKey(Main.getInstance(), "thriller_pickaxe");

    public static ItemStack create() {
        Material mat = Material.NETHERITE_PICKAXE;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§dThriller Pickaxe");
            meta.setCustomModelData(1001);
            meta.getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isThriller(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Byte value = meta.getPersistentDataContainer().get(KEY, PersistentDataType.BYTE);
        return value != null && value == 1;
    }
}
