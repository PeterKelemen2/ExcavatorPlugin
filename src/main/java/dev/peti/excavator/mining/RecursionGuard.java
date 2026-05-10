package dev.peti.excavator.mining;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RecursionGuard {
	private static final Set<UUID> activePlayers = new HashSet<>();

	public static boolean isProcessing(UUID playerId) {
		return activePlayers.contains(playerId);
	}

	public static void start(UUID playerId) {
		activePlayers.add(playerId);
	}

	public static void end(UUID playerId) {
		activePlayers.remove(playerId);
	}
}

