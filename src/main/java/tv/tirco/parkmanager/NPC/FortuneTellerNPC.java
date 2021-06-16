package tv.tirco.parkmanager.NPC;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.util.MessageHandler;
import tv.tirco.parkmanager.util.Util;

public class FortuneTellerNPC extends Trait{
	
	public FortuneTellerNPC() {
		super("fortuneteller");
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
	
//	List<String> goodLuckComments = ImmutableList.of(
//			"&eWhat do you want");
	
    // An example event handler. All traits will be registered automatically as Bukkit Listeners.
    @EventHandler
    public void clickr(NPCRightClickEvent e) {
        if(e.getNPC() == this.getNPC()){
        	NPC npc = this.getNPC();
            Player player = e.getClicker();
            
            if(player.hasPotionEffect(PotionEffectType.LUCK) || player.hasPotionEffect(PotionEffectType.UNLUCK)) {
            	sendMessage(npc,player, "&eIt seems your fortune has already been revealed to you.");
            	sendMessage(npc,player, "&eCome see me again when that has passed.");
            	return;
            }
            
            //ECO
            Economy econ = ParkManager.getEconomy();
            EconomyResponse r = econ.withdrawPlayer(player, 10);
            if(!r.transactionSuccess()) {
            	sendMessage(npc,player,"&eI sense that your wallet is lacking...");
            	sendMessage(npc,player,"&eCome back when you can pay me!");
            	return;
            }
            player.sendMessage(" ");
            sendMessage(npc, player, String.format("I have taken %s from your wallet.", econ.format(r.amount)));
            sendMessage(npc, player, "&eNow let's see what the spirits say...");
            player.sendMessage(" ");
            int result = Util.getRandom().nextInt(10); //0, 1, 2, 3, 4, 5, 6, 7, 8, 9
            switch(result) {
            case 0:
            case 1:
            case 2:
            case 3:
            	//Level 1
            	player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 60*20*30, 0));
            	sendMessage(npc, player, "&2The spirits are in good humor today. I sense you'll have a little extra luck.");
            	return;
            case 4:
            	//Level 2
            	player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 60*20*30, 1));
            	sendMessage(npc, player, "&aThe spirits are very pleased today! They have blessed you with great fortune!");
            	return;
            case 5:
            	//Bad 2
            	player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 60*20*30, 1));
            	sendMessage(npc, player, "&4The spirits are very displeased today. They have cursed you with poor luck...");
            	return;
            case 6:
            case 7:
            case 8:
            case 9:
            default:
            	//Bad 1
            	player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 60*20*30, 0));
            	sendMessage(npc, player, "&cThe spirits are somewhat annoyed today. I sense luck is not on your side...");
            	return;
            }


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
    	MessageHandler.getInstance().log(npc.getName() + ChatColor.YELLOW + " has been assigned FortuneTeller!");
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
