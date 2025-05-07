package com.arkflame.classes.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;

public class PlayerQuitListener implements Listener {
  private final ClassPlayerManager classPlayerManager;
  
  public PlayerQuitListener(ClassPlayerManager classPlayerManager) {
    this.classPlayerManager = classPlayerManager;
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    ClassPlayer classPlayer = this.classPlayerManager.get(player);
    classPlayer.clearClassEffects();
    this.classPlayerManager.remove(player);
  }
}
