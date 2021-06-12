package tv.tirco.parkmanager.traincarts;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;

import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;

public class RideTrainListener extends SignAction{

	
	@Override
    public boolean match(SignActionEvent info) {
        return info.isType("ridetracker");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isTrainSign()
                && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)
                && info.isPowered() && info.hasGroup()
        ) {
            for (MinecartMember<?> member : info.getGroup()) {
            	startRideForCart(info, member);
            }
            return;
        }
        if (info.isCartSign()
                && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)
                && info.isPowered() && info.hasMember()
        ) {
            startRideForCart(info, info.getMember());
            return;
        }
    }
    
    //Line 0 - [train]
    //Line 1 - [ridetracker]
    //Line 2 - [Start / Stop]
    //Line 3 - [identifier]
    

    //IncorrectNaming...
    public void startRideForCart(SignActionEvent info, MinecartMember<?> member) {
    	if(member.getEntity().getPlayerPassengers().isEmpty()){
    		return;
    	}
    	String type = info.getLine(2); //start or stop
        String identifier = info.getLine(3);
        
        Ride ride = DataStorage.getInstance().getRide(identifier);
        if(ride == null) {
        	Bukkit.getConsoleSender().sendMessage("Error: Attempted to start ride " + identifier + " - No such identifier exists.");
        	return;
        }
        if(type.equalsIgnoreCase("start")) {
            for (Player passenger : member.getEntity().getPlayerPassengers()) {
               ride.start(passenger);
            }
        } else if(type.equalsIgnoreCase("stop")) {
            for (Player passenger : member.getEntity().getPlayerPassengers()) {
                ride.stop(passenger);
             }
        }

    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create()
                .setName(event.isCartSign() ? "cart ridetracker" : "train ridetracker")
                .setDescription("Starts or stops the tracking of a player in a ride. ")
                .handle(event.getPlayer());
    }
}
