package dev.peti.excavator.mining;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class AreaCalculator {
	public static List<Block> calculateArea(Block anchor, int size) {
		List<Block> blocks = new ArrayList<>();
		if (size == 2) {
			// 2x2x2: anchor is bottom-left-front, expand +X +Y +Z
			for (int dx = 0; dx < 2; dx++) {
				for (int dy = 0; dy < 2; dy++) {
					for (int dz = 0; dz < 2; dz++) {
						blocks.add(anchor.getWorld().getBlockAt(
								anchor.getX() + dx,
								anchor.getY() + dy,
								anchor.getZ() + dz));
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

