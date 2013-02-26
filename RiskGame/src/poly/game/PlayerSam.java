package poly.game;

import java.util.Random;

public class PlayerSam extends Player {

	private Random ran;
	public PlayerSam(String name) {
		super(name);
		ran = new Random();
		this.attackingUnits = 0;
	}

	@Override
	public void updateModel() {
		numberOfTerritories = occupiedTerritories.size() - 1;
	}

	@Override
	public String chooseTerritory() {
		for(Territory t : allTerritories){
			if(!t.isOccupied){
				return t.name;
			}
		}
		return "VICTORY (or problem)";
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

	// Analyze what happened after a combat round (AI)
	@Override
	public void combatAnalysis(int myLostUntis, int enemyLostUnits) {
		if(myLostUntis != 0 || enemyLostUnits  != 0){
			System.out.println(name+" lost "+myLostUntis+" units VS "+enemyLostUnits);
		}
		System.out.println();
		
		int currentTerritoriesCount = occupiedTerritories.size() - 1;
		// Check if we got a new territory
		if (currentTerritoriesCount > numberOfTerritories && this.attacker != null && this.target != null){
			//Assign units to the new territory we got last round (refereed as target*)
			// Note : this could be any number as long as there is at least one remaining
			// unit on the attacking territory.
			this.target.addUnits(this.attacker.getUnits() -1);
			System.out.println("New units originating from :" +this.attacker.name +" which has " + this.attacker.getUnits());
			System.out.println("New territory :"+target.name +" now has " + target.getUnits() + " units" );
		}
		
		numberOfTerritories = currentTerritoriesCount;
		this.attacker = null;
	}

	// Return true if the player will attack; decided by the AI 
	@Override // (SHOULDN'T NEED TO REIMPLEMENT)
	public boolean isAttacking() {		
		// Assign the territories
		chooseAttackerAndTarget();

		// Get the number of units to use for this round of attack
		if(this.attacker != null && this.target != null){
			chooseAttackingUnits();
			if(this.attackingUnits > 0){
				// Attack will go on
				return true;
			}
		}
		// Won't attack, ends the attack phase for the player
		return false;
	}

	// Decides which territory to attack from and what territory to attack.  MUST be adjacent :)
	@Override
	public void chooseAttackerAndTarget() {
		int numberTerritoriesChecked = 0;
		while(numberTerritoriesChecked < occupiedTerritories.size()){

			// try a random territory in what we occupy
			int rt = ran.nextInt(occupiedTerritories.size());
			Territory attacker = occupiedTerritories.get(rt);

			// Check all adjacent territories and try to find an enemy
			for(Territory target : attacker.adjacentTerritories){
				if(target.getOwner().name != attacker.getOwner().name && attacker.getUnits() > 1){
					this.target = target;
					this.attacker = attacker;
				}
			}
			// Go to next territory if not possible for current territory
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
			this.attackingUnits = 0; // Attack cancelled
		}
	}
}
