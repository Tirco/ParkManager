package tv.tirco.parkmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tv.tirco.parkmanager.Inventories.ItemModifier;

public class ExampleCommand implements CommandExecutor {
    ParkManager plugin;

    public ExampleCommand(ParkManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("example")) {
            return false;
        }
        
        

        sender.sendMessage("Successfully used example command!");
        
        if(sender instanceof Player) {
        	Player player = (Player) sender;
            new ItemModifier(ParkManager.parkManager, player.getInventory().getItemInMainHand() ,player);
        }


        return true;
    }
}
