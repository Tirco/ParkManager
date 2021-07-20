package tv.tirco.parkmanager.commands;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableList;

public class GrantTagCommand implements CommandExecutor,TabCompleter{
	
	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length < 2) {
			sender.sendMessage("Please specify a player and a tag identifier.");
			return false;
		}
		
		String playername = args[0];
		
		Player player = Bukkit.getPlayer(playername);
		if(player == null) {
			OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(args[0]);
			if(!oPlayer.hasPlayedBefore()) {
				sender.sendMessage("Unknown player: " + playername);
			}
		}
		String perm = "deluxetags.tag." + args[1];
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + playername + " permission set " + perm + " true");
		
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args.length) {
		case 1:
			return null;
		case 2:
			return ImmutableList.of("<identifier>");
		default: return null;
		}
	}
}
