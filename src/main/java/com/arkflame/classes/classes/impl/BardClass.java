package com.arkflame.classes.classes.impl;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.plugin.ClassesEffect;
import com.arkflame.classes.utils.Potions;
import com.arkflame.classes.utils.Materials;

public class BardClass extends EquipableClass {
    public BardClass() {
        usesEnergy = true;
        applyNearby = true;

        passiveEffects.add(Potions.newPotionEffect("SPEED", 1200, 1));
        passiveEffects.add(Potions.newPotionEffect("REGENERATION", 1200, 0));
        passiveEffects.add(Potions.newPotionEffect("DAMAGE_RESISTANCE", 1200, 1));

        activeEffects.put(Materials.get("SUGAR"),
                new ClassesEffect("effects_speed", 16, Potions.newPotionEffect("SPEED", 80, 2)));
        activeEffects.put(Materials.get("FEATHER"),
                new ClassesEffect("effects_jump", 35, Potions.newPotionEffect("JUMP", 100, 4)));
        activeEffects.put(Materials.get("GHAST_TEAR"),
                new ClassesEffect("effects_regeneration", 35, Potions.newPotionEffect("REGENERATION", 100, 1)));
        activeEffects.put(Materials.get("IRON_INGOT"),
                new ClassesEffect("effects_resistance", 35, Potions.newPotionEffect("DAMAGE_RESISTANCE", 100, 2)));
        activeEffects.put(Materials.get("BLAZE_POWDER"),
                new ClassesEffect("effects_strength", 35, Potions.newPotionEffect("INCREASE_DAMAGE", 100, 1)));
        activeEffects.put(Materials.get("GOLDEN_CARROT"),
                new ClassesEffect("effects_night_vision", 20, Potions.newPotionEffect("NIGHT_VISION", 500, 1)));
        activeEffects.put(Materials.get("MAGMA_CREAM"),
                new ClassesEffect("effects_fire_resistance", 20, Potions.newPotionEffect("FIRE_RESISTANCE", 500, 0)));
        activeEffects.put(Materials.get("SPIDER_EYE"),
                new ClassesEffect("effects_wither", 35, Potions.newPotionEffect("WITHER", 100, 1)));
        activeEffects.put(Materials.get("RED_MUSHROOM"),
                new ClassesEffect("effects_poison", 35, Potions.newPotionEffect("POISON", 100, 1)));
        activeEffects.put(Materials.get("BROWN_MUSHROOM"),
                new ClassesEffect("effects_weakness", 35, Potions.newPotionEffect("WEAKNESS", 100, 0)));
        activeEffects.put(Materials.get("INK_SACK"),
                new ClassesEffect("effects_invisibility", 35, Potions.newPotionEffect("INVISIBILITY", 800, 0)));

        heldItemEffects.put(Materials.get("SUGAR"), Potions.newPotionEffect("SPEED", 100, 1));
        heldItemEffects.put(Materials.get("GHAST_TEAR"), Potions.newPotionEffect("REGENERATION", 100, 0));
        heldItemEffects.put(Materials.get("IRON_INGOT"), Potions.newPotionEffect("DAMAGE_RESISTANCE", 100, 0));
        heldItemEffects.put(Materials.get("BLAZE_POWDER"), Potions.newPotionEffect("INCREASE_DAMAGE", 100, 0));
        heldItemEffects.put(Materials.get("GOLDEN_CARROT"), Potions.newPotionEffect("NIGHT_VISION", 100, 0));
        heldItemEffects.put(Materials.get("MAGMA_CREAM"), Potions.newPotionEffect("FIRE_RESISTANCE", 100, 0));
        heldItemEffects.put(Materials.get("RABBIT_FOOT"), Potions.newPotionEffect("JUMP", 100, 1));
        heldItemEffects.put(Materials.get("INK_SACK"), Potions.newPotionEffect("INVISIBILITY", 100, 0));
    }

    public String getName() {
        return "Bard";
    }

    public int getCooldown() {
      return 5000;
    }
}