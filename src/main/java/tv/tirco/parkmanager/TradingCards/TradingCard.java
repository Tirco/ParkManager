package tv.tirco.parkmanager.TradingCards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.parkmanager.util.Util;

public class TradingCard {
	
	TradingCardRarity rarity;
	int id;
	String name;
	List<String> lore;
	boolean available;
	TradingCardType type;
	int modelData;
	String signerUUID;

	/**
	 * 
	 * @param name - the name of the card.
	 * @param id - the id of the card
	 * @param lore - the lore of the card
	 * @param rarity - the rarity of the card
	 * @param available 
	 * @param cardType 
	 * @param uuid - The uuid of the person that can sign it.
	 */
	public TradingCard(
			@NotNull String name,@NotNull int id,@NotNull List<String> lore,
			@NotNull TradingCardRarity rarity, @NotNull String UUID, 
			@NotNull TradingCardType cardType, boolean available, int modelData) {
		this.name = name;
		this.id = id;
		this.lore = lore;
		this.rarity = rarity;
		this.type = cardType;
		this.available = available;
		this.modelData = modelData;
		if(UUID == null) {
			this.signerUUID = "None";
		} else {
			this.signerUUID = UUID;
		}
		
	}
	
	public ItemStack buildCardItem(TradingCardCondition condition, Boolean signed, Boolean shiny) {
		//Card Data
		//Name
		//Lore
		String rarityString = rarity.getAsString();
		String typeString = type.getString();
		String rarityNamePrefix = rarity.getNamePrefix();
		String conditionString = condition.getAsString();
		
		ItemStack item = new ItemStack(Material.BLAZE_POWDER, 1);
		
		
		if(shiny) {
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		ItemMeta meta = item.getItemMeta();
		//Item Name
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
				rarityNamePrefix + " " + name + " &a#" + String.format("%03d",id)));
		
		//Build lore
		List<String> itemLore = new ArrayList<String>();
		itemLore.add(ChatColor.translateAlternateColorCodes('&', "&aTrading Card"));
		itemLore.add(ChatColor.translateAlternateColorCodes('&', "&aRarity: " + rarityString));
		itemLore.add(ChatColor.translateAlternateColorCodes('&', "&aCard Type: " + typeString));
		itemLore.add(ChatColor.translateAlternateColorCodes('&', "&aCondition:&7 " + conditionString));
		itemLore.add(ChatColor.translateAlternateColorCodes('&', "&f"));
		for(String s : lore) {
			itemLore.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		if(signed) {
			lore.add("");
			lore.add(ChatColor.translateAlternateColorCodes('&',
					TradingCardConfig.getInstance().getSignature(signerUUID)));
		}
		
		meta.setLore(itemLore);
		
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
		meta.setCustomModelData(modelData);
		item.setItemMeta(meta);
		
		//NBT
		NBTItem nbti = new NBTItem(item);
		nbti.setInteger("TradingCardID", id);
		nbti.setString("TradingCardRarity", rarity.toString());
		nbti.setString("TradingCardCondition", condition.toString());
		nbti.setBoolean("TradingCardShiny", shiny);
		nbti.setInteger("TradingCardDefaultLoreSize", itemLore.size());
		nbti.setBoolean("TradingCardSigned", false);
		nbti.setDouble("TradingCardScore", TradingCardManager.getInstance().getCardValue(rarity,condition,signed,shiny,available));
		nbti.setString("TradingCardStorageID", TradingCardManager.getInstance().getCardStorageID(id, shiny, signed, condition, rarity)); //TODO
		nbti.setLong("TradinCardNoStack", Util.getRandom().nextLong());
		
		item = nbti.getItem();
		return item;
	}

	public TradingCardRarity getRarity() {
		return rarity;
	}
	
	public int getID() {
		return id;
	}

	public boolean isAvailable() {
		return available;
	}

	
	public String getSignerUUID() {
		return this.signerUUID;
	}
}


