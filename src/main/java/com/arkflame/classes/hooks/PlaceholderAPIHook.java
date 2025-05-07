package com.arkflame.classes.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion {
  private final Plugin plugin;
  
  private final ClassPlayerManager classPlayerManager;
  
  public PlaceholderAPIHook(Plugin plugin, ClassPlayerManager classPlayerManager) {
    this.plugin = plugin;
    this.classPlayerManager = classPlayerManager;
  }
  
  public String getIdentifier() {
    return "MineClasses";
  }
  
  public String getAuthor() {
    return this.plugin.getDescription().getAuthors().toString();
  }
  
  public String getVersion() {
    return this.plugin.getDescription().getVersion();
  }
  
  public String onPlaceholderRequest(Player player, String identifier) {
    if (player != null && !identifier.isEmpty()) {
      switch (identifier) {
        case "class":
          return this.classPlayerManager.get(player).getClassType().getName();
        case "cooldown":
          return String.valueOf(this.classPlayerManager.get(player).getCooldownLeftSeconds());
        case "energy":
          ClassPlayer classPlayer = this.classPlayerManager.get(player);
          return String.valueOf(classPlayer.getEnergy()); 
        case "max_energy":
          classPlayer = this.classPlayerManager.get(player);
          return String.valueOf(classPlayer.getMaxEnergy());
      }
    } 
    return null;
  }
}
