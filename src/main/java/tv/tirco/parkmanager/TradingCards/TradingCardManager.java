package tv.tirco.parkmanager.TradingCards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.md_5.bungee.api.ChatColor;
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
	
	public TradingCardManager() {
		this.allCards = HashBiMap.create();
		this.legendaryCards = new ArrayList<Integer>();
		this.epicCards = new ArrayList<Integer>();
		this.rareCards = new ArrayList<Integer>();
		this.uncommonCards = new ArrayList<Integer>();
		this.commonCards = new ArrayList<Integer>();
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
		lore.set(2,ChatColor.translateAlternateColorCodes('&', "&aCondition:&7 " + conditionString));
		
		meta.setLore(lore);
		returnItem.setItemMeta(meta);
		return returnItem;
	}

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

}
