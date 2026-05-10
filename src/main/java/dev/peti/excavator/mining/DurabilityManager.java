
package dev.peti.excavator.mining;

import java.security.SecureRandom;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * Utility for handling tool durability and Unbreaking logic.
 */
public class DurabilityManager {
	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Simulate Unbreaking for a batch of blocks. Returns a boolean array where true means durability is consumed for that block.
	 * This must be called ONCE per mining operation and the result reused for both pre-check and application.
	 *
	 * @param tool the tool
	 * @param validBlocks number of blocks
	 * @return boolean array, true if durability is consumed
	 */
	public static boolean[] simulateUnbreakingRolls(ItemStack tool, int validBlocks) {
		int unbreaking = tool.getEnchantmentLevel(Enchantment.UNBREAKING);
		boolean[] rolls = new boolean[validBlocks];
		for (int i = 0; i < validBlocks; i++) {
			// Vanilla: chance to NOT consume durability is unbreaking/(unbreaking+1)
			rolls[i] = (unbreaking <= 0) || (RANDOM.nextInt(unbreaking + 1) == 0);
		}
		return rolls;
	}

	/**
	 * Count how many durability points will be consumed based on Unbreaking rolls.
	 *
	 * @param rolls the rolls array
	 * @return durability cost
	 */
	public static int countDurabilityCost(boolean[] rolls) {
		int cost = 0;
		for (boolean b : rolls) {
			if (b) {
				cost++;
			}
		}
		return cost;
	}

	/**
	 * Checks if the tool has enough durability.
	 *
	 * @param tool the tool
	 * @param cost durability cost
	 * @return true if enough
	 */
	public static boolean hasEnoughDurability(ItemStack tool, int cost) {
		Damageable meta = (Damageable) tool.getItemMeta();
		int current = meta.getDamage();
		int max = tool.getType().getMaxDurability();
		return max - current > cost;
	}

	/**
	 * Applies durability damage to the tool.
	 *
	 * @param tool the tool
	 * @param cost durability cost
	 */
	public static void applyDamage(ItemStack tool, int cost) {
		Damageable meta = (Damageable) tool.getItemMeta();
		meta.setDamage(meta.getDamage() + cost);
		tool.setItemMeta((org.bukkit.inventory.meta.ItemMeta) meta);
	}
}
