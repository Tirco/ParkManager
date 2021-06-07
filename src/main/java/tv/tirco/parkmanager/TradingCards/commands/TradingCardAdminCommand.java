package tv.tirco.parkmanager.TradingCards.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tv.tirco.parkmanager.TradingCards.TradingCardManager;
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
					}
					
					PlayerData pData = UserManager.getPlayer(player);
					player.openInventory(pData.getBinderPages().get(0));
					return true;
				}
			} else {
				
				ItemStack newCard = TradingCardManager.getInstance().getRandomTradingCardItem();
				player.getInventory().addItem(newCard);
				player.sendMessage("Random card!");	
			}
		}
		return true;
	}

}
