package tv.tirco.parkmanager.TradingCards.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class TradingCardAdminCommand implements CommandExecutor, TabCompleter{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("binder")) {
					if(!UserManager.hasPlayerDataKey(player)) {
						player.sendMessage("Not loaded");
						return true;
					}
					
					PlayerData pData = UserManager.getPlayer(player);
					if(!pData.isLoaded()) {
						player.sendMessage("Not loaded");
						return true;
						
					}
					Inventory invToOpen = pData.getBinderPages().get(0);
					TradingCardManager.getInstance().updateScoreItem(pData, invToOpen);
					player.openInventory(invToOpen);
					return true;
				} else if(args[0].equalsIgnoreCase("pack")) {
					PlayerData pData = UserManager.getPlayer(player);
					if(!pData.isLoaded()) {
						player.sendMessage("Not loaded");
						return true;
						
					}
					new TradingCardPackTask(ParkManager.parkManager, player, pData);
					return true;
					
				} else if(args[0].equalsIgnoreCase("forceload")) {
					TradingCardManager.getInstance().loadTopTen();
					sender.sendMessage("Done!");
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
					ItemStack item = new ItemStack(Material.PAPER, 1);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Trading Card Pack");
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GREEN + "Card Pack");
					lore.add("");
					lore.add(ChatColor.YELLOW + "Open this pack to");
					lore.add(ChatColor.YELLOW + "receive 3 random");
					lore.add(ChatColor.YELLOW + "Trading Cards.");
					lore.add("");
					lore.add(ChatColor.GRAY + "<Right-Click to Open>");
					meta.setLore(lore);
					meta.setCustomModelData(1);
					item.setItemMeta(meta);
					
					NBTItem nbti = new NBTItem(item);
					nbti.setBoolean("isCardPack", true);
					
					item = nbti.getItem();
					
					player.getInventory().addItem(item);
					player.sendMessage("Item received!");
				} else if(args[0].equalsIgnoreCase("getcard")) {
					int id = 1;
					boolean shiny = false;
					TradingCardCondition cond = TradingCardCondition.UNKNOWN;
					boolean signed = false;
					
					for(String s: args) {
						if(s.startsWith("id:")) {
							String parseString = s.substring(3);
							try {
								id = Integer.parseInt(parseString);
							} catch (NumberFormatException ex) {
								sender.sendMessage("Can not parse " + parseString + " to a number");
							}
						} else if(s.startsWith("condition:")) {
							String parseString = s.substring(10);
							cond = TradingCardCondition.valueOf(parseString.toUpperCase());
						} else if(s.startsWith("shiny:")) {
							shiny = s.endsWith("true");
						} else if(s.startsWith("signed:")) {
							signed = s.endsWith("true");
						}
					}
					TradingCard card = TradingCardManager.getInstance().getCardByID(id);
					ItemStack cardItem = card.buildCardItem(cond, signed, shiny);
					
					player.getInventory().addItem(cardItem);
					player.sendMessage("Done!");
					return true;
				} else if(args[0].equalsIgnoreCase("loadcardfromcode")) {
					if(args.length < 2) {
						player.sendMessage("Please specify a string.");
						return true;
					}
					
					String code = args[1];
					ItemStack item = TradingCardManager.getInstance().getCardItemFromCode(code);
					player.getInventory().addItem(item);
					player.sendMessage("Done!");
					return true;
				}
			} else {
				
				ItemStack newCard = TradingCardManager.getInstance().getRandomTradingCardItem();
				player.getInventory().addItem(newCard);
				player.sendMessage("Random card!");	
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> arguments = ImmutableList.of("condition:","shiny:","id:","signed:");
		if(args.length > 1 && args[0].equalsIgnoreCase("getCard")) {
			return StringUtil.copyPartialMatches(args[args.length -1], arguments, new ArrayList<String>(arguments.size()));
		}
		return null;
	}

}
