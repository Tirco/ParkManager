package tv.tirco.parkmanager.TradingCards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class TradingCardBinderListener implements Listener{

	//Adding card to already occupied slot needs will take out other item and insert new item.
	//Updating score will be done by adding the value to the already known score and removing the score from the item taken out.
	//Full score and item reset will be done on load. Loading will be async.
	
	@EventHandler
	public void onPlayerClickCardBinder(InventoryClickEvent e) {

		if(e.getClickedInventory() == null) {
			return;
		}
		
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
		if(pData.getBinderPages().contains(e.getClickedInventory())) {

			Inventory inv = e.getClickedInventory();
			//It's a card binder! Confirmed!
			e.setCancelled(true);
			
			//Click green slot.
			if(e.getSlot() == 49) {
				if(e.getCursor() == null) {
					return;
				}
				ItemStack itemInHand = e.getCursor();
				if(itemInHand.getType().equals(Material.BLAZE_POWDER)) {
					NBTItem nbti = new NBTItem(itemInHand);
					if(!nbti.hasNBTData()) {
						return;
					}
					if(!nbti.hasKey("TradingCardID")) {
						return;
					}
					int cardID = nbti.getInteger("TradingCardID");
					
					ItemStack storedItem = pData.getStoredCard(cardID);
					int storedScore = 0;
					if(storedItem != null) {
						//There's an item there already!
						//Does the player have space?
						if(player.getInventory().firstEmpty() == -1) {
							player.sendMessage("There is already a card stored at that slot. You need an empty space in your inventory to take it out.");
							return;
						}
						
						player.getInventory().addItem(storedItem);
						storedScore = TradingCardManager.getInstance().getItemScore(storedItem);
					}
					
					//Remove their cursor item, store their card, send a message.
					player.setItemOnCursor(new ItemStack(Material.AIR));
					pData.storeCard(cardID, itemInHand);
					player.sendMessage("The card in your binder has been updated!");
					pData.updateScore(storedScore, TradingCardManager.getInstance().getItemScore(itemInHand));
					TradingCardManager.getInstance().updateScoreItem(pData, inv);
				}
			}
			//Click next slot
			else if(e.getSlot() == 51) {
				int page = pData.getBinderPageNumber(inv) + 1;
				if(page > (TradingCardConfig.getInstance().getAmountOfCards()/45)) {
					return;
				}
				player.openInventory(pData.getBinderPage(page));
			}
			//Click prev slot
			else if(e.getSlot() == 47) {
				int page = pData.getBinderPageNumber(inv) - 1;
				if(page < 0) {
					return;
				}
				player.openInventory(pData.getBinderPage(page));
			}
			
			//Click exit slot
			else if(e.getSlot() == 53) {
				player.closeInventory();
				return;
			}
			//Attempt to take out item.
			else if(inv.getItem(e.getSlot()).getType().equals(Material.BLAZE_POWDER)) {
				if(player.getInventory().firstEmpty() == -1) {
					player.sendMessage("You need an empty slot in your inventory.");
					return;
				}
				
				int cardID = e.getSlot() + (45 * pData.getBinderPageNumber(inv));
				ItemStack storedItem = pData.getStoredCard(cardID);
				if(storedItem != null) {
					pData.removeStoredCard(cardID, TradingCardManager.getInstance().getUnownedCardItem(cardID));
					player.getInventory().addItem(storedItem);
					pData.removeCardScore(TradingCardManager.getInstance().getItemScore(storedItem));
					TradingCardManager.getInstance().updateScoreItem(pData, inv);
				}
			}
			
		} else if(pData.getBinderPages().contains(e.getView().getTopInventory())) {
			if(e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
				ItemStack itemInHand = e.getCurrentItem();
				if(itemInHand.getType().equals(Material.BLAZE_POWDER)) {
					NBTItem nbti = new NBTItem(itemInHand);
					if(!nbti.hasNBTData()) {
						return;
					}
					if(!nbti.hasKey("TradingCardID")) {
						return;
					}
					int cardID = nbti.getInteger("TradingCardID");
					
					ItemStack storedItem = pData.getStoredCard(cardID);
					int storedScore = 0;
					if(storedItem != null) {
						//There's an item there already!
						//Does the player have space?
						if(player.getInventory().firstEmpty() == -1) {
							player.sendMessage("There is already a card stored at that slot. You need an empty space in your inventory to take it out.");
							storedScore = TradingCardManager.getInstance().getItemScore(storedItem);
							return;
						}
						
						player.getInventory().addItem(storedItem);
					}
					
					//Remove their cursor item, store their card, send a message.
					player.setItemOnCursor(new ItemStack(Material.AIR));
					e.getClickedInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
					pData.storeCard(cardID, itemInHand);
					player.sendMessage("The card in your binder has been updated!");
					pData.updateScore(storedScore, TradingCardManager.getInstance().getItemScore(itemInHand));
					TradingCardManager.getInstance().updateScoreItem(pData, e.getView().getTopInventory());
				}
			}
		}
		
	}
}
