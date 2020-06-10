package xyz.michaelzhao.mikeymcplus;

import java.io.File;
import java.util.HashMap;

public class Data {
    public HashMap<String, GameData> gameData;
    public String currGame = "";
    public File gamesFolder, stageFolder;
    public Data() {
        gameData = new HashMap<>();
        gamesFolder = new File(MikeyMcPlus.getInstance().getDataFolder().getPath() + System.getProperty("file.separator") + "games");
        stageFolder = new File(MikeyMcPlus.getInstance().getDataFolder().getPath() + System.getProperty("file.separator") + "savedStages");
        if (!gamesFolder.exists()) gamesFolder.mkdir();
        if (!stageFolder.exists()) stageFolder.mkdir();
    }
}
