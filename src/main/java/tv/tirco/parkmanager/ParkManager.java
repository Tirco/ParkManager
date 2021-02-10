package tv.tirco.parkmanager;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.google.common.base.Charsets;

import tv.tirco.parkmanager.Inventories.ItemModifierListener;
import tv.tirco.parkmanager.commands.BenchCommand;
import tv.tirco.parkmanager.commands.HideItemInfoCommand;
import tv.tirco.parkmanager.commands.ModelDataCommand;
import tv.tirco.parkmanager.commands.SitCommand;
import tv.tirco.parkmanager.commands.VoucherCommand;
import tv.tirco.parkmanager.config.Aliases;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.listeners.EntityInteractListener;
import tv.tirco.parkmanager.listeners.JoinLeaveListener;
import tv.tirco.parkmanager.listeners.ProtectionListener;
import tv.tirco.parkmanager.listeners.ShulkerBoxListener;
import tv.tirco.parkmanager.traincarts.CmdTrainListener;

public class ParkManager extends JavaPlugin {
    
	public ParkManager parkManagerPlugin;

	public boolean noErrorsInConfigFiles = true;
	
	public final CmdTrainListener cmdTrainListener = new CmdTrainListener();
	
    public static ParkManager parkManager;

	@Override
    public void onEnable() {
		parkManagerPlugin = this;
		parkManager = this;
        // Don't log enabling, Spigot does that for you automatically!
		
    	setupFilePaths();
        loadConfig();

        // Commands enabled with following method must have entries in plugin.yml
        getCommand("example").setExecutor(new ExampleCommand(this));
        getCommand("sit").setExecutor(new SitCommand());
        getCommand("hideiteminfo").setExecutor(new HideItemInfoCommand());
        getCommand("bench").setExecutor(new BenchCommand());
        getCommand("modeldata").setExecutor(new ModelDataCommand());
        getCommand("voucher").setExecutor(new VoucherCommand());
        
        //Register carts
        if(Bukkit.getPluginManager().getPlugin("Train_Carts") != null) {
        	Bukkit.getLogger().log(Level.INFO, "Hooking into Train_Carts");
        	SignAction.register(cmdTrainListener);
        } else {
        	Bukkit.getLogger().log(Level.INFO, "Could not find the Train_Carts plugin.");
        }
        
        Bukkit.getPluginManager().registerEvents(new ItemModifierListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShulkerBoxListener(), this);
        
    }
    
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    	
    	SignAction.unregister(cmdTrainListener);
    }
    
	public InputStreamReader getResourceAsReader(String fileName) {
		InputStream in = getResource(fileName);
		return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
	}
	
	// File Manager setup bulk
	File mainFile;
	static String mainDirectory;
	static String userFileDirectory;
	static String usersFile;
	
	private void loadConfig() {
		Config.getInstance();
		Aliases.getInstance();
	}
	
	public static String getMainDirectory() {
		return mainDirectory;
	}

	public static String getFlatFileDirectory() {
		return userFileDirectory;
	}

	public static String getUsersFilePath() {
		return usersFile;
	}
	
	private void setupFilePaths() {
		mainFile = getFile();
		mainDirectory = getDataFolder().getPath() + File.separator;
		userFileDirectory = mainDirectory + "userFiles" + File.separator;
		usersFile = userFileDirectory + "Users";
		fixFilePaths();
	}
	
	private void fixFilePaths() {
		File currentFlatfilePath = new File(userFileDirectory);
		currentFlatfilePath.mkdirs();
	}
}
