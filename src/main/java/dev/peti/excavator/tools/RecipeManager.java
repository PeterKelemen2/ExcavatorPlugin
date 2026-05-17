package dev.peti.excavator.tools;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers (and re-registers) shaped crafting recipes for excavator tools.
 *
 * <p>Recipe layout (3x3):
 * <pre>
 *   T . T
 *   . E .
 *   T . T
 * </pre>
 * where {@code T} is the configured base tool material and {@code E} is an emerald.
 */
public class RecipeManager {
	private final Plugin plugin;
	private final ToolFactory toolFactory;
	private final List<NamespacedKey> registeredKeys = new ArrayList<>();

	public RecipeManager(Plugin plugin, ToolFactory toolFactory) {
		this.plugin = plugin;
		this.toolFactory = toolFactory;
	}

	/** Registers all excavator recipes. Safe to call multiple times. */
	public void registerAll() {
		unregisterAll();
		for (ExcavatorToolType type : ExcavatorToolType.values()) {
			NamespacedKey key = new NamespacedKey(plugin, "excavator_" + type.name().toLowerCase());
			ItemStack result = toolFactory.createExcavator(type);
			ShapedRecipe recipe = new ShapedRecipe(key, result);
			recipe.shape("T T", " E ", "T T");
			Material corner = toolFactory.getMaterialFor(type);
			recipe.setIngredient('T', corner);
			recipe.setIngredient('E', Material.EMERALD);
			try {
				Bukkit.addRecipe(recipe);
				registeredKeys.add(key);
			} catch (IllegalStateException ex) {
				plugin.getLogger().warning("Failed to register recipe for " + type.name() + ": " + ex.getMessage());
			}
		}
	}

	/** Unregisters every recipe previously added by this manager. */
	public void unregisterAll() {
		for (NamespacedKey key : registeredKeys) {
			Bukkit.removeRecipe(key);
		}
		registeredKeys.clear();
	}
}


