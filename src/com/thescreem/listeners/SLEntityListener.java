package com.thescreem.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;

import com.thescreem.SuperLog;
import com.thescreem.util.API;
import com.thescreem.util.VAR;

public class SLEntityListener extends EntityListener {

	public static SuperLog plugin;
	public SLEntityListener(SuperLog instance){
		plugin = instance;
	}

	public void onPaintingBreak(PaintingBreakEvent event){
		if(VAR.config.getBoolean("log-block-breaking", true) && event instanceof PaintingBreakByEntityEvent){
			Painting painting = event.getPainting();
			Location paintingLoc = painting.getLocation();
			Player loggedPlayer = (Player) ((PaintingBreakByEntityEvent) event).getRemover();
			
			API.checkBlockFileSize();
			
			API.logToBlockLogs("[" + API.getDate() + "] PAINTING destroyed by: " + loggedPlayer.getName()
			+ " at [X: " + paintingLoc.getX() + " Y: " + paintingLoc.getY() + " Z: " + paintingLoc.getZ() + "]");
			
			if(VAR.config.getBoolean("create-individual-player-logs", false)){
				API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] PAINTING destroyed by: " + loggedPlayer.getName()
				+ " at [X: " + paintingLoc.getX() + " Y: " + paintingLoc.getY() + " Z: " + paintingLoc.getZ() + "]");
			}
			
			if(VAR.MSUsers.contains(loggedPlayer.getName())){
				event.setCancelled(true);
				((PaintingBreakByEntityEvent) event).setCancelled(true);
			}
		}
	}

	public void onPaintingPlace(PaintingPlaceEvent event){
		if(VAR.config.getBoolean("log-block-placement", true)){
			Player loggedPlayer = event.getPlayer();
			Location paintingLoc = event.getPainting().getLocation();
			
			API.checkBlockFileSize();
			
			API.logToBlockLogs("[" + API.getDate() + "] PAINTING placed by: " + loggedPlayer.getName()
			+ " at [X: " + paintingLoc.getX() + " Y: " + paintingLoc.getY() + " Z: " + paintingLoc.getZ() + "]");
			
			if(VAR.config.getBoolean("create-individual-player-logs", false)){
				API.logToPlayerLog(loggedPlayer, "[" + API.getDate() + "] PAINTING placed by: " + loggedPlayer.getName()
			+ " at [X: " + paintingLoc.getX() + " Y: " + paintingLoc.getY() + " Z: " + paintingLoc.getZ() + "]");
			}
		}
	}
}
