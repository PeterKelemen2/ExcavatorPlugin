package dev.peti.excavator.listeners;

import dev.peti.excavator.ExcavatorPlugin;
import dev.peti.excavator.tools.ToolManager;
import dev.peti.excavator.tools.ExcavatorToolType;
import dev.peti.excavator.mining.MiningProcessor;
import dev.peti.excavator.mining.ProtectionManager;
import dev.peti.excavator.mining.RecursionGuard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class BlockBreakListener implements Listener {
    private final ExcavatorPlugin plugin;
    private final ToolManager toolManager;
    private final MiningProcessor miningProcessor = new MiningProcessor(new ProtectionManager());

    public BlockBreakListener(ExcavatorPlugin plugin) {
        this.plugin = plugin;
        this.toolManager = plugin.getToolManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!toolManager.isExcavator(tool)) return;
        if (RecursionGuard.isProcessing(player.getUniqueId())) return;
        ExcavatorToolType type = toolManager.getToolType(tool);
        if (type == null) return;
        RecursionGuard.start(player.getUniqueId());
        try {
            miningProcessor.process(player, event.getBlock(), tool, type);
            event.setCancelled(true);
        } finally {
            RecursionGuard.end(player.getUniqueId());
        }
    }
}
