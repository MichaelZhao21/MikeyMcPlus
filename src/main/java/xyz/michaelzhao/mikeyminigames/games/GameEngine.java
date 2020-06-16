package xyz.michaelzhao.mikeyminigames.games;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.michaelzhao.mikeyminigames.MikeyMinigames;
import xyz.michaelzhao.mikeyminigames.Util;

import java.util.Collections;
import java.util.HashMap;

public class GameEngine { // TODO add change game type
    /**
     * Changes the enabled attribute after checking for setup conditions
     *
     * @param sender the sender that issued the command
     * @param args   command arguments
     */
    public static void enableGame(CommandSender sender, String[] args) {
        // Command checking
        if (Util.isArgsIncorrectLength(args, 2, "games enable <GameName>", sender)) return;
        if (Util.isInvalidGame(args[1], sender)) return;

        // Define objects for comparing to the BlockVector3 and Location objects
        BlockVector3 blockNotSet = BlockVector3.at(0, 0, 0);

        // Get the current game as the base class
        GameData data = Util.getData(args[1]);
//
//        // Check to make sure game type is set
//        if (data.gameType == GameType.NONE) {
//            sender.sendMessage(ChatColor.RED + "Game type not set");
//            return;
//        }

        // Make variable to check for errors
        boolean noError = true;

        if (enableError(Util.isLocationNotSet(data.exitLoc), "Exit location not set", sender)) noError = false;
        if (data.hasLobby)
            if (enableError(Util.isLocationNotSet(data.lobby), "Lobby not set", sender)) noError = false;
        if (data.hasArena)
            if (enableError(data.pos1.equals(blockNotSet) || data.pos2.equals(blockNotSet), "Arena bounds not set", sender)) noError = false;
        if (data.hasSpawnPlatform)
            if (enableError(data.startPos1.equals(blockNotSet) || data.startPos2.equals(blockNotSet), "Starting platform not set", sender)) noError = false;
        if (data.hasSpectators)
            if (enableError(Util.isLocationNotSet(data.spectatorLoc), "Spectator spawn position not set", sender)) noError = false;
        if (data.hasCheckpoints)
            if (enableError(data.checkpoints.size() < 2, "You must set at least 2 checkpoints (start and end)", sender)) noError = false;

        // Enable if there were no errors
        if (noError) {
            Util.getData(args[1]).enabled = true;
            sender.sendMessage(ChatColor.GOLD + args[1] + " enabled!");
        }
    }

    public static boolean enableError(boolean condition, String errorMessage, CommandSender sender) {
        if (condition) sender.sendMessage(errorMessage);
        return condition;
    }

    /**
     * Disables the game after checking to make sure the game isn't currently disabled
     *
     * @param player the player that issued the command
     * @param args   command arguments
     */
    public static void disableGame(Player player, String[] args) {
        // Check args
        if (Util.isArgsIncorrectLength(args, 2, "games disable <Game Name>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Disable the game and send player message
        Util.getData(args[1]).enabled = false;
        player.sendMessage(ChatColor.GOLD + args[1] + " disabled");
    }

    /**
     * Prints out info for the data object
     *
     * @param player the player that issued the command
     * @param args   command arguments
     */
    public static void info(Player player, String[] args) {
        // Check command
        if (Util.isArgsIncorrectLength(args, 2, "games info <Game Name>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Get generic GameData obj
        GameData data = Util.getData(args[1]);

        // Print out general info w/ header
        player.sendMessage("-----------------------------------");
        player.sendMessage(ChatColor.GOLD + "Name: " + data.name);
        player.sendMessage("Enabled: " + data.enabled);
        player.sendMessage(Util.coordsToString("Lobby", data.lobby));
        player.sendMessage(Util.coordsToString("Exit Location", data.exitLoc));
        player.sendMessage("Arena Enabled: " + data.hasArena);
        player.sendMessage(Util.coordsToString("Spectator Location", data.spectatorLoc));
        player.sendMessage(String.format("Spawning area: (%d, %d, %d) to (%d, %d, %d)", data.startPos1.getX(), data.startPos1.getY(), data.startPos1.getZ(), data.startPos2.getX(), data.startPos2.getY(), data.startPos2.getZ()));

        // Print arena data
        // TODO: split by game type
        if (data.hasArena) {
            player.sendMessage("Arena Saved: " + data.arenaSaved);
            player.sendMessage(String.format("Arena area: (%d, %d, %d) to (%d, %d, %d)", data.pos1.getX(), data.pos1.getY(), data.pos1.getZ(), data.pos2.getX(), data.pos2.getY(), data.pos2.getZ()));
        }

        // Bottom line
        player.sendMessage("-----------------------------------");
    }

    public static void kit(Player player, String[] args) { // TODO: fix :((
        String em = ChatColor.RED + "Usage: /games kit <spleef |>";
        if (args.length != 2 || !args[1].equals("spleef")) {
            player.sendMessage(em);
            return;
        }
        giveKit(args[1], player);
    }

    /**
     * Gives the kit specified to the player
     *
     * @param type   the type of kit to get
     * @param player the player to give the kit to
     */
    public static void giveKit(String type, Player player) {
        // Give kit based on type
        if (type.equals("spleef")) {
            // Create the pickaxe and add metadata, then give to player
            ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta meta = pick.getItemMeta();
            if (meta == null) return;
            meta.setDisplayName(ChatColor.GREEN + "S P O O N");
            meta.setLore(Collections.singletonList("digdigdig"));
            meta.addEnchant(Enchantment.DIG_SPEED, 100, true);
            meta.addEnchant(Enchantment.DURABILITY, 100, true);
            meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
            pick.setItemMeta(meta);
            pick.setAmount(1);
            player.getInventory().addItem(pick);

            // Add potion effects to player
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 10, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1000000, 10, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 10, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
        }
    }

    /**
     * Joins a game lobby
     *
     * @param player the player who issued the command
     * @param args   command arguments
     */
    public static void joinGame(Player player, String[] args) {
        // Check commands
        if (Util.isArgsIncorrectLength(args, 2, "games join <GameName>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Check if player is already in game
        if (MikeyMinigames.data.playersInGameList.containsKey(player)) {
            player.sendMessage(ChatColor.RED + "Already in game " + MikeyMinigames.data.playersInGameList.get(player));
            return;
        }

        // Get game data object
        GameData data = Util.getData(args[1]);

        // Add player to hashmap of players
        data.gamePlayers.put(player.getName(), player);

        // Add player data to hashmap
        data.gamePlayerObjects.put(player.getName(), new PlayerGameData(player));


        // Make sure the game is enabled
        if (!data.enabled) {
            player.sendMessage(ChatColor.RED + "Game not enabled!");
            return;
        }

        // Add player to list of players in a game
        MikeyMinigames.data.playersInGameList.put(player, data.name);

        // TODO: add check for lobby

        // Check for stopped state
        if (data.gameState == GameState.RUNNING) { // TODO: Add join as spectator
            player.sendMessage(ChatColor.RED + "Game is currently playing");
            return;
        }

        // Clear inventory and prepare them for the game
        player.getInventory().clear();
        giveKit("spleef", player); // TODO: change kit based on game
        player.setHealth(20.0);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(data.lobby);

        // Send player joined message
        player.sendMessage(ChatColor.AQUA + "Joined " + ChatColor.GOLD + args[1]);

        // If the game hasn't begun, start it
        if (data.gameState == GameState.STOPPED)
            startLobby(data);
    }

    /**
     * Start the lobby
     *
     * @param data the game data
     */
    public static void startLobby(GameData data) {
        data.timerId = createTimer(data.name, true, 30, "start", null);
        data.gameState = GameState.LOBBY;
    }

    /**
     * Command to autostart the game
     *
     * @param player the player that issued the command
     * @param args   command arguments
     */
    public static void startCall(Player player, String[] args) {
        // Check command
        if (Util.isArgsIncorrectLength(args, 2, "games start <GameName>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Get game data object
        GameData data = Util.getData(args[1]);

        // Check if the game is in the lobby state
        if (data.gameState != GameState.LOBBY) {
            player.sendMessage(ChatColor.RED + args[1] + " is not in the lobby!");
            return;
        }

        // Cancel timer and start game
        MikeyMinigames.instance.getServer().getScheduler().cancelTask(data.timerId);
        start(args[1]);
    }

    /**
     * Start the game
     *
     * @param gameName the name of the game
     */
    public static void start(String gameName) {
        // Get game data object
        GameData data = Util.getData(gameName);

        // TODO: Add cases for game types

        // Set the state to running
        data.gameState = GameState.RUNNING;

        // Create the game stopwatch
        data.timerId = createTimer(gameName, false, 0, null, "endgame");

        // Set the playersAlive to the number of players in the game
        data.playersAlive = data.gamePlayers.size();

        // Prep and teleport each player to the game arena
        for (Player player : data.gamePlayers.values()) {
            data.gamePlayerObjects.get(player.getName()).state = PlayerState.GAME;
            player.setGameMode(GameMode.SURVIVAL);
            player.setLevel(0);
            player.teleport(randomSpawn(data.startPos1, data.startPos2));
        }
    }

    /**
     * Generates a random starting point from 2 corners
     *
     * @param start lowest-coordinate-valued corner
     * @param end   highest-coordinate-valued corner
     * @return the Location object representing spawn point
     */
    public static Location randomSpawn(BlockVector3 start, BlockVector3 end) { //TODO: Add facing center
        double x = Math.random() * (end.getX() - start.getX()) + start.getX();
        double z = Math.random() * (end.getZ() - start.getZ()) + start.getZ();
        return new Location(MikeyMinigames.data.currWorld, x, start.getY(), z);
    }

    /**
     * Player leaves game
     *
     * @param player the player that issued the command
     */
    public static void quit(Player player) {
        // Check to make sure player is in a game
        if (!MikeyMinigames.data.playersInGameList.containsKey(player)) {
            player.sendMessage(ChatColor.RED + "Not in a game");
            return;
        }

        // Remove player from game and send message
        removeFromGame(player);
        player.sendMessage(ChatColor.AQUA + "Left game " + ChatColor.GOLD + Util.getData(MikeyMinigames.data.playersInGameList.get(player)).name);
        //TODO: Add check to see if no players left
    }

    /**
     * Runs commands to remove the player from the game
     *
     * @param player the player that issued the command
     */
    public static void removeFromGame(Player player) {
        // Get data object
        GameData data = Util.getData(MikeyMinigames.data.playersInGameList.get(player));

        // Get the player data object
        PlayerGameData pDat = data.gamePlayerObjects.get(player.getName());

        // Remove players from the list of players in the game and general list TODO fix this desc
        data.gamePlayers.remove(player.getName());
        data.gamePlayerObjects.remove(player.getName());
        MikeyMinigames.data.playersInGameList.remove(player);

        // Restore the player's items
        ItemStack[] items = pDat.oldInventory;
        player.getInventory().clear();
        player.getInventory().setContents(items);

        // Remove all potion effects and restore old effects
        for (PotionEffect e : player.getActivePotionEffects())
            player.removePotionEffect(e.getType());
        for (PotionEffect p : pDat.oldPotionEffects)
            player.addPotionEffect(p);

        // Reset gamemode/xp, set velocity to 0, and teleport them to the exit location
        player.setGameMode(pDat.oldMode);
        player.setTotalExperience(pDat.oldExpLvl);
        player.setVelocity(new Vector());
        player.teleport(data.exitLoc);
    }

    /**
     * Checks to see if the game ends based on specific conditions
     *
     * @param data the game data object
     */
    public static void checkForEndGame(GameData data) {
        // For deathmatches, check to see if only one player is alive
        if (data.playersAlive == 1) {
            // Set them as winner and get their name
            Player winner = getWinner(data);
            String winName = winner.getName();

            // Cancel the running timer
            MikeyMinigames.instance.getServer().getScheduler().cancelTask(data.timerId);

            // Get a list of player names and iterate through them, removing them from the game
            String[] pm = data.gamePlayers.keySet().toArray(new String[0]);
            for (String p : pm) {
                Player player = data.gamePlayers.get(p);
                removeFromGame(player);

                // Show all players who won the game
                player.sendTitle(String.format("%s won %s!", ChatColor.GOLD + winName, ChatColor.AQUA + data.name), "", 10, 60, 20);
            }

            // Reload destroyed arena, reset timer count, and set game state to stopped
            GameSetup.loadArena(data);
            data.timerCount = 0;
            data.gameState = GameState.STOPPED;
        }
    }

    /**
     * Finds the winner from the list of players in a game
     *
     * @param data the game data
     * @return the Player object representing the winner
     */
    public static Player getWinner(GameData data) {
        for (Player p : data.gamePlayers.values())
            if (data.gamePlayerObjects.get(p.getName()).state == PlayerState.GAME)
                return p;
        return null;
    }

    /**
     * Runs when the player dies: Normally health below 0 or y-level below 0
     * Is called on event listener
     *
     * @param player   the player that died
     * @param gameName the name of the game
     */
    public static void playerDeath(Player player, String gameName) {
        // Get game data object
        GameData data = Util.getData(gameName);

        // Check to see if the game is running and the player is in the current game
        if (data.gameState == GameState.RUNNING && data.gamePlayerObjects.get(player.getName()).state == PlayerState.GAME) {
            // Set the player's state to spectator and decrease players alive counter
            data.gamePlayerObjects.get(player.getName()).state = PlayerState.SPECTATOR;
            data.playersAlive--;

            // Tell the player they died and change them to a spectator
            player.sendTitle("You died!", "You lasted " + data.timerCount + " seconds", 10, 60, 20);
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.teleport(data.spectatorLoc);

            // Check to see if that death caused the winning condition
            checkForEndGame(data);

            // Notify all players that the current player died
            for (Player p : data.gamePlayers.values())
                p.sendMessage(ChatColor.AQUA + player.getName() + " died! " + ChatColor.LIGHT_PURPLE + data.playersAlive + " players remaining.");
        }
    }

    /**
     * Creates the game timer and stores it in the game's timer object
     *
     * @param game      the name of hte game
     * @param countdown if the timer is a countdown or stopwatch
     * @param seconds   number of seconds if it is a countdown
     * @param callback  function to run after countdown ends
     * @param fun       function to run while the command runs (see timerFun)
     * @return the ID of the timer
     */
    public static int createTimer(String game, boolean countdown, int seconds, String callback, String fun) {
        // Get game data object
        GameData data = Util.getData(game);

        // Store timer value in timerCount
        data.timerCount = (countdown ? seconds : 0);

        // Iterate through players and set their xp bar level as the timer value
        for (Player p : data.gamePlayers.values()) {
            p.setLevel(data.timerCount);
        }

        // Create and return the timer ID
        return MikeyMinigames.instance.getServer().getScheduler().scheduleSyncRepeatingTask(MikeyMinigames.instance, (() -> runTimer(data, countdown, callback, fun)), 20L, 20L);
    }

    /**
     * Runnable function inside of the timer
     *
     * @param data      the game data object
     * @param countdown if the timer is a countdown or stopwatch
     * @param callback  function to run after countdown ends
     * @param fun       function to run while the command runs (see timerFun)
     */
    public static void runTimer(GameData data, boolean countdown, String callback, String fun) {
        // Decrement/increment timer
        data.timerCount += countdown ? -1 : 1;

        // Check for when the timer runs out
        if (data.timerCount == 0) {
            timerEnd(callback, data);
        }

        // Set XP bar to display timer for all game players
        for (Player p : data.gamePlayers.values()) {
            p.setLevel(data.timerCount);
        }

        // Run timer functions
        if (fun != null)
            timerFun(fun, data);
    }

    /**
     * Runs the timer function based on the name
     *
     * @param fun  name of the function
     * @param data the game data object
     */
    public static void timerFun(String fun, GameData data) {
        // Run function based on name
        switch (fun) {
            case "endgame":
                checkForEndGame(data);
                break;
        }
    }

    /**
     * Ends the timer and runs the callback function
     *
     * @param callback the name of the callback function
     * @param data     the game data object
     */
    public static void timerEnd(String callback, GameData data) {
        // Stop timer
        MikeyMinigames.instance.getServer().getScheduler().cancelTask(data.timerId);

        // Run callback function
        switch (callback) {
            case "start":
                start(data.name);
                break;
        }
    }
}
