package xyz.michaelzhao.mikeymcplus;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.World;

public class GameData {
    public World currWorld;
    public BlockVector3 pos1, pos2;
    public boolean stageSaved;
    public String name;
    public GameData(World currWorld, String name) {
        this.currWorld = currWorld;
        this.name = name;
        this.pos1 = BlockVector3.at(0, 0, 0);
        this.pos2 = BlockVector3.at(0, 0, 0);
        this.stageSaved = false;
    }
}
