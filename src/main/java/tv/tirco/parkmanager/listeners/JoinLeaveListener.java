package tv.tirco.parkmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
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
		World world = Bukkit.getWorld("world"); //TODO Config?
		Location spawn = new Location(world, -29.5, 65, 161.5, 0f, 0f);
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
		World world = Bukkit.getWorld("world"); //TODO Config?
		Location spawn = new Location(world, -29.5, 65, 161.5, 0f, 0f);
		player.teleport(spawn, TeleportCause.PLUGIN);
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		if(e.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
			//Gamemode failsafe.
			e.getPlayer().setGameMode(GameMode.ADVENTURE);
		}
	}
}
