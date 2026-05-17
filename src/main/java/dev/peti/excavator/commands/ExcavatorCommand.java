package dev.peti.excavator.commands;

import dev.peti.excavator.ExcavatorPlugin;
import dev.peti.excavator.mining.PlayerToggleManager;
import dev.peti.excavator.stats.StatsManager;
import dev.peti.excavator.tools.ExcavatorToolType;
import dev.peti.excavator.tools.RecipeManager;
import dev.peti.excavator.tools.ToolFactory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Top-level dispatcher for the {@code /excavator} command.
 * Subcommands: give, toggle, reload, stats.
 */
public class ExcavatorCommand implements CommandExecutor {
	private final ExcavatorPlugin plugin;
	private final ToolFactory toolFactory;
	private final PlayerToggleManager toggleManager;
	private final StatsManager statsManager;
	private final RecipeManager recipeManager;

	public ExcavatorCommand(ExcavatorPlugin plugin,
							ToolFactory toolFactory,
							PlayerToggleManager toggleManager,
							StatsManager statsManager,
							RecipeManager recipeManager) {
		this.plugin = plugin;
		this.toolFactory = toolFactory;
		this.toggleManager = toggleManager;
		this.statsManager = statsManager;
		this.recipeManager = recipeManager;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
							 @NotNull String label, @NotNull String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§eUsage: /excavator <give|toggle|reload|stats>");
			return true;
		}
		String sub = args[0].toLowerCase();
		return switch (sub) {
			case "give" -> handleGive(sender, args);
			case "toggle" -> handleToggle(sender);
			case "reload" -> handleReload(sender);
			case "stats" -> handleStats(sender, args);
			default -> {
				sender.sendMessage("§cUnknown subcommand. Use: give, toggle, reload, stats.");
				yield true;
			}
		};
	}

	// ---- give ----
	private boolean handleGive(CommandSender sender, String[] args) {
		// Keep isOp() for now per project requirement; also accept the permission node.
		if (!sender.isOp() && !sender.hasPermission("excavator.give")) {
			sender.sendMessage("§cYou don't have permission to use this command.");
			return true;
		}
		if (args.length != 4) {
			sender.sendMessage("§cUsage: /excavator give <player> <pickaxe|axe|shovel> <2x2|3x3|5x5>");
			return true;
		}
		Player target = Bukkit.getPlayerExact(args[1]);
		if (target == null) {
			sender.sendMessage("§cPlayer not found: " + args[1]);
			return true;
		}
		String toolArg = args[2];
		String sizeArg = args[3];
		if (!ExcavatorToolType.isValidTool(toolArg)) {
			sender.sendMessage("§cInvalid tool. Use pickaxe, axe, or shovel.");
			return true;
		}
		int size = parseSize(sizeArg);
		if (size == -1) {
			sender.sendMessage("§cInvalid size. Use 2x2, 3x3, or 5x5.");
			return true;
		}
		ExcavatorToolType type = ExcavatorToolType.fromToolAndSize(toolArg, size);
		if (type == null) {
			sender.sendMessage("§cInvalid tool/size combination.");
			return true;
		}
		ItemStack tool = toolFactory.createExcavator(type);
		target.getInventory().addItem(tool);
		sender.sendMessage("§aGave " + type.getDisplayName() + " to " + target.getName() + ".");
		return true;
	}

	// ---- toggle ----
	private boolean handleToggle(CommandSender sender) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("§cOnly players can toggle their excavator state.");
			return true;
		}
		if (!player.hasPermission("excavator.toggle")) {
			player.sendMessage("§cYou don't have permission to toggle the excavator effect.");
			return true;
		}
		boolean nowDisabled = toggleManager.toggle(player.getUniqueId());
		if (nowDisabled) {
			player.sendMessage("§eArea mining §cdisabled§e. Use /excavator toggle to enable again.");
		} else {
			player.sendMessage("§eArea mining §aenabled§e.");
		}
		return true;
	}

	// ---- reload ----
	private boolean handleReload(CommandSender sender) {
		if (!sender.isOp() && !sender.hasPermission("excavator.reload")) {
			sender.sendMessage("§cYou don't have permission to reload the plugin.");
			return true;
		}
		plugin.reloadConfig();
		recipeManager.registerAll();
		statsManager.load();
		sender.sendMessage("§aExcavator configuration, recipes and statistics reloaded.");
		return true;
	}

	// ---- stats ----
	private boolean handleStats(CommandSender sender, String[] args) {
		if (!sender.hasPermission("excavator.stats")) {
			sender.sendMessage("§cYou don't have permission to view statistics.");
			return true;
		}
		UUID targetId;
		String targetName;
		if (args.length >= 2) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
			if (offline.getUniqueId() == null) {
				sender.sendMessage("§cUnknown player: " + args[1]);
				return true;
			}
			targetId = offline.getUniqueId();
			targetName = offline.getName() != null ? offline.getName() : args[1];
		} else if (sender instanceof Player player) {
			targetId = player.getUniqueId();
			targetName = player.getName();
		} else {
			sender.sendMessage("§cUsage: /excavator stats <player>");
			return true;
		}
		long count = statsManager.getBlocks(targetId);
		sender.sendMessage("§e" + targetName + " has area-mined §b" + count + "§e blocks with excavator tools.");
		return true;
	}

	private int parseSize(String arg) {
		return switch (arg) {
			case "2x2" -> 2;
			case "3x3" -> 3;
			case "5x5" -> 5;
			default -> -1;
		};
	}
}

