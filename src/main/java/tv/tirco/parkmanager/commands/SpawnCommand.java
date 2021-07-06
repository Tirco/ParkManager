package tv.tirco.parkmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tv.tirco.parkmanager.util.Util;

public class SpawnCommand implements CommandExecutor{
	
	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		int version = Util.getVersion(player);
		if(Util.isGeyser(player) || ((version != 0 && version < 477))) {
			player.performCommand("warp minigames");
		} else {
			player.performCommand("warp spawn");
		}
		return true;
	}
}
