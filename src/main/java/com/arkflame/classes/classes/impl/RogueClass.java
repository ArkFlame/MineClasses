package com.arkflame.classes.classes.impl;

import org.bukkit.potion.PotionEffect;

import com.arkflame.classes.classes.EquipableMagicClass;
import com.arkflame.classes.plugin.ClassesEffect;
import com.arkflame.classes.utils.Potions;
import com.arkflame.classes.utils.Materials;

public class RogueClass extends EquipableMagicClass {
    public RogueClass() {
        activeEffects.put(Materials.get("SUGAR"),
                new ClassesEffect("VELOCIDAD", 0, Potions.newPotionEffect("SPEED", 100, 4)));
        activeEffects.put(Materials.get("FEATHER"),
                new ClassesEffect("SALTO", 0, Potions.newPotionEffect("JUMP", 100, 4)));
    }

    @Override
    public PotionEffect[] getPassiveEffects() {
        return new PotionEffect[] {
                Potions.newPotionEffect("SPEED", 1200, 2),
                Potions.newPotionEffect("JUMP", 1200, 2),
                Potions.newPotionEffect("DAMAGE_RESISTANCE", 1200, 1)
        };
    }

    @Override
    public String getName() {
        return "Rogue";
    }
}