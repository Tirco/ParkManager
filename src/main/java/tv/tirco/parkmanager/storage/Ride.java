package tv.tirco.parkmanager.storage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.advancements.AdvancementUtil;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.MessageHandler;

public class Ride {
	
	String identifier;
	double maxPayout;
	String name;
	List<String> description;
	double defaultPayPerMinute;
	boolean changed = false;
	String warpName;
	ItemStack icon;
	double minPay = 2.00;
	boolean hasAdvancement = false;
	boolean enabled = false;

	/**
	 * 
	 * @param identifier - Name of ride, internal use only.
	 * @param maxPayout - How much can the ride pay, max? Not counting multipliers.
	 * @param name - The Name of the ride that players will see.
	 * @param description - The description of the ride, that players will see.
	 * @param defaultPayPerMinute - How much does it pay pr minute?
	 * @param changed - Is this changed / new?
	 */	
	public Ride(
			String identifier, double maxPayout, String name, List<String> description, 
			Double defaultPayPerMinute, String warp, boolean hasAdvancement, boolean changed, boolean enabled, 
			Material material, int modeldata){
		this.identifier = identifier;
		this.maxPayout = maxPayout;
		this.name = name;
		this.description = description;
		this.defaultPayPerMinute = defaultPayPerMinute;
		this.warpName = warp;
		this.hasAdvancement = hasAdvancement;
		this.changed = changed;
		this.enabled = enabled;
		this.icon = buildIcon(material, modeldata);
		
		if(this.icon == null) {
			MessageHandler.getInstance().log("Error: Icon for ride " + identifier + " was NULL");
		}
	}
	
	
	public ItemStack buildIcon(Material mat, int modeldata) {
			if(mat == null || mat.equals(Material.AIR)) {
				mat = Material.MINECART;
			}
			
			ItemStack item = new ItemStack(mat);
			NBTItem nbtItem = new NBTItem(item);
			nbtItem.setString("RideIdentifier", identifier);
			item = nbtItem.getItem();

			
			ItemMeta meta = item.getItemMeta();
			meta.setCustomModelData(modeldata);
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			List<String> LoreList = new ArrayList<String>();
			LoreList.add(ChatColor.translateAlternateColorCodes('&', (enabled ? "&aEnabled" : "&cDisabled")));
			LoreList.add("");
			for(String s : description) {
				LoreList.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			meta.setLore(LoreList);
			item.setItemMeta(meta);

			return item;
	}


	public Ride(String identifier) {
		this.identifier = identifier;
		this.maxPayout = 100.0;
		this.name = identifier;
		this.description = new ArrayList<String>();
		this.defaultPayPerMinute = 1.00;
		this.icon = new ItemStack(Material.MINECART);
		this.hasAdvancement = false;
		this.warpName = "Unknown";
		this.changed = true;
		this.enabled = false;
	}
	 
	public boolean hasAdvancement() {
		return hasAdvancement;
	}
	
	public void setAdvancementBoolean(boolean state) {
		this.hasAdvancement = state;
		this.changed = true;
	}
	 
	 

	public double getPayPerMinute() {
		return defaultPayPerMinute;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getDescription() {
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
			
			//Atleast pay them 1.
			if(payout < minPay) {
				payout = minPay;
			}
			
			long second = (durationInMillis / 1000) % 60;
			long minute = (durationInMillis / (1000 * 60)) % 60;
			long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

			String duration = String.format("%02d:%02d:%02d", hour, minute, second);
			
			if(payout > maxPayout) {
				payout = maxPayout;
			}
			
			Double playerMultiplier = Config.getInstance().getPlayerBonus(player);
			Double globalBonus = Config.getInstance().getGlobalBonus();
			
			Double totalMultiplier = playerMultiplier * globalBonus;
			
			double totalPayout = totalMultiplier * payout;
			DecimalFormat df = new DecimalFormat("#.##");
			
			player.sendMessage("----- " + ChatColor.GOLD + "Ride Ended"+ ChatColor.WHITE + " ----- ");
			player.sendMessage("");
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
					"  " + name));
			player.sendMessage("  Ride Duration: " + duration);
			player.sendMessage("  Money Earned " + ChatColor.GOLD +df.format(totalPayout) + ChatColor.GRAY +" (" + df.format(payout) + " x " + df.format(totalMultiplier) + ")");
			player.sendMessage("");
			player.sendMessage("----- " + ChatColor.GOLD + "Ride Ended"+ ChatColor.WHITE + " ----- ");
			
			
			ParkManager.getEconomy().depositPlayer(player, totalPayout);
			pData.endRide();
			
			//Advancement
			if(hasAdvancement) {
				AdvancementUtil.getInstance().grantRideAdvancement(identifier, player);
			}
		}
	}

	
	
	/*
	 * Setters
	 */
	
	public void setName(String newName) {
		this.name = newName;
		this.changed = true;
	}
	
	public void setWarp(String newWarp) {
		this.warpName = newWarp;
		this.changed = true;
	}
	
	public String getWarp() {
		return this.warpName;
	}
	
	public void setPayPerMinute(double newPayout) {
		this.defaultPayPerMinute = newPayout;
		this.changed = true;
	}
	
	public void setDescription(String newDescription) {
		List<String> splitDescription = Arrays.asList(newDescription.split("\n"));
		this.description = splitDescription;
		this.changed = true;
	}
	
	public void setMaxPayout(double maxPayout) {
		this.maxPayout = maxPayout;
		this.changed = true;
	}

	
	public void setIcon(ItemStack icon) {
		ItemMeta meta = icon.getItemMeta();
		int modelData = 0;
		if(meta.hasCustomModelData()) modelData = meta.getCustomModelData();
		this.icon = buildIcon(icon.getType(), modelData);
		this.changed = true;
	}
	
	public ItemStack getIcon() {
		return icon;
	}


	public boolean isEnabled() {
		return this.enabled;
	}
	
	
}
