package xyz.michaelzhao.mikeyminigames.games;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) { // TODO: generalize to commandsender
            Player player = (Player) commandSender;

            if (args.length == 0) {
                player.sendMessage("Use " + ChatColor.AQUA + "/games help" + ChatColor.WHITE + " to see commands");
                return false;
            }

            switch (args[0]) {
                case "add":
                    GameSetup.newGame(player, args);
                    break;
                case "list":
                    GameSetup.list(player, args);
                    break;
                case "tool":
                    GameSetup.giveTool(player, args);
                    break;
                case "arena":
                    GameSetup.arenaCommand(player, args);
                    break;
                case "save":
                    GameSetup.saveAllGames(commandSender);
                    break;
                case "load":
                    GameSetup.loadAllGames(commandSender);
                    break;
                case "enable":
                    GameEngine.enableGame(player, args);
                    break;
                case "disable":
                    GameEngine.disableGame(player, args);
                    break;
                case "kit": // TODO: fix kit command
                    GameEngine.giveKit("spleef", player);
//                    GameEngine.kit(player, args);
                    break;
                case "info":
                    GameEngine.info(player, args);
                    break;
                case "join":
                    GameEngine.joinGame(player, args);
                    break;
                case "quit":
                    GameEngine.quit(player);
                    break;
                case "forcestart":
                    GameEngine.startCall(player, args);
                    break;
                default:
                    return false;
            }
        }
        return true;
    }
}
