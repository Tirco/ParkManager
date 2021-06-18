package tv.tirco.parkmanager.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.util.Util;

public class ParkmanagerCommand implements CommandExecutor,TabCompleter{
	

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			//Geyser checks
			String geyserStatus = ChatColor.RED + "Geyser is not loaded.";
			if(ParkManager.geyserEnabled) {
				if(Util.isGeyser(player)) {
					geyserStatus = "Is a geyser player.";
				} else {
					geyserStatus = "Not a geyser player, or unknown.";
				}	
			}

			//Viaversion checks
			int versionID = Util.getVersion(player);
			String versionIDString = (versionID == 0) ? versionID + "" : "Unknown";
			
			//String protocolVersion = player.getHandle().playerConnection.networkManager.getVersion();
			
			player.sendMessage("Geyser status: " + geyserStatus);
			player.sendMessage("ViaVersion: " + versionIDString);
			
			
			

		}
		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args.length) {
		case 1:
		default:
			return null;
    	}
			
	}
}
