package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.DataStorage;

public class SetAdminWarpCommand implements CommandExecutor,TabCompleter{
	
	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//adminwarp <name>
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be sendt by players.");
			return true;
		}
		if(args.length < 1) {
			sender.sendMessage("Please specify a warp name.");
			return true;
		}
		
		Player player = (Player) sender;
		if(!DataStorage.getInstance().isWarp(args[0].toLowerCase())) {
			Location loc = player.getLocation();
			DataStorage.getInstance().setWarp(args[0].toLowerCase(), loc);
			sender.sendMessage("The adminwarp " + args[0].toLowerCase() + " has been set to World: " + loc.getWorld().getName() + " X " + loc.getBlockX() + " Y " + loc.getBlockY() + " Z " + loc.getBlockZ());
		} else {
			sender.sendMessage(ChatColor.RED + "There is already a warp with that name. Use /delwarp to remove it.");
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
