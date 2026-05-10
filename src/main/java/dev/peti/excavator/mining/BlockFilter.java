package dev.peti.excavator.mining;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;

public class BlockFilter {
	public static boolean isValid(Block block) {
		if (block == null) return false;
		Material type = block.getType();
		if (type.isAir() || !type.isBlock()) return false;
		// Paper API: liquids are not blocks, so isBlock() covers it
		if (block.getState() instanceof TileState) return false;
		if (!block.getChunk().isLoaded()) return false;
		if (!type.isSolid()) return false;
		// Unbreakable blocks
		try {
			if (type.getHardness() < 0) return false;
		} catch (NoSuchMethodError ignored) {}
		return true;
	}
}
