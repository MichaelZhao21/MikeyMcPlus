package xyz.michaelzhao.mikeymcplus.games;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.michaelzhao.mikeymcplus.MikeyMcPlus;

import java.util.HashMap;

enum GameState {LOBBY, RUNNING, STOPPED}

public class DeathmatchData extends GameData {
    public BlockVector3 pos1, pos2, startPlatform1, startPlatform2;
    public boolean stageSaved;
    public Location spectatorLoc;
    public HashMap<String, Player> gamePlayers;
    public HashMap<String, PlayerGameData> gamePlayerObjects;
    public int timerId, timerCount;
    public int playersAlive;
    public GameState gameState;

    public DeathmatchData(String name, GameType gameType) {
        super(name, gameType);
        this.pos1 = BlockVector3.at(0, 0, 0);
        this.pos2 = BlockVector3.at(0, 0, 0);
        this.stageSaved = false;
        this.startPlatform1 = BlockVector3.at(0, 0, 0);
        this.startPlatform2 = BlockVector3.at(0, 0, 0);
        this.spectatorLoc = new Location(MikeyMcPlus.data.currWorld, 0, 0, 0);
        this.gamePlayers = new HashMap<>();
        this.gamePlayerObjects = new HashMap<>();
        this.timerId = 0;
        this.gameState = GameState.STOPPED;
    }

}
