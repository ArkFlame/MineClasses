package com.arkflame.classes.classes.impl;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.utils.Potions;

public class MinerClass extends EquipableClass {
    public MinerClass() {
        passiveEffects.add(Potions.newPotionEffect("FIRE_RESISTANCE", 1200, 1));
        passiveEffects.add(Potions.newPotionEffect("FAST_DIGGING", 1200, 1));
        passiveEffects.add(Potions.newPotionEffect("NIGHT_VISION", 1200, 0));
    }

    @Override
    public String getName() {
        return "Miner";
    }
}