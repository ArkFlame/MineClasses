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
    protected boolean usesEnergy = false; // Default is false

    public EquipableMagicClass(boolean usesEnergy) {
        this.usesEnergy = usesEnergy;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;
        ClassesEffect effect = activeEffects.get(item.getType());
        if (effect == null) return;

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
            item.setAmount(item.getAmount() - 1);
            player.sendMessage(ChatColor.RED + "-" + effect.getEnergy() + " energia");
            player.sendMessage(ChatColor.GREEN + "Activaste el efecto de "
                    + ChatColor.AQUA + effect.getEffectName() + ChatColor.GREEN
                    + "! Te queda " + ChatColor.AQUA + cp.getEnergy() + ChatColor.GREEN + " energia.");
        }

        // Apply to self or nearby
        PotionEffect pe = effect.getPotionEffect();
        if ("INCREASE_DAMAGE".equals(pe.getType().getName())) {
            cp.givePotionEffect(pe);
        }
        cp.giveNearPlayersEffect(pe, 25);
    }
}