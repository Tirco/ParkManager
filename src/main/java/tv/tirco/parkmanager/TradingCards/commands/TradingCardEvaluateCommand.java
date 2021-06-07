package tv.tirco.parkmanager.TradingCards.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.TradingCards.TradingCardCondition;
import tv.tirco.parkmanager.TradingCards.TradingCardConfig;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;

public class TradingCardEvaluateCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack card = player.getInventory().getItemInMainHand();
			if(!card.getType().equals(Material.BLAZE_POWDER)) {
				player.sendMessage("This is not a card!");
				return false;
			}
			
			NBTItem nbti = new NBTItem(card);
			if(!nbti.hasNBTData()) {
				player.sendMessage("This is not a card!");
				return false;
			}
			
			if(!nbti.getString("TradingCardCondition").equalsIgnoreCase(TradingCardCondition.UNKNOWN.toString())) {
				player.sendMessage("This card is already evaluated!");
			} else {
				TradingCardCondition newCondition = TradingCardConfig.getInstance().getRandomCondition();
				ItemStack updatedCard = TradingCardManager.getInstance().updateCondition(newCondition, card);
				player.getInventory().setItemInMainHand(updatedCard);
				
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
						"&eYour card has been evaluated to be in a &d" + newCondition.getAsString() + "&e condition."));
			}
			return true;

		}
		return true;
	}

}
