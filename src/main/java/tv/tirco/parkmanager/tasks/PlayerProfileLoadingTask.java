package tv.tirco.parkmanager.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.PlayerProfile;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.MessageHandler;


public class PlayerProfileLoadingTask extends BukkitRunnable {
	private static final int MAX_TRIES = 3;
	private final Player player;
	private int attempt = 0;

	public PlayerProfileLoadingTask(Player player) {
		this.player = player;
	}

	public PlayerProfileLoadingTask(Player player, int attempt) {
		this.player = player;
		this.attempt = attempt;
	}

	// ASYNC TASK
	// DO NOT MODIFY PlayerData FROM HERE!!!
	public void run() {
		// Is the player online?
		if (!player.isOnline()) {
			MessageHandler.getInstance().debug("Aborting profile loading recovery for " + player.getName() + " - player logged out");
			return;
		}
		// increase counter and try to load.

		attempt++;
		//VoidRPG.VoidRPGPlugin.debug("Begin loading profile for player " + player.getName() + " attempt: " + attempt);

		//PlayerProfile profile = ParkManager.db.loadPlayerProfile(player.getName(), player.getUniqueId(), true, true);
		
		PlayerProfile profile = ParkManager.getDB().loadPlayerProfile(player.getName(), player.getUniqueId(), true, true);
		
		if (profile.isLoaded()) {
			MessageHandler.getInstance().debug("Profile is loaded, applying...");
			new ApplySuccessfulProfile(new PlayerData(player, profile)).runTask(ParkManager.plugin);
			return;
		}
		// failed max times.
		if (attempt >= MAX_TRIES) {
			MessageHandler.getInstance().log("Giving up on attempting to load the PlayerProfile for " + player.getName());

			return;
		}

		// retry
		new PlayerProfileLoadingTask(player, attempt).runTaskLaterAsynchronously(ParkManager.plugin, 100 * attempt);
	}

	private class ApplySuccessfulProfile extends BukkitRunnable {

		private final PlayerData pData;

		private ApplySuccessfulProfile(PlayerData pData) {
			this.pData = pData;
		}

		public void run() {
			if (!player.isOnline()) {
				//VoidRPG.log("Aborting profile loading recovery for " + player.getName() + " - player logged out");
				return;
			}

			UserManager.track(pData);
			//VoidRPG.log("Debug - Profile Applied and tracked.");

		}

	}

}
