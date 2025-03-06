package com.festp.utils;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;

public class NettyUtils {
	
	private static final String BUKKIT_PACKAGE = "org.bukkit.craftbukkit.";
	
	/** format: "entity.CraftPlayer" or "org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer" */
	private static Class<?> getBukkitClass(String name) {
		if (!name.startsWith(BUKKIT_PACKAGE)) {
			String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		    name = BUKKIT_PACKAGE + version + "." + name;
		}
		
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
    
	/**
	 * 
	 * @return ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel
	 */
    public static Channel getChannel(Player player)
    {
		try {
			Class<?> craftPlayerClass = getBukkitClass("entity.CraftPlayer");
			Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
			// TODO support classes before 1.17 NMS remapping
			Class<?> playerConnectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
			Class<?> networkManagerClass = Class.forName("net.minecraft.network.NetworkManager");
			Class<?> channelClass = Class.forName("io.netty.channel.Channel");
			
			Object craftPlayer = craftPlayerClass.cast(player);
			Object nmsPlayer = getHandleMethod.invoke(craftPlayer);
			Object playerConnection = ReflectionUtils.findAndGetField(nmsPlayer, playerConnectionClass);
			Object networkManager = ReflectionUtils.findAndGetField(playerConnection, networkManagerClass);
			Object channel = ReflectionUtils.findAndGetField(networkManager, channelClass);
	    	return (Channel)channel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    }
}
