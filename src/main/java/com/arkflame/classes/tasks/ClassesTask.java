package com.arkflame.classes.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.arkflame.classes.MineClasses;
import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.classes.impl.BardClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.utils.Potions;

public class ClassesTask implements Runnable {
  private Server server;
  private ClassPlayerManager classPlayerManager;

  public ClassesTask(Server server, ClassPlayerManager classPlayerManager) {
    this.server = server;
    this.classPlayerManager = classPlayerManager;
  }

  public void run() {
    for (Player player : server.getOnlinePlayers()) {
      ClassPlayer classPlayer = classPlayerManager.get(player);
      if (classPlayer != null) {
        EquipableClass newClassType = classPlayer.isEffectsAllowed() ? EquipableClass.getArmor(player) : null;
        EquipableClass oldClassType = classPlayer.getClassType();
        if (newClassType != oldClassType) {
          classPlayer.clearPendingEffects();
          classPlayer.clearClassEffects();
          classPlayer.setClassType(newClassType);
          if (newClassType != null)
            MineClasses.getInstance().getLanguageManager().sendMessage(player, "class_activated", "%class%", newClassType.getName());
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
}
