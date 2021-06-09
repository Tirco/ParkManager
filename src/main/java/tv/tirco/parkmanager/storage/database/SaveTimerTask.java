package tv.tirco.parkmanager.storage.database;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.PlayerProfileSaveTask;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.MessageHandler;


public class SaveTimerTask extends BukkitRunnable {
	@Override
	public void run() {
		// do save stuff
		int count = 1;
		for (PlayerData pData : UserManager.getPlayers()) {
			new PlayerProfileSaveTask(pData.getProfile()).runTaskLaterAsynchronously(ParkManager.plugin, count);
			count++;
		}
		if (count > 1) {
			MessageHandler.getInstance().debug("AutoSave - Saved " + (count - 1) + " players");
		}
		
		//Load our top 10 later.
		Bukkit.getScheduler().runTaskLaterAsynchronously(ParkManager.plugin, new Runnable() {

			@Override
			public void run() {
				MessageHandler.getInstance().debug("Loading top 10...");
				TradingCardManager.getInstance().setTop10(ParkManager.db.getTop10());
				MessageHandler.getInstance().debug("Done!");
			}
			
		}, 20 + count);

	}
}
