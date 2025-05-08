package com.arkflame.classes.utils;

import org.bukkit.Material;

public class ArmorSet {
    private Material helmet;
    private Material chestplate;
    private Material leggings;
    private Material boots;

    public ArmorSet(Material helmet, Material chestplate, Material leggings, Material boots) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public Material getHelmet() {
        return helmet;
    }

    public Material getChestplate() {
        return chestplate;
    }

    public Material getLeggings() {
        return leggings;
    }

    public Material getBoots() {
        return boots;
    }
}
