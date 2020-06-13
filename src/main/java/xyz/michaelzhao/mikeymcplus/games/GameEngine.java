package xyz.michaelzhao.mikeymcplus.games;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.michaelzhao.mikeymcplus.MikeyMcPlus;

import java.util.Collections;

public class GameEngine { // TODO add change game type
    public static void enableGame(Player player, String[] args) {
        String em = ChatColor.RED + "Usage: /games enable <GameName> [true | false]"; //TODO Check if this is correct
        if (args.length != 3)
            player.sendMessage(em);
        else if (!args[2].equals("true") && !args[2].equals("false"))
            player.sendMessage(em);
        else if (!MikeyMcPlus.data.gameData.containsKey(args[1]))
            player.sendMessage(ChatColor.RED + "Game " + args[2] + " could not be found!");
        else {
            boolean en = args[2].equals("true");
            DeathmatchData curr = (DeathmatchData) MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.toolGame); // TODO: Fix casting
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
                    MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.toolGame).enabled = true;
                    player.sendMessage(ChatColor.GOLD + MikeyMcPlus.data.toolGame + " enabled!");
                }
            }
            else {
                MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.toolGame).enabled = false;
                player.sendMessage(ChatColor.GOLD + MikeyMcPlus.data.toolGame + " disabled");
            }
        }
    }

    public static void info(Player player) {
        // TODO: process argument omg
        DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.toolGame); // TODO: Fix casting
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

    public static String coordsToString(String label, Location v) {
        return String.format("%s: (%d, %d, %d) | facing<%s>", label, (int) v.getX(), (int) v.getY(), (int) v.getZ(), v.getDirection().toString());
    }

    public static void kit(Player player, String[] args) { // TODO: fix :((
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
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
            DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(args[1]); // TODO: Fix casting
            if (!data.enabled) {
                player.sendMessage(ChatColor.RED + "Game not enabled!");
                return;
            }
            if (data.gameState != GameState.LOBBY && data.gameState != GameState.STOPPED) {
                player.sendMessage(ChatColor.RED + "Game is currently playing");
            }
            data.gamePlayers.put(player.getName(), player);
            data.gamePlayerObjects.put(player.getName(), new PlayerGameData(
                    player.getInventory().getContents(),
                    player.getActivePotionEffects(),
                    player.getGameMode()));
            MikeyMcPlus.data.playersInGameList.put(player, data.name);
            player.getInventory().clear();
            giveKit("spleef", player);
            player.setHealth(20.0);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(data.lobby);
            player.sendMessage(ChatColor.AQUA + "Joined " + ChatColor.GOLD + args[1]);
            if (data.gameState == GameState.STOPPED)
                startLobby(data);
        }
    }

    public static void startLobby(DeathmatchData data) { // TODO: Fix casting
        data.timerId = createTimer(data.name, true, 30, "start", null);
        data.gameState = GameState.LOBBY;
    }

    public static void startCall(Player player, String[] args) {
        DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(args[1]); // TODO: Fix casting
        if (args.length != 2)
            player.sendMessage(ChatColor.RED + "Usage: /games start <GameName>");
        else if (!MikeyMcPlus.data.gameData.containsKey(args[1]))
            player.sendMessage(ChatColor.RED + args[1] + " game not found");
        else if (data.gameState != GameState.LOBBY) // TODO: Fix casting
            player.sendMessage(ChatColor.RED + args[1] + " has no players in the lobby!");
        else {
            MikeyMcPlus.instance.getServer().getScheduler().cancelTask(data.timerId); // TODO: Fix casting
            start(args[1]);
        }
    }

    public static void start(String gameName) {
        DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(gameName); // TODO: Fix casting
        data.gameState = GameState.RUNNING;
        data.timerId = createTimer(gameName, false, 0, null, "endgame");
        data.playersAlive = data.gamePlayers.size();
        for (Player player : data.gamePlayers.values()) {
            data.gamePlayerObjects.get(player.getName()).state = PlayerState.GAME;
            player.setGameMode(GameMode.SURVIVAL);
            player.setLevel(0);
            player.teleport(randomSpawn(data.startPlatform1, data.startPlatform2));
        }
    }

    public static Location randomSpawn(BlockVector3 start, BlockVector3 end) { //TODO: Add facing center
        double x = Math.random() * (end.getX() - start.getX()) + start.getX();
        double z = Math.random() * (end.getZ() - start.getZ()) + start.getZ();
        return new Location(MikeyMcPlus.data.currWorld, x, start.getY(), z);
    }

    public static void quit(Player player) { // TODO: Add check to see if no players left
        if (!MikeyMcPlus.data.playersInGameList.containsKey(player)) {
            player.sendMessage(ChatColor.RED + "Not in a game");
        }
        else {
            player.sendMessage(ChatColor.AQUA + "Left game " + ChatColor.GOLD + MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.playersInGameList.get(player)).name);
            removeFromGame(player);
        }
    }

    public static void removeFromGame(Player player) {
        DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(MikeyMcPlus.data.playersInGameList.get(player)); // TODO: Fix casting
        PlayerGameData pDat = data.gamePlayerObjects.get(player.getName());
        ItemStack[] items = pDat.oldInventory;
        MikeyMcPlus.data.playersInGameList.remove(player);
        data.gamePlayers.remove(player.getName());
        data.gamePlayerObjects.remove(player.getName());

        player.getInventory().clear();
        for (PotionEffect e : player.getActivePotionEffects())
            player.removePotionEffect(e.getType());
        for (PotionEffect p : pDat.oldPotionEffects)
            player.addPotionEffect(p);
        player.setGameMode(pDat.oldMode);
        player.getInventory().setContents(items);
        player.setVelocity(new Vector());
        player.teleport(data.exitLoc);
    }

    public static void checkForEndGame(DeathmatchData data) { // TODO: Fix casting
        if (data.playersAlive == 1) {
            Player winner = getWinner(data);
            String winName = winner.getName();
            MikeyMcPlus.instance.getServer().getScheduler().cancelTask(data.timerId);
            String[] pm = data.gamePlayers.keySet().toArray(new String[0]);
            for (String p : pm) {
                Bukkit.broadcastMessage(p);
                Player player = data.gamePlayers.get(p);
                removeFromGame(player);
                player.sendTitle(String.format("%s won %s!", ChatColor.GOLD + winName, ChatColor.AQUA + data.name), "", 10, 60, 20);
            }
            MikeyMcPlus.data.toolGame = data.name;
            GameSetup.loadStage(); // TODO: Remove the currGame thing
            data.timerCount = 0;
            data.gameState = GameState.STOPPED;
        }
    }

    public static Player getWinner(DeathmatchData data) { // TODO: Fix casting
        for (Player p : data.gamePlayers.values())
            if (data.gamePlayerObjects.get(p.getName()).state == PlayerState.GAME)
                return p;
        return null;
    }

    public static void playerDeath(Player player, String gameName) {
        DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(gameName); // TODO: Fix casting
        if (data.gameState == GameState.RUNNING && data.gamePlayerObjects.get(player.getName()).state == PlayerState.GAME) {
            player.sendTitle("You died!", "You lasted " + data.timerCount + " seconds", 10, 60, 20);
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.teleport(data.spectatorLoc);
            data.gamePlayerObjects.get(player.getName()).state = PlayerState.SPECTATOR;
            data.playersAlive--;
            checkForEndGame(data);
            for (Player p : data.gamePlayers.values())
                p.sendMessage(ChatColor.AQUA + player.getName() + " died! " + ChatColor.LIGHT_PURPLE + data.playersAlive + " players remaining.");
        }
    }

    public static int createTimer(String game, boolean countdown, int seconds, String callback, String fun) {
        DeathmatchData data = (DeathmatchData) MikeyMcPlus.data.gameData.get(game); // TODO: Fix casting
        data.timerCount = (countdown ? seconds : 0);
        for (Player p : data.gamePlayers.values()) {
            p.setLevel(data.timerCount);
        }
        return MikeyMcPlus.instance.getServer().getScheduler().scheduleSyncRepeatingTask(MikeyMcPlus.instance, (() -> runTimer(data, countdown, callback, fun)), 20L, 20L);
    }

    public static void runTimer(DeathmatchData data, boolean countdown, String callback, String fun) { // TODO: Fix casting
        if (countdown) {
            data.timerCount--;
            if (data.timerCount == 0) {
                timerEnd(callback, data);
            }
        }
        else {
            data.timerCount++;
        }
        if (fun != null)
            timerFun(fun, data);
        for (Player p : data.gamePlayers.values()) {
            p.setLevel(data.timerCount);
        }
    }

    public static void timerFun(String fun, DeathmatchData data) { // TODO: Fix casting
        switch (fun) {
            case "endgame":
                checkForEndGame(data);
                break;
        }
    }

    public static void timerEnd(String callback, DeathmatchData data) { // TODO: Fix casting
        MikeyMcPlus.instance.getServer().getScheduler().cancelTask(data.timerId);
        switch (callback) {
            case "start":
                start(data.name);
                break;
        }
    }
}
