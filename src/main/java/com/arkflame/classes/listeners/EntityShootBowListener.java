package com.arkflame.classes.listeners;

import java.util.Iterator;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.utils.Materials;

public class EntityShootBowListener implements Listener {
  private final ClassPlayerManager classPlayerManager;
  
  public EntityShootBowListener(ClassPlayerManager classPlayerManager) {
    this.classPlayerManager = classPlayerManager;
  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onEntityShootBow(EntityShootBowEvent event) {
    Entity projectile = event.getProjectile();
    if (projectile instanceof Arrow) {
      LivingEntity livingEntity = event.getEntity();
      if (livingEntity instanceof Player) {
        Player player = (Player)livingEntity;
        ClassPlayer classPlayer = this.classPlayerManager.get(player);
        EquipableClass classType = classPlayer.getClassType();
        if (classType != null && classType.isArcher()) {
          boolean chance = (Math.random() > 0.9D);
          if (chance) {
            PlayerInventory playerInventory = player.getInventory();
            if (consumeArrows(playerInventory, 2)) {
              Arrow arrow = (Arrow)projectile;
              Location arrowLocation = arrow.getLocation();
              Vector arrowVelocity = arrow.getVelocity();
              boolean arrowIsCritical = arrow.isCritical();
              Arrow arrow1 = (Arrow)player.launchProjectile(Arrow.class);
              Arrow arrow2 = (Arrow)player.launchProjectile(Arrow.class);
              arrow1.teleport(arrowLocation.add(-0.5D, 0.0D, -0.5D));
              arrow2.teleport(arrowLocation.add(1.0D, 0.0D, 1.0D));
              arrow1.setVelocity(arrowVelocity);
              arrow2.setVelocity(arrowVelocity);
              arrow1.setCritical(arrowIsCritical);
              arrow2.setCritical(arrowIsCritical);
            } 
          } 
        } 
      } 
    } 
  }
  
  private boolean consumeArrows(PlayerInventory playerInventory, int amount) {
    if (playerInventory.containsAtLeast(new ItemStack(Materials.get("ARROW")), amount + 1)) {
      Map<Integer, ? extends ItemStack> items = playerInventory.all(Materials.get("ARROW"));
      int amountRemoved = 0;
      for (Iterator<Integer> iterator = items.keySet().iterator(); iterator.hasNext(); ) {
        int slot = ((Integer)iterator.next()).intValue();
        ItemStack itemStack = items.get(Integer.valueOf(slot));
        while (itemStack.getAmount() > 0) {
          itemStack.setAmount(itemStack.getAmount() - 1);
          amountRemoved++;
          if (amountRemoved == amount)
            return true; 
        } 
      } 
      return false;
    } 
    return false;
  }
}
