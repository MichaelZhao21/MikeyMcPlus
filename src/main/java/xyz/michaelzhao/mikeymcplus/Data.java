package xyz.michaelzhao.mikeymcplus;

import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.michaelzhao.mikeymcplus.games.GameData;

import java.io.File;
import java.util.HashMap;

public class Data {
    public World currWorld;
    public HashMap<String, GameData> gameData;
    public String toolGame;
    public File gamesFolder, arenaFolder;
    public HashMap<Player, String> playersInGameList;

    public Data(World currWorld) {
        this.currWorld = currWorld;
        gameData = new HashMap<>();
        gamesFolder = new File(MikeyMcPlus.instance.getDataFolder().getPath() + System.getProperty("file.separator") + "games");
        arenaFolder = new File(MikeyMcPlus.instance.getDataFolder().getPath() + System.getProperty("file.separator") + "savedarenas");
        if (!gamesFolder.exists()) gamesFolder.mkdir();
        if (!arenaFolder.exists()) arenaFolder.mkdir();
        playersInGameList = new HashMap<>();
        this.toolGame = "";
    }
}