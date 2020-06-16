package xyz.michaelzhao.mikeyminigames.games;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import xyz.michaelzhao.mikeyminigames.MikeyMinigames;

public class GameListener implements Listener {
    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        // Right or left click on the minigame tool
        if (item != null &&
                item.getType() == Material.BLAZE_ROD &&
                item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Minigame Tool")) {

            // Sneak click to open inventory
            if (player.getPose() == Pose.SNEAKING && (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_AIR))) {
                // Create tool inventory class with game name
                MikeyMinigames.data.toolInventory = new ToolInventory(item.getItemMeta().getLore().get(0));
                player.openInventory(MikeyMinigames.data.toolInventory.inventory);

                // Register event listener
                MikeyMinigames.instance.getServer().getPluginManager().registerEvents(MikeyMinigames.data.toolInventory, MikeyMinigames.instance);
                return;
            }

            // TODO: write else desc

            // Get tool mode
            ToolMode mode = MikeyMinigames.data.toolInventory.toolMode;

            // Run corners or position setting
            if (mode == ToolMode.ARENA || mode == ToolMode.SPAWN_PLATFORM)
                GameSetup.setCorners(player, action, event);
            else
                GameSetup.setPos(MikeyMinigames.data.toolInventory.toolMode, action, player);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.DIAMOND_PICKAXE &&
                item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "S P O O N")) {
            e.setDropItems(false);
        }
    }

    @EventHandler
    public void onPlayerFallEvent(PlayerMoveEvent e) {
        if (e.getTo() != null && e.getTo().getY() < 0 &&
                MikeyMinigames.data.playersInGameList.containsKey(e.getPlayer()) &&
                MikeyMinigames.data.gameData.get(MikeyMinigames.data.playersInGameList.get(e.getPlayer())).deathType == DeathType.FALLING)
            GameEngine.playerDeath(e.getPlayer(), MikeyMinigames.data.playersInGameList.get(e.getPlayer()));
    }
}
