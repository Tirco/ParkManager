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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.util.Util;

public class ProtectionListener implements Listener{
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(player.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		
		//Don't you point that NULL at me!
		if(e.getClickedBlock() == null) {
			return;
		}
		
		//Is it okay to click this block?
		if(Util.canBeInteracted(e.getClickedBlock().getType())){
			return;
		}
		
		//Is clicking blocks allowed in this world?
		//Needed for some minigames that have doors etc.
		if(!Util.rightClickBlockAllowed(player.getWorld().getName())) {
			e.setCancelled(true);
		}
		
		if(!e.getHand().equals(EquipmentSlot.HAND)) {
			return;
		}
		
		//NO LEFT CLICK ALLOWED
		if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			e.setCancelled(true);
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
				} else {
					//player.sendMessage("Cauldron level: " + cauldronData.getLevel() + "/" + cauldronData.getMaximumLevel());
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

	
	//Disable breaking blocks for non creative players.
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if(!Util.blockBreakAllowed(e.getPlayer().getWorld().getName())) {
			e.setCancelled(true);
		}
		
	}
	


	//Disable placing blocks for non creative players.
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if(!Util.blockPlaceAllowed(e.getPlayer().getWorld().getName())) {
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

}
