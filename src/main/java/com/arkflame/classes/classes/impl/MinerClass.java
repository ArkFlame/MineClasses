package com.arkflame.classes.classes.impl;

import org.bukkit.potion.PotionEffect;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.utils.Potions;

public class MinerClass extends EquipableClass {
    @Override
    public PotionEffect[] getPassiveEffects() {
        return new PotionEffect[]{
            Potions.newPotionEffect("FIRE_RESISTANCE", 1200, 1),
            Potions.newPotionEffect("FAST_DIGGING", 1200, 1),
            Potions.newPotionEffect("NIGHT_VISION", 1200, 0)
        };
    }

    @Override
    public String getName() {
        return "Miner";
    }
}