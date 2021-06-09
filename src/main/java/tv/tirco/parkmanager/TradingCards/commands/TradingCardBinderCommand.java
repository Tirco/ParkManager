package tv.tirco.parkmanager.TradingCards.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class TradingCardBinderCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		
		if(!UserManager.hasPlayerDataKey(player)) {
			player.sendMessage(ChatColor.RED + "Please wait, your binder is not loaded yet.");
			return true;
		}		
		
		PlayerData pData = UserManager.getPlayer(player);

		if(!pData.isLoaded()) {
			player.sendMessage(ChatColor.RED + "Please wait, your binder is not loaded yet.");
			return true;
		}
		
		if(pData.spamCooldown())  {
			player.sendMessage(ChatColor.RED + "Slow down!");
			return true;
		} else {
			pData.updateSpamCooldown();
		}
		
		Inventory invToOpen = pData.getBinderPages().get(0);
		TradingCardManager.getInstance().updateScoreItem(pData, invToOpen);
		player.openInventory(invToOpen);
		return true;
	}
}
