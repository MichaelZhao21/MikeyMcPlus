package xyz.michaelzhao.mikeymcplus;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.entity.Player;

import java.util.HashMap;

enum GameState {LOBBY, RUNNING, STOPPED};

public class GameData {
    public BlockVector3 pos1, pos2, lobby, startPlatform1, startPlatform2, spectatorLoc, exitLoc;
    public boolean stageSaved;
    public String name;
    public boolean enabled;
    public HashMap<String, Player> gamePlayers;
    public HashMap<String, PlayerGameData> gamePlayerObjects;
    public int timerId, timerCount;
    public int playersAlive;
    public GameState gameState;

    public GameData(String name) {
        this.name = name;
        this.pos1 = BlockVector3.at(0, 0, 0);
        this.pos2 = BlockVector3.at(0, 0, 0);
        this.stageSaved = false;
        this.enabled = false;
        this.lobby = BlockVector3.at(0, 0, 0);
        this.startPlatform1 = BlockVector3.at(0, 0, 0);
        this.startPlatform2 = BlockVector3.at(0, 0, 0);
        this.spectatorLoc = BlockVector3.at(0, 0, 0);
        this.exitLoc = BlockVector3.at(0, 0, 0);
        this.gamePlayers = new HashMap<>();
        this.gamePlayerObjects = new HashMap<>();
        this.timerId = 0;
        this.gameState = GameState.STOPPED;
    }
}
