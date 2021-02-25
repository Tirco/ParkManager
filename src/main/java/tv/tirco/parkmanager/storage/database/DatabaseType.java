package tv.tirco.parkmanager.storage.database;

import tv.tirco.parkmanager.util.MessageHandler;

public enum DatabaseType {
	FLATFILE, SQL;

	public static DatabaseType getDatabaseType(String typeName) {
		for (DatabaseType type : values()) {
			if (type.name().equalsIgnoreCase(typeName)) {
				return type;
			}
		}

		if (typeName.equalsIgnoreCase("file")) {
			return FLATFILE;
		} else if (typeName.equalsIgnoreCase("mysql")) {
			return SQL;
		}

		// Default to Flatfile
		MessageHandler.getInstance().log("Unable to identify storage type \" " + typeName + "\" - Defaulting to flatfile.");
		return FLATFILE;
	}
}