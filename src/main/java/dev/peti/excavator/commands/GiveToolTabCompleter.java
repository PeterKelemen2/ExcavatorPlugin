package dev.peti.excavator.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveToolTabCompleter implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			List<String> subs = new ArrayList<>();
			for (String sub : new String[]{"give", "toggle", "reload", "stats"}) {
				if (sub.startsWith(args[0].toLowerCase())) subs.add(sub);
			}
			return subs;
		}
		String sub = args[0].toLowerCase();
		// give <player> <tool> <size>
		if (sub.equals("give")) {
			if (!sender.isOp() && !sender.hasPermission("excavator.give")) return Collections.emptyList();
			if (args.length == 2) {
				List<String> playerNames = new ArrayList<>();
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
						playerNames.add(player.getName());
					}
				}
				return playerNames;
			}
			if (args.length == 3) {
				List<String> tools = new ArrayList<>();
				for (String t : new String[]{"pickaxe", "axe", "shovel"}) {
					if (t.startsWith(args[2].toLowerCase())) tools.add(t);
				}
				return tools;
			}
			if (args.length == 4) {
				List<String> sizes = new ArrayList<>();
				for (String s : new String[]{"2x2", "3x3", "5x5"}) {
					if (s.startsWith(args[3].toLowerCase())) sizes.add(s);
				}
				return sizes;
			}
		}
		// stats [player]
		if (sub.equals("stats") && args.length == 2 && sender.hasPermission("excavator.stats")) {
			List<String> playerNames = new ArrayList<>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
					playerNames.add(player.getName());
				}
			}
			return playerNames;
		}
		return Collections.emptyList();
	}
}
