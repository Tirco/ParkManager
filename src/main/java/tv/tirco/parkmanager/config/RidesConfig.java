package tv.tirco.parkmanager.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;

public class RidesConfig extends AutoUpdateConfigLoader {
	private static RidesConfig instance;

	private RidesConfig() {
		super("rides.yml");
		validate();
	}
	
	public void save() {
		saveFile();
	}
	
	public static RidesConfig getInstance() {
		if (instance == null) {
			instance = new RidesConfig();
		}

		return instance;
	}

	@Override
	public void loadKeys() {
		for(String s : config.getConfigurationSection("rides").getKeys(false)) {
			String identifier = s;
			String name = config.getString("rides." + s + ".name", "Unknown");
			List<String> description = config.getStringList("rides." + s + ".description");
			double maxPayout = config.getDouble("rides." + s + ".maxPayout", 100.00);
			double defaultPayPerMinute = config.getDouble("rides." + s + ".defaultPayPerMinute", 100.00);
			String warp = config.getString("rides." + s + ".warp", "Unknown");
			Boolean hasAdvancement = config.getBoolean("rides." + s + ".hasAdvancement", false);
			Boolean isEnabled = config.getBoolean("rides." + s + ".enabled", true);
			Material mat = Material.getMaterial(config.getString("rides."+identifier+".item.material", "Minecart"));
			
			int modeldata = config.getInt("rides."+identifier+".item.modeldata",0);
			
			Ride ride = new Ride(identifier, maxPayout, name, description, defaultPayPerMinute, warp, hasAdvancement, false, isEnabled, mat, modeldata);
			DataStorage.getInstance().addRide(ride);
		}
	}
	
	public void saveRides() {
		for(Ride r : DataStorage.getInstance().getRides()) {
			if(r.changed()) {
				String path = "rides." + r.getIdentifier() + ".";
				config.set(path+"name", r.getName());
				config.set(path+"description", r.getDescription());
				config.set(path+"maxPayout", r.maxPayout());
				config.set(path+"defaultPayPerMinute", r.getPayPerMinute());
				config.set(path+"warp", r.getWarp());
				config.set(path+"hasAdvancement", r.hasAdvancement());
				
				ItemStack icon = r.getIcon();
				config.set(path+"item.material", icon.getType().toString());
				int modeldata = 0;
				if(icon.hasItemMeta()) {
					ItemMeta meta = icon.getItemMeta();
					if(meta.hasCustomModelData()) {
						modeldata = meta.getCustomModelData();
					}
				}
				config.set(path+"item.modeldata", modeldata);
			}
		}
		save();
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
