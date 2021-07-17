package tv.tirco.parkmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.util.MessageHandler;
import tv.tirco.parkmanager.util.Util;


public class ResourcePackCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(Util.isGeyser(player)) {
				player.sendMessage(ChatColor.YELLOW + "The resourcepack is not Bedrock Compatible.");
				player.sendMessage(ChatColor.YELLOW + "It uses the CustomModelData parameter that bedrock does not have support for at the moment.");
				player.sendMessage(ChatColor.GOLD + "You can still play minigames at "
						+ ChatColor.GREEN + "/warp minigame" + ChatColor.GOLD + ".");
			}

			try {
				int version = Util.getVersion(player);
				if(version < 477 && version != 0) { //477 is 1.14 - https://wiki.vg/Protocol_version_numbers
						
					player.sendMessage(
							ChatColor.RED + "Error: " + ChatColor.YELLOW + "The resourcepack uses CustomModelData "
							+ "which was first added in version 1.14. We recommend that you update your client if you want to"
							+ " properly enjoy the rides in this park. We always recommend connecting with the latest compatible client.");
					player.sendMessage(ChatColor.GOLD + "If you can't connect with a newer client, you can still play minigames at "
							+ ChatColor.GREEN + "/warp minigame" + ChatColor.GOLD + ".");
					return true;
				}
			} catch(IllegalArgumentException ex) {
				//player.sendMessage("An error seems to have occurred while trying to execute this command...");
				MessageHandler.getInstance().debug("command /resourcepack threw an Illegal Argument Exception.");
			}

			player.performCommand("usepack parkage");
			//Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "usepack parkage " + player.getName());
			
			return true;
		} else {
			sender.sendMessage("This command can only be used by players.");
			return true;
		}
	}

}
