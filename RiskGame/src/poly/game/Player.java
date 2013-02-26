package poly.game;
import java.util.ArrayList;

public abstract class Player {
	/*
	 * Generic Player members
	 */
	
	public String name;
	
	// List of all the occupied territories of this Player
	public ArrayList<Territory> occupiedTerritories;
	
	// List of all the territories of every players
	public ArrayList<Territory> allTerritories;
	
	// Number of units available for one turn
	public int remainingUnits;
	
	// Territory from which we are attacking
	protected Territory attacker;
	
	// Territory we are attacking
	protected Territory target;
	
	// Number of units we want to attack with (MAX 3 per round)
	protected int attackingUnits;
	
	// Number of territories owned
	public int numberOfTerritories;
	
	
	/*
	 * Generic Player functions
	 */
	public Player(String name){
		this.name = name;
	}
	
	// TO IMPLEMENT
	public abstract void updateModel();
		
	// Picks a territory (initial round)
	// TO IMPLEMENT
	public abstract String chooseTerritory();
	
	// Assign a number of units (initial round)
	// TO IMPLEMENT
	public abstract int chooseNbOfUnits(int remainingThisTurn);
	
	// reinforce a territory (initial round)
	// TO IMPLEMENT
	public abstract String pickReinforceTerritory();
	
	// Assign part of remaining reinforcements 
	// TO IMPLEMENT
	public abstract void assignReinforcements();
	
	// Return true if the player will attack; decided by the AI (DO NOT RE-IMPLEMENT)
	public abstract boolean isAttacking();

	// Getters and setters for the automatic combat process 
	public Territory getAttackingTerritory() {
		return this.attacker;
	}

	public Territory getTargetTerritory() {
		return this.target;
	}

	public int getNbOfAttackingUnits() {
		return this.attackingUnits;
	}
	
	// Internal decision functions, picking an attacking territory, a target, and how many units to send
	// TO IMPLEMENT
	protected abstract void chooseAttackerAndTarget();
	
	// TO IMPLEMENT
	protected abstract void chooseAttackingUnits();
	
	// TO IMPLEMENT
	protected abstract void combatAnalysis(int myLostUntis, int enemyLostUnits);

	public boolean isAlive(){
		if(occupiedTerritories.size() == 0){
			return false;
		}
		return true;
	}
	
	// Gets the total number of units of our player
	protected int countUnits(){
		int units = 0;
		for( Territory t : occupiedTerritories){
			units += t.getUnits();
		}
		return units;
		
	}
	
	public void printTerritories(){
		System.out.println("--------- " + this.name + " Territories ---------");
		for(Territory t : occupiedTerritories){
			System.out.println(t.name + " with " + t.getUnits() + " units");
		}
		System.out.println();
	}
	
}
