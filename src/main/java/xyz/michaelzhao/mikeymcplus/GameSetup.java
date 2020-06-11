package xyz.michaelzhao.mikeymcplus;

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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Arrays;

public class GameSetup {

    public static void newGame(Player player, String[] args) {
        if (args.length != 2) return;
        if (MikeyMcPlus.data.gameData.putIfAbsent(args[1], new GameData(player.getWorld(), args[1])) == null)
            player.sendMessage(ChatColor.GOLD + "Added " + args[1]);
        else
            player.sendMessage(ChatColor.RED + args[1] + " already exists!");
        MikeyMcPlus.data.currGame = args[1];
        player.sendMessage(ChatColor.GOLD + args[1] + ChatColor.GOLD + " set to active game editor");
        saveGame();
    }

    public static void setActive(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(MikeyMcPlus.data.currGame.isEmpty() ? ChatColor.RED + "No game editor active" : ChatColor.GOLD + MikeyMcPlus.data.currGame + ChatColor.AQUA + " is active");
        }
        else if (args.length == 2) {
            if (MikeyMcPlus.data.gameData.containsKey(args[1])) {
                MikeyMcPlus.data.currGame = args[1];
                player.sendMessage(ChatColor.GOLD + args[1] + ChatColor.GOLD + " set to active game editor");
            }
            else
                player.sendMessage(ChatColor.RED + "Could not find game with name " + ChatColor.GOLD + args[1]);
        }
    }

    public static void giveTool(Player player) {
        ItemStack tool = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = tool.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "Minigame Tool");
        meta.setLore(Arrays.asList("Left click to select pos1", "Right click to select pos2"));
        tool.setItemMeta(meta);
        tool.setAmount(1);
        player.getInventory().addItem(tool);
        player.sendMessage(ChatColor.AQUA + "Minigame tool: Left click to select pos1 and right click to select pos2");
        player.sendMessage(ChatColor.AQUA + "pos1 MUST be the bottom NW corner and pos2 MUST be the top SE corner");
    }

    public static void list(Player player) {
        player.sendMessage(ChatColor.AQUA + "All avaliable editors:");
        for (String str : MikeyMcPlus.data.gameData.keySet()) {
            player.sendMessage(ChatColor.GREEN + str);
        }
    }

    public static File getGameFile() {
        File gameFile = new File(MikeyMcPlus.data.stageFolder + System.getProperty("file.separator") + MikeyMcPlus.data.currGame.replace(' ', '_'));
        if (!gameFile.exists()) {
            try {
                gameFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return gameFile;
    }

    public static boolean isNoGameSelected(Player player) {
        if (MikeyMcPlus.data.currGame.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No active game editors");
            return true;
        }
        return false;
    }

    public static void setPos(Player player, String[] args) {
        GameData data = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /games setPos <lobby | startPlatform1 | startPlatform2 | spectatorLoc | exitLoc>");
        }
        else if (args[1].equals("lobby") || args[1].equals("startPlatform1") || args[1].equals("startPlatform2") || args[1].equals("spectatorLoc") || args[1].equals("exitLoc")) {
            double x = player.getLocation().getX();
            double y = player.getLocation().getY();
            double z = player.getLocation().getZ();
            BlockVector3 pos = BlockVector3.at(x, y, z);
            switch (args[1]) {
                case "lobby":
                    data.lobby = pos;
                    break;
                case "startPlatform1":
                    data.startPlatform1 = pos;
                    break;
                case "startPlatform2":
                    data.startPlatform2 = pos;
                    break;
                case "spectatorLoc":
                    data.spectatorLoc = pos;
                    break;
                case "exitLoc":
                    data.exitLoc = pos;
                    break;
            }
            player.sendMessage(ChatColor.GOLD + args[1] + " position set!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Invalid set position, use <lobby | startPlatform1 | startPlatform2 | spectatorLoc | exitLoc>");
        }
    }

    public static void saveStage(Player player) {
        GameData gameData = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
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

        MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame).stageSaved = true;
        player.sendMessage(ChatColor.GOLD + "Saved!");
    }

    public static void loadStage() {
        ClipboardFormat format = ClipboardFormats.findByFile(getGameFile());
        try (ClipboardReader reader = format.getReader(new FileInputStream(getGameFile()))) {
            Clipboard clipboard = reader.read();
            GameData currGame = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
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
            MikeyMcPlus.data.currGame = str;
            saveGame();
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

    public static void saveGame() {
        GameData data = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
        JSONObject out = new JSONObject();
        out.put("name", data.name);
        out.put("pos1", CoordinatesToJsonArr(data.pos1));
        out.put("pos2", CoordinatesToJsonArr(data.pos2));
        out.put("stageSaved", data.stageSaved);
        out.put("enabled", data.enabled);
        out.put("lobby", CoordinatesToJsonArr(data.lobby));
        out.put("spectatorLoc", CoordinatesToJsonArr(data.spectatorLoc));
        out.put("exitLoc", CoordinatesToJsonArr(data.exitLoc));
        out.put("startPlatform1", CoordinatesToJsonArr(data.startPlatform1));
        out.put("startPlatform2", CoordinatesToJsonArr(data.startPlatform2));
        try {
            FileWriter fw = new FileWriter(MikeyMcPlus.data.gamesFolder.getPath() + System.getProperty("file.separator") + MikeyMcPlus.data.currGame + ".json");
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
                MikeyMcPlus.data.currGame = (String) o;
                loadGame(player);
            }
            player.sendMessage(ChatColor.AQUA + "Loaded all games!");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void loadGame(Player player) {
        String input = readAllLines(MikeyMcPlus.data.gamesFolder.getPath() + System.getProperty("file.separator") + MikeyMcPlus.data.currGame + ".json");
        JSONParser parser = new JSONParser();
        JSONObject object;
        try {
            object = (JSONObject) parser.parse(input);
            GameData data = new GameData(player.getWorld(), object.get("name").toString());
            data.pos1 = JsonArrToCoordinates("pos1", object);
            data.pos2 = JsonArrToCoordinates("pos2", object);
            data.stageSaved = object.get("stageSaved").toString().equals("true");
            data.enabled = object.get("enabled").toString().equals("true");
            data.lobby = JsonArrToCoordinates("lobby", object);
            data.spectatorLoc = JsonArrToCoordinates("spectatorLoc", object);
            data.startPlatform1 = JsonArrToCoordinates("startPlatform1", object);
            data.startPlatform2 = JsonArrToCoordinates("startPlatform2", object);
            data.exitLoc = JsonArrToCoordinates("exitLoc", object);
            MikeyMcPlus.data.gameData.put(data.name, data);
            player.sendMessage(ChatColor.GOLD + "Loaded " + data.name);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static BlockVector3 JsonArrToCoordinates(String attrib, JSONObject obj) {
        JSONArray arr = (JSONArray) obj.get(attrib);
        return BlockVector3.at(Integer.parseInt(arr.get(0).toString()), Integer.parseInt(arr.get(1).toString()), Integer.parseInt(arr.get(2).toString()));
    }

    public static JSONArray CoordinatesToJsonArr(BlockVector3 b) {
        JSONArray arr = new JSONArray();
        arr.addAll(Arrays.asList(b.getX(), b.getY(), b.getZ()));
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
