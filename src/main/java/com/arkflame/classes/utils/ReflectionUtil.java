package com.arkflame.classes.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;

public class ReflectionUtil {
	private static Method getHandleMethod = null;
	private static Field pingField = null;
	private static Method getServerMethod = null;
	private static Method getRecipeManagerMethod = null;
	private static Field allDisplaysField = null;
	private static Method getPingMethod = null;
	
	private static Method getLocalePlayerMethod = null;
	private static Method getLocaleSpigotMethod = null;

	public static Method getLocalePlayerMethod(Player player) throws NoSuchMethodException, SecurityException {
		getLocalePlayerMethod = getLocalePlayerMethod == null
				? getLocalePlayerMethod = player.getClass().getMethod("getLocale")
				: getLocalePlayerMethod;
		getLocalePlayerMethod.setAccessible(true);
		return getLocalePlayerMethod;
	}

	public static Method getLocaleSpigotMethod() throws NoSuchMethodException, SecurityException {
		getLocaleSpigotMethod = getLocaleSpigotMethod == null
				? getLocaleSpigotMethod = Spigot.class.getMethod("getLocale")
				: getLocaleSpigotMethod;
		getLocaleSpigotMethod.setAccessible(true);
		return getLocaleSpigotMethod;
	}

	public static String getLocale(Player player) {
		try {
			return (String) getLocalePlayerMethod(player).invoke(player);
		} catch (Exception ex) {
			/* Couldn't get locale */
		}
		try {
			return (String) getLocaleSpigotMethod().invoke(player.spigot());
		} catch (Exception ex) {
			/* Couldn't get locale */
		}

		return null;
	}

	public static Method getHandleMethod(Player player) throws NoSuchMethodException, SecurityException {
		getHandleMethod = getHandleMethod == null ? getHandleMethod = player.getClass().getMethod("getHandle")
				: getHandleMethod;
		getHandleMethod.setAccessible(true);
		return getHandleMethod;
	}

	public static Field getPingField(Object playerHandle) throws NoSuchFieldException, SecurityException {
		pingField = pingField == null ? pingField = playerHandle.getClass().getField("ping") : pingField;
		pingField.setAccessible(true);
		return pingField;
	}

	public static Method getPingMethod() throws NoSuchMethodException, SecurityException {
		getPingMethod = getPingMethod == null ? getPingMethod = Player.class.getMethod("getPing") : getPingMethod;
		getPingMethod.setAccessible(true);
		return getPingMethod;
	}

	public static Object getHandle(Player player) throws Throwable {
		return getHandleMethod(player).invoke(player);
	}

	public static int getPing(Object playerHandle) throws Throwable {
		return (int) getPingField(playerHandle).get(playerHandle);
	}

	public static int getPing(Player player) {
		try {
			// Try to call the getPing method directly on the Player object via reflection
			Method getPingMethod = getPingMethod();
			Object ping = getPingMethod.invoke(player);
			if (ping instanceof Integer) {
				return (Integer) ping;
			}
		} catch (Throwable ignored) {
			/* Unavailable */
		}

		try {
			// Fallback to original method
			return getPing(getHandle(player));
		} catch (Throwable ignored) {
			/* Unavailable */
		}

		return 0;
	}

	public static List<?> getAllRecipeDisplays() throws Throwable {
		if (getServerMethod == null) {
			Class<?> minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
			getServerMethod = minecraftServerClass.getMethod("getServer");
			getRecipeManagerMethod = minecraftServerClass.getMethod("getRecipeManager");
		}

		getServerMethod.setAccessible(true);
		getRecipeManagerMethod.setAccessible(true);
		Object minecraftServerInstance = getServerMethod.invoke(null);
		Object recipeManagerObj = getRecipeManagerMethod.invoke(minecraftServerInstance);

		if (allDisplaysField == null) {
			Class<?> recipeManagerClass = Class.forName("net.minecraft.world.item.crafting.RecipeManager");
			allDisplaysField = recipeManagerClass.getDeclaredField("allDisplays");
			allDisplaysField.setAccessible(true);
		}

		return (List<?>) allDisplaysField.get(recipeManagerObj);
	}
}