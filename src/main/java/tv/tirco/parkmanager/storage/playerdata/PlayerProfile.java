package tv.tirco.parkmanager.storage.playerdata;

import java.util.UUID;

public class PlayerProfile {

	private final String playerName;
	private UUID uuid;
	private boolean loaded;
	private String rideidentifier;
	private long rideStartTime;
	private boolean changed = false;
	//private volatile boolean changed;

	
	public PlayerProfile(String playerName, UUID uuid) {
		this.playerName = playerName;
		this.uuid = uuid;
		this.loaded = true;
		this.rideidentifier = "none";
		//this.changed = true; //Changed so we get an updated last login.
	}
	
	public void startRide(String identifier) {
		this.rideidentifier = identifier;
		this.rideStartTime = System.currentTimeMillis();
	}
	
	public void endRide() {
		this.rideidentifier = "none";
		this.rideStartTime = 0;
	}
	
	public String getRideIdentifier() {
		return rideidentifier;
	}
	
	public Long getRideStartTime() {
		return rideStartTime;
	}
	
	public boolean isInRide() {
		return !rideidentifier.equals("none");
	}

	public void save() {
		// TODO Auto-generated method stub
		
	}
	
	public void scheduleAsyncSave() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isChanged() {
		return changed;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public String getPlayerName() {
		return playerName;
	}


}
