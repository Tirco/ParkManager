package tv.tirco.parkmanager.storage.playerdata;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
}
