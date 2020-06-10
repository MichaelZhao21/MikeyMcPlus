package xyz.michaelzhao.mikeymcplus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class CommandGayStick implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            int lvl;
            if (args.length == 0) lvl = 10;
            else if (args.length > 1) {
                player.sendMessage(ChatColor.RED + "Why do you have so many arguments >:(");
                return false;
            }
            else {
                try {
                    lvl = Integer.parseInt(args[0]);
                }
                catch (NumberFormatException n) {
                    player.sendMessage(ChatColor.RED + "Invalid argument for " + ChatColor.GOLD + "<Knockback Level>");
                    return false;
                }
            }

            ItemStack stick = new ItemStack(Material.STICK);
            ItemMeta meta = stick.getItemMeta();
            meta.setDisplayName("Gay Stick");
            meta.setLore(Collections.singletonList("Mikey's stick hehe"));
            meta.addEnchant(Enchantment.KNOCKBACK, lvl, true);
            stick.setItemMeta(meta);
            stick.setAmount(1);

            player.getInventory().addItem(stick);
        }
        return true;
    }
}
