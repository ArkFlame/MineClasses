package com.arkflame.classes.classes.impl;

import org.bukkit.potion.PotionEffect;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.utils.Potions;

public class DiamondClass extends EquipableClass {
    @Override
    public PotionEffect[] getPassiveEffects() {
        return new PotionEffect[]{
            Potions.newPotionEffect("SPEED", 1200, 0)
        };
    }
}