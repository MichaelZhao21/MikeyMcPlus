package xyz.michaelzhao.mikeymcplus;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class CommandFirework implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            spawnRandomFirework(player.getLocation());
        }
        return true;
    }

    public static void spawnRandomFirework(Location loc) {
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
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MikeyMcPlus.getInstance(), fw::detonate, 20);
    }
}
