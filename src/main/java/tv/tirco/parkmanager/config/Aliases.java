package tv.tirco.parkmanager.config;

import java.util.ArrayList;
import java.util.List;

import tv.tirco.parkmanager.alias.Alias;
import tv.tirco.parkmanager.storage.DataStorage;

public class Aliases extends AutoUpdateConfigLoader {
	private static Aliases instance;

	private Aliases() {
		super("alias.yml");
		validate();
	}
	
	public void save() {
		saveFile();
	}
	
	public static Aliases getInstance() {
		if (instance == null) {
			instance = new Aliases();
		}

		return instance;
	}

	@Override
	protected void loadKeys() {

	}

	@Override
	protected boolean validateKeys() {
		// Validate all the settings!
		List<String> reason = new ArrayList<String>();

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
	
	public void loadAllAliases() {
		for(String key : config.getKeys(false)) {
			String identifier = key;
			List<String> aliasText = config.getStringList(identifier + ".aliasText");
			boolean asConsole = config.getBoolean(identifier + ".asConsole", false);
			boolean isPermission = config.getBoolean(identifier + ".isPermission", false);
			Alias a = new Alias(identifier, aliasText, asConsole, isPermission);
			
			DataStorage.getInstance().addAlias(a);
		}
	}
	
	public void saveAllAliases() {
		for(Alias a : DataStorage.getInstance().getAliases()) {
			if(a.isChanged()) {
				setAlias(a, false);
				a.setChanged(false);
			}
		}
		saveFile();
	}
	
	public void setAlias(Alias alias, Boolean save) {
		config.set(alias.getIdentifier() + ".aliasText", alias.getText());
		config.set(alias.getIdentifier() + ".asConsole", alias.asConsole());
		config.set(alias.getIdentifier() + ".isPermission", alias.isPermission());
		
		//String identifier, String aliasText, Boolean asConsole, Boolean isPermission
		if(save) {
			saveFile();
			alias.setChanged(false);
		}
	}
	
	public boolean isSet(String key) {
		return config.isSet(key);
	}
	
}
