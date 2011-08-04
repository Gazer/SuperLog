package com.thescreem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class SLPlayerListener extends PlayerListener {
	
	public SuperLog plugin;
	public SLPlayerListener(SuperLog instance){
		plugin = instance;
	}
	
	public String date;
	
	//I haz interactions//
	@SuppressWarnings("static-access")
	public void onPlayerInteract(PlayerInteractEvent event){
		
		//Did the player right click a block?//
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player player = event.getPlayer();
			ItemStack item_in_hand = player.getItemInHand();
			
			//Do they want to log chest openings?//
			if(plugin.config.getBoolean("log-chest-openings", true)){
				Player griefer = event.getPlayer();
				Block block = event.getClickedBlock();
			
				//Is that block a chest? If so, log the damn stealer!//
				if(block.getType() == Material.CHEST){
					getDate();
				
					try{
						BufferedWriter writer = new BufferedWriter(new FileWriter("plugins/SuperLog/chest.logs", true));
						writer.write("[" + date + "] Chest opened by: " + griefer.getName() + " at [X: " + block.getX() + " Y: " + block.getY() + " Z: " + block.getZ() + "]");
						writer.newLine();
						writer.flush();
						writer.close();
						
					} catch(IOException e){
						plugin.log.severe("[SuperLog] An error occured logging a chest opening. Here is the error:\n");
						e.printStackTrace();
					}
				}
			}
			
			//If 'send-message-when-Flint_and_Steel-is-used' is true in the config.yml file, then send a message
			//that blabla player has used flint and steel to everyone who has the permission node stated
			if(plugin.config.getBoolean("send-message-when-Flint_and_Steel-is-used", false)){
				if(item_in_hand.getType() == Material.FLINT_AND_STEEL){
					Player firestarter = event.getPlayer();
					
					for(Player person : plugin.getServer().getOnlinePlayers()){
						if(plugin.hasPermission(person, "superlog.notify.flintandsteel")){
							person.sendMessage("§6[SuperLog] " + firestarter.getName() + " has just used flint and steel!");
						}
					}
				}
			}
			
			//If 'send-message-when-TNT-is-ignited' is true in the config.yml file, then send a message
			//that blabla player has ignited TNT to everyone who has the permission node stated
			if(plugin.config.getBoolean("send-message-when-TNT-is-ignited", false)){
				if(item_in_hand.getType() == Material.FLINT_AND_STEEL && event.getClickedBlock().getType() == Material.TNT){
					Player exploder = event.getPlayer();
					
					for(Player person : plugin.getServer().getOnlinePlayers()){
						if(plugin.hasPermission(person, "superlog.notify.tnt.ignition")){
							person.sendMessage("§6[SuperLog] " + exploder.getName() + " has just ignited some TNT!");
						}
					}
				}
			}
		}
	}
	
	//Oh noes! I spilled my lava and water!//
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
		if(plugin.config.getBoolean("log-bucket-emptying", true)){
			Player player = event.getPlayer();
			Block location = event.getBlockClicked();
		
			//Is the emptied bucket a lava bucket?//
			if(event.getBucket() == Material.LAVA_BUCKET){
				getDate();
				try{
					BufferedWriter writer = new BufferedWriter(new FileWriter("plugins/SuperLog/block.logs", true));
					writer.write("[" + date + "] Lava bucket emptied by: " + player.getName() + " at [X: " + location.getX() + " Y: " + location.getY() + " Z: " + location.getZ() + "]");
					writer.newLine();
					writer.flush();
					writer.close();
					
				} catch(IOException e){
					plugin.log.severe("[SuperLog] An error occured logging a player emptying a lava bucket. Here is the error:\n");
					e.printStackTrace();
				}
			}
		
			//Is the emptied bucket a water bucket?//
			if(event.getBucket() == Material.WATER_BUCKET){
				getDate();
				try{
					BufferedWriter writer = new BufferedWriter(new FileWriter("plugins/SuperLog/block.logs", true));
					writer.write("[" + date + "] Water bucket emptied by: " + player.getName() + " at [X: " + location.getX() + " Y: " + location.getY() + " Z: " + location.getZ() + "]");
					writer.newLine();
					writer.flush();
					writer.close();
					
				} catch(IOException e){
					plugin.log.severe("[SuperLog] An error occured logging a player emptying a water bucket. Here is the error:\n");
					e.printStackTrace();
				}
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
