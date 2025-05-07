package com.arkflame.classes.classes.impl;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.utils.Potions;

public class DiamondClass extends EquipableClass {
    public DiamondClass() {
        passiveEffects.add(Potions.newPotionEffect("SPEED", 1200, 0));
    }

    @Override
    public String getName() {
        return "Diamond";
    }
}