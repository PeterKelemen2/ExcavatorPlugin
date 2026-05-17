package dev.peti.excavator.util;

import dev.peti.excavator.ExcavatorPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Sends a non-intrusive action bar notification to the player when their
 * inventory fill ratio crosses one or more configurable thresholds.
 *
 * <p>Warnings are stepped: each threshold is announced at most once per
 * "crossing". If the player frees inventory below a step, that step becomes
 * eligible to fire again next time it is crossed.</p>
 */
public final class InventoryWarningManager {
	/**
	 * Total storage slots in a player's main inventory (excluding armor/offhand).
	 */
	private static final int STORAGE_SLOTS = 36;

	private final ExcavatorPlugin plugin;
	private final Map<UUID, Integer> lastStepByPlayer = new HashMap<>();

	public InventoryWarningManager(ExcavatorPlugin plugin) {
		this.plugin = plugin;
	}


	/**
	 * Checks the player's inventory fullness and sends action bar messages for
	 * every new threshold step crossed since the last check.
	 */
	public void check(Player player) {
		if (player == null) return;
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("inventory-warning");
		if (section == null || !section.getBoolean("enabled", true)) {
			lastStepByPlayer.remove(player.getUniqueId());
			return;
		}

		List<Step> steps = loadSteps(section);
		if (steps.isEmpty()) return;

		double fillRatio = computeFillRatio(player.getInventory());
		int currentStep = -1;
		for (int i = 0; i < steps.size(); i++) {
			if (fillRatio >= steps.get(i).threshold) currentStep = i;
			else break;
		}

		UUID id = player.getUniqueId();
		Integer last = lastStepByPlayer.get(id);
		int lastStep = last == null ? -1 : last;

		if (currentStep < 0) {
			// Below all thresholds; reset so future crossings re-trigger.
			if (lastStep != -1) lastStepByPlayer.remove(id);
			return;
		}

		// Reset bookkeeping if the player has freed enough slots to drop below a step.
		if (currentStep < lastStep) {
			lastStepByPlayer.put(id, currentStep);
			return;
		}

		// Fire every newly crossed step in order, show the most severe one on the action bar.
		if (currentStep > lastStep) {
			Component toShow = null;
			for (int i = lastStep + 1; i <= currentStep; i++) {
				Step step = steps.get(i);
				toShow = Component.text(
						step.message.replace("{percent}", Integer.toString((int) Math.round(fillRatio * 100))),
						step.color);
			}
			if (toShow != null) player.sendActionBar(toShow);
			lastStepByPlayer.put(id, currentStep);
		}
	}

	/**
	 * Forgets warning state for a player (e.g. on logout).
	 */
	public void forget(UUID id) {
		lastStepByPlayer.remove(id);
	}

	private static double computeFillRatio(PlayerInventory inv) {
		ItemStack[] contents = inv.getStorageContents();
		int occupied = 0;
		for (ItemStack stack : contents) {
			if (stack != null && stack.getType() != Material.AIR) occupied++;
		}
		return (double) occupied / STORAGE_SLOTS;
	}

	private List<Step> loadSteps(ConfigurationSection section) {
		List<Map<?, ?>> raw = section.getMapList("steps");
		List<Step> steps = new ArrayList<>();
		for (Map<?, ?> entry : raw) {
			Object t = entry.get("threshold");
			if (!(t instanceof Number)) continue;
			double threshold = ((Number) t).doubleValue();
			if (threshold <= 0.0 || threshold > 1.0) continue;
			String message = entry.get("message") == null
					? "Inventory {percent}% full"
					: entry.get("message").toString();
			TextColor color = parseColor(entry.get("color"));
			steps.add(new Step(threshold, message, color));
		}
		Collections.sort(steps);
		return steps;
	}

	private static TextColor parseColor(Object raw) {
		if (raw == null) return NamedTextColor.YELLOW;
		String s = raw.toString().trim();
		if (s.startsWith("#")) {
			TextColor c = TextColor.fromCSSHexString(s);
			if (c != null) return c;
		}
		NamedTextColor named = NamedTextColor.NAMES.value(s.toLowerCase());
		return named != null ? named : NamedTextColor.YELLOW;
	}

	private static final class Step implements Comparable<Step> {
		final double threshold;
		final String message;
		final TextColor color;

		Step(double threshold, String message, TextColor color) {
			this.threshold = threshold;
			this.message = message;
			this.color = color;
		}

		@Override
		public int compareTo(Step o) {
			return Double.compare(this.threshold, o.threshold);
		}
	}
}

