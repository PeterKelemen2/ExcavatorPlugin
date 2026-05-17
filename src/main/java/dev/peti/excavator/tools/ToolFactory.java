package dev.peti.excavator.tools;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating custom excavator tools.
 */
public class ToolFactory {
	private final Plugin plugin;
	private final NamespacedKey excavatorKey;
	private final NamespacedKey excavatorToolKey;

	/**
	 * Constructs the factory.
	 * @param plugin the plugin instance
	 */
	public ToolFactory(Plugin plugin) {
		this.plugin = plugin;
		this.excavatorKey = new NamespacedKey(plugin, "excavator_size");
		this.excavatorToolKey = new NamespacedKey(plugin, "excavator_tool");
	}

	/**
	 * Resolves the configured material for the given tool type, falling back to the enum default.
	 */
	public Material getMaterialFor(ExcavatorToolType type) {
		String configured = plugin.getConfig().getString("tools." + type.getConfigKey());
		if (configured != null) {
			try {
				Material mat = Material.valueOf(configured.toUpperCase());
				if (mat.name().endsWith("_PICKAXE") || mat.name().endsWith("_AXE") || mat.name().endsWith("_SHOVEL")) {
					return mat;
				}
				plugin.getLogger().warning("Configured material " + configured + " for " + type.getConfigKey()
						+ " is not a tool – falling back to default.");
			} catch (IllegalArgumentException ex) {
				plugin.getLogger().warning("Unknown material '" + configured + "' for " + type.getConfigKey()
						+ " – falling back to default.");
			}
		}
		return type.getMaterial();
	}

	/**
	 * Creates an excavator tool of the correct material for the type.
	 * @param type the tool type
	 * @return the item stack
	 */
	public ItemStack createExcavator(ExcavatorToolType type) {
		Material material = getMaterialFor(type);
		ItemStack item = new ItemStack(material);
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
