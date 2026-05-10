
package dev.peti.excavator.tools;

import org.bukkit.Material;
import java.util.Collections;
import java.util.List;

public enum ExcavatorToolType {
	EXCAVATOR_2X2(2, "2x2x2 Excavator Pickaxe", Material.IRON_PICKAXE, Collections.singletonList("Small but efficient.")),
	EXCAVATOR_3X3(3, "3x3x3 Excavator Pickaxe", Material.DIAMOND_PICKAXE, Collections.singletonList("Balanced for daily mining.")),
	EXCAVATOR_5X5(5, "5x5x5 Excavator Pickaxe", Material.NETHERITE_PICKAXE, Collections.singletonList("Ultimate area mining tool."));

	private final int size;
	private final String displayName;
	private final Material material;
	private final List<String> lore;

	ExcavatorToolType(int size, String displayName, Material material, List<String> lore) {
		this.size = size;
		this.displayName = displayName;
		this.material = material;
		this.lore = lore;
	}
	public List<String> getLore() {
		return lore;
	}

	public int getSize() {
		return size;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Material getMaterial() {
		return material;
	}

	public static ExcavatorToolType fromSize(int size) {
		for (ExcavatorToolType type : values()) {
			if (type.size == size) return type;
		}
		return null;
	}
}

