package xyz.michaelzhao.mikeymcplus.games;

public class ParkourData extends GameData {

    public ParkourData(String name) {
        super(name);
    }

    @Override
    public String getGameType() { return "parkour"; }
}
