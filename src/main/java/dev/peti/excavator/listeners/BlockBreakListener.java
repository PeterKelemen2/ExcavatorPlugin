package dev.peti.excavator.listeners;

import dev.peti.excavator.ExcavatorPlugin;
import dev.peti.excavator.mining.MiningProcessor;
import dev.peti.excavator.mining.PlayerToggleManager;
import dev.peti.excavator.mining.RecursionGuard;
import dev.peti.excavator.tools.ExcavatorToolType;
import dev.peti.excavator.tools.ToolManager;
import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for block break events to handle Excavator tool logic.
 */
public class BlockBreakListener implements Listener {
	private final ExcavatorPlugin plugin;
	private final ToolManager toolManager;
	private final MiningProcessor miningProcessor;
	private final PlayerToggleManager toggleManager;

	public BlockBreakListener(ExcavatorPlugin plugin,
							  MiningProcessor miningProcessor,
							  PlayerToggleManager toggleManager) {
		this.plugin = Objects.requireNonNull(plugin);
		this.toolManager = plugin.getToolManager();
		this.miningProcessor = miningProcessor;
		this.toggleManager = toggleManager;
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = player.getInventory().getItemInMainHand();
		debug("BlockBreakEvent triggered for player: " + player.getName());
		if (!toolManager.isExcavator(tool)) {
			debug("Tool is NOT recognized as excavator.");
			return;
		}
		debug("Tool IS recognized as excavator.");
		// Sneak to disable area effect for a single break
		if (player.isSneaking()) {
			debug("Player is sneaking - skipping area effect.");
			return;
		}
		// Per-player toggle
		if (toggleManager.isDisabled(player.getUniqueId())) {
			debug("Area mining toggled off for this player - skipping.");
			return;
		}
		if (RecursionGuard.isProcessing(player.getUniqueId())) {
			debug("RecursionGuard active, skipping.");
			return;
		}
		ExcavatorToolType type = toolManager.getToolType(tool);
		if (type == null) {
			debug("ExcavatorToolType is null (PDC missing or invalid).");
			return;
		}
		// Permission gate for actually using the area effect
		if (!player.isOp() && !player.hasPermission("excavator.use")) {
			debug("Player lacks excavator.use permission.");
			return;
		}
		debug("Mining pipeline entered for type: " + type);
		RecursionGuard.start(player.getUniqueId());
		try {
			boolean mined = miningProcessor.process(player, event.getBlock(), tool, type);
			if (mined) {
				event.setCancelled(true);
			}
		} finally {
			RecursionGuard.end(player.getUniqueId());
		}
	}

	private void debug(String message) {
		if (plugin.isDebug()) {
			plugin.getLogger().info("[DEBUG] " + message);
		}
	}
}
