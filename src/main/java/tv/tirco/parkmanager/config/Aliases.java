package tv.tirco.parkmanager.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Aliases extends AutoUpdateConfigLoader {
	private static Aliases instance;

	private Aliases() {
		super("alias.yml");
		validate();
	}
	
	public void save() {
		saveFile();
		config.addDefault("this", "that");
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
	
	public void setValue(String key, String value) {
		config.set(key, value);
		saveFile();
	}
	
	public boolean isSet(String key) {
		return config.isSet(key);
	}
	
	public void executeCommandsAsConsole(String key, String playername) {
		List<String> commands = config.getStringList(key);
		ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
		if(commands != null) {
			for(String s : commands) {
				String cmd = s.replace("{player}", playername);
				Bukkit.getServer().dispatchCommand(sender, cmd);
			}
		}
	}
	
	public void executeCommandsAsPlayer(String key, Player player) {
		List<String> commands = config.getStringList(key);
		if(commands != null) {
			for(String s : commands) {
				String cmd = s.replace("{player}", player.getName());
				Bukkit.getServer().dispatchCommand(player, cmd);
			}
		}
	}
	
	public List<String> getAliasList(String key) {
		List<String> commands = config.getStringList(key);
		if(commands == null) {
			commands = new ArrayList<String>();
		}
		return commands;
	}


}
