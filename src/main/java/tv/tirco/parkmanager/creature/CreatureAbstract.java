package tv.tirco.parkmanager.creature;

import java.util.UUID;

import org.bukkit.event.Listener;

public abstract class CreatureAbstract extends CreatureRunnable implements Listener{

	//These are values all the creatues must have.
	//
	
	//protected AbstractNMSEntity entity;
	String name;
	CreatureType type;

	UUID owner;
	long creatureID;
	
	boolean Active;
	boolean inBattle;
	
	//Updateables
	double health;
	double maxHealth;
	
	int level;
	double exp;
	int tier;
	
	double mana;
	double maxMana;
	
	double attackDamage;
	double attackSpeed;
	
	int breedingCount;
	int breedingProficiencySpeed;
	int breedingProficiencyHealth;
	int breedingProficiencyAttack;
	
	int gender; //0 female, 1 male;
	
	//Attack
	//Defend
	
	//CreatureAbility abilityOne;
	//CreatureAbility abilityTwo;
	//CreatureAbility abilityThree;
	//CreatureAbility abilityFour;
	//CreatureAbility abilityFive;
	
	public CreatureAbstract(UUID owner, long creatureID) {
		this.owner = owner;
		this.creatureID = creatureID;
	}
	
    protected abstract void onEquip();
    
    protected abstract void onUnequip();
	
    public void equip(boolean b) {
    	
    }
    
    public void unequip(boolean b) {
    	
    }
    
	public void onAttack(CreatureAbstract target) {
		target.onGetHit(this);
	}
	
	public void onGetHit(CreatureAbstract attacker) {
		
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	protected CreatureType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	protected void setType(CreatureType type) {
		this.type = type;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the owner
	 */
	public UUID getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	/**
	 * @return the creatureID
	 */
	public long getCreatureID() {
		return creatureID;
	}

	/**
	 * @param creatureID the creatureID to set
	 */
	public void setCreatureID(long creatureID) {
		this.creatureID = creatureID;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return Active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		Active = active;
	}

	/**
	 * @return the inBattle
	 */
	public boolean isInBattle() {
		return inBattle;
	}

	/**
	 * @param inBattle the inBattle to set
	 */
	public void setInBattle(boolean inBattle) {
		this.inBattle = inBattle;
	}

	/**
	 * @return the health
	 */
	public double getHealth() {
		return health;
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(double health) {
		this.health = health;
	}

	/**
	 * @return the maxHealth
	 */
	public double getMaxHealth() {
		return maxHealth;
	}

	/**
	 * @param maxHealth the maxHealth to set
	 */
	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the exp
	 */
	public double getExp() {
		return exp;
	}

	/**
	 * @param exp the exp to set
	 */
	public void setExp(double exp) {
		this.exp = exp;
	}

	/**
	 * @return the tier
	 */
	public int getTier() {
		return tier;
	}

	/**
	 * @param tier the tier to set
	 */
	public void setTier(int tier) {
		this.tier = tier;
	}

	/**
	 * @return the mana
	 */
	public double getMana() {
		return mana;
	}

	/**
	 * @param mana the mana to set
	 */
	public void setMana(double mana) {
		this.mana = mana;
	}

	/**
	 * @return the maxMana
	 */
	public double getMaxMana() {
		return maxMana;
	}

	/**
	 * @param maxMana the maxMana to set
	 */
	public void setMaxMana(double maxMana) {
		this.maxMana = maxMana;
	}

	/**
	 * @return the attackDamage
	 */
	public double getAttackDamage() {
		return attackDamage;
	}

	/**
	 * @param attackDamage the attackDamage to set
	 */
	public void setAttackDamage(double attackDamage) {
		this.attackDamage = attackDamage;
	}

	/**
	 * @return the attackSpeed
	 */
	public double getAttackSpeed() {
		return attackSpeed;
	}

	/**
	 * @param attackSpeed the attackSpeed to set
	 */
	public void setAttackSpeed(double attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	/**
	 * @return the breedingCount
	 */
	public int getBreedingCount() {
		return breedingCount;
	}

	/**
	 * @param breedingCount the breedingCount to set
	 */
	public void setBreedingCount(int breedingCount) {
		this.breedingCount = breedingCount;
	}

	/**
	 * @return the breedingProficiencySpeed
	 */
	public int getBreedingProficiencySpeed() {
		return breedingProficiencySpeed;
	}

	/**
	 * @param breedingProficiencySpeed the breedingProficiencySpeed to set
	 */
	public void setBreedingProficiencySpeed(int breedingProficiencySpeed) {
		this.breedingProficiencySpeed = breedingProficiencySpeed;
	}

	/**
	 * @return the breedingProficiencyHealth
	 */
	public int getBreedingProficiencyHealth() {
		return breedingProficiencyHealth;
	}

	/**
	 * @param breedingProficiencyHealth the breedingProficiencyHealth to set
	 */
	public void setBreedingProficiencyHealth(int breedingProficiencyHealth) {
		this.breedingProficiencyHealth = breedingProficiencyHealth;
	}

	/**
	 * @return the breedingProficiencyAttack
	 */
	public int getBreedingProficiencyAttack() {
		return breedingProficiencyAttack;
	}

	/**
	 * @param breedingProficiencyAttack the breedingProficiencyAttack to set
	 */
	public void setBreedingProficiencyAttack(int breedingProficiencyAttack) {
		this.breedingProficiencyAttack = breedingProficiencyAttack;
	}

	/**
	 * @return the gender
	 */
	public int getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(int gender) {
		this.gender = gender;
	}

}
