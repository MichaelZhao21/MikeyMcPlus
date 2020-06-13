package xyz.michaelzhao.mikeymcplus.games;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import xyz.michaelzhao.mikeymcplus.MikeyMcPlus;

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
                item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Minigame Tool") &&
                (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_BLOCK))) {
            Block block = event.getClickedBlock();
            if (block == null) return;
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            DeathmatchData toolData = (DeathmatchData) MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.toolGame); // TODO: Fix casting
            if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                event.setCancelled(true);
                player.sendMessage(String.format("%sPos1 set to: (%d, %d, %d)",ChatColor.LIGHT_PURPLE, x, y, z));
                toolData.pos1 = BlockVector3.at(x, y, z);
            }
            else {
                player.sendMessage(String.format("%sPos2 set to: (%d, %d, %d)",ChatColor.LIGHT_PURPLE, x, y, z));
                toolData.pos2 = BlockVector3.at(x, y, z);
            }
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
        if (e.getTo() != null && e.getTo().getY() < 0 && MikeyMcPlus.data.playersInGameList.containsKey(e.getPlayer()))
            GameEngine.playerDeath(e.getPlayer(), MikeyMcPlus.data.playersInGameList.get(e.getPlayer()));
    }
}
