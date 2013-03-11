package poly.game;

import java.util.ArrayList;
import java.util.Random;

public class SamAI extends Player{

	private Random ran;
	
	public SamAI(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		ran = new Random();
	}

	/*******************************************************
	 * 
	 *  DEPLOYEMENT PHASE METHODS
	 * 
	 ******************************************************/
	@Override
	// Choose the territory you want for this turn of territories selection
	// Simply return an empty territory from the map.  Actual territories 
	// and their status can be found in [this.public_allTerritories].
	public String chooseTerritory() {
		// TODO Auto-generated method stub
		return null;
	}

	// Choose a number of units to place on a territory during
	// the initial deployment phase. Must use between 1 and 3 units
	@Override
	public int chooseNbOfUnits(int remainingThisTurn) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// Choose a territory to reinforce with 1 to 3 units
	// Returns the territory to reinforce
	@Override
	public String pickReinforceTerritory() {
		// TODO Auto-generated method stub
		return null;
	}

	/*******************************************************
	 * 
	 *  REINFORCEMENT PHASE METHODS (Pre - Combat)
	 *  Assign 3 cards to be turned in for bonus units if you wish
	 *  After receiving your units at the beginning of a turn,
	 *  place all of them as you wish by selecting one of
	 *  your territories and adding the new units (addUnits)
	 * 
	 ******************************************************/
	
	@Override
	// Make sure [this.remainingUnits] reaches 0 here by assigning
	// all of your received units to a territory you own in [this.myOccupiedTerritories]
	public void assignReinforcements() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Card> tradeCards() {
		// TODO Auto-generated method stub
		return null;
	}

	/*******************************************************
	 * 
	 *  COMBAT PHASE METHODS
	 *  
	 *  To perform an attack, a player must : 
	 *  1. Set [this.willAttack] to true
	 *  2. Choose an attacking and target territories [this.attacker] & [this.target]
	 *  3. Choose how many units to send, picked from this.attacker and set to [this.attackingUnits]
	 *  * If any of the above aren't filled, the attack will abort.
	 *  * All (I hope so) values will be checked for integrity ( != null or 0 )
	 * 
	 ******************************************************/
	
	// Decides to attack or not here
	public void updateModel() {
		super.updateModel();
		
		Map.checkIfContinentOwned(Map.AFRICA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.NORTH_AMERICA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.EUROPE, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.ASIA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.AUSTRALIA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.SOUTH_AMERICA, this.myOccupiedTerritories);
		
		if(ran.nextInt(100) > 1){
			this.willAttack = true;
		} else {
			this.willAttack = false;
		}
	}
	
	@Override
	protected void chooseAttackerAndTarget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void chooseAttackingUnits() {
		// TODO Auto-generated method stub
		
	}

	// Analyze combat outcome, if required
	@Override
	public void postCombatUpdateModel(int myLostUntis, int enemyLostUnits) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didGainNewTerritory(Territory t) {
		// TODO Auto-generated method stub
		
	}

	/*******************************************************
	 * 
	 *  MOVEMENT PHASE METHODS
	 *  Set [this.moveOrigin] , [this.moveDestination] , [this.moveUnits]
	 *  when you want to move units from one territory to another 
	 *  (only once at the end of your turn)
	 * 
	 ******************************************************/
	@Override
	public void chooseMovementTerritoriesAndUnits() {
		// TODO Auto-generated method stub
		
	}

}
