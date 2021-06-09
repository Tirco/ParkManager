package tv.tirco.parkmanager.storage.database.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.tr7zw.changeme.nbtapi.NBTItem;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.storage.database.DatabaseManager;
import tv.tirco.parkmanager.storage.playerdata.PlayerProfile;
import tv.tirco.parkmanager.util.MessageHandler;


public abstract class SQLiteDatabase implements DatabaseManager{
	ParkManager plugin;
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "cards";
    public int tokens = 0;
    public SQLiteDatabase(ParkManager instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE uuid = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    // Exact same method here, Except as mentioned above i am looking for total!
    public Integer getTotal(String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '"+string+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("player").equalsIgnoreCase(string.toLowerCase())){
                    return rs.getInt("total");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
    private void tryClose(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (Exception e) {
                // Ignore
            }
        }
    }
    

	@Override
	public void purgeOldUsers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeUser(UUID uuid) {
        boolean success = false;
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getSQLConnection();
            statement = connection.prepareStatement("DELETE FROM cards " +
                    "WHERE u.user = ?");

            statement.setString(1, uuid.toString());

            success = statement.executeUpdate() != 0;
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            tryClose(statement);
            tryClose(connection);
        }
//
//        if (success) {
//            if(uuid != null)
//                cleanupUser(uuid);
//
//            Misc.profileCleanup(playerName);
//        }

        return success;
	}

	@Override
	public boolean saveUser(PlayerProfile profile) {
		MessageHandler.getInstance().debug("SQLite save request started.");
        Connection conn = null;
        PreparedStatement ps = null;
        String uuid = profile.getUuid().toString();
        
        String cards = "";
        for(ItemStack item : profile.getStoredCards().values()) {
        	NBTItem nbti = new NBTItem(item);
        	if(nbti.hasNBTData()) {
        		if(nbti.hasKey("TradingCardStorageID")) {
        			cards += nbti.getString("TradingCardStorageID") + "=";
        		}
        	}
        }

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + table + " (uuid,player,score,cards) VALUES(?,?,?,?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.setString(1, uuid);                                             // YOU MUST put these into this line!! And depending on how many
                                                                                                         // colums you put (say you made 5) All 5 need to be in the brackets
                                                                                                         // Seperated with comma's (,) AND there needs to be the same amount of
                                                                                                         // question marks in the VALUES brackets. Right now i only have 3 colums
                                                                                                         // So VALUES (?,?,?) If you had 5 colums VALUES(?,?,?,?,?)
            ps.setString(2, profile.getPlayerName());
            ps.setInt(3, profile.getCardScore()); // This sets the value in the database. The colums go in order. Player is ID 1, kills is ID 2, Total would be 3 and so on. you can use
                                  // setInt, setString and so on. tokens and total are just variables sent in, You can manually send values in as well. p.setInt(2, 10) <-
                                  // This would set the players kills instantly to 10. Sorry about the variable names, It sets their kills to 10 i just have the variable called
                                  // Tokens from another plugin :/
            ps.setString(4, cards);
            ps.executeUpdate();
            MessageHandler.getInstance().debug("Prepared statement executed.");
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return true;      
	}

	@Override
	public void newUser(String playerName, UUID uuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int purgePowerlessUsers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getStoredUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create, boolean retry) {
	    // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
	    // This returns the number of people the player killed.
	        Connection conn = null;
	        PreparedStatement ps = null;
	        ResultSet rs = null;
	        try {
	            conn = getSQLConnection();
	            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = ?");
	            ps.setString(1, uuid.toString());
	   
	            rs = ps.executeQuery();
	            if(rs.next()) {
	            	MessageHandler.getInstance().debug("Got result - sending profile.");
	            	PlayerProfile profile = loadFromResult(uuid, rs);
	            	return profile;
	            	}
	        } catch (SQLException ex) {
	            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
	        } finally {
	            try {
	                if (ps != null)
	                    ps.close();
	                if (conn != null)
	                    conn.close();
	            } catch (SQLException ex) {
	                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
	            }
	        }
	        if(create) {
	        	MessageHandler.getInstance().debug("Creating new player profile.");
	        	HashBiMap<Integer,ItemStack> map = HashBiMap.create();
	        	return new PlayerProfile(playerName, uuid, map, 0);
	        }
	        return null;
	}
	
    private PlayerProfile loadFromResult(UUID uuid, ResultSet rs) throws SQLException {
    	MessageHandler.getInstance().debug("Loading profile from result.");
		BiMap<Integer,ItemStack> cards = HashBiMap.create();
		//1 = UUID
		
		//2 = NAME
		String name = rs.getString(2);
		//3 = Score
		int score = rs.getInt(3);
		//4 = Cards
		String[] cardString = rs.getString(4).split("=");
		for(String s : cardString) {
			int id;
			try {
				String[] itemString = s.split(":");
				id = Integer.parseInt(itemString[0]);
			} catch (NumberFormatException ex) {
				continue;
			}
			ItemStack item = TradingCardManager.getInstance().getCardItemFromCode(s);
			cards.put(id, item);
		}
		
		return new PlayerProfile(name, uuid, cards, score);
	}

	private void printErrors(SQLException ex) {
        if (Config.getInstance().getDebug()) {
            ex.printStackTrace();
        }

        StackTraceElement element = ex.getStackTrace()[0];
        MessageHandler.getInstance().log(Level.SEVERE,"Location: " + element.getClassName() + " " + element.getMethodName() + " " + element.getLineNumber());
        MessageHandler.getInstance().log(Level.SEVERE,"SQLException: " + ex.getMessage());
        MessageHandler.getInstance().log(Level.SEVERE,"SQLState: " + ex.getSQLState());
        MessageHandler.getInstance().log(Level.SEVERE,"VendorError: " + ex.getErrorCode());
    }
	
	public LinkedHashMap<String, Integer> getTop10(){
		MessageHandler.getInstance().log("Fetching top 10 from SQL");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player, score FROM " + table + " ORDER BY score DESC LIMIT 10");
            //ps.setString(1, uuid.toString());
   
            rs = ps.executeQuery();
        	return getTop10FromResult(rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
	}

	private LinkedHashMap<String, Integer> getTop10FromResult(ResultSet rs) {
		LinkedHashMap<String, Integer> map = new LinkedHashMap<String,Integer>();
		try {
			while(rs.next()) {
				String name = rs.getString("player");
				int score = rs.getInt("score");
				map.put(name, score);
			}
		} catch (SQLException ex){
			MessageHandler.getInstance().log(Level.SEVERE, "Error when fetching top 10" );
			ex.printStackTrace();
		}
		
		if(map.isEmpty()) {
			MessageHandler.getInstance().log("Top 10 is empty...");
		}
		
		return map;
	}
}
