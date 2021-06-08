package tv.tirco.parkmanager.storage.database.SQLite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.util.MessageHandler;


public class SQLite extends SQLiteDatabase{
    String dbname;
    public SQLite(ParkManager instance){
        super(instance);
        dbname = "userFiles/" + Config.getInstance().getSQLiteFilename(); // Set the table name here e.g player_kills
    }

	public String SQLiteCreateCardsTable = "CREATE TABLE IF NOT EXISTS cards (" + // make sure to put your table name in here too.
            "`uuid` varchar(32) NOT NULL," + 
            "`player` varchar(32) NOT NULL," + 
            "`score` int(11) NOT NULL," +
            "`cards` varchar(30000) NOT NULL," +
            "PRIMARY KEY (`uuid`)" +  
            ");";


    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateCardsTable);
            s.close();
            MessageHandler.getInstance().log("SQLite Loaded with prepared statement.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}
