package tv.tirco.parkmanager.listeners;

import java.util.ArrayList;
import java.util.List;

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
	public void onPlayerJoin(PlayerItemConsumeEvent e) {
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
				List<PotionEffect> effects = new ArrayList<PotionEffect>();
				effects.add(new PotionEffect(PotionEffectType.SPEED, 30*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.SPEED, 60*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.SPEED, 30*20, 1, false));
				effects.add(new PotionEffect(PotionEffectType.SPEED, 60*20, 1, false));
				effects.add(new PotionEffect(PotionEffectType.SPEED, 120*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.SPEED, 240*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 30*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 20*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 20*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 30*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.HUNGER, 20*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.HUNGER, 30*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.HUNGER, 40*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.SLOW, 30*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.ABSORPTION, 30*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 30*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.GLOWING, 20*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.UNLUCK, 20*20, 0, false));
				effects.add(new PotionEffect(PotionEffectType.LUCK, 20*20, 0, false));
				//Random effect:
				
				player.addPotionEffect(effects.get(Util.getRandom().nextInt(effects.size())));
	            Bukkit.getServer().getScheduler().runTaskLater(ParkManager.plugin, new Runnable() {
	                public void run() {
	                    player.getInventory().remove(Material.GLASS_BOTTLE);
	                }
	            }, 1L);
			}
			
		}
	}
	
}
