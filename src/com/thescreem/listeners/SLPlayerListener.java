package com.thescreem.listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;

import com.thescreem.SuperLog;
import com.thescreem.util.API;
import com.thescreem.util.VAR;

public class SLPlayerListener extends PlayerListener {

	public SuperLog plugin;
	public SLPlayerListener(SuperLog instance){
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player player = event.getPlayer();
			ItemStack item_in_hand = player.getItemInHand();

			if(VAR.config.getBoolean("log-chest-openings", true)){
				Player loggedPlayer = event.getPlayer();
				Block chest = event.getClickedBlock();
				if(chest.getType() == Material.CHEST){
					API.checkChestFileSize();
					
					API.logToChestLogs("[" + API.getDate() + "] Chest opened by: " + loggedPlayer.getName()
					+ " at [X: " + chest.getX() + " Y: " + chest.getY() + " Z: " + chest.getZ() + "]");
					
					if(VAR.config.getBoolean("create-individual-player-logs", false)){
						API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] Chest opened by: " + loggedPlayer.getName()
						+ " at [X: " + chest.getX() + " Y: " + chest.getY() + " Z: " + chest.getZ() + "]");
					}
				}
			}

			if(VAR.config.getBoolean("send-message-when-Flint_and_Steel-is-used", false)){
				if(item_in_hand.getType() == Material.FLINT_AND_STEEL){
					Player firestarter = event.getPlayer();

					for(Player person : plugin.getServer().getOnlinePlayers()){
						if((API.hasPermission(person, "superlog.notify.flintandsteel") || person.hasPermission("superlog.notify.flintandsteel"))
						&& firestarter.getName() != person.getName()){
							person.sendMessage("§6[SuperLog] " + firestarter.getName() + " has just used flint and steel!");
						}
					}
				}
			}

			if(VAR.config.getBoolean("send-message-when-TNT-is-ignited", false)){
				if(item_in_hand.getType() == Material.FLINT_AND_STEEL && event.getClickedBlock().getType() == Material.TNT){
					Player exploder = event.getPlayer();

					for(Player person : plugin.getServer().getOnlinePlayers()){
						if((API.hasPermission(person, "superlog.notify.tnt.ignition") || person.hasPermission("superlog.notify.tnt.ignition"))
						&& exploder.getName() != person.getName()){
							person.sendMessage("§6[SuperLog] " + exploder.getName() + " has just ignited some TNT!");
						}
					}
				}
			}
		}

		if(event.getAction() == Action.LEFT_CLICK_BLOCK){
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();
			
			if(player.getItemInHand().getType() == Material.STICK
			&& API.hasPermission(player, "SuperLog.MagicStick")
			&& VAR.MSUsers.contains(player.getName())){
				
				event.setCancelled(true);
				
				
				if(block.getType() != Material.CHEST){
					API.createBlockLogsArray();
					
					String X = Integer.toString((int) block.getLocation().getX());
					String Y = Integer.toString((int) block.getLocation().getY());
					String Z = Integer.toString((int) block.getLocation().getZ());
					ArrayList<String> changes = new ArrayList<String>();

					for(int i = 0; i < VAR.blockChanges.size(); i++){
						String logLine = VAR.blockChanges.get(i);
						String[] splitLogLine = logLine.split("@");
						if(splitLogLine.length == 5){
							String[] coordinates = splitLogLine[4].split(",");
							//VAR.log.info(splitLogLine[0] + ", " + splitLogLine[2]
							//+ ", "+ splitLogLine[3] + ", " + splitLogLine[4]);
							//VAR.log.info(logLine);

							if(coordinates[0].equals(X)
							&& coordinates[1].equals(Y)
							&& coordinates[2].equals(Z)){
								player.sendMessage("§3[" + splitLogLine[0] + "] §c" + splitLogLine[3] + "§6 has §c"
								+ splitLogLine[2].toLowerCase() + "§6 a §c" + splitLogLine[1].toLowerCase().replaceAll("_", " ") + " §6here.");
								changes.add("blurp");
							}
						}
						
						if(i == VAR.blockChanges.size() - 1 && changes.size() == 0){
							player.sendMessage("§6[SuperLog] Nobody has modified this block.");
						}
					}
				} else{
					API.createChestLogsArray();
					
					String X = Integer.toString((int) block.getLocation().getX());
					String Y = Integer.toString((int) block.getLocation().getY());
					String Z = Integer.toString((int) block.getLocation().getZ());
					
					ArrayList<String> changes = new ArrayList<String>();
					
					for(int i = 0; i < VAR.chestChanges.size(); i++){
						String logLine = VAR.chestChanges.get(i);
						String[] splitLogLine = logLine.split("@");
						if(splitLogLine.length == 4){
							String[] coordinates = splitLogLine[3].split(",");
							
							if(coordinates[0].equals(X)
							&& coordinates[1].equals(Y)
							&& coordinates[2].equals(Z)){
								player.sendMessage("§3[" + splitLogLine[0] + "] §c" + splitLogLine[2]
								+ "§6 has opened this chest.");
								changes.add("blurp");
							}
						}
						
						if(i == VAR.chestChanges.size() - 1 && changes.size() == 0){
							player.sendMessage("§6[SuperLog] Nobody has opened this chest.");
						}
					}
				}
			}
		}
	}

	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
		if(VAR.config.getBoolean("log-bucket-emptying-and-filling", true)){
			Player loggedPlayer = event.getPlayer();
			Block liquid = event.getBlockClicked();

			if(event.getBucket() == Material.LAVA_BUCKET){
				API.checkBlockFileSize();
				
				API.logToBlockLogs("[" + API.getDate() + "] Lava bucket emptied by: " + loggedPlayer.getName()
				+ " at [X: " + liquid.getX() + " Y: " + liquid.getY() + " Z: " + liquid.getZ() + "]");
				
				if(VAR.config.getBoolean("create-individual-player-logs", false)){
					API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] Lava bucket emptied by: " + loggedPlayer.getName()
					+ " at [X: " + liquid.getX() + " Y: " + liquid.getY() + " Z: " + liquid.getZ() + "]");
				}
			}

			if(event.getBucket() == Material.WATER_BUCKET){
				API.checkBlockFileSize();
				
				API.logToBlockLogs("[" + API.getDate() + "] Water bucket emptied by: " + loggedPlayer.getName()
				+ " at [X: " + liquid.getX() + " Y: " + liquid.getY() + " Z: " + liquid.getZ() + "]");
				
				if(VAR.config.getBoolean("create-individual-player-logs", false)){
					API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] Water bucket emptied by: " + loggedPlayer.getName()
					+ " at [X: " + liquid.getX() + " Y: " + liquid.getY() + " Z: " + liquid.getZ() + "]");
				}
			}
		}
	}

	public void onPlayerBucketFill(PlayerBucketFillEvent event){
		if(VAR.config.getBoolean("log-bucket-emptying-and-filling", true)){
			Player loggedPlayer = event.getPlayer();
			Block liquid = event.getBlockClicked();

			if(event.getBucket() == Material.LAVA_BUCKET){
				API.checkBlockFileSize();
				
				API.logToBlockLogs("[" + API.getDate() + "] Lava bucket filled by: " + loggedPlayer.getName()
				+ " at [X: " + liquid.getX() + " Y: " + liquid.getY() + " Z: " + liquid.getZ() + "]");
				
				if(VAR.config.getBoolean("create-individual-player-logs", false)){
					API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] Lava bucket filled by: " + loggedPlayer.getName()
					+ " at [X: " + liquid.getX() + " Y: " + liquid.getY() + " Z: " + liquid.getZ() + "]");
				}
			}

			if(event.getBucket() == Material.WATER_BUCKET){
				API.checkBlockFileSize();
				
				API.logToBlockLogs("[" + API.getDate() + "] Water bucket filled by: " + loggedPlayer.getName()
				+ " at [X: " + liquid.getX() + " Y: " + liquid.getY() + " Z: " + liquid.getZ() + "]");
				
				if(VAR.config.getBoolean("create-individual-player-logs", false)){
					API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] Water bucket filled by: " + loggedPlayer.getName()
					+ " at [X: " + liquid.getX() + " Y: " + liquid.getY() + " Z: " + liquid.getZ() + "]");
				}
			}
		}
	}
	
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(VAR.config.getBoolean("log-commands", true)){
			Player loggedPlayer = event.getPlayer();
			String cmd = event.getMessage();
			
			API.checkCommandsFileSize();
			
			API.logToCommandsLog("[" + API.getDate() + "] " + loggedPlayer.getName() + " has used the command: " + cmd);
			
			if(VAR.config.getBoolean("create-individual-player-logs", false)){
				API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] " + loggedPlayer.getName() + " has used the command: " + cmd);
			}
		}
	}

	public void onPlayerLogin(PlayerLoginEvent event){
		if(VAR.config.getBoolean("create-individual-player-logs", false)){
			Player player = event.getPlayer();
			String name = player.getName();
			File playerFolder = new File(VAR.mainDirectory + "Player_Logs/" + name);
			File playerLog = new File(VAR.mainDirectory + "Player_Logs/" + name + "/" + name + ".logs");
			
			playerFolder.mkdir();
			
			if(!playerLog.exists()){
				try{
					playerLog.createNewFile();
					VAR.log.info("[SuperLog] Player log for " + name + " successfully created!");
					
				} catch(IOException e){
					VAR.log.severe("[SuperLog] An error occured creating a player log for " + name + ". Here is the error:\n");
					e.printStackTrace();
				}
			}
		}
	}
}
