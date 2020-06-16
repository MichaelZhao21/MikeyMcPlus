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
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.michaelzhao.mikeyminigames.MikeyMinigames;
import xyz.michaelzhao.mikeyminigames.Util;

import java.io.*;

public class GameSetup {
    /**
     * Creates a new game with a game name and type
     *
     * @param player player that issued the command
     * @param args   command arguments
     */
    public static void newGame(Player player, String[] args) {
        // Check args
        if (Util.isArgsIncorrectLength(args, 2, "games add <Game Name>", player)) return;

        // Check if the game exists
        if (MikeyMinigames.data.gameData.containsKey(args[1])) {
            player.sendMessage(ChatColor.RED + args[1] + " already exists!");
            return;
        }

        // Create data object
        GameData data = new GameData(args[1]);

        // Add to hashmap and send added message
        MikeyMinigames.data.gameData.put(args[1], data);
        player.sendMessage(ChatColor.GOLD + "Added " + args[1]);

        // Saves game
        saveGame(args[1]);
    }

    /**
     * Gives the arena selection tool to the player
     *
     * @param player player that issued the command
     * @param args   command arguments
     */
    public static void giveTool(Player player, String[] args) {
        // Check command
        if (Util.isArgsIncorrectLength(args, 2, "games tool <Game Name>", player)) return;
        if (Util.isInvalidGame(args[1], player)) return;

        // Add items to player and send message
        player.getInventory().addItem(Util.createInventoryItem(Material.BLAZE_ROD, 1,
                ChatColor.GOLD + "Minigame Tool",
                args[1]));
        player.sendMessage(ChatColor.AQUA + "Minigame tool - Select corners of the game area (to be saved and regenerated)");
        player.sendMessage(ChatColor.AQUA + "Left click to select pos1 and right click to select pos2");
    }

    /**
     * List out the games currently avaliable
     *
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

    public static void setCorners(Player player, Action action, PlayerInteractEvent event) {
        if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            // Get the block clicked
            Block block = event.getClickedBlock();
            if (block == null) return;

            // Get coordinates and store that position
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            // Get game data object
            GameData data = Util.getData(MikeyMinigames.data.toolInventory.toolGame);

            // Store in pos1/pos2 or startPos1/startPos2 based on clicks and tool mode
            // TODO: Encapsulate or make nicer (repeated code!)
            if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                event.setCancelled(true);
                player.sendMessage(String.format("%sPos1 set to: (%d, %d, %d)", ChatColor.LIGHT_PURPLE, x, y, z));
                if (MikeyMinigames.data.toolInventory.toolMode == ToolMode.ARENA)
                    data.pos1 = BlockVector3.at(x, y, z);
                else
                    data.startPos1 = BlockVector3.at(x, y + 1, z);
            } else {
                player.sendMessage(String.format("%sPos2 set to: (%d, %d, %d)", ChatColor.LIGHT_PURPLE, x, y, z));
                if (MikeyMinigames.data.toolInventory.toolMode == ToolMode.ARENA)
                    data.pos2 = BlockVector3.at(x, y, z);
                else
                    data.startPos2 = BlockVector3.at(x, y + 1, z);
            }
        }
    }

    /**
     * Set position with tool click based on player position
     *
     * @param toolMode the mode the tool is in (based on the enum)
     * @param action   the action performed
     * @param player   player that issued the command
     */
    public static void setPos(ToolMode toolMode, Action action, Player player) {
        // Get if the player left clicked
        boolean leftClick = (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR);

        // Get game data object
        GameData data = Util.getData(MikeyMinigames.data.toolInventory.toolGame);

        // Get player location
        Location loc = player.getLocation();
        BlockVector3 pos = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        switch (toolMode) {
            case LOBBY:
                data.lobby = loc;
                break;
            case SPAWN_PLATFORM:
                if (leftClick)
                    data.startPos1 = pos;
                else
                    data.startPos2 = pos;
                break;
            case SPECTATOR:
                data.spectatorLoc = loc;
                break;
            case EXIT:
                data.exitLoc = loc;
                break;
        }
        player.sendMessage(ChatColor.GOLD + ToolInventory.toolModeToString(toolMode) + " set!");
    }

    /**
     * Player runs arena command
     *
     * @param player player that issued the command
     * @param args   command arguments
     */
    public static void arenaCommand(Player player, String[] args) {
        // Check command
        if (Util.isArgsIncorrectLength(args, 3, "games arena <save | load> <Game Name>", player)) return;
        if (Util.isInvalidGame(args[2], player)) return;

        // Get game data
        GameData data = Util.getData(args[2]);

        // Check to see if arena is enabled
        if (!data.hasArena) {
            player.sendMessage(ChatColor.RED + "Game " + data.name + " doesn't have arena enabled");
            return;
        }

        // Check operation and run method if valid
        if (args[1].equals("save"))
            saveArena(player, data);
        else if (args[1].equals("load"))
            loadArena(data);
        else {
            player.sendMessage(ChatColor.RED + "Unknown operation" + args[2]);
            player.sendMessage(ChatColor.RED + "Usage: /games arena <save | load> <Game Name>");
        }
    }

    /**
     * Saves the arena
     *
     * @param player player that issued the command
     * @param data   the game object base class
     */
    public static void saveArena(Player player, GameData data) {
        // TODO: update corners when saving
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
     *
     * @param data the game object base class
     */
    public static void loadArena(GameData data) {
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
     *
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
     *
     * @param gameName the name of the game
     */
    public static void saveGame(String gameName) {
        // Get game data and create output object
        GameData data = Util.getData(gameName);
        JSONObject out = new JSONObject();

        // Add general data
        // TODO: update save data
        out.put("name", data.name);
        out.put("enabled", data.enabled);
        out.put("lobby", Util.locationToJsonArr(data.lobby));
        out.put("exitLoc", Util.locationToJsonArr(data.exitLoc));
        out.put("gameType", GameData.gameTypeToString(data.gameType));
        out.put("pos1", Util.blockVector3ToJsonArr(data.pos1));
        out.put("pos2", Util.blockVector3ToJsonArr(data.pos2));
        out.put("arenaSaved", data.arenaSaved);
        out.put("spectatorLoc", Util.locationToJsonArr(data.spectatorLoc));
        out.put("startPlatform1", Util.blockVector3ToJsonArr(data.startPos1));
        out.put("startPlatform2", Util.blockVector3ToJsonArr(data.startPos2));
        out.put("hasArena", data.hasArena);
        out.put("hasLobby", data.hasLobby);
        out.put("hasSpawnPlatform", data.hasSpawnPlatform);
        out.put("hasSpectators", data.hasSpectators);
        out.put("hasCheckpoints", data.hasCheckpoints);

        // Write data to file
        try {
            FileWriter fw = new FileWriter(Util.getFileInDir(data.gameFolder, gameName + ".dat"));
            fw.write(out.toJSONString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the load games command on all games and creates the game objects from data files
     *
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

        if (input.equals("[]")) {
            sender.sendMessage(ChatColor.RED + "No games exist");
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
     *
     * @param gameName the name of the game
     */
    public static void loadGame(String gameName) {
        // Read the file
        String input = Util.readAllLines(Util.getFileInDir(new File(Util.getSubPath(MikeyMinigames.data.gamesFolder, gameName)), gameName + ".dat"));

        // Parse the data
        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(input);

            // Get the game name
            String name = object.get("name").toString();

            // Create data object
            GameData data = new GameData(name);

            // Load general attributes
            data.gameType = GameData.stringToGameType(object.get("gameType").toString());
            data.enabled = object.get("enabled").toString().equals("true");
            data.lobby = Util.jsonArrToLocation("lobby", object);
            data.exitLoc = Util.jsonArrToLocation("exitLoc", object);
            data.spectatorLoc = Util.jsonArrToLocation("spectatorLoc", object);
            data.startPos1 = Util.jsonArrToBlockVector3("startPlatform1", object);
            data.startPos2 = Util.jsonArrToBlockVector3("startPlatform2", object);
            data.pos1 = Util.jsonArrToBlockVector3("pos1", object);
            data.pos2 = Util.jsonArrToBlockVector3("pos2", object);
            data.arenaSaved = object.get("arenaSaved").toString().equals("true");
            data.hasCheckpoints = object.get("hasCheckpoints").toString().equals("true");
            data.hasArena = object.get("hasArena").toString().equals("true");
            data.hasSpectators = object.get("hasSpectators").toString().equals("true");
            data.hasSpawnPlatform = object.get("hasSpawnPlatform").toString().equals("true");
            data.hasLobby = object.get("hasLobby").toString().equals("true");

            // Add gameData to hashmap of games
            MikeyMinigames.data.gameData.put(data.name, data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
