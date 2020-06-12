package xyz.michaelzhao.mikeymcplus;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

enum PlayerState {LOBBY, GAME, SPECTATOR}

public class PlayerGameData {
    public ItemStack[] oldInventory;
    public Collection<PotionEffect> oldPotionEffects;
    public GameMode oldMode;
    public PlayerState state;

    public PlayerGameData(ItemStack[] oldInventory, Collection<PotionEffect> potionEffects, GameMode oldMode) {
        this.oldInventory = oldInventory;
        this.oldPotionEffects = potionEffects;
        this.oldMode = oldMode;
        this.state = PlayerState.LOBBY;
    }

}
