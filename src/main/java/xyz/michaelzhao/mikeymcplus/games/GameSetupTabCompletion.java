package xyz.michaelzhao.mikeymcplus.games;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class GameSetupTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("games") && args.length == 1 && commandSender instanceof Player) {
            return Arrays.asList("add", "list", "arena", "load",
                    "save",  "tool", "enable", "api", "setpos",
                    "join", "quit");
        }
        // TODO: add advanced tab complete
        return null;
    }
}
