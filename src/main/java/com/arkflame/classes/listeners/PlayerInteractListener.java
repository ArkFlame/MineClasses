package com.arkflame.classes.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;

public class PlayerInteractListener implements Listener {
  private final ClassPlayerManager classPlayerManager;

  public PlayerInteractListener(ClassPlayerManager classPlayerManager) {
    this.classPlayerManager = classPlayerManager;
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Action action = event.getAction();
    if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
      Player player = event.getPlayer();
      ItemStack itemStack = event.getItem();
      if (itemStack != null) {
        ClassPlayer classPlayer = this.classPlayerManager.get(player);
        EquipableClass classType = classPlayer.getClassType();
        if (classType == null) {
          return;
        }
        classType.onInteract(event);
      }
    }
  }
}
