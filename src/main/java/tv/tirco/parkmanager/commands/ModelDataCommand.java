package tv.tirco.parkmanager.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class ModelDataCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be used by players.");
			return true;
		}
		Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if(item == null || item.getType().equals(Material.AIR)) {
			player.sendMessage(ChatColor.RED + "You need to hold an item in your main hand to use this command.");
			return true;
		}
		ItemMeta meta = item.getItemMeta();
		if(args.length > 0) {
			int data = 0;
			try {
				data = Integer.parseInt(args[0]);
			} catch(NumberFormatException ex) {
				player.sendMessage("Unable to parse " + args[0] + " to a number.");
				return true;
			}
			if(data == 0) {
				meta.setCustomModelData(null);
			} else {
				meta.setCustomModelData(data);
			}

			item.setItemMeta(meta);
			player.getInventory().setItemInMainHand(item);
			player.sendMessage(ChatColor.GREEN + "The CustomModelData of the item in your hand has been set to " + data);
			return true;
		} else {
			int data = 0;
			if(meta.hasCustomModelData()) {
				data = meta.getCustomModelData();
				player.sendMessage(ChatColor.GREEN + "The CustomModelData of your item is " + data);
			} else {
				player.sendMessage(ChatColor.RED + "The item in your hand does not have CustomModelData set.");
			}
			player.sendMessage(ChatColor.GOLD + "To set the data, use the command /modeldata <int>");
			return true;

		}
	}

}
