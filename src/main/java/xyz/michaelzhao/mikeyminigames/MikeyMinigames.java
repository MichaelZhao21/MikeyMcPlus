package xyz.michaelzhao.mikeyminigames;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.michaelzhao.mikeyminigames.games.*;

public class MikeyMinigames extends JavaPlugin {

    public static MikeyMinigames instance = null;
    public static Data data;

    @Override
    public void onEnable() {
        // Override call
        super.onEnable();

        // Store the instance to a static variable
        instance = this;

        // Make the data folder if it doesn't exist
        getDataFolder().mkdir();

        // Create the data object
        data = new Data(getServer().getWorlds().get(0));

        // Set executor, tab completers, and listeners for modules
        getCommand("fun").setExecutor(new FunCommands());
        getCommand("games").setExecutor(new GameCommands());
        getCommand("games").setTabCompleter(new GameSetupTabCompletion());
        getServer().getPluginManager().registerEvents(new GameListener(), this);

        // Load games from file
        GameSetup.loadAllGames(getServer().getConsoleSender());
    }

    @Override
    public void onDisable() {
        // Override call
        super.onDisable();

        // Remove all players from games
        for (Player p : data.playersInGameList.keySet()) {
            GameEngine.quit(p);
        }

        // Saves all games to a file
        GameSetup.saveAllGames(getServer().getConsoleSender());
    }

}
