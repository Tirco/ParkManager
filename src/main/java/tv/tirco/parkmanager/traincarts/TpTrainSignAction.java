package tv.tirco.parkmanager.traincarts;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.MaterialTypeProperty;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.BlockTimeoutMap;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import tv.tirco.parkmanager.util.MessageHandler;

public class TpTrainSignAction extends SignAction {
    private BlockTimeoutMap teleportTimes = new BlockTimeoutMap();

    @Override
    public boolean canSupportRC() {
        return true;
    }

    @Override
    public boolean match(SignActionEvent info) {
    	return info.isType("tp");    }

    @Override
    public void execute(SignActionEvent info) {
        if (!info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) || !info.isPowered()) {
            return;
        }

        MinecartGroup group = null;
        if (!info.isRCSign()) {
            if (!info.hasGroup()) {
                return;
            }
            group = info.getGroup();
        } else {
            Collection<MinecartGroup> groups = info.getRCTrainGroups();
            if (groups.isEmpty()) {
                return;
            }
            group = groups.iterator().next();
        }

        String destName = info.getLine(1) +" " + info.getLine(2);
        String direction = info.getLine(3);
        //x:0y:0z:0
        String[] destArgs = destName.split(" ");
        double x = 0.1337;
        double y = 0.1337;
        double z = 0.1337;
        
        for(String s : destArgs) {
        	double parsed = 0;
        	try {
            	parsed = Double.parseDouble(s.substring(2));
        	} catch (NumberFormatException | NullPointerException ex) {
        		MessageHandler.getInstance().debug("failed to parse substring " + s);
            	
        		continue;
        	}

        	MessageHandler.getInstance().debug("TP Sign parsed double to " + parsed);
        	
        	if(s.startsWith("x:")) {
        		x = parsed;
        	} else if (s.startsWith("y:")) {
        		y = parsed;
        	} else if (s.startsWith("z:")) {
        		z = parsed;
        	}
        }
        
        if(x == 0.1337 || y == 0.1337 || z == 0.1337) {
        	MessageHandler.getInstance().log("Teleport Sign at " + info.getLocation() + " couldn't not parse X Y Z");
        	return;
        }
        
        Location destination = new Location(info.getWorld(), x, y, z);
        

        Block rBlock = getRailsBlock(destination.getBlock());
        if (destination != null && rBlock != null) {

            // This prevents instant teleporting back to the other end
            if (info.hasRails() && this.teleportTimes.isMarked(info.getRails(), 2000)) {
                return;
            } else {
                this.teleportTimes.mark(rBlock);
            }

            BlockFace spawnDirection = BlockFace.valueOf(direction);

            // Teleporting to another world doesn't work on the tick we are updating on (1.14 and later)
            // We must do this a tick delayed to prevent issues
            if (destination.getWorld() == group.getWorld()) { //Should never fail?
                group.teleportAndGo(getRailsBlock(destination.getBlock()), spawnDirection);
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create()
                .setPermission(Permission.BUILD_TELEPORTER)
                .setName("tp")
                .setDescription("teleport trains large distances to another teleporter sign")
                .handle(event.getPlayer());
    }
    
    public final MaterialTypeProperty ISVERTRAIL = new MaterialTypeProperty(Material.LADDER);
    public final MaterialTypeProperty ISTCRAIL = new MaterialTypeProperty(ISVERTRAIL, MaterialUtil.ISRAILS, MaterialUtil.ISPRESSUREPLATE);
    
    public Block getRailsBlock(Block from) {
        if (ISTCRAIL.get(from)) {
            return from;
        } else {
            from = from.getRelative(BlockFace.DOWN);
            return ISTCRAIL.get(from) ? from : null;
        }
    }
}
