package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import tv.tirco.parkmanager.config.RidesConfig;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;

public class RideAdminCommand implements CommandExecutor,TabCompleter{
	
	List<String> commands = ImmutableList.of("help","create","loadconfig","setname","setwarp","setdescription","setitem","setmaxpayout","setdefaultpayperminute","startride","stopride","save","reloadrides");

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//help
		//rideadmin create <identifier>
		//rideadmin setname <identifier> name can be long
		//rideadmin setdescription <identifier> name can be long
		//rideadmin setmaxpayout <identifier> name can be long
		//rideadmin setdefaultpayperminute <identifier> name can be long
		if(args.length < 1 || args[0].equalsIgnoreCase("help")) {
			sender.sendMessage("No. Ask Tirco :P");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("save")) {
			RidesConfig.getInstance().saveRides();
			sender.sendMessage("Saved all rides!");
			return true;
		} else if(args[0].equalsIgnoreCase("reloadrides")) {
			DataStorage.getInstance().rebuildRideMenu();
			sender.sendMessage("Reloaded all rides!");
			return true;
		} else if(args[0].equalsIgnoreCase("loadconfig")) {
			RidesConfig.getInstance().newInstance();
			RidesConfig.getInstance().loadKeys();
			sender.sendMessage("Loaded all rides!");
			return true;
		}
		
		if(args.length < 2) {
			sender.sendMessage("Please specify an identifier for the ride.");
			return true;
		}
		String identifier = args[1];
		
		if(args[0].equalsIgnoreCase("create")) {
			if(DataStorage.getInstance().getRide(identifier) != null) {
				sender.sendMessage("Error: A ride with that identifier already exists!");
				return true;
			} else {
				Ride ride = new Ride(identifier);
				DataStorage.getInstance().addRide(ride);
				sender.sendMessage("The ride " + identifier + " has been created.");
				return true;
			}
		}
		
		Ride ride = DataStorage.getInstance().getRide(identifier);
		if(ride == null) {
			sender.sendMessage("No such ride-identifier found.");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("setitem")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item == null) {
					player.sendMessage("You need to hold an item to use this.");
					return true;
				} else {
					ride.setIcon(item);
					DataStorage.getInstance().rebuildRideMenu();
					player.sendMessage("Item added. RideMenu is getting an update!");
					return true;
				}
			} else {
				sender.sendMessage("This command must be run by a player.");
				return true;
			}
		}
		
		
		if(args.length < 3) {
			sender.sendMessage("You need to specify a value.");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("startride")) {
			String name = args[2];
			Player target = Bukkit.getPlayer(name);
			if(target == null) {
				sender.sendMessage("No such player.");
				return true;
			}
			ride.start(target);
			return true;
			
		} else if(args[0].equalsIgnoreCase("stopride")) {
			String name = args[2];
			Player target = Bukkit.getPlayer(name);
			if(target == null) {
				sender.sendMessage("No such player.");
				return true;
			}
			ride.stop(target);
			return true;
		}

		
		//args[2 - End]
		String argument = String.join(" ", Arrays.asList(args).subList(2, args.length).toArray(new String[]{}));
		
		if(args[0].equalsIgnoreCase("setname")) {
			ride.setName(argument);
			sender.sendMessage("The name of " + identifier + " has now been set to: " + argument);
			return true;
		} else if(args[0].equalsIgnoreCase("setwarp")) {
				ride.setWarp(argument);
				sender.sendMessage("The warp of " + identifier + " has now been set to: " + argument);
				return true;
		} else if(args[0].equalsIgnoreCase("setdescription")) {
			ride.setDescription(argument);
			sender.sendMessage("The description of " + identifier + " has now been set to: " + argument);
			return true;
		} else if(args[0].equalsIgnoreCase("setmaxpayout")) {
			double maxPayout = 0;
			try {
				maxPayout = Double.parseDouble(argument);
			} catch(NumberFormatException ex) {
				sender.sendMessage("Unable to parse " + argument + " to double");
				return true;
			}
			ride.setMaxPayout(maxPayout);
			sender.sendMessage("The description of " + identifier + " has now been set to: " + argument);
			return true;
		} else if(args[0].equalsIgnoreCase("setdefaultpayperminute")) {
			double payPerMinute = 0;
			try {
				payPerMinute = Double.parseDouble(argument);
			} catch(NumberFormatException ex) {
				sender.sendMessage("Unable to parse " + argument + " to double");
				return true;
			}
			ride.setPayPerMinute(payPerMinute);
			sender.sendMessage("The description of " + identifier + " has now been set to: " + argument);
			return true;
		}
		
		sender.sendMessage("Unknown argument!");
		sender.sendMessage("Available: " + commands);
		return true;

	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> rides = DataStorage.getInstance().getRides().stream().map(r -> r.getIdentifier()).collect(Collectors.toList());
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<String>(commands.size()));
		case 2:
			return StringUtil.copyPartialMatches(args[1], rides, new ArrayList<String>(rides.size()));
		default:
			return null;
    	}
			
	}
}
