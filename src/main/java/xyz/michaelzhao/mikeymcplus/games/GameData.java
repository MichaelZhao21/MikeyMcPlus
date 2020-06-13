package xyz.michaelzhao.mikeymcplus.games;

import org.bukkit.Location;
import xyz.michaelzhao.mikeymcplus.MikeyMcPlus;

enum GameType {DEATHMATCH, PARKOUR}

public class GameData {
    public Location lobby, exitLoc;
    public String name;
    public boolean enabled;
    public GameType gameType;

    public GameData(String name, GameType gameType) {
        this.name = name;
        this.enabled = false;
        this.lobby = new Location(MikeyMcPlus.data.currWorld, 0, 0, 0);
        this.exitLoc = new Location(MikeyMcPlus.data.currWorld, 0, 0, 0);
        this.gameType = gameType;
    }

    public static String gameTypeToString(GameType type) {
        switch (type) {
            case DEATHMATCH:
                return "deathmatch";
            case PARKOUR:
                return "parkour";
        }
        return null;
    }

    public static GameType stringToGameType(String type) {
        switch (type) {
            case "deathmatch":
                return GameType.DEATHMATCH;
            case "parkour":
                return GameType.PARKOUR;
        }
        return null;
    }
}
