package com.arkflame.classes.plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.arkflame.classes.MineClasses;
import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.utils.Potions;

public class ClassPlayer {
  private final Player player;

  private Collection<PotionEffect> pendingEffects = new HashSet<>();

  private ItemStack heldItem = null;

  private EquipableClass classType = null;

  private int energy = 0;

  private long lastSpellTime = 0L;

  private long lastArcherTagTime = 0L;

  private boolean effectsAllowed = true;

  private boolean invisible = false;

  public ClassPlayer(Player player) {
    this.player = player;
  }

  public void setEffectsAllowed(boolean b) {
    this.effectsAllowed = b;
  }

  public boolean isEffectsAllowed() {
    return this.effectsAllowed;
  }

  public EquipableClass getClassType() {
    return this.classType;
  }

  public boolean setClassType(EquipableClass classType) {
    if (this.classType != classType) {
      this.classType = classType;
      setEnergy(0); // Reset energy
      return true;
    }
    return false;
  }

  public void setEnergy(int energy) {
    this.energy = energy;
  }

  public void addEnergy(int energy) {
    this.energy += energy;
  }

  public int getEnergy() {
    return this.energy;
  }

  public void setLastArcherTagTime() {
    this.lastArcherTagTime = System.currentTimeMillis();
  }

  public boolean hasArcherTag() {
    return (System.currentTimeMillis() - this.lastArcherTagTime <= 10000L);
  }

  public void setLastSpellTime() {
    this.lastSpellTime = System.currentTimeMillis();
  }

  public long getCooldownLeftMillis() {
    if (this.classType == null) {
      return 1;
    }
    int cooldown = this.classType.getCooldown();
    return (cooldown - (System.currentTimeMillis() - this.lastSpellTime));
  }

  /*
   * Give potion effect only if its better (and save last one)
   */
  public void givePotionEffect(PotionEffect effect) {
    MineClasses.runSync(() -> {
      int effectDuration = effect.getDuration();
      if (effectDuration > 0) {
        PotionEffectType effectType = effect.getType();
        PotionEffect effect1 = Potions.getPotionEffect(this.player, effectType);
        if (effect1 != null) {
          int effectAmplifier = effect.getAmplifier();
          int effectAmplifier1 = effect1.getAmplifier();
          if (effectAmplifier1 < effectAmplifier) {
            Potions.removePotionEffect(player, effectType);
            addPendingEffect(effect1);
          } else if (effectAmplifier1 == effectAmplifier && effect1.getDuration() < effectDuration) {
            Potions.removePotionEffect(player, effectType);
          }
        }
        Potions.addPotionEffect(player, effect);
      }
    });
  }

  private void addPendingEffect(PotionEffect effect) {
    this.pendingEffects.add(effect);
  }

  public void givePendingEffects() {
    if (!this.pendingEffects.isEmpty()) {
      Iterator<PotionEffect> pendingEffectsIterator = this.pendingEffects.iterator();
      while (pendingEffectsIterator.hasNext()) {
        PotionEffect effect = pendingEffectsIterator.next();
        pendingEffectsIterator.remove();
        Potions.addPotionEffect(player, effect);
      }
    }
  }

  public void clearPendingEffects() {
    this.pendingEffects.clear();
  }

  public void giveNearPlayersEffect(PotionEffect potionEffect, int radius) {
    Location location = this.player.getLocation();
    PotionEffectType potionEffectType = potionEffect.getType();
    ClassPlayerManager classPlayerManager = MineClasses.getClassPlayerManager();
    String team = MineClasses.getTeam(this.player);
    boolean isPositive = !Potions.isPotionEffectType(potionEffectType, "WITHER", "POISON", "WEAKNESS");
    for (Player player1 : location.getWorld().getPlayers()) {
      Location location1 = player1.getLocation();
      if (this.player != player1 && location.distance(location1) <= radius) {
        String team1 = MineClasses.getTeam(player1);
        boolean sameTeam = !(this.player != player1 && (team == null || team1 == null || !team.equals(team1)));
        if ((isPositive && sameTeam) || (!isPositive && !sameTeam)) {
          ClassPlayer classPlayer1 = classPlayerManager.get(player1);
          if (classPlayer1 != null)
            classPlayer1.givePotionEffect(potionEffect);
        }
      }
    }
  }

  public ItemStack getHeldItem() {
    return this.heldItem;
  }

  public void setHeldItem(ItemStack heldItem) {
    this.heldItem = heldItem;
  }

  public void clearClassEffects() {
    if (this.classType != null) {
      for (PotionEffect potionEffect : this.classType.getPassiveEffects()) {
        Potions.removePotionEffect(player, potionEffect.getType());
      }
      if (this.classType.isMiner())
        Potions.removePotionEffect(player, "INVISIBILITY");
    }
  }

  public void setInvisible(boolean invisible) {
    this.invisible = invisible;
  }

  public boolean isInvisible() {
    return this.invisible;
  }

  public int getMaxEnergy() {
    return 100;
  }

  public float getCooldownLeftSeconds() {
    return Math.round(this.getCooldownLeftMillis() / 1000f * 10f) / 10f; // #.#
  }
}
