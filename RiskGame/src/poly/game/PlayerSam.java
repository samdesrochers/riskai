package poly.game;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	// REINFORCEMENT. Trade 3 cards to get bonus units
	// Must be all of the same type, or one of each (3) types
	// Return null if you don't want to trade cards
	public ArrayList<Card> tradeCards(){
		
		if(this.cards.size() >= 3){
			ArrayList<Card> inf_cards = new ArrayList<Card>();
			ArrayList<Card> cav_cards = new ArrayList<Card>();
			ArrayList<Card> art_cards = new ArrayList<Card>();
			
			for(int i = 0; i < this.cards.size(); i++){
				Card card = this.cards.get(i);
				if(card.type == Card.TYPE_INFANTRY){
					inf_cards.add(card);
				} else if(card.type == Card.TYPE_CAVALRY){
					cav_cards.add(card);
				} else if(card.type == Card.TYPE_ARTILERY){
					art_cards.add(card);
				} 
			}
			if(inf_cards.size() == 3){
				return inf_cards;
			} else if(cav_cards.size() == 3){
				return cav_cards;
			} else if(art_cards.size() == 3){
				return art_cards;
			} 
		}
		return null;
	}
	
	// REINFORCEMENT Phase, assign reinforcements as we want
	// Make sure [this.remainingUnits] reaches 0 here
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
	 *  To perform an attack, a player must : 
	 *  1. Set [this.willAttack] to true
	 *  2. Choose an attacking and target territories [this.attacker] & [this.target]
	 *  3. Choose how many units to send, picked from this.attacker and set to [this.attackingUnits]
	 *  * If any of the above aren't filled, the attack will abort.
	 *  * All (I hope so) values will be checked for integrity ( != null or 0 )
	 * 
	 ******************************************************/
	
	// Only called before preparing a combat round
	public void updateModel() {
		super.updateModel();
		
		Map.checkIfContinentOwned(Map.AFRICA, this.occupiedTerritories);
		Map.checkIfContinentOwned(Map.NORTH_AMERICA, this.occupiedTerritories);
		Map.checkIfContinentOwned(Map.EUROPE, this.occupiedTerritories);
		Map.checkIfContinentOwned(Map.ASIA, this.occupiedTerritories);
		Map.checkIfContinentOwned(Map.AUSTRALIA, this.occupiedTerritories);
		Map.checkIfContinentOwned(Map.SOUTH_AMERICA, this.occupiedTerritories);
		
		if(ran.nextInt(100) > 1){
			this.willAttack = true;
		} else {
			this.willAttack = false;
		}
	}

	// Decides which territory to attack from and what territory to attack.  MUST be adjacent :)
	public void chooseAttackerAndTarget() {
		int numberTerritoriesChecked = 0;
		
		while(numberTerritoriesChecked < occupiedTerritories.size()){

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
