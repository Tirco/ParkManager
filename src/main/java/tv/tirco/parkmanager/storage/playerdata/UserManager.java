package tv.tirco.parkmanager.storage.playerdata;


import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.tasks.PlayerProfileLoadingTask;
import tv.tirco.parkmanager.util.MessageHandler;

public class UserManager {

	private UserManager() {
	}

	/**
	 * Track a new user.
	 * 
	 * @param recklessPlayer the player profile to start tracking.
	 */
	public static void track(PlayerData pData) {
		pData.getPlayer().setMetadata(ParkManager.playerDataKey, new FixedMetadataValue(ParkManager.plugin, pData));
	}

	/**
	 * Remove a user.
	 * 
	 * @param player - the player object
	 */
	public static void remove(Player player) {
		player.removeMetadata(ParkManager.playerDataKey, ParkManager.plugin);
	}

	/**
	 * Clear all users.
	 */
	public static void clearAll() {
		for (Player player : ParkManager.plugin.getServer().getOnlinePlayers()) {
			remove(player);
		}
	}

	/**
	 * Save all users on this thread aka. sync
	 */
	public static void saveAll() {
		ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(ParkManager.plugin.getServer().getOnlinePlayers());
		MessageHandler.getInstance().debug("Saving players... (" + onlinePlayers.size() + ")" );

		for (Player player : onlinePlayers) {
			try {
				getPlayer(player).getProfile().save();
			} catch (Exception e) {
				MessageHandler.getInstance().log("WARNING: failed to save profile of player" + player.getName());
			}
		}
	}

	/**
	 * get the RecklessPlayer of a player by name.
	 * 
	 * @param playerName The name of the player whose McMMOPlayer to retreive.
	 * @return the players RecklessPlayer object
	 */
	public static PlayerData getPlayer(String playerName) {
		return retrieveRecklessPlayer(playerName, false);
	}

	public static PlayerData getOfflinePlayer(OfflinePlayer player) {
		if (player instanceof Player) {
			return getPlayer((Player) player);
		}

		return retrieveRecklessPlayer(player.getName(), true);
	}

	public static PlayerData getOfflinePlayer(String playerName) {
		return retrieveRecklessPlayer(playerName, true);
	}

	public static PlayerData getPlayer(Player player) {
		return (PlayerData) player.getMetadata(ParkManager.playerDataKey).get(0).value();
	}

	private static PlayerData retrieveRecklessPlayer(String playerName, boolean offlineValid) {
		Player player = ParkManager.plugin.getServer().getPlayerExact(playerName);

		if (player == null) {
			if (!offlineValid) {
				MessageHandler.getInstance().debug("A valid PlayerData object could not be found for " + playerName + ".");
			}

			return null;
		}

		return getPlayer(player);
	}

	public static boolean hasPlayerDataKey(Entity entity) {
		return entity != null && entity.hasMetadata(ParkManager.playerDataKey);
	}

	public static Collection<PlayerData> getPlayers() {
		Collection<PlayerData> playerCollection = new ArrayList<PlayerData>();

		for (Player player : ParkManager.plugin.getServer().getOnlinePlayers()) {
			if (hasPlayerDataKey(player)) {
				playerCollection.add(getPlayer(player));
			}
		}

		return playerCollection;
	}
	

	public static void profileCleanup(String playerName) {
		Player player = ParkManager.plugin.getServer().getPlayerExact(playerName);

		if (player != null) {
			UserManager.remove(player);
			new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(ParkManager.plugin, 1); // 1 Tick delay to
																								// ensure the player is
																								// marked as online
																								// before we begin
																								// loading
		}
	}

	public static void sendErrorMessage(Player player, String error) {
		player.sendMessage("Your account seems to not be loaded properly.");
		player.sendMessage("Please relogg in an attempt to fix this issue.");
		player.sendMessage("If it percists, contact an administrator.");
		player.sendMessage(ChatColor.RED + "ERROR: " + error);
	}

}
