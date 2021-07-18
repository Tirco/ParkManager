package tv.tirco.parkmanager.creature;

import org.bukkit.inventory.ItemStack;

public abstract class CreatureAbility {


	ItemStack icon;
	
	double manaCost;
	
	public CreatureAbility() {}

	public boolean checkMana(CreatureAbstract caster) {
		return caster.getMana() >= manaCost;
	}
	
	public void takeManaCost(CreatureAbstract caster) {
		caster.setMana(caster.getMana() - manaCost);
	}
	
	public boolean onExecute(CreatureAbstract attacker, CreatureAbstract target) {
		return false;
	};
	
	public boolean onExecute(CreatureAbstract target) {
		return false;
	};

	/**
	 * @return the manaCost
	 */
	protected double getManaCost() {
		return manaCost;
	}

	/**
	 * @param manaCost the manaCost to set
	 */
	protected void setManaCost(double manaCost) {
		this.manaCost = manaCost;
	}
}
