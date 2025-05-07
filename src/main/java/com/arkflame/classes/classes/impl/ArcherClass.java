package com.arkflame.classes.classes.impl;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.plugin.ClassesEffect;
import com.arkflame.classes.utils.Potions;
import com.arkflame.classes.utils.Materials;

public class ArcherClass extends EquipableClass {
    public ArcherClass() {
        passiveEffects.add(Potions.newPotionEffect("SPEED", 1200, 2));
        passiveEffects.add(Potions.newPotionEffect("DAMAGE_RESISTANCE", 1200, 2));

        activeEffects.put(Materials.get("SUGAR"),
                new ClassesEffect("effects_speed", 0, Potions.newPotionEffect("SPEED", 100, 4)));
        activeEffects.put(Materials.get("FEATHER"),
                new ClassesEffect("effects_jump", 0, Potions.newPotionEffect("JUMP", 100, 4)));
    }

    public String getName() {
        return "Archer";
    }
}