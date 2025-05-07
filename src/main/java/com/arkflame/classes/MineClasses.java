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
import com.arkflame.classes.tasks.ClassesTask;
import com.arkflame.classes.utils.ConfigUtil;

public class MineClasses extends JavaPlugin {
  private static MineClasses instance;
  private static ClassPlayerManager classPlayerManager;

  public static MineClasses getInstance() {
    return instance;
  }

  public static void setInstance(MineClasses instance) {
    MineClasses.instance = instance;
  }

  private static void setClassPlayerManager(ClassPlayerManager classPlayerManager) {
    MineClasses.classPlayerManager = classPlayerManager;
  }

  public static ClassPlayerManager getClassPlayerManager() {
    return classPlayerManager;
  }

  private ClassesPlaceholders classesPlaceholders;
  private LanguageManager languageManager;

  public LanguageManager getLanguageManager() {
    return languageManager;
  }

  public void onEnable() {
    setInstance(this);
    setClassPlayerManager(new ClassPlayerManager());
    Server server = getServer();
    PluginManager pluginManager = server.getPluginManager();
    String dataFolder = getDataFolder().toString();
    ConfigUtil configUtil = new ConfigUtil(this);
    languageManager = new LanguageManager(dataFolder, configUtil);
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
    Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ClassesTask(server, classPlayerManager), 20L, 20L);
  }

  public void onDisable() {
    if (this.classesPlaceholders != null) {
      this.classesPlaceholders.unregister();
    }
    Bukkit.getScheduler().cancelTasks(this);
  }

  public static String getTeam(Player player1) {
    if (Bukkit.getServer().getPluginManager().isPluginEnabled("MineClans")) {
      return MineClansHook.getTeam(player1);
    }
    return null;
  }

  public static void runTask(Runnable task) {
    if (Bukkit.isPrimaryThread()) {
      task.run();
    } else {
      Bukkit.getScheduler().runTask(MineClasses.getInstance(), task);
    }
  }
}
