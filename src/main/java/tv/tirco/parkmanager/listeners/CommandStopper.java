package tv.tirco.parkmanager.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.storage.playerdata.UserManager;

public class CommandStopper implements Listener {

	//List<String> notAllowed = Arrays.asList("");

	@EventHandler
	public void onPrePlayerTriggerCommand(PlayerCommandPreprocessEvent e) {
		String message = e.getMessage();
		if(message == null || message.isEmpty()) {
			return;
		}
		
		Player player = e.getPlayer();
		if(player.getName().equalsIgnoreCase("Tirco")) {
			player.sendMessage("Debug - Command Message is: " + message);
		}
		
		//Is on ride test.
		if(!UserManager.hasPlayerDataKey(player)) {
			player.sendMessage(ChatColor.YELLOW + "Please wait, as your profile is still being loaded.");
			e.setCancelled(true);
			return;
		}
		if(UserManager.getPlayer(player).isInRide()) {
			if(!player.hasPermission("parkmanager.usecommandonride")) {
				if(message.startsWith("/exitride")
						||message.startsWith("/exit")
						||message.startsWith("/leaveride")) {
					
				} else {
					e.setCancelled(true);
					player.sendMessage(ChatColor.YELLOW + "You can not use that command while on a ride.");
					player.sendMessage(ChatColor.YELLOW + "To exit your current ride, use the command /exitride.");
				}
			}

			
		}
		
		//Creative players aren't region blocked.
		if(player.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		
		String worldname = player.getWorld().getName();

		if(message.startsWith("/tl") || message.startsWith("/thelab")
				|| message.startsWith("/thelab:thelab")
				|| message.startsWith("/thelab:tl")) {
			if(worldname.equalsIgnoreCase("world_thelab")) {
				
			} else {
				e.setCancelled(true);
				player.sendMessage("You can only use that command while you are in TheLab.");
				return;
			}
			
		}
	}
}
