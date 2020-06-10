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
        this.getCommand("fun").setExecutor(new Fun());
        this.getCommand("games").setExecutor(new CommandGames());
        this.getCommand("games").setTabCompleter(new GameSetupTabCompletion());

        getServer().getPluginManager().registerEvents(new GameSetupListener(), this);

        getLogger().info("Mikey is literally so cool");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static MikeyMcPlus getInstance() {
        return instance;
    }
}
