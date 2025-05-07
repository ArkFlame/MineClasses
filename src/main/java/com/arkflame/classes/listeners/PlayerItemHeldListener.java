package com.arkflame.classes.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;

public class PlayerItemHeldListener implements Listener {
  private final ClassPlayerManager classPlayerManager;
  
  public PlayerItemHeldListener(ClassPlayerManager classPlayerManager) {
    this.classPlayerManager = classPlayerManager;
  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onItemSwitch(PlayerItemHeldEvent event) {
    Player player = event.getPlayer();
    ClassPlayer classPlayer = this.classPlayerManager.get(player);
    PlayerInventory playerInventory = player.getInventory();
    ItemStack heldItem = playerInventory.getItem(event.getNewSlot());
    classPlayer.setHeldItem(heldItem);
    EquipableClass classType = classPlayer.getClassType();
    if (classType != null) {
      classType.runHeldItemEffect(classPlayer); 
    }
  }
}
