package xyz.michaelzhao.mikeyminigames;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import xyz.michaelzhao.mikeyminigames.games.GameData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Util {
    /**
     * Returns the subfile path given a directory and filename
     *
     * @param parent  the parent directory
     * @param subName the file name
     * @return String representing the file path
     */
    public static String getSubPath(File parent, String subName) {
        return parent.getPath() + System.getProperty("file.separator") + subName.replace(' ', '_');
    }

    /**
     * Opens the game file
     *
     * @param parentFolder the parent directory of the file
     * @param gameName     the name of the game
     * @return the file object
     */
    public static File getFileInDir(File parentFolder, String gameName) {
        // Creates a file object, replacing spaces with underscores in the game name
        File gameFile = new File(getSubPath(parentFolder, gameName));

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
     * Parse all lines of a file and compile it into a string
     * for use by a JSONParser object
     *
     * @param path the path of the file
     * @return String of concatenated file text
     */
    public static String readAllLines(File path) {
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

    /**
     * Convert JSONArray to BlockVector3
     *
     * @param attrib name of attribute to get
     * @param obj    JSONArray object
     * @return BlockVector3 object
     */
    public static BlockVector3 jsonArrToBlockVector3(String attrib, JSONObject obj) {
        JSONArray arr = (JSONArray) obj.get(attrib);
        return BlockVector3.at(Integer.parseInt(arr.get(0).toString()), Integer.parseInt(arr.get(1).toString()), Integer.parseInt(arr.get(2).toString()));
    }

    /**
     * Convert BlockVector3 to JSONArray
     *
     * @param b BlockVector3 object
     * @return JSONArray object
     */
    public static JSONArray blockVector3ToJsonArr(BlockVector3 b) {
        JSONArray arr = new JSONArray();
        arr.addAll(Arrays.asList(b.getX(), b.getY(), b.getZ()));
        return arr;
    }

    /**
     * Convert JSONArray to Location
     *
     * @param attrib name of attribute to get
     * @param obj    JSONArray object
     * @return Location object
     */
    public static Location jsonArrToLocation(String attrib, JSONObject obj) {
        JSONArray arr = (JSONArray) obj.get(attrib);
        Location l = new Location(MikeyMinigames.data.currWorld, Integer.parseInt(arr.get(0).toString()), Integer.parseInt(arr.get(1).toString()), Integer.parseInt(arr.get(2).toString()));
        l.setDirection(new Vector(Integer.parseInt(arr.get(3).toString()), Integer.parseInt(arr.get(4).toString()), Integer.parseInt(arr.get(5).toString())));
        return l;
    }

    /**
     * Convert Location to JSONArray
     *
     * @param l Location object
     * @return JSONArray object
     */
    public static JSONArray locationToJsonArr(Location l) {
        JSONArray arr = new JSONArray();
        arr.addAll(Arrays.asList((int) l.getX(), (int) l.getY(), (int) l.getZ(), (int) l.getDirection().getX(), (int) l.getDirection().getY(), (int) l.getDirection().getZ()));
        return arr;
    }

    /**
     * Checks the length of the arguments and prints the message to the player if incorrect
     *
     * @param args          command arguments
     * @param correctLength correct length of arguments
     * @param usage         usage message to send player
     * @param sender        the sender that issued the command
     * @return if the argument was incorrect or not
     */
    public static boolean isArgsIncorrectLength(String[] args, int correctLength, String usage, CommandSender sender) {
        if (args.length != correctLength) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + usage);
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the game is invalid and prints the message to the player if invalid
     *
     * @param gameName name of the game
     * @param sender   the sender that issued the command
     * @return if the game was invalid or not
     */
    public static boolean isInvalidGame(String gameName, CommandSender sender) {
        if (!MikeyMinigames.data.gameData.containsKey(gameName.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "Game " + gameName + " could not be found!");
            return true;
        }
        return false;
    }

    /**
     * Changes a Location object to a formatted string
     *
     * @param label the label to put in front of the coordinate string
     * @param v     the Location object
     * @return the formatted string
     */
    public static String coordsToString(String label, Location v) {
        return String.format("%s: (%d, %d, %d) | facing <%s>", label, (int) v.getX(), (int) v.getY(), (int) v.getZ(), v.getDirection().toString());
    }

    /**
     * Tests if the location is in default still (0, 0, 0)
     *
     * @param l Location object
     * @return if location is not set
     */
    public static boolean isLocationNotSet(Location l) {
        return l.getX() == 0 && l.getY() == 0 && l.getZ() == 0;
    }

    // TODO: Add javadoc
    public static GameData getData(String gameName) {
        return MikeyMinigames.data.gameData.get(gameName.toLowerCase());
    }
}
