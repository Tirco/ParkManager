package tv.tirco.parkmanager.TradingCards;

public enum TradingCardCondition {
	GEM_MINT,
	MINT,
	NEAR_MINT,
	EXCELLENT,
	VERY_GOOD,
	GOOD,
	FAIR,
	POOR,
	UNKNOWN;
	
	

	public String getAsString() {
		switch(this){
		case GEM_MINT:
			return "&dGem Mint";
		case MINT:
			return "&5Mint";
		case NEAR_MINT:
			return "&6Near Mint";
		case EXCELLENT:
			return "&eExcellent";
		case VERY_GOOD:
			return "&aVery Good";
		case GOOD:
			return "&2Good";
		case FAIR:
			return "&fFair";
		case POOR:
			return "&7Poor";
		default:
			return "&7Unknown";
		}
	}
	
	public double getValueDouble() {
		switch(this){
		case GEM_MINT:
			return 5.0;
		case MINT:
			return 3.0;
		case NEAR_MINT:
			return 2.5;
		case EXCELLENT:
			return 2.0;
		case VERY_GOOD:
			return 1.5;
		case GOOD:
			return 1.0;
		case FAIR:
			return 0.5;
		case POOR:
			return 0.1;
		default:
			return 1.0;
		}
	}

	
	int getConditionCode() {
		switch(this){
			case GEM_MINT:
				return 8;
			case MINT:
				return 7;
			case NEAR_MINT:
				return 6;
			case EXCELLENT:
				return 5;
			case VERY_GOOD:
				return 4;
			case GOOD:
				return 3;
			case FAIR:
				return 2;
			case POOR:
				return 1;
			default:
				return 0;
		}
	}
	
	public static TradingCardCondition getCondFromCode(String string) {
		switch(string) {
			case "8":
				return GEM_MINT;
			case "7":
				return MINT;
			case "6":
				return NEAR_MINT;
			case "5":
				return EXCELLENT;
			case "4":
				return VERY_GOOD;
			case "3":
				return GOOD;
			case "2":
				return FAIR;
			case "1":
				return POOR;
			default:
				return UNKNOWN;
		}
	}
}
