package tv.tirco.parkmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.util.Util;

public class ConsumeListener implements Listener{

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent e) {
		//Spawn
		Player player = e.getPlayer();
		
		if(!player.getWorld().getName().equalsIgnoreCase("world")) {
			return;
		}
		
		if(e.getItem() == null ) {
			return;
		}
		
		
		
		ItemStack item = e.getItem();
		if(e.getItem().getType().equals(Material.POTION)) {
			ItemMeta meta = item.getItemMeta();
			if(meta.hasCustomModelData()) {
				player.getInventory().removeItem(item);
				int seconds = (Util.getRandom().nextInt(155) + 15); //(15-180);
				int modifier = Util.getRandom().nextInt(3); //(0-2);
				//Random effect:
				
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, seconds*20, modifier, false));
	            Bukkit.getServer().getScheduler().runTaskLater(ParkManager.plugin, new Runnable() {
	                public void run() {
	                    player.getInventory().remove(Material.GLASS_BOTTLE);
	                }
	            }, 1L);
			}
			
		}
	}
	
}
