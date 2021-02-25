package tv.tirco.parkmanager.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.tasks.PlayerProfileLoadingTask;

public class JoinLeaveListener implements Listener{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		//Spawn
		Player player = e.getPlayer();
		Location spawn = new Location(player.getWorld(), -29.5, 65, 161.5, 0f, 0f);
		player.teleport(spawn, TeleportCause.PLUGIN);
		
		new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(ParkManager.plugin, 60);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		//TODO if in ride remove from ride.
		if(player.isInsideVehicle()) {
			player.getVehicle().removePassenger(player);
		}
		Location spawn = new Location(player.getWorld(), -29.5, 65, 161.5, 0f, 0f);
		player.teleport(spawn, TeleportCause.PLUGIN);
	}
}
