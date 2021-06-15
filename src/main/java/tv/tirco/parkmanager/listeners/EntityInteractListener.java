package tv.tirco.parkmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.data.Levelled;
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
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.TradingCards.TradingCardPackTask;
import tv.tirco.parkmanager.alias.Alias;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.Util;

public class EntityInteractListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickEntity(PlayerInteractEntityEvent e) {
		if(e.getRightClicked() != null) {
			Entity clicked = e.getRightClicked();
			Player player = e.getPlayer();
			if(!player.getGameMode().equals(GameMode.CREATIVE) || player.isSneaking()) {
				
				//Cancel if not 
				if(!(clicked.getType().equals(EntityType.MINECART) 
						|| clicked.getType().equals(EntityType.ARMOR_STAND)
						|| clicked.getType().equals(EntityType.PLAYER)
						|| Util.isNPC(clicked))) {
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
	public void onPlayerUse(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(player.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		
		//Is it okay to click this block?
		if(e.getClickedBlock() != null && e.getClickedBlock().getType() != Material.AIR) {
			if(Util.canBeInteracted(e.getClickedBlock().getType())){
				return;
			}
		}

		
		//Is clicking blocks allowed in this world?
		//Needed for some minigames that have doors etc.
		if(Util.rightClickBlockAllowed(player.getWorld().getName())) {
			return;
		} else {
			//Cancel the click
			e.setCancelled(true);
		}
		
		if(!e.getHand().equals(EquipmentSlot.HAND)) {
			return;
		}
		
		//NO LEFT CLICK ALLOWED
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			//Don't you point that NULL at me!
			if(e.getClickedBlock() == null) {
				return;
			}
			
			//Trashcan
			if(e.getClickedBlock().getType().equals(Material.CAULDRON)) {
				if(!(player.getInventory().getItemInMainHand() == null ||
						player.getInventory().getItemInMainHand().getType().equals(Material.AIR))) {
					player.sendMessage("Please don't hold an item in your hand while trying to interact with this block.");
				} else {
					//BlockData data = e.getClickedBlock().getBlockData();
					Levelled cauldronData = (Levelled) e.getClickedBlock().getBlockData();
					if(cauldronData.getLevel() == 3) {
						//Makes a new inventory not stored anywhere.
						player.openInventory(Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', 
								"&cTrashcan!")));
					}
				}
				//Clicked cauldron, nothing happened.
				e.setCancelled(true);
				return;
			}
		}
		
	    //Make sure we're doing a right click action
	    if(e.getAction().equals(Action.RIGHT_CLICK_AIR) 
	    || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){

		    //Main hand check
		    if(!e.getHand().equals(EquipmentSlot.HAND)) {
		    	return;
		    }
	    	
	    	//Use item check - Do we have an item in hand?
	    	ItemStack item = player.getInventory().getItemInMainHand();
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
		    			player.sendMessage(ChatColor.RED + "There seems to be an issue with your item. Please contact an administrator.");
		    			return;
		    		} else {
		    			if(alias.execute(player, item)) {
		    				player.sendMessage(ChatColor.GREEN + "Redeemed successfully!");
		    			} else {
		    				player.sendMessage(ChatColor.RED + "You can not redeem this item.");
		    			}
		    			return;
		    		}
		    	} else if(nbti.hasKey("isCardPack")) {
		    		
		    		//Check inv
		    		if(!UserManager.hasPlayerDataKey(player)) {
		    			player.sendMessage("Please wait, as your profile is not loaded yet.");
						return;
		    		}
					PlayerData pData = UserManager.getPlayer(player);
					if(!pData.isLoaded()) {
						player.sendMessage("Please wait, as your profile is not loaded yet.");
						return;
					}
					if(pData.spamCooldown())  {
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED + "Slow down!");
						return;
					} else {
						pData.updateSpamCooldown();
					}
					
					
					//Has atleast 3 empty slots?
					  int i = 0;
				      ItemStack[] cont = player.getInventory().getContents();
				      for (ItemStack itemStack : cont)
				        if (itemStack != null && itemStack.getType() != Material.AIR) {
				          i++;
				        }
				      i =  36 - i;
					  if(i < 3) {
						  player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GOLD + "You need at least 3 empty slots in your inventory to open a card pack.");
						  return;
					  }
					
						ItemStack removeItem = item.clone();
						removeItem.setAmount(1);
						player.getInventory().removeItem(removeItem);

					new TradingCardPackTask(ParkManager.parkManager, player, pData);
					return;
		    	}
		    }
	    	
	    	//Hat item?
		    
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

	    		if(player.getInventory().getHelmet() == null || player.getInventory().getHelmet().getType().equals(Material.AIR)) {
	    			player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
	    			player.getInventory().setItem(EquipmentSlot.HEAD, item);
	    			return;
	    		} else {
	    			player.sendMessage(ChatColor.RED + "Your are already wearing a hat.");
	    			return;
	    		}
	    	}
	    }
	}

}
