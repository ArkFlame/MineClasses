package com.arkflame.classes.listeners;

import java.util.EnumMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.arkflame.classes.enums.ClassType;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.plugin.ClassesEffect;
import com.arkflame.classes.utils.Materials;

public class PlayerInteractListener implements Listener {
  private final ClassPlayerManager classPlayerManager;
  
  private final Map<ClassType, Map<Material, ClassesEffect>> effectMap = new EnumMap<>(ClassType.class);
  
  public PlayerInteractListener(ClassPlayerManager classPlayerManager) {
    this.classPlayerManager = classPlayerManager;
    Map<Material, ClassesEffect> bardEffects = new EnumMap<>(Material.class);
    Map<Material, ClassesEffect> rogueEffects = new EnumMap<>(Material.class);
    Map<Material, ClassesEffect> archerEffects = new EnumMap<>(Material.class);
    this.effectMap.put(ClassType.BARD, bardEffects);
    this.effectMap.put(ClassType.ROGUE, rogueEffects);
    this.effectMap.put(ClassType.ARCHER, archerEffects);
    bardEffects.put(Materials.get("SUGAR"), 
        new ClassesEffect("VELOCIDAD", 16, new PotionEffect(PotionEffectType.SPEED, 80, 2)));
    bardEffects.put(Materials.get("FEATHER"), 
        new ClassesEffect("SALTO", 35, new PotionEffect(PotionEffectType.JUMP, 100, 4)));
    bardEffects.put(Materials.get("GHAST_TEAR"), 
        new ClassesEffect("REGENERATION", 35, new PotionEffect(PotionEffectType.REGENERATION, 100, 1)));
    bardEffects.put(Materials.get("IRON_INGOT"), 
        new ClassesEffect("RESISTENCIA", 35, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2)));
    bardEffects.put(Materials.get("BLAZE_POWDER"), 
        new ClassesEffect("FUERZA", 35, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1)));
    bardEffects.put(Materials.get("GOLDEN_CARROT"), 
        new ClassesEffect("VISION NOCTURNA", 20, new PotionEffect(PotionEffectType.NIGHT_VISION, 500, 1)));
    bardEffects.put(Materials.get("MAGMA_CREAM"), new ClassesEffect("RESISTENCIA AL FUEGO", 20, 
          new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 500, 0)));
    bardEffects.put(Materials.get("SPIDER_EYE"), 
        new ClassesEffect("WITHER", 35, new PotionEffect(PotionEffectType.WITHER, 100, 1)));
    bardEffects.put(Materials.get("RED_MUSHROOM"), 
        new ClassesEffect("VENENO", 35, new PotionEffect(PotionEffectType.POISON, 100, 1)));
    bardEffects.put(Materials.get("BROWN_MUSHROOM"), 
        new ClassesEffect("DEBILIDAD", 35, new PotionEffect(PotionEffectType.WEAKNESS, 100, 0)));
    bardEffects.put(Materials.get("INK_SACK"), 
        new ClassesEffect("INVISIBILIDAD", 35, new PotionEffect(PotionEffectType.INVISIBILITY, 800, 0)));
    rogueEffects.put(Materials.get("SUGAR"), 
        new ClassesEffect("VELOCIDAD", 0, new PotionEffect(PotionEffectType.SPEED, 100, 4)));
    rogueEffects.put(Materials.get("FEATHER"), 
        new ClassesEffect("SALTO", 0, new PotionEffect(PotionEffectType.JUMP, 100, 4)));
    archerEffects.put(Materials.get("SUGAR"), 
        new ClassesEffect("VELOCIDAD", 0, new PotionEffect(PotionEffectType.SPEED, 100, 4)));
    archerEffects.put(Materials.get("FEATHER"), 
        new ClassesEffect("SALTO", 0, new PotionEffect(PotionEffectType.JUMP, 100, 4)));
  }
  
  private void applyEffect(Player player, ItemStack itemStack, ClassesEffect classesEffect) {
    ClassPlayer classPlayer = this.classPlayerManager.get(player);
    float currentCooldown = Math.max(0.0F, (int)((float)classPlayer.getCooldown() / 100.0F) / 10.0F);
    if (currentCooldown <= 0.0F) {
      ClassType classType = classPlayer.getClassType();
      double classPlayerEnergy = classPlayer.getEnergy();
      int energy = classesEffect.getEnergy();
      if (classType != ClassType.BARD || classPlayerEnergy >= energy) {
        PotionEffect potionEffect = classesEffect.getPotionEffect();
        classPlayer.setLastSpellTime();
        itemStack.setAmount(itemStack.getAmount() - 1);
        if (classType == ClassType.BARD) {
          if (potionEffect.getType() != PotionEffectType.INCREASE_DAMAGE)
            classPlayer.givePotionEffect(potionEffect); 
          classPlayer.giveNearPlayersEffect(potionEffect, 25);
          classPlayer.addEnergy(-energy);
          player.sendMessage(ChatColor.RED + "-" + energy + " energia");
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                "&aActivaste el efecto de &b" + classesEffect.getEffectName() + "&a! Te queda &b" + (
                classPlayerEnergy - energy) + "&a energia!"));
        } else {
          classPlayer.givePotionEffect(potionEffect);
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                "&aActivaste el efecto de &b" + classesEffect.getEffectName() + "&a!"));
        } 
      } else {
        player.sendMessage(
            ChatColor.translateAlternateColorCodes('&', "&cNo tienes suficiente energia! Te falta &e" + (
              energy - classPlayerEnergy) + "&c de energia!"));
      } 
    } else {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            "&cEspera &e" + currentCooldown + "&c antes de volver a usar un efecto!"));
    } 
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Action action = event.getAction();
    if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
      Player player = event.getPlayer();
      ItemStack itemStack = event.getItem();
      if (itemStack != null) {
        ClassPlayer classPlayer = this.classPlayerManager.get(player);
        ClassType classType = classPlayer.getClassType();
        if (this.effectMap.containsKey(classType)) {
          Material material = itemStack.getType();
          Map<Material, ClassesEffect> classEffects = this.effectMap.get(classType);
          if (classEffects.containsKey(material))
            applyEffect(player, itemStack, classEffects.get(material)); 
        } 
      } 
    } 
  }
}
