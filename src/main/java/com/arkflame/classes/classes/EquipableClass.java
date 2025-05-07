package com.arkflame.classes.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.arkflame.classes.classes.impl.ArcherClass;
import com.arkflame.classes.classes.impl.BardClass;
import com.arkflame.classes.classes.impl.DiamondClass;
import com.arkflame.classes.classes.impl.MinerClass;
import com.arkflame.classes.classes.impl.RogueClass;
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
    System.out.println("trying to get armor");
    if (helmetItem == null || chestplateItem == null || leggingsItem == null || bootsItem == null) {
      return null;
    }

    Material helmet = helmetItem.getType();
    Material chestplate = chestplateItem.getType();
    Material leggings = leggingsItem.getType();
    Material boots = bootsItem.getType();
    System.out.println(helmet + " " + chestplate + " " + leggings + " " + boots);
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

  public abstract PotionEffect[] getPassiveEffects();

  public void onInteract(PlayerInteractEvent event) {
    // no-op by default
  }

  public final void applyEffects(Player player) {
    for (PotionEffect effect : getPassiveEffects()) {
      if (effect != null) {
        Potions.addPotionEffect(player, effect);
      }
    }
  }
}