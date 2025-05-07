package com.arkflame.classes.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Players {
  public static String getLocale(Player player) {
      if (player != null && player.isOnline()) {
          String locale = ReflectionUtil.getLocale(player);

          if (locale != null && locale.length() > 1) {
              locale = locale.substring(0, 2);
          }
          
          return locale;
      }

      return null;
  }

  public static String getLocale(CommandSender sender) {
    if (sender instanceof Player) {
      return getLocale((Player)sender);
    }
    return "en";
  }
}
