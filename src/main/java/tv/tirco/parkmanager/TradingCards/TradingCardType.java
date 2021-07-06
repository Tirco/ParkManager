package tv.tirco.parkmanager.TradingCards;

public enum TradingCardType {
	PLAYER,
	STAFFMEMBER,
	ATTRACTION,
	ANIMAL,
	MONSTER,
	ITEM,
	BLOCK,
	UNKNOWN,
	SPECIAL;

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
		case SPECIAL:
			return "&aSpecial";
		case UNKNOWN:
		default:
			return "&fUnknown";
		}
	}
}
