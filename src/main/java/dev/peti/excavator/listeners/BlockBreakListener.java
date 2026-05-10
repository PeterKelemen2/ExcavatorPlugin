
package dev.peti.excavator.listeners;

import dev.peti.excavator.ExcavatorPlugin;
import dev.peti.excavator.mining.MiningProcessor;
import dev.peti.excavator.mining.ProtectionManager;
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
	/** Plugin instance (not exposed externally). */
	private final ExcavatorPlugin plugin;
	private final ToolManager toolManager;
	private final MiningProcessor miningProcessor = new MiningProcessor(new ProtectionManager());

	/**
	 * Constructs the listener.
	 * @param plugin plugin instance (not stored externally)
	 */
	public BlockBreakListener(ExcavatorPlugin plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.toolManager = plugin.getToolManager();
	}

	/**
	 * Handles block break events for Excavator tools.
	 *
	 * @param event the block break event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = player.getInventory().getItemInMainHand();
		plugin.getLogger().info("[DEBUG] BlockBreakEvent triggered for player: " + player.getName());
		if (!toolManager.isExcavator(tool)) {
			plugin.getLogger().info("[DEBUG] Tool is NOT recognized as excavator.");
			return;
		} else {
			plugin.getLogger().info("[DEBUG] Tool IS recognized as excavator.");
		}
		if (RecursionGuard.isProcessing(player.getUniqueId())) {
			plugin.getLogger().info("[DEBUG] RecursionGuard active, skipping.");
			return;
		}
		ExcavatorToolType type = toolManager.getToolType(tool);
		if (type == null) {
			plugin.getLogger().info("[DEBUG] ExcavatorToolType is null (PDC missing or invalid).");
			return;
		}
		plugin.getLogger().info("[DEBUG] Mining pipeline entered for type: " + type);
		RecursionGuard.start(player.getUniqueId());
		try {
			miningProcessor.process(player, event.getBlock(), tool, type);
			event.setCancelled(true);
		} finally {
			RecursionGuard.end(player.getUniqueId());
		}
	}
}
