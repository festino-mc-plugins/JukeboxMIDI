package com.festp.jukebox;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.festp.Logger;
import com.festp.utils.NettyUtils;
import com.festp.utils.ReflectionUtils;
import com.festp.utils.Vector3i;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;

public class JukeboxPacketListener implements Listener {
	
	final JukeboxHandler handler;
	
	public JukeboxPacketListener(JukeboxHandler handler) {
		this.handler = handler;
	}
	
	@EventHandler
    public void onJoin(PlayerJoinEvent event) {
        injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }
    private void removePlayer(Player player) {
        Channel channel = NettyUtils.getChannel(player);
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    private void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception
            {
            	if (Logger.isOnPacketLevel()) Logger.info(ChatColor.YELLOW + "PACKET READ: " + ChatColor.RED + packet.toString());
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception
            {
                if (ReflectionUtils.getPacketPlayOutWorldEventClass().isInstance(packet)) {
                	Vector3i pos = ReflectionUtils.getBlockPosition(packet);
                	World world = player.getWorld();
                	for (Jukebox jukebox : handler.getClickedJukeboxes()) {
                		if (jukebox.getWorld() == world && jukebox.getX() == pos.getX() && jukebox.getY() == pos.getY() && jukebox.getZ() == pos.getZ()) {
                			if (Logger.isOnPacketLevel()) Logger.info(ChatColor.AQUA + "PACKET BLOCKED: " + ChatColor.GREEN + packet.toString());
                        	return;
                		}
                	}
                	if (Logger.isOnPacketLevel()) Logger.info(ChatColor.AQUA + "PACKET PASSED: " + ChatColor.GREEN + packet.toString());
                }
                super.write(channelHandlerContext, packet, channelPromise);
            }


        };

        ChannelPipeline pipeline = ((Channel)NettyUtils.getChannel(player)).pipeline();
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    
    // https://github.com/frengor/PacketInjectorAPI/blob/master/src/main/java/com/fren_gor/packetInjectorAPI/ReflectionUtil.java
	private static final Map<String, Map<String, Field>> fields = new ConcurrentHashMap<>();
	
	public static Object getField(Object object, String field) {
		return getField(object, object.getClass(), field);
	}
	private static Object getField(Object object, Class<?> c, String field) {

		if (fields.containsKey(c.getCanonicalName())) {
			Map<String, Field> fs = fields.get(c.getCanonicalName());
			if (fs.containsKey(field)) {
				try {
					return fs.get(field).get(object);
				} catch (ReflectiveOperationException e) {
					return null;
				}
			}
		}

		Class<?> current = c;
		Field f;
		while (true)
			try {
				f = current.getDeclaredField(field);
				break;
			} catch (ReflectiveOperationException e1) {
				current = current.getSuperclass();
				if (current != null) {
					continue;
				}
				return null;
			}

		f.setAccessible(true);

		Map<String, Field> map;
		if (fields.containsKey(c.getCanonicalName())) {
			map = fields.get(c.getCanonicalName());
		} else {
			map = new ConcurrentHashMap<>();
			fields.put(c.getCanonicalName(), map);
		}

		map.put(f.getName(), f);

		try {
			return f.get(object);
		} catch (ReflectiveOperationException e) {
			return null;
		}

	}
}
