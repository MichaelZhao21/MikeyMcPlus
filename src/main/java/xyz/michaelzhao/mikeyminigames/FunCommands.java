package xyz.michaelzhao.mikeyminigames;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class FunCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (args.length == 0) return false;

            switch(args[0]) {
                case "gaystick":
                    giveStick(player, args);
                    break;
                case "firework":
                    if (args.length == 2)
                        try {
                            final int num = Integer.parseInt(args[1]);
                            for (int i = 0; i < num; i++)
                                spawnRandomFirework(player);
                        } catch(NumberFormatException e) {
                            e.printStackTrace();
                        }
                    else spawnRandomFirework(player);
            }
        }
        return true;
    }

    public static void giveStick(Player player, String[] args) {
        int lvl;
        if (args.length == 1) lvl = 10;
        else if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Why do you have so many arguments >:(");
            player.sendMessage(ChatColor.RED + "Usage: /fun gaystick <level>");
            return;
        }
        else {
            try {
                lvl = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException n) {
                player.sendMessage(ChatColor.RED + "Invalid argument for " + ChatColor.GOLD + "<Knockback Level>");
                return;
            }
        }

        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName("Gay Stick");
        meta.setLore(Collections.singletonList("Mikey's stick hehe"));
        meta.addEnchant(Enchantment.KNOCKBACK, lvl, true);
        stick.setItemMeta(meta);
        stick.setAmount(1);

        player.getInventory().addItem(stick);
    }

    public static void spawnRandomFirework(Player player) {
        Location loc = player.getLocation();
        World wld = loc.getWorld();
        if (wld == null) return;
        Firework fw = (Firework) wld.spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower((int) (Math.random() * 10 + 1));
        fwm.addEffect(FireworkEffect.builder()
                .withColor(Color.fromRGB(((int) (Math.random() * 255 + 1)),
                        ((int) (Math.random() * 255 + 1)),
                        ((int) (Math.random() * 255 + 1))))
                .flicker(true)
                .build());

        fw.setFireworkMeta(fwm);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MikeyMinigames.instance, fw::detonate, 20);
    }
}