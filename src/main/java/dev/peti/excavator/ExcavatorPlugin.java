package dev.peti.excavator;

import dev.peti.excavator.commands.GiveToolCommand;
import dev.peti.excavator.commands.GiveToolTabCompleter;
import dev.peti.excavator.listeners.BlockBreakListener;
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

	@Override
	public void onEnable() {
		PaperLib.suggestPaper(this);
		this.toolFactory = new ToolFactory(this);
		this.toolManager = new ToolManager(this);
		getCommand("excavator").setExecutor(new GiveToolCommand(this, toolFactory));
		getCommand("excavator").setTabCompleter(new GiveToolTabCompleter());
		getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
		saveDefaultConfig();
	}

	public ToolFactory getToolFactory() {
		return toolFactory;
	}

	public ToolManager getToolManager() {
		return toolManager;
	}

	public boolean isDebug() {
		return getConfig().getBoolean("debug", false);
	}
}
