package com.thescreem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SLBlockListener extends BlockListener {
	
	public SuperLog plugin;
	public SLBlockListener(SuperLog instance){
		plugin = instance;
	}
	
	public String date;
	
	//YOU PLACED A BLOCK?! HOW DARE YOU! YOU'RE GETTING LOGGED!//
	@SuppressWarnings("static-access")
	public void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock();
		
		//Is 'log-block-placement' true in the config.yml? If so, log it!//
		if(plugin.config.getBoolean("log-block-placement", true)){
			Block loggedBlock = event.getBlock();
			Player loggedPlayer = event.getPlayer();
			getDate();
		
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter("plugins/SuperLog/block.logs", true));
				writer.write("[" + date + "] " + loggedBlock.getType() + " placed by: " + loggedPlayer.getName() + " at [X: " + loggedBlock.getX() + " Y: " + loggedBlock.getY() + " Z: " + loggedBlock.getZ() + "]");
				writer.newLine();
				writer.flush();
				writer.close();
			} catch(IOException e){
				plugin.log.severe("[SuperLog] An error occured logging a player placing a block. Here is the error:\n");
				e.printStackTrace();
			}
		}
		
		//If 'send-message-when-TNT-is-placed' is true in the config.yml file, then send a message
		//that blabla player has placed TNT to everyone who has the permission node stated
		if(plugin.config.getBoolean("send-message-when-TNT-is-placed", false)){
			if(block.getType() == Material.TNT){
				Player player = event.getPlayer();
				for(Player person : plugin.getServer().getOnlinePlayers()){
					if(plugin.hasPermission(person, "superlog.notify.tnt.placement")){
						person.sendMessage("§6[SuperLog] " + player.getName() + " has just placed some TNT!");
					}
				}
			}
		}
	}
	
	//OH NOW YOU BROKE A BLOCK?! HOW DARE YOU! YOU'RE GETTING LOGGED AGAIN!//
	public void onBlockBreak(BlockBreakEvent event){
		
		//Is 'log-block-breaking' true in the config.yml? If so, log it!//
		if(plugin.config.getBoolean("log-block-breaking", true)){
			Block block = event.getBlock();
			Player player = event.getPlayer();
			getDate();
		
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter("plugins/SuperLog/block.logs", true));
				writer.write("[" + date + "] " + block.getType() + " destroyed by: " + player.getName() + " at [X: " + block.getX() + " Y: " + block.getY() + " Z: " + block.getZ() + "]");
				writer.newLine();
				writer.flush();
				writer.close();
			
			} catch(IOException e){
				plugin.log.severe("[SuperLog] An error occured logging a player breaking a block. Here is the error:\n");
				e.printStackTrace();
			}
		}
	}
	
	//Method to get the date so the logs can show the date//
	public void getDate(){
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH + 1);
		date = Integer.toString(month);
		date += "/";
		date += c.get(Calendar.DAY_OF_MONTH) + "/";
		date += c.get(Calendar.YEAR) + " ";
		date += c.get(Calendar.HOUR_OF_DAY) + ":";
		date += c.get(Calendar.MINUTE) + ".";
		date += c.get(Calendar.SECOND);
	}
}
