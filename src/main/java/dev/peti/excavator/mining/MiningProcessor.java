package dev.peti.excavator.mining;

import dev.peti.excavator.tools.ExcavatorToolType;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        if (!creative) {
            int cost = DurabilityManager.simulateUnbreaking(tool, area.size());
            if (!DurabilityManager.hasEnoughDurability(tool, cost)) {
                // Not enough durability, fallback to vanilla
                return;
            }
        }
        // Mining
        for (Block block : area) {
            block.breakNaturally(tool);
        }
        // Durability application
        if (!creative) {
            int cost = DurabilityManager.simulateUnbreaking(tool, area.size());
            DurabilityManager.applyDamage(tool, cost);
        }
    }
}

