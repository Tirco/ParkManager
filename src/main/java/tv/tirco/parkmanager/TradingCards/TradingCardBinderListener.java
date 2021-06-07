package tv.tirco.parkmanager.TradingCards;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class TradingCardBinderListener implements Listener{

	//Adding card to already occupied slot needs will take out other item and insert new item.
	//Updating score will be done by adding the value to the already known score and removing the score from the item taken out.
	//Full score and item reset will be done on load. Loading will be async.
	
	@EventHandler
	public void onPlayerClickCardBinder(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		
		if(!UserManager.hasPlayerDataKey(player)) {
			e.setCancelled(true);
			player.sendMessage("An error has occured. Please close this inventory and try again.");
			player.sendMessage("If the issue persists, please relogg.");
		}
		PlayerData pData = UserManager.getPlayer(player);
		if(!pData.getBinderPages().contains(e.getClickedInventory())) {
			return;
		} 
		//It's a card binder! Confirmed!
		e.setCancelled(true);
		
	}
}
