package tv.tirco.parkmanager.Inventories;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tv.tirco.parkmanager.ParkManager;


public class ItemModifier {
private ParkManager main;
private ItemStack item;
private Inventory itemModifierMenu;
boolean create;

private ItemStack background;
    
    public ItemModifier(ParkManager main, ItemStack item, Player player)
    {
    	this.setMain(main);
    	this.create = true;
    	openGUI(player);
    }
    
    public ItemModifier(ParkManager main, Player player, Inventory inv)
    {
    	this.setMain(main);
    	this.item = inv.getItem(22);
    	this.create = false;
    	this.itemModifierMenu = inv;
    	openGUI(player);
    }

	public void openGUI(Player player) 
	{
		
		boolean unbreakable = false;
		boolean hasModelData = false;
		ItemMeta meta;
		
		if(create || itemModifierMenu == null) {
			this.itemModifierMenu = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', 
					"&6&lItem Modifier"));
			
			ItemStack background = getBackground();
			for(int i = 0; i < itemModifierMenu.getSize(); i++) {
				if(i == 22) {
					continue;
				}
				if(itemModifierMenu.getItem(i) == null || itemModifierMenu.getItem(i).getType().equals(Material.AIR)) {
					itemModifierMenu.setItem(i, background);
				}
			}
			
			itemModifierMenu.setItem(44, new ItemStack(getItem("&cExit", 
					Material.REDSTONE_BLOCK,
					1,new ArrayList<String>())));

		}
		
		if(item != null) {
			if(item.hasItemMeta()) {
				meta = item.getItemMeta();
				unbreakable = meta.isUnbreakable();
				hasModelData = meta.hasCustomModelData();
			}

		}
		
		itemModifierMenu.setItem(10, getItem("&6Unbreakable: " + unbreakable, 
				(unbreakable ? Material.LIME_WOOL : Material.RED_WOOL),
				1,new ArrayList<String>()));
		
		List<String> lore = new ArrayList<String>();
		if(hasModelData) {
			int i = 0;
			if(hasModelData) {
				i = item.getItemMeta().getCustomModelData();
				lore.add("Data: " + i);
			}
		}
		itemModifierMenu.setItem(19, getItem("&6Has Model Data: " + hasModelData, 
				(hasModelData ? Material.LIME_WOOL : Material.RED_WOOL),
				1,lore));
		


		
		//Button 1: Unbreakable
		//Button 2: modelData
		//Button 3: 
		//Button 4:
		//Button 5:
		//Button 6: Hide flags.
		
		player.openInventory(itemModifierMenu);
		
		
		
	}
	
	private ItemStack getItem(String name, Material mat, int i, List<String> lore) {
		ItemStack item= new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		meta.setLore(lore);
		item.setItemMeta(meta);
		item.setAmount(i);
		return item;
	}

	public ItemStack getBackground() {
		if(this.background == null) {
			ItemStack bg = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
			ItemMeta meta = bg.getItemMeta();
			meta.setDisplayName(" ");
			bg.setItemMeta(meta);
			this.background = bg;
		}
		return this.background;
		
	}

	public ParkManager getMain() {
		return main;
	}

	public void setMain(ParkManager main) {
		this.main = main;
	}
}
