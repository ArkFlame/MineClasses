package com.arkflame.classes.classes;

import java.util.EnumMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import com.arkflame.classes.MineClasses;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.plugin.ClassesEffect;

public abstract class EquipableMagicClass extends EquipableClass {
    protected final Map<Material, ClassesEffect> activeEffects = new EnumMap<>(Material.class);
    protected boolean usesEnergy = false; // Use energy for the effect
    protected boolean applyNearby = false; // Apply effect to nearby players

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null)
            return;
        ClassesEffect effect = activeEffects.get(item.getType());
        if (effect == null)
            return;

        ClassPlayer cp = MineClasses.getClassPlayerManager().get(player);
        float cooldown = cp.getCooldown() / 1000f;
        if (cooldown > 0) {
            player.sendMessage(ChatColor.RED + "Espera " + cooldown + "s antes de usar otro efecto.");
            return;
        }

        // Check if energy should be used
        if (usesEnergy && cp.getEnergy() < effect.getEnergy()) {
            player.sendMessage(ChatColor.RED + "No tienes suficiente energia! Te falta "
                    + (effect.getEnergy() - cp.getEnergy()) + " energia.");
            return;
        }

        if (usesEnergy) {
            cp.setLastSpellTime();
            cp.addEnergy(-effect.getEnergy());
            MineClasses.getInstance().getLanguageManager().sendMessage(player, "used_energy", "%energy%",
                    effect.getEnergy());
            MineClasses.getInstance().getLanguageManager().sendMessage(player, "activated_effect_energy", "%effect%",
                    effect.getEffectName(), "%energy%", cp.getEnergy(), "%max_energy%", cp.getMaxEnergy());
        } else {
            MineClasses.getInstance().getLanguageManager().sendMessage(player, "activated_effect", "%effect%",
                    effect.getEffectName());
        }

        item.setAmount(item.getAmount() - 1);

        // Apply to self and nearby
        PotionEffect pe = effect.getPotionEffect();
        cp.givePotionEffect(pe);
        if (applyNearby) {
            cp.giveNearPlayersEffect(pe, 25);
        }
    }
}