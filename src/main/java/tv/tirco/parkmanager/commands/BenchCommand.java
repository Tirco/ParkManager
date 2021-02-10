package tv.tirco.parkmanager.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BenchCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;

			
	        Location ploc = player.getLocation().add(0, -1.25, 0);
	        ploc.setX(ploc.getBlockX() + 0.5);
	        ploc.setZ(ploc.getBlockZ() + 0.5);
	        ploc = rpGetPlayerDirection(player, ploc);

	        Location loc2 = getLoc2(ploc.clone(),1.0);
	        //Location loc2 = new Location(ploc.getWorld(), newX, ploc.getY(), newZ, ploc.getYaw(), ploc.getPitch());
	        
	        //AS 1
	        ArmorStand as = (ArmorStand) ploc.getWorld().spawn(ploc, ArmorStand.class);
	        
	        ItemStack helmet = new ItemStack(Material.GOLDEN_SWORD);
	        ItemMeta meta = helmet.getItemMeta();
	        meta.setCustomModelData(4);
	        helmet.setItemMeta(meta);

	        as.setGravity(false);
	        as.setCanPickupItems(false);
	        as.setCustomName("bench1");
	        as.setCustomNameVisible(false);
	        as.getEquipment().setHelmet(helmet);
	        as.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
	        as.addEquipmentLock(EquipmentSlot.CHEST, LockType.REMOVING_OR_CHANGING);
	        as.addEquipmentLock(EquipmentSlot.FEET, LockType.REMOVING_OR_CHANGING);
	        as.addEquipmentLock(EquipmentSlot.LEGS, LockType.REMOVING_OR_CHANGING);
	        as.setVisible(false);
	        as.addScoreboardTag("seat");

	        //AS 2
	        //AS 1
	        ArmorStand as2 = (ArmorStand) loc2.getWorld().spawn(loc2, ArmorStand.class);
	        
	        as2.setGravity(false);
	        as2.setCanPickupItems(false);
	        as2.setCustomName("bench2");
	        as2.setCustomNameVisible(false);
	        as2.setVisible(false);
	        as2.addScoreboardTag("seat");
	        as2.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
	        as2.addEquipmentLock(EquipmentSlot.CHEST, LockType.REMOVING_OR_CHANGING);
	        as2.addEquipmentLock(EquipmentSlot.FEET, LockType.REMOVING_OR_CHANGING);
	        as2.addEquipmentLock(EquipmentSlot.LEGS, LockType.REMOVING_OR_CHANGING);
	        
	        player.sendMessage(
	        		ploc.getX() + " "+
	        		ploc.getY() + " "+
	        		ploc.getZ() + " ");

	        player.sendMessage(
	        		loc2.getX() + " "+
	        		loc2.getY() + " "+
	        		loc2.getZ() + " ");
	        return true;

		} else {
			sender.sendMessage("This command can only be used by players.");
		}
		return false;
	}
	
    //
    // rpGetPlayerDirection - Convert Player's Yaw into a human readable direction out of 16 possible.
    //
    public Location rpGetPlayerDirection(Player playerSelf,Location loc){
         float y = playerSelf.getLocation().getYaw();
         y = (y % 360 + 360) % 360;
         playerSelf.sendMessage("yaw = " + y);
         
         float newY = 0;
         if (y > 135 && y < 225) {
        	 //North
             newY = 180;
         } else if (y > 225) {
        	 //east
        	 newY = 270;
         } else if (y > 45) {
        	 //West
        	 newY = 90;
         } else {
        	 //South
        	 newY = 0;
         }
         loc.setYaw(newY);
         return loc;
    }
    
    public Location getLoc2(Location loc, double offset) {
        float y = loc.getYaw();
        y = (y % 360 + 360) % 360;
        Location newLoc = loc;
        if (y > 135 && y < 225) {
        	//North
        	newLoc.setX(newLoc.getX() - offset);
        } else if (y > 225) {
        	//East
        	newLoc.setZ(newLoc.getZ() - offset);
        } else if (y > 45) {
        	//West
        	newLoc.setZ(newLoc.getZ() + offset);
        } else {
        	//South
        	newLoc.setX(newLoc.getX() + offset);
        }
        return newLoc;
    }

}
