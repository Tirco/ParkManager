package tv.tirco.parkmanager.storage.playerdata;

import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import tv.tirco.parkmanager.TradingCards.TradingCardManager;
import tv.tirco.parkmanager.util.MessageHandler;

public class PlayerProfile {

	private final String playerName;
	private UUID uuid;
	private boolean loaded;
	private String rideidentifier;
	private long rideStartTime;
	private boolean changed = false;
	//private volatile boolean changed;
	
	//Card stuff
	private BiMap<Integer, ItemStack> storedCards;
	private List<Inventory> cardBinderPages;
	
	private int cardScore = 0;

	
	public PlayerProfile(String playerName, UUID uuid) {
		this.playerName = playerName;
		this.uuid = uuid;
		this.loaded = true;
		this.rideidentifier = "none";
		//this.changed = true; //Changed so we get an updated last login.
		
		this.storedCards = HashBiMap.create();
		this.cardBinderPages = TradingCardManager.getInstance().buildCardBinder(storedCards);
		this.cardScore = 0;
		
	}
	
	public void startRide(String identifier) {
		this.rideidentifier = identifier;
		this.rideStartTime = System.currentTimeMillis();
	}
	
	public void endRide() {
		this.rideidentifier = "none";
		this.rideStartTime = 0;
	}
	
	public String getRideIdentifier() {
		return rideidentifier;
	}
	
	public Long getRideStartTime() {
		return rideStartTime;
	}
	
	public boolean isInRide() {
		return !rideidentifier.equals("none");
	}

	public void save() {
		// TODO Auto-generated method stub
		
	}
	
	public void scheduleAsyncSave() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isChanged() {
		return changed;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public String getPlayerName() {
		return playerName;
	}

	
	public List<Inventory> getBinderPages() {
		return cardBinderPages; 
	}

	public Inventory getBinderPage(int page) {
		
		return cardBinderPages.get(page);
	}

	public void storeCard(int cardID, ItemStack item) {
		this.storedCards.put(cardID, item);
		
		int page = (int) Math.ceil(cardID / 45);
		MessageHandler.getInstance().debug("Debug: page = " + page);
		Inventory insertionInv = getBinderPage(page);
		insertionInv.setItem(cardID - ((page) * 45), item);
	}

	public int getBinderPageNumber(Inventory inv) {
		return cardBinderPages.indexOf(inv);
	}

	public ItemStack getStoredCard(int cardID) {
		return this.storedCards.get(cardID);
	}

	public void removeStoredCard(int cardID, ItemStack unownedCardItem) {
		if(unownedCardItem != null) {
			int page = (int) Math.ceil(cardID / 45);
			Inventory insertionInv = getBinderPage(page);
			insertionInv.setItem(cardID - ((page) * 45), unownedCardItem);
		}
		this.storedCards.remove(cardID);
	}
	
	public int getCardScore() {
		return this.cardScore;
	}
	
	public void addCardScore(int value) {
		this.cardScore += value;
	}
	
	public void removeCardScore(int value) {
		this.cardScore -= value;
	}
	
	public void updateScore(int remove, int add) {
		this.cardScore = this.cardScore - remove + add;
	}


}
