package xyz.michaelzhao.mikeymcplus;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MikeyMcPlus extends JavaPlugin {

    public static MikeyMcPlus instance = null;

    public static Data data;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        MikeyMcPlus.instance.getDataFolder().mkdir();
        data = new Data(this.getServer().getWorlds().get(0));
        this.getCommand("fun").setExecutor(new FunCommands());
        this.getCommand("games").setExecutor(new GameCommands());
        this.getCommand("games").setTabCompleter(new GameSetupTabCompletion());

        getServer().getPluginManager().registerEvents(new GameListener(), this);

        getLogger().info("Mikey is literally so cool");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Player p : data.playersInGameList.keySet())
            GameEngine.quit(p);
    }

}
