package tv.tirco.parkmanager.storage.database.SQLite;

import java.util.logging.Level;

import tv.tirco.parkmanager.ParkManager;

public class Error {
    public static void execute(ParkManager plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(ParkManager plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}