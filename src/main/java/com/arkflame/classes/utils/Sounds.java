package com.arkflame.classes.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Sounds {
    private Sounds() {
    }

    /**
     * Attempts to get the first valid Sound from a list of names.
     * If no valid Sound is found, returns null.
     *
     * @param names One or more String names of the Sound to retrieve.
     * @return The first found Sound, or null if none are found.
     */
    public static Sound get(String... names) {
        for (String name : names) {
            try {
                return Sound.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Sound with specified name not found, continue searching
            }
        }
        // Return null if no valid Sound was found
        return null;
    }

    /**
     * Plays a sound at a specified location for a player, if the sound is not null.
     * You can use multiple sounds to play for 1.8 servers and 1.20 servers
     *
     * @param player The player to play the sound for.
     * @param volume The volume of the sound (0.0 to 1.0).
     * @param pitch  The pitch of the sound (0.5 to 2.0).
     * @param sounds One or more String names of the Sound to play.
     */
    public static void play(Player player, float volume, float pitch, String... sounds) {
        Sound sound = get(sounds);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
    
    /**
     * Plays a sound at a specified location, if the sound is not null.
     * You can use multiple sounds to play for 1.8 servers and 1.20 servers
     *
     * @param location The location to play the sound at.
     * @param volume   The volume of the sound (0.0 to 1.0).
     * @param pitch    The pitch of the sound (0.5 to 2.0).
     * @param sounds   One or more String names of the Sound to play.
     */
   public static void play(Location location, float volume, float pitch, String... sounds) {
       Sound sound = get(sounds);
       if (sound != null) {
           location.getWorld().playSound(location, sound, volume, pitch);
       }
   }

    // You can use multiple sounds to play for 1.8 servers and 1.20 servers
    public static void play(float volume, float pitch, String... sounds) {
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            play(player, volume, pitch, sounds);
        }
    }

    public static void play(World world, Location dropLocation, float f, float g, String... sounds) {
        Sound sound = get(sounds);
        if (sound != null) {
            world.playSound(dropLocation, sound, f, g);
        }
    }
}
