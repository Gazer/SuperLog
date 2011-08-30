package com.thescreem;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;

import com.thescreem.listeners.SLBlockListener;
import com.thescreem.listeners.SLEntityListener;
import com.thescreem.listeners.SLPlayerListener;
import com.thescreem.util.API;
import com.thescreem.util.VAR;

public class SuperLog extends JavaPlugin {
	
	PluginDescriptionFile pdfile;
	
	private final SLPlayerListener playerListener = new SLPlayerListener(this);
	private final SLBlockListener blockListener = new SLBlockListener(this);
	private final SLEntityListener entityListener = new SLEntityListener(this);
	
	@Override
	public void onDisable() {
		pdfile = getDescription();
		VAR.log.info("[" + pdfile.getFullName() + "] is now disabled!");
	}

	@Override
	public void onEnable() {
		VAR.config = getConfiguration();
		
		new File(VAR.mainDirectory).mkdir();
		VAR.blockLogsFolder.mkdir();
		VAR.chestLogsFolder.mkdir();
		VAR.commandLogsFolder.mkdir();
		
		//Config file\\
		if(!VAR.configFile.exists()){
			try{
				VAR.configFile.createNewFile();
				API.checkConfig(VAR.config);
				VAR.log.info("[SuperLog] Config file successfully created!");
			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured creating the config.yml file, here is the error:\n");
				e.printStackTrace();
			}
		}
		
		//Creating the block.logs file if it doesn't exist\\
		if(!VAR.blockLogs.exists() && VAR.config.getBoolean("log-block-placement", true)
								   && VAR.config.getBoolean("log-block-breaking", true)){
			try{
				VAR.blockLogs.createNewFile();
				VAR.log.info("[SuperLog] Block.logs file successfully created!");
			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured creating the block.logs file, here is the error:\n");
				e.printStackTrace();
			}
		}
		
		//Creating the chest.logs file if it doesn't exist\\
		if(!VAR.chestLogs.exists() && VAR.config.getBoolean("log-chest-openings", true)){
			try{
				VAR.chestLogs.createNewFile();
				VAR.log.info("[SuperLog] Chest.logs file successfully created!");
			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured creating the chest.logs file, here is the error:\n");
				e.printStackTrace();
			}
		}
		
		//Creates the player folder if it doesn't exist\\
		if(VAR.config.getBoolean("make-individual-player-logs", false)){
			try{
				VAR.playerLogsFolder.mkdir();
			} catch(Exception e){
				VAR.log.severe("[SuperLog] An error occured creating the PlayerLogs foler, here is the error:\n");
				e.printStackTrace();
			}
		}
		
		//Creates the commands.logs file if it doesn't exist\\
		if(!VAR.commandLogs.exists() && VAR.config.getBoolean("log-commands", true)){
			try{
				VAR.commandLogs.createNewFile();
			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured creating the command.logs file, here is the error:\n");
				e.printStackTrace();
			}
		}
		
		//Start of permissions setup\\
		Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
		
		if(permissionsPlugin == null){
			VAR.log.info("[SuperLog] Permissions system not detected, defaulting to OP permissions.");
		} else{
			VAR.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
		    VAR.log.info("[SuperLog] Found and will use plugin " + ((Permissions)permissionsPlugin).getDescription().getFullName());
		}
		//End of permissions setup\\
		
		//Event registering\\
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PAINTING_BREAK, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PAINTING_PLACE, entityListener, Event.Priority.Normal, this);
		
		//Checks the config to make sure all the properties are in it\\
		API.checkConfig(VAR.config);
		
		pdfile = getDescription();
		VAR.log.info("[" + pdfile.getFullName() + "] is now enabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		String[] split = args;
		String cmd = command.getName().toLowerCase();
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			if(cmd.equals("magicstick") || cmd.equals("ms") && (player.hasPermission("SuperLog.MagicStick") || API.hasPermission(player, "SuperLog.MagicStick"))){
				API.toggleMagicStick(player);
				return true;
			}
			
			if((cmd.equals("sl") || cmd.equals("superlog")) && split[0].equalsIgnoreCase("config") && split.length == 1 && (player.hasPermission("SuperLog.Config") || API.hasPermission(player, "SuperLog.Config"))){
				player.sendMessage("§3----------§6SuperLog Config Options Page 1§3----------");
				player.sendMessage("§6Logging block placement: §3" + VAR.config.getBoolean("log-block-placement", true));
				player.sendMessage("§6Logging block breaking: §3" + VAR.config.getBoolean("log-block-breaking", true));
				player.sendMessage("§6Logging chest openings: §3" + VAR.config.getBoolean("log-chest-openings", true));
				player.sendMessage("§6Logging bucket emptying/spilling: §3" + VAR.config.getBoolean("log-bucket-emptying-and-filling", true));
				player.sendMessage("§6Logging commands: §3" + VAR.config.getBoolean("log-commands", true));
				player.sendMessage("§6Message sent when TNT is placed: §3" + VAR.config.getBoolean("send-message-when-TNT-is-placed", false));
				player.sendMessage("§6Message sent when TNT is ignited: §3" + VAR.config.getBoolean("send-message-when-TNT-is-ignited", false));
				player.sendMessage("§6Message sent when Flint and Steel is used: §3" + VAR.config.getBoolean("send-message-when-Flint_and_Steel-is-used", false));
				player.sendMessage("§cUse /sl config 2 or /superlog config 2 to go to page two.");
				return true;
			}
			
			if((cmd.equals("sl") || cmd.equals("superlog")) && split[0].equalsIgnoreCase("config") && split[1].equalsIgnoreCase("2") && split.length == 2 && (player.hasPermission("SuperLog.Config") || API.hasPermission(player, "SuperLog.Config"))){
				player.sendMessage("§3----------§6SuperLog Config Options Page 2§3----------");
				player.sendMessage("§6Individual player logs being created: §3" + VAR.config.getBoolean("create-individual-player-logs", true));
				player.sendMessage("§6block.logs file size until a new one is created: §3" + VAR.config.getDouble("memory-in-MB-until-new-block-logs-file-is-created", 50));
				player.sendMessage("§6chest.logs file size until a new one is created: §3" + VAR.config.getDouble("memory-in-MB-until-new-chest-logs-file-is-created", 50));
				player.sendMessage("§6commands.logs file size until a new one is created: §3" + VAR.config.getDouble("memory-in-MB-until-new-commands-logs-file-is-created", 50));
				player.sendMessage("§6[player].logs file size until a new one is created: §3" + VAR.config.getDouble("memory-in-MB-until-new-[player]-logs-file-is-created", 50));
				player.sendMessage("§cUse /sl config or /superlog config to go back to page one.");
				return true;
			}
			
		} else{
			VAR.log.info("[SuperLog] You must be a player to use this command!");
			return false;
		}
		return false;
	}
}
