package tv.tirco.parkmanager.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import tv.tirco.parkmanager.config.Aliases;

public class AliasCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Help
		//Get
		//Set
		//Edit
		//RunAsPlayer
		//RunAsConsole
		if(args.length < 1 || args[0].equalsIgnoreCase("help")) {
			
			
			return true;
		}
		
		if(args.length < 2) {
			//No alias specify.
			sender.sendMessage("Please specify an alias.");
			return true;
		}
		String alias = args[1];
		
		if(args[0].equalsIgnoreCase("create")) {
			if(Aliases.getInstance().isSet(alias)) {
				sender.sendMessage("This alias already exists. Please use the remove command to remove it.");
			}
			
		} else if(args[0].equalsIgnoreCase("get")) {
			List<String> commands = Aliases.getInstance().getAliasList(alias);
			for(String s : commands) {
				sender.sendMessage(s);
			}
			
		} else if(args[0].equalsIgnoreCase("edit")) {
			
		} else if(args[0].equalsIgnoreCase("add")) {
				
		} else if(args[0].equalsIgnoreCase("remove")) {
			
		} else if(args[0].equalsIgnoreCase("delete")) {
			
		}  else if(args[0].equalsIgnoreCase("runasplayer")) {
			
		} else if(args[0].equalsIgnoreCase("runasconsole")) {
			
		} else if(args[0].equalsIgnoreCase("save")) {
			Aliases.getInstance().save();
			
		}
		return false;
	}

}
