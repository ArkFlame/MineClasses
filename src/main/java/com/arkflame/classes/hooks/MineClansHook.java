package com.arkflame.classes.hooks;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class MineClansHook {
    public static String getTeam(Player player) {
        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager()
                .getOrLoad(player.getUniqueId());
        if (factionPlayer != null) {
            Faction faction = factionPlayer.getFaction();
            if (faction != null) {
                return faction.getName();
            }
        }
        return null;
    }
}
