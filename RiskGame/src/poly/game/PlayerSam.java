package poly.game;

import java.util.Random;

public class PlayerSam extends Player {

	private Random ran;
	
	// Territory from which we are attacking
	protected Territory attacker;
	
	// Territory we are attacking
	private Territory target;
	
	// Number of units we want to attack with (MAX 3 per round)
	private int attackingUnits;
	
	public PlayerSam(String name) {
		super(name);
		ran = new Random();
		attackingUnits = 0;
	}

	@Override
	public void updateModel() {

	}

	@Override
	public String chooseTerritory() {
		for(Territory t : allTerritories){
			if(!t.isOccupied){
				return t.name;
			}
		}
		return "problem";
	}

	@Override
	public int chooseNbOfUnits(int remainingThisTurn) {
		// Random nb of units between 1 and the remaining units placable this turn (3 being the max)
		int r = ran.nextInt(remainingThisTurn) + 1;
		return r;
	}

	public String pickReinforceTerritory() {
		int r = ran.nextInt(occupiedTerritories.size());
		Territory pick = occupiedTerritories.get(r);
		
		return pick.name;
	}

	public void assignReinforcements() {
		//random territory
		int rt = ran.nextInt(occupiedTerritories.size());
		
		// random nb of units
		int ru = ran.nextInt(this.remainingUnits) + 1;

		// assign random nb of units on the random territory
		Territory pick = occupiedTerritories.get(rt);
		pick.setUnits(ru);
		
		this.remainingUnits -= ru;
	}

	// Takes the decision of attacking or not in this function
	public boolean isAttacking() {
		
		chooseAttackerAndTarget();
		chooseAttackingUnits();
		if(this.attacker != null && this.attackingUnits > 0){
			return true;
		}
		return false;
	}

	// Getters and setters for the automatic combat process 
	@Override
	public Territory getAttackingTerritory() {
		return this.attacker;
	}

	@Override
	public Territory getTargetTerritory() {
		return this.target;
	}

	@Override
	public int getNbOfAttackingUnits() {
		return this.attackingUnits;
	}

	// Analyze what happened after a combat round (AI)
	@Override
	public void combatAnalysis(int myLostUntis, int enemyLostUnits) {
		// TODO Auto-generated method stub
		System.out.println("I ("+name+") lost "+myLostUntis+" units VS "+enemyLostUnits);
		this.attacker = null;
	}

	// Decides which territory to attack from and what territory to attack.  MUST be adjacents :)
	@Override
	public void chooseAttackerAndTarget() {
		int numberTerritoriesChecked = 0;
		while(numberTerritoriesChecked < occupiedTerritories.size()){
			
			// try a random territory
			int rt = ran.nextInt(occupiedTerritories.size());
			Territory attacker = occupiedTerritories.get(rt);
			
			// Check all adjacent territories and try to find an enemy
			for(Territory t : attacker.adjacentTerritories){
				if(t.getOwner().name != attacker.getOwner().name){
					this.target = t;
					this.attacker = attacker;
				}
			}
			// Go to next territory if not
			numberTerritoriesChecked ++;
		}
	}

	// Decides how many units to send for this round (MAX is 3, minimum is 1, 0 cancels the attack)
	@Override
	public void chooseAttackingUnits() {
		// Attack without leaving the territory empty
		if(this.attacker.getUnits() >= 4 ){
			this.attackingUnits = 3;
		} else if(this.attacker.getUnits() >= 3 ){
			this.attackingUnits = 2;
		} else if(this.attacker.getUnits() >= 2 ){
			this.attackingUnits = 1;
		} else {
			this.attackingUnits = 0;
		}
	}
}
