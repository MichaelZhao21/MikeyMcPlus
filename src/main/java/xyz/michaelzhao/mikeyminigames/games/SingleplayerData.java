package xyz.michaelzhao.mikeyminigames.games;

import java.util.HashMap;

public class SingleplayerData extends GameData {

    public HashMap<String, int[]> playerTimes;

    public SingleplayerData(String name) {
        super(name);
    }

    @Override
    public String getGameType() { return "parkour"; }
}
