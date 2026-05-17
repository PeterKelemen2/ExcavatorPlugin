package dev.peti.excavator;

import dev.peti.excavator.commands.ExcavatorCommand;
import dev.peti.excavator.commands.GiveToolTabCompleter;
import dev.peti.excavator.listeners.BlockBreakListener;
import dev.peti.excavator.mining.MiningProcessor;
import dev.peti.excavator.mining.PlayerToggleManager;
import dev.peti.excavator.mining.ProtectionManager;
import dev.peti.excavator.stats.StatsManager;
import dev.peti.excavator.tools.RecipeManager;
import dev.peti.excavator.tools.ToolFactory;
import dev.peti.excavator.tools.ToolManager;
import io.papermc.lib.PaperLib;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Levi Muniz on 7/29/20.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class ExcavatorPlugin extends JavaPlugin {
	private ToolFactory toolFactory;
	private ToolManager toolManager;
	private PlayerToggleManager toggleManager;
	private StatsManager statsManager;
	private RecipeManager recipeManager;

	@Override
	public void onEnable() {
		PaperLib.suggestPaper(this);
		saveDefaultConfig();

		this.toolFactory = new ToolFactory(this);
		this.toolManager = new ToolManager(this);
		this.toggleManager = new PlayerToggleManager();
		this.statsManager = new StatsManager(this);
		this.recipeManager = new RecipeManager(this, toolFactory);

		MiningProcessor miningProcessor = new MiningProcessor(new ProtectionManager(), statsManager);

		ExcavatorCommand cmd = new ExcavatorCommand(this, toolFactory, toggleManager, statsManager, recipeManager);
		getCommand("excavator").setExecutor(cmd);
		getCommand("excavator").setTabCompleter(new GiveToolTabCompleter());

		getServer().getPluginManager().registerEvents(
				new BlockBreakListener(this, miningProcessor, toggleManager), this);

		recipeManager.registerAll();
	}

	@Override
	public void onDisable() {
		if (recipeManager != null) {
			recipeManager.unregisterAll();
		}
		if (statsManager != null) {
			statsManager.save();
		}
	}

	public ToolFactory getToolFactory() {
		return toolFactory;
	}

	public ToolManager getToolManager() {
		return toolManager;
	}

	public PlayerToggleManager getToggleManager() {
		return toggleManager;
	}

	public StatsManager getStatsManager() {
		return statsManager;
	}

	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	public boolean isDebug() {
		return getConfig().getBoolean("debug", false);
	}
}
