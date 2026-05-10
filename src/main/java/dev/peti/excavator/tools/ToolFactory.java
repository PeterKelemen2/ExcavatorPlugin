package dev.peti.excavator.tools;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import dev.peti.excavator.tools.ExcavatorToolType;

/**
 * Factory for creating custom excavator tools.
 */
public class ToolFactory {
	private final NamespacedKey excavatorKey;
	private final NamespacedKey excavatorToolKey;

	/**
	 * Constructs the factory.
	 * @param plugin the plugin instance
	 */
	public ToolFactory(Plugin plugin) {
		this.excavatorKey = new NamespacedKey(plugin, "excavator_size");
		this.excavatorToolKey = new NamespacedKey(plugin, "excavator_tool");
	}

	/**
	 * Creates an excavator tool of the correct material for the type.
	 * @param type the tool type
	 * @return the item stack
	 */
	public ItemStack createExcavator(ExcavatorToolType type) {
		ItemStack item = new ItemStack(type.getMaterial());
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {
			throw new IllegalStateException("ItemMeta is null");
		}
		meta.getPersistentDataContainer().set(excavatorKey, PersistentDataType.INTEGER, type.getSize());
		// Store the tool type as a string: pickaxe, axe, or shovel
		String toolType = type.name().split("_")[0].toLowerCase();
		meta.getPersistentDataContainer().set(excavatorToolKey, PersistentDataType.STRING, toolType);
		meta.setDisplayName(type.getDisplayName());
		meta.setLore(type.getLore());
		item.setItemMeta(meta); // Ensure meta is applied
		return item;
	}

	private boolean isPickaxe(Material material) {
		return material.name().endsWith("_PICKAXE");
	}

	/**
	 * Gets the excavator key.
	 * @return the key
	 */
	public NamespacedKey getExcavatorKey() {
		return excavatorKey;
	}

	public NamespacedKey getExcavatorToolKey() {
		return excavatorToolKey;
	}
}

