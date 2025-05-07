package com.arkflame.classes.classes.impl;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.classes.classes.EquipableMagicClass;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.plugin.ClassesEffect;
import com.arkflame.classes.utils.Potions;
import com.arkflame.classes.utils.Materials;

public class BardClass extends EquipableMagicClass {
    private final Map<Material, PotionEffect> heldItemEffects = new EnumMap<>(Material.class);

    public void runHeldItemEffect(ClassPlayer classPlayer) {
        ItemStack heldItem = classPlayer.getHeldItem();
        if (heldItem != null) {
            PotionEffect potionEffect = heldItemEffects.get(heldItem.getType());
            if (potionEffect != null) {
                PotionEffectType potionEffectType = potionEffect.getType();
                if (potionEffect.getAmplifier() > 0)
                    potionEffect = Potions.newPotionEffect(potionEffectType, potionEffect.getDuration(), 0);
                classPlayer.giveNearPlayersEffect(potionEffect, 25);
            }
        }
    }

    public BardClass() {
        super(true);
        activeEffects.put(Materials.get("SUGAR"),
                new ClassesEffect("VELOCIDAD", 16, Potions.newPotionEffect("SPEED", 80, 2)));
        activeEffects.put(Materials.get("FEATHER"),
                new ClassesEffect("SALTO", 35, Potions.newPotionEffect("JUMP", 100, 4)));
        activeEffects.put(Materials.get("GHAST_TEAR"),
                new ClassesEffect("REGENERATION", 35, Potions.newPotionEffect("REGENERATION", 100, 1)));
        activeEffects.put(Materials.get("IRON_INGOT"),
                new ClassesEffect("RESISTENCIA", 35, Potions.newPotionEffect("DAMAGE_RESISTANCE", 100, 2)));
        activeEffects.put(Materials.get("BLAZE_POWDER"),
                new ClassesEffect("FUERZA", 35, Potions.newPotionEffect("INCREASE_DAMAGE", 100, 1)));
        activeEffects.put(Materials.get("GOLDEN_CARROT"),
                new ClassesEffect("VISION NOCTURNA", 20, Potions.newPotionEffect("NIGHT_VISION", 500, 1)));
        activeEffects.put(Materials.get("MAGMA_CREAM"),
                new ClassesEffect("RESISTENCIA AL FUEGO", 20, Potions.newPotionEffect("FIRE_RESISTANCE", 500, 0)));
        activeEffects.put(Materials.get("SPIDER_EYE"),
                new ClassesEffect("WITHER", 35, Potions.newPotionEffect("WITHER", 100, 1)));
        activeEffects.put(Materials.get("RED_MUSHROOM"),
                new ClassesEffect("VENENO", 35, Potions.newPotionEffect("POISON", 100, 1)));
        activeEffects.put(Materials.get("BROWN_MUSHROOM"),
                new ClassesEffect("DEBILIDAD", 35, Potions.newPotionEffect("WEAKNESS", 100, 0)));
        activeEffects.put(Materials.get("INK_SACK"),
                new ClassesEffect("INVISIBILIDAD", 35, Potions.newPotionEffect("INVISIBILITY", 800, 0)));

        heldItemEffects.put(Materials.get("SUGAR"), Potions.newPotionEffect("SPEED", 100, 1));
        heldItemEffects.put(Materials.get("GHAST_TEAR"), Potions.newPotionEffect("REGENERATION", 100, 0));
        heldItemEffects.put(Materials.get("IRON_INGOT"), Potions.newPotionEffect("DAMAGE_RESISTANCE", 100, 0));
        heldItemEffects.put(Materials.get("BLAZE_POWDER"), Potions.newPotionEffect("INCREASE_DAMAGE", 100, 0));
        heldItemEffects.put(Materials.get("GOLDEN_CARROT"), Potions.newPotionEffect("NIGHT_VISION", 100, 0));
        heldItemEffects.put(Materials.get("MAGMA_CREAM"), Potions.newPotionEffect("FIRE_RESISTANCE", 100, 0));
        heldItemEffects.put(Materials.get("RABBIT_FOOT"), Potions.newPotionEffect("JUMP", 100, 1));
        heldItemEffects.put(Materials.get("INK_SACK"), Potions.newPotionEffect("INVISIBILITY", 100, 0));
    }

    @Override
    public PotionEffect[] getPassiveEffects() {
        return new PotionEffect[] {
                Potions.newPotionEffect("SPEED", 1200, 1),
                Potions.newPotionEffect("REGENERATION", 1200, 0),
                Potions.newPotionEffect("DAMAGE_RESISTANCE", 1200, 1)
        };
    }

    public String getName() {
        return "Bard";
    }
}