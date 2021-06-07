package tv.tirco.parkmanager.TradingCards;

public enum TradingCardType {
	PLAYER,
	STAFFMEMBER,
	ATTRACTION,
	ANIMAL,
	MONSTER,
	ITEM,
	BLOCK,
	UNKNOWN;

	String getString() {
		switch(this) {
		case PLAYER:
			return "&fPlayer";
		case STAFFMEMBER:
			return "&6Staff Member";
		case ATTRACTION:
			return "&dAttraction";
		case ANIMAL:
			return "&eAnimal";
		case MONSTER:
			return "&cMonster";
		case ITEM:
			return "&fItem";
		case BLOCK:
			return "&fBlock";
		case UNKNOWN:
		default:
			return "&fUnknown";
		}
	}
}
