package tv.tirco.parkmanager.util;

import java.util.LinkedHashMap;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

/**
 * This class will be registered through the register-method in the 
 * plugins onEnable-method.
 */
public class PapiExpansion extends PlaceholderExpansion {

    private ParkManager plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public PapiExpansion(ParkManager plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "parkmanager";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        //if(player == null){
        //    return "";
        //}

        // %parkmanager_player_inride%
        if(identifier.equals("player_inride")){
        	if(player == null) {
        		return "Loading...";
        	}
        	if(!UserManager.hasPlayerDataKey(player)) {
        		return "Loading...";
        	}
        	PlayerData pData = UserManager.getPlayer(player);
            return "" + pData.isInRide();
        }
        
        if(identifier.equals("player_ride_identifier")){
        	if(player == null) {
        		return "Loading...";
        	}
        	if(!UserManager.hasPlayerDataKey(player)) {
        		return "Loading...";
        	}
        	PlayerData pData = UserManager.getPlayer(player);
            return pData.getRideIdentifier();
        }
        
        if(identifier.equals("player_ride_name")){
        	if(player == null) {
        		return "Loading...";
        	}
        	if(!UserManager.hasPlayerDataKey(player)) {
        		return "Loading...";
        	}
        	PlayerData pData = UserManager.getPlayer(player);
        	
        	Ride ride = DataStorage.getInstance().getRide(pData.getRideIdentifier());
        	if(ride == null) {
        		return ChatColor.GRAY + " Not on a ride.";
        	}
            return ChatColor.translateAlternateColorCodes('&', "Riding: " + ride.getName());
        }
        
        if(identifier.equals("player_card_score")) {
        	if(player == null) {
        		return "Loading...";
        	}
        	if(!UserManager.hasPlayerDataKey(player)) {
        		return "Loading...";
        	}
        	PlayerData pData = UserManager.getPlayer(player);
        	
        	int score = pData.getCardScore();

            return "" + score;
        }
        if(identifier.equals("player_card_stored")) {
        	if(player == null) {
        		return "Loading...";
        	}
        	if(!UserManager.hasPlayerDataKey(player)) {
        		return "Loading...";
        	}
        	PlayerData pData = UserManager.getPlayer(player);
        	
        	int amount = pData.getStoredCardAmount();

            return "" + amount;
        }
        if(identifier.matches("cards_top_name_[0-9][0-9]*")) {
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Sending PlaceHolder Data");
        	int number = Integer.valueOf(identifier.split("_")[3]) - 1;
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Number = " + number);
        	String name = getByIndex(TradingCardManager.getInstance().getTopTen(), number);
        	if(name == null) {
        		name = "Unknown";
        	}

        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- UUID was not null. " + uuid.toString());
        	return name;
        }
        if(identifier.matches("cards_top_score_[0-9][0-9]*")) {
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Sending PlaceHolder Data");
        	int number = Integer.valueOf(identifier.split("_")[3]) - 1;
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Number = " + number);
        	String name = "" + getScoreByIndex(TradingCardManager.getInstance().getTopTen(), number);

        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- UUID was not null. " + uuid.toString());
        	return name;
        }
 
        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) 
        // was provided
        return null;
    }
        
        private String getByIndex(LinkedHashMap<String, Integer> hMap, int index){
        	if(hMap == null) {
        		return "";
        	}
        	if(index > hMap.size()-1) {
        		return "";
        	}
     	   return (String) hMap.keySet().toArray()[index];
     	}
        private int getScoreByIndex(LinkedHashMap<String, Integer> hMap, int index){
        	if(hMap == null) {
        		return 0;
        	}
        	if(index > hMap.size()-1) {
        		return 0;
        	}
      	   return (int) hMap.values().toArray()[index];
      	}

}
