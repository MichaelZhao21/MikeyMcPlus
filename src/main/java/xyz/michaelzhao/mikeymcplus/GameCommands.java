package xyz.michaelzhao.mikeymcplus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Missing arguments!");
                return false;
            }

            switch (args[0]) {
                case "add":
                    GameSetup.newGame(player, args);
                    break;
                case "active":
                    GameSetup.setActive(player, args);
                    break;
                case "list":
                    GameSetup.list(player);
                    break;
                case "tool":
                    if (GameSetup.isNoGameSelected(player)) return false;
                    GameSetup.giveTool(player);
                    break;
                case "stagesave":
                    if (GameSetup.isNoGameSelected(player)) return false;
                    GameSetup.saveStage(player);
                    GameSetup.saveGame();
                    break;
                case "stageload":
                    if (GameSetup.isNoGameSelected(player)) return false;
                    GameSetup.loadStage();
                    break;
                case "save":
                    GameSetup.saveAllGames(player);
                    break;
                case "load":
                    GameSetup.loadAllGames(player);
                    break;
                case "enable":
                    GameEngine.enableGame(player, args);
                    break;
                case "api":
                    GameEngine.kit(player, args);
                    break;
                case "setpos":
                    GameSetup.setPos(player, args);
                    break;
                case "info":
                    GameEngine.info(player);
                    break;
                case "join":
                    GameEngine.join(player, args);
                    break;
                case "quit":
                    GameEngine.quit(player);
                    break;
                case "forcestart":
                    GameEngine.start(player, args);
            }
        }
        return true;
    }
}
