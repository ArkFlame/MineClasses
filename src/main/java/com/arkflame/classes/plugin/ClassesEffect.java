package com.arkflame.classes.plugin;

import org.bukkit.potion.PotionEffect;

public class ClassesEffect {
  private final String effectName;
  
  private final int energy;
  
  private final PotionEffect potionEffect;
  
  public ClassesEffect(String effectName, int energy, PotionEffect potionEffect) {
    this.effectName = effectName;
    this.energy = energy;
    this.potionEffect = potionEffect;
  }
  
  public PotionEffect getPotionEffect() {
    return this.potionEffect;
  }
  
  public int getEnergy() {
    return this.energy;
  }
  
  public String getEffectName() {
    return this.effectName;
  }
}
