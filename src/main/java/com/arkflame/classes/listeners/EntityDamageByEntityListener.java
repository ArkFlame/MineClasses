package com.arkflame.classes.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.arkflame.classes.MineClasses;
import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.utils.Materials;
import com.arkflame.classes.utils.Potions;
import com.arkflame.classes.utils.Sounds;

public class EntityDamageByEntityListener implements Listener {
  private final ClassPlayerManager classPlayerManager;

  public EntityDamageByEntityListener(ClassPlayerManager classPlayerManager) {
    this.classPlayerManager = classPlayerManager;
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    Entity damager = event.getDamager();
    if (damager instanceof Projectile) {
      ProjectileSource shooter = ((Projectile) damager).getShooter();
      if (shooter instanceof Entity) {
        damager = (Entity) shooter;
      }
    }
    if (damager instanceof Player) {
      Player damagerPlayer = (Player) damager;
      ClassPlayer classPlayer = this.classPlayerManager.get(damagerPlayer);
      if (classPlayer != null) {
        EntityDamageEvent.DamageCause damageCause = event.getCause();
        Entity damaged = event.getEntity();
        EquipableClass classType = classPlayer.getClassType();
        if (classType != null) {
            event.setDamage(event.getDamage() + event.getDamage() * MineClasses.getInstance().getDamageBoost(damagerPlayer));
        }
        if (damageCause == EntityDamageEvent.DamageCause.PROJECTILE) {
          if (classType != null && classType.isArcher()) {
            if (damaged instanceof Player) {
              Player damagedPlayer = (Player) damaged;
              ClassPlayer damagedClassPlayer = this.classPlayerManager.get(damagedPlayer);
              Potions.removePotionEffect(damagedPlayer, "INVISIBILITY");
              damagedClassPlayer.setLastArcherTagTime();
              MineClasses.getInstance().getLanguageManager().sendMessage(damagedPlayer, "archer_tag", "%time%",
                  10, "%damager%", damagerPlayer.getDisplayName());
            }
            event.setDamage(event.getDamage() * 1.25D);
          }
        } else if (damageCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
          if (damaged instanceof Player) {
            Player damagedPlayer = (Player) damaged;
            ClassPlayer damagedClassPlayer = this.classPlayerManager.get(damagedPlayer);
            if (damagedClassPlayer.hasArcherTag()) {
              event.setDamage(event.getDamage() * 1.25D);
            }
          }
          if (classType != null && classType.isRogue()) { // Backstab
            Location tLoc = damaged.getLocation();
            Vector toAttacker = damager.getLocation().toVector()
                .subtract(tLoc.toVector())
                .normalize();
            Vector tFacing = tLoc.getDirection().normalize();
            double cos = tFacing.dot(toAttacker);

            if (cos < -0.8 && cos > -1.0) { // Behind enemy
              ItemStack weapon = damagerPlayer.getInventory().getItem(damagerPlayer.getInventory().getHeldItemSlot());
              if (weapon != null && weapon.getType() == Materials.get("GOLD_SWORD", "GOLDEN_SWORD")) {
                double damage = event.getDamage() * 4;
                event.setDamage(damage);
                damagerPlayer.getInventory().setItem(damagerPlayer.getInventory().getHeldItemSlot(), null);
                MineClasses.getInstance().getLanguageManager().sendMessage(damagerPlayer, "backstab", "%target%",
                    damaged.getName(), "%damage%", damage);
                Sounds.play(damagerPlayer, 1f, 1f, "ITEM_BREAK", "ENTITY_ITEM_BREAK");
              }
            }
          }
        }
      }
    }
  }
}
