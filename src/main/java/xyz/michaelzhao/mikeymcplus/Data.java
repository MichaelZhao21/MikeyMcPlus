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
    public File gamesFolder, stageFolder;
    public HashMap<Player, String> playersInGameList;

    public Data(World currWorld) {
        this.currWorld = currWorld;
        gameData = new HashMap<>();
        gamesFolder = new File(MikeyMcPlus.instance.getDataFolder().getPath() + System.getProperty("file.separator") + "games");
        stageFolder = new File(MikeyMcPlus.instance.getDataFolder().getPath() + System.getProperty("file.separator") + "savedStages");
        if (!gamesFolder.exists()) gamesFolder.mkdir();
        if (!stageFolder.exists()) stageFolder.mkdir();
        playersInGameList = new HashMap<>();
        this.toolGame = "";
    }
}