package dev.peti.excavator.listeners;

import dev.peti.excavator.util.InventoryWarningManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

/**
 * Triggers inventory-fullness warnings at the moment a player actually
 * picks up an item, rather than on a fixed-delay timer.
 */
public class InventoryWarningListener implements Listener {
	private final InventoryWarningManager inventoryWarningManager;

	public InventoryWarningListener(InventoryWarningManager inventoryWarningManager) {
		this.inventoryWarningManager = inventoryWarningManager;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		// Check after the event so the item is already counted in the inventory.
		inventoryWarningManager.check(player);
	}
}

