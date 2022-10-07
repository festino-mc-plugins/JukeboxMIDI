package com.festp.utils;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;

public class NettyUtils {
	
	private static final String BUKKIT_PACKAGE = "org.bukkit.craftbukkit.";
	
	/** format: "entity.CraftHorse" or "org.bukkit.craftbukkit.v1_18_R1.entity.CraftHorse" */
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
    
    public static Channel getChannel(Player player)
    {
		EntityPlayer nmsPlayer;
		try {
			Class<?> craftPlayerClass = getBukkitClass("entity.CraftPlayer");
			Object craftPlayer = craftPlayerClass.cast(player);
			Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
			nmsPlayer = (EntityPlayer)getHandleMethod.invoke(craftPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    	// .playerConnection.networkManager.channel
    	// b.a.k in 1.18.1
    	// b.b.m in 1.19.2
		PlayerConnection playerConnection = ReflectionUtils.findAndGetField(nmsPlayer, PlayerConnection.class);
		NetworkManager networkManager = ReflectionUtils.findAndGetField(playerConnection, NetworkManager.class);
    	return ReflectionUtils.findAndGetField(networkManager, Channel.class);
    }
}
