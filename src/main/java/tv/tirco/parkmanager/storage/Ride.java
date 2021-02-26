package tv.tirco.parkmanager.storage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.MessageHandler;

public class Ride {
	
	String identifier;
	double maxPayout;
	String name;
	String description;
	double defaultPayPerMinute;
	boolean changed = false;
	//Location warp
	//ItemStack icon

	/**
	 * 
	 * @param identifier - Name of ride, internal use only.
	 * @param maxPayout - How much can the ride pay, max? Not counting multipliers.
	 * @param name - The Name of the ride that players will see.
	 * @param description - The description of the ride, that players will see.
	 * @param defaultPayPerMinute - How much does it pay pr minute?
	 * @param changed - Is this changed / new?
	 */
	public Ride(String identifier, double maxPayout, String name, String description, Double defaultPayPerMinute, boolean changed){
		this.identifier = identifier;
		this.maxPayout = maxPayout;
		this.name = name;
		this.description = description;
		this.defaultPayPerMinute = defaultPayPerMinute;
		this.changed = changed;
	}
	
	public Ride(String identifier) {
		this.identifier = identifier;
		this.maxPayout = 100.0;
		this.name = identifier;
		this.description = "No description";
		this.defaultPayPerMinute = 1.00;
		this.changed = true;
	}

	public double getPayPerMinute() {
		return defaultPayPerMinute;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public double maxPayout() {
		return maxPayout;
	}
	
	public boolean changed() {
		return changed;
	}
	
	/*
	 * Ride handling
	 */
	
	public void start(Player player) {
		if(UserManager.hasPlayerDataKey(player)) {
			PlayerData pData = UserManager.getPlayer(player);
			pData.startRide(identifier);
		}
	}
	
	public void stop(Player player) {
		if(UserManager.hasPlayerDataKey(player)) {
			PlayerData pData = UserManager.getPlayer(player);
			
			//Check if ride is correct, to prevent abuse:
			if(!pData.getRideIdentifier().equalsIgnoreCase(identifier)) {

				player.sendMessage("An Error has occured: Attempted to end a ride the player was not on.");
				player.sendMessage("Please report this to an administrator.");
				MessageHandler.getInstance().log("RideEndError:");
				MessageHandler.getInstance().log("- Player: " + player.getName());
				MessageHandler.getInstance().log("- Players Tracked Ride: " + pData.getRideIdentifier());
				MessageHandler.getInstance().log("- Actual Ride: " + identifier);
				pData.endRide();
				return;
			}
			
			Long startTime = pData.getStartTime();
			if(startTime == 0) {
				return;
			}
			Long endTime = System.currentTimeMillis();
			long durationInMillis = ((endTime - startTime));
			int minutes = (int) durationInMillis/60000;

			double payout = defaultPayPerMinute * minutes;
			
			long second = (durationInMillis / 1000) % 60;
			long minute = (durationInMillis / (1000 * 60)) % 60;
			long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

			String duration = String.format("%02d:%02d:%02d", hour, minute, second);
			
			player.sendMessage("Ride Ended: " + ChatColor.translateAlternateColorCodes('&', name));
			player.sendMessage("Test - Payout: " + payout + " (" + minutes + " minutes)");
			player.sendMessage("Duration: " + duration);
			//TODO Send message
			
			pData.endRide();
		}

		
		//pData.endRide;
		
		
	}

	
	
	/*
	 * Setters
	 */
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public void setPayPerMinute(double newPayout) {
		this.defaultPayPerMinute = newPayout;
	}
	
	public void setDescription(String newDescription) {
		this.description = newDescription;
	}
	
	public void setMaxPayout(double maxPayout) {
		this.maxPayout = maxPayout;
	}
	
	
}