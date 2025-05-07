package com.arkflame.classes.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.classes.impl.BardClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.utils.Potions;

public class TaskTimer {
  public TaskTimer(Plugin plugin, ClassPlayerManager classPlayerManager) {
    Server server = plugin.getServer();
    server.getScheduler().runTaskTimer(plugin, () -> {
      for (Player player : server.getOnlinePlayers())
        runTask(classPlayerManager, player);
    }, 20L, 20L);
  }

  private void runTask(ClassPlayerManager classPlayerManager, Player player) {
    ClassPlayer classPlayer = classPlayerManager.get(player);
    if (classPlayer != null) {
      EquipableClass newClassType = classPlayer.isEffectsAllowed() ? EquipableClass.getArmor(player) : null;
      EquipableClass oldClassType = classPlayer.getClassType();
      if (newClassType != oldClassType) {
        classPlayer.clearPendingEffects();
        classPlayer.clearClassEffects();
        classPlayer.setClassType(newClassType);
        if (newClassType != null)
          player.sendMessage(ChatColor.GREEN + "Activaste la clase " + ChatColor.AQUA +
              newClassType.toString() + ChatColor.GREEN + "!");
      }
      if (newClassType != null) {
        byte b;
        int i;
        PotionEffect[] arrayOfPotionEffect;
        for (i = (arrayOfPotionEffect = newClassType.getPassiveEffects()).length, b = 0; b < i;) {
          PotionEffect potionEffect = arrayOfPotionEffect[b];
          classPlayer.givePotionEffect(potionEffect);
          b++;
        }
        double energy = classPlayer.getEnergy();
        if (newClassType == EquipableClass.BARD) {
          if (energy < 100.0D) {
            classPlayer.addEnergy(1.0D);
          }
          BardClass bardClass = (BardClass) classPlayer.getClassType();
          bardClass.runHeldItemEffect(classPlayer);
        } else {
          if (energy > 0.0D)
            classPlayer.addEnergy(-energy);
          if (newClassType == EquipableClass.MINER)
            if (player.getLocation().getY() <= 50.0D) {
              classPlayer.givePotionEffect(Potions.newPotionEffect("INVISIBILITY", 1200, 0));
              classPlayer.setInvisible(true);
            } else if (classPlayer.isInvisible()) {
              PotionEffect potionEffect = Potions.getPotionEffect(player,
                  "INVISIBILITY");
              if (potionEffect != null) {
                Potions.removePotionEffect(player, "INVISIBILITY");
                classPlayer.setInvisible(false);
              }
            }
        }
      }
    }
  }
}
