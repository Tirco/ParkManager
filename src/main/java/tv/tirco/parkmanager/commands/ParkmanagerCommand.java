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
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.HoverEvent.Action;
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
			
			
			int page = 1;
			if(args.length <=2) {
				
			} else {
				try {
					page = Integer.parseInt(args[2]);
					
				} catch (NumberFormatException ex) {
					page = 1;
				}
			}
			if(page < 1) {
				page = 1;
			}
			getPlayerInfo(sender,target,page);
			
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
			
			PlayerData pData = UserManager.getPlayer(target);
			if(pData.getMoneyLimitReached(true)) {
				target.sendMessage(ChatColor.RED + "You have reached the money limit for this hour.");
				sender.sendMessage("Could not pay " + target.getName() + " as they have reached their hourly limit.");
				return true;
				//Update money limit;
				
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
			DecimalFormat df = new DecimalFormat("#.##");
			double modifiedAmount =(amount * totalModifier); 
			
			pData.addRecentMoney(modifiedAmount);
			
			boolean silenced = false;
			if(args.length >= 5) {
				silenced = args[4].equalsIgnoreCase("true");
			}
			
			ParkManager.getEconomy().depositPlayer(target, modifiedAmount);

			if(!silenced) {
				
				target.sendMessage(ChatColor.GREEN + "You earned $" + ChatColor.GOLD + df.format(modifiedAmount) + ChatColor.GRAY +
						" (" + df.format(amount) + " x " + df.format(totalModifier) + ")");
				
			}
			String reason = "Not set";
			if(args.length >= 6) {
				String message = "";
				for (int i = 5; i < args.length; i++) {
				    message = message + args[i] + " ";
				}
				reason = message;
			}
			
			String message = (ChatColor.GREEN + "Paid " + ChatColor.GOLD + target.getName() + " " + ChatColor.GREEN + df.format(modifiedAmount) + " $ - " + " Their modifier: " + df.format(totalModifier)
			+ " Reason: " + reason);
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

	private void getPlayerInfo(CommandSender sender, Player target, int page) {
		int maxPage = 2;
		//Geyser checks
		String geyserStatus = ChatColor.RED + "Geyser is not loaded.";
		if(Util.isGeyser(target)) {
			geyserStatus = ChatColor.GREEN + "Is a Bedrock player.";
		} else {
			geyserStatus = ChatColor.RED + "Not a Bedrock player.";
		}	
		//Viaversion checks
		int versionID = Util.getVersion(target);
		String versionName = Util.getVersionString(target);
		
		//String protocolVersion = player.getHandle().playerConnection.networkManager.getVersion();


		sender.sendMessage(ChatColor.GOLD + "------" + target.getName() + " - Page " + page + "------");
		if(page == 1) {
			sender.spigot().sendMessage(
					getComp(ChatColor.GOLD + "Information About: ", ChatColor.GREEN + target.getName(), //Text layout
							"/teleport " + target.getName(), ClickEvent.Action.RUN_COMMAND, //Click action
							ChatColor.GREEN + "Click to teleport to " + target.getName())); //Hover text.
			sender.sendMessage(ChatColor.GOLD + "Geyser status: " + geyserStatus);
			sender.sendMessage(ChatColor.GOLD + "ViaVersion: " + ChatColor.GREEN + + versionID + " - " + versionName);
			sender.spigot().sendMessage(
					getComp(ChatColor.GOLD + "IP: ", ChatColor.GREEN + "<SENSITIVE - Hover to SEE>", //Text layout
							target.getAddress().toString(), ClickEvent.Action.COPY_TO_CLIPBOARD, //Click action
							ChatColor.GREEN + "IP: " + target.getAddress().toString()+"\n"+"Click to copy.")); //Hover text.
			sender.spigot().sendMessage(
					getComp(ChatColor.GOLD + "UUID: ", ChatColor.GREEN + target.getUniqueId().toString(), //Text layout
							target.getUniqueId().toString(), ClickEvent.Action.COPY_TO_CLIPBOARD, //Click action
							ChatColor.GREEN + "Click to copy UUID to clipboard.")); //Hover text.
			sender.spigot().sendMessage(
					getComp(ChatColor.GOLD + "GameMode: ", ChatColor.GREEN + target.getGameMode().toString(), //Text layout
							"/gamemode adventure " + target.getName(), ClickEvent.Action.SUGGEST_COMMAND, //Click action
							ChatColor.GREEN + "Click to get a suggested\ngamemode command for " + target.getName())); //Hover text.
			sender.spigot().sendMessage(
					getComp(ChatColor.GOLD + "Location:", 
							ChatColor.RED + " X:" + ChatColor.WHITE + target.getLocation().getBlockX() + 
							ChatColor.GREEN + " Y:" + ChatColor.WHITE + target.getLocation().getBlockY() + 
							ChatColor.YELLOW + " Z:" + ChatColor.WHITE + target.getLocation().getBlockZ(), //Text layout
							target.getLocation().getBlockX() + " " +
							target.getLocation().getBlockY() + " " +
							target.getLocation().getBlockZ(), ClickEvent.Action.COPY_TO_CLIPBOARD, //Click action
							ChatColor.GREEN + "Click to copy Location to clipboard.")); //Hover text.
			sender.spigot().sendMessage(
					getComp(ChatColor.GOLD + "World: ", ChatColor.GREEN + target.getWorld().getName(), //Text layout
							"/mvinfo " + target.getWorld().getName(), ClickEvent.Action.RUN_COMMAND, //Click action
							ChatColor.GREEN + "Click to get Multiverse Info about " + target.getWorld().getName())); //Hover text.
			sender.sendMessage(ChatColor.GOLD + "OwedItems: " + ChatColor.GREEN + DataStorage.getInstance().owedItems(target.getUniqueId()).size());
			if(target.getActivePotionEffects().isEmpty()) {
				sender.sendMessage(ChatColor.GOLD + "Potion Effects: " + ChatColor.GREEN + "None.");
			} else {
				String effectString = "";
				for(PotionEffect e : target.getActivePotionEffects()) {
					effectString += 
							ChatColor.GOLD + e.getType().getName() 
							+ ChatColor.GREEN + " Level: " + ChatColor.WHITE + (e.getAmplifier() + 1) + ChatColor.GREEN +" Duration: " 
							+ ChatColor.WHITE + (e.getDuration()/20) + "s\n";
				}
				sender.spigot().sendMessage(getComp(ChatColor.GOLD + "Potion Effects: ", ChatColor.GREEN +""+  target.getActivePotionEffects().size() + " - Hover for list", //Text layout
						"/effect clear " + target.getName(), ClickEvent.Action.SUGGEST_COMMAND, //Click action
						effectString)); //Hover text.
			}
			sender.sendMessage(ChatColor.GOLD + "Fly Enabled: " + ChatColor.GREEN + target.getAllowFlight() + ChatColor.GOLD + " Is flying: " + ChatColor.GREEN + target.isFlying());
			sender.sendMessage(ChatColor.GOLD + "Currently opened Inventory: " + ChatColor.WHITE + target.getOpenInventory().getTitle());
			sender.spigot().sendMessage(
					getComp(ChatColor.GOLD + "Money: ", ChatColor.GREEN  + ParkManager.getEconomy().format(ParkManager.getEconomy().getBalance(target)) + "", //Text layout
							"/eco give " + target.getName() + " 0", ClickEvent.Action.SUGGEST_COMMAND, //Click action
							ChatColor.GREEN + "/eco give <name> <amount>\n"
							+ ChatColor.WHITE + " - Gives flat amount of money.\n"
							+ ChatColor.GREEN + "/parkmanager eco give <name> <amount>\n"
							+ ChatColor.WHITE + " - Gives money with rank/global bonus.")); //Hover text.
		} else {

			if(UserManager.hasPlayerDataKey(target)) {
				PlayerData pData = UserManager.getPlayer(target);
				if(pData.isLoaded()) {
					sender.sendMessage(ChatColor.GOLD + "PlayerData Loaded: " + ChatColor.GREEN + "true");
					sender.sendMessage(ChatColor.GOLD + "PlayerData Changed: " + ChatColor.GREEN + pData.isChanged());
					sender.sendMessage(ChatColor.GOLD + "Current Ride: " + ChatColor.GREEN + pData.getRideIdentifier());
					sender.sendMessage(ChatColor.GOLD + "Trading Card Score: " + ChatColor.GREEN + pData.getCardScore());
					sender.sendMessage(ChatColor.GOLD + "Stored Cards: " + ChatColor.GREEN + pData.getStoredCardAmount());
					sender.sendMessage(ChatColor.GOLD + "Money in last hour: " + ChatColor.GREEN + pData.getMoneyInLastHour() + ChatColor.GOLD + " / " + ChatColor.RED +  Config.getInstance().getMoneyLimit() + ChatColor.GOLD + ".");
				} else {
					sender.sendMessage(ChatColor.GOLD + "PlayerData Loaded: " + ChatColor.RED + "false");
				}
				sender.spigot().sendMessage(
						getComp(ChatColor.GOLD + "HeadHunter Found: " , PlaceholderAPI.setPlaceholders(target, "%headhunter_player_found_amount%"), //Text layout
								"/hha seelistas " + target.getName(), ClickEvent.Action.SUGGEST_COMMAND, //Click action
								ChatColor.GREEN + "Click to see the list")); //Hover text.
				sender.sendMessage(PlaceholderAPI.setPlaceholders(target, ChatColor.GOLD + "ProCosmetics Shards: " + ChatColor.GREEN + "%procosmetics_coins%"));
				sender.sendMessage(PlaceholderAPI.setPlaceholders(target, ChatColor.GOLD + "OldCombatMechanics PvP Mode:" + ChatColor.GREEN + " %ocm_pvp_mode%"));
				sender.spigot().sendMessage(
						getComp(ChatColor.GOLD + "MurderMystery Stats:" , ChatColor.GREEN + " <Hover>", //Text layout
								"/mm stats " + target.getName(), ClickEvent.Action.SUGGEST_COMMAND, //Click action
								 PlaceholderAPI.setPlaceholders(target,ChatColor.GREEN + "Kills: " + "%murdermystery_kills%" + "\n"
								+ "Deaths: " + "%murdermystery_deaths%" + "\n"
								+ "Games Played: " + "%murdermystery_games_played%" + "\n"
								+ "Wins: " + "%murdermystery_wins%" + "\n"
								+ "Losses: " + "%murdermystery_loses%" + "\n"
								+ "Highscore: " + "%murdermystery_highest_score%" + "\n"))); //Hover text.
				sender.spigot().sendMessage(
						getComp(ChatColor.GOLD + "mBedwars:" , ChatColor.GREEN + " <Hover>", //Text layout
								"/mbedwars stats " + target.getName(), ClickEvent.Action.SUGGEST_COMMAND, //Click action
								ChatColor.GREEN + PlaceholderAPI.setPlaceholders(target, "Rank: " + "%mbedwars_stats-rank%" + "\n"
								+ "Wins: " + "%mbedwars_stats-wins%" + "\n"
								+ "Loses: " + "%mbedwars_stats-loses%" + "\n"
								+ "Rounds: " + "%mbedwars_stats-rounds_played%" + "\n"
								+ "Kills: " + "%mbedwars_stats-kills%" + "\n"
								+ "Deaths: " + "%mbedwars_stats-deaths%" + "\n"
								+ "Playtime: " + "%mbedwars_stats-play_time%"))); //Hover text.
				sender.spigot().sendMessage(
						getComp(ChatColor.GOLD + "Votes:" , ChatColor.GREEN + " <Hover>", //Text layout
								"/voteparty checkvotes " + target.getName(), ClickEvent.Action.SUGGEST_COMMAND, //Click action
								ChatColor.GREEN + PlaceholderAPI.setPlaceholders(target, 
								   "Daily: #" + "%voteparty_placement_daily%" + " (%voteparty_totalvotes_daily%)"+ "\n"
								+ "Weekly: #%voteparty_placement_weekly%" + " (%voteparty_totalvotes_weekly%)"+ "\n"
								+ "Monthly: #%voteparty_placement_monthly%" + " (%voteparty_totalvotes_monthly%)"+ "\n"
								+ "Annually: #%voteparty_placement_annually%" + " (%voteparty_totalvotes_annually%)"+ "\n"
								+ "Alltime: #%voteparty_placement_alltime%" + " (%voteparty_totalvotes_alltime%)"))); //Hover text.
			}
		}
		
		//Footer
		TextComponent prev = new TextComponent((page > 1 ? ChatColor.GREEN : ChatColor.GRAY) + "<--");
		TextComponent next = new TextComponent((page < maxPage ? ChatColor.GREEN : ChatColor.GRAY) + "-->");
		TextComponent lines = new TextComponent(ChatColor.GOLD + " ----- ");
		if(page > 1) {
			prev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/parkmanager playerinfo " + target.getName() + " " + (page - 1)));
		}
		if(page < maxPage) {
			next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/parkmanager playerinfo " + target.getName() + " " + (page + 1)));
		}
		
		BaseComponent[] component = new ComponentBuilder().append(lines).append(prev).append(lines).append(next).append(lines).create();
		sender.spigot().sendMessage(component);
		
	}
	
	private BaseComponent[] getComp(String text, String clickableText, String clickText, ClickEvent.Action action, String hoverText) {
		TextComponent comp = new TextComponent(clickableText); //Information about: <player> - click to TP
		comp.setClickEvent(new ClickEvent(action, clickText));
		if(hoverText != null) {
			comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text(hoverText)));
		}
		BaseComponent[] component = new ComponentBuilder(text).append(comp).create();
		return component;
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
				case 6:
					return ImmutableList.of("<msg>");
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
