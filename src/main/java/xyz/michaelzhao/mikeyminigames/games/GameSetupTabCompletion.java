package xyz.michaelzhao.mikeyminigames.games;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class GameSetupTabCompletion implements TabCompleter {

    public String[] arg0List;

    public GameSetupTabCompletion() {
        this.arg0List = new String[]{"help", "add", "remove", "join", "quit"};
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("games") && args.length == 1 && commandSender instanceof Player) {
            return Arrays.asList(arg0List);
        }
        // TODO: add advanced tab complete
        return null;
    }
}
