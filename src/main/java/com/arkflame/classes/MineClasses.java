package com.arkflame.classes;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.arkflame.classes.commandexecutors.ClassesCommandExecutor;
import com.arkflame.classes.hooks.MineClansHook;
import com.arkflame.classes.language.LanguageManager;
import com.arkflame.classes.listeners.EntityDamageByEntityListener;
import com.arkflame.classes.listeners.EntityShootBowListener;
import com.arkflame.classes.listeners.PlayerInteractListener;
import com.arkflame.classes.listeners.PlayerItemHeldListener;
import com.arkflame.classes.listeners.PlayerJoinListener;
import com.arkflame.classes.listeners.PlayerQuitListener;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.placeholders.ClassesPlaceholders;
import com.arkflame.classes.utils.ConfigUtil;

public class MineClasses extends JavaPlugin {
  private static ClassPlayerManager classPlayerManager;

  private ClassesPlaceholders classesPlaceholders;

  private static void setClassPlayerManager(ClassPlayerManager classPlayerManager) {
    MineClasses.classPlayerManager = classPlayerManager;
  }

  public static ClassPlayerManager getClassPlayerManager() {
    return classPlayerManager;
  }

  public void onEnable() {
    setClassPlayerManager(new ClassPlayerManager());
    Server server = getServer();
    PluginManager pluginManager = server.getPluginManager();
    String dataFolder = getDataFolder().toString();
    ConfigUtil configUtil = new ConfigUtil(this);
    LanguageManager languageManager = new LanguageManager(dataFolder, configUtil);
    pluginManager.registerEvents((Listener) new EntityDamageByEntityListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new EntityShootBowListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new PlayerInteractListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new PlayerItemHeldListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new PlayerJoinListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new PlayerQuitListener(classPlayerManager), (Plugin) this);
    getCommand("classes")
        .setExecutor((CommandExecutor) new ClassesCommandExecutor(languageManager, getDescription().getVersion()));
    if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
      this.classesPlaceholders = new ClassesPlaceholders((Plugin) this, classPlayerManager);
      this.classesPlaceholders.register();
    }
  }

  public void onDisable() {
    if (this.classesPlaceholders != null)
      this.classesPlaceholders.unregister();
  }

  public static String getTeam(Player player1) {
    if (Bukkit.getServer().getPluginManager().isPluginEnabled("MineClans")) {
      return MineClansHook.getTeam(player1);
    }
    return null;
  }
}
