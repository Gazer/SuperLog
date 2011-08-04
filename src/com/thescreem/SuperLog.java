package com.thescreem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class SuperLog extends JavaPlugin {
	
	public Logger log = Logger.getLogger("Minecraft");
	PluginDescriptionFile pdfile;
	public static String mainDirectory = "plugins/SuperLog/";
	public static File blockLogs = new File(mainDirectory + "block.logs");
	public static File chestLogs = new File(mainDirectory + "chest.logs");
	public static File configFile = new File(mainDirectory + "config.yml");
	private final SLPlayerListener playerListener = new SLPlayerListener(this);
	private final SLBlockListener blockListener = new SLBlockListener(this);
	public static PermissionHandler permissionHandler;
	public Configuration config;
	
	@Override
	public void onDisable() {
		pdfile = getDescription();
		log.info("[" + pdfile.getFullName() + "] is now disabled!");
	}

	@Override
	public void onEnable() {
		config = getConfiguration();
		
		//Creating the SuperLog folder if it doesn't exist//
		new File(mainDirectory).mkdir();
		
		if(!configFile.exists()){
			try{
				configFile.createNewFile();
				writeConfig();
				log.info("[SuperLog] Config file successfully created!");
			} catch(IOException e){
				log.severe("[SuperLog] An error occured creating the config.yml file, here is the error:\n");
				e.printStackTrace();
			}
		}
		
		//Creating the block.logs file if it doesn't exist//
		if(config.getBoolean("log-block-placement", true) && config.getBoolean("log-block-breaking", true)){
			if(!blockLogs.exists()){
				try{
					blockLogs.createNewFile();
					log.info("[SuperLog] Block.logs file successfully created!");
				} catch(IOException e){
					log.severe("[SuperLog] An error occured creating the block.logs file, here is the error:\n");
					e.printStackTrace();
				}
			}
		}
		
		//Creating the chest.logs file if it doesn't exist//
		if(config.getBoolean("log-chest-openings", true)){
			if(!chestLogs.exists()){
				try{
					chestLogs.createNewFile();
					log.info("[SuperLog] Chest.logs file successfully created!");
				} catch(IOException e){
					log.severe("[SuperLog] An error occured creating the chest.logs file, here is the error:\n");
					e.printStackTrace();
				}
			}
		}
		
		//Setup Permissions//
		Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
		
		if(permissionsPlugin == null){
			log.info("[SuperLog] Permissions system not detected, defaulting to OP permissions.");
		} else{
			permissionHandler = ((Permissions) permissionsPlugin).getHandler();
		    log.info("[SuperLog] Found and will use plugin " + ((Permissions)permissionsPlugin).getDescription().getFullName());
		}
		
		//Event registering//
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.Normal, this);
		pdfile = getDescription();
		log.info("[" + pdfile.getFullName() + "] is now enabled!");
	}
	
	//Method to check if the player has permission to do something//
	public static boolean hasPermission(Player player, String permission){
		if(permissionHandler == null){
			return player.isOp();
		} else{
			return permissionHandler.has(player, permission);
		}
	}
	
	//Method to write the properties to the config.yml file//
	public void writeConfig(){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, true));
			writer.write("log-block-placement: true\nlog-block-breaking: true\nlog-chest-openings: true\nlog-bucket-emptying: true");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch(IOException e){
			log.severe("[SuperLog] An error occured writing to the config file.");
		}
	}
}
