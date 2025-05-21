package com.arkflame.classes;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.commands.ArcherCommand;
import com.arkflame.classes.commands.BardCommand;
import com.arkflame.classes.commands.ClassesCommand;
import com.arkflame.classes.commands.MinerCommand;
import com.arkflame.classes.commands.RogueCommand;
import com.arkflame.classes.hooks.PlaceholderAPIHook;
import com.arkflame.classes.hooks.MineClansHook;
import com.arkflame.classes.language.LanguageManager;
import com.arkflame.classes.listeners.EntityDamageByEntityListener;
import com.arkflame.classes.listeners.EntityShootBowListener;
import com.arkflame.classes.listeners.PlayerInteractListener;
import com.arkflame.classes.listeners.PlayerItemHeldListener;
import com.arkflame.classes.listeners.PlayerJoinListener;
import com.arkflame.classes.listeners.PlayerQuitListener;
import com.arkflame.classes.managers.ClassPlayerManager;
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

  private PlaceholderAPIHook classesPlaceholders;
  private LanguageManager languageManager;

  public LanguageManager getLanguageManager() {
    return languageManager;
  }

  public void onEnable() {
    saveDefaultConfig();
    setInstance(this);
    setClassPlayerManager(new ClassPlayerManager());
    Server server = getServer();
    PluginManager pluginManager = server.getPluginManager();
    String dataFolder = getDataFolder().toString();
    ConfigUtil configUtil = new ConfigUtil(this);
    EquipableClass.loadClasses(configUtil);
    languageManager = new LanguageManager(dataFolder, configUtil);
    pluginManager.registerEvents((Listener) new EntityDamageByEntityListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new EntityShootBowListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new PlayerInteractListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new PlayerItemHeldListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new PlayerJoinListener(classPlayerManager), (Plugin) this);
    pluginManager.registerEvents((Listener) new PlayerQuitListener(classPlayerManager), (Plugin) this);
    registerCommand("classes", new ClassesCommand(languageManager, getDescription().getVersion()));
    registerCommand("archer", new ArcherCommand(languageManager));
    registerCommand("bard", new BardCommand(languageManager));
    registerCommand("rogue", new RogueCommand(languageManager));
    registerCommand("miner", new MinerCommand(languageManager));
    if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
      this.classesPlaceholders = new PlaceholderAPIHook((Plugin) this, classPlayerManager);
      this.classesPlaceholders.register();
    }
    Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ClassesTask(server, classPlayerManager), 20L, 20L);
  }

  private void registerCommand(String cmd, CommandExecutor executor) {
    PluginCommand command = getCommand(cmd);
    if (command != null) {
      command.setExecutor(executor);
    } else {
      getLogger().warning("Command " + cmd + " not found in plugin.yml");
    }
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

  public static void runSync(Runnable task) {
    if (Bukkit.isPrimaryThread()) {
      task.run();
    } else {
      Bukkit.getScheduler().runTask(MineClasses.getInstance(), task);
    }
  }

  public static void runAsync(Runnable runnable) {
    if (Bukkit.isPrimaryThread()) {
      Bukkit.getScheduler().runTaskAsynchronously(MineClasses.getInstance(), runnable);
    } else {
      runnable.run();
    }
  }

  public double getDamageBoost(Player damagerPlayer) {
    List<Integer> damageBoosts = getConfig().getIntegerList("damage-boosts");
    Collections.sort(damageBoosts, Collections.reverseOrder());
    for (int damageBoost : damageBoosts) {
      if (damagerPlayer.hasPermission("mineclasses.damageboost." + damageBoost)) {
        return damageBoost / 100.0D;
      }
    }
    return 0;
  }
}
