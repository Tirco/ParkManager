package tv.tirco.parkmanager.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.alias.Alias;

public class DataStorage {

	private static DataStorage instance;
	
	List<Ride> rides;
	List<Alias> aliases;
	Inventory rideMenu; //Storing it here as it shouldn't change.
	HashMap<UUID, List<ItemStack>> owedItems; //Owed Items - Items that are supposed to be given out to players.
	Boolean owedItemsChanged = false;
	HashMap<String, ItemStack> storedItems; //Stored Items - Items that can be handed out with commands.
	Boolean storedItemsChanged = false;
	Map<String,Location> adminwarps;
	Boolean adminWarpsChanged = false;
	
	
	public Inventory getRideMenu() {
		return getRideInventory();
	}
	
	private Inventory getRideInventory() {
		Inventory inv = rideMenu;
		
		if(inv == null) {
			inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&6&lAvailable Rides"));
			for(Ride r : rides) {
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
		//RidesConfig.getInstance().saveRides();
		if(inv != null) {
			for (HumanEntity viewer : new ArrayList<>(inv.getViewers())) {
			    viewer.closeInventory();
				viewer.sendMessage("We had to close this inventory as it is being reloaded.");
			}
			inv.clear();
		}
		this.rideMenu = null;
		this.rideMenu = getRideInventory();
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
		this.owedItems = new HashMap<UUID, List<ItemStack>>();
		this.storedItems = new HashMap<String,ItemStack>();
		this.adminwarps = new HashMap<String,Location>();
	}
	
	public void setAdminWarps(HashMap<String,Location> warps) {
		this.adminwarps = warps;
	}
	
	public Map<String, Location> getAdminWarps() {
		return adminwarps;
	}
	
	public Location getWarp(String name) {
		return adminwarps.get(name);
	}
	
	public void setWarp(String name, Location loc) {
		adminwarps.put(name, loc);
		adminWarpsChanged = true;
	}
	
	public void delWarp(String name) {
		adminwarps.remove(name);
		adminWarpsChanged = true;
	}
	
	public boolean isAdminWarpsChanged() {
		return adminWarpsChanged;
	}
	
	public void setAdminWarpsChanged(boolean state) {
		this.adminWarpsChanged = state;
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

	
	public void addPlayerOwedItems(UUID uniqueId, List<ItemStack> items) {
		if(this.owedItems.containsKey(uniqueId)) {
			List<ItemStack> newItems = owedItems.get(uniqueId);
			newItems.addAll(items);
			owedItems.put(uniqueId, newItems);
			return;
		} else {
			owedItems.put(uniqueId, items);
		}
	}
	
	public void setPlayerOwedItems(UUID uniqueId, List<ItemStack> items) {
		this.owedItemsChanged = true;
		owedItems.put(uniqueId, items);
	}
	
	public void addPlayerOwedItem(UUID uniqueId, ItemStack item) {
		this.owedItemsChanged = true;
		List<ItemStack> items;
		if(this.owedItems.containsKey(uniqueId)) {
			items = owedItems.get(uniqueId);
		} else {
			items = new ArrayList<ItemStack>();
		}
		items.add(item);
		owedItems.put(uniqueId, items);
	}
	
	public void clearPlayerOwedItems(UUID uniqueId) {
		this.owedItemsChanged = true;
		if(this.owedItems.containsKey(uniqueId)) {
			this.owedItems.remove(uniqueId);
		}
	}
	
	public List<ItemStack> owedItems(UUID uniqueId) {
		if(this.owedItems.containsKey(uniqueId)) {
			return owedItems.get(uniqueId);
		} else {
			return new ArrayList<ItemStack>();
		}
	}

	public boolean isOwedItems(UUID uuid) {
		return owedItems.containsKey(uuid);
	}
	public boolean isOwedItemsChanged() {
		return owedItemsChanged;
	}

	public HashMap<UUID, List<ItemStack>> getAllOwedItems() {
		return owedItems;
	}

	//Stored Items
	public void setStoredItems(HashMap<String, ItemStack> storedItems) {
		this.storedItems = storedItems;
		this.storedItemsChanged = true;
	}
	
	/**
	 * Store an item in the system.
	 * @param identifier - The name of the item. Will be made lowercase.
	 * @param item - The item to store.
	 * @return True if it replaced an item, false if no item was set.
	 */
	public boolean storeItem(String identifier, ItemStack item) {
		this.storedItemsChanged = true;
		return this.storedItems.put(identifier.toLowerCase(), item) != null;
		
	}
	
	public boolean isStoredItem(String identifier) {
		return this.storedItems.containsKey(identifier.toLowerCase());
	}
	
	/**
	 * Delete a stored item.
	 * @param identifier
	 * @return false if item didn't exist, true if it exsisted.
	 */
	public boolean removeStoredItem(String identifier) {
		this.storedItemsChanged = true;
		return this.storedItems.remove(identifier.toLowerCase()) != null;
	}
	
	public ItemStack getStoredItem(String identifier) {
		ItemStack item = this.storedItems.get(identifier.toLowerCase());
		if(item == null) {
			return null;
		} else {
			return item.clone();
		}
	}

	public Set<String> getStoredItemNames() {
		return storedItems.keySet();
	}
	
	public boolean isStoredItemsChanged() {
		return storedItemsChanged;
	}
	
	public void setStoredItemsChanged(boolean state) {
		this.storedItemsChanged = state;
	}

	public void setOwedItemsChanged(boolean b) {
		this.owedItemsChanged = b;
	}

	public HashMap<String, ItemStack> getAllStoredItems() {
		return this.storedItems;
	}

	
	public boolean isWarp(String string) {
		return adminwarps.containsKey(string);
	}


}
