package tv.tirco.parkmanager.config;

import java.util.ArrayList;
import java.util.List;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.storage.database.DatabaseManager;
import tv.tirco.parkmanager.storage.database.PlayerFileManager;
import tv.tirco.parkmanager.storage.database.SQLite.SQLite;
import tv.tirco.parkmanager.util.MessageHandler;

public class Config extends AutoUpdateConfigLoader {
	private static Config instance;

	private Config() {
		super("config.yml");
		validate();
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}

		return instance;
	}

	@Override
	protected void loadKeys() {
		MessageHandler.getInstance().setDebugState(getDebug());
		MessageHandler.getInstance().setDebugToAdminState(getDebugToAdmins());
	}

	@Override
	protected boolean validateKeys() {
		// Validate all the settings!
		List<String> reason = new ArrayList<String>();

		if (getDebug()) {
			MessageHandler.getInstance().setDebugState(true);
			MessageHandler.getInstance().debug("Debugging has been enabled.");
		}

		// If the reason list is empty, keys are valid.
		return noErrorsInConfig(reason);
	}

	@SuppressWarnings("unused")
	private String getStringIncludingInts(String key) {
		String str = config.getString(key);

		if (str == null) {
			str = String.valueOf(config.getInt(key));
		}

		if (str.equals("0")) {
			str = "No value set for '" + key + "'";
		}
		return str;
	}

	// Config Getters
	/* General Settings */
	public String getLocale() {
		return config.getString("General.Locale", "en_us");
	}

	public boolean getDebug() {
		return config.getBoolean("debug", false);
	}
	public boolean getDebugToAdmins() {
		return config.getBoolean("debugToAdmins", false);
	}
	
	public List<String> blockBreakAllowedWorlds(){
		return config.getStringList("worldprotection.blockbreak_allowed_in");
	}
	
	public List<String> blockPlaceAllowedWorlds(){
		return config.getStringList("worldprotection.blockplace_allowed_in");
	}
	
	public List<String> rightClickBlockAllowedWorlds(){
		return config.getStringList("worldprotection.rightclickblock_allowed_in");
	}

	public int getOldUsersCutoff() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public String getSQLiteFilename() {
		return config.getString("SQLite.Filename", "PlayerDatabase");
	}

	public DatabaseManager getDatabaseType() {
		String database = config.getString("DatabaseType","SQLite");
		if(database.equalsIgnoreCase("sqlite")) {
			SQLite sqlite = new SQLite(ParkManager.parkManager);
			sqlite.load();
			return sqlite;
		} else {
			return new PlayerFileManager();
		}
	}

	
	public long getSaveInterval() {
		return config.getInt("saveinterval", 17);
		
	}
	

}
