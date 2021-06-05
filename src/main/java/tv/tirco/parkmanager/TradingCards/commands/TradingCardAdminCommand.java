package tv.tirco.parkmanager.TradingCards.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tv.tirco.parkmanager.TradingCards.TradingCardManager;

public class TradingCardAdminCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack newCard = TradingCardManager.getInstance().getRandomTradingCardItem();
			player.getInventory().addItem(newCard);
			player.sendMessage("Done!");
		}
		return true;
	}

}
