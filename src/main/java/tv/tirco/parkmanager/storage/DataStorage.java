package tv.tirco.parkmanager.storage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.alias.Alias;
import tv.tirco.parkmanager.config.Rides;

public class DataStorage {

	private static DataStorage instance;
	
	List<Ride> rides;
	List<Alias> aliases;
	Inventory rideMenu; //Storing it here as it shouldn't change.
	
	
	public Inventory getRideMenu() {
		return getRideInventory();
	}
	
	private Inventory getRideInventory() {
		Inventory inv = rideMenu;
		
		if(inv == null) {
			inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&6&lAvailable Rides"));
			for(Ride r : DataStorage.getInstance().getRides()) {
				inv.addItem(r.getIcon());
			}
			setRideMenu(inv);
			return inv;
		} else {
			return inv;
		}
		
	}
	
	
	public void rebuildRideMenu() {
		Inventory inv = rideMenu;
		Rides.getInstance().saveRides();
		this.rides.clear();
		if(inv != null) {
			for(HumanEntity e :inv.getViewers()) {
				e.getOpenInventory().close();
				e.sendMessage("We had to close this inventory as it is being reloaded.");
			}
		}
		this.rideMenu = null;
		Rides.getInstance().loadKeys();
		inv = getRideInventory();
		
	}
	
	public void setRideMenu(Inventory menu) {
		this.rideMenu = menu;
	}
	
	public static DataStorage getInstance() {
		if (instance == null) {
			instance = new DataStorage();
		}

		return instance;
	}
	
	public DataStorage() {
		this.rides = new ArrayList<Ride>();
		this.aliases = new ArrayList<Alias>();
	}
	

	/**
	 * RIDES
	 */

	/**
	 * 
	 * @param identifier
	 * @return the Ride object
	 */
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
	
	/**
	 * ALIASES
	 */
	
	/**
	 * 
	 * @param identifier
	 * @return the Alias object
	 */
	public Alias getAlias(String identifier) {
		for(Alias a : aliases) {
			if(a.getIdentifier().equalsIgnoreCase(identifier)) {
				return a;
			}
		}
		return null;
	}
	
	public void addAlias(Alias alias) {
		if(aliases.contains(alias)) {
			aliases.remove(alias);
		}
		
		if(getRide(alias.getIdentifier()) != null) {
			aliases.remove(getAlias(alias.getIdentifier()));
		}
		
		aliases.add(alias);
		
	}

	public List<Alias> getAliases() {
		return aliases;
	}


}
