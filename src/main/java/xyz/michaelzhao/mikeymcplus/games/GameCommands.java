package xyz.michaelzhao.mikeymcplus.games;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
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
                    GameSetup.saveAllGames(player);
                    break;
                case "load":
                    GameSetup.loadAllGames(player);
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
                case "setpos":
                    GameSetup.setPos(player, args);
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
        else if (commandSender instanceof RemoteConsoleCommandSender) {
            RemoteConsoleCommandSender sender = (RemoteConsoleCommandSender) commandSender;
            // TODO: Add commands allowed to be sent by console
            if (args.length == 0) {
                sender.sendMessage("Use " + ChatColor.AQUA + "/games help" + ChatColor.WHITE + " to see commands");
                return false;
            }



        }
        return true;
    }
}
