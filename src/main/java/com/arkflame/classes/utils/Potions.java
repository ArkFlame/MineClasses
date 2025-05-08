package com.arkflame.classes.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.classes.MineClasses;

public class Potions {
  public static PotionEffect getPotionEffect(Player player, PotionEffectType potionEffectType) {
    if (potionEffectType != null) {
      if (player.hasPotionEffect(potionEffectType)) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
          if (potionEffect.getType() == potionEffectType) {
            return potionEffect;
          }
        }
      }
    }
    return null;
  }

  public static PotionEffect getPotionEffect(Player player, String name) {
    return getPotionEffect(player, getPotionEffectType(name));
  }

  public static PotionEffectType getPotionEffectType(String name) {
    return PotionEffectType.getByName(name);
  }

  public static PotionEffect newPotionEffect(String name, int duration, int amplifier) {
    PotionEffectType potionEffectType = getPotionEffectType(name);
    if (potionEffectType != null) {
      return newPotionEffect(potionEffectType, duration, amplifier);
    }
    return null;
  }

  public static PotionEffect newPotionEffect(PotionEffectType potionEffectType, int duration, int amplifier) {
    return new PotionEffect(potionEffectType, duration, amplifier);
  }

  public static void removePotionEffect(Player player, PotionEffectType potionEffectType) {
    MineClasses.runSync(() -> player.removePotionEffect(potionEffectType));
  }

  public static void removePotionEffect(Player player, String name) {
    removePotionEffect(player, getPotionEffectType(name));
  }

  public static void addPotionEffect(Player player, PotionEffect potionEffect) {
    MineClasses.runSync(() -> player.addPotionEffect(potionEffect));
  }

  public static boolean isPotionEffectType(PotionEffectType potionEffectType, String... compare) {
    for (String name : compare) {
      if (potionEffectType.getName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isPotionEffectType(PotionEffect potionEffect, String... compare) {
    return isPotionEffectType(potionEffect.getType(), compare);
  }
}
