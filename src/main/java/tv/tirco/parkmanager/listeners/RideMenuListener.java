package tv.tirco.parkmanager.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;

public class RideMenuListener implements Listener{

	@EventHandler
	public void onRideMenuClick(InventoryClickEvent e) {
		if(e.getInventory() == null) {
			return;
		}
		
		if(e.getClickedInventory() != DataStorage.getInstance().getRideMenu()) {
			return;
		}
		
		e.setCancelled(true);
		
		Player player = null;
		if(e.getWhoClicked() instanceof Player) {
			player = (Player) e.getWhoClicked();
		} else {
			return;
		}
		
		if(e.getAction().equals(InventoryAction.PICKUP_ALL)) {
			ItemStack clicked = e.getCurrentItem();
			if(clicked == null || clicked.getType().equals(Material.AIR)) {
				return;
			}
			
			NBTItem nbti = new NBTItem(clicked);
			if(nbti.hasNBTData()) {
				if(nbti.hasKey("RideIdentifier")) {
					String identifier = nbti.getString("RideIdentifier");
					Ride ride = DataStorage.getInstance().getRide(identifier);
					if(ride != null) {
						if(ride.isEnabled()) {
							player.performCommand("warp " + ride.getWarp());
							player.closeInventory();
						} else {
							player.sendMessage(ChatColor.RED + "That ride is currently disabled.");
							player.closeInventory();
						}
					}
				}
			}
		}
	}
}
