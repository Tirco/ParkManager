package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.tr7zw.changeme.nbtapi.NBTItem;

public class VoucherCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack item = player.getInventory().getItemInMainHand();
			if(item == null || item.getType().equals(Material.AIR)) {
				player.sendMessage("You need to hold an item in your main hand to use this command.");
				return true;
			}
			NBTItem nbti = new NBTItem(item);
			
			if(args.length < 1 || args[0].equalsIgnoreCase("info")) {
				if(nbti.hasNBTData()) {
					for(String s : nbti.getKeys()) {
						player.sendMessage(s);
					}
					player.sendMessage("That's all!");
					return true;
				} else {
					player.sendMessage("No data found on this item.");
					return true;
				}
			} else {
				if(args[0].equalsIgnoreCase("set")) {
					if(args.length < 3) {
						player.sendMessage("usage: /voucher set <key> <value>");
						return true;
					}
					String key = args[1];
					List<String> list = new ArrayList<String>(Arrays.asList(args));
					list.remove(1);
					list.remove(0);
					
					String value = "";
					for(String s : list) {
						value += s + " ";
					}
					nbti.setString(key, value);
					player.getInventory().setItemInMainHand(nbti.getItem());
					player.sendMessage("Done!");
					return true;
				}
			}
			
			return false;
		} else {
			sender.sendMessage("This command can only be used by players.");
		}
		return false;
	}

}
