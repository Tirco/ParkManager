package tv.tirco.parkmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class RidesCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//sender.sendMessage("Currently added rides: - This command is a wip");
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!UserManager.hasPlayerDataKey(player)) {
				player.sendMessage("Please wait a few seconds before executing this command...");
				return true;
			}
			
			PlayerData pData = UserManager.getPlayer(player);
			if(pData.isInRide()) {
				player.sendMessage(ChatColor.RED  + "You can't use this command while on a ride.");
				return true;
			}
			player.openInventory(DataStorage.getInstance().getRideMenu());
		}
		return true;
	}

}
