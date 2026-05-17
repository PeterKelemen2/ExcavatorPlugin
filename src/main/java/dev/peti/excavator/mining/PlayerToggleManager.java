package dev.peti.excavator.mining;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks players who have temporarily disabled the area mining effect via /excavator toggle.
 * State is kept in-memory only and resets on server restart.
 */
public class PlayerToggleManager {
	private final Set<UUID> disabled = new HashSet<>();

	/** @return true if area mining is disabled for the given player */
	public boolean isDisabled(UUID playerId) {
		return disabled.contains(playerId);
	}

	/**
	 * Flips the player's toggle state.
	 * @return the new state (true = disabled, false = enabled)
	 */
	public boolean toggle(UUID playerId) {
		if (disabled.contains(playerId)) {
			disabled.remove(playerId);
			return false;
		}
		disabled.add(playerId);
		return true;
	}
}

