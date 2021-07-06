package tv.tirco.parkmanager.TradingCards.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.TradingCards.TradingCard;
import tv.tirco.parkmanager.TradingCards.TradingCardCondition;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;
import tv.tirco.parkmanager.TradingCards.TradingCardPackTask;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.Util;

public class TradingCardAdminCommand implements CommandExecutor, TabCompleter{
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("openpack")) {
				Player target;
				if(args.length > 1) {
					//Target;
					target = Bukkit.getPlayer(args[1]);
					if(target == null) {
						sender.sendMessage("No such player");
						return true;
					}
				} else {
					//Self use;
					if(sender instanceof Player);
					target = (Player) sender;
				}
				
				//Execute:
					if(!UserManager.hasPlayerDataKey(target)) {
						sender.sendMessage("Error: Playerdata not loaded");
						return true;
					}
					PlayerData pData = UserManager.getPlayer(target);
					if(!pData.isLoaded()) {
						sender.sendMessage("Error: Playerdata not loaded");
						return true;
					}
					if(!Util.hasEmptySlots(target,3)) {
						sender.sendMessage("The player does not have enough empty slots in their inventory.");
						return true;
					}
					new TradingCardPackTask(ParkManager.parkManager, target, pData);
					sender.sendMessage("Making " + target.getName() + " open a cardpack!");
					return true;
			} else if(args[0].equalsIgnoreCase("forceload")) {
				TradingCardManager.getInstance().loadTopTen();
				sender.sendMessage("Loading top 10!");
				return true;
			} else if(args[0].equalsIgnoreCase("forcesave")) {
				UserManager.saveAll();
				sender.sendMessage("All players saved!");
				return true;
			} else if(args[0].equalsIgnoreCase("top")) {
				LinkedHashMap<String, Integer> map = TradingCardManager.getInstance().getTopTen();
				int rank = 1;
				for(String s : map.keySet()) {
					sender.sendMessage(rank + " " + s + " - " + map.get(s));
					rank ++;
				}
				return true;
			} else if(args[0].equalsIgnoreCase("getpack")) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = TradingCardManager.getInstance().getCardPackItem();
					

					player.getInventory().addItem(item);
					player.sendMessage("Item received!");
					return true;
				} else {
					sender.sendMessage("This command can only be used by players.");
					return true;
				}
			} else if(args[0].equalsIgnoreCase("givepack")) {
				if(args.length > 1) {
					//Target;
					Player target = Bukkit.getPlayer(args[1]);
					if(target == null) {
						sender.sendMessage("No such player");
						return true;
					}
					int amount = 1;
					
					//How many?
					if(args.length > 2) {
						try {
							amount = Integer.parseInt(args[2]);
						} catch(NumberFormatException ex) {
							//Ignore
							amount = 1;
						}
					}
					
					ItemStack pack = TradingCardManager.getInstance().getCardPackItem();
					pack.setAmount(amount);
					
					if(!Util.hasEmptySlots(target, 1)) {
						DataStorage.getInstance().addPlayerOwedItem(target.getUniqueId(), pack);
						target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThere was no space in your inventory, "
								+ "so we're holding on to the item for now. Please use the command &a/oweditems&e to get your items."));
						target.sendMessage("Please use the command /oweditem to redeem it.");
					} else {
						target.getInventory().addItem(pack);
					}
					sender.sendMessage("Gave " + amount + " cardpacks to " + target.getName());
				} else {
					sender.sendMessage("Please specify a target.");
				}
			} else if(args[0].equalsIgnoreCase("getcard")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage("This argument can only be used by players. use givecard instead.");
					return true;
				}
				Player player = (Player) sender;

				ItemStack cardItem = getCardFromStrings(args);
				
				if(cardItem == null) {
					sender.sendMessage("Error - No such card!");
					return true;
				}
				
				player.getInventory().addItem(cardItem);
				player.sendMessage("Here you go!");
				return true;
			} else if(args[0].equalsIgnoreCase("givecard")) {
				if(args.length <= 2) { 
					sender.sendMessage("Please specify the player to give a card, and which card to give.");
				}
				Player target = Bukkit.getPlayer(args[1]);
				if(target == null) {
					sender.sendMessage("Unknown player....");
					return true;
				}

				ItemStack cardItem = getCardFromStrings(args);
				
				if(cardItem == null) {
					sender.sendMessage("Error - No such card!");
					return true;
				}
				
				if(Util.hasEmptySlots(target, 1)) {
					target.getInventory().addItem(cardItem);
					target.sendMessage("You received a card pack!");
				} else {
					DataStorage.getInstance().addPlayerOwedItem(target.getUniqueId(), cardItem);
					target.sendMessage(ChatColor.translateAlternateColorCodes('&', 
							"&eYou were supposed to be given a cardpack, but did not have an empty slot in your inventory. You can claim the card pack when you have an empty slot by using the &f/oweditems&e command."));
				}
				
				
				return true;
			} else if(args[0].equalsIgnoreCase("cardinfo")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage("Only players can perform this command.");
					return true;
				}
				Player player = (Player) sender;
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item != null && item.getType().equals(Material.BLAZE_POWDER)) {
					ItemMeta meta = item.getItemMeta();
					NBTItem nbti = new NBTItem(item);
					if(nbti.hasNBTData()) {
						player.sendMessage(ChatColor.GOLD + " ");
						player.sendMessage(ChatColor.GOLD + "--- Card Info ---");
						player.sendMessage(ChatColor.GREEN + "Name: " + ChatColor.WHITE + meta.getDisplayName());
						player.sendMessage(ChatColor.GREEN + "Model: " + ChatColor.WHITE + meta.getCustomModelData());
						player.sendMessage(ChatColor.GREEN + "Lore: ");
						for(String s : meta.getLore()) {
							player.sendMessage(ChatColor.WHITE + " - " + s);
						}
						player.sendMessage(ChatColor.GREEN + "NBT: ");
						for(String s: nbti.getKeys()) {
							switch(nbti.getType(s)) {
							case NBTTagString:
								player.sendMessage(s + ": " + nbti.getString(s));
								break;
							case NBTTagInt:
								player.sendMessage(s + ": " + nbti.getInteger(s));
								break;
							case NBTTagDouble:
								player.sendMessage(s + ": " + nbti.getDouble(s));
								break;
							case NBTTagLong:
								player.sendMessage(s + ": " + nbti.getLong(s));
								break;
							default:
								break;
							}
						}
					return true;
					}
				} 
				sender.sendMessage(ChatColor.RED + "You need to be holding a card.");
				return true;
			} else if(args[0].equalsIgnoreCase("getcardfromcode")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage("Only players can perform this command.");
					return true;
				}
				Player player = (Player) sender;
				if(args.length < 2) {
					player.sendMessage("Please specify a string.");
					return true;
				}
				
				String code = args[1];
				ItemStack item = TradingCardManager.getInstance().getCardItemFromCode(code);
				if(item == null) {
					sender.sendMessage("Error: There's something wrong with the code you gave. Format 0:0:0:0 as in ID:CONDITION:SHINY:SIGNED");
					return true;
				}
				player.getInventory().addItem(item);
				player.sendMessage("There you go!");
				return true;
			} else {
				sender.sendMessage("Unknown argument!");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get a card from the given strings. Will ignore unused arguments. Will return null if the card doesn't exist.
	 * @param args
	 * @return
	 */
	private ItemStack getCardFromStrings(String[] args) {
		int id = 0;
		boolean shiny = false;
		TradingCardCondition cond = TradingCardCondition.UNKNOWN;
		boolean signed = false;
		
		for(String s: args) {
			if(s.startsWith("id:")) {
				String parseString = s.substring(3);
				try {
					id = Integer.parseInt(parseString);
				} catch (NumberFormatException ex) {
					//ignore
				}
			} else if(s.startsWith("condition:")) {
				String parseString = s.substring(10);
				cond = TradingCardCondition.valueOf(parseString.toUpperCase());
				if(cond == null) {
					//NPE fix
					cond = TradingCardCondition.UNKNOWN;
				}
			} else if(s.toLowerCase().startsWith("shiny:")) {
				shiny = s.toLowerCase().endsWith("true");
			} else if(s.startsWith("signed:")) {
				signed = s.toLowerCase().endsWith("true");
			}
		}
		
		TradingCard card = TradingCardManager.getInstance().getCardByID(id);
		if(card == null) {
			return null;
		}
		ItemStack cardItem = card.buildCardItem(cond, signed, shiny);
		return cardItem;
	}
	

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> levelonearguments = ImmutableList.of("binder","top","forceload","getpack","getcardfromcode","givecardfromcode","givepack","getcard","givecard","cardinfo");
		List<String> arguments = ImmutableList.of("condition:","shiny:","id:","signed:");
		if(args.length == 1) {
			return StringUtil.copyPartialMatches(args[args.length -1], levelonearguments, new ArrayList<String>(levelonearguments.size()));
		}
		else if(args.length > 1 && args[0].equalsIgnoreCase("getCard")) {
			return StringUtil.copyPartialMatches(args[args.length -1], arguments, new ArrayList<String>(arguments.size()));
		}else if(args.length > 2 && args[0].equalsIgnoreCase("giveCard")) {
			return StringUtil.copyPartialMatches(args[args.length -1], arguments, new ArrayList<String>(arguments.size()));
		}
		return null;
	}

}
