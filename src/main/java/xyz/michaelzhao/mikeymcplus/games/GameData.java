package xyz.michaelzhao.mikeymcplus.games;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.michaelzhao.mikeymcplus.MikeyMcPlus;

public class GameData {
    public Location lobby, exitLoc;
    public String name;
    public boolean enabled;

    public GameData(String name) {
        this.name = name;
        this.enabled = false;
        this.lobby = new Location(MikeyMcPlus.data.currWorld, 0, 0, 0);
        this.exitLoc = new Location(MikeyMcPlus.data.currWorld, 0, 0, 0);
    }

    public String getGameType() { return "base"; }

    /**
     * Checks to see if the type entered is valid
     * @param type - the game type entered
     * @param player - the player that issued the command
     * @return - if the game type is valid
     */
    public static boolean isValidGameType(String type, Player player) {
        if (!type.equals("deathmatch") && !type.equals("parkour")) {
            player.sendMessage(ChatColor.RED + "Invalid game type");
            player.sendMessage(ChatColor.RED + "Valid game types: deathmatch, parkour");
            return false;
        }
        return true;
    }
}
