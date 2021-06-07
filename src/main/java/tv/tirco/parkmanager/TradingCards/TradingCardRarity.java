package tv.tirco.parkmanager.TradingCards;

public enum TradingCardRarity {
	COMMON,
	UNCOMMON,
	RARE,
	EPIC,
	LEGENDARY;

	
	public String getAsString() {
		switch(this) {
		case COMMON:
			return "&f&lCommon";
		case UNCOMMON:
			return "&e&lUncommon";
		case RARE:
			return "&a&lRare";
		case EPIC:
			return "&3&lEpic";
		case LEGENDARY:
			return "&d&lLegendary";
		default:
			return "&7Unknown";
		}
	}
	
	String getNamePrefix() {
		switch(this) {
		case COMMON:
			return "&f&lCommon Card:";
		case UNCOMMON:
			return "&e&lUncommon Card:";
		case RARE:
			return "&a&lRare Card:";
		case EPIC:
			return "&3&lEpic Card:";
		case LEGENDARY:
			return "&d&lLegendary Card:";
		default:
			return "";
		}
	}
		
	public double getBaseValue() {
		switch(this) {
		case COMMON:
			return 1.0;
		case UNCOMMON:
			return 5.0;
		case RARE:
			return 25.0;
		case EPIC:
			return 100.0;
		case LEGENDARY:
			return 200.0;
		default: 
			return 1.0;
		}
	}
	/*
	 * 	public String getAsString() {
		switch(this) {
		case COMMON:
		case UNCOMMON:
		case RARE:
		case EPIC:
		case LEGENDARY:
		default:
		}
	}
	 */

	public int getRarityCode() {
		switch(this) {
		case COMMON:
			return 1;
		case UNCOMMON:
			return 2;
		case RARE:
			return 3;
		case EPIC:
			return 4;
		case LEGENDARY:
			return 5;
		default:
			return 1;
		}
	}
		
	public static TradingCardRarity getRarityFromCode(String code) {
		switch(code) {
		case "1":
			return COMMON;
		case "2":
			return UNCOMMON;
		case "3":
			return RARE;
		case "4":
			return EPIC;
		case "5":
			return LEGENDARY;
		default:
			return COMMON;
		}
	}


}
