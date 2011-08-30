package com.thescreem.util;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;

public class VAR {
	
	//The logger\\
	public static Logger log = Logger.getLogger("Minecraft");
	
	//Files\\
	public static Configuration config;
	
	public static String mainDirectory = "plugins/SuperLog/";
	
	public static File playerLogsFolder = new File(mainDirectory + "/Player_Logs");
	
	public static File blockLogsFolder = new File(mainDirectory + "Block_Logs/");
	public static File blockLogs = new File(mainDirectory + "Block_Logs/" +  "block.logs");
	
	public static File chestLogsFolder = new File(mainDirectory + "Chest_Logs/");
	public static File chestLogs = new File(mainDirectory + "Chest_Logs/" + "chest.logs");
	
	public static File commandLogsFolder = new File(mainDirectory + "Command_Logs/");
	public static File commandLogs = new File(mainDirectory + "Command_Logs/" + "command.logs");
	
	public static File configFile = new File(mainDirectory + "config.yml");
	
	//ArrayLists\\
	public static ArrayList<String> blockChanges = new ArrayList<String>();
	public static ArrayList<String> chestChanges = new ArrayList<String>();
	public static ArrayList<String> MSUsers = new ArrayList<String>();
	
	//Permissions\\
	public static PermissionHandler permissionHandler;
}
