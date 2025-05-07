package com.arkflame.classes.language;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class ClassesLanguage {
  private final Map<String, String> entries = new HashMap<>();
  
  ClassesLanguage(ConfigurationSection configuration) {
    for (String key : configuration.getKeys(false)) {
      Object rawValue = configuration.get(key);
      if (rawValue instanceof String)
        this.entries.put(key, ((String)rawValue).replace('&', '§')); 
    } 
  }
  
  public String getEntry(String key) {
    return this.entries.getOrDefault(key, "missingno");
  }
}
