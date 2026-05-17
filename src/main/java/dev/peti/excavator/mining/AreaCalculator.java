package dev.peti.excavator.mining;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AreaCalculator {

	/**
	 * Calculates the area of blocks to mine.
	 *
	 * @param anchor  the block the player broke
	 * @param size    the side length of the cube (2, 3, or 5)
	 * @param player  the player, used to orient the 2x2 offset correctly
	 * @return mutable list of blocks in the area
	 */
	public static List<Block> calculateArea(Block anchor, int size, Player player) {
		List<Block> blocks = new ArrayList<>();
		if (size == 2) {
			// 2x2x2: expand toward the player's facing direction and up/right
			// Determine per-axis offsets so the area "wraps around" the anchor
			// in the direction the player is facing.
			BlockFace facing = player.getFacing();
			int ox = facing.getModX() >= 0 ? 0 : -1; // shift left if facing -X
			int oz = facing.getModZ() >= 0 ? 0 : -1; // shift left if facing -Z
			// Always expand upward from the anchor
			int oy = 0;
			for (int dx = 0; dx < 2; dx++) {
				for (int dy = 0; dy < 2; dy++) {
					for (int dz = 0; dz < 2; dz++) {
						blocks.add(anchor.getWorld().getBlockAt(
								anchor.getX() + dx + ox,
								anchor.getY() + dy + oy,
								anchor.getZ() + dz + oz));
					}
				}
			}
		} else if (size == 3 || size == 5) {
			int radius = size / 2;
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dy = -radius; dy <= radius; dy++) {
					for (int dz = -radius; dz <= radius; dz++) {
						blocks.add(anchor.getWorld().getBlockAt(
								anchor.getX() + dx,
								anchor.getY() + dy,
								anchor.getZ() + dz));
					}
				}
			}
		}
		return blocks;
	}
}
