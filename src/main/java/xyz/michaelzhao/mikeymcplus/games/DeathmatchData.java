package xyz.michaelzhao.mikeymcplus.games;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.michaelzhao.mikeymcplus.MikeyMcPlus;

import java.util.HashMap;

enum GameState {LOBBY, RUNNING, STOPPED}

public class DeathmatchData extends GameData {
    public BlockVector3 startPos1, startPos2;
    public Location spectatorLoc;
    public HashMap<String, Player> gamePlayers;
    public HashMap<String, PlayerGameData> gamePlayerObjects;
    public int timerId, timerCount;
    public int playersAlive;
    public GameState gameState;
    public boolean arenaSaved;
    public BlockVector3 pos1, pos2;

    public DeathmatchData(String name) {
        super(name);
        this.arenaSaved = false;
        this.pos1 = BlockVector3.at(0, 0, 0);
        this.pos2 = BlockVector3.at(0, 0, 0);
        this.startPos1 = BlockVector3.at(0, 0, 0);
        this.startPos2 = BlockVector3.at(0, 0, 0);
        this.spectatorLoc = new Location(MikeyMcPlus.data.currWorld, 0, 0, 0);
        this.gamePlayers = new HashMap<>();
        this.gamePlayerObjects = new HashMap<>();
        this.timerId = 0;
        this.gameState = GameState.STOPPED;
    }

    @Override
    public String getGameType() { return "deathmatch"; }

}
