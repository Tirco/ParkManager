package tv.tirco.parkmanager.creature.abilities;

import tv.tirco.parkmanager.creature.CreatureAbility;
import tv.tirco.parkmanager.creature.CreatureAbstract;

public class HealSelf extends CreatureAbility {

	
//	public HealSelf() {
//		setManaCost(10);
//		setMat(Material.PAPER);
//		setName("Heal Self");
//		setLore(null);
//		setModeldata(1);
//	}

	CreatureAbstract caster;
	
	@Override
	public boolean onExecute(CreatureAbstract target) {
		if(!checkMana(caster)) {
			return false;
		}
		takeManaCost(caster);
		target.setHealth(1);
		return true;
	}
}
