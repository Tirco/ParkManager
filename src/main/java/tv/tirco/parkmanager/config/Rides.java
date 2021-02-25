package tv.tirco.parkmanager.config;

import java.util.ArrayList;
import java.util.List;

import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;

public class Rides extends AutoUpdateConfigLoader {
	private static Rides instance;

	private Rides() {
		super("rides.yml");
		validate();
	}
	
	public void save() {
		saveFile();
	}
	
	public static Rides getInstance() {
		if (instance == null) {
			instance = new Rides();
		}

		return instance;
	}

	@Override
	protected void loadKeys() {
		for(String s : config.getConfigurationSection("rides").getKeys(false)) {
			String identifier = s;
			String name = config.getString("rides." + s + ".name", "Unknown");
			String description = config.getString("rides." + s + ".description", "Unknown");
			double maxPayout = config.getDouble("rides." + s + ".maxPayout", 100.00);
			double defaultPayPerMinute = config.getDouble("rides." + s + ".defaultPayPerMinute", 100.00);
			
			Ride ride = new Ride(identifier, maxPayout, name, description, defaultPayPerMinute, false);
			DataStorage.getInstance().addRide(ride);
		}
	}
	
	protected void saveRides() {
		for(Ride r : DataStorage.getInstance().getRides()) {
			if(r.changed()) {
				String path = "rides." + r.getIdentifier() + ".";
				config.set(path+"name", r.getName());
				config.set(path+"decription", r.getDescription());
				config.set(path+"maxPayout", r.maxPayout());
				config.set(path+"defaultPayPerMinute", r.getPayPerMinute());
			}
		}
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

	
	public boolean isSet(String key) {
		return config.isSet(key);
	}
	




}
