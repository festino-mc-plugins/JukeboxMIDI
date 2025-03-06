package com.festp.utils;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Utils {
	
	/**@return <b>null</b> if the <b>stack</b> was only given<br>
	 * <b>Item</b> if at least one item was dropped*/
	public static Item giveOrDrop(Inventory inv, ItemStack stack)
	{
		HashMap<Integer, ItemStack> res = inv.addItem(stack);
		if (res.isEmpty())
			return null;
		return dropUngiven(inv.getLocation(), res.get(0));
	}
	/** Can give items only to players.
	 * @return <b>null</b> if the <b>stack</b> was only given<br>
	 * <b>Item</b> if at least one item was dropped*/
	public static Item giveOrDrop(Entity entity, ItemStack stack)
	{
		if (entity instanceof Player)
			return giveOrDrop(((Player)entity).getInventory(), stack);
		return dropUngiven(entity.getLocation(), stack);
	}
	private static Item dropUngiven(Location l, ItemStack stack) {
		Item item = l.getWorld().dropItem(l, stack);
		item.setVelocity(new Vector());
		item.setPickupDelay(0);
		return item;
	}
}
