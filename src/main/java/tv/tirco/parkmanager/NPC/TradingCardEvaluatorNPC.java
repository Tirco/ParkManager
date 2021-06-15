package tv.tirco.parkmanager.NPC;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.TradingCards.TradingCardCondition;
import tv.tirco.parkmanager.TradingCards.TradingCardConfig;
import tv.tirco.parkmanager.TradingCards.TradingCardManager;
import tv.tirco.parkmanager.util.MessageHandler;
import tv.tirco.parkmanager.util.Util;

public class TradingCardEvaluatorNPC extends Trait{
	
	public TradingCardEvaluatorNPC() {
		super("tradingcardevaluator");
		plugin = ParkManager.parkManager;
	}
	
	ParkManager plugin = null;
	
//	Boolean isShady = false;
	
//	@Persist("shady") boolean automaticallyPersistedSetting = false;
	
	// Here you should load up any values you have previously saved (optional). 
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getEntity() will return null.
//	public void load(DataKey key) {
//		isShady = key.getBoolean("shady", false);
//	}
	
	// Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
//	public void save(DataKey key) {
//		key.setBoolean("shady",isShady);
//	}
	
	List<String> shadyComments = ImmutableList.of(
			"&eWhat do you want?",
			"&eDo you have any?",
			"&eMust... Have... ALL!",
			"&eGotta collect 'em all!",
			"&eBring me a &aTrading Card&e and &a10$&e and I will rate it!",
			"&eEhehehhe... My Best one is a Shiny GEM-MINT King Cobrex! Now I just need him to sign it!");
	
    // An example event handler. All traits will be registered automatically as Bukkit Listeners.
    @EventHandler
    public void clickr(NPCRightClickEvent e) {
        if(e.getNPC() == this.getNPC()){
        	NPC npc = this.getNPC();
            Player player = e.getClicker();
            
            //Verify card
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item == null || item.getType().equals(Material.AIR)) {
            	sendMessage(npc, player, shadyComments.get(Util.getRandom().nextInt(shadyComments.size())));
            	return;
            }
            
            if(!item.getType().equals(Material.BLAZE_POWDER)) {
            	sendMessage(npc, player, "&eI have no interest in that...");
            	return;
            }
            
            NBTItem nbti = new NBTItem(item);
            if(!nbti.hasNBTData() || !nbti.hasKey("TradingCardID")) {
            	sendMessage(npc, player, "&eI have no interest in that...");
            	return;
            }
            
            TradingCardCondition cond = TradingCardCondition.valueOf(nbti.getString("TradingCardCondition"));
            
            if(cond != TradingCardCondition.UNKNOWN) {
            	sendMessage(npc, player, "&eWhat? Do you think I'm blind?!");
            	sendMessage(npc, player, "&eThis card is already rated!");
            	return;
            }
            
            //ECO
            Economy econ = ParkManager.getEconomy();
            EconomyResponse r = econ.withdrawPlayer(player, 10);
            if(r.transactionSuccess()) {
                player.sendMessage(" ");
            	sendMessage(npc, player, String.format("I have taken %s from your wallet.", econ.format(r.amount)));
            } else {
            	sendMessage(npc,player,"&eIt seems we can't do this right now.");
            	sendMessage(npc,player,"&eCome back when you can pay me!.");
            	return;
            }
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

            sendMessage(npc, player, "&eNow... Let's have a look at that card...");
            
			TradingCardCondition newCondition = TradingCardConfig.getInstance().getRandomCondition();
			
			player.sendMessage(" ");
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
			"&6Your card has been evaluated to be in a &d" + newCondition.getAsString() + "&6 condition."));
            
			ItemStack updatedCard = TradingCardManager.getInstance().updateCondition(newCondition, item);
			player.getInventory().setItemInMainHand(updatedCard);

        }
    }
    
    private void sendMessage(NPC npc, Player player, String message) {
    	player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
    			"&a["+npc.getName()+"&a -> Me] " + message));
    }
    
//    // Called every tick
//    @Override
//    public void run() {
//    }
    
	//Run code when your trait is attached to a NPC. 
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
    	MessageHandler.getInstance().log(npc.getName() + ChatColor.YELLOW + " has been assigned tradingcardevaluator!");
    }
//    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getEntity() is still valid.
//    @Override
//    public void onDespawn() {
//    }
//
//    //Run code when the NPC is spawned. Note that npc.getEntity() will be null until this method is called.
//    //This is called AFTER onAttach and AFTER Load when the server is started.
//    @Override
//    public void onSpawn() {
//
//    }
//
//    //run code when the NPC is removed. Use this to tear down any repeating tasks.
//    @Override
//    public void onRemove() {
//    }

}
