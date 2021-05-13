package tv.tirco.parkmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.tirco.parkmanager.storage.DataStorage;

public class RidesCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage("Currently added rides: - This command is a wip");
		if(sender instanceof Player) {
			Player player = (Player) sender;
			player.openInventory(DataStorage.getInstance().getRideMenu());
		}
		return true;
	}

}
