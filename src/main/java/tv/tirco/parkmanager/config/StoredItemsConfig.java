package tv.tirco.parkmanager.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import tv.tirco.parkmanager.alias.Alias;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.util.MessageHandler;

public class StoredItemsConfig extends AutoUpdateConfigLoader {
	private static StoredItemsConfig instance;

	private StoredItemsConfig() {
		super("storeditems.yml");
		validate();
	}
	
	public void save() {
		saveFile();
	}
	
	public static StoredItemsConfig getInstance() {
		if (instance == null) {
			instance = new StoredItemsConfig();
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
	
	public void loadAllItems() {
		MessageHandler.getInstance().debug("----  OwedItems ---");
		//Stored as "<uudi>.1.<lots of itemstuff>"
		HashMap<String,ItemStack> storedItems = new HashMap<String,ItemStack>();
		for(String key : config.getKeys(false)) {
			ItemStack item = ItemStack.deserialize(config.getConfigurationSection(key).getValues(true));
			//ConfigurationSection section = config.getConfigurationSection(player);
			if(item != null) {
				storedItems.put(key, item);
			} else {
				MessageHandler.getInstance().log("Error with stored item " + key + " Item was NULL. - Item will be deleted.");
			}
			
		}
		DataStorage.getInstance().setStoredItems(storedItems);
	}
	
	public void clearAllSavedOwedItems() {
		Set<String> keys = config.getKeys(false);
		for(String s : keys) {
			config.set(s, null);
		}
	}
	
	public void saveAllItems() {
		HashMap<UUID, List<ItemStack>> owedItems = DataStorage.getInstance().getAllOwedItems();
		clearAllSavedOwedItems(); 
		for(UUID id : owedItems.keySet()) {
			List<ItemStack> items = owedItems.get(id);
			int i = 0;
			for(ItemStack item : items) {
				config.set(id.toString() + "." + i, item.serialize());
				i++;
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
