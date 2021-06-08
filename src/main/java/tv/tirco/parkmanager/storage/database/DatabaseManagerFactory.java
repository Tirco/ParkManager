package tv.tirco.parkmanager.storage.database;

import tv.tirco.parkmanager.config.Config;

public class DatabaseManagerFactory {

	public static DatabaseManager getDatabaseManager() {
		return Config.getInstance().getDatabaseType();
	}

	public static DatabaseManager createDatabaseManager(DatabaseType type) {
		switch (type) {
		case FLATFILE:
			return new PlayerFileManager();

		/*case SQL:
			return new SQLDatabaseManager(); */

		default:
			return null;
		}
	}
}
