package com.arkflame.classes.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Potions {
  public static PotionEffect getPotionEffect(Player player, PotionEffectType potionEffectType) {
    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
      if (potionEffect.getType() == potionEffectType)
        return potionEffect;
    }
    return null;
  }

  public static PotionEffectType getPotionEffectType(String name) {
    for (PotionEffectType potionEffectType : PotionEffectType.values()) {
      if (potionEffectType.getName().equalsIgnoreCase(name))
        return potionEffectType;
    }
    return null;
  }

  public static PotionEffect newPotionEffect(String name, int duration, int amplifier) {
    PotionEffectType potionEffectType = getPotionEffectType(name);
    if (potionEffectType != null)
      return new PotionEffect(potionEffectType, duration, amplifier);
    return null;
  }
}
