package tv.tirco.parkmanager.alias;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Alias {
	
	String identifier; //The alias that triggers this.
	List<String> aliasText; //The text to trigger
	Boolean asConsole; //Triggered from console? if no, triggers as player
	Boolean isPermission; //Is this a permission to grant?
	boolean changed;

	public Alias(String identifier, List<String> aliasText, Boolean asConsole, Boolean isPermission){
		this.identifier = identifier;
		this.aliasText = aliasText;
		this.asConsole = asConsole;
		this.isPermission = isPermission;
		this.changed = true;
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public List<String> getText() {
		return aliasText;
	}
	
	public void setText(List<String> text) {
		this.aliasText = text;
		this.changed = true;
	}
	
	public void addText(String text) {
		this.aliasText.add(text);
		this.changed = true;
	}
	
	public boolean removeText(String text) {
		this.changed = true;
		return aliasText.remove(text);
	}
	
	public boolean asConsole() {
		return asConsole;
	}
	
	public void setAsConsole(Boolean state) {
		this.asConsole = state;
		this.changed = true;
	}
	
	
	public boolean isPermission() {
		return isPermission;
	}
	
	public void setIsPermission(Boolean state) {
		this.isPermission = state;
		this.changed = true;
	}
	
	public void setChanged(Boolean state) {
		this.changed = state;
	}
	 
	/**
	 * 
	 * @param player - The player to trigger the alias for.
	 * @param item - Can be null - will remove 1 of this item from the player.
	 * @return Did it succeed?
	 */
	public boolean execute(Player player, ItemStack item) {
		if(isPermission) {
			List<String> unownedPerms = new ArrayList<String>();
			for(String s : aliasText) {
				if(!player.hasPermission(s)) {
					unownedPerms.add(s);
				}
			}
			
			if(unownedPerms.isEmpty()) {
				return false;
			} else {
				for(String perm : unownedPerms) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
							"lp user " + player.getName() + " permission set " + perm + " true");
				}
			}
		} else {
			//is command
			for(String c : aliasText) {
				String command = parseCommand(player,c);
				if(asConsole) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				} else {
					player.performCommand(command);
				}
			}
		}
		if(item != null) {
			ItemStack removeItem = item.clone();
			removeItem.setAmount(1);
			player.getInventory().removeItem(removeItem);
			player.playSound(player.getLocation(), Sound.ITEM_AXE_STRIP, 2.0f, 0.2f);
		}
		
		return true;
	}


	private String parseCommand(Player player, String command) {
		command = command.replace("%player%", player.getName());
		return command;
	}
}
