package tv.tirco.parkmanager.commands;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;

public class ForceHatCommand implements CommandExecutor{

    List<String> sitMessages = ImmutableList.of(
    		"&eYou plonked those ol' butcheeks down.",
    		"&eYou sit down.",
    		"&eTime to stop moving around!",
    		"&eOof, those legs were getting tired.",
    		"&eTime to sit down and enjoy the view!",
    		"&eNever underestimate the power of sitting quietly!",
    		"&eOne sits more comfortably on a colour that one likes.",
    		"&eSitting time!",
    		"&eWoohoo! Resting time! You better not be spamming the sit command just to see what type of messages you can get!",
    		"&eI fits, I sits! &a-Any cat ever&e.",
    		"&eWho needs a bench?! This spot right here is perfect!",
    		"&eWalking around the park can be tiring! Time for some rest!",
    		"&eYou are no longer standing. Nice!");
	
	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.hasPermission(command.getPermission())) {
				player.sendMessage(command.getPermissionMessage());
			}
			if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR) ) {
				player.sendMessage("You must have an item in hand to use this command.");
				return true;
			}
			ItemStack hand = player.getInventory().getItemInMainHand().clone();
			
			//Helmet logic
			if(player.getInventory().getHelmet() != null && !player.getInventory().getHelmet().getType().equals(Material.AIR)){
				ItemStack helmet = player.getInventory().getItem(EquipmentSlot.HEAD).clone();
				player.getInventory().setItem(EquipmentSlot.HEAD, hand);
				player.getInventory().setItemInMainHand(helmet);
			} else {
				player.getInventory().setItem(EquipmentSlot.HEAD, hand);
				player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			}
			
			player.sendMessage("You're looking fancy!");
			return true;

		} else {
			sender.sendMessage("This command can only be used by players.");
		}
		return false;
	}

}
