package xyz.michaelzhao.mikeymcplus;

import org.bukkit.plugin.java.JavaPlugin;

public class MikeyMcPlus extends JavaPlugin {

    public static MikeyMcPlus instance = null;
    public static Data data;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        MikeyMcPlus.getInstance().getDataFolder().mkdir();
        data = new Data();
        this.getCommand("firework").setExecutor(new CommandFirework());
        this.getCommand("gaystick").setExecutor(new CommandGayStick());
        this.getCommand("games").setExecutor(new CommandGames());
        this.getCommand("games").setTabCompleter(new GameSetupTabCompletion());

        getServer().getPluginManager().registerEvents(new GameSetupListener(), this);

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
