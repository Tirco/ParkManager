package tv.tirco.parkmanager.commands;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;

import net.clownercraft.ccRides.ccRidesPlugin;
import net.clownercraft.ccRides.config.ConfigHandler;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.MessageHandler;

public class ExitRideCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//sender.sendMessage("WIP: This command is not yet implemented.");
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!UserManager.hasPlayerDataKey(player)) {
				player.sendMessage(ChatColor.DARK_RED + "Your playerdata is not loaded.");
				player.sendMessage(ChatColor.YELLOW + "Please leave and re-join the server to fix this.");
				return true;
			}
			
			PlayerData pData = UserManager.getPlayer(player);
			if(!pData.isInRide()) {
				player.sendMessage(ChatColor.YELLOW + "You're not currently on a ride. Please use the warp command to change locations.");
			} else {
				

				boolean ejected = false;
				
				//ccRides?
				MessageHandler.getInstance().debug("-- Debug for force-exiting player " + player.getName() + " --");
				MessageHandler.getInstance().debug("Checking if it's ccrides..");
				if(ParkManager.ccRidesEnabled()) {
					MessageHandler.getInstance().debug("CCRides is enabled");
					ConfigHandler configHandler = ccRidesPlugin.getInstance().getConfigHandler();
					ConcurrentHashMap<UUID, String> ridePlayers = configHandler.ridePlayers;
					if(ridePlayers.containsKey(player.getUniqueId())) {
						MessageHandler.getInstance().debug("Player was found in a ccRides ride!");
						configHandler.rides.get(ridePlayers.get(player.getUniqueId())).ejectPlayer(player, false);
						ejected = true;
					} else {
						MessageHandler.getInstance().debug("Player was NOT found in a ccRides ride.");
					}

				}
				
				Location spawn = ParkManager.parkManager.spawn;
				
				//TrainCarts?
				if(!ejected) {
					MessageHandler.getInstance().debug("Checking if it's TrainCarts..");
					MinecartMember<?> member = MinecartMemberStore.getFromEntity(player.getVehicle());
					if(member != null) {
						MessageHandler.getInstance().debug("Player was found in a TrainCarts ride!");
						member.eject();
						player.teleport(spawn, TeleportCause.PLUGIN);
						ejected = true;
					}
				}

				
				if(!ejected) {
					MessageHandler.getInstance().debug("Player was not found in ccRides or TrainCarts... Attempting force eject");
					//Vehicle
					if(player.isInsideVehicle()) {
						player.getVehicle().removePassenger(player);
					}

					player.teleport(spawn, TeleportCause.PLUGIN);
					player.sendMessage(ChatColor.YELLOW + "An attempt to remove you from the ride has been done.");
					player.sendMessage(ChatColor.YELLOW + "If you are still on the ride, you can wait for it to end, or reconnect to the server.");
				}

				
				pData.endRide();
				return true;
			}
		}
		return true;
	}
}
