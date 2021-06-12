package tv.tirco.parkmanager.storage.database;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.storage.database.SQLite.SQLite;

public class DatabaseManagerFactory {

	public static DatabaseManager getDatabaseManager() {
		return Config.getInstance().getDatabaseType();
	}

	public static DatabaseManager createDatabaseManager(DatabaseType type) {
		switch (type) {
		case FLATFILE:
			return new PlayerFileManager();

		case SQL:
			return new SQLite(ParkManager.parkManager);

		default:
			return null;
		}
	}
}
