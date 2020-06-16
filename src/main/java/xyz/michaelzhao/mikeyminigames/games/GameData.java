package xyz.michaelzhao.mikeyminigames.games;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.michaelzhao.mikeyminigames.MikeyMinigames;
import xyz.michaelzhao.mikeyminigames.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

enum DeathType {NONE, FALLING, HEALTH}

enum GameState {LOBBY, RUNNING, STOPPED}

enum GameType {NONE, MULTIPLAYER, TEAM, SINGLEPLAYER}

public class GameData {
    public boolean hasArena, hasLobby, hasCheckpoints, hasSpawnPlatform, hasSpectators;
    public Location lobby, exitLoc;
    public String name;
    public boolean enabled;
    public HashMap<String, Player> gamePlayers;
    public HashMap<String, PlayerGameData> gamePlayerObjects;
    public ArrayList<Location> checkpoints;
    public DeathType deathType;
    public File gameFolder;
    public BlockVector3 startPos1, startPos2;
    public Location spectatorLoc;
    public int timerId, timerCount;
    public int playersAlive;
    public GameState gameState;
    public boolean arenaSaved;
    public BlockVector3 pos1, pos2;
    public GameType gameType;

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
        this.arenaSaved = false;
        this.pos1 = BlockVector3.at(0, 0, 0);
        this.pos2 = BlockVector3.at(0, 0, 0);
        this.startPos1 = BlockVector3.at(0, 0, 0);
        this.startPos2 = BlockVector3.at(0, 0, 0);
        this.spectatorLoc = new Location(MikeyMinigames.data.currWorld, 0, 0, 0);
        this.timerId = 0;
        this.gameState = GameState.STOPPED;
        this.gameType = GameType.NONE;

        this.hasArena = false;
        this.hasLobby = false;
        this.hasSpawnPlatform = false;
        this.hasCheckpoints = false;
        this.hasSpectators = false;

        this.checkpoints = new ArrayList<>();
    }

    // TODO: javadoc
    public static String gameTypeToString(GameType type) {
        switch (type) {
            case NONE:
                return "none";
            case MULTIPLAYER:
                return "multiplayer";
            case TEAM:
                return "team";
            case SINGLEPLAYER:
                return "singleplayer";
        }
        return null;
    }

    public static GameType stringToGameType(String s) {
        switch (s) {
            case "none":
                return GameType.NONE;
            case "multiplayer":
                return GameType.MULTIPLAYER;
            case "team":
                return GameType.TEAM;
            case "singleplayer":
                return GameType.SINGLEPLAYER;
        }
        return null;
    }
}
