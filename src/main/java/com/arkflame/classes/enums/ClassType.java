package com.arkflame.classes.enums;

import org.bukkit.potion.PotionEffect;

import com.arkflame.classes.utils.Potions;

public enum ClassType {
  BARD(new PotionEffect[] { Potions.newPotionEffect("SPEED", 1200, 1), Potions.newPotionEffect("REGENERATION", 1200, 0), Potions.newPotionEffect("DAMAGE_RESISTANCE", 1200, 1) }),
  ARCHER(new PotionEffect[] { Potions.newPotionEffect("SPEED", 1200, 2), Potions.newPotionEffect("DAMAGE_RESISTANCE", 1200, 2) }),
  ROGUE(new PotionEffect[] { Potions.newPotionEffect("SPEED", 1200, 2), Potions.newPotionEffect("JUMP", 1200, 2), Potions.newPotionEffect("DAMAGE_RESISTANCE", 1200, 1) }),
  MINER(new PotionEffect[] { Potions.newPotionEffect("FIRE_RESISTANCE", 1200, 1), Potions.newPotionEffect("FAST_DIGGING", 1200, 1), Potions.newPotionEffect("NIGHT_VISION", 1200, 0) }),
  DIAMOND(new PotionEffect[] { Potions.newPotionEffect("SPEED", 1200, 0) });
  
  private final PotionEffect[] potionEffects;
  
  ClassType(PotionEffect[] potionEffects) {
    this.potionEffects = potionEffects;
  }
  
  public PotionEffect[] getPotionEffects() {
    return this.potionEffects;
  }
}
