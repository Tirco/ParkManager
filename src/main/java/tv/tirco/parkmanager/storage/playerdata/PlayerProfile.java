package tv.tirco.parkmanager.storage.playerdata;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.BiMap;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
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
	
	private long spamCooldown = 0;
	private int spamCount = 0;
	
	//Card stuff
	private BiMap<Integer, ItemStack> storedCards;
	private List<Inventory> cardBinderPages;
	
	private int cardScore = 0;
	
	private boolean isOpeningPack = false;

	
	public PlayerProfile(String playerName, UUID uuid, BiMap<Integer,ItemStack> cards, int score) {
		this.playerName = playerName;
		this.uuid = uuid;

		this.rideidentifier = "none";
		//this.changed = true; //Changed so we get an updated last login.
		
		this.storedCards = cards;
		this.cardScore = score;
		this.cardBinderPages = TradingCardManager.getInstance().buildCardBinder(storedCards);
		this.cardScore = score;
		if(this.cardScore < 0) {
			this.cardScore = 0;
		}
		forceUpdateAllCards();
		this.loaded = true;
	}
	
	
	
	private void forceUpdateAllCards() {
		try {
			int score = 0;
			for(int cardID : storedCards.keySet()) {

				int page = (int) Math.ceil(cardID / 46); //Error found?
				//MessageHandler.getInstance().debug("Debug: page = " + page);
				Inventory insertionInv = getBinderPage(page);
				ItemStack cardItem = storedCards.get(cardID);
				score += TradingCardManager.getInstance().getItemScore(cardItem);
				int insertionSpot = (cardID - 1) - ((page) * 45);
				//MessageHandler.getInstance().debug("Insertion Spot = " + insertionSpot + " cardID " + cardID);
				insertionInv.setItem(insertionSpot,  cardItem);
			}
			this.cardScore = score;
		} catch (ArrayIndexOutOfBoundsException ex){
			ex.printStackTrace();
		}
		MessageHandler.getInstance().debug("Updated " + storedCards.size() + " cards to " + playerName + "'s binder.");
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
		if ((!changed || !loaded)) {
			MessageHandler.getInstance().debug("Not saving profile for " + playerName + ". Loaded: " + loaded + " Changed:" + changed);
			return;
		}
		
		
		MessageHandler.getInstance().debug("Saving PlayerProfile of player " + playerName + " ...");
		PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, storedCards, cardScore);
		changed = !ParkManager.db.saveUser(profileCopy);

		if (changed) {
			MessageHandler.getInstance().log(ChatColor.RED + "PlayerProfile saving failed for player: " + ChatColor.WHITE + playerName
					+ " , uuid: " + uuid);
		}
		
	}
	
	public void scheduleAsyncSave() {
		new PlayerProfileSaveTask(this).runTaskAsynchronously(ParkManager.plugin);
		
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
		
		int page = (int) Math.ceil(cardID / 46);
		//MessageHandler.getInstance().debug("Debug: page = " + page);
		Inventory insertionInv = getBinderPage(page);
		insertionInv.setItem(cardID-1 - ((page) * 45), item);
		this.changed = true;
	}

	public int getBinderPageNumber(Inventory inv) {
		return cardBinderPages.indexOf(inv);
	}

	public ItemStack getStoredCard(int cardID) {
		return this.storedCards.get(cardID);
	}

	public void removeStoredCard(int cardID, ItemStack unownedCardItem) {
		if(unownedCardItem != null) {
			int page = (int) Math.ceil(cardID / 46);
			Inventory insertionInv = getBinderPage(page);
			insertionInv.setItem((cardID -1 ) - ((page) * 45), unownedCardItem);
		}
		this.storedCards.remove(cardID);
		this.changed = true;
	}
	
	public int getCardScore() {
		return this.cardScore;
		
	}
	
	public void addCardScore(int value) {
		this.cardScore += value;
		this.changed = true;
	}
	
	public void removeCardScore(int value) {
		this.cardScore -= value;
		this.changed = true;
		if(this.cardScore < 0) {
			MessageHandler.getInstance().debug("Trading card issue for "+ playerName + " - Players score was somehow in the negatives?");
			this.cardScore = 0;
		}
	}
	
	public void updateScore(int remove, int add) {
		this.cardScore -= remove;
		this.cardScore += add;
		this.changed = true;
		if(this.cardScore < 0) {
			MessageHandler.getInstance().debug("Trading card issue for "+ playerName + " - Players score was somehow in the negatives?");
			this.cardScore = 0;
		}
	}

	public BiMap<Integer,ItemStack> getStoredCards() {
		return storedCards;
	}



	
	public void setIsOpeningPack(boolean b) {
		this.isOpeningPack = b;
		
	}
	
	public boolean getOpeningPack() {
		return this.isOpeningPack;
	}

	public boolean spamCooldown() {
		boolean spam = ((System.currentTimeMillis() - this.spamCooldown) < 300);
		if(spam) {
			this.spamCount ++;
			if(spamCount >= 20) {
				Player player = Bukkit.getPlayer(playerName);
				player.kickPlayer(ChatColor.RED + "I SAID SLOW DOWN!");
			}
		} else if ((System.currentTimeMillis() - this.spamCooldown) > 10000) {
			this.spamCount = 0;
		}
		return spam;
	}
	
	public void updateSpamCooldown() {
		this.spamCooldown = System.currentTimeMillis();
	}


//	public Inventory getCardPackInventory() {
//		return cardPackInventory;
//		
//	}
//	
//	public void setCardPackInventory(Inventory inv) {
//		this.cardPackInventory = inv;
//	}


}
