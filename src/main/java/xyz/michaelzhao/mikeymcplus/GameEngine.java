package xyz.michaelzhao.mikeymcplus;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class GameEngine {
    public static void enableGame(Player player, String[] args) {
        String em = ChatColor.RED + "Usage: /games enable <GameName> [true | false]";
        if (args.length != 3)
            player.sendMessage(em);
        else if (!args[2].equals("true") && !args[2].equals("false"))
            player.sendMessage(em);
        else if (!MikeyMcPlus.data.gameData.containsKey(args[1]))
            player.sendMessage(ChatColor.RED + "Game " + args[2] + " could not be found!");
        else {
            boolean en = args[2].equals("true");
            GameData curr = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
            if (en) {
                boolean oop = false;
                BlockVector3 notSet = BlockVector3.at(0, 0, 0);
                if (!curr.stageSaved) {
                    oop = true;
                    player.sendMessage(ChatColor.RED + "Stage not saved");
                }
                if (curr.lobby.equals(notSet)) {
                    oop = true;
                    player.sendMessage(ChatColor.RED + "Lobby not set");
                }
                if (curr.startPlatform1.equals(notSet) || curr.startPlatform2.equals(notSet)) {
                    oop = true;
                    player.sendMessage(ChatColor.RED + "Starting platform (2 corners) not set");
                }
                if (curr.spectatorLoc.equals(notSet)) {
                    oop = true;
                    player.sendMessage(ChatColor.RED + "Spectator location not set");
                }
                if (curr.exitLoc.equals(notSet)) {
                    oop = true;
                    player.sendMessage(ChatColor.RED + "Exit location not set");
                }
                if (!oop) {
                    MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame).enabled = true;
                    player.sendMessage(ChatColor.GOLD + MikeyMcPlus.data.currGame + " enabled!");
                }
            }
            else {
                MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame).enabled = false;
                player.sendMessage(ChatColor.GOLD + MikeyMcPlus.data.currGame + " disabled");
            }
        }
    }

    public static void info(Player player) {
        GameData data = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.currGame);
        player.sendMessage("-----------------------------------");
        player.sendMessage(ChatColor.GOLD + "Name: " + data.name);
        player.sendMessage("Enabled: " + data.enabled);
        player.sendMessage("Stage Saved: " + data.stageSaved);
        player.sendMessage(String.format("Stage area: (%d, %d, %d) to (%d, %d, %d)", data.pos1.getX(), data.pos1.getY(), data.pos1.getZ(), data.pos2.getX(), data.pos2.getY(), data.pos2.getZ()));
        player.sendMessage(coordsToString("Lobby", data.lobby));
        player.sendMessage(coordsToString("Exit Location", data.exitLoc));
        player.sendMessage(coordsToString("Spectator Location", data.spectatorLoc));
        player.sendMessage(String.format("Spawning area: (%d, %d, %d) to (%d, %d, %d)", data.startPlatform1.getX(), data.startPlatform1.getY(), data.startPlatform1.getZ(), data.startPlatform2.getX(), data.startPlatform2.getY(), data.startPlatform2.getZ()));
        player.sendMessage("-----------------------------------");
    }

    public static String coordsToString(String label, BlockVector3 v) {
        return String.format("%s: (%d, %d, %d)", label, v.getX(), v.getY(), v.getZ());
    }

    public static void kit(Player player, String[] args) {
        String em = ChatColor.RED + "Usage: /games kit <spleef |>";
        if (args.length != 2 || !args[1].equals("spleef")) {
            player.sendMessage(em);
            return;
        }
        giveKit(args[1], player);
    }

    public static void giveKit(String type, Player player) {
        if (type.equals("spleef")) {
            ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta meta = pick.getItemMeta();
            if (meta == null) return;
            meta.setDisplayName(ChatColor.GREEN + "S P O O N");
            meta.setLore(Collections.singletonList("digdigdig"));
            meta.addEnchant(Enchantment.DIG_SPEED, 100, true);
            meta.addEnchant(Enchantment.DURABILITY, 100, true);
            meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
            pick.setItemMeta(meta);
            pick.setAmount(1);
            player.getInventory().addItem(pick);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 10, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1000000, 10, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 10, false, false));
        }
    }

    public static void join(Player player, String[] args) {
        String fm = ChatColor.RED + "Usage: /games join <GameName>";
        if (args.length != 2) {
            player.sendMessage(fm);
        }
        else if (!MikeyMcPlus.data.gameData.containsKey(args[1])) {
            player.sendMessage(ChatColor.RED + args[1] + " game not found");
        }
        else if (MikeyMcPlus.data.playersInGameList.containsKey(player)) {
            player.sendMessage(ChatColor.RED + "Already in game " + MikeyMcPlus.data.playersInGameList.get(player));
        }
        else {
            GameData data = MikeyMcPlus.data.gameData.get(args[1]);
            if (!data.enabled) {
                player.sendMessage(ChatColor.RED + "Game not enabled!");
                return;
            }
            data.playersInGame.put(player, new PlayerInGame(
                    player.getInventory().getContents(),
                    player.getActivePotionEffects(),
                    player.getGameMode()));
            MikeyMcPlus.data.playersInGameList.put(player, data.name);

            player.getInventory().clear();
            giveKit("spleef", player);
            player.teleport(blockVectorToLocation(data.startPlatform1, player));
            player.sendMessage(ChatColor.AQUA + "Joined " + ChatColor.GOLD + args[1]);
        }

    }

    public static void quit(Player player) {
        if (!MikeyMcPlus.data.playersInGameList.containsKey(player)) {
            player.sendMessage(ChatColor.RED + "Not in a game");
        }
        else {
            GameData data = MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.playersInGameList.get(player));
            PlayerInGame pDat = data.playersInGame.get(player);
            ItemStack[] items = pDat.oldInventory;
            MikeyMcPlus.data.playersInGameList.remove(player);
            data.playersInGame.remove(player);

            player.getInventory().clear();
            for (PotionEffect e : player.getActivePotionEffects())
                player.removePotionEffect(e.getType());
            for (PotionEffect p : pDat.oldPotionEffects)
                player.addPotionEffect(p);
            player.setGameMode(pDat.oldMode);
            player.getInventory().setContents(items);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(blockVectorToLocation(data.lobby, player));
            player.sendMessage(ChatColor.AQUA + "Left game " + ChatColor.GOLD + data.name);
        }
    }

    public static Location blockVectorToLocation(BlockVector3 v, Player p) {
        return new Location(MikeyMcPlus.data.currWorld, v.getX(), v.getY(), v.getZ());
    }

    public static int createTimer(String game, boolean countdown, int seconds) {
        return MikeyMcPlus.instance.getServer().getScheduler().scheduleSyncRepeatingTask(MikeyMcPlus.instance, new Runnable() {
            @Override
            public void run() {
                MikeyMcPlus.data.gameData.get(game);
            }
        }, 0L, 20L);
    }
}
