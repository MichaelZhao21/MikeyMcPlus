package xyz.michaelzhao.mikeymcplus.games;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.michaelzhao.mikeymcplus.MikeyMcPlus;

import java.io.*;
import java.util.Arrays;

public class GameSetup {

    /**
     * Creates a new game with a game name and type
     * @param player - player that issued the command
     * @param args - command arguments
     */
    public static void newGame(Player player, String[] args) {
        // All game names lowercase
        args[1] = args[1].toLowerCase();

        // Usage error message
        String usage = ChatColor.RED + "Usage: /games add <Game Name> <deathmatch | parkour>";

        // Check to see if correct num of args
        if (args.length != 3) {
            player.sendMessage(usage);
            return;
        }

        // Check for valid GameType
        if (GameData.stringToGameType(args[2]) == null) {
            player.sendMessage(usage);
            return;
        }

        // Add game or tell player that it already exists
        GameData data = MikeyMcPlus.data.gameData.putIfAbsent(args[1], new GameData(args[1], GameData.stringToGameType(args[2])));
        if (data == null)
            player.sendMessage(ChatColor.RED + args[1] + " already exists!");
        else
            player.sendMessage(ChatColor.GOLD + "Added " + args[1]);

        // Set game to current game and save
        MikeyMcPlus.data.toolGame = args[1];
        player.sendMessage(ChatColor.GOLD + args[1] + ChatColor.GOLD + " set to active game editor");
        saveGame(args[1]);
    }

    /**
     * Gives the stage selection tool to the player
     * @param player - player that issued the command
     * @param args - command arguments
     */
    public static void giveTool(Player player, String[] args) {
        // Check for num of args
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /games tool <Game Name>");
            return;
        }

        // Get game and send error if invalid name
        GameData data = MikeyMcPlus.data.gameData.get(args[1]);
        if (data == null) {
            player.sendMessage(ChatColor.RED + data.name + " is not a valid game");
            return;
        }

        // Sets the game to the toolGame
        MikeyMcPlus.data.toolGame = args[1];

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
     * @param player - player that issued the command
     */
    public static void list(Player player) {
        // List title
        player.sendMessage(ChatColor.AQUA + "List of minigames:");

        // Print out list of games and save count
        int count = 0;
        for (String str : MikeyMcPlus.data.gameData.keySet()) {
            count++;
            player.sendMessage(ChatColor.GRAY + Integer.toString(count) + ". " + ChatColor.GREEN + str);
        }

        // Send message if no games
        if (count == 0)
            player.sendMessage(ChatColor.GREEN + "No games avaliable");
    }

    /**
     * Opens the game file
     * @return the file object
     */
    public static File getGameFile() {
        // Creates a file object, replacing spaces with underscores in the game name
        File gameFile = new File(MikeyMcPlus.data.stageFolder + System.getProperty("file.separator") + MikeyMcPlus.data.toolGame.replace(' ', '_'));

        // Check to see if the file exists and creates one if not
        if (!gameFile.exists()) {
            try {
                gameFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Returns the file object
        return gameFile;
    }

    /**
     * Checks to see if any game is active
     * @param player - the player object
     * @return - no game selected
     */
    public static boolean isNoGameSelected(Player player) { // TODO: Remove this function oml
        if (!MikeyMcPlus.data.toolGame.isEmpty())
            return false;
        player.sendMessage(ChatColor.RED + "No active game editors");
        return true;
    }

    /**
     * Set position
     * @param player - player that issued the command
     * @param args - command arguments
     */
    public static void setPos(Player player, String[] args) { // TODO: put this on the tool
        DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.toolGame); // TODO: Fix casting
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
                    data.startPlatform1 = pos;
                    break;
                case "startPlatform2":
                    data.startPlatform2 = pos;
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
     * Saves the stage
     * @param player - player that issued the command
     * @param args - command arguments
     */
    public static void saveStage(Player player, String[] args) {
        // TODO: Check args
        DeathmatchData gameData = (DeathmatchData) MikeyMcPlus.data.gameData.get(args[1]); // TODO: Fix casting
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(MikeyMcPlus.data.currWorld), gameData.pos1, gameData.pos2);
        player.sendMessage(ChatColor.AQUA + "Saving " + region.getArea() + " blocks...");
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

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(getGameFile()))) {
            try {
                writer.write(clipboard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameData.stageSaved = true;
        player.sendMessage(ChatColor.GOLD + "Saved!");
        saveGame(args[1]);
    }

    public static void loadStage() {
        ClipboardFormat format = ClipboardFormats.findByFile(getGameFile());
        try (ClipboardReader reader = format.getReader(new FileInputStream(getGameFile()))) {
            Clipboard clipboard = reader.read();
            DeathmatchData currGame = (DeathmatchData) MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.toolGame); // TODO: Fix casting
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(MikeyMcPlus.data.currWorld), -1);
            double x = currGame.pos1.getX();
            double y = currGame.pos1.getY();
            double z = currGame.pos1.getZ();
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

    public static void saveAllGames(Player player) {
        JSONArray out = new JSONArray();
        for (String str : MikeyMcPlus.data.gameData.keySet()) {
            out.add(str);
            MikeyMcPlus.data.toolGame = str;
            saveGame(str);
        }
        try {
            FileWriter fw = new FileWriter(MikeyMcPlus.instance.getDataFolder().getPath() + System.getProperty("file.separator") + "games.json");
            fw.write(out.toJSONString());
            fw.close();
            player.sendMessage(ChatColor.GOLD + "Games saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveGame(String gameName) {
        DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.toolGame); // TODO: Fix casting
        JSONObject out = new JSONObject();
        out.put("name", data.name);
        out.put("pos1", BlockVector3ToJsonArr(data.pos1));
        out.put("pos2", BlockVector3ToJsonArr(data.pos2));
        out.put("stageSaved", data.stageSaved);
        out.put("enabled", data.enabled);
        out.put("lobby", LocationToJsonArr(data.lobby));
        out.put("spectatorLoc", LocationToJsonArr(data.spectatorLoc));
        out.put("exitLoc", LocationToJsonArr(data.exitLoc));
        out.put("startPlatform1", BlockVector3ToJsonArr(data.startPlatform1));
        out.put("startPlatform2", BlockVector3ToJsonArr(data.startPlatform2));
        try {
            FileWriter fw = new FileWriter(MikeyMcPlus.data.gamesFolder.getPath() + System.getProperty("file.separator") + MikeyMcPlus.data.toolGame + ".json");
            fw.write(out.toJSONString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAllGames(Player player) {
        player.sendMessage(ChatColor.AQUA + "Loading all games...");
        String input = readAllLines(MikeyMcPlus.instance.getDataFolder().getPath() + System.getProperty("file.separator") + "games.json");
        if (input == null) return;
        JSONParser parser = new JSONParser();
        JSONArray arr;
        try {
            arr = (JSONArray) parser.parse(input);
            for (Object o : arr.toArray()) {
                MikeyMcPlus.data.toolGame = (String) o;
                loadGame(player);
            }
            player.sendMessage(ChatColor.AQUA + "Loaded all games!");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void loadGame(Player player) { // TODO: remove _ from saves
        String input = readAllLines(MikeyMcPlus.data.gamesFolder.getPath() + System.getProperty("file.separator") + MikeyMcPlus.data.toolGame + ".json");
        JSONParser parser = new JSONParser();
        JSONObject object;
        try {
            object = (JSONObject) parser.parse(input);
            DeathmatchData data = new DeathmatchData(object.get("name").toString(), GameType.DEATHMATCH); // TODO: Fix casting
            data.pos1 = JsonArrToBlockVector3("pos1", object);
            data.pos2 = JsonArrToBlockVector3("pos2", object);
            data.stageSaved = object.get("stageSaved").toString().equals("true");
            data.enabled = object.get("enabled").toString().equals("true");
            data.lobby = JsonArrToLocation("lobby", object);
            data.spectatorLoc = JsonArrToLocation("spectatorLoc", object);
            data.startPlatform1 = JsonArrToBlockVector3("startPlatform1", object);
            data.startPlatform2 = JsonArrToBlockVector3("startPlatform2", object);
            data.exitLoc = JsonArrToLocation("exitLoc", object);
            MikeyMcPlus.data.gameData.put(data.name, data);
            player.sendMessage(ChatColor.GOLD + "Loaded " + data.name);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static BlockVector3 JsonArrToBlockVector3(String attrib, JSONObject obj) {
        JSONArray arr = (JSONArray) obj.get(attrib);
        return BlockVector3.at(Integer.parseInt(arr.get(0).toString()), Integer.parseInt(arr.get(1).toString()), Integer.parseInt(arr.get(2).toString()));
    }

    public static JSONArray BlockVector3ToJsonArr(BlockVector3 b) {
        JSONArray arr = new JSONArray();
        arr.addAll(Arrays.asList(b.getX(), b.getY(), b.getZ()));
        return arr;
    }

    public static Location JsonArrToLocation(String attrib, JSONObject obj) {
        JSONArray arr = (JSONArray) obj.get(attrib);
        Location l = new Location(MikeyMcPlus.data.currWorld, Integer.parseInt(arr.get(0).toString()), Integer.parseInt(arr.get(1).toString()), Integer.parseInt(arr.get(2).toString()));
        l.setDirection(new Vector(Integer.parseInt(arr.get(3).toString()), Integer.parseInt(arr.get(4).toString()), Integer.parseInt(arr.get(5).toString())));
        return l;
    }

    public static JSONArray LocationToJsonArr(Location l) {
        JSONArray arr = new JSONArray();
        arr.addAll(Arrays.asList((int) l.getX(), (int) l.getY(), (int) l.getZ(), (int) l.getDirection().getX(), (int) l.getDirection().getY(), (int) l.getDirection().getZ()));
        return arr;
    }

    public static String readAllLines(String path) {
        try {
            BufferedReader f = new BufferedReader(new FileReader(path));
            String line;
            StringBuilder in = new StringBuilder();
            while ((line = f.readLine()) != null) {
                in.append(line);
            }
            f.close();
            return in.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
