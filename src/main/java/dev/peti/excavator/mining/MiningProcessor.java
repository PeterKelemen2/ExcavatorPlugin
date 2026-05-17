package dev.peti.excavator.mining;

import dev.peti.excavator.tools.ExcavatorToolType;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MiningProcessor {
	private final ProtectionManager protectionManager;

	public MiningProcessor(ProtectionManager protectionManager) {
		this.protectionManager = protectionManager;
	}

	/**
	 * Processes an area mine for the given player and tool.
	 *
	 * @return true if at least one block was mined (event should be cancelled), false otherwise
	 */
	public boolean process(Player player, Block anchor, ItemStack tool, ExcavatorToolType type) {
		int size = type.getSize();
		List<Block> area = AreaCalculator.calculateArea(anchor, size, player);
		// Filter blocks
		area.removeIf(block -> !BlockFilter.isValid(block) || !protectionManager.canBreak(player, block));
		if (area.isEmpty()) return false;
		boolean creative = player.getGameMode() == GameMode.CREATIVE;
		// Durability precheck
		boolean[] unbreakingRolls = null;
		int durabilityCost = 0;
		if (!creative) {
			unbreakingRolls = DurabilityManager.simulateUnbreakingRolls(tool, area.size());
			durabilityCost = DurabilityManager.countDurabilityCost(unbreakingRolls);
			if (!DurabilityManager.hasEnoughDurability(tool, durabilityCost)) {
				// Not enough durability, fallback to vanilla
				return false;
			}
		}
		// Mining
		boolean silkTouch = tool != null && tool.containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH);
		int totalXp = 0;
		for (Block block : area) {
			// Fire BlockBreakEvent for plugin compatibility
			BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
			org.bukkit.Bukkit.getPluginManager().callEvent(breakEvent);
			if (breakEvent.isCancelled()) continue;
			Material preBreakType = block.getType();
			block.breakNaturally(tool);
			totalXp += getVanillaXp(preBreakType, silkTouch);
		}
		// Spawn a single batched XP orb at the anchor location
		if (!creative && totalXp > 0) {
			World world = anchor.getWorld();
			ExperienceOrb orb = world.spawn(anchor.getLocation().add(0.5, 0.5, 0.5), ExperienceOrb.class);
			orb.setExperience(totalXp);
		}
		// Durability application
		if (!creative && durabilityCost > 0) {
			DurabilityManager.applyDamage(tool, durabilityCost);
			// Check if tool is broken
			Damageable meta = (Damageable) tool.getItemMeta();
			int max = tool.getType().getMaxDurability();
			if (meta.getDamage() >= max) {
				// Remove the tool and play break animation
				player.getInventory().setItem(EquipmentSlot.HAND, null);
				player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
			}
		}
		return true;
	}

	private int getVanillaXp(Material type, boolean silkTouch) {
		if (silkTouch) return 0;
		return switch (type) {
			case COAL_ORE, DEEPSLATE_COAL_ORE ->
					ThreadLocalRandom.current().nextInt(0, 2); // 0-1

			case COPPER_ORE, DEEPSLATE_COPPER_ORE ->
					ThreadLocalRandom.current().nextInt(7, 11); // 7-10

			case GOLD_ORE, DEEPSLATE_GOLD_ORE ->
					1;

			case NETHER_GOLD_ORE ->
					ThreadLocalRandom.current().nextInt(0, 2); // 0-1

			case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE,
				 EMERALD_ORE, DEEPSLATE_EMERALD_ORE ->
					ThreadLocalRandom.current().nextInt(3, 7); // 3-6

			case LAPIS_ORE, DEEPSLATE_LAPIS_ORE,
				 NETHER_QUARTZ_ORE ->
					ThreadLocalRandom.current().nextInt(2, 5); // 2-4

			case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE ->
					ThreadLocalRandom.current().nextInt(1, 6); // 1-5

			default -> 0;
		};
	}
}
