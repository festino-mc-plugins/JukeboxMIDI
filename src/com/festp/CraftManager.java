package com.festp;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;

import com.festp.notebook.NoteDiscCrafter;

public class CraftManager implements Listener
{
	Server server;
	Main plugin;
	
	List<NamespacedKey> recipeKeys = new ArrayList<>();
	
	public CraftManager(Main plugin, Server server) {
		this.plugin = plugin;
		this.server = server;
	}
	
	public void addCrafts() {
		NoteDiscCrafter.addCrafts(plugin, this);
	}
	
	public void giveRecipe(Player p, String recipe) {
		Bukkit.getServer().dispatchCommand(p, "recipe give "+p.getName()+" "+recipe);
	}
	public void giveOwnRecipe(Player p, String recipe) {
		giveRecipe(p, plugin.getName().toLowerCase()+":"+recipe);
	}
	public void giveRecipe(HumanEntity player, NamespacedKey key) {
		player.discoverRecipe(key);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		for (NamespacedKey recipe_name : recipeKeys) {
			giveRecipe(p, recipe_name);
		}
	}
	
	public boolean addCraftbookRecipe(NamespacedKey key, Recipe recipe) {
		if (recipeKeys.contains(key))
			return false;
    	server.addRecipe(recipe);
		recipeKeys.add(key);
		return true;
	}
}
