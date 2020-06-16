package xyz.michaelzhao.mikeyminigames.games;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.michaelzhao.mikeyminigames.MikeyMinigames;
import xyz.michaelzhao.mikeyminigames.Util;

enum ToolMode {NONE, LOBBY, ARENA, EXIT, SPECTATOR, SPAWN_PLATFORM, CHECKPOINTS}

public class ToolInventory implements Listener {

    public Inventory inventory;
    public ToolMode toolMode;
    public String toolGame;

    public ToolInventory(String gameName) {
        this.toolGame = gameName;
        this.toolMode = ToolMode.NONE;
        this.inventory = Bukkit.createInventory(null, 18, this.toolGame);
        createInventory();
    }

    public void createInventory() {
        GameData data = Util.getData(toolGame);
        ItemStack[] newInv = new ItemStack[18];

        // Add toggles
        newInv[0] = Util.createInventoryItem(Material.PAPER, 1,
                "Game settings");
        newInv[1] = Util.createInventoryItem(Material.QUARTZ, 1,
                ChatColor.BLUE + "Click to toggle arena", "false");
        newInv[2] = Util.createInventoryItem(Material.RED_DYE, 1,
                ChatColor.LIGHT_PURPLE + "Click to toggle lobby", "false");
        newInv[3] = Util.createInventoryItem(Material.IRON_INGOT, 1,
                ChatColor.GREEN + "Click to toggle spectators", "false");
        newInv[4] = Util.createInventoryItem(Material.SAND, 1,
                "Click to toggle spawn platform", "false");
        newInv[5] = Util.createInventoryItem(Material.GOLD_INGOT, 1,
                "Click to toggle checkpoints", "false");

        // Add selections
        newInv[9] = Util.createInventoryItem(Material.PAPER, 1,
                "Tool modes");
        newInv[15] = Util.createInventoryItem(Material.BARRIER, 1,
                ChatColor.RED + "Exit location",
                "Right click to set the exit location");
        inventory.setContents(newInv);
    }

    public void editInventory(Material material) {
        ItemStack[] items = inventory.getContents();
        switch (material) {
            case RED_DYE:
                items[11] = Util.createInventoryItem(Material.RED_BED, 1,
                        ChatColor.LIGHT_PURPLE + "Lobby position",
                        "Right click to set lobby position");
                Util.getData(toolGame).hasLobby = true;
                break;
            case QUARTZ:
                items[10] = Util.createInventoryItem(Material.QUARTZ_BLOCK, 1,
                        ChatColor.BLUE + "Game arena corners",
                        "Left and right click to select the corners of the game arena");
                Util.getData(toolGame).hasArena = true;
                break;
            case IRON_INGOT:
                items[12] = Util.createInventoryItem(Material.DROPPER, 1,
                        ChatColor.GREEN + "Spectator spawn location",
                        "Right click to set the spectator spawn location");
                Util.getData(toolGame).hasSpectators = true;
                break;
            case SAND:
                items[13] = Util.createInventoryItem(Material.BRICK, 1,
                        ChatColor.DARK_AQUA + "Starting platform location",
                        "Left and right click to select the corners of the starting platform");
                Util.getData(toolGame).hasSpawnPlatform = true;
                break;
            case GOLD_INGOT:
                items[14] = Util.createInventoryItem(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1,
                        ChatColor.DARK_PURPLE + "Set checkpoints",
                        "Left click on light weighted pressure plates to set checkpoints");
                Util.getData(toolGame).hasCheckpoints = true;
                break;
        }
        inventory.setContents(items);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // Check to see if this inventory was clicked
        if (e.getInventory() == MikeyMinigames.data.toolInventory.inventory) {

            // Check if player clicked on smth
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem == null) return;

            // Don't let player pick up block and close the player's inventory
            e.setCancelled(true);
            
            boolean a = false;

            // Switch based on the item clicked
            switch (clickedItem.getType()) {
                case RED_BED:
                    e.getWhoClicked().sendMessage(ChatColor.AQUA + "Click to set the lobby position");
                    toolMode = ToolMode.LOBBY;
                    a = true;
                    break;
                case QUARTZ_BLOCK:
                    e.getWhoClicked().sendMessage(ChatColor.AQUA + "Left and Right click to set the corners of the game arena");
                    toolMode = ToolMode.ARENA;
                    a = true;
                    break;
                case BARRIER:
                    e.getWhoClicked().sendMessage(ChatColor.AQUA + "Click to set the exit position");
                    toolMode = ToolMode.EXIT;
                    a = true;
                    break;
                case DROPPER:
                    e.getWhoClicked().sendMessage(ChatColor.AQUA + "Click to set the spectator spawn location");
                    toolMode = ToolMode.SPECTATOR;
                    a = true;
                    break;
                case BRICK:
                    e.getWhoClicked().sendMessage(ChatColor.AQUA + "Left and Right click to set the corners of the game spawn platform");
                    toolMode = ToolMode.SPAWN_PLATFORM;
                    a = true;
                    break;
                case LIGHT_WEIGHTED_PRESSURE_PLATE:
                    e.getWhoClicked().sendMessage(ChatColor.AQUA + "Left click on light weighted pressure plates (gold) to select checkpoints");
                    toolMode = ToolMode.CHECKPOINTS;
                    a = true;
                    break;
                case QUARTZ:
                case RED_DYE:
                case IRON_INGOT:
                case SAND:
                case GOLD_INGOT:
                    editInventory(clickedItem.getType());
                    break;
                default:
                    break;
            }
            if (a) {
                e.getWhoClicked().closeInventory();
                HandlerList.unregisterAll(MikeyMinigames.data.toolInventory);
            }
        }
    }

    public static String toolModeToString(ToolMode toolMode) {
        switch (toolMode) {
            case NONE:
                return "ERROR";
            case LOBBY:
                return "Lobby position";
            case ARENA:
                return "Arena corners";
            case EXIT:
                return "Exit location";
            case SPECTATOR:
                return "Spectator location";
            case SPAWN_PLATFORM:
                return "Spawn platform corners";
        }
        return null;
    }
}
