package tv.tirco.parkmanager.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.MessageHandler;
import tv.tirco.parkmanager.util.Util;

public class ParkmanagerCommand implements CommandExecutor, TabCompleter{
		List<String> baseArguments = ImmutableList.of("playerinfo","storeitem","getstoreditem","givestoreditem","deletestoreditem","eco");

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
			
			Player target = Bukkit.getPlayer(args[1]);
			if(target == null) {
				sender.sendMessage("Could not find a player with the name " + args[1]);
				return true;
			}
			
			getPlayerInfo(sender,target);
			return true;
		} else if(args[0].equalsIgnoreCase("eco")) {
			if(args.length <= 3) {
				sender.sendMessage("Usage: /parkmanager eco give <playername> amount");
				sender.sendMessage("This will automatically add their bonus multiplier and global bonus to the money given.");
				return true;
			}
			//   /parkmanager eco give Tirco 20 <true>
			
			//arg 2 isn't used, we don't
			if(!args[1].equalsIgnoreCase("give")) {
				sender.sendMessage("Unknown parameter " + args[1]);
				sender.sendMessage("Usage: /parkmanager eco give <playername> amount");
				sender.sendMessage("This will automatically add their bonus multiplier and global bonus to the money given.");
				return true;
			}
			
			
			Player target = Bukkit.getPlayer(args[2]);
			if(target == null) {
				sender.sendMessage("Could not find a player with the name " + args[2]);
				return true;
			}

			double amount = 0;
			try {
				amount = Double.parseDouble(args[3]);
			} catch(NumberFormatException ex) {
				sender.sendMessage("Could not parse " + args[3] + " to a Double value.");
				return true;
			}
			
			double globalModifier = Config.getInstance().getGlobalBonus();
			double playerModifier = Config.getInstance().getPlayerBonus(target);
			double totalModifier = globalModifier * playerModifier;
			
			double modifiedAmount = amount * (totalModifier); 
			
			boolean silenced = false;
			if(args.length >= 5) {
				silenced = args[4].equalsIgnoreCase("true");
			}
			
			ParkManager.getEconomy().depositPlayer(target, modifiedAmount);

			if(!silenced) {
				DecimalFormat df = new DecimalFormat("#.##");
				target.sendMessage(ChatColor.GREEN + "You earned $" + ChatColor.GOLD + df.format(modifiedAmount) + ChatColor.GRAY +
						" (" + df.format(amount) + " x " + df.format(totalModifier) + ")");
				
			}
			
			String message = (ChatColor.GREEN + "Paid " + ChatColor.GOLD + target.getName() + " " + ChatColor.GREEN + modifiedAmount + " $ - " + " Their modifier: " + totalModifier);
			if(sender instanceof Player) {
				sender.sendMessage(message);
			} else {
				MessageHandler.getInstance().log(Level.INFO, message);
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
			
			if(args.length > 2) {
				if(args[2].equalsIgnoreCase("true")) {
					overwrite = true;
				}
			}
			ItemStack item = player.getInventory().getItemInMainHand().clone();
			if(item == null) {
				sender.sendMessage("You need to be holding an item that should be stored.");
			}
			
			if(!overwrite && DataStorage.getInstance().getStoredItem(identifier) != null) {
				sender.sendMessage("There is already an item stored with that identifier. Use the command \"/parkmanager storeitem " + identifier + " true\" to overwrite it." );
			} else {
				DataStorage.getInstance().storeItem(identifier, item);
				sender.sendMessage("The item has been stored as " + identifier.toLowerCase());
				MessageHandler.getInstance().log(sender.getName() + " stored a new item of type " + item.getType().toString() + " with the identifier " + identifier.toLowerCase());
			}
			return true;
			
			
		} else if(args[0].equalsIgnoreCase("getstoreditem")) {
			//parkmanager getstoreditem <identifier> <amount>
			if(!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by players.");
				return true;
			}
			Player player = (Player) sender;
			if(args.length > 1) {
				String identifier = args[1];
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
			if(args.length < 3) {
				sender.sendMessage("Usage: /parkmanager givestoreditem <identifier> <name> <amount>");
				return true;
			}
			String identifier = args[1];
			ItemStack item = DataStorage.getInstance().getStoredItem(identifier);
			if(item == null) {
				sender.sendMessage("There is no stored item with the identifier " + identifier);
				return true;
			}
			
			if(args.length > 3) {
				try {
					int amount = Integer.parseInt(args[3]);
					item.setAmount(amount);
				} catch (NumberFormatException ex) {
					//Ignore
				}
			}
			
			Player target = Bukkit.getPlayer(args[2]);
			if(target == null) {
				@SuppressWarnings("deprecation")
				OfflinePlayer oTarget = Bukkit.getOfflinePlayer(args[2]);
				if(!oTarget.hasPlayedBefore()) {
					sender.sendMessage("No such player - " + args[2]);
					return true;
				} else {
					UUID uuid = oTarget.getUniqueId();
					DataStorage.getInstance().addPlayerOwedItem(uuid, item);
					sender.sendMessage("Player is offline - Successfully added item as an oweditem.");
					return true;
				}
			}
			
			if(Util.hasEmptySlots(target, 1)) {
				target.getInventory().addItem(item);
				sender.sendMessage("Successfully added item to their inventory.");
				return true;
			} else {
				DataStorage.getInstance().addPlayerOwedItem(target.getUniqueId(), item);
				target.sendMessage(ChatColor.YELLOW + "We tried to add item(s) to your inventory, but there wasn't enough space. Use the command " + ChatColor.GOLD + "/oweditems" + ChatColor.YELLOW + " when you have cleared up some room to claim the item(s).");
				sender.sendMessage("Player had a full inventory - Successfully added item as an oweditem.");
				return true;
			}
			
			
		} else if(args[0].equalsIgnoreCase("deletestoreditem")) {
			if(args.length > 1) {
				String identifier = args[1];
				if(DataStorage.getInstance().removeStoredItem(identifier)) {
					sender.sendMessage("Successfully deleted the item " + identifier);
				}
			} else {
				sender.sendMessage("Please specify which stored item you would like to delete.");
			}
			
		}
		return false;
		

	}

	private void getPlayerInfo(CommandSender sender, Player target) {
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
		sender.sendMessage("ResourcePack SHA1:");
		sender.sendMessage("UUID: " + target.getUniqueId().toString());
		sender.sendMessage("GameMode: " + target.getGameMode().toString());
		sender.sendMessage("Location: X:" + target.getLocation().getBlockX() + " Y:" + target.getLocation().getBlockY()+ " Z:" + target.getLocation().getBlockZ());
		sender.sendMessage("World: " + target.getWorld().getName());
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
		
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return StringUtil.copyPartialMatches(args[args.length -1], baseArguments, new ArrayList<String>(baseArguments.size()));
		} else {
			if(args[0].equalsIgnoreCase("eco")) {
				switch(args.length) {
				case 2:
					return ImmutableList.of("give");
				case 3:
					return null;
				case 4:
					return ImmutableList.of("<amount>");
				case 5:
					return ImmutableList.of("<silenced>","true","false");
				default:
					return null;
				}
			} else if(args[0].equalsIgnoreCase("playerinfo")) {
				return null; //Null = playernames with defaut spigot settings.
			} else {
				//Stored Item
				return StringUtil.copyPartialMatches(args[args.length -1], DataStorage.getInstance().getStoredItemNames(), new ArrayList<String>(DataStorage.getInstance().getStoredItemNames()));
			}
		}
	}
}
