package tv.tirco.parkmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;

public class RidesCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage("Currently added rides: - This command is a wip");
		for(Ride r : DataStorage.getInstance().getRides()) {
			sender.sendMessage(r.getName());
		}
		return true;
	}

}
