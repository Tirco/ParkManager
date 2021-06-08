package tv.tirco.parkmanager.storage.playerdata;

import org.bukkit.scheduler.BukkitRunnable;

public class PlayerProfileSaveTask extends BukkitRunnable {
	private PlayerProfile playerProfile;

	public PlayerProfileSaveTask(PlayerProfile playerProfile) {
		this.playerProfile = playerProfile;
	}

	@Override
	public void run() {
		playerProfile.save();
	}
}
