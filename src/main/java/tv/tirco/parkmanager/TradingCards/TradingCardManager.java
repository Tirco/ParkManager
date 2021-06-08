package tv.tirco.parkmanager.TradingCards;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.util.MessageHandler;
import tv.tirco.parkmanager.util.Util;

public class TradingCardManager {
	
	private static TradingCardManager instance;
	
	public static TradingCardManager getInstance() {
		if(TradingCardManager.instance == null) {
			TradingCardManager.instance = new TradingCardManager();
		}
		return instance;
	}
	
	private BiMap<Integer,TradingCard> allCards;
	private List<Integer> legendaryCards;
	private List<Integer> epicCards;
	private List<Integer> rareCards;
	private List<Integer> uncommonCards;
	private List<Integer> commonCards;
	
	private List<Inventory> emptyInventories;
	
	private LinkedHashMap<String, Integer> topTen;
	
 	public TradingCardManager() {
		this.allCards = HashBiMap.create();
		this.legendaryCards = new ArrayList<Integer>();
		this.epicCards = new ArrayList<Integer>();
		this.rareCards = new ArrayList<Integer>();
		this.uncommonCards = new ArrayList<Integer>();
		this.commonCards = new ArrayList<Integer>();
		
		buildEmptyInventories();
		
		this.topTen = new LinkedHashMap<String, Integer>();
		loadTopTen();
	}
	
	public void loadTopTen() {
		MessageHandler.getInstance().debug("Scheduling async load of top 10");
		Bukkit.getScheduler().runTaskLaterAsynchronously(ParkManager.plugin, new Runnable() {
			@Override
			public void run() {
				TradingCardManager.getInstance().setTop10(ParkManager.db.getTop10());
				
			}
		}, 1);
		
	}

	public void setTop10(LinkedHashMap<String, Integer> map) {
		this.topTen = map;
	}
	
	public LinkedHashMap<String, Integer> getTopTen() {
		return this.topTen;
	}


	private void buildEmptyInventories() {
		this.emptyInventories = new ArrayList<Inventory>();
		int maxCards = TradingCardConfig.getInstance().getAmountOfCards();
		int remainingCardCounter = maxCards;
		int cardCounter = 1;
		
		//Filer item
		ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		ItemMeta fillerMeta = filler.getItemMeta();
		fillerMeta.setDisplayName(" ");
		filler.setItemMeta(fillerMeta);

		//Default Card Item
		ItemStack cardItem = getUnownedCardItem(1);
		ItemMeta cardMeta = cardItem.getItemMeta();

		//loop 'em
		while(remainingCardCounter > 0) {
			Inventory inv = Bukkit.createInventory(null, 54); //Last 9 is for menu buttons.
			
			//Set bottom line

			for(int i = 45; i <= 53; i++) {
				inv.setItem(i, filler);
			}
			//Set Next button
			inv.setItem(51, getNextButton());
			//Set Insert button
			inv.setItem(49, getInsertItem());
			//Set Prev button
			inv.setItem(47, getPrevButton());
			//Set exit button
			inv.setItem(53, getExitItem());
			//Set score button
			inv.setItem(45, getScoreItem());

			//Load empty cards:
			for(int i = 0; i <= 44; i++) {
				cardMeta.setDisplayName(ChatColor.YELLOW + "Unknown Card " + ChatColor.GREEN + "#" + String.format("%03d",cardCounter));
				cardItem.setItemMeta(cardMeta);
				inv.setItem(i, cardItem);
				cardCounter ++;
				if(cardCounter > maxCards) {
					break;
				}
			}
			remainingCardCounter -= 45; //45 cards pr page.
			
			emptyInventories.add(inv);
		}
	}

	private ItemStack getNextButton() {
		ItemStack item = new ItemStack(Material.COMPARATOR, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Next Page");
		item.setItemMeta(itemMeta);
		return item;
	}

	private ItemStack getInsertItem() {
		ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Add Card");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + "Drag a card on to");
		lore.add(ChatColor.YELLOW + "this slot to add it");
		lore.add(ChatColor.YELLOW + "to the card binder.");
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		return item;
	}

	private ItemStack getPrevButton() {
		ItemStack item = new ItemStack(Material.REPEATER, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
		item.setItemMeta(itemMeta);
		return item;
	}

	private ItemStack getExitItem() {
		ItemStack item = new ItemStack(Material.REDSTONE_BLOCK, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "EXIT");
		item.setItemMeta(itemMeta);
		return item;
	}
	
	private ItemStack getScoreItem() {
		ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
		return item;
	}

	public TradingCardCondition getRandomCondition() {
		return TradingCardConfig.getInstance().getRandomCondition();
	}

	public void clearLists() {
		TradingCardManager.instance = new TradingCardManager();
	}
	
	/**
	 * Is relatively slow compared to an arraylist.
	 * @return
	 */
	public TradingCard getRandomCard() {
		Random rand = Util.getRandom();
		Object[] randomList = allCards.keySet().toArray();
		TradingCard key = (TradingCard) randomList[rand.nextInt(randomList.length)];
		return key;
	}
	
	public TradingCard getCardByID(int id) {
		//TODO nullcheck?
		return allCards.get(id);
	}

	public void addCard(TradingCard card) {

		if(card.isAvailable()) {
			if(card.getRarity().equals(TradingCardRarity.COMMON)) {
				commonCards.add(card.getID());
			} else if(card.getRarity().equals(TradingCardRarity.UNCOMMON)) {
				uncommonCards.add(card.getID());
			} else if(card.getRarity().equals(TradingCardRarity.RARE)) {
				rareCards.add(card.getID());
			} else if(card.getRarity().equals(TradingCardRarity.EPIC)) {
				epicCards.add(card.getID());
			} else if(card.getRarity().equals(TradingCardRarity.LEGENDARY)) {
				legendaryCards.add(card.getID());
			} else {
				MessageHandler.getInstance().log("Failed to add card " + card.getID() + " The card had no rarity set.");
				return;
			}
		}

		allCards.put(Integer.valueOf(card.getID()),card);
	}
	
	/**
	 * Will fail if the lore isn't already there.
	 * @param condition
	 * @param item
	 * @return
	 */
	public ItemStack updateCondition(TradingCardCondition condition, ItemStack item) {
		ItemStack returnItem = item.clone();
		ItemMeta meta = item.getItemMeta();
		String conditionString = condition.getAsString();
		List<String> lore = meta.getLore();
		lore.set(3,ChatColor.translateAlternateColorCodes('&', "&aCondition:&7 " + conditionString));
		
		meta.setLore(lore);
		returnItem.setItemMeta(meta);
		return returnItem;
	}

	//TODO rename to score / points
	public double getCardValue(TradingCardRarity rarity, TradingCardCondition condition, Boolean signed, Boolean shiny, Boolean available) {
		double value = 1;
		value = value * rarity.getBaseValue();
		value = value * condition.getValueDouble();
		if(signed) {
			value = value * 2;
		}
		if(shiny) {
			value = value * 2;
		}
		if(!available) {
			value = value * 3;
		}
		return value;
	}
	
	public TradingCard drawTradingCard(TradingCardRarity rarity) {
		List<Integer> drawingPool;
		switch(rarity.toString()) {
		case "LEGENDARY":
			drawingPool = legendaryCards;
			break;
		case "EPIC":
			drawingPool = epicCards;
			break;
		case "RARE":
			drawingPool = rareCards;
			break;
		case "UNCOMMON":
			drawingPool = uncommonCards;
			break;
		case "COMMON":
		default:
			drawingPool = commonCards;
			break;
		}
		
		Random rand = Util.getRandom();
		int cardID = drawingPool.get(rand.nextInt(drawingPool.size()));
		return getCardByID(cardID);
	}
	
	public ItemStack getRandomTradingCardItem() {
		TradingCard card = drawTradingCard();
		
		//shiny?
		boolean shiny = TradingCardConfig.getInstance().getShinyRandom();
		return card.buildCardItem(TradingCardCondition.UNKNOWN, false, shiny);
	}
	
	public TradingCard drawTradingCard() {
		TradingCardRarity rarity = TradingCardConfig.getInstance().getRandomRarity();
		return drawTradingCard(rarity);
	}
	
	/**
	 * 
	 * @param id
	 * @param shiny
	 * @param signed
	 * @param cond
	 * @param rarity
	 * @return
	 */
	public String getCardStorageID(int id, boolean shiny, boolean signed, TradingCardCondition cond, TradingCardRarity rarity) {
		String returnValue = "";
		returnValue += id + ":";
		returnValue += (shiny ? "1" : "0") + ":";
		returnValue += (signed ? "1" : "0") + ":";
		returnValue += cond.getConditionCode() + ":";
		//returnValue += rarity.getRarityCode() + ":"; //Not needed, rarity is stored in card.
		
		return returnValue;
	}
	
	public ItemStack getCardItemFromCode(String cardCode) {
		String[] cardString = cardCode.split(":");
		if(cardString.length < 4) {
			MessageHandler.getInstance().debug("Error when loading card from id - not enough arguments.");
			return null;
		}
		int cardID = 0;
		try {
			cardID = Integer.parseInt(cardString[0]);
		} catch(NumberFormatException ex) {
			MessageHandler.getInstance().debug("Error when loading card from id - ID was not a number.");
			return null;
		}
		
		Boolean shiny = cardString[1].equals("1");
		Boolean signed = cardString[2].equals("1");
		TradingCardCondition cond = TradingCardCondition.getCondFromCode(cardString[3]);
		//TradingCardRarity rarity = TradingCardRarity.getRarityFromCode(cardString[4]);
		
		TradingCard card = getCardByID(cardID);
		if(card == null) {
			MessageHandler.getInstance().debug("Error when loading card from id - card not found.");
			return null;
		}
		
		return card.buildCardItem(cond, signed, shiny);
	}
	
	
	/*
	 * Inventory Stuff
	 */

	
	public Inventory buildCardBinderPage(BiMap<Integer,ItemStack> cards, int page) {
		ItemStack[] items = emptyInventories.get(page - 1).getContents().clone();
 		Inventory inv = Bukkit.createInventory(null, 54,  ChatColor.GREEN + "Card Binder - Page " + page);
 		inv.setContents(items);
		
 		int startID = 1 + ((page - 1) * 45);
 		int endID = startID + 45;
 		int max = TradingCardConfig.getInstance().getAmountOfCards();
 		if (endID > max ) endID = max;
 		int posCounter = 0;
 		
 		for(int i = startID; i >= endID; i++) {
 			if(cards.containsKey(i)) {
 				inv.setItem(posCounter, cards.get(i));
 			}
 			posCounter ++;
 		}
 		
		return inv;
	}
	
	public ItemStack getUnownedCardItem(int id) {
		ItemStack cardItem = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta cardMeta = cardItem.getItemMeta();
		cardMeta.setDisplayName(ChatColor.YELLOW + "Unknown Card " + ChatColor.GREEN + "#" + String.format("%03d",id));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "Trading Card");
		lore.add("");
		lore.add(ChatColor.YELLOW + "???");
		cardMeta.setLore(lore);
		cardMeta.setCustomModelData(1000);
		
		cardItem.setItemMeta(cardMeta);
		return cardItem;
	}
	
	public List<Inventory> buildCardBinder(BiMap<Integer, ItemStack> storedCards) {
		List<Inventory> returnList = new ArrayList<Inventory>();
		int page = 1;
		for(@SuppressWarnings("unused") Inventory inv : emptyInventories) { //TODO better loop?
			Inventory inventory = buildCardBinderPage(storedCards, page);
			returnList.add(inventory);
			page ++;
		}
		return returnList;
	}
	
	
 	public void updateScoreItem(PlayerData pData, Inventory inv) {
		ItemStack scoreItem = inv.getItem(45);
		int score = pData.getCardScore();
		
		ItemMeta meta = scoreItem.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Card Score: " + ChatColor.YELLOW + score);
		scoreItem.setItemMeta(meta);
	}

	public int getItemScore(ItemStack Item) {
		NBTItem nbti = new NBTItem(Item);
		int score = 0;
		if(nbti.hasNBTData()) {
			if(nbti.hasKey("TradingCardScore")) {
				score = nbti.getInteger("TradingCardScore");
			}
		}
		return score;
	}


	
}
