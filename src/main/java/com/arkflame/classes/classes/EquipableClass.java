package com.arkflame.classes.classes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.classes.MineClasses;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.plugin.ClassesEffect;
import com.arkflame.classes.utils.ArmorSet;
import com.arkflame.classes.utils.ConfigUtil;
import com.arkflame.classes.utils.Materials;
import com.arkflame.classes.utils.Potions;

public class EquipableClass {
  private static Collection<EquipableClass> classes = ConcurrentHashMap.newKeySet();

  public static EquipableClass getArmor(Player player) {
    for (EquipableClass cls : classes) {
      if (cls.armorMatches(player)) {
        return cls;
      }
    }
    return null;
  }

  public static void loadClasses(ConfigUtil configUtil) {
    File file = new File(MineClasses.getInstance().getDataFolder(), "classes.yml");
    configUtil.copyResource("classes.yml", file);
    YamlConfiguration cfg = configUtil.loadConfig(file);
    ConfigurationSection classesSec = cfg.getConfigurationSection("classes");
    if (classesSec == null)
      return;

    for (String className : classesSec.getKeys(false)) {
      ConfigurationSection def = classesSec.getConfigurationSection(className);

      // 1) simple flags
      boolean usesEnergy = def.getBoolean("usesEnergy");
      boolean applyNearby = def.getBoolean("applyNearby");
      int cooldown = def.getInt("cooldown", 0);
      boolean isMiner = def.getBoolean("miner");
      boolean isArcher = def.getBoolean("archer");
      boolean isRogue = def.getBoolean("rogue");

      // 2) armor
      ArmorSet armor = null;
      ConfigurationSection armorSec = def.getConfigurationSection("armor");
      if (armorSec != null) {
        armor = new ArmorSet(
            Materials.get(getArmorPiece(armorSec, "helmet")),
            Materials.get(getArmorPiece(armorSec, "chestplate")),
            Materials.get(getArmorPiece(armorSec, "leggings")),
            Materials.get(getArmorPiece(armorSec, "boots")));
      }

      // 3) passive effects
      List<PotionEffect> passive = new ArrayList<>();
      for (Map<?, ?> m : def.getMapList("passiveEffects")) {
        passive.add(Potions.newPotionEffect(
            (String) m.get("type"),
            (Integer) m.get("duration"),
            (Integer) m.get("amplifier")));
      }

      // 4) active effects
      Map<Material, ClassesEffect> active = new EnumMap<>(Material.class);
      ConfigurationSection actSec = def.getConfigurationSection("activeEffects");
      if (actSec != null)
        for (String mat : actSec.getKeys(false)) {
          ConfigurationSection e = actSec.getConfigurationSection(mat);
          ConfigurationSection eff = e.getConfigurationSection("effect");
          active.put(Materials.get(mat),
              new ClassesEffect(
                  e.getString("key"),
                  e.getInt("energy"),
                  Potions.newPotionEffect(
                      eff.getString("type"),
                      eff.getInt("duration"),
                      eff.getInt("amplifier"))));
        }

      // 5) held‐item effects
      Map<Material, PotionEffect> held = new EnumMap<>(Material.class);
      ConfigurationSection heldSec = def.getConfigurationSection("heldItemEffects");
      if (heldSec != null)
        for (String mat : heldSec.getKeys(false)) {
          ConfigurationSection h = heldSec.getConfigurationSection(mat);
          held.put(Materials.get(mat),
              Potions.newPotionEffect(
                  h.getString("type"),
                  h.getInt("duration"),
                  h.getInt("amplifier")));
        }

      // 6) finally, instantiate via your factory:
      EquipableClass cls = new EquipableClass(
          className, usesEnergy, applyNearby, cooldown,
          armor, passive, active, held, isArcher, isRogue, isMiner);
      classes.add(cls);
    }
  }

  private static List<String> getArmorPiece(ConfigurationSection armorSec, String pieceKey) {
    if (armorSec.isString(pieceKey)) {
      return Collections.singletonList(armorSec.getString(pieceKey));
    } else {
      if (armorSec.isList(pieceKey)) {
        return armorSec.getStringList(pieceKey);
      }
    }
    return null;
  }

  protected final Collection<PotionEffect> passiveEffects = ConcurrentHashMap.newKeySet();
  protected final Map<Material, ClassesEffect> activeEffects = new EnumMap<>(Material.class);
  protected final Map<Material, PotionEffect> heldItemEffects = new EnumMap<>(Material.class);
  protected boolean usesEnergy = false; // Use energy for the effect
  protected boolean applyNearby = false; // Apply effect to nearby players
  protected String name = "Unknown";
  protected int cooldown = 0;
  protected ArmorSet armor = null;
  protected boolean isArcher = false;
  protected boolean isRogue = false;
  protected boolean isMiner = false;

  public EquipableClass(String className, boolean usesEnergy, boolean applyNearby, int cooldown, ArmorSet armor,
      List<PotionEffect> passive, Map<Material, ClassesEffect> active, Map<Material, PotionEffect> held,
      boolean isArcher, boolean isRogue, boolean isMiner) {
    this.name = className;
    this.usesEnergy = usesEnergy;
    this.applyNearby = applyNearby;
    this.cooldown = cooldown;
    this.armor = armor;
    this.passiveEffects.addAll(passive);
    this.activeEffects.putAll(active);
    this.heldItemEffects.putAll(held);
    this.isArcher = isArcher;
    this.isRogue = isRogue;
    this.isMiner = isMiner;
  }

  public boolean armorMatches(Player player) {
    if (armor != null) {
      if (player.getInventory().getHelmet() != null
          && player.getInventory().getHelmet().getType() == armor.getHelmet()) {
        if (player.getInventory().getChestplate() != null
            && player.getInventory().getChestplate().getType() == armor.getChestplate()) {
          if (player.getInventory().getLeggings() != null
              && player.getInventory().getLeggings().getType() == armor.getLeggings()) {
            if (player.getInventory().getBoots() != null
                && player.getInventory().getBoots().getType() == armor.getBoots()) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public Collection<PotionEffect> getPassiveEffects() {
    return passiveEffects;
  }

  public Map<Material, ClassesEffect> getActiveEffects() {
    return activeEffects;
  }

  public boolean usesEnergy() {
    return usesEnergy;
  }

  public boolean applyNearby() {
    return applyNearby;
  }

  public final void applyEffects(Player player) {
    for (PotionEffect effect : passiveEffects) {
      if (effect != null) {
        Potions.addPotionEffect(player, effect);
      }
    }
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return getName();
  }

  public int getCooldown() {
    return cooldown;
  }

  public ArmorSet getArmor() {
    return armor;
  }

  public boolean isArcher() {
    return isArcher;
  }

  public boolean isRogue() {
    return isRogue;
  }

  public boolean isMiner() {
    return isMiner;
  }

  public void onInteract(PlayerInteractEvent event) {
    if (activeEffects.isEmpty()) {
      return;
    }
    Player player = event.getPlayer();
    ItemStack item = event.getItem();
    if (item == null)
      return;
    ClassesEffect effect = activeEffects.get(item.getType());
    if (effect == null)
      return;

    ClassPlayer cp = MineClasses.getClassPlayerManager().get(player);
    float cooldown = cp.getCooldownLeftSeconds();
    if (cooldown > 0) {
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "on_cooldown", "%cooldown%",
          cooldown);
      return;
    }

    // Check if energy should be used
    if (usesEnergy && cp.getEnergy() < effect.getEnergy()) {
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "not_enough_energy", "%energy_required%",
          effect.getEnergy() - cp.getEnergy());
      return;
    }

    int newAmount = item.getAmount() - 1;
    item.setAmount(newAmount); // Consume item
    if (newAmount == 0) {
      item.setType(Material.AIR);
    }
    cp.setLastSpellTime(); // Cooldown

    if (usesEnergy) {
      cp.addEnergy(-effect.getEnergy());
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "used_energy", "%energy%",
          effect.getEnergy());
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "activated_effect_energy", "%effect%",
          effect.getEffectName(player), "%energy%", cp.getEnergy(), "%max_energy%", cp.getMaxEnergy());
    } else {
      MineClasses.getInstance().getLanguageManager().sendMessage(player, "activated_effect", "%effect%",
          effect.getEffectName(player));
    }

    // Apply to self and nearby
    PotionEffect pe = effect.getPotionEffect();
    cp.givePotionEffect(pe);
    if (applyNearby) {
      cp.giveNearPlayersEffect(pe, 25);
    }
  }

  public void runHeldItemEffect(ClassPlayer classPlayer) {
    if (heldItemEffects.isEmpty()) {
      return;
    }
    ItemStack heldItem = classPlayer.getHeldItem();
    if (heldItem != null) {
      PotionEffect potionEffect = heldItemEffects.get(heldItem.getType());
      if (potionEffect != null) {
        PotionEffectType potionEffectType = potionEffect.getType();
        if (potionEffect.getAmplifier() > 0)
          potionEffect = Potions.newPotionEffect(potionEffectType, potionEffect.getDuration(), 0);
        classPlayer.giveNearPlayersEffect(potionEffect, 25);
      }
    }
  }
}