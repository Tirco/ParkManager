package tv.tirco.parkmanager.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.alias.Alias;
import tv.tirco.parkmanager.config.AliasesConfig;
import tv.tirco.parkmanager.storage.DataStorage;

public class AliasCommand implements CommandExecutor,TabCompleter{
	
	List<String> actions = ImmutableList.of("create","addvalue","removevalue","setisperm","setasconsole","info","applytoitem","save","reload");
	List<String> booleans = ImmutableList.of("true","false");

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(args.length < 1 || args[0].equalsIgnoreCase("help")) {
			for(String s : actions) {
				sender.sendMessage("/alias " + s);
			}
			return true;
		}
		
		if(args.length < 1) {
			//No alias specify.
			sender.sendMessage(ChatColor.RED +"Please specify an action. Available actions are:");
			for(String s : actions) {
				sender.sendMessage("/alias " + s);
			}
			return true;
		}

		//Alias reload
		if(args[0].equalsIgnoreCase("reload")) {
			AliasesConfig.getInstance().loadAllAliases();
			sender.sendMessage(ChatColor.GREEN + "All aliases reloaded.");
			return true;
		}
		//Alias save
		if(args[0].equalsIgnoreCase("save")) {
			AliasesConfig.getInstance().saveAllAliases();
			sender.sendMessage(ChatColor.GREEN + "All aliases saved.");
			return true;
		}
		
		if(args.length < 2) {
			//No alias specify.
			sender.sendMessage(ChatColor.RED +"Please specify an alias.");
			return true;
		}

		
		String aliasName = args[1];
		
		//Create
		if(args[0].equalsIgnoreCase("create")) {
			
			if(DataStorage.getInstance().getAlias(aliasName) != null) {
				sender.sendMessage(ChatColor.RED + "That alias already exists!");
				return true;
			} else {
				Alias a = new Alias(aliasName, new ArrayList<String>(), false, false);
				DataStorage.getInstance().addAlias(a);
				AliasesConfig.getInstance().setAlias(a, false);
				sender.sendMessage(ChatColor.GREEN + 
						"A new alias with the identifier " + ChatColor.YELLOW + aliasName +
						ChatColor.GREEN + " has been created.");
				return true;
			}
		}
		
		Alias alias = DataStorage.getInstance().getAlias(aliasName);
		if(alias == null) {
			sender.sendMessage(ChatColor.RED +
					"An alias with the name " +
					ChatColor.YELLOW+ aliasName + ChatColor.RED 
					+ " could not be found.");
			return true;
		}
		
		//Info
		if(args[0].equalsIgnoreCase("info")) {
			sender.sendMessage(ChatColor.YELLOW + "Alias Name: " + ChatColor.GREEN + alias.getIdentifier());
			boolean isPermission = alias.isPermission();
			if(isPermission) {
				sender.sendMessage(ChatColor.YELLOW + "Alias Type: " + ChatColor.GREEN + "Permission.");
			} else {
				sender.sendMessage(ChatColor.YELLOW + "Alias Type: " + ChatColor.GREEN + "Command.");
				sender.sendMessage(ChatColor.YELLOW + "Run as: " + ChatColor.GREEN + (alias.asConsole() ? "Console" : "Player"));
			}
			sender.sendMessage(ChatColor.YELLOW + "Values: ");
			if(alias.getText().isEmpty()) {
				sender.sendMessage(ChatColor.GREEN + " None.");
			} else {
				for(String s : alias.getText()) {
					sender.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GREEN + s);
				}
			}
			return true;
		}
		
		if(args[0].equalsIgnoreCase("applytoitem")){
			if(sender instanceof Player) {
				Player player = (Player) sender;
				ItemStack item = player.getInventory().getItemInMainHand();
				
				//null check
				if(item == null || item.getType().equals(Material.AIR)) {
					player.sendMessage("You need to hold an item in your hand.");
					return true;
				}
				
				NBTItem nbti = new NBTItem(item);
				nbti.setString("alias", alias.getIdentifier());
				player.getInventory().setItemInMainHand(nbti.getItem());
				
				player.sendMessage("Item in hand has been updated.");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
				return true;
			}
		}
		
		

		if(args.length < 3) {
			//No value specify.
			sender.sendMessage("Please specify a value.");
			return true;
		}
		
		//addvalue
		if(args[0].equalsIgnoreCase("addvalue")) {
			String textToAdd = "";
			for(int i = 2 ; i < args.length ; i++){
				textToAdd += args[i] + " "; 
			}
			textToAdd = textToAdd.substring(0, textToAdd.length() - 1);
			alias.addText(textToAdd);
			sender.sendMessage(ChatColor.GREEN + "The following string has been added:");
			sender.sendMessage(ChatColor.YELLOW + textToAdd);
			return true;
		}
		
		//removevalue
		if(args[0].equalsIgnoreCase("removevalue")) {
			String textToRemove = "";
			for(int i = 2 ; i < args.length ; i++){
				textToRemove += args[i] + " "; 
			}
			textToRemove = textToRemove.substring(0, textToRemove.length() - 1);
			if(alias.removeText(textToRemove)) {
				sender.sendMessage(ChatColor.GREEN + "The value was successfully removed.");
			} else {
				sender.sendMessage(ChatColor.RED + "Failed to remove the value. Due to the extra spacing added in the parse command, you may have to manually remove it from the alias.yml file and perform a /alias reload");
			}
			return true;
		}
		//setperm
		if(args[0].equalsIgnoreCase("setisperm")) {
			boolean state = false;
			if(args[2].equalsIgnoreCase("true")) {
				state = true;
			}
			
			alias.setIsPermission(state);
			sender.sendMessage(ChatColor.YELLOW + (state ? "The alias is now a Permission" : "The alias is not a permission"));
			return true;
		}
		//setasconsole
		if(args[0].equalsIgnoreCase("setasconsole")) {
			boolean state = false;
			if(args[2].equalsIgnoreCase("true")) {
				state = true;
			}
			
			alias.setIsPermission(state);
			sender.sendMessage(ChatColor.YELLOW + (state ? "The alias will now be run as console" : "The alias will be run as the player"));
			return true;
		}

		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> aliases = DataStorage.getInstance().getAliases().stream().map(a -> a.getIdentifier()).collect(Collectors.toList());
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0], actions, new ArrayList<String>(actions.size()));
		case 2:
			if(!args[0].equalsIgnoreCase("create")) {
				return StringUtil.copyPartialMatches(args[1], aliases, new ArrayList<String>(aliases.size()));
			} else  {
				return null;
			}
		case 3:
			if(args[0].equalsIgnoreCase("removevalue")) {
				Alias aliasInCommand = DataStorage.getInstance().getAlias(args[1]);
				if(aliasInCommand == null) {
					return null;
				}
				return StringUtil.copyPartialMatches(args[2], aliasInCommand.getText(), new ArrayList<String>(aliasInCommand.getText().size()));
				
			} else if(args[0].equalsIgnoreCase("setisperm") ||
					args[0].equalsIgnoreCase("setasconsole")) {
				StringUtil.copyPartialMatches(args[2], booleans, new ArrayList<String>(booleans.size()));
			} else {
				return null;
			}
			
		default:
			return null;
    	}
			
	}
}
