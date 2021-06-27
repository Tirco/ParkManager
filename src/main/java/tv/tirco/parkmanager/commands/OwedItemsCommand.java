package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.DataStorage;

public class OwedItemsCommand implements CommandExecutor{

	//Used to claim owed items;
	
	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();
			if(!DataStorage.getInstance().isOwedItems(uuid)) {
				player.sendMessage(ChatColor.RED + "There were no items waiting for you.");
				return true;
			}
			
			List<ItemStack> owedItems = DataStorage.getInstance().owedItems(uuid);
			List<ItemStack> givenItems = new ArrayList<ItemStack>();
			for(ItemStack i : owedItems) {
				if(player.getInventory().firstEmpty() != -1) {
					player.getInventory().addItem(i);
					givenItems.add(i);
				} else {
					break;
				}
			}
			owedItems.removeAll(givenItems);
			player.sendMessage(ChatColor.GOLD + "You were given " + givenItems.size() + " item(s).");
			if(!owedItems.isEmpty()) {
				player.sendMessage("There are still " + owedItems.size() + " items waiting for you.");
				DataStorage.getInstance().setPlayerOwedItems(uuid, owedItems);
			} else {
				DataStorage.getInstance().clearPlayerOwedItems(uuid);
			}
		} else {
			sender.sendMessage("This command can only be run by players");
			return true;
		}
		return false;
	}
	
    

}
