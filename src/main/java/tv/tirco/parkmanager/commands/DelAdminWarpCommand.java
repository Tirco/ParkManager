package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.DataStorage;

public class DelAdminWarpCommand implements CommandExecutor,TabCompleter{
	
	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//adminwarp <name>

		if(args.length < 1) {
			sender.sendMessage("Please specify a warp.");
			return true;
		}
		if(DataStorage.getInstance().isWarp(args[0].toLowerCase())) {
			DataStorage.getInstance().delWarp(args[0].toLowerCase());
			sender.sendMessage("The warp " + args[0].toLowerCase() + " has been removed.");
		} else {
			sender.sendMessage(ChatColor.RED + "There is no such warp.");
		}
		
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		Map<String,Location> warps = DataStorage.getInstance().getAdminWarps();
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0], warps.keySet(), new ArrayList<String>(warps.size()));
		default: return null;
		}
	}
}
