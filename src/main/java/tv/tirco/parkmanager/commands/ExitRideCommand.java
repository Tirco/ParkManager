package tv.tirco.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class ExitRideCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage("WIP: This command is not yet implemented.");
		
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
				//Vehicle
				if(player.isInsideVehicle()) {
					player.getVehicle().removePassenger(player);
				}
				World world = Bukkit.getWorld("world"); //TODO Config?
				Location spawn = new Location(world, -29.5, 65, 161.5, 0f, 0f);
				player.teleport(spawn, TeleportCause.PLUGIN);
				player.sendMessage(ChatColor.YELLOW + "You have been removed from the ride.");
				
				pData.endRide();
			}
		}
		return true;
	}
}
