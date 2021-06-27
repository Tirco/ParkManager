package tv.tirco.parkmanager.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.playerdata.PlayerData;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.tasks.PlayerProfileLoadingTask;
import tv.tirco.parkmanager.util.MessageHandler;

public class JoinLeaveListener implements Listener{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		//Spawn
		Player player = e.getPlayer();
		Location spawn = ParkManager.parkManager.spawn;
		player.teleport(spawn, TeleportCause.PLUGIN);
		
		//Owed Item Check
		UUID uuid = player.getUniqueId();
		if(DataStorage.getInstance().isOwedItems(uuid)) {
			player.sendMessage(ChatColor.YELLOW + "There were some items waiting for you!");
			List<ItemStack> owedItems = DataStorage.getInstance().owedItems(uuid);
			List<ItemStack> stillOwedItems = new ArrayList<ItemStack>();
			Inventory inv = player.getInventory();
			for(ItemStack item : owedItems) {
				if(inv.firstEmpty() != -1) {
					inv.addItem(item);
				} else {
					stillOwedItems.add(item);
				}
			}
			MessageHandler.getInstance().log(ChatColor.YELLOW + player.getName() + " had " + owedItems.size() + " items waiting for them when they logged in.");
			MessageHandler.getInstance().log(ChatColor.YELLOW + "We added what we could. There are now " + stillOwedItems.size() + " items remaining.");
			DataStorage.getInstance().clearPlayerOwedItems(uuid);
			
			if(!stillOwedItems.isEmpty()) {
				DataStorage.getInstance().addPlayerOwedItems(uuid, stillOwedItems);
				player.sendMessage(ChatColor.YELLOW + "We could not add all of your missing items to your inventory. There are still " + stillOwedItems.size() + " item(s) left.");
				player.sendMessage(ChatColor.YELLOW + "Please use the command " + ChatColor.GREEN + "/oweditems " + ChatColor.YELLOW + "to reclaim them.");
			}
		}
		
		new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(ParkManager.plugin, 60);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		//TODO if in ride remove from ride.
		if(player.isInsideVehicle()) {
			player.getVehicle().removePassenger(player);
		}
		World world = Bukkit.getWorld("world"); //TODO Config?
		Location spawn = new Location(world, -29.5, 65, 161.5, 0f, 0f);
		player.teleport(spawn, TeleportCause.PLUGIN);
		
		if(UserManager.hasPlayerDataKey(player)) {
			PlayerData pData = UserManager.getPlayer(player);
			pData.save();
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		if(e.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
			//Gamemode failsafe.
			e.getPlayer().setGameMode(GameMode.ADVENTURE);
		}
	}
}
