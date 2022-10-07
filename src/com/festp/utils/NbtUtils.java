package com.festp.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class NbtUtils
{
	private static JavaPlugin plugin;
	
	public static void setPlugin(JavaPlugin plugin)
	{
		NbtUtils.plugin = plugin;
	}

	public static ItemStack remove(ItemStack stack, String key)
	{
		if (!isValid(stack))
			return null;
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		ItemMeta meta = stack.getItemMeta();
		meta.getPersistentDataContainer().remove(nameKey);
		stack = stack.clone();
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack setByteArray(ItemStack stack, String key, byte[] value)
	{
		if (!isValid(stack))
			return null;
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		ItemMeta meta = stack.getItemMeta();
		meta.getPersistentDataContainer().set(nameKey, PersistentDataType.BYTE_ARRAY, value);
		stack = stack.clone();
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static boolean hasByteArray(ItemStack stack, String key)
	{
		if (!isValid(stack))
			return false;
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
		return container.has(nameKey, PersistentDataType.BYTE_ARRAY);
	}
	
	public static byte[] getByteArray(ItemStack stack, String key)
	{
		if (!isValid(stack))
			return null;
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
		if (!container.has(nameKey, PersistentDataType.BYTE_ARRAY))
			return null;
		return container.get(nameKey, PersistentDataType.BYTE_ARRAY);
	}
	
	private static boolean isValid(ItemStack stack) {
		return stack != null && stack.hasItemMeta();
	}
}
