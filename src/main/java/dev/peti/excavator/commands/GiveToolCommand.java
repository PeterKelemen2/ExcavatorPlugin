package dev.peti.excavator.commands;

import dev.peti.excavator.tools.ExcavatorToolType;
import dev.peti.excavator.tools.ToolFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


/**
 * Command executor for giving excavator tools to players.
 */
public class GiveToolCommand implements CommandExecutor {
	private final ToolFactory toolFactory;

	/**
	 * Constructs the command executor.
	 * @param plugin the plugin instance (unused)
	 * @param toolFactory the tool factory
	 */
	public GiveToolCommand(Plugin plugin, ToolFactory toolFactory) {
		this.toolFactory = toolFactory;
	}

	/**
	 * Handles the /excavator give command.
	 * @param sender the command sender
	 * @param command the command
	 * @param label the command label
	 * @param args the command arguments
	 * @return true if handled
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage("§cOnly operators can use this command.");
			return true;
		}
		if (args.length != 3 || !args[0].equalsIgnoreCase("give")) {
			sender.sendMessage("§cUsage: /excavator give <player> <2x2|3x3|5x5>");
			return true;
		}
		Player target = Bukkit.getPlayerExact(args[1]);
		if (target == null) {
			sender.sendMessage("§cPlayer not found: " + args[1]);
			return true;
		}
		ExcavatorToolType type = parseType(args[2]);
		if (type == null) {
			sender.sendMessage("§cInvalid tool type. Use 2x2, 3x3, or 5x5.");
			return true;
		}
		ItemStack tool = toolFactory.createExcavator(type);
		target.getInventory().addItem(tool);
		sender.sendMessage("§aGave " + type.getDisplayName() + " to " + target.getName() + ".");
		return true;
	}

	/**
	 * Parses the tool type argument.
	 * @param arg the argument
	 * @return the tool type or null
	 */
	private ExcavatorToolType parseType(String arg) {
		return switch (arg.toLowerCase()) {
			case "2x2" -> ExcavatorToolType.EXCAVATOR_2X2;
			case "3x3" -> ExcavatorToolType.EXCAVATOR_3X3;
			case "5x5" -> ExcavatorToolType.EXCAVATOR_5X5;
			default -> null;
		};
	}
}

