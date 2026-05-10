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
        if (!sender.isOp()) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            if ("give".startsWith(args[0])) {
                subcommands.add("give");
            }
            return subcommands;
        }
        if (args.length == 2 && args[0].equals("give")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().startsWith(args[1])) {
                    playerNames.add(player.getName());
                }
            }
            return playerNames;
        }
        if (args.length == 3 && args[0].equals("give")) {
            List<String> types = new ArrayList<>();
            if ("2x2".startsWith(args[2])) types.add("2x2");
            if ("3x3".startsWith(args[2])) types.add("3x3");
            if ("5x5".startsWith(args[2])) types.add("5x5");
            return types;
        }
        return Collections.emptyList();
    }
}
