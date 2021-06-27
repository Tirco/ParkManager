package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.Util;

public class ParkmanagerCommand implements CommandExecutor, TabCompleter{
		List<String> baseArguments = ImmutableList.of("playerinfo","storeitem","getstoreditem","givestoreditem","deletestoreditem");

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length <= 0) {
			sender.sendMessage("Please specify an argument!");
			for(String s :baseArguments) {
				sender.sendMessage("- " + s);
			}
			return true;
		}
		if(args[0].equalsIgnoreCase("playerinfo")) {
			if(args.length <= 1) {
				sender.sendMessage("Please specify which player you would like information about!");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				sender.sendMessage("Could not find a player with the name " + args[0]);
				return true;
			}
			
			//Geyser checks
			String geyserStatus = ChatColor.RED + "Geyser is not loaded.";
			if(Util.isGeyser(target)) {
				geyserStatus = "Is a Bedrock player.";
			} else {
				geyserStatus = "Not a Bedrock player.";
			}	
			//Viaversion checks
			int versionID = Util.getVersion(target);
			String versionName = Util.getVersionString(target);
			
			//String protocolVersion = player.getHandle().playerConnection.networkManager.getVersion();
			sender.sendMessage("Information about " + target.getName());
			sender.sendMessage("Geyser status: " + geyserStatus);
			sender.sendMessage("ViaVersion: " + versionID + " - " + versionName);
			sender.sendMessage("UUID: " + target.getUniqueId().toString());
			sender.sendMessage("GameMode: " + target.getGameMode().toString());
			sender.sendMessage("Location: X:" + target.getLocation().getBlockX() + " Y:" + target.getLocation().getBlockY()+ " Z:" + target.getLocation().getBlockZ());
			sender.sendMessage("World: X:" + target.getWorld().getName());
			sender.sendMessage("OwedItems: " + DataStorage.getInstance().owedItems(target.getUniqueId()).size());
			if(UserManager.hasPlayerDataKey(target)) {
				PlayerData pData = UserManager.getPlayer(target);
				if(pData.isLoaded()) {
					sender.sendMessage("PlayerData Loaded: True");
					sender.sendMessage("PlayerData Changed: " + pData.isChanged());
					sender.sendMessage("Current Ride: " + pData.getRideIdentifier());
					sender.sendMessage("--");
					sender.sendMessage("Trading Card Score: " + pData.getCardScore());
					sender.sendMessage("Stored Cards: " + pData.getStoredCardAmount());
				} else {
					sender.sendMessage("PlayerData Loaded: False");
				}
				
			}
			return true;
		}
		//
		//args[1] = Identifier

		
		if(args[0].equalsIgnoreCase("storeitem")) {
			//parkmanager storeitem <identifier> <overwrite:true>
			
			if(!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by players.");
				return true;
			}
			
			Player player = (Player) sender;
			
			if(args.length <= 1) {
				sender.sendMessage("/parkmanager storeitem <identifier> (true) <- overwrite?");
				return true;
			}
			String identifier = args[1];
			boolean overwrite = false;
			if(args[2] != null && args[2].equalsIgnoreCase("true")) {
				overwrite = true;
			}
			
			ItemStack item = player.getInventory().getItemInMainHand();
			if(item == null) {
				sender.sendMessage("You need to be holding an item that should be stored.");
			}
			
			if(!overwrite && DataStorage.getInstance().getStoredItem(identifier) != null) {
				sender.sendMessage("There is already an item stored with that identifier. Use the command \"/parkmanager storeitem " + identifier + " true\" to overwrite it." );
			} else {
				DataStorage.getInstance().storeItem(identifier, item);
			}
			return true;
			
			
		} else if(args[0].equalsIgnoreCase("getstoreditem")) {
			//parkmanager getstoreditem <identifier> <amount>
			if(!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by players.");
				return true;
			}
			Player player = (Player) sender;
			if(args[0] != null) {
				String identifier = args[0];
				ItemStack item = DataStorage.getInstance().getStoredItem(identifier);
				if(item == null) {
					player.sendMessage("There is no item stored with that identifier. (" + identifier + ")");
					return true;
				}
				
				player.getInventory().addItem(item);
				player.sendMessage("Successfully added the item " + identifier + " to your inventory.");
				return true;
			} else {
				player.sendMessage("Please specify which item to get.");
				return true;
			}
			
			
			
			
		} else if(args[0].equalsIgnoreCase("givestoreditem")) {
			//parkmanager givestoreditem <identifier> <name> <amount>
			
		} else if(args[0].equalsIgnoreCase("deletestoreditem")) {
			if(args[0] != null) {
				String identifier = args[0];
				if(DataStorage.getInstance().removeStoredItem(identifier)) {
					sender.sendMessage("Successfully deleted the item " + identifier);
				}
			} else {
				sender.sendMessage("Please specify which stored item you would like to delete.");
			}
			
		}
		return false;
		

	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[args.length -1], baseArguments, new ArrayList<String>(baseArguments.size()));
		case 2:
			if(args[0].equalsIgnoreCase("playerinfo")) {
				return null; //Null = playernames with defaut spigot settings.
			} else {
				//Stored Item
				return StringUtil.copyPartialMatches(args[args.length -1], DataStorage.getInstance().getStoredItemNames(), new ArrayList<String>(DataStorage.getInstance().getStoredItemNames()));
			}
		default:
			return null;
    	}
			
	}
}
