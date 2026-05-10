package dev.peti.excavator.tools;

import dev.peti.excavator.tools.ExcavatorToolType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ToolFactory {
    private final NamespacedKey excavatorKey;

    public ToolFactory(Plugin plugin) {
        this.excavatorKey = new NamespacedKey(plugin, "excavator_size");
    }

    public ItemStack createExcavator(ExcavatorToolType type, Material baseMaterial) {
        if (!isPickaxe(baseMaterial)) throw new IllegalArgumentException("Base material must be a pickaxe");
        ItemStack item = new ItemStack(baseMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalStateException("ItemMeta is null");
        meta.getPersistentDataContainer().set(excavatorKey, PersistentDataType.INTEGER, type.getSize());
        meta.setDisplayName(type.getDisplayName());
        item.setItemMeta(meta);
        return item;
    }

    private boolean isPickaxe(Material material) {
        return material.name().endsWith("_PICKAXE");
    }

    public NamespacedKey getExcavatorKey() {
        return excavatorKey;
    }
}

