package tv.tirco.parkmanager.creature.creatures;

import java.util.UUID;

import tv.tirco.parkmanager.creature.CreatureAbstract;
import tv.tirco.parkmanager.creature.CreatureType;

public class DragonCreature extends CreatureAbstract{

	public DragonCreature(UUID owner, long creatureID) {
		super(owner, creatureID);
		setType(CreatureType.DRAGON);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onEquip() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onUnequip() {
		// TODO Auto-generated method stub
		
	}

}
