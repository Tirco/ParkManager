package tv.tirco.parkmanager.traincarts;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;

import tv.tirco.parkmanager.alias.Alias;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.util.MessageHandler;

public class CmdTrainListener extends SignAction{

	
	@Override
    public boolean match(SignActionEvent info) {
        return info.isType("alias");
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
    
    //Line 2 - alias
    //Line 3 - aliasidentifier
    //Line 4 - aliasidentifier
    

    public void sendGreetingForCart(SignActionEvent info, MinecartMember<?> member) {
    	if(member.getEntity().getPlayerPassengers().isEmpty()){
    		return;
    	}
        String alias = info.getLine(2)+info.getLine(3);
        Alias a = DataStorage.getInstance().getAlias(alias);
        if(a == null) {
        	MessageHandler.getInstance().debug("Train carts attempted to run the Alias " + alias + " but returned null");
        } else {
            for (Player passenger : member.getEntity().getPlayerPassengers()) {
                a.execute(passenger, null);
            }
        }

    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create()
                .setName(event.isCartSign() ? "cart alias" : "train alias")
                .setDescription("Executes a command/permission alias for the cart/train. ")
                .handle(event.getPlayer());
    }
}
