package dev.peti.excavator.stats;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Tracks per-player area-mined block counts and persists them to {@code stats.yml}.
 */
public class StatsManager {
	private final Plugin plugin;
	private final File statsFile;
	private final Map<UUID, Long> blocksMined = new HashMap<>();

	public StatsManager(Plugin plugin) {
		this.plugin = plugin;
		this.statsFile = new File(plugin.getDataFolder(), "stats.yml");
		load();
	}

	/** Loads statistics from disk into memory. Safe to call repeatedly (e.g. on reload). */
	public void load() {
		blocksMined.clear();
		if (!statsFile.exists()) {
			return;
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(statsFile);
		for (String key : config.getKeys(false)) {
			try {
				UUID uuid = UUID.fromString(key);
				blocksMined.put(uuid, config.getLong(key));
			} catch (IllegalArgumentException ex) {
				plugin.getLogger().warning("Skipping invalid UUID in stats.yml: " + key);
			}
		}
	}

	/** Saves statistics to disk. */
	public void save() {
		YamlConfiguration config = new YamlConfiguration();
		for (Map.Entry<UUID, Long> entry : blocksMined.entrySet()) {
			config.set(entry.getKey().toString(), entry.getValue());
		}
		try {
			if (!statsFile.getParentFile().exists()) {
				statsFile.getParentFile().mkdirs();
			}
			config.save(statsFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.WARNING, "Failed to save stats.yml", ex);
		}
	}

	/** Increments the player's block-mined counter by {@code amount}. */
	public void addBlocks(UUID playerId, int amount) {
		if (amount <= 0) return;
		blocksMined.merge(playerId, (long) amount, Long::sum);
	}

	/** @return total blocks area-mined by the given player */
	public long getBlocks(UUID playerId) {
		return blocksMined.getOrDefault(playerId, 0L);
	}
}

