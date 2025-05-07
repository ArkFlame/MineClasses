package com.arkflame.classes.language;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.arkflame.classes.utils.ConfigUtil;

public class LanguageManager {
  private final Map<String, ClassesLanguage> locales = new HashMap<>();

  private final String defaultLocale = "en";

  public LanguageManager(String dataFolder, ConfigUtil configUtil) {
    String[] premadeLocales = { "en", "es" };
    String localeFolderRaw = String.valueOf(dataFolder) + "/locales/";
    File folder = new File(localeFolderRaw);
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
}
