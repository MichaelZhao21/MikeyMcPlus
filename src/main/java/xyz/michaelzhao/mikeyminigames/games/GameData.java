package xyz.michaelzhao.mikeyminigames.games;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.michaelzhao.mikeyminigames.MikeyMinigames;
import xyz.michaelzhao.mikeyminigames.Util;

import java.io.File;
import java.util.HashMap;

enum DeathType {NONE, FALLING, HEALTH}
enum GameState {LOBBY, RUNNING, STOPPED}

public class GameData {
    public Location lobby, exitLoc;
    public String name;
    public boolean enabled;
    public HashMap<String, Player> gamePlayers;
    public HashMap<String, PlayerGameData> gamePlayerObjects;
    public DeathType deathType;
    public File gameFolder;

    public GameData(String name) {
        this.name = name;
        this.enabled = false;
        this.lobby = new Location(MikeyMinigames.data.currWorld, 0, 0, 0);
        this.exitLoc = new Location(MikeyMinigames.data.currWorld, 0, 0, 0);
        this.gamePlayers = new HashMap<>();
        this.gamePlayerObjects = new HashMap<>();
        this.deathType = DeathType.NONE;
        this.gameFolder = new File(Util.getSubPath(MikeyMinigames.data.gamesFolder, name));
        if (!this.gameFolder.exists()) this.gameFolder.mkdir();

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
