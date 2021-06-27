package tv.tirco.parkmanager;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.google.common.base.Charsets;


import net.citizensnpcs.api.trait.TraitInfo;
import net.clownercraft.ccRides.ccRidesPlugin;
import net.milkbowl.vault.economy.Economy;
import tv.tirco.parkmanager.NPC.FortuneTellerNPC;
import tv.tirco.parkmanager.NPC.TradingCardEvaluatorNPC;
import tv.tirco.parkmanager.TradingCards.TradingCardBinderListener;
import tv.tirco.parkmanager.TradingCards.TradingCardConfig;
import tv.tirco.parkmanager.TradingCards.commands.TradingCardAdminCommand;
import tv.tirco.parkmanager.TradingCards.commands.TradingCardBinderCommand;
import tv.tirco.parkmanager.TradingCards.commands.TradingCardEvaluateCommand;
import tv.tirco.parkmanager.TradingCards.commands.TradingCardSignCommand;
import tv.tirco.parkmanager.commands.AliasCommand;
import tv.tirco.parkmanager.commands.BenchCommand;
import tv.tirco.parkmanager.commands.ExitRideCommand;
import tv.tirco.parkmanager.commands.GadgetsCommand;
import tv.tirco.parkmanager.commands.HideItemInfoCommand;
import tv.tirco.parkmanager.commands.ModelDataCommand;
import tv.tirco.parkmanager.commands.OwedItemsCommand;
import tv.tirco.parkmanager.commands.ParkmanagerCommand;
import tv.tirco.parkmanager.commands.RandomrideCommand;
import tv.tirco.parkmanager.commands.ResourcePackCommand;
import tv.tirco.parkmanager.commands.RideAdminCommand;
import tv.tirco.parkmanager.commands.RidesCommand;
import tv.tirco.parkmanager.commands.SitCommand;
import tv.tirco.parkmanager.commands.SpawnCommand;
import tv.tirco.parkmanager.config.AliasesConfig;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.config.OwedItemsConfig;
import tv.tirco.parkmanager.config.RidesConfig;
import tv.tirco.parkmanager.config.StoredItemsConfig;
import tv.tirco.parkmanager.listeners.CommandStopper;
import tv.tirco.parkmanager.listeners.ConsumeListener;
import tv.tirco.parkmanager.listeners.EntityInteractListener;
import tv.tirco.parkmanager.listeners.JoinLeaveListener;
import tv.tirco.parkmanager.listeners.ProtectionListener;
import tv.tirco.parkmanager.listeners.RideMenuListener;
import tv.tirco.parkmanager.listeners.ShulkerBoxListener;
import tv.tirco.parkmanager.storage.database.DatabaseManager;
import tv.tirco.parkmanager.storage.database.DatabaseManagerFactory;
import tv.tirco.parkmanager.storage.database.SaveTimerTask;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.traincarts.CmdTrainSignAction;
import tv.tirco.parkmanager.traincarts.RideTrainSignAction;
import tv.tirco.parkmanager.traincarts.TpTrainSignAction;
import tv.tirco.parkmanager.util.MessageHandler;
import tv.tirco.parkmanager.util.PapiExpansion;

public class ParkManager extends JavaPlugin {
    
	public ParkManager parkManagerPlugin;
	public static String playerDataKey = "ParkManager Tracker";

	public boolean noErrorsInConfigFiles = true;
	
	public final CmdTrainSignAction cmdTrainSignAction = new CmdTrainSignAction();
	public final RideTrainSignAction rideTrainSignAction = new RideTrainSignAction();
	public final TpTrainSignAction tpTrainSignAction = new TpTrainSignAction();
	
    public static ParkManager parkManager;
    
	public PapiExpansion placeholders;
	
	public static ccRidesPlugin ccRides;
	public static boolean ccRidesEnabled = false;
	
//	public static boolean geyserEnabled = false;
	
	public boolean papi = false;
	
	public Location spawn;

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
		
		this.spawn = new Location(Bukkit.getWorld("world"), -29.5, 65, 161.5, 0f, 0f);
        
		MessageHandler.getInstance().log("Setting up economy...");
		
    	//Vault
        if (!setupEconomy() ) {
            MessageHandler.getInstance().log(Level.SEVERE, String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
    	//check if Citizens is present and enabled.
        MessageHandler.getInstance().log("Hooking into Citizens...");
		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}

		//Register your trait with Citizens.   
		MessageHandler.getInstance().log("Registering Citizens traits...");
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TradingCardEvaluatorNPC.class).withName("tradingcardevaluator"));
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FortuneTellerNPC.class).withName("fortuneteller"));
		
        MessageHandler.getInstance().log("Fixing file paths...");
    	setupFilePaths();
    	MessageHandler.getInstance().log("Loading configuration files...");
        loadConfig();

        MessageHandler.getInstance().log("Registering commands...");
        // Commands enabled with following method must have entries in plugin.yml
        getCommand("sit").setExecutor(new SitCommand());
        getCommand("hideiteminfo").setExecutor(new HideItemInfoCommand());
        getCommand("bench").setExecutor(new BenchCommand());
        getCommand("modeldata").setExecutor(new ModelDataCommand());
        getCommand("alias").setExecutor(new AliasCommand());
        getCommand("rides").setExecutor(new RidesCommand());
        getCommand("rideadmin").setExecutor(new RideAdminCommand());
        getCommand("exitride").setExecutor(new ExitRideCommand());
        getCommand("resourcepack").setExecutor(new ResourcePackCommand());
        getCommand("gadgets").setExecutor(new GadgetsCommand());
        getCommand("randomride").setExecutor(new RandomrideCommand());
        getCommand("tradingcardadmin").setExecutor(new TradingCardAdminCommand());
        getCommand("tradingcardevaluate").setExecutor(new TradingCardEvaluateCommand());
        getCommand("tradingcardsign").setExecutor(new TradingCardSignCommand());
        getCommand("tradingcardbinder").setExecutor(new TradingCardBinderCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("parkmanager").setExecutor(new ParkmanagerCommand());
        getCommand("oweditems").setExecutor(new OwedItemsCommand());
        
        MessageHandler.getInstance().log("Loading database...");
        db = DatabaseManagerFactory.getDatabaseManager();
        
        //Register carts
        if(Bukkit.getPluginManager().getPlugin("Train_Carts") != null) {
        	MessageHandler.getInstance().log("Hooking into Train_Carts...");
        	SignAction.register(cmdTrainSignAction);
        	SignAction.register(rideTrainSignAction);
        	SignAction.register(tpTrainSignAction);
        } else {
        	MessageHandler.getInstance().log("Could not find the Train_Carts plugin... Ignoring");
        }
        
        //PAPI
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
        	MessageHandler.getInstance().log("Hooking into PlaceholderAPI...");
			placeholders = new PapiExpansion(this);
            placeholders.register();
            this.papi = true;
		} else {
			MessageHandler.getInstance().log("PlaceholderAPI not found... Ignoring");
		}
        
        //ccRides
        if(Bukkit.getPluginManager().getPlugin("ccRides") != null){
        	MessageHandler.getInstance().log("Hooking into ccRides...");
			ccRides = ccRidesPlugin.getInstance();
            ParkManager.ccRidesEnabled = true;
		} else {
			MessageHandler.getInstance().log("ccRides not found... Ignoring");
		}
        
        MessageHandler.getInstance().log("Setting up EventListeners...");
        Bukkit.getPluginManager().registerEvents(new EntityInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShulkerBoxListener(), this);
        Bukkit.getPluginManager().registerEvents(new RideMenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new CommandStopper(), this);
        Bukkit.getPluginManager().registerEvents(new TradingCardBinderListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConsumeListener(), this);
        
        
        MessageHandler.getInstance().log("Scheduling tasks...");
        
        scheduleTasks();
        
        MessageHandler.getInstance().log("Parkmanager has been Enabled! ...");
    }
    
    @Override
    public void onDisable() {
    	
    	MessageHandler.getInstance().log("Preparing to disable!");
    	MessageHandler.getInstance().log("Saving all users...!");
    	UserManager.saveAll();
        // Don't log disabling, Spigot does that for you automatically!
    	MessageHandler.getInstance().log("Saving all rides...");
    	RidesConfig.getInstance().saveRides();
    	MessageHandler.getInstance().log("Saving all aliases...");
    	AliasesConfig.getInstance().saveAllAliases();
    	MessageHandler.getInstance().log("Saving all owed items...");
    	OwedItemsConfig.getInstance().saveAllItems();
    	MessageHandler.getInstance().log("Saving all stored items...");
    	StoredItemsConfig.getInstance().saveAllItems();
    	
    	
    	
    	MessageHandler.getInstance().log("Unregistering TrainCarts signs...");
    	SignAction.unregister(cmdTrainSignAction);
    	SignAction.unregister(rideTrainSignAction);
    	SignAction.unregister(tpTrainSignAction);
    	
        MessageHandler.getInstance().log("Canceling all tasks...");
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        MessageHandler.getInstance().log("Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations
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

	private void scheduleTasks() {
		
		long saveInterval = Config.getInstance().getSaveInterval();
		if(saveInterval != 0) {
			long saveIntervalTicks = saveInterval * 1200; //1200 = 1 minute
			new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);
		}
		
	    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
	        public void run() {
	            for (Player player : Bukkit.getWorld("world").getPlayers()) {
	                if (player.getLocation().getY() < 0) {
	                	
	                    player.teleport(spawn);
	                    MessageHandler.getInstance().log("Saved " + player.getName() + " from eternal falling.");
	                }
	            }
	        }
	    },10*20, 10*20); //Every 10 seconds.

	}
	
	private void loadConfig() {
		Config.getInstance();
		AliasesConfig.getInstance().loadAllAliases();;
		RidesConfig.getInstance().loadKeys();
		TradingCardConfig.getInstance().loadAllCards();
		OwedItemsConfig.getInstance().loadAllItems();
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
	
	public static DatabaseManager getDB() {
		return db;
	}
	
	public static ccRidesPlugin ccRides() {
		return ccRides;
	}
	
	public static boolean ccRidesEnabled() {
		return ccRidesEnabled;
	}


}
