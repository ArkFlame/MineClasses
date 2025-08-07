package com.arkflame.classes.plugin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

  private Map<PotionEffectType, PotionEffect> pendingEffects = new ConcurrentHashMap<>();
  private ItemStack heldItem = null;
  private EquipableClass classType = null;

  private int energy = 0;
  private int lastPotionEffectsCount = 0;

  private long lastSpellTime = 0L;
  private long lastArcherTagTime = 0L;
  private long lastBackstabTime = 0L; // Last time this player was backstabbed

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
      int newDuration = effect.getDuration();
      if (newDuration > 0) {
        PotionEffectType effectType = effect.getType();
        PotionEffect currentEffect = Potions.getPotionEffect(this.player, effectType);

        if (currentEffect != null) {
          int newAmplifier = effect.getAmplifier();
          int currentAmplifier = currentEffect.getAmplifier();
          int currentDuration = currentEffect.getDuration();

          if (newAmplifier == currentAmplifier) {
            // Same amplifier
            if (newDuration > currentDuration) {
              // More duration - replace without saving
              Potions.removePotionEffect(player, effectType);
              Potions.addPotionEffect(player, effect);
            }
            // If newDuration <= currentDuration, do nothing (don't apply new effect)

          } else if (newAmplifier > currentAmplifier) {
            // Higher amplifier - always apply and save old effect as pending
            Potions.removePotionEffect(player, effectType);
            addPendingEffect(currentEffect);
            Potions.addPotionEffect(player, effect);

          } else {
            // Lower amplifier - don't apply new effect, add it as pending
            addPendingEffect(effect);
          }
        } else {
          // No current effect of this type - simply apply the new one
          Potions.addPotionEffect(player, effect);
        }
      }
    });
  }

  private void addPendingEffect(PotionEffect effect) {
    // Don't add permanent effects (duration -1 or Integer.MAX_VALUE) as pending
    if (effect.getDuration() > 0 && effect.getDuration() != Integer.MAX_VALUE) {
      // Only add effects with less than 10 minutes (12000 ticks) to prevent bugs
      if (effect.getDuration() < 12000) {
        this.pendingEffects.put(effect.getType(), effect);
      }
    }
  }

  public void givePendingEffects() {
    if (!this.pendingEffects.isEmpty()) {
      Iterator<PotionEffect> pendingEffectsIterator = this.pendingEffects.values().iterator();
      while (pendingEffectsIterator.hasNext()) {
        PotionEffect effect = pendingEffectsIterator.next();
        pendingEffectsIterator.remove();

        // Check if the effect is still valid (hasn't expired)
        if (effect.getDuration() > 0) {
          PotionEffect currentEffect = Potions.getPotionEffect(this.player, effect.getType());

          // Only apply pending effect if there's no current effect or if pending has
          // higher/equal amplifier
          if (currentEffect == null || effect.getAmplifier() >= currentEffect.getAmplifier()) {
            if (currentEffect != null) {
              Potions.removePotionEffect(player, effect.getType());
            }
            Potions.addPotionEffect(player, effect);
          }
        }
      }
    }
  }

  public void clearPendingEffects() {
    this.pendingEffects.clear();
  }

  // Helper method to check pending effects count for debugging
  public int getPendingEffectsCount() {
    return this.pendingEffects.size();
  }

  // Helper method to get pending effects of a specific type
  public List<PotionEffect> getPendingEffectsOfType(PotionEffectType type) {
    return this.pendingEffects.values().stream()
        .filter(effect -> effect.getType().equals(type))
        .collect(Collectors.toList());
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

  public int getLastPotionEffectsCount() {
    return lastPotionEffectsCount;
  }

  public void setLastPotionEffectsCount(int currentPotionEffectsCount) {
    this.lastPotionEffectsCount = currentPotionEffectsCount;
  }

  public boolean hasBackstabCooldown() {
    return System.currentTimeMillis() - this.lastBackstabTime <= 3000L;
  }

  public void updateBackstabCooldown() {
    this.lastBackstabTime = System.currentTimeMillis();
  }
}
