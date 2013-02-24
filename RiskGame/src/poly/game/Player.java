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
	
	// reinforce a territory
	public abstract String reinforceTerritory();


	
	
	public boolean isAlive(){
		if(occupiedTerritories.size() == 0){
			return false;
		}
		return true;
		
	}
	
}
