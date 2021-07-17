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
		int modifier;
		int seconds;
		if(e.getItem().getType().equals(Material.POTION)) {
			ItemMeta meta = item.getItemMeta();
			if(meta.hasCustomModelData()) {
				player.getInventory().removeItem(item);
				switch (meta.getCustomModelData()) {
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
					seconds = (Util.getRandom().nextInt(30) + 5); //(15-180);
					modifier = Util.getRandom().nextInt(2); //(0-1);
					int effect = Util.getRandom().nextInt(4);
					if( effect == 0) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, seconds*20, modifier, false));
					} else if (effect == 1) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, seconds*20, modifier, false));
					} else if (effect == 2){
						player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, seconds*20, modifier, false));
					} else {
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, seconds*20, modifier, false));
					}
					 
					break;
				case 7:
				case 8:
				case 9:
					seconds = (Util.getRandom().nextInt(155) + 15); //(15-180);
					modifier = Util.getRandom().nextInt(3); //(0-2);
					//Random effect:
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, seconds*20, modifier, false));
					break;
				default: break;
				}

	            Bukkit.getServer().getScheduler().runTaskLater(ParkManager.plugin, new Runnable() {
	                public void run() {
	                    player.getInventory().remove(Material.GLASS_BOTTLE);
	                }
	            }, 1L);
			}
			
		}
	}
	
}
