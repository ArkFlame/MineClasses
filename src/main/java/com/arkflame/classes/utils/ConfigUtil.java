package com.arkflame.classes.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigUtil {
    private final JavaPlugin plugin;

    public ConfigUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveConfig(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config file: " + file.getName(), e);
        }
    }

    public YamlConfiguration loadConfig(File file) {
        if (!file.exists()) {
            plugin.getLogger().warning("Config file does not exist: " + file.getName());
            return null;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config == null) {
            return new YamlConfiguration();
        }
        return config;
    }

    public boolean deleteConfig(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public void createDirectory(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create directory: {0}", dir.getAbsolutePath());
        }
    }

    public void copyResource(String resourcePath, File targetFile) {
        if (targetFile.exists())
            return;

        try (InputStream inputStream = plugin.getResource(resourcePath)) {
            if (inputStream == null) {
                plugin.getLogger().warning("Resource not found: " + resourcePath);
                return;
            }

            createDirectory(targetFile.getParentFile());
            Files.copy(inputStream, targetFile.toPath());
            plugin.getLogger().info("Copied default resource: " + resourcePath);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to copy resource: " + resourcePath, e);
        }
    }
}
