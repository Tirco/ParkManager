package tv.tirco.parkmanager.listeners;

import org.bukkit.GameMode;
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
import tv.tirco.parkmanager.util.Util;

public class ProtectionListener implements Listener{
	
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
