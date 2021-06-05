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
	
	

	String getAsString() {
		switch(this){
		case GEM_MINT:
			return "Gem Mint";
		case MINT:
			return "Mint";
		case NEAR_MINT:
			return "Near Mint";
		case EXCELLENT:
			return "Excellent";
		case VERY_GOOD:
			return "Very Good";
		case GOOD:
			return "Good";
		case FAIR:
			return "Fair";
		case POOR:
			return "Poor";
		default:
			return "Unknown";
		}
	}
	
	double getValueDouble() {
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
}
