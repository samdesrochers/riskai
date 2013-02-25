package poly.game;
import java.util.ArrayList;

public abstract class Player {
	
	public String name;
	
	// List of all the occupied territories of this Player
	public ArrayList<Territory> occupiedTerritories;
	
	// List of all the territories of every players
	public ArrayList<Territory> allTerritories;
	
	// Number of units available for one turn
	public int remainingUnits;
	
	public Player(String name){
		this.name = name;
	}
	
	public abstract void updateModel();
		
	// Picks a territory (initial round)
	public abstract String chooseTerritory();
	
	// Assign a number of units (initial round)
	public abstract int chooseNbOfUnits(int remainingThisTurn);
	
	// reinforce a territory (initial round)
	public abstract String pickReinforceTerritory();
	
	// Assign part of remaining reinforcements 
	public abstract void assignReinforcements();
	
	// Return true if the player will attack 
	public abstract boolean isAttacking();

	// Getters and setters
	public abstract Territory getAttackingTerritory();
	
	public abstract Territory getTargetTerritory();
	
	public abstract int getNbOfAttackingUnits();
	
	// Internal decision functions, picking an attacking territory, a target, and how many units to send
	protected abstract void chooseAttackerAndTarget();
	
	protected abstract void chooseAttackingUnits();
	
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
