package tv.tirco.parkmanager;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.google.common.base.Charsets;

import net.milkbowl.vault.economy.Economy;
import tv.tirco.parkmanager.Inventories.ItemModifierListener;
import tv.tirco.parkmanager.commands.BenchCommand;
import tv.tirco.parkmanager.commands.ExitRideCommand;
import tv.tirco.parkmanager.commands.GadgetsCommand;
import tv.tirco.parkmanager.commands.HideItemInfoCommand;
import tv.tirco.parkmanager.commands.ModelDataCommand;
import tv.tirco.parkmanager.commands.ResourcePackCommand;
import tv.tirco.parkmanager.commands.RideAdminCommand;
import tv.tirco.parkmanager.commands.RidesCommand;
import tv.tirco.parkmanager.commands.SitCommand;
import tv.tirco.parkmanager.commands.VoucherCommand;
import tv.tirco.parkmanager.config.Aliases;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.config.Rides;
import tv.tirco.parkmanager.listeners.CommandStopper;
import tv.tirco.parkmanager.listeners.EntityInteractListener;
import tv.tirco.parkmanager.listeners.JoinLeaveListener;
import tv.tirco.parkmanager.listeners.ProtectionListener;
import tv.tirco.parkmanager.listeners.RideMenuListener;
import tv.tirco.parkmanager.listeners.ShulkerBoxListener;
import tv.tirco.parkmanager.storage.database.DatabaseManager;
import tv.tirco.parkmanager.storage.database.DatabaseManagerFactory;
import tv.tirco.parkmanager.traincarts.CmdTrainListener;
import tv.tirco.parkmanager.traincarts.RideTrainListener;
import tv.tirco.parkmanager.util.MessageHandler;
import tv.tirco.parkmanager.util.PapiExpansion;

public class ParkManager extends JavaPlugin {
    
	public ParkManager parkManagerPlugin;
	public static String playerDataKey = "ParkManager Tracker";

	public boolean noErrorsInConfigFiles = true;
	
	public final CmdTrainListener cmdTrainListener = new CmdTrainListener();
	public final RideTrainListener rideTrainListener = new RideTrainListener();
	
    public static ParkManager parkManager;
    
	public PapiExpansion placeholders;
	public boolean papi = false;
    
	
	// File Manager setup bulk
	File mainFile;
	static String mainDirectory;
	static String userFileDirectory;
	static String usersFile;
	
	public static Plugin plugin;
	public static DatabaseManager db;
	
	//Economy
	private static Economy econ = null;

	@Override
    public void onEnable() {
		plugin = this;
		parkManager = this;
        
    	//Vault
        if (!setupEconomy() ) {
            MessageHandler.getInstance().log(Level.SEVERE, String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		
    	setupFilePaths();
        loadConfig();

        // Commands enabled with following method must have entries in plugin.yml
        getCommand("example").setExecutor(new ExampleCommand(this));
        getCommand("sit").setExecutor(new SitCommand());
        getCommand("hideiteminfo").setExecutor(new HideItemInfoCommand());
        getCommand("bench").setExecutor(new BenchCommand());
        getCommand("modeldata").setExecutor(new ModelDataCommand());
        getCommand("voucher").setExecutor(new VoucherCommand());
        getCommand("rides").setExecutor(new RidesCommand());
        getCommand("rideadmin").setExecutor(new RideAdminCommand());
        getCommand("exitride").setExecutor(new ExitRideCommand());
        getCommand("resourcepack").setExecutor(new ResourcePackCommand());
        getCommand("gadgets").setExecutor(new GadgetsCommand());
        
        db = DatabaseManagerFactory.getDatabaseManager();
        
        //Register carts
        if(Bukkit.getPluginManager().getPlugin("Train_Carts") != null) {
        	Bukkit.getLogger().log(Level.INFO, "Hooking into Train_Carts");
        	SignAction.register(cmdTrainListener);
        	SignAction.register(rideTrainListener);
        } else {
        	Bukkit.getLogger().log(Level.INFO, "Could not find the Train_Carts plugin.");
        }
        
        //PAPI
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			placeholders = new PapiExpansion(this);
            placeholders.register();
            this.papi = true;
		}
        
        Bukkit.getPluginManager().registerEvents(new ItemModifierListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShulkerBoxListener(), this);
        Bukkit.getPluginManager().registerEvents(new RideMenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new CommandStopper(), this);
        
        
    }
    
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    	Rides.getInstance().saveRides();
    	
    	SignAction.unregister(cmdTrainListener);
    	SignAction.unregister(rideTrainListener);
    	

    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
    
	public InputStreamReader getResourceAsReader(String fileName) {
		InputStream in = getResource(fileName);
		return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
	}

	
	private void loadConfig() {
		Config.getInstance();
		Aliases.getInstance();
		Rides.getInstance().loadKeys();
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
