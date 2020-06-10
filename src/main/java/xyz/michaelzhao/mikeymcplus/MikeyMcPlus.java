package xyz.michaelzhao.mikeymcplus;

import org.bukkit.plugin.java.JavaPlugin;

public class MikeyMcPlus extends JavaPlugin {

    public static MikeyMcPlus instance = null;
    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        this.getCommand("firework").setExecutor(new CommandFirework());
        getLogger().info("Mikey is here!!!!!!!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static MikeyMcPlus getInstance() {
        return instance;
    }
}
