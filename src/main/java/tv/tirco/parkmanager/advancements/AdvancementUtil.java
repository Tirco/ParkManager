package tv.tirco.parkmanager.advancements;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import tv.tirco.parkmanager.util.MessageHandler;

public class AdvancementUtil {
	
	private static AdvancementUtil instance;
	
	public static AdvancementUtil getInstance() {
		if (instance == null) {
			instance = new AdvancementUtil();
		}

		return instance;
	}
	
	public AdvancementUtil() {

	}
	
	public boolean hasCompletedAdvancement(Player player, String key) {
		Advancement adv = Bukkit.getAdvancement(NamespacedKey.fromString(key));
		if(adv == null) {
			return false;
		}
		AdvancementProgress prog = player.getAdvancementProgress(adv);
		return prog.getRemainingCriteria().isEmpty();
	}
	
	public boolean advancementExists(String key) {
		Advancement adv = Bukkit.getAdvancement(NamespacedKey.fromString(key));
		if(adv == null) {
			return false;
		}
		return true;
	}
	
	public void grantAdvancement(Player player, NamespacedKey key) {
		Advancement adv = Bukkit.getAdvancement(key);
		if(adv == null) {
			MessageHandler.getInstance().log("Missing Advancement: " + key.toString());
			return;
		}
		
		AdvancementProgress progress = player.getAdvancementProgress(adv);
		for(String criteria : progress.getRemainingCriteria())
		    progress.awardCriteria(criteria);
		
	}

	@SuppressWarnings("deprecation")
	public void grantRideAdvancement(String identifier, Player player) {
		grantAdvancement(player, new NamespacedKey("parkage","root"));
		grantAdvancement(player, new NamespacedKey("parkage",identifier.toLowerCase()));
	}
}