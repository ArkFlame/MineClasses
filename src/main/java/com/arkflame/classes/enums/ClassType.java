package com.arkflame.classes.enums;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum ClassType {
  BARD(new PotionEffect[] { new PotionEffect(PotionEffectType.SPEED, 1200, 1), new PotionEffect(PotionEffectType.REGENERATION, 1200, 0), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 1) }),
  ARCHER(new PotionEffect[] { new PotionEffect(PotionEffectType.SPEED, 1200, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 2) }),
  ROGUE(new PotionEffect[] { new PotionEffect(PotionEffectType.SPEED, 1200, 2), new PotionEffect(PotionEffectType.JUMP, 1200, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 1) }),
  MINER(new PotionEffect[] { new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 1), new PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 1), new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 0) }),
  DIAMOND(new PotionEffect[] { new PotionEffect(PotionEffectType.SPEED, 1200, 0) });
  
  private final PotionEffect[] potionEffects;
  
  ClassType(PotionEffect[] potionEffects) {
    this.potionEffects = potionEffects;
  }
  
  public PotionEffect[] getPotionEffects() {
    return this.potionEffects;
  }
}
