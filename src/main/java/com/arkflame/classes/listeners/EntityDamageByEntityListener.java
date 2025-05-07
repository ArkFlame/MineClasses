package com.arkflame.classes.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.utils.Materials;
import com.arkflame.classes.utils.Potions;

public class EntityDamageByEntityListener implements Listener {
  private final ClassPlayerManager classPlayerManager;
  
  public EntityDamageByEntityListener(ClassPlayerManager classPlayerManager) {
    this.classPlayerManager = classPlayerManager;
  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    Entity damager = event.getDamager();
    if (damager instanceof Player) {
      Player damagerPlayer = (Player)damager;
      ClassPlayer classPlayer = this.classPlayerManager.get(damagerPlayer);
      if (classPlayer != null) {
        EntityDamageEvent.DamageCause damageCause = event.getCause();
        Entity damaged = event.getEntity();
        if (damageCause == EntityDamageEvent.DamageCause.PROJECTILE) {
          if (classPlayer.getClassType() == EquipableClass.ARCHER) {
            if (damaged instanceof Player) {
              Player damagedPlayer = (Player)damaged;
              ClassPlayer damagedClassPlayer = this.classPlayerManager.get(damagedPlayer);
              Potions.removePotionEffect(damagedPlayer, "INVISIBILITY");
              damagedClassPlayer.setLastArcherTagTime();
              damagedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    "&cFuiste tageado por &e10 segundos&c por &b" + damagerPlayer.getDisplayName() + 
                    "&c!"));
            } 
            event.setDamage(event.getDamage() * 1.25D);
          } 
        } else if (damageCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
          if (damaged instanceof Player) {
            Player damagedPlayer = (Player)damaged;
            ClassPlayer damagedClassPlayer = this.classPlayerManager.get(damagedPlayer);
            if (damagedClassPlayer.hasArcherTag())
              event.setDamage(event.getDamage() * 1.25D); 
          } 
          if (classPlayer.getClassType() == EquipableClass.ROGUE) {
            Location damagerLocation = damager.getLocation();
            Vector damagerDirection = damagerLocation.getDirection();
            Vector damagedDirection = damaged.getLocation().getDirection();
            if (damagerDirection.dot(damagedDirection) > 0.6D) {
              PlayerInventory playerInventory = damagerPlayer.getInventory();
              int heldItemSlot = playerInventory.getHeldItemSlot();
              ItemStack heldItem = playerInventory.getItem(heldItemSlot);
              if (heldItem != null && heldItem.getType() == Materials.get("GOLD_SWORD")) {
                double damage = event.getDamage() * 4.0D;
                event.setDamage(damage);
                playerInventory.setItem(heldItemSlot, null);
                damagerPlayer.playSound(damagerLocation, Sound.valueOf("ITEM_BREAK"), 1.0F, 1.0F);
                damagerPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      "&aApuñalaste a &b" + damaged.getName() + "&a por &c" + damage + "♥&a!"));
              } 
            } 
          } 
        } 
      } 
    } 
  }
}
