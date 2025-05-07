package com.arkflame.classes.tasks;

import java.util.EnumMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.arkflame.classes.enums.ClassType;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.utils.Materials;
import com.arkflame.classes.utils.Potions;

public class TaskTimer {
  private final Map<Material, PotionEffect> effectMap = new EnumMap<>(Material.class);
  
  public TaskTimer(Plugin plugin, ClassPlayerManager classPlayerManager) {
    Server server = plugin.getServer();
    this.effectMap.put(Materials.get("SUGAR"), new PotionEffect(PotionEffectType.SPEED, 100, 1));
    this.effectMap.put(Materials.get("GHAST_TEAR"), new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
    this.effectMap.put(Materials.get("IRON_INGOT"), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
    this.effectMap.put(Materials.get("BLAZE_POWDER"), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
    this.effectMap.put(Materials.get("GOLDEN_CARROT"), new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0));
    this.effectMap.put(Materials.get("MAGMA_CREAM"), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0));
    this.effectMap.put(Materials.get("RABBIT_FOOT"), new PotionEffect(PotionEffectType.JUMP, 100, 1));
    this.effectMap.put(Materials.get("INK_SACK"), new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
    server.getScheduler().runTaskTimer(plugin, () -> {
          for (Player player : server.getOnlinePlayers())
            runTask(classPlayerManager, player); 
        }, 20L, 20L);
  }
  
  private ClassType getArmor(Player player) {
    PlayerInventory inventory = player.getInventory();
    if ((inventory.getArmorContents()).length > 0) {
      ItemStack helmetItem = inventory.getHelmet();
      if (helmetItem != null) {
        ItemStack chestplateItem = inventory.getChestplate();
        if (chestplateItem != null) {
          ItemStack leggingsItem = inventory.getLeggings();
          if (leggingsItem != null) {
            ItemStack bootsItem = inventory.getBoots();
            if (bootsItem != null) {
              Material helmet = helmetItem.getType();
              Material chestplate = chestplateItem.getType();
              Material leggings = leggingsItem.getType();
              Material boots = bootsItem.getType();
              if (helmet == Materials.get("GOLD_HELMET") && chestplate == Materials.get("GOLD_CHESTPLATE") && 
                leggings == Materials.get("GOLD_LEGGINGS") && boots == Materials.get("GOLD_BOOTS"))
                return ClassType.BARD; 
              if (helmet == Materials.get("LEATHER_HELMET") && chestplate == Materials.get("LEATHER_CHESTPLATE") && 
                leggings == Materials.get("LEATHER_LEGGINGS") && boots == Materials.get("LEATHER_BOOTS"))
                return ClassType.ARCHER; 
              if (helmet == Materials.get("CHAINMAIL_HELMET") && chestplate == Materials.get("CHAINMAIL_CHESTPLATE") && 
                leggings == Materials.get("CHAINMAIL_LEGGINGS") && boots == Materials.get("CHAINMAIL_BOOTS"))
                return ClassType.ROGUE; 
              if (helmet == Materials.get("IRON_HELMET") && chestplate == Materials.get("IRON_CHESTPLATE") && 
                leggings == Materials.get("IRON_LEGGINGS") && boots == Materials.get("IRON_BOOTS"))
                return ClassType.MINER; 
              if (helmet == Materials.get("DIAMOND_HELMET") && chestplate == Materials.get("DIAMOND_CHESTPLATE") && 
                leggings == Materials.get("DIAMOND_LEGGINGS") && boots == Materials.get("DIAMOND_BOOTS"))
                return ClassType.DIAMOND; 
            } 
          } 
        } 
      } 
    } 
    return null;
  }
  
  public void runBardEffect(ClassPlayer classPlayer) {
    ItemStack heldItem = classPlayer.getHeldItem();
    if (heldItem != null) {
      PotionEffect potionEffect = getPotionEffect(heldItem.getType());
      if (potionEffect != null) {
        PotionEffectType potionEffectType = potionEffect.getType();
        if (potionEffect.getAmplifier() > 0)
          potionEffect = new PotionEffect(potionEffectType, potionEffect.getDuration(), 0); 
        classPlayer.giveNearPlayersEffect(potionEffect, 25);
      } 
    } 
  }
  
  private PotionEffect getPotionEffect(Material material) {
    return this.effectMap.getOrDefault(material, null);
  }
  
  private void runTask(ClassPlayerManager classPlayerManager, Player player) {
    ClassPlayer classPlayer = classPlayerManager.get(player);
    if (classPlayer != null) {
      ClassType newClassType = classPlayer.isEffectsAllowed() ? getArmor(player) : null;
      ClassType oldClassType = classPlayer.getClassType();
      if (newClassType != oldClassType) {
        classPlayer.clearPendingEffects();
        classPlayer.clearClassEffects();
        classPlayer.setClassType(newClassType);
        if (newClassType != null)
          player.sendMessage(ChatColor.GREEN + "Activaste la clase " + ChatColor.AQUA + 
              newClassType.toString() + ChatColor.GREEN + "!"); 
      } 
      if (newClassType != null) {
        byte b;
        int i;
        PotionEffect[] arrayOfPotionEffect;
        for (i = (arrayOfPotionEffect = newClassType.getPotionEffects()).length, b = 0; b < i; ) {
          PotionEffect potionEffect = arrayOfPotionEffect[b];
          classPlayer.givePotionEffect(potionEffect);
          b++;
        } 
        double energy = classPlayer.getEnergy();
        if (newClassType == ClassType.BARD) {
          if (energy < 100.0D)
            classPlayer.addEnergy(1.0D); 
          runBardEffect(classPlayer);
        } else {
          if (energy > 0.0D)
            classPlayer.addEnergy(-energy); 
          if (newClassType == ClassType.MINER)
            if (player.getLocation().getY() <= 50.0D) {
              classPlayer.givePotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 0));
              classPlayer.setInvisible(true);
            } else if (classPlayer.isInvisible()) {
              PotionEffect potionEffect = Potions.getPotionEffect(player, 
                  PotionEffectType.INVISIBILITY);
              if (potionEffect != null) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                classPlayer.setInvisible(false);
              } 
            }  
        } 
      } 
    } 
  }
}
