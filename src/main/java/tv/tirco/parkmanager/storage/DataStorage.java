package tv.tirco.parkmanager.storage;

import java.util.ArrayList;
import java.util.List;

public class DataStorage {

	private static DataStorage instance;
	
	List<Ride> rides;
	
	public static DataStorage getInstance() {
		if (instance == null) {
			instance = new DataStorage();
		}

		return instance;
	}
	
	public DataStorage() {
		this.rides = new ArrayList<Ride>();
	}
	
	public Ride getRide(String identifier) {
		for(Ride r : rides) {
			if(r.getIdentifier().equalsIgnoreCase(identifier)) {
				return r;
			}
		}
		
		return null;
	}

	public void addRide(Ride ride) {
		if(rides.contains(ride)) {
			rides.remove(ride);
		}
		
		if(getRide(ride.getIdentifier()) != null) {
			rides.remove(getRide(ride.getIdentifier()));
		}
		
		rides.add(ride);
		
	}
	
	public List<Ride> getRides() {
		return rides;
	}
}
