package tv.tirco.parkmanager.storage;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class DataStorage {

	private static DataStorage instance;
	
	HashMap<UUID,Integer> rideID;
	HashMap<UUID,Long> rideStartTime;
	
	public static DataStorage getInstance() {
		if (instance == null) {
			instance = new DataStorage();
		}

		return instance;
	}
	
	public DataStorage() {
		this.rideID = new HashMap<UUID,Integer>();
		this.rideStartTime = new HashMap<UUID,Long>();
	}
	
	
	public boolean isInRide(Player p) {
		return rideID.containsKey(p.getUniqueId());
	}
	
	public void startRide(Player p, int id) {
		this.rideID.put(p.getUniqueId(), id);
		this.rideStartTime.put(p.getUniqueId(), System.currentTimeMillis());
	}
	
	public long getStartTime(Player p) {
		long time = 0;
		if(this.rideStartTime.containsKey(p.getUniqueId())) {
			time = rideStartTime.get(p.getUniqueId());
		}
		return time;
	}
	
	public void clearPlayer(Player p) {
		if(this.rideStartTime.containsKey(p.getUniqueId())) {
			this.rideStartTime.remove(p.getUniqueId());
		}
		if(this.rideID.containsKey(p.getUniqueId())) {
			this.rideID.remove(p.getUniqueId());
		}
	}
}
