package com.arkflame.classes.classes;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.classes.MineClasses;
import com.arkflame.classes.classes.impl.ArcherClass;
import com.arkflame.classes.classes.impl.BardClass;
import com.arkflame.classes.classes.impl.DiamondClass;
import com.arkflame.classes.classes.impl.MinerClass;
import com.arkflame.classes.classes.impl.RogueClass;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.plugin.ClassesEffect;
import com.arkflame.classes.utils.Potions;

public abstract class EquipableClass {
  public static final EquipableClass ARCHER = new ArcherClass();
  public static final EquipableClass BARD = new BardClass();
  public static final EquipableClass DIAMOND = new DiamondClass();
  public static final EquipableClass MINER = new MinerClass();
  public static final EquipableClass ROGUE = new RogueClass();

  public static EquipableClass getArmor(Player player) {
    PlayerInventory inventory = player.getInventory();
    ItemStack helmetItem = inventory.getHelmet();
    ItemStack chestplateItem = inventory.getChestplate();
    ItemStack leggingsItem = inventory.getLeggings();
    ItemStack bootsItem = inventory.getBoots();
    if (helmetItem == null || chestplateItem == null || leggingsItem == null || bootsItem == null) {
      return null;
    }

    Material helmet = helmetItem.getType();
    Material chestplate = chestplateItem.getType();
    Material leggings = leggingsItem.getType();
    Material boots = bootsItem.getType();
    // Check if all armor pieces start with the same material type
    if (armorMatches("GOLD", helmet, chestplate, leggings, boots))
      return EquipableClass.BARD;
    if (armorMatches("LEATHER", helmet, chestplate, leggings, boots))
      return EquipableClass.ARCHER;
    if (armorMatches("CHAINMAIL", helmet, chestplate, leggings, boots))
      return EquipableClass.ROGUE;
    if (armorMatches("IRON", helmet, chestplate, leggings, boots))
      return EquipableClass.MINER;
    if (armorMatches("DIAMOND", helmet, chestplate, leggings, boots))
      return EquipableClass.DIAMOND;

    return null;
  }

  // Helper method to check if all armor pieces match the specified material type
  private static boolean armorMatches(String materialPrefix, Material... materials) {
    for (Material material : materials) {
      if (!material.name().startsWith(materialPrefix)) {
        return false;
      }
    }
    return true;
  }

  protected final Collection<PotionEffect> passiveEffects = ConcurrentHashMap.newKeySet();
  protected final Map<Material, ClassesEffect> activeEffects = new EnumMap<>(Material.class);
  protected final Map<Material, PotionEffect> heldItemEffects = new EnumMap<>(Material.class);
  protected boolean usesEnergy = false; // Use energy for the effect
  protected boolean applyNearby = false; // Apply effect to nearby players

  public Collection<PotionEffect> getPassiveEffects() {
    return passiveEffects;
  }

  public Map<Material, ClassesEffect> getActiveEffects() {
    return activeEffects;
  }

  public boolean usesEnergy() {
    return usesEnergy;
  }

  public boolean applyNearby() {
    return applyNearby;
  }

  public final void applyEffects(Player player) {
    for (PotionEffect effect : passiveEffects) {
      if (effect != null) {
        Potions.addPotionEffect(player, effect);
      }
    }
  }

  public String getName() {
    return "Unknown";
  }

  public String toString() {
    return getName();
  }

  public int getCooldown() {
    return 40000;
  }

  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack item = event.getItem();
    if (item == null)
      return;
    ClassesEffect effect = activeEffects.get(item.getType());
    if (effect == null)
      return;

    ClassPlayer cp = MineClasses.getClassPlayerManager().get(player);
    float cooldown = cp.getCooldownLeftSeconds();
    if (cooldown > 0) {
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "on_cooldown", "%cooldown%",
          cooldown);
      return;
    }

    // Check if energy should be used
    if (usesEnergy && cp.getEnergy() < effect.getEnergy()) {
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "not_enough_energy", "%energy_required%",
          effect.getEnergy() - cp.getEnergy());
      return;
    }

    item.setAmount(item.getAmount() - 1); // Consume item
    cp.setLastSpellTime(); // Cooldown

    if (usesEnergy) {
      cp.addEnergy(-effect.getEnergy());
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "used_energy", "%energy%",
          effect.getEnergy());
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "activated_effect_energy", "%effect%",
          effect.getEffectName(player), "%energy%", cp.getEnergy(), "%max_energy%", cp.getMaxEnergy());
    } else {
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "activated_effect", "%effect%",
          effect.getEffectName(player));
    }

    // Apply to self and nearby
    PotionEffect pe = effect.getPotionEffect();
    cp.givePotionEffect(pe);
    if (applyNearby) {
      cp.giveNearPlayersEffect(pe, 25);
    }
  }

  public void runHeldItemEffect(ClassPlayer classPlayer) {
    ItemStack heldItem = classPlayer.getHeldItem();
    if (heldItem != null) {
      PotionEffect potionEffect = heldItemEffects.get(heldItem.getType());
      if (potionEffect != null) {
        PotionEffectType potionEffectType = potionEffect.getType();
        if (potionEffect.getAmplifier() > 0)
          potionEffect = Potions.newPotionEffect(potionEffectType, potionEffect.getDuration(), 0);
        classPlayer.giveNearPlayersEffect(potionEffect, 25);
      }
    }
  }
}