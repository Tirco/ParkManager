package tv.tirco.parkmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import net.md_5.bungee.api.ChatColor;

public class ProtectionListener implements Listener{
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(player.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		
		if(e.getClickedBlock() == null) {
			return;
		}
		
		if(canBeClicked(e.getClickedBlock().getType())){
			return;
		} else {
			if(!isAllowedWorld(e.getPlayer().getWorld().getName())) {
				e.setCancelled(true);
			}
			
			if(!e.getHand().equals(EquipmentSlot.HAND)) {
				return;
			}
			//Trashcan?
			if(e.getClickedBlock().getType().equals(Material.CAULDRON)) {
				//BlockData data = e.getClickedBlock().getBlockData();
				Levelled cauldronData = (Levelled) e.getClickedBlock().getBlockData();
				if(cauldronData.getLevel() == 3) {
					player.openInventory(Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', 
							"&cTrashcan!")));
				} else {
					player.sendMessage("Cauldron level: " + cauldronData.getLevel() + "/" + cauldronData.getMaximumLevel());
				}
				//String dataString = "minecraft:cauldron[level=3]";
				//data.getAsString();
			}
		}
	}
	
    @EventHandler
    public void itemFrameItemRemoval(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
        	if(e.getDamager() instanceof Player) {
        		Player p = (Player) e.getDamager();
        		if(p.getGameMode().equals(GameMode.CREATIVE)) {
        			return;
        		}
        	}
            e.setCancelled(true);
        }
    }
	
	private boolean canBeClicked(Material mat) {
		switch(mat) {
		//Buttons
		case STONE_BUTTON:
		case OAK_BUTTON:
		case SPRUCE_BUTTON:
		case BIRCH_BUTTON:
		case JUNGLE_BUTTON:
		case ACACIA_BUTTON:
		case DARK_OAK_BUTTON:
		case CRIMSON_BUTTON:
		case WARPED_BUTTON:
		case POLISHED_BLACKSTONE_BUTTON:
		//Pressureplates
		case STONE_PRESSURE_PLATE:
		case OAK_PRESSURE_PLATE:
		case SPRUCE_PRESSURE_PLATE:
		case BIRCH_PRESSURE_PLATE:
		case JUNGLE_PRESSURE_PLATE:
		case ACACIA_PRESSURE_PLATE:
		case DARK_OAK_PRESSURE_PLATE:
		case CRIMSON_PRESSURE_PLATE:
		case WARPED_PRESSURE_PLATE:
		case POLISHED_BLACKSTONE_PRESSURE_PLATE:
		case LIGHT_WEIGHTED_PRESSURE_PLATE:
		case HEAVY_WEIGHTED_PRESSURE_PLATE:
		//Containers
		case ENDER_CHEST:
		//Signs
		case OAK_SIGN:
		case ACACIA_SIGN:
		case SPRUCE_SIGN:
		case BIRCH_SIGN:
		case DARK_OAK_SIGN:
		case JUNGLE_SIGN:
		case CRIMSON_SIGN:
		case WARPED_SIGN:
		case OAK_WALL_SIGN:
		case ACACIA_WALL_SIGN:
		case SPRUCE_WALL_SIGN:
		case BIRCH_WALL_SIGN:
		case DARK_OAK_WALL_SIGN:
		case JUNGLE_WALL_SIGN:
		case CRIMSON_WALL_SIGN:
		case WARPED_WALL_SIGN:
			return true;
		default: 
			return false;
		}
	}
	
	//Disable breaking blocks for non creative players.
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if(!isAllowedWorld(e.getPlayer().getWorld().getName())) {
			e.setCancelled(true);
		}
		
	}
	


	//Disable placing blocks for non creative players.
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if(!isAllowedWorld(e.getPlayer().getWorld().getName())) {
			e.setCancelled(true);
		}
	}
	
	
	//Stop taking items from ArmorStands
	@EventHandler
	public void onPlayerInteractWithArmorstand(PlayerArmorStandManipulateEvent e) {
		Player player = e.getPlayer();
		if(player.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		} else {
			e.setCancelled(true);
		}
	}
	
	//Stop Item Frames from breaking
    @EventHandler
    public void onFrameBreak(HangingBreakByEntityEvent event) {
        //Do nothing if not a player
        if(!(event.getRemover() instanceof Player)) {
        	event.setCancelled(true);
        	return;
        }
        Player player = (Player) event.getRemover();
        if(event.getEntity() instanceof ItemFrame && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }

    }

	private boolean isAllowedWorld(String name) {
		if(name.equalsIgnoreCase("world_TheLab")
				|| name.equalsIgnoreCase("Murder")) {
			return true;
		}
		return false;
	}
}
