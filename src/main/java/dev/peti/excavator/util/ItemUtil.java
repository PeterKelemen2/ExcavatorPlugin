package dev.peti.excavator.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
	public static boolean isPickaxe(ItemStack item) {
		if (item == null) return false;
		Material mat = item.getType();
		return mat.name().endsWith("_PICKAXE");
	}
}

