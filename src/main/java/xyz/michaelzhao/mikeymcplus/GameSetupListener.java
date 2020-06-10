package xyz.michaelzhao.mikeymcplus;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GameSetupListener implements Listener {
    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent event) {
        Player player = event.getPlayer(); //TODO: Add multiple players
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
            double x = block.getX();
            double y = block.getY();
            double z = block.getZ();
            GameData toolData = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
            if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                event.setCancelled(true);
                player.sendMessage(String.format("%sPos1 set to: (%f, %f, %f)",ChatColor.LIGHT_PURPLE, x, y, z));
                player.sendMessage(BlockVector3.at(x, y, z).toString());
                player.sendMessage(toolData.pos1.toString());
                toolData.pos1 = BlockVector3.at(x, y, z);
            }
            else {
                player.sendMessage(String.format("%sPos2 set to: (%f, %f, %f)",ChatColor.LIGHT_PURPLE, x, y, z));
                toolData.pos2 = BlockVector3.at(x, y, z);
            }
        }
    }
}
