package com.arkflame.classes.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.classes.MineClasses;

public class Potions {
  public static PotionEffect getPotionEffect(Player player, PotionEffectType potionEffectType) {
    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
      if (potionEffect.getType() == potionEffectType) {
        return potionEffect;
      }
    }
    return null;
  }

  public static PotionEffect getPotionEffect(Player player, String name) {
    PotionEffectType potionEffectType = getPotionEffectType(name);
    if (potionEffectType != null) {
      return getPotionEffect(player, potionEffectType);
    }
    return null;
  }

  public static PotionEffectType getPotionEffectType(String name) {
    for (PotionEffectType potionEffectType : PotionEffectType.values()) {
      if (potionEffectType.getName().equalsIgnoreCase(name)) {
        return potionEffectType;
      }
    }
    return null;
  }

  public static PotionEffect newPotionEffect(PotionEffectType potionEffectType, int duration, int amplifier) {
    return new PotionEffect(potionEffectType, duration, amplifier);
  }

  public static PotionEffect newPotionEffect(String name, int duration, int amplifier) {
    PotionEffectType potionEffectType = getPotionEffectType(name);
    if (potionEffectType != null) {
      return newPotionEffect(potionEffectType, duration, amplifier);
    }
    return null;
  }

  public static void removePotionEffect(Player player, PotionEffectType potionEffectType) {
    MineClasses.runTask(() -> player.removePotionEffect(potionEffectType));
  }

  public static void addPotionEffect(Player player, PotionEffect potionEffect) {
    MineClasses.runTask(() -> player.addPotionEffect(potionEffect));
  }

  public static void removePotionEffect(Player damagedPlayer, String name) {
    for (PotionEffect potionEffect : damagedPlayer.getActivePotionEffects()) {
      if (potionEffect.getType().getName().equalsIgnoreCase(name)) {
        removePotionEffect(damagedPlayer, potionEffect.getType());
      }
    }
  }

  public static boolean isPotionEffectType(PotionEffectType potionEffectType, String ...compare) {
    for (String name : compare) {
      if (potionEffectType.getName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isPotionEffectType(PotionEffect potionEffect, String ...compare) {
    return isPotionEffectType(potionEffect.getType(), compare);
  } 
}
