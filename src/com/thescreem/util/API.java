package com.thescreem.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class API {
	
	/*
	 * This methods enables/disables the MagicStick
	 */
	public static void toggleMagicStick(Player player){
		if(VAR.MSUsers.contains(player.getName())){
			VAR.MSUsers.remove(player.getName());
			player.sendMessage("§6[SuperLog] MagicStick disabled.");
		} else{
			VAR.MSUsers.add(player.getName());
			player.sendMessage("§6[SuperLog] MagicStick enabled.");
		}
	}
	
	/*
	 * Checks if a player has permission to do something
	 */
	public static boolean hasPermission(Player player, String permission){
		if(VAR.permissionHandler == null){
			return player.isOp();
		} else{
			return VAR.permissionHandler.has(player, permission);
		}
	}
	
	/*
	 * Checks the config file to make sure all the
	 * properties are in it.
	 */
	public static void checkConfig(Configuration config){
		try{
			config.setProperty("log-block-placement", config.getBoolean("log-block-placement", true));
			config.setProperty("log-block-breaking", config.getBoolean("log-block-breaking", true));
			config.setProperty("log-chest-openings", config.getBoolean("log-chest-openings", true));
			config.setProperty("log-bucket-emptying-and-filling", config.getBoolean("log-bucket-emptying-and-filling", true));
			config.setProperty("log-commands", config.getBoolean("log-commands", true));
			config.setProperty("send-message-when-TNT-is-placed", config.getBoolean("send-message-when-TNT-is-placed", false));
			config.setProperty("send-message-when-TNT-is-ignited", config.getBoolean("send-message-when-TNT-is-ignited", false));
			config.setProperty("send-message-when-Flint_and_Steel-is-used", config.getBoolean("send-message-when-Flint_and_Steel-is-used", false));
			config.setProperty("create-individual-player-logs", config.getBoolean("create-individual-player-logs", false));
			config.setProperty("memory-in-MB-until-new-block-logs-file-is-created", config.getDouble("memory-in-MB-until-new-block-logs-file-is-created", 50.0));
			config.setProperty("memory-in-MB-until-new-chest-logs-file-is-created", config.getDouble("memory-in-MB-until-new-chest-logs-file-is-created", 50.0));
			config.setProperty("memory-in-MB-until-new-command-logs-file-is-created", config.getDouble("memory-in-MB-until-new-command-logs-file-is-created", 50.0));
			config.setProperty("memory-in-MB-until-new-[player]-logs-file-is-created", config.getDouble("memory-in-MB-until-new-[player]-logs-file-is-created", 50.0));
			config.save();
		} catch(Exception e){
			VAR.log.severe("[SuperLog] An error occured checking the config file. Here is the error:\n");
			e.printStackTrace();
		}
	}
	
	/*
	 * Gets the date
	 */
	public static String getDate(){
		String date;
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH) + 1;
		date = Integer.toString(month);
		date += "/";
		date += c.get(Calendar.DAY_OF_MONTH) + "/";
		date += c.get(Calendar.YEAR) + " ";
		date += c.get(Calendar.HOUR_OF_DAY) + ":";
		date += c.get(Calendar.MINUTE) + ".";
		date += c.get(Calendar.SECOND);
		return date;
	}
	
	/*
	 * Gets the partial date. (For the log files)
	 */
	public static String getPartialDate(){
		String date;
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH) + 1;
		date = Integer.toString(month);
		date += "_";
		date += c.get(Calendar.DAY_OF_MONTH) + "_";
		date += c.get(Calendar.YEAR) + " [";
		date += c.get(Calendar.HOUR_OF_DAY) + "_";
		date += c.get(Calendar.MINUTE) + "_";
		date += c.get(Calendar.SECOND) + "]";
		return date;
	}
	
	/*
	 * Logs to the block.logs file
	 */
	public static void logToBlockLogs(String message){
		checkBlockFileSize();
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(VAR.blockLogs, true));
			writer.write(message);
			writer.newLine();
			writer.flush();
			writer.close();
		
		} catch(IOException e){
			VAR.log.severe("[SuperLog] An error occured logging a player creating a change to a block. Here is the error:\n");
			e.printStackTrace();
		}
	}
	
	/*
	 * Logs to the chest.logs file
	 */
	public static void logToChestLogs(String message){
		checkChestFileSize();
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(VAR.chestLogs, true));
			writer.write(message);
			writer.newLine();
			writer.flush();
			writer.close();
		
		} catch(IOException e){
			VAR.log.severe("[SuperLog] An error occured logging a player opening a chest. Here is the error:\n");
			e.printStackTrace();
		}
	}
	
	/*
	 * Logs to the specified [player].log file
	 */
	public static void logToPlayerLog(Player player, String message){
		File playerFolder = new File(VAR.mainDirectory + "/Player_Logs/" + player.getName() + "/");
		if(playerFolder != null){
			for(File child : playerFolder.listFiles()){
				if(child.getName().contains(player.getName()) && child.length() < VAR.config.getDouble("memory-in-MB-until-new-[player]-logs-file-is-created", 50) * 1000000){
					try{
						BufferedWriter writer = new BufferedWriter(new FileWriter(child, true));
						writer.write(message);
						writer.newLine();
						writer.flush();
						writer.close();

					} catch(IOException e){
						VAR.log.severe("[SuperLog] An error occured logging to " + player.getName() + "\'s log. Here is the error:\n");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void logToCommandsLog(String message){
		checkCommandsFileSize();
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(VAR.commandLogs, true));
			writer.write(message);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch(IOException e){
			VAR.log.severe("[SuperLog] An error occured logging to the commands.logs file. Here is the error:\n");
			e.printStackTrace();
		}
	}
	
	/*
	 * Checks the size of the block.logs file
	 */
	public static void checkBlockFileSize(){
		if(VAR.blockLogs.length() >= VAR.config.getDouble("memory-in-MB-until-new-block-logs-file-is-created", 50) * 1000000){
			File newBlockLogs = new File(VAR.mainDirectory + "Block_Logs/" + "block" + "_" + getPartialDate() + ".logs");
			VAR.blockLogs.renameTo(newBlockLogs);
			
			try{
				newBlockLogs.createNewFile();
				VAR.log.info("[SuperLog] A new block.logs file has been successfully created!");
			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured creating a new block.logs file. Here is the error:\n");
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Checks the size of the chest.logs file
	 */
	public static void checkChestFileSize(){
		if(VAR.chestLogs.length() >= VAR.config.getDouble("memory-in-MB-until-new-chest-logs-file-is-created", 50) * 1000000){
			File newChestLogs = new File(VAR.mainDirectory + "Chest_Logs/" + "chest" + "_" + getPartialDate() + ".logs");
			VAR.chestLogs.renameTo(newChestLogs);
			
			try{
				newChestLogs.createNewFile();
				VAR.log.info("[SuperLog] A new chest.logs file has been successfully created!");
			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured creating a new chest.logs file. Here is the error:\n");
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Checks the size of the [player].log files
	 */
	public static void checkPlayerFileSize(){
		for(File child : VAR.playerLogsFolder.listFiles()){
			if(child.length() >= VAR.config.getDouble("memory-in-MB-until-new-[player]-logs-file-is-created", 50) * 1000000){
				File newPlayerLog = new File(VAR.mainDirectory + "Player_Logs/" + child.getName() + "/" + child.getName() + "_" + getPartialDate() + ".logs");
				child.renameTo(newPlayerLog);
				
				try{
					newPlayerLog.createNewFile();
					VAR.log.info("[SuperLog] A new player log file for " + child.getName() + " has been successfully created!");
				} catch(IOException e){
					VAR.log.severe("[SuperLog] An error occured creating a new player log file for " + child.getName() + ". Here is the error:\n");
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Checks the size of the command.logs file
	 */
	public static void checkCommandsFileSize(){
		if(VAR.commandLogs.length() >= VAR.config.getDouble("memory-in-MB-until-new-command-logs-file-is-created", 50) * 1000000){
			File newCommandLogs = new File(VAR.mainDirectory + "Command_Logs/" + "command" + "_" + getPartialDate() + ".logs");
			VAR.commandLogs.renameTo(newCommandLogs);
			
			try{
				newCommandLogs.createNewFile();
				VAR.log.info("[SuperLog] A new command.logs file has been successfully created!");
			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured creating a new command.logs file. Here is the error:\n");
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Formats a line from the block.logs file so
	 * it's usable
	 */
	public static String formatBlockLog(String string){
		String updatedLine1 = string.replaceAll(" placed by: ", "@PLACED@");
		String updatedLine2 = updatedLine1.replaceAll(" destroyed by: ", "@DESTROYED@");
		String updatedLine3 = updatedLine2.replace("[", "");
		String updatedLine4 = updatedLine3.replaceAll("] ", "@");
		String updatedLine5 = updatedLine4.replaceAll("]", "");
		String updatedLine6 = updatedLine5.replaceAll(" X: ", "@");
		String updatedLine7 = updatedLine6.replaceAll(" Y: ", ",");
		String updatedLine8 = updatedLine7.replaceAll(" Z: ", ",");
		String formattedBlockLine = updatedLine8.replaceAll(" at", "");
		return formattedBlockLine;
	}
	
	/*
	 * Formats a line from the chest.logs array so
	 * it's usable
	 */
	public static String formatChestLog(String string){
		String updatedLine1 = string.replaceAll(" opened by: ", "@");
		String updatedLine2 = updatedLine1.replace("[", "");
		String updatedLine3 = updatedLine2.replaceAll("] ", "@");
		String updatedLine4 = updatedLine3.replaceAll("]", "");
		String updatedLine5 = updatedLine4.replaceAll(" X: ", "@");
		String updatedLine6 = updatedLine5.replaceAll(" Y: ", ",");
		String updatedLine7 = updatedLine6.replaceAll(" Z: ", ",");
		String formattedChestLine = updatedLine7.replaceAll(" at", "");
		return formattedChestLine;
	}
	
	public static void createBlockLogsArray(){
		for(File child : VAR.blockLogsFolder.listFiles()){
			try{
				Scanner blocks = new Scanner(child);
				VAR.blockChanges.clear();

				while(blocks.hasNextLine()){
					String loggedLine = blocks.nextLine();
					String formattedLoggedLine = formatBlockLog(loggedLine);
					VAR.blockChanges.add(formattedLoggedLine);
				}

			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured reading and adding to the blockChanges array. Here is the error:\n");
				e.printStackTrace();
			}
		}
	}
	
	public static void createChestLogsArray(){
		for(File child : VAR.chestLogsFolder.listFiles()){
			try{
				Scanner chests = new Scanner(child);
				VAR.chestChanges.clear();
				
				while(chests.hasNextLine()){
					String loggedLine = chests.nextLine();
					String formattedLoggedLine = formatChestLog(loggedLine);
					VAR.chestChanges.add(formattedLoggedLine);
				}
			} catch(IOException e){
				VAR.log.severe("[SuperLog] An error occured reading and adding to the chestChanges array. Here is the error:\n");
				e.printStackTrace();
			}
		}
	}
}
