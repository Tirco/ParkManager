package tv.tirco.parkmanager.storage;

import org.bukkit.entity.Player;

import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

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
			pData.startRide(identifier);
			Long startTime = pData.getStartTime();
			Long endTime = System.currentTimeMillis();
			int minutes = (int) ((endTime - startTime) * 1000);
			double payout = defaultPayPerMinute * minutes;
			
			player.sendMessage("Test - Payout: " + payout);
			//TODO Send message
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
