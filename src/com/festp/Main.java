package com.festp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.jukebox.JukeboxHandler;
import com.festp.jukebox.JukeboxPacketListener;
import com.festp.notebook.NoteDiscCrafter;
import com.festp.notebook.NoteSoundRecorder;
import com.festp.notebook.RecordingBookList;
import com.festp.notedisc.NoteDiscList;
import com.festp.utils.NbtUtils;

public class Main extends JavaPlugin
{
	Config conf;
	private CraftManager craftManager;
	
	public void onEnable() {
		Logger.setLogger(getLogger());
    	PluginManager pm = getServer().getPluginManager();
    	
    	NbtUtils.setPlugin(this);
		
		conf = new Config(this);
		Config.loadConfig();
    	craftManager = new CraftManager(this, getServer());
    	
    	craftManager.addCrafts();
    	pm.registerEvents(craftManager, this);

    	NoteDiscList noteDiscList = new NoteDiscList();
    	JukeboxHandler jukeboxHandler = new JukeboxHandler(noteDiscList);
    	pm.registerEvents(jukeboxHandler, this);
    	NoteDiscCrafter noteDiscListener = new NoteDiscCrafter();
    	pm.registerEvents(noteDiscListener, this);

    	RecordingBookList recordingBookList = new RecordingBookList();
    	NoteSoundRecorder noteSoundRecorder = new NoteSoundRecorder(recordingBookList);
    	pm.registerEvents(noteSoundRecorder, this);

    	JukeboxPacketListener jukePackets = new JukeboxPacketListener(jukeboxHandler);
    	pm.registerEvents(jukePackets, this);
    	
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
			new Runnable() {
				public void run() {
					TaskList.tick();
					
					jukeboxHandler.tick();
					
					noteDiscList.tick();
					
					recordingBookList.tick();
				}
			}, 0L, 1L);
		
	}
	
	public CraftManager getCraftManager()
	{
		return craftManager;
	}
}
