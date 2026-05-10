package dev.peti.excavator.tools;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.NamespacedKey;

public class ToolManager {
    private final NamespacedKey excavatorKey;

    public ToolManager(Plugin plugin) {
        this.excavatorKey = new NamespacedKey(plugin, "excavator_size");
    }

    public boolean isExcavator(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(excavatorKey, PersistentDataType.INTEGER);
    }

    public ExcavatorToolType getToolType(ItemStack item) {
        if (!isExcavator(item)) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Integer size = pdc.get(excavatorKey, PersistentDataType.INTEGER);
        if (size == null) return null;
        return ExcavatorToolType.fromSize(size);
    }
}

