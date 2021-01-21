package tv.tirco.parkmanager.Inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;

public class ItemModifierListener implements Listener{
	
	private int updateItemSlot = 22;

	
	@EventHandler
	public void onItemModifierClick(InventoryClickEvent e) {
		if(e.getClickedInventory() == null) {
			return;
		}
		
		Player player = (Player) e.getWhoClicked();

		//Check if title is correct.
		if(!ChatColor.translateAlternateColorCodes('&',
				e.getView().getTitle()).equals(ChatColor.translateAlternateColorCodes('&', 
						"&6&lItem Modifier"))) {
			return;
		}
		//Check if size is correct.
		//Should prevent most false positive cases.
		if(e.getView().getTopInventory().getSize() != 45) {
			return;
		}
		
		if(e.getClickedInventory().equals(e.getView().getTopInventory())) {
			if(e.getSlot() != updateItemSlot) {
				e.setCancelled(true);
			}
		} else {
			return;
		}
		
		int clickedSlot = e.getSlot();
		ItemStack item = e.getView().getTopInventory().getItem(updateItemSlot);
		if(item == null || item.getType().equals(Material.AIR)) {
			return;
		}
		ItemMeta meta = item.getItemMeta();
		
		switch(clickedSlot) {
		case 10: //Unbreakable
			meta.setUnbreakable(!meta.isUnbreakable());
			item.setItemMeta(meta);
			new ItemModifier(ParkManager.parkManager, player, e.getClickedInventory());
			return;
		case 19: //Modeldata
			//meta.setUnbreakable(!meta.isUnbreakable());
			//item.setItemMeta(meta);
			//new ItemModifier(ParkManager.parkManager, player, e.getClickedInventory());
			return;
		case 22: //Slot of item;
			if(e.getView().getTopInventory().getItem(updateItemSlot) != null) {
				if(!e.getView().getTopInventory().getItem(updateItemSlot).getType().equals(Material.AIR)) {
					ItemStack updateItem = e.getView().getTopInventory().getItem(updateItemSlot);
					new ItemModifier(ParkManager.parkManager, updateItem, player);
				}
			}
			return;
		case 44:
			player.closeInventory();
			return;
		}
		
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getInventory() == null) {
			return;
		}
		
		Player player = (Player) e.getPlayer();

		//Check if title is correct.
		if(!ChatColor.translateAlternateColorCodes('&',
				e.getView().getTitle()).equals(ChatColor.translateAlternateColorCodes('&', 
						"&6&lItem Modifier"))) {
			return;
		}
		//Check if size is correct.
		//Should prevent most false positive cases.
		Inventory inv = e.getView().getTopInventory();
		if(inv.getSize() != 45) {
			return;
		}
		
		//Maybe schedule as a delayed task?
		if(inv.getItem(updateItemSlot) != null && !inv.getItem(updateItemSlot).getType().equals(Material.AIR)) {
			ItemStack item = inv.getItem(updateItemSlot);
			if(player.getOpenInventory().getType() == InventoryType.CRAFTING || player.getOpenInventory().getType() == InventoryType.CREATIVE) {
				player.getInventory().addItem(item);
			}
		}
		
	}
}
