package tv.tirco.parkmanager.storage.playerdata;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import tv.tirco.parkmanager.ParkManager;

public class PlayerData {
	//Class used to access PlayerProfile information.
	
	private Player player;
	private PlayerProfile profile;

	private final FixedMetadataValue playerMetadata;
	
	public PlayerData(Player player, PlayerProfile profile) {
		String playerName = player.getName();
		UUID uuid = player.getUniqueId();

		// final Map<AttributeType, AttributeManager> attributeManagers = new
		// HashMap<AttributeType, AttributeManager>();

		this.setPlayer(player);
		this.playerMetadata = new FixedMetadataValue(ParkManager.plugin, playerName);
		this.profile = profile;

		if (profile.getUuid() == null) {
			profile.setUuid(uuid);
		}

	}
	
	public void save() {
		profile.save();
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public UUID getUuid() {
		return profile.getUuid();
	}
	
	public boolean isLoaded() {
		return profile.isLoaded();
	}
	
	public boolean isChanged() {
		return profile.isChanged();
	}
	
	public void logout(boolean syncSave) {
		Player thisPlayer = getPlayer();

		if (syncSave) {
			getProfile().save();
		} else {
			getProfile().scheduleAsyncSave();
		}

		UserManager.remove(thisPlayer);
	}

	public FixedMetadataValue getPlayerMetadata() {
		return playerMetadata;
	}

	public PlayerProfile getProfile() {
		return profile;
	}

	public void startRide(String identifier) {
		profile.startRide(identifier);
	}
	
	public void endRide() {
		profile.endRide();
	}
	
	public boolean isInRide() {
		return profile.isInRide();
	}

	
	public Long getStartTime() {
		return profile.getRideStartTime();
	}

	public String getRideIdentifier() {
		return profile.getRideIdentifier();
		
	}

	public List<Inventory> getBinderPages() {
		return profile.getBinderPages();
	}

	public Inventory getBinderPage(int page) {
		return profile.getBinderPage(page);
	}

	public void storeCard(int cardID, ItemStack item) {
		profile.storeCard(cardID, item);	
	}

	public int getBinderPageNumber(Inventory inv) {
		return profile.getBinderPageNumber(inv);
	}

	public ItemStack getStoredCard(int cardID) {
		return profile.getStoredCard(cardID);
	}
	
	public void removeStoredCard(int cardID) {
		removeStoredCard(cardID, null);
	}

	public void removeStoredCard(int cardID, ItemStack unownedCardItem) {
		profile.removeStoredCard(cardID, unownedCardItem);
	}

	public int getCardScore() {
		return profile.getCardScore();
	}

	public void removeCardScore(int score) {
		profile.removeCardScore(score);
		
	}

	public void updateScore(int remove, int add) {
		profile.updateScore(remove, add);
		
	}

	
	public void setIsOpeningPack(boolean b) {
		profile.setIsOpeningPack(b);
		
	}

	public boolean isOpeningPack() {
		return profile.getOpeningPack();
	}
	
	public boolean spamCooldown() {
		return profile.spamCooldown();
	}
	
	public void updateSpamCooldown() {
		profile.updateSpamCooldown();
	}
//	public Inventory getCardPackInventory() {
//		profile.getCardPackInventory();
//	}
//	
//	public void setCardPackInventory() {
//		profile.setCardPackInventory();
//	}

	public int getStoredCardAmount() {
		return profile.getStoredCards().size();
	}
	
}
