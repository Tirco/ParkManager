package tv.tirco.parkmanager.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import tv.tirco.parkmanager.storage.DataStorage;

public class AdminWarpsConfig extends AutoUpdateConfigLoader {
	private static AdminWarpsConfig instance;

	private AdminWarpsConfig() {
		super("adminwarps.yml");
		validate();
	}
	
	public void save() {
		saveFile();
	}
	
	public static AdminWarpsConfig getInstance() {
		if (instance == null) {
			instance = new AdminWarpsConfig();
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
	
	public void loadAllWarps() {
		HashMap<String,Location> warps = new HashMap<String,Location>();
		for(String key : config.getKeys(false)) {
			Location loc = config.getLocation(key);
			warps.put(key, loc);
		}
		DataStorage.getInstance().setAdminWarps(warps);
		DataStorage.getInstance().setAdminWarpsChanged(false);
	}
	
	public void saveAllWarps() {
		Map<String, Location> warps = DataStorage.getInstance().getAdminWarps();
		for(String s : warps.keySet()) {
			config.set(s, warps.get(s));
		}
		saveFile();
	}

	
	public boolean isSet(String key) {
		return config.isSet(key);
	}
	
}
