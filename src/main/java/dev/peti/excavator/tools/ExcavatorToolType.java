
package dev.peti.excavator.tools;

import org.bukkit.Material;
import java.util.Collections;
import java.util.List;

public enum ExcavatorToolType {
	PICKAXE_2X2(2, "2x2x2 Excavator Pickaxe", Material.IRON_PICKAXE, Collections.singletonList("Small but efficient.")),
	PICKAXE_3X3(3, "3x3x3 Excavator Pickaxe", Material.DIAMOND_PICKAXE, Collections.singletonList("Balanced for daily mining.")),
	PICKAXE_5X5(5, "5x5x5 Excavator Pickaxe", Material.NETHERITE_PICKAXE, Collections.singletonList("Ultimate area mining tool.")),

	AXE_2X2(2, "2x2x2 Excavator Axe", Material.IRON_AXE, Collections.singletonList("Small but efficient.")),
	AXE_3X3(3, "3x3x3 Excavator Axe", Material.DIAMOND_AXE, Collections.singletonList("Balanced for daily chopping.")),
	AXE_5X5(5, "5x5x5 Excavator Axe", Material.NETHERITE_AXE, Collections.singletonList("Ultimate area chopping tool.")),

	SHOVEL_2X2(2, "2x2x2 Excavator Shovel", Material.IRON_SHOVEL, Collections.singletonList("Small but efficient.")),
	SHOVEL_3X3(3, "3x3x3 Excavator Shovel", Material.DIAMOND_SHOVEL, Collections.singletonList("Balanced for daily digging.")),
	SHOVEL_5X5(5, "5x5x5 Excavator Shovel", Material.NETHERITE_SHOVEL, Collections.singletonList("Ultimate area digging tool."));

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

	public static ExcavatorToolType fromToolAndSize(String tool, int size) {
		for (ExcavatorToolType type : values()) {
			if (type.name().startsWith(tool.toUpperCase()) && type.size == size) {
				return type;
			}
		}
		return null;
	}

	public static boolean isValidTool(String tool) {
		return tool.equals("pickaxe") || tool.equals("axe") || tool.equals("shovel");
	}
}

