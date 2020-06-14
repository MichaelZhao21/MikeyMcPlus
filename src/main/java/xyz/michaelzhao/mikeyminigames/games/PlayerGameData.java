package xyz.michaelzhao.mikeyminigames.games;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

enum PlayerState {LOBBY, GAME, SPECTATOR}

public class PlayerGameData {
    public ItemStack[] oldInventory;
    public Collection<PotionEffect> oldPotionEffects;
    public GameMode oldMode;
    public int oldExpLvl;
    public PlayerState state;

    public PlayerGameData(Player player) {
        this.oldInventory = player.getInventory().getContents();
        this.oldPotionEffects = player.getActivePotionEffects();
        this.oldMode = player.getGameMode();
        this.oldExpLvl = player.getTotalExperience();
        this.state = PlayerState.LOBBY;
    }

}
