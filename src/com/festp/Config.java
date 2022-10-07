package com.festp;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
	
	private static JavaPlugin plugin;
	private static MemoryConfiguration c;

	private static final String USE_VANILLA_OCTAVES_NAME = "use-vanilla-octaves";
	public static boolean useVanillaOctaves = false;
	
	public Config(JavaPlugin jp) {
		Config.plugin = jp;
		Config.c = jp.getConfig();
	}
	
	public static void loadConfig()
	{
		c.addDefault(USE_VANILLA_OCTAVES_NAME, false);
		c.options().copyDefaults(true);
		plugin.saveConfig();
		//getConfig().save(file);

		Config.useVanillaOctaves = plugin.getConfig().getBoolean(USE_VANILLA_OCTAVES_NAME);

		Logger.info("Config reloaded.");
	}
	
	public static void saveConfig()
	{
		c.set(USE_VANILLA_OCTAVES_NAME, Config.useVanillaOctaves);

		plugin.saveConfig();

		Logger.info("Config successfully saved.");
	}
	
	public static JavaPlugin plugin() {
		return plugin;
	}
}
