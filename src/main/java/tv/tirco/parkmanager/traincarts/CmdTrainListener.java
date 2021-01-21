package tv.tirco.parkmanager.traincarts;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;

public class CmdTrainListener extends SignAction{

	
	@Override
    public boolean match(SignActionEvent info) {
        return info.isType("cmd");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isTrainSign()
                && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)
                && info.isPowered() && info.hasGroup()
        ) {
            for (MinecartMember<?> member : info.getGroup()) {
                sendGreetingForCart(info, member);
            }
            return;
        }
        if (info.isCartSign()
                && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)
                && info.isPowered() && info.hasMember()
        ) {
            sendGreetingForCart(info, info.getMember());
            return;
        }
    }
    
    //Line 2 - cmd
    //Line 3 - asPlayer asConsoleOnce asConsoleForAll
    //Line 4 - alias
    

    public void sendGreetingForCart(SignActionEvent info, MinecartMember<?> member) {
    	if(member.getEntity().getPlayerPassengers().isEmpty()){
    		return;
    	}
    	String type = info.getLine(2);
        String alias = info.getLine(3);
        if(type.equalsIgnoreCase("asPlayerPerm")) {
        	
        } else if(type.equalsIgnoreCase("asPlayerAllPerm")) {
        	
        } else if(type.equalsIgnoreCase("asConOnce")) {
        
        } else if(type.equalsIgnoreCase("asConPerPlayer")) {
        	
        }
        for (Player passenger : member.getEntity().getPlayerPassengers()) {
            passenger.sendMessage(alias);
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create()
                .setName(event.isCartSign() ? "cart command" : "train command")
                .setDescription("Sends a command alias for the cart/train. ")
                .handle(event.getPlayer());
    }
}
