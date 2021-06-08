package tv.tirco.parkmanager.TradingCards.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;
import tv.tirco.parkmanager.TradingCards.TradingCardPackTask;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class TradingCardAdminCommand implements CommandExecutor{

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
				}
			} else {
				
				ItemStack newCard = TradingCardManager.getInstance().getRandomTradingCardItem();
				player.getInventory().addItem(newCard);
				player.sendMessage("Random card!");	
			}
		}
		return false;
	}

}
