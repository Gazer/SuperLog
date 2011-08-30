package com.thescreem.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.thescreem.SuperLog;
import com.thescreem.util.API;
import com.thescreem.util.VAR;

public class SLBlockListener extends BlockListener {
	
	public SuperLog plugin;
	public SLBlockListener(SuperLog instance){
		plugin = instance;
	}
	
	public void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock();
		
		if(VAR.config.getBoolean("log-block-placement", true) && block.getType() != Material.AIR){
			Block loggedBlock = event.getBlock();
			Player loggedPlayer = event.getPlayer();
			
			API.checkBlockFileSize();
			
			API.logToBlockLogs("[" + API.getDate() + "] " + loggedBlock.getType() + " placed by: " + loggedPlayer.getName()
			+ " at [X: " + loggedBlock.getX() + " Y: " + loggedBlock.getY() + " Z: " + loggedBlock.getZ() + "]");
			
			if(VAR.config.getBoolean("create-individual-player-logs", false)){
				API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] " + loggedBlock.getType() + " placed by: " + loggedPlayer.getName()
				+ " at [X: " + loggedBlock.getX() + " Y: " + loggedBlock.getY() + " Z: " + loggedBlock.getZ() + "]");
			}
		}
		
		if(VAR.config.getBoolean("send-message-when-TNT-is-placed", false)){
			if(block.getType() == Material.TNT){
				Player loggedPlayer = event.getPlayer();
				
				for(Player person : plugin.getServer().getOnlinePlayers()){
					if((API.hasPermission(person, "superlog.notify.tnt.placement") || person.hasPermission("superlog.notify.tnt.placement"))
					&& loggedPlayer.getName() != person.getName()){
						person.sendMessage("§6[SuperLog] " + loggedPlayer.getName() + " has just placed some TNT!");
					}
				}
			}
		}
	}
	
	public void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		
		if(VAR.config.getBoolean("log-block-breaking", true) && block.getType() != Material.AIR){
			Block loggedBlock = event.getBlock();
			Player loggedPlayer = event.getPlayer();
			
			API.checkBlockFileSize();
			
			API.logToBlockLogs("[" + API.getDate() + "] " + loggedBlock.getType() + " destroyed by: " + loggedPlayer.getName()
			+ " at [X: " + loggedBlock.getX() + " Y: " + loggedBlock.getY() + " Z: " + loggedBlock.getZ() + "]");
			
			if(VAR.config.getBoolean("create-individual-player-logs", false)){
				API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] " + loggedBlock.getType() + " destroyed by: " + loggedPlayer.getName()
				+ " at [X: " + loggedBlock.getX() + " Y: " + loggedBlock.getY() + " Z: " + loggedBlock.getZ() + "]");
			}
		}
	}
}
