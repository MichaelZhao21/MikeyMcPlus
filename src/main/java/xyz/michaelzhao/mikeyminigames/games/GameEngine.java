package xyz.michaelzhao.mikeyminigames.games;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.*;
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

public class GameEngine { // TODO add change game type
    /**
     * Changes the enabled attribute after checking for setup conditions
     * @param player the player that issued the command
     * @param args command arguments
     */
    public static void enableGame(Player player, String[] args) {
        // Command checking
        if (Util.isArgsIncorrectLength(args, 2, "games enable <GameName>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Define objects for comparing to the BlockVector3 and Location objects
        BlockVector3 blockNotSet = BlockVector3.at(0, 0, 0);

        // Get the current game as the base class
        GameData data = MikeyMinigames.data.gameData.get(args[1]);

        // Define general conditions and errors
        boolean[] generalConditions = new boolean[]{
                Util.isLocationNotSet(data.lobby),
                Util.isLocationNotSet(data.exitLoc)
        };
        String[] generalErrors = new String[]{
                "Lobby not set",
                "Exit location not set"
        };

        // Make variable to check for errors
        boolean noError = true;

        // Loop through general conditions and print error if true
        for (int i = 0; i < generalConditions.length; i++) {
            if (generalConditions[i]) {
                player.sendMessage(ChatColor.RED + generalErrors[i]);
                noError = false;
            }
        }

        // Specific conditions and errors
        if (data instanceof DeathmatchData) {
            // Cast to DeathmatchData child class
            DeathmatchData deathmatchData = (DeathmatchData) data;

            // Define conditions and errors for DeathmatchData
            boolean[] deathmatchConditions = new boolean[]{
                    !deathmatchData.arenaSaved,
                    deathmatchData.startPos1.equals(blockNotSet) || deathmatchData.startPos2.equals(blockNotSet),
                    Util.isLocationNotSet(deathmatchData.spectatorLoc)
            };
            String[] deathmatchErrors = new String[]{
                "Arena not saved",
                "Starting platform (2 corners) not set",
                "Spectator location not set"
            };

            // Loop through deathmatch conditions/errors
            for (int i = 0; i < deathmatchConditions.length; i++) {
                if (deathmatchConditions[i]) {
                    player.sendMessage(ChatColor.RED + deathmatchErrors[i]);
                    noError = false;
                }
            }
        }
        else if (data instanceof SingleplayerData) {
            // TODO: Fill this in
            SingleplayerData singleplayerData = (SingleplayerData) data;
        }

        // Enable if there were no errors
        if (noError) {
            MikeyMinigames.data.gameData.get(MikeyMinigames.data.toolGame).enabled = true;
            player.sendMessage(ChatColor.GOLD + MikeyMinigames.data.toolGame + " enabled!");
        }

        // TODO: Make checking into functions and convert back to if statements
//        // Create hashmaps to store game type specific conditions and errors
//        HashMap<String, boolean[]> specificConditions = new HashMap<>();
//        HashMap<String, String[]> specificErrors = new HashMap<>();
//
//        // Deathmatch conditions/errors
//        specificConditions.put("deathmatch", new boolean[]{
//                !curr.arenaSaved,
//                curr.startPos1.equals(blockNotSet) || curr.startPos2.equals(blockNotSet),
//                data.spectatorLoc.equals(Util.locationNotSet())
//        });
//        specificErrors.put("parkour", new String[]{
//                "Arena not saved",
//                "Starting platform (2 corners) not set"
//        });
//
//        if (data instanceof DeathmatchData) { // TODO: create template function
//            for (int i = 0; i < specificConditions.get("deathmatch").length; i++) {
//                if (specificConditions.get("deathmatch")[i]) {
//                    player.sendMessage(ChatColor.RED + specificErrors.get("deathmatch")[i]);
//                    noError = false;
//                }
//            }
//        }
//        else if (data instanceof ParkourData) {
//
//            for (int i = 0; i < specificConditions.get("parkour").length; i++) {
//                if (specificConditions.get("parkour")[i]) {
//                    player.sendMessage(ChatColor.RED + specificErrors.get("parkour")[i]);
//                    noError = false;
//                }
//            }
//        }
    }

    /**
     * Disables the game after checking to make sure the game isn't currently disabled
     * @param player the player that issued the command
     * @param args command arguments
     */
    public static void disableGame(Player player, String[] args) {
        // Check args
        if (Util.isArgsIncorrectLength(args, 1, "games disable", player)) return;

        // Disable the game and send player message
        MikeyMinigames.data.gameData.get(MikeyMinigames.data.toolGame).enabled = false;
        player.sendMessage(ChatColor.GOLD + MikeyMinigames.data.toolGame + " disabled");
    }

    /**
     * Prints out info for the data object
     * @param player the player that issued the command
     * @param args command arguments
     */
    public static void info(Player player, String[] args) {
        // Check command
        if (Util.isArgsIncorrectLength(args, 2, "games info <Game Name>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Get generic GameData obj
        GameData data = MikeyMinigames.data.gameData.get(args[1]);

        // Print out general info w/ header
        player.sendMessage("-----------------------------------");
        player.sendMessage(ChatColor.GOLD + "Name: " + data.name);
        player.sendMessage("Enabled: " + data.enabled);
        player.sendMessage(Util.coordsToString("Lobby", data.lobby));
        player.sendMessage(Util.coordsToString("Exit Location", data.exitLoc));

        // Convert to specific type and print specific info
        if (data instanceof DeathmatchData) {
            DeathmatchData death = (DeathmatchData) data;
            player.sendMessage("Arena Saved: " + death.arenaSaved);
            player.sendMessage(String.format("Arena area: (%d, %d, %d) to (%d, %d, %d)", death.pos1.getX(), death.pos1.getY(), death.pos1.getZ(), death.pos2.getX(), death.pos2.getY(), death.pos2.getZ()));
            player.sendMessage(Util.coordsToString("Spectator Location", death.spectatorLoc));
            player.sendMessage(String.format("Spawning area: (%d, %d, %d) to (%d, %d, %d)", death.startPos1.getX(), death.startPos1.getY(), death.startPos1.getZ(), death.startPos2.getX(), death.startPos2.getY(), death.startPos2.getZ()));    
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
     * @param type the type of kit to get
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
     * Joins a multiplayer game lobby
     * @param player the player who issued the command
     * @param args command arguments
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
        GameData data = MikeyMinigames.data.gameData.get(args[1]);

        // Add player to hashmap of players
        data.gamePlayers.put(player.getName(), player);

        // Add player data to hashmap
        data.gamePlayerObjects.put(player.getName(), new PlayerGameData(player));

        // Cast to type
        if (data instanceof DeathmatchData) {
            DeathmatchData deathmatchData = (DeathmatchData) data;

            // Make sure the game is enabled
            if (!deathmatchData.enabled) {
                player.sendMessage(ChatColor.RED + "Game not enabled!");
                return;
            }

            // Check for stopped state
            if (deathmatchData.gameState == GameState.RUNNING) { // TODO: Add join as spectator
                player.sendMessage(ChatColor.RED + "Game is currently playing");
                return;
            }

            // Add player to list of players in a game
            MikeyMinigames.data.playersInGameList.put(player, deathmatchData.name);

            // Clear inventory and prepare them for the game
            player.getInventory().clear();
            giveKit("spleef", player); // TODO: change kit based on game
            player.setHealth(20.0);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(deathmatchData.lobby);

            // Send player joined message
            player.sendMessage(ChatColor.AQUA + "Joined " + ChatColor.GOLD + args[1]);

            // If the game hasn't begun, start it
            if (deathmatchData.gameState == GameState.STOPPED)
                startLobby(deathmatchData);
        }
    }

    /**
     * Start the lobby of the multiplayer game
     * @param data the game data
     */
    public static void startLobby(DeathmatchData data) {
        data.timerId = createTimer(data.name, true, 30, "start", null);
        data.gameState = GameState.LOBBY;
    }

    /**
     * Command to autostart the game
     * @param player the player that issued the command
     * @param args command arguments
     */
    public static void startCall(Player player, String[] args) {
        // Check command
        if (Util.isArgsIncorrectLength(args, 2, "games start <GameName>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Get data object
        DeathmatchData data = (DeathmatchData) MikeyMinigames.data.gameData.get(args[1]);

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
     * @param gameName the name of the game
     */
    public static void start(String gameName) {
        // Get game data object
        GameData data = MikeyMinigames.data.gameData.get(gameName);

        // Cast to type
        if (data instanceof DeathmatchData) {
            DeathmatchData deathmatchData = (DeathmatchData) data;

            // Set the state to running
            deathmatchData.gameState = GameState.RUNNING;

            // Create the game stopwatch
            deathmatchData.timerId = createTimer(gameName, false, 0, null, "endgame");

            // Set the playersAlive to the number of players in the game
            deathmatchData.playersAlive = deathmatchData.gamePlayers.size();

            // Prep and teleport each player to the game arena
            for (Player player : deathmatchData.gamePlayers.values()) {
                deathmatchData.gamePlayerObjects.get(player.getName()).state = PlayerState.GAME;
                player.setGameMode(GameMode.SURVIVAL);
                player.setLevel(0);
                player.teleport(randomSpawn(deathmatchData.startPos1, deathmatchData.startPos2));
            }
        }

    }

    /**
     * Generates a random starting point from 2 corners
     * @param start lowest-coordinate-valued corner
     * @param end highest-coordinate-valued corner
     * @return the Location object representing spawn  \point
     */
    public static Location randomSpawn(BlockVector3 start, BlockVector3 end) { //TODO: Add facing center
        double x = Math.random() * (end.getX() - start.getX()) + start.getX();
        double z = Math.random() * (end.getZ() - start.getZ()) + start.getZ();
        return new Location(MikeyMinigames.data.currWorld, x, start.getY(), z);
    }

    /**
     * Player leaves game
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
        player.sendMessage(ChatColor.AQUA + "Left game " + ChatColor.GOLD + MikeyMinigames.data.gameData.get(MikeyMinigames.data.playersInGameList.get(player)).name);
        //TODO: Add check to see if no players left
    }

    /**
     * Runs commands to remove the player from the game
     * @param player the player that issued the command
     */
    public static void removeFromGame(Player player) {
        // Get data object
        GameData data = MikeyMinigames.data.gameData.get(MikeyMinigames.data.playersInGameList.get(player));

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
     * @param data the game data object
     */
    public static void checkForEndGame(GameData data) {
        // Cast to type
        if (data instanceof DeathmatchData) {
            DeathmatchData deathmatchData = (DeathmatchData) data;

            // For deathmatches, check to see if only one player is alive
            if (deathmatchData.playersAlive == 1) {
                // Set them as winner and get their name
                Player winner = getWinner(deathmatchData);
                String winName = winner.getName();

                // Cancel the running timer
                MikeyMinigames.instance.getServer().getScheduler().cancelTask(deathmatchData.timerId);

                // Get a list of player names and iterate through them, removing them from the game
                String[] pm = deathmatchData.gamePlayers.keySet().toArray(new String[0]);
                for (String p : pm) {
                    Player player = deathmatchData.gamePlayers.get(p);
                    removeFromGame(player);

                    // Show all players who won the game
                    player.sendTitle(String.format("%s won %s!", ChatColor.GOLD + winName, ChatColor.AQUA + deathmatchData.name), "", 10, 60, 20);
                }

                // Reload destroyed arena, reset timer count, and set game state to stopped
                GameSetup.loadArena(deathmatchData);
                deathmatchData.timerCount = 0;
                deathmatchData.gameState = GameState.STOPPED;
            }
        }
    }

    /**
     * Finds the winner from the list of players in a game
     * @param data the game data
     * @return the Player object representing the winner
     */
    public static Player getWinner(GameData data) {
        // Cast to game type
        if (data instanceof DeathmatchData) {
            DeathmatchData deathmatchData = (DeathmatchData) data;
            for (Player p : deathmatchData.gamePlayers.values())
                if (deathmatchData.gamePlayerObjects.get(p.getName()).state == PlayerState.GAME)
                    return p;
        }
        return null;
    }

    /**
     * Runs when the player dies: Normally health below 0 or y-level below 0
     * Is called on event listener
     * @param player the player that died
     * @param gameName the name of the game
     */
    public static void playerDeath(Player player, String gameName) {
        // Get game data object
        GameData gameData = MikeyMinigames.data.gameData.get(gameName);

        // Cast to type
        if (gameData instanceof DeathmatchData) { //TODO Add more conditions
            DeathmatchData data = (DeathmatchData) gameData;

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
    }

    /**
     * Creates the game timer and stores it in the game's timer object
     * @param game the name of hte game
     * @param countdown if the timer is a countdown or stopwatch
     * @param seconds number of seconds if it is a countdown
     * @param callback function to run after countdown ends
     * @param fun function to run while the command runs (see timerFun)
     * @return the ID of the timer
     */
    public static int createTimer(String game, boolean countdown, int seconds, String callback, String fun) {
        // Get game data object
        GameData gameData = MikeyMinigames.data.gameData.get(game);

        // Cast to type
        if (gameData instanceof DeathmatchData) {
            DeathmatchData data = (DeathmatchData) gameData;

            // Store timer value in timerCount
            data.timerCount = (countdown ? seconds : 0);

            // Iterate through players and set their xp bar level as the timer value
            for (Player p : data.gamePlayers.values()) {
                p.setLevel(data.timerCount);
            }

            // Create and return the timer ID
            return MikeyMinigames.instance.getServer().getScheduler().scheduleSyncRepeatingTask(MikeyMinigames.instance, (() -> runTimer(data, countdown, callback, fun)), 20L, 20L);
        }

        // TODO: more
        return -1;
    }

    /**
     * Runnable function inside of the timer
     * @param data the game data object
     * @param countdown if the timer is a countdown or stopwatch
     * @param callback function to run after countdown ends
     * @param fun function to run while the command runs (see timerFun)
     */
    public static void runTimer(GameData data, boolean countdown, String callback, String fun) {
        // Decrement/increment timer
        int timerChange = countdown ? -1 : 1;

        // Cast to type
        // TODO: Add more
        if (data instanceof DeathmatchData) {
            DeathmatchData deathmatchData = (DeathmatchData) data;

            // Change the stored timer value
            deathmatchData.timerCount += timerChange;

            // Check for when the timer runs out
            if (deathmatchData.timerCount == 0) {
                timerEnd(callback, deathmatchData);
            }

            // Set XP bar to display timer for all game players
            for (Player p : deathmatchData.gamePlayers.values()) {
                p.setLevel(deathmatchData.timerCount);
            }
        }

        // Run timer functions
        if (fun != null)
            timerFun(fun, data);
    }

    /**
     * Runs the timer function based on the name
     * @param fun name of the function
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
     * @param callback the name of the callback function
     * @param data the game data object
     */
    public static void timerEnd(String callback, GameData data) {
        // Cast to type
        if (data instanceof DeathmatchData) {
            DeathmatchData deathmatchData = (DeathmatchData) data;

            // Stop timer
            MikeyMinigames.instance.getServer().getScheduler().cancelTask(deathmatchData.timerId);
        }

        // Run callback function
        switch (callback) {
            case "start":
                start(data.name);
                break;
        }
    }
}
