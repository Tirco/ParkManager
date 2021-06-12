package tv.tirco.parkmanager.TradingCards.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.TradingCards.TradingCard;
import tv.tirco.parkmanager.TradingCards.TradingCardCondition;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;

public class TradingCardSignCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by players.");
			return true;
		}
		
		Player player = (Player) sender;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item == null || !item.getType().equals(Material.BLAZE_POWDER)) {
			player.sendMessage(ChatColor.YELLOW + "You need to hold a Trading Card in your hand to use this command.");
			return true;
		}
		
		NBTItem nbti = new NBTItem(item);
		if(!nbti.hasNBTData() || !nbti.hasKey("TradingCardID")) {
			player.sendMessage(ChatColor.YELLOW + "You need to hold a Trading Card in your hand to use this command.");
			return true;
		}
		boolean signed = false;
		if(nbti.hasKey("TradingCardSigned")) {
			signed = nbti.getBoolean("TradingCardSigned");
		}
		if(signed) {
			player.sendMessage(ChatColor.RED + "This card is already signed!");
			return true;
		}
		int id = nbti.getInteger("TradingCardID");
		TradingCard card = TradingCardManager.getInstance().getCardByID(id);
		if(!card.getSignerUUID().equalsIgnoreCase(player.getUniqueId().toString())) {
			player.sendMessage(ChatColor.RED + "You can't sign this card.");
			return true;
		} else {
			signed = true;
		}
		
		Boolean shiny = nbti.getBoolean("TradingCardShiny");
		TradingCardCondition condition = TradingCardCondition.valueOf(nbti.getString("TradingCardCondition"));
		
		ItemStack newCard = card.buildCardItem(condition, signed, shiny);
		player.getInventory().remove(item);
		player.getInventory().addItem(newCard);
		
		player.sendMessage("You have signed the card.");
		
//		nbti.setInteger("TradingCardID", id);
//		nbti.setString("TradingCardRarity", rarity.toString());
//		nbti.setString("TradingCardCondition", condition.toString());
//		nbti.setBoolean("TradingCardShiny", shiny);
//		nbti.setInteger("TradingCardDefaultLoreSize", itemLore.size());
//		nbti.setBoolean("TradingCardSigned", false);
//		nbti.setDouble("TradingCardScore", TradingCardManager.getInstance().getCardValue(rarity,condition,signed,shiny,available));
//		nbti.setString("TradingCardStorageID", TradingCardManager.getInstance().getCardStorageID(id, shiny, signed, condition, rarity)); //TODO
//		nbti.setLong("TradinCardNoStack", Util.getRandom().nextLong());
		return true;
	}
}
