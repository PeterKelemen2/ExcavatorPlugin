package dev.peti.excavator.mining;

import dev.peti.excavator.tools.ExcavatorToolType;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.Sound;
import java.util.List;

public class MiningProcessor {
	private final ProtectionManager protectionManager;

	public MiningProcessor(ProtectionManager protectionManager) {
		this.protectionManager = protectionManager;
	}

	public void process(Player player, Block anchor, ItemStack tool, ExcavatorToolType type) {
		int size = type.getSize();
		List<Block> area = AreaCalculator.calculateArea(anchor, size);
		// Filter blocks
		area.removeIf(block -> !BlockFilter.isValid(block) || !protectionManager.canBreak(player, block));
		if (area.isEmpty()) return;
		boolean creative = player.getGameMode() == GameMode.CREATIVE;
		// Durability precheck
		boolean[] unbreakingRolls;
		int durabilityCost = 0;
		if (!creative) {
			unbreakingRolls = DurabilityManager.simulateUnbreakingRolls(tool, area.size());
			durabilityCost = DurabilityManager.countDurabilityCost(unbreakingRolls);
			if (!DurabilityManager.hasEnoughDurability(tool, durabilityCost)) {
				// Not enough durability, fallback to vanilla
				return;
			}
		}
		// Mining
		for (Block block : area) {
			block.breakNaturally(tool);
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
	}
}

