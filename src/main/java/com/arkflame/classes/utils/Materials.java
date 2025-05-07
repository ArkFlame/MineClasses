package com.arkflame.classes.utils;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for retrieving Bukkit Materials across different server versions.
 */
public class Materials {
    private static Map<String, Material> CACHE = new HashMap<>();

    private Materials() {}

    /**
     * Attempts to get the first valid Material from a list of names.
     * If no valid Material is found, returns Material.AIR.
     *
     * @param names One or more String names of the Material to retrieve.
     * @return The first found Material, or Material.AIR if none are found.
     */
    public static Material get(String... names) {
        for (String name : names) {
            name = name.toUpperCase();
            if (CACHE.containsKey(name)) {
                return CACHE.get(name);
            }
            Material material = Material.getMaterial(name);
            if (material != null && material != Material.AIR) {
                CACHE.put(name, material);
                return material;
            }
        }
        return Material.AIR;
    }

    /**
     * Attempts to get the first valid Material from a list of names.
     * If no valid Material is found, returns Material.AIR.
     *
     * @param names One or more String names of the Material to retrieve.
     * @return The first found Material, or Material.AIR if none are found.
     */
    public static Material get(List<String> names) {
        if (names == null) {
            return Material.AIR;
        }
        return get(names.toArray(new String[0]));
    }
}
