package poly.game;

import java.util.Random;

public class PlayerSam extends Player {

	private Random ran;
	public PlayerSam(String name) {
		super(name);
		ran = new Random();
	}

	/*******************************************************
	 * 
	 *  DEPLOYEMENT PHASE METHODS
	 * 
	 ******************************************************/
	
	// Picks the next non occupied territory (TO IMPLEMENT)
	public String chooseTerritory() {
		for(Territory t : allTerritories){
			if(!t.isOccupied){
				return t.name;
			}
		}
		return "error";
	}

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

	/*******************************************************
	 * 
	 *  REINFORCEMENT PHASE METHODS
	 * 
	 ******************************************************/
	
	// REINFORCEMENT Phase, assign between 1 and 3 reinforcements
	public void assignReinforcements() {
		//random territory
		int rt = ran.nextInt(occupiedTerritories.size());

		// random nb of units
		int ru = ran.nextInt(this.remainingUnits) + 1;

		// assign random nb of units on the random territory
		Territory pick = occupiedTerritories.get(rt);
		pick.addUnits(ru);

		this.remainingUnits -= ru;
	}

	/*******************************************************
	 * 
	 *  COMBAT PHASE METHODS
	 *  
	 *  To perfom an attack, a player must : 
	 *  1. Set [this.willAttack] to true
	 *  1. Choose an attacking and target territories [this.attacker] & [this.target]
	 *  2. Choose how many units to send, picked from this.attacker and set to [this.attackingUnits]
	 *  * All (I hope so) values will be checked for integrity ( != null or 0 )
	 * 
	 ******************************************************/
	
	// Only called before preparing a combat round
	public void updateModel() {
		super.updateModel();
		
		if(ran.nextInt(25) > 1){
			this.willAttack = true;
		} else {
			this.willAttack = false;
		}
	}

	// Decides which territory to attack from and what territory to attack.  MUST be adjacent :)
	public void chooseAttackerAndTarget() {
		int numberTerritoriesChecked = 0;
		
		while(numberTerritoriesChecked < occupiedTerritories.size() - 1){

			// try a random territory in what we occupy
			int rt = ran.nextInt(occupiedTerritories.size());
			Territory attacker = occupiedTerritories.get(rt);

			// Check all adjacent territories and try to find an enemy
			for(int i = 0; i < attacker.adjacentTerritories.size(); i++){
					Territory t = attacker.adjacentTerritories.get(i);
				if(t.getOwner().name != attacker.getOwner().name && attacker.getUnits() > 1){
					this.target = t; // Bug aux pays frontieres en raison du 1 unit
					this.attacker = attacker;
				}
			}
			// Go to next territory if not possible for current territory
			numberTerritoriesChecked ++;
		}
	}

	// Decides how many units to send for this round (MAX is 3, minimum is 1, 0 cancels the attack)
	public void chooseAttackingUnits() {
		// Attack with full capacity without leaving the territory empty
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
	
	// TO IMPLEMENT (AI) : called when finishing a combat round
	public void postCombatUpdateModel(int myLostUntis, int enemyLostUnits) {
		
	}

	// TO IMPLEMENT (AI) : called when our player wins a new territory (which was the targeted territory)
	public void didGainNewTerritory(Territory conqueredTerritory) {
		// Add all units from the attacking territory to the new we just conquered
		// (MUST leave at least one on the territory we attacked with)
		conqueredTerritory.setUnits(this.attacker.getUnits() -1);
		this.attacker.setUnits(1);
	}
}
