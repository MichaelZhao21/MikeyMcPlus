package xyz.michaelzhao.mikeyminigames.games;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.michaelzhao.mikeyminigames.MikeyMinigames;
import xyz.michaelzhao.mikeyminigames.Util;

import java.io.*;
import java.util.Arrays;

public class GameSetup {
    /**
     * Creates a new game with a game name and type
     * @param player player that issued the command
     * @param args command arguments
     */
    public static void newGame(Player player, String[] args) {
        //Check args
        if (Util.isArgsIncorrectLength(args, 3, "Usage: /games add <Game Name> <deathmatch|parkour>", player)) return;

        // All game names are lowercase
        args[1] = args[1].toLowerCase();

        // Check if the game exists
        if (MikeyMinigames.data.gameData.containsKey(args[1])) {
            player.sendMessage(ChatColor.RED + args[1] + " already exists!");
            return;
        }

        // Check for valid game type
        if (!GameData.isValidGameType(args[2], player)) return;

        // Create general data object and child class // TODO: fix this description
        GameData data;
        if (args[2].equals("deathmatch"))
            data = new DeathmatchData(args[1]);
        else if (args[2].equals("parkour"))
            data = new SingleplayerData(args[1]);
        else
            return;

        // Add to hashmap and send added message
        MikeyMinigames.data.gameData.put(args[1], data);
        player.sendMessage(ChatColor.GOLD + "Added " + args[1]);

        // Saves game
        saveGame(args[1]);
    }

    /**
     * Gives the arena selection tool to the player
     * @param player player that issued the command
     * @param args command arguments
     */
    public static void giveTool(Player player, String[] args) {
        // Check command
        if (Util.isArgsIncorrectLength(args, 2, "games tool <Game Name>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Sets the game to the toolGame
        MikeyMinigames.data.toolGame = args[1]; // TODO: Add UI for tool | Maybe create tool class?

        // Create tool and give it to the player
        ItemStack tool = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = tool.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Minigame Tool");
        meta.setLore(Arrays.asList("Left click to select pos1", "Right click to select pos2"));
        tool.setItemMeta(meta);
        tool.setAmount(1);

        // Add items to player and send message
        player.getInventory().addItem(tool);
        player.sendMessage(ChatColor.AQUA + "Minigame tool - Select corners of the game area (to be saved and regenerated)");
        player.sendMessage(ChatColor.AQUA + "Left click to select pos1 and right click to select pos2");
    }

    /**
     * List out the games currently avaliable
     * @param player player that issued the command
     */
    public static void list(Player player, String[] args) {
        // Check args length
        if (Util.isArgsIncorrectLength(args, 1, "games list", player)) return;

        // List title
        player.sendMessage(ChatColor.AQUA + "List of minigames:");

        // Print out list of games and save count
        int count = 0;
        for (String str : MikeyMinigames.data.gameData.keySet()) {
            count++;
            player.sendMessage(ChatColor.GRAY + Integer.toString(count) + ". " + ChatColor.GREEN + str);
        }

        // Send message if no games
        if (count == 0)
            player.sendMessage(ChatColor.GREEN + "No games avaliable");
    }

    /**
     * Set position
     * @param player player that issued the command
     * @param args command arguments
     */
    public static void setPos(Player player, String[] args) { // TODO: put this on the tool
        DeathmatchData data = (DeathmatchData) MikeyMinigames.data.gameData.get(MikeyMinigames.data.toolGame); // TODO: Fix casting
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /games setPos <lobby | startPlatform1 | startPlatform2 | spectatorLoc | exitLoc>");
        }
        else if (args[1].equals("lobby") || args[1].equals("startPlatform1") || args[1].equals("startPlatform2") || args[1].equals("spectatorLoc") || args[1].equals("exitLoc")) {
            Location loc = player.getLocation();
            BlockVector3 pos = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
            switch (args[1]) {
                case "lobby":
                    data.lobby = loc;
                    break;
                case "startPlatform1":
                    data.startPos1 = pos;
                    break;
                case "startPlatform2":
                    data.startPos2 = pos;
                    break;
                case "spectatorLoc":
                    data.spectatorLoc = loc;
                    break;
                case "exitLoc":
                    data.exitLoc = loc;
                    break;
            }
            player.sendMessage(ChatColor.GOLD + args[1] + " position set!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Invalid set position, use <lobby | startPlatform1 | startPlatform2 | spectatorLoc | exitLoc>");
        }
    }

    /**
     * Player runs arena command
     * @param player player that issued the command
     * @param args command arguments
     */
    public static void arenaCommand(Player player, String[] args) {
        // Check command
        if (Util.isArgsIncorrectLength(args, 3, "games arena <save | load> <Game Name>", player)) return;
        if (Util.isInvalidGame(args[2], player)) return;

        // All game names lowercase
        args[2] = args[2].toLowerCase();

        // Get data and make sure it's the right type
        GameData dataObj = MikeyMinigames.data.gameData.get(args[2]);
        if (!(dataObj instanceof DeathmatchData)) {
            player.sendMessage(ChatColor.RED + "Game mode " + dataObj.getGameType() + " does not have an arena");
        }

        // Check operation and run method if valid
        if (args[1].equals("save"))
            saveArena(player, dataObj);
        else if (args[1].equals("load"))
            loadArena(dataObj);
        else { // TODO: Make this error message more logical ?(move before game mode error)
            player.sendMessage(ChatColor.RED + "Unknown operation" + args[2]);
            player.sendMessage(ChatColor.RED + "Usage: /games arena <save | load> <Game Name>");
        }
    }

    /**
     * Saves the arena
     * @param player player that issued the command
     * @param gameData the game object base class
     */
    public static void saveArena(Player player, GameData gameData) {
        // Cast to correct type
        DeathmatchData data = (DeathmatchData) gameData;

        // Get the region object from position 1 and 2
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(MikeyMinigames.data.currWorld), data.pos1, data.pos2);
        data.arenaSaved = true;

        // Tell the player that we're saving
        player.sendMessage(ChatColor.AQUA + "Saving " + region.getArea() + " blocks...");

        // Create clipboard and editsession from region and copy it
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(true);
        try {
            Operations.complete(forwardExtentCopy);
            editSession.flushSession();
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

        // Write clipboard to the save file
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(Util.getFileInDir(data.gameFolder, data.name + ".arena")))) {
            try {
                writer.write(clipboard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set the arenaSaved flag to true and tell the player that the arena was saved
        player.sendMessage(ChatColor.GOLD + "Saved!");

        // Saves the game
        saveGame(data.name);
    }

    /**
     * Loads the arena
     * @param gameData the game object base class
     */
    public static void loadArena(GameData gameData) {
        // Convert to correct type
        DeathmatchData data = (DeathmatchData) gameData;

        // Create clipboard from file
        File gameFile = Util.getFileInDir(data.gameFolder, data.name + ".arena");
        ClipboardFormat format = ClipboardFormats.findByFile(gameFile);

        // Reads the schematic and pastes
        try (ClipboardReader reader = format.getReader(new FileInputStream(gameFile))) { // TODO: add exception
            Clipboard clipboard = reader.read();
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(MikeyMinigames.data.currWorld), -1);
            double x = data.pos1.getX();
            double y = data.pos1.getY();
            double z = data.pos1.getZ();
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
            editSession.flushSession();
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the save command on all games and creates a global games json file to reference
     * @param sender player/console that issued the command
     */
    public static void saveAllGames(CommandSender sender) {
        // Create output array
        JSONArray out = new JSONArray();

        // Iterate through games, add game name to output array and run saveGame method
        for (String str : MikeyMinigames.data.gameData.keySet()) {
            out.add(str);
            saveGame(str);
            sender.sendMessage(ChatColor.GOLD + str + ChatColor.AQUA + " was saved successfully");
        }

        // Write output array to games json file
        try {
            FileWriter fw = new FileWriter(Util.getFileInDir(MikeyMinigames.data.gamesFolder, "games.json"));
            fw.write(out.toJSONString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Tell the player that the saving was successful
        sender.sendMessage(ChatColor.GOLD + "Games saved!");
    }

    /**
     * Saves single game to its own file in the games folder
     * @param gameName the name of the game
     */
    public static void saveGame(String gameName) {
        // Get game data and create output object
        GameData gameData = MikeyMinigames.data.gameData.get(gameName);
        JSONObject out = new JSONObject();

        // Add general data
        out.put("gameType", gameData.getGameType());
        out.put("name", gameData.name);
        out.put("enabled", gameData.enabled);
        out.put("lobby", Util.locationToJsonArr(gameData.lobby));
        out.put("exitLoc", Util.locationToJsonArr(gameData.exitLoc));

        // Add game type specific data
        if (gameData instanceof DeathmatchData) {
            DeathmatchData deathmatchData = (DeathmatchData) gameData;
            out.put("pos1", Util.blockVector3ToJsonArr(deathmatchData.pos1));
            out.put("pos2", Util.blockVector3ToJsonArr(deathmatchData.pos2));
            out.put("arenaSaved", deathmatchData.arenaSaved);
            out.put("spectatorLoc", Util.locationToJsonArr(deathmatchData.spectatorLoc));
            out.put("startPlatform1", Util.blockVector3ToJsonArr(deathmatchData.startPos1));
            out.put("startPlatform2", Util.blockVector3ToJsonArr(deathmatchData.startPos2));
        }

        // Write data to file
        try {
            FileWriter fw = new FileWriter(Util.getFileInDir(MikeyMinigames.data.gamesFolder, gameName + ".dat"));
            fw.write(out.toJSONString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the load games command on all games and creates the game objects from data files
     * @param sender the player that issued the command
     */
    public static void loadAllGames(CommandSender sender) {
        // Send loading message to player
        sender.sendMessage(ChatColor.AQUA + "Loading all games...");

        // Read the file
        String input = Util.readAllLines(Util.getFileInDir(MikeyMinigames.data.gamesFolder, "games.json"));

        // Return if the file doesn't exist
        if (input == null) {
            sender.sendMessage(ChatColor.RED + "Games file doesn't exist!");
            return;
        }

        // Create JSON parser and parse the input into an array
        JSONParser parser = new JSONParser();
        JSONArray arr;
        try {
            arr = (JSONArray) parser.parse(input);

            // Iterate through the array and load the game
            for (Object o : arr.toArray()) {
                loadGame((String) o);
                sender.sendMessage(ChatColor.AQUA + "Loaded " + ChatColor.GOLD + o);
            }
            sender.sendMessage(ChatColor.AQUA + "Loaded all games!");
        } catch (ParseException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "No games found");
        }
    }

    /**
     * Loads single game from the games folder
     * @param gameName the name of the game
     */
    public static void loadGame(String gameName) {
        // Read the file
        String input = Util.readAllLines(Util.getFileInDir(MikeyMinigames.data.gamesFolder, gameName + ".dat"));

        // Parse the data
        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(input);

            // Get the game type
            String type = object.get("gameType").toString();

            // Create game based on data type and load specific attributes first
            // Then cast to base class type
            GameData data;
            if (type.equals("deathmatch")) { //TODO: switch
                DeathmatchData deathmatchData = new DeathmatchData(object.get("name").toString());
                deathmatchData.spectatorLoc = Util.jsonArrToLocation("spectatorLoc", object);
                deathmatchData.startPos1 = Util.jsonArrToBlockVector3("startPlatform1", object);
                deathmatchData.startPos2 = Util.jsonArrToBlockVector3("startPlatform2", object);
                deathmatchData.pos1 = Util.jsonArrToBlockVector3("pos1", object);
                deathmatchData.pos2 = Util.jsonArrToBlockVector3("pos2", object);
                deathmatchData.arenaSaved = object.get("arenaSaved").toString().equals("true");
                data = deathmatchData;
            }
            else if (type.equals("parkour")) {
                data = null; //TODO: add
            }
            else {
                return;
            }

            // Load general attributes
            data.enabled = object.get("enabled").toString().equals("true");
            data.lobby = Util.jsonArrToLocation("lobby", object);
            data.exitLoc = Util.jsonArrToLocation("exitLoc", object);
            MikeyMinigames.data.gameData.put(data.name, data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
}
