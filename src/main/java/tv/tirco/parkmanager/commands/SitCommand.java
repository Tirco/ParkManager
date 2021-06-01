package tv.tirco.parkmanager.commands;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EquipmentSlot;

import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.util.Util;

public class SitCommand implements CommandExecutor{

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
			if(player.isInsideVehicle() || player.getVehicle() != null) {
				//TODO check if player is RideActive
				player.sendMessage("You can not use this command while in a ride or if you are already seated!");
				return true;
			}
			
			//Not flying / Not in air.
			if (!(!player.isFlying() && player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid())) {
			    // on ground
				player.sendMessage("You need to be standing on the ground when using this command.");
				return true;
			}
			
//			//Not moving
//			Vector NULL_VECTOR = new Vector(0, 0, 0);
//			if(!player.getVelocity().equals(NULL_VECTOR)) {
//				player.sendMessage("You need to be standing still when using this command.");
//				return true;
//			}
			
			//Summon armorstand
			//Add Armorstand to map of stands that should be deleted upon server reboot.
	        Location ploc = player.getLocation().add(0, -1.7, 0);
	        
	        ArmorStand as = (ArmorStand) ploc.getWorld().spawn(ploc, ArmorStand.class);

	        as.setGravity(false);
	        as.setCanPickupItems(false);
	        as.setCustomName("sitting");
	        as.setCustomNameVisible(false);
	        as.setVisible(false);
	        as.addScoreboardTag("delete-on-dismount");
	        as.addPassenger(player);
	        as.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
	        as.addEquipmentLock(EquipmentSlot.CHEST, LockType.REMOVING_OR_CHANGING);
	        as.addEquipmentLock(EquipmentSlot.FEET, LockType.REMOVING_OR_CHANGING);
	        as.addEquipmentLock(EquipmentSlot.LEGS, LockType.REMOVING_OR_CHANGING);
	        
	        Random rand = Util.getRandom();

	        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
	        		sitMessages.get(rand.nextInt(sitMessages.size()))));
	        return true;

		} else {
			sender.sendMessage("This command can only be used by players.");
		}
		return false;
	}

}
