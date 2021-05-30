package tv.tirco.parkmanager.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GadgetsCommand implements CommandExecutor{

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;

			player.performCommand("gadgetsmenu menu main");
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
