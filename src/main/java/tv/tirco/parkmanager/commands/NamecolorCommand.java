package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;

public class NamecolorCommand implements CommandExecutor, TabCompleter{

    List<String> colors = ImmutableList.of(
    		"AQUA","BLACK","BLUE","DARK_AQUA","DARK_BLUE","DARK_GRAY","DARK_GREEN","DARK_PURPLE","DARK_RED","GOLD","GRAY","GREEN","LIGHT_PURPLE","RED","WHITE","YELLOW");
	
	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			///nick <name> &6player.get
			if(args.length < 1) {
				sender.sendMessage(ChatColor.GOLD + "Please specify a color!");
				return true;
			}
			ChatColor colorCode = getColorCode(args[0]);
			if(colorCode == null) {
				sender.sendMessage(ChatColor.RED + "You can not use " + ChatColor.WHITE + args[0] + ChatColor.RED + " as a color code. Valid colors are:");
				String s = "";
				for(String c : colors) {
					s += getColorCode(c) + c + " ";
				}
				sender.sendMessage(s);
				return true;
			}
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "nick " + player.getName() + " " + colorCode + player.getName());
		} else {
			sender.sendMessage("This command can only be used by players.");
		}
		return false;
	}

	private ChatColor getColorCode(String string) {
		if(colors.contains(string.toUpperCase())) {
			return ChatColor.of(string.toUpperCase());
		}
		else {
			return null;
		}
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0], colors, new ArrayList<String>(colors.size()));
		default: return null;
		}
	}

}
