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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.michaelzhao.mikeymcplus.GameData;
import xyz.michaelzhao.mikeymcplus.MikeyMcPlus;

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
        GameSetup.saveGame();
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
        player.sendMessage(ChatColor.AQUA + "Minigame tool: Left click to select pos1 and right click to selecct pos2");
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

    public static void saveStage(Player player) {
        GameData gameData = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(gameData.currWorld), gameData.pos1, gameData.pos2);
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
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(currGame.currWorld), -1);
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

    public static void saveAllGames() {
        JSONArray out = new JSONArray();
        for (String str : MikeyMcPlus.data.gameData.keySet()) {
            out.add(str);
            MikeyMcPlus.data.currGame = str;
            saveGame();
        }
        try {
            FileWriter fw = new FileWriter(MikeyMcPlus.getInstance().getDataFolder().getPath() + System.getProperty("file.separator") + "games.json");
            fw.write(out.toJSONString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveGame() {
        GameData data = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
        JSONObject out = new JSONObject();
        JSONArray pos1Arr = new JSONArray();
        pos1Arr.addAll(Arrays.asList(data.pos1.getX(), data.pos1.getY(), data.pos1.getZ()));
        JSONArray pos2Arr = new JSONArray();
        pos2Arr.addAll(Arrays.asList(data.pos2.getX(), data.pos2.getY(), data.pos2.getZ()));
        out.put("name", data.name);
        out.put("pos1", pos1Arr);
        out.put("pos2", pos2Arr);
        out.put("stageSaved", data.stageSaved);
        try {
            FileWriter fw = new FileWriter(MikeyMcPlus.data.gamesFolder.getPath() + System.getProperty("file.separator") + MikeyMcPlus.data.currGame + ".json");
            fw.write(out.toJSONString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAllGames(Player player) {
        String input = readAllLines(MikeyMcPlus.getInstance().getDataFolder().getPath() + System.getProperty("file.separator") + "games.json");
        if (input == null) return;
        JSONParser parser = new JSONParser();
        JSONArray arr;
        Bukkit.getServer().broadcastMessage(input);
        try {
            arr = (JSONArray) parser.parse(input);
            for (Object o : arr.toArray()) {
                MikeyMcPlus.data.currGame = (String) o;
                loadGame(player);
            }
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
            data.stageSaved = object.get("stageSaved").toString().equals("true");
            JSONArray pos1Arr = (JSONArray) object.get("pos1");
            JSONArray pos2Arr = (JSONArray) object.get("pos2");
            data.pos1 = BlockVector3.at(Integer.parseInt(pos1Arr.get(0).toString()), Integer.parseInt(pos1Arr.get(1).toString()), Integer.parseInt(pos1Arr.get(2).toString()));
            data.pos2 = BlockVector3.at(Integer.parseInt(pos2Arr.get(0).toString()), Integer.parseInt(pos2Arr.get(1).toString()), Integer.parseInt(pos2Arr.get(2).toString()));
            MikeyMcPlus.data.gameData.put(data.name, data);
            Bukkit.getServer().broadcastMessage(object.toJSONString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
