package tv.tirco.parkmanager.TradingCards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.util.MessageHandler;
import tv.tirco.parkmanager.util.Util;

public class TradingCardPackTask {

	Player player;
	PlayerData pData;
	ParkManager plugin;
	
	TradingCard cardOne;
	TradingCard cardTwo;
	TradingCard cardThree;
	
	List<ItemStack> counterItems;
	
	Inventory inv;

	int task;
	
	int cycle = 0;
	int loopCounter = 0;
	
	public TradingCardPackTask(ParkManager plugin, Player player, PlayerData pData) {
		this.plugin = plugin;
		this.player = player;
		this.pData = pData;
		pData.setIsOpeningPack(true);
		 cardOne = TradingCardManager.getInstance().drawTradingCard();
		 cardTwo = TradingCardManager.getInstance().drawTradingCard();
		 cardThree = TradingCardManager.getInstance().drawTradingCard();
		 
		MessageHandler.getInstance().debug("Starting opening of CardPack for player" + player.getName());
		MessageHandler.getInstance().debug("Player will get " + 
		cardOne.getID() + ", "+
		cardTwo.getID() + ", "+
		cardThree.getID() + ". ");
	
		counterItems = new ArrayList<ItemStack>();
		
		player.openInventory(getInventory());
		
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 2.0f);

		this.task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {

				if(cycle < 3) {
					ItemStack i = inv.getItem(3);
					i.setItemMeta(getItemMeta(i));
				}
				if(cycle < 2) {
					ItemStack i = inv.getItem(2);
					i.setItemMeta(getItemMeta(i));
				}
				if(cycle < 1) {
					ItemStack i = inv.getItem(1);
					i.setItemMeta(getItemMeta(i));
				}
				
				
				if(!player.isOnline()) {
					Finish();
				}
				
				loopCounter ++;
				if(loopCounter >= 4) {
					loopCounter = 0;
					cycle ++;
					if(cycle == 1) {
						boolean shiny = TradingCardConfig.getInstance().getShinyRandom();
						inv.setItem(1, cardOne.buildCardItem(TradingCardCondition.UNKNOWN, false, shiny));
						player.playSound(player.getLocation(), cardOne.getRarity().getSound(), 0.3f,1);
						if(shiny) {
							player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 0.3f, 2);
						}
					} else if (cycle == 2) {
						boolean shiny = TradingCardConfig.getInstance().getShinyRandom();
						inv.setItem(2, cardTwo.buildCardItem(TradingCardCondition.UNKNOWN, false, shiny));
						player.playSound(player.getLocation(), cardTwo.getRarity().getSound(), 0.3f,1);
						if(shiny) {
							player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 0.3f, 2);
						}
					}
					else if (cycle >= 3) {
						boolean shiny = TradingCardConfig.getInstance().getShinyRandom();
						inv.setItem(3, cardThree.buildCardItem(TradingCardCondition.UNKNOWN, false, shiny));
						player.playSound(player.getLocation(), cardThree.getRarity().getSound(), 0.3f,1);
						if(shiny) {
							player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 0.3f, 2);
						}
						counterItems.clear();
						Finish();
					} 
				} else {
						player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 2.0f);
				}
			}

 		}, 8l, 8l);
	}
	
	private void Finish() {
		pData.setIsOpeningPack(false);
		Bukkit.getScheduler().cancelTask(task);
		if(!player.isOnline()) {
			MessageHandler.getInstance().log("Player " + player.getName() + " disconnected while opening card pack.");
			MessageHandler.getInstance().log("Player should have  gotten " + 
					cardOne.getID() + ", "+
					cardTwo.getID() + ", "+
					cardThree.getID() + ". ");
		}
	}
	
	private Inventory getInventory() {
		ItemStack defaultCounterItem = getDefaultCounterItem();
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "Card Pack");
		ItemStack item = defaultCounterItem.clone();
		item.setItemMeta(getItemMeta(item));
		ItemStack itemTwo = item.clone();
		ItemStack itemThree = item.clone();
		counterItems.add(item);
		counterItems.add(itemTwo);
		counterItems.add(itemThree);
		inv.setItem(1, item);
		inv.setItem(2, itemTwo);
		inv.setItem(3, itemThree);
		this.inv = inv;
		return inv;
	}
	
	private ItemMeta getItemMeta(ItemStack i) {
		ItemMeta meta = i.getItemMeta();
		meta.setCustomModelData(Util.getRandom().nextInt(5) + 1);
		return meta;
	}
	
	private ItemStack getDefaultCounterItem() {
		ItemStack defaultItem = new ItemStack(Material.BLAZE_POWDER,1);
		ItemMeta itemMeta = defaultItem.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "???");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "Trading Card");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Unknown card!");
		lore.add(ChatColor.YELLOW + "Which one could it be?");
		itemMeta.setLore(lore);
		itemMeta.setCustomModelData(1);
		defaultItem.setItemMeta(itemMeta);
		return defaultItem;
	}
	

}
