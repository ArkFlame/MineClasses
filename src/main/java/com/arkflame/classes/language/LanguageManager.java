package com.arkflame.classes.language;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.arkflame.classes.utils.ConfigUtil;
import com.arkflame.classes.utils.Players;

public class LanguageManager {
  private final Map<String, ClassesLanguage> locales = new HashMap<>();

  public LanguageManager(String dataFolder, ConfigUtil configUtil) {
    String[] premadeLocales = { "en", "es" };
    File folder = new File(dataFolder, "locales");
    if (!folder.exists())
      folder.mkdir();
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = premadeLocales).length, b = 0; b < i;) {
      String locale = arrayOfString1[b];
      configUtil.copyResource(locale + ".yml", new File(folder, locale + ".yml"));
      b++;
    }
    for (File file : folder.listFiles()) {
      YamlConfiguration yamlConfiguration = configUtil.loadConfig(file);
      String locale = file.getName().split("[.]")[0];
      this.locales.put(locale, new ClassesLanguage((ConfigurationSection) yamlConfiguration));
      b++;
    }
  }

  public ClassesLanguage getLanguage(String rawLocale) {
    String locale = formatLocale(rawLocale);
    if (this.locales.containsKey(locale))
      return this.locales.get(locale);
    return this.locales.get("en");
  }

  private String formatLocale(String rawLocale) {
    if (rawLocale.length() > 1)
      return rawLocale.substring(0, 2);
    return "en";
  }

  public String getMessage(String locale, String key, Object... placeholders) {
    String message = getLanguage(locale).getEntry(key);
    for (int i = 0; i < placeholders.length; i+=2) {
      if (i >= placeholders.length - 1)
        break;
        message = message.replace(String.valueOf(placeholders[i]), String.valueOf(placeholders[i+1]));
    }
    return message;
  }

  public String getMessage(Player player, String key, Object... placeholders) {
    return getMessage(Players.getLocale(player), key, placeholders);
  }

  public void sendMessage(Player player, String key, Object... placeholders) {
    player.sendMessage(getMessage(player, key, placeholders));
  }
}
