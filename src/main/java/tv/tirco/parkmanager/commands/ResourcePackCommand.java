package tv.tirco.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.util.MessageHandler;
import us.myles.ViaVersion.api.Via;


public class ResourcePackCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;

			try {
				int version = Via.getAPI().getPlayerVersion(player.getUniqueId());
				if(version < 477) { //477 is 1.14 - https://wiki.vg/Protocol_version_numbers
						
					player.sendMessage(
							ChatColor.RED + "Error: " + ChatColor.YELLOW + "The resourcepack uses CustomModelData "
							+ "which was first added in version 1.14. We recommend that you update your client if you want to"
							+ " properly enjoy the rides in this park.");
					player.sendMessage(ChatColor.GOLD + "A way to access Minigame areas without entering the park is being worked on.");
					return true;
				}
			} catch(IllegalArgumentException ex) {
				MessageHandler.getInstance().debug("command /resourcepack threw an Illegal Argument Exception.");
			}


			
			//Execute the command for the player.
			//The ResourcePackDownloader plugin doesn't actually have the command listed,
			//it's reacting to the CommandPre event thingy.
			//player.chat("/givemeresourcepack");
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "usepack parkage " + player.getName());
			
			return true;
		} else {
			sender.sendMessage("This command can only be used by players.");
			return true;
		}
	}

}
