package tv.tirco.parkmanager.util;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.geysermc.connector.GeyserConnector;
import org.geysermc.connector.network.session.GeyserSession;

import com.viaversion.viaversion.api.Via;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.config.Config;

public class Util {

    static Random rand = new Random();
    
    public static Random getRandom() {
    	return rand;
    }
    
    public static boolean isNPC(Entity entity) {
        return (entity == null || entity.hasMetadata("NPC") || entity instanceof NPC || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
    }
	
 	public static boolean canBeInteracted(Material mat) {
		switch(mat) {
		//Buttons
		case STONE_BUTTON:
		case OAK_BUTTON:
		case SPRUCE_BUTTON:
		case BIRCH_BUTTON:
		case JUNGLE_BUTTON:
		case ACACIA_BUTTON:
		case DARK_OAK_BUTTON:
		case CRIMSON_BUTTON:
		case WARPED_BUTTON:
		case POLISHED_BLACKSTONE_BUTTON:
		//Pressureplates
		case STONE_PRESSURE_PLATE:
		case OAK_PRESSURE_PLATE:
		case SPRUCE_PRESSURE_PLATE:
		case BIRCH_PRESSURE_PLATE:
		case JUNGLE_PRESSURE_PLATE:
		case ACACIA_PRESSURE_PLATE:
		case DARK_OAK_PRESSURE_PLATE:
		case CRIMSON_PRESSURE_PLATE:
		case WARPED_PRESSURE_PLATE:
		case POLISHED_BLACKSTONE_PRESSURE_PLATE:
		case LIGHT_WEIGHTED_PRESSURE_PLATE:
		case HEAVY_WEIGHTED_PRESSURE_PLATE:
		//Containers
		case ENDER_CHEST:
		//Signs
		case OAK_SIGN:
		case ACACIA_SIGN:
		case SPRUCE_SIGN:
		case BIRCH_SIGN:
		case DARK_OAK_SIGN:
		case JUNGLE_SIGN:
		case CRIMSON_SIGN:
		case WARPED_SIGN:
		case OAK_WALL_SIGN:
		case ACACIA_WALL_SIGN:
		case SPRUCE_WALL_SIGN:
		case BIRCH_WALL_SIGN:
		case DARK_OAK_WALL_SIGN:
		case JUNGLE_WALL_SIGN:
		case CRIMSON_WALL_SIGN:
		case WARPED_WALL_SIGN:
			return true;
		default: 
			return false;
		}
	}
		
	public static boolean blockBreakAllowed(String name) {
		return Config.getInstance().blockBreakAllowedWorlds().contains(name);
	}
	
	public static boolean blockPlaceAllowed(String name) {
		return Config.getInstance().blockPlaceAllowedWorlds().contains(name);
	}
	
	public static boolean rightClickBlockAllowed(String name) {
		return Config.getInstance().rightClickBlockAllowedWorlds().contains(name);
	}

	public static boolean isGeyser(Player player) {
		if(ParkManager.geyserEnabled) {
			GeyserConnector connector = GeyserConnector.getInstance();
			if(connector != null) {
				GeyserSession session = connector.getPlayerByUuid(player.getUniqueId());
				if(session == null) {
					return false;//geyserStatus = "Not a geyser player.";
					
				} else {
					return true;
					//geyserStatus = "Is a geyser player.";
				}
			}
		} return false;
	}

	public static int getVersion(Player player) {
		int versionID = 0;
		try {
			versionID = Via.getManager().getPlatform().getApi().getPlayerVersion(player.getUniqueId());
		} catch (Exception ex) {
			MessageHandler.getInstance().log("Exception while fetching API version of " + player.getName());
			ex.printStackTrace();
		}
		return versionID;
	}


}
