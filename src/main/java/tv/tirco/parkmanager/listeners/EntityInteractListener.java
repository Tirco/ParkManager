package tv.tirco.parkmanager.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.alias.Alias;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.util.Util;

public class EntityInteractListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickEntity(PlayerInteractEntityEvent e) {
		if(e.getRightClicked() != null) {
			Entity clicked = e.getRightClicked();
			Player player = e.getPlayer();
			if(!player.getGameMode().equals(GameMode.CREATIVE) || player.isSneaking()) {
				
				//Cancel if not 
				if(!(clicked.getType().equals(EntityType.MINECART) ||
						clicked.getType().equals(EntityType.ARMOR_STAND)
						|| clicked.getType().equals(EntityType.PLAYER))) {
					e.setCancelled(true);	
				}
				
			}
			
			if(clicked instanceof ArmorStand) {
				//Tags:
				//seat - Used when player uses /sit or public chairs.
				//
				
				//Don't sit if creative and not sneaking.
				if(player.getGameMode().equals(GameMode.CREATIVE) && !player.isSneaking()) {
					return;
				}
				
				ArmorStand armorStand = (ArmorStand) clicked;
				if(armorStand.getScoreboardTags().contains("seat")) {
					armorStand.addPassenger(player);
					e.setCancelled(true);
				}
				
			} else if(clicked instanceof ItemFrame) {
				if(player.getGameMode().equals(GameMode.CREATIVE) && !player.isSneaking()) {
					return;
				}
				ItemFrame frame = (ItemFrame) clicked;
		        ItemStack item = frame.getItem();
		        if(item != null && !item.getType().equals(Material.AIR)) {
		        	String name = "";
		        	if(item.getItemMeta().hasDisplayName()) {
		        		name = item.getItemMeta().getDisplayName();
		        		String nameClean = ChatColor.stripColor(name);
		        		if(nameClean.toLowerCase().startsWith("right-click to play:")) {
		        			String gameName = nameClean.substring(21);
		        			gameName = gameName.replaceAll(" ", "");
		        			String cmd = "mbg play " + gameName;
		        			player.performCommand(cmd);
		        			//player.sendMessage("Attempting to start game: " + gameName);
		        			
		        		}
		        	}
		        }
			}
		}
	}

	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerUnmount(EntityDismountEvent e) {

		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		//Player player = (Player) e.getEntity();
		
		if(e.getDismounted() instanceof ArmorStand) {
			ArmorStand armorStand = (ArmorStand) e.getDismounted();
			if(armorStand.getScoreboardTags().contains("delete-on-dismount")) {
				armorStand.remove();
			}
		}
//		
//		if(e.isCancelled()) {
//			return;
//		}
//		
//		if(UserManager.hasPlayerDataKey(player)) {
//			PlayerData ridePlayer = UserManager.getPlayer(player);
//			if(ridePlayer.isInRide()) {
//				e.setCancelled(true);
//				player.sendMessage(ChatColor.YELLOW + "If you want to leave the ride you are on, use the command /exitride");
//				//ridePlayer.endRide();
//			}
//		}

	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerUse(PlayerInteractEvent event){
	    Player p = event.getPlayer();

	 
	    //Make sure we're doing a right click action
	    if(event.getAction().equals(Action.RIGHT_CLICK_AIR) 
	    || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
	    	
		    
		    //Main hand check
		    if(!event.getHand().equals(EquipmentSlot.HAND)) {
		    	return;
		    }
	    	
	    	if(event.getClickedBlock() != null
	    	&& 
	    	event.getClickedBlock().getType() != null 
	    	&&
	    	!event.getClickedBlock().getType().equals(Material.AIR)) {
	    		Material clicked = event.getClickedBlock().getType();
	    		if(Util.canBeInteracted(clicked)) {
	    			return;
	    		}
	    	}
	    	
	    	//Use item check - Do we have an item in hand?
	    	ItemStack item = p.getInventory().getItemInMainHand();
	    	if(item == null || item.getType().equals(Material.AIR)) {
	    		return;
	    	}
	    	
//NBTI check
		    NBTItem nbti = new NBTItem(item);
		    if(nbti.hasNBTData()) {
		    	//Check for commands:
		    	if(nbti.hasKey("alias")) {
		    		String aliasIdentifer = nbti.getString("alias");
		    		Alias alias = DataStorage.getInstance().getAlias(aliasIdentifer);
		    		if(alias == null) {
		    			p.sendMessage(ChatColor.RED + "There seems to be an issue with your item. Please contact an administrator.");
		    			return;
		    		} else {
		    			if(alias.execute(p, item)) {
		    				p.sendMessage(ChatColor.GREEN + "Redeemed successfully!");
		    			} else {
		    				p.sendMessage(ChatColor.RED + "You can not redeem this item.");
		    			}
		    			return;
		    		}
		    	}
		    }
	    	
	    	
//GOLDEN AXE   //TODO Gold_Ingot, Iron_Ingot
		    if(item.getType().equals(Material.GOLDEN_AXE)) {
	    		if(!item.hasItemMeta()) {
	    			return;
	    		}
	    		
	    		if(!item.getItemMeta().hasCustomModelData()) {
	    			return;
	    		}
	    		
	    		if(item.getItemMeta().getCustomModelData() < 1) {
	    			return;
	    		}

	    		if(p.getInventory().getHelmet() == null || p.getInventory().getHelmet().getType().equals(Material.AIR)) {
	    			p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
	    			p.getInventory().setItem(EquipmentSlot.HEAD, item);
	    			return;
	    		} else {
	    			p.sendMessage(ChatColor.RED + "Your are already wearing a hat.");
	    			return;
	    		}
	    	}
	    }
	}

}
