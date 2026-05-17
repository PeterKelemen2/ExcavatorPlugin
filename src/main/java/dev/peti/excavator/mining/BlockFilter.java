package dev.peti.excavator.mining;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;

public class BlockFilter {
	public static boolean isValid(Block block) {
		if (block == null) return false;
		Material type = block.getType();
		if (type.isAir() || !type.isBlock()) return false;
		// Tile entities (chests, furnaces, etc.) should not be mass-broken
		if (block.getState() instanceof TileState) return false;
		if (!block.getChunk().isLoaded()) return false;
		// Unbreakable blocks (bedrock, barriers, etc.) have hardness < 0
		try {
			if (type.getHardness() < 0) return false;
		} catch (NoSuchMethodError ignored) {}
		// Note: isSolid() is intentionally NOT checked here – it returns false for
		// leaves and other valid breakable blocks (e.g. axe targets).
		return true;
	}
}
