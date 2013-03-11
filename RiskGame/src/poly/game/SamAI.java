package poly.game;

import java.util.ArrayList;

public class SamAI extends Player{

	public SamAI(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/*******************************************************
	 * 
	 *  DEPLOYEMENT PHASE METHODS
	 * 
	 ******************************************************/
	@Override
	public String chooseTerritory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int chooseNbOfUnits(int remainingThisTurn) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*******************************************************
	 * 
	 *  REINFORCEMENT PHASE METHODS (Pre - Combat)
	 *  Assign 3 cards to be turned in for bonus units if you wish
	 *  After recieveing your units at the beginning of a turn,
	 *  place all of them as you wish by selecting one of
	 *  your territories and adding the new units (addUnits)
	 * 
	 ******************************************************/
	@Override
	public String pickReinforceTerritory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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
	@Override
	protected void chooseAttackerAndTarget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void chooseAttackingUnits() {
		// TODO Auto-generated method stub
		
	}

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
	 * 
	 ******************************************************/
	@Override
	public void chooseMovementTerritoriesAndUnits() {
		// TODO Auto-generated method stub
		
	}

}
