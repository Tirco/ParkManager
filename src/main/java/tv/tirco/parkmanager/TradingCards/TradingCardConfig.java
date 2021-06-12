package tv.tirco.parkmanager.TradingCards;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tv.tirco.parkmanager.config.AutoUpdateConfigLoader;
import tv.tirco.parkmanager.storage.DataStorage;
import tv.tirco.parkmanager.storage.Ride;

public class TradingCardConfig extends AutoUpdateConfigLoader {
	private static TradingCardConfig instance;

	private TradingCardConfig() {
		super("tradingcards.yml");
		validate();
	}
	
	public void save() {
		saveFile();
	}
	
	public static TradingCardConfig getInstance() {
		if (instance == null) {
			instance = new TradingCardConfig();
		}

		return instance;
	}

	@Override
	public void loadKeys() {

	}
	
	public void saveRides() {
		for(Ride r : DataStorage.getInstance().getRides()) {
			if(r.changed()) {
				String path = "rides." + r.getIdentifier() + ".";
				config.set(path+"name", r.getName());
				config.set(path+"description", r.getDescription());
				config.set(path+"maxPayout", r.maxPayout());
				config.set(path+"defaultPayPerMinute", r.getPayPerMinute());
				config.set(path+"warp", r.getWarp());
				config.set(path+"hasAdvancement", r.hasAdvancement());
				
				ItemStack icon = r.getIcon();
				config.set(path+"item.material", icon.getType().toString());
				int modeldata = 0;
				if(icon.hasItemMeta()) {
					ItemMeta meta = icon.getItemMeta();
					if(meta.hasCustomModelData()) {
						modeldata = meta.getCustomModelData();
					}
				}
				config.set(path+"item.modeldata", modeldata);
			}
		}
		save();
	}

	@Override
	protected boolean validateKeys() {
		// Validate all the settings!
		List<String> reason = new ArrayList<String>();

		// If the reason list is empty, keys are valid.
		return noErrorsInConfig(reason);
	}

	@SuppressWarnings("unused")
	private String getStringIncludingInts(String key) {
		String str = config.getString(key);

		if (str == null) {
			str = String.valueOf(config.getInt(key));
		}

		if (str.equals("0")) {
			str = "No value set for '" + key + "'";
		}
		return str;
	}

	
	public boolean isSet(String key) {
		return config.isSet(key);
	}
	
	public TradingCardRarity getRandomRarity() {
		//Double CommonRarityChance = config.getDouble("settings.rarity.chances.COMMON", 0.40);
		Double UncommonRarityChance = config.getDouble("settings.rarity.chances.UNCOMMON", 0.34);
		Double RareRarityChance = config.getDouble("settings.rarity.chances.RARE", 0.20);
		Double EpicRarityChance = config.getDouble("settings.rarity.chances.EPIC", 0.05);
		Double LegendaryRarityChance = config.getDouble("settings.rarity.chances.LEGENDARY", 0.01);
		
		Double random = Math.random();
			if(LegendaryRarityChance > random) {
				return TradingCardRarity.LEGENDARY;
			} else if(EpicRarityChance > random) {
				return TradingCardRarity.EPIC;
			} else if(RareRarityChance > random) {
				return TradingCardRarity.RARE;
			} else if(UncommonRarityChance > random) {
				return TradingCardRarity.UNCOMMON;
			} else {
				return TradingCardRarity.COMMON;
			}
	}
	
	public TradingCardCondition getRandomCondition() {
		Double GemMintChance = config.getDouble("settings.condition.chances.GEM_MINT", 0.001);
		Double MintChance = GemMintChance + config.getDouble("settings.condition.chances.MINT", 0.01);
		Double NearMintChance = MintChance + config.getDouble("settings.condition.chances.NEAR_MINT", 0.04);
		Double ExcellentChance = NearMintChance + config.getDouble("settings.condition.chances.EXCELLENT", 0.08);
		Double VeryGoodChance = ExcellentChance + config.getDouble("settings.condition.chances.VERY_GOOD", 0.1);
		Double GoodChance = VeryGoodChance + config.getDouble("settings.condition.chances.GOOD", 0.369);
		Double FairChance = GoodChance + config.getDouble("settings.condition.chances.FAIR", 0.25);
		//No need to grab poor, just have it stored so they can keep track easier.
		
		Double random = Math.random();
			if(GemMintChance > random) {
				return TradingCardCondition.GEM_MINT;
			} else if (MintChance > random) {
				return TradingCardCondition.MINT;
			} else if (NearMintChance > random) {
				return TradingCardCondition.NEAR_MINT;
			} else if (ExcellentChance > random) {
				return TradingCardCondition.EXCELLENT;
			} else if (VeryGoodChance > random) {
				return TradingCardCondition.VERY_GOOD;
			} else if (GoodChance > random) {
				return TradingCardCondition.GOOD;
			} else if (FairChance > random) {
				return TradingCardCondition.FAIR;
			} else {
				return TradingCardCondition.POOR;
			}
		
	}
	
	public void loadAllCards() {
		TradingCardManager.getInstance().clearLists(false);
		for(String key : config.getConfigurationSection("cards").getKeys(false)) {
			try {
				int id = Integer.parseInt(key);
				TradingCard card = buildCardObject(id);
				TradingCardManager.getInstance().addCard(card);
			} catch (NumberFormatException ex) {
				continue;
			}
		}
	}
	
	private TradingCard buildCardObject(int id) {
		//Card Data
		String name = config.getString("cards."+ id + ".name", "Unknown");
		List<String> lore = config.getStringList("cards." + id + ".lore");
		String signerUUID = config.getString("cards." + id + ".signer","NONE");
		TradingCardRarity rarity = TradingCardRarity.valueOf(config.getString("cards." + id + ".rarity","COMMON").toUpperCase());
		TradingCardType cardType = TradingCardType.valueOf(config.getString("cards." + id + ".type","UNKNOWN").toUpperCase());
		boolean available = config.getBoolean("cards." + id + ".available");
		int modelData = config.getInt("cards."+id+".modeldata", 1);
		
		return new TradingCard(name, id, lore, rarity, signerUUID, cardType, available,modelData);
	}

	public boolean getShinyRandom() {
		Double shinyChance = config.getDouble("settings.shinychance",0.05);
		Double random = Math.random();
		return (shinyChance > random);
	}

	public int getAmountOfCards() {
		return config.getConfigurationSection("cards").getKeys(false).size();
	}
	
	public boolean isEvaluatorNPC(int id) {
		return config.getIntegerList("settings.CardEvaluatorNPCs").contains(id);
	}

	
	public String getSignature(String signerUUID) {
		return config.getString("signatures."+signerUUID,"&7Signed by Unknown Player.");
	}
	




}
