package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.Util;

public class RandomrideCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(!UserManager.hasPlayerDataKey(player)) {
				player.sendMessage(ChatColor.RED + "You can't use that command right now.");
				return true;
			}
			
			PlayerData pData = UserManager.getPlayer(player);
			if(pData.isInRide()) {
				player.sendMessage(ChatColor.RED + "You can't use that command while you are on a ride.");
				return true;
			}
			List<Ride> rides = new ArrayList<Ride>();
			for(Ride r : DataStorage.getInstance().getRides()) {
				if(!r.getWarp().equals("Unknown") && !r.getWarp().isEmpty() && !(r.getWarp() == null)) {
					rides.add(r);
				}
			}
			
			if(rides.isEmpty()) {
				player.sendMessage(ChatColor.RED + "No applicable rides found.");
			}
			

			Random rand = Util.getRandom();
		    Ride selected = rides.get(rand.nextInt(rides.size()));
		    player.sendMessage(ChatColor.translateAlternateColorCodes(
		    		'&',"&aRandomly selected the &e"+ selected.getName() + "&a ride."));

		    player.performCommand("warp " + selected.getWarp());
		    return true;
			
			
		} else {
			sender.sendMessage("This command can only be used by players.");
		}
		return true;
	}

	
}
