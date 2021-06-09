package tv.tirco.parkmanager.TradingCards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class TradingCardBinderListener implements Listener{

	//Adding card to already occupied slot needs will take out other item and insert new item.
	//Updating score will be done by adding the value to the already known score and removing the score from the item taken out.
	//Full score and item reset will be done on load. Loading will be async.
	
	@EventHandler
	public void onPlayerCloseInventory(InventoryCloseEvent e) {
		if(e.getInventory().getType().equals(InventoryType.HOPPER) && e.getView().getTitle().equalsIgnoreCase("Card Pack")){
			Player player = (Player) e.getPlayer();
			if(UserManager.hasPlayerDataKey(player)) {
				PlayerData pData = UserManager.getPlayer(player);				
				
				if(pData.isOpeningPack()) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(ParkManager.plugin, 
							new Runnable() {

								@Override
									public void run() {
									player.openInventory(e.getInventory()); 
								}},1);
				}else {
					for(ItemStack i : e.getView().getTopInventory().getContents()) {
						if(i != null) {
							player.getInventory().addItem(i);
						}
					}
					e.getView().getTopInventory().clear();
					
				} 
			}
		}
	}
	
	@EventHandler
	public void onPlayerClickWhileLocked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if(!UserManager.hasPlayerDataKey(player)) {
			e.setCancelled(true);
			return;
		}
		
		PlayerData pData = UserManager.getPlayer(player);
		if(pData.isOpeningPack()) {
			e.setCancelled(true);
		}
	}
	
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
		if(!pData.isLoaded()) {
			player.sendMessage(ChatColor.RED + "Your profile appears to not be loaded.");
			player.sendMessage(ChatColor.RED + "Please contact an administrator.");
			e.setCancelled(true);
			return;
		}

		
		if(pData.getBinderPages().contains(e.getClickedInventory())) {
			if(pData.spamCooldown())  {
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "Slow down!");
				return;
			} else {
				pData.updateSpamCooldown();
			}

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
							player.sendMessage(ChatColor.YELLOW +
									"There is already a card stored at that slot.");
							player.sendMessage(ChatColor.YELLOW + 
									"You need an empty space in your inventory to take it out.");
							return;
						}
						
						player.getInventory().addItem(storedItem);
						storedScore = TradingCardManager.getInstance().getItemScore(storedItem);
					}
					
					//Remove their cursor item, store their card, send a message.
					player.setItemOnCursor(new ItemStack(Material.AIR));
					pData.storeCard(cardID, itemInHand);
					pData.updateScore(storedScore, TradingCardManager.getInstance().getItemScore(itemInHand));
					player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 0.4f, 1f);
					TradingCardManager.getInstance().updateScoreItem(pData, inv);
				}
			}
			//Click next slot
			else if(e.getSlot() == 51) {
				int page = pData.getBinderPageNumber(inv) + 1;
				if(page > (TradingCardConfig.getInstance().getAmountOfCards()/45)) {
					return;
				}
				player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.2f, 1f);
				TradingCardManager.getInstance().updateScoreItem(pData, pData.getBinderPage(page));
				player.openInventory(pData.getBinderPage(page));
			}
			//Click prev slot
			else if(e.getSlot() == 47) {
				int page = pData.getBinderPageNumber(inv) - 1;
				if(page < 0) {
					return;
				}
				player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.2f, 1f);
				TradingCardManager.getInstance().updateScoreItem(pData, pData.getBinderPage(page));
				player.openInventory(pData.getBinderPage(page));
			}
			
			//Click exit slot
			else if(e.getSlot() == 53) {
				player.closeInventory();
				return;
			}
			//Attempt to take out item.
			else {
				ItemStack clickedItem = inv.getItem(e.getSlot());
				if(clickedItem == null) {
					return;
				} else if(clickedItem.getType().equals(Material.BLAZE_POWDER)) {
					if(player.getInventory().firstEmpty() == -1) {
						player.sendMessage(ChatColor.YELLOW +
								"You need an empty slot in your inventory.");
						return;
				}
				
				int cardID = (e.getSlot() + 1) + (45 * pData.getBinderPageNumber(inv));
				ItemStack storedItem = pData.getStoredCard(cardID);
				if(storedItem != null) {
					pData.removeStoredCard(cardID, TradingCardManager.getInstance().getUnownedCardItem(cardID));
					player.getInventory().addItem(storedItem);
					pData.removeCardScore(TradingCardManager.getInstance().getItemScore(storedItem));
					player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 0.4f, 1f);
					TradingCardManager.getInstance().updateScoreItem(pData, inv);
				}
				}
			}
		//SHIFT CLICK
		} else if(pData.getBinderPages().contains(e.getView().getTopInventory())) {
			if(pData.spamCooldown())  {
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "Slow down!");
				return;
			} else {
				pData.updateSpamCooldown();
			}
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
							player.sendMessage(ChatColor.YELLOW +
									"There is already a card stored at that slot.");
							player.sendMessage(ChatColor.YELLOW + 
									"You need an empty space in your inventory to take it out.");
							storedScore = TradingCardManager.getInstance().getItemScore(storedItem);
							return;
						}
						
						player.getInventory().addItem(storedItem);
					}
					
					//Remove their cursor item, store their card, send a message.
					player.setItemOnCursor(new ItemStack(Material.AIR));
					e.getClickedInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));

					pData.storeCard(cardID, itemInHand);
					pData.updateScore(storedScore, TradingCardManager.getInstance().getItemScore(itemInHand));
					player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 0.4f, 1f);
					TradingCardManager.getInstance().updateScoreItem(pData, e.getView().getTopInventory());
					player.updateInventory();
				}
			}
		}
		
	}
}
