package com.arkflame.classes.tasks;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.arkflame.classes.MineClasses;
import com.arkflame.classes.classes.EquipableClass;
import com.arkflame.classes.managers.ClassPlayerManager;
import com.arkflame.classes.plugin.ClassPlayer;
import com.arkflame.classes.utils.Potions;

public class ClassesTask implements Runnable {
  private static int ENERGY_PER_SECOND = 2;

  private Server server;
  private ClassPlayerManager classPlayerManager;
  private boolean processing = false;

  public ClassesTask(Server server, ClassPlayerManager classPlayerManager) {
    this.server = server;
    this.classPlayerManager = classPlayerManager;
  }

  public void run() {
    if (processing) {
      return;
    }
    try {
      processing = true;
      for (Player player : server.getOnlinePlayers()) {
        ClassPlayer classPlayer = classPlayerManager.get(player);
        if (classPlayer != null) {
          // Potion effects changed
          int currentPotionEffectsCount = player.getActivePotionEffects().size();
          if (currentPotionEffectsCount != classPlayer.getLastPotionEffectsCount()) {
            classPlayer.givePendingEffects();
            classPlayer.setLastPotionEffectsCount(currentPotionEffectsCount);
          }
          // Class changed
          EquipableClass newClassType = classPlayer.isEffectsAllowed() ? EquipableClass.getArmor(player) : null;
          EquipableClass oldClassType = classPlayer.getClassType();
          if (newClassType != oldClassType) {
            classPlayer.clearPendingEffects();
            classPlayer.clearClassEffects();
            classPlayer.setClassType(newClassType);
            if (newClassType != null) {
              MineClasses.getInstance().getLanguageManager().sendMessage(player, "class_activated", "%class%",
                  newClassType.getName());
              double damageBoost = MineClasses.getInstance().getDamageBoost(player);
              if (damageBoost > 0.0D) {
                player.sendMessage(
                    MineClasses.getInstance().getLanguageManager().getMessage(player, "damage_boost", "%boost%",
                        (int) (damageBoost * 100.0D)));
              }
            }
          }
          if (newClassType != null) {
            for (PotionEffect passiveEffect : newClassType.getPassiveEffects()) {
              classPlayer.givePotionEffect(passiveEffect);
            }
            newClassType.runHeldItemEffect(classPlayer);
            int energy = classPlayer.getEnergy();
            if (newClassType.usesEnergy()) {
              if (energy < classPlayer.getMaxEnergy()) {
                classPlayer.addEnergy(ENERGY_PER_SECOND);
              }
            } else {
              if (energy > 0)
                classPlayer.addEnergy(-energy);
              if (newClassType.isMiner())
                if (player.getLocation().getY() <= 50) {
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
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      processing = false;
    }
  }
}
