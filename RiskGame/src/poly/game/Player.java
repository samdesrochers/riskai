package poly.game;
import java.awt.Color;
import java.util.ArrayList;

public abstract class Player {
	/*
	 * Generic Player members
	 */
	
	public String name;
	
	public Color color;
	
	// List of all the occupied territories of this Player
	public ArrayList<Territory> occupiedTerritories;
	
	// List of all the territories of every players
	public ArrayList<Territory> allTerritories;
	
	// List of all the territories of every players
	public ArrayList<Card> cards;
	
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
	
	// Will the player attack this round
	public boolean willAttack;
	
	/*
	 * Generic Player functions
	 */
	public Player(String name){
		this.name = name;
		this.willAttack = false;
		this.attackingUnits = 0;
		this.cards = new ArrayList<Card>();
		this.occupiedTerritories = new ArrayList<Territory>();
	}
	
	// TO subsclass
	public void updateModel() {
		this.numberOfTerritories = this.occupiedTerritories.size() - 1;
	}
		
	// Picks a territory (initial round)
	// TO IMPLEMENT
	public abstract String chooseTerritory();
	
	// Assign a number of units (initial round)
	// TO IMPLEMENT
	public abstract int chooseNbOfUnits(int remainingThisTurn);
	
	// reinforce a territory (initial round)
	// TO IMPLEMENT
	public abstract String pickReinforceTerritory();
	
	
	// Assign part of remaining reinforcements (Normal round)
	// TO IMPLEMENT
	public abstract void assignReinforcements();
	
	//Functions doesn't validate if your moving your own soldiers
	//Return true if valid move, false if invalid
	public boolean moveSoldiers(Territory sourceTerritory,
			Territory destinationTerritory, int numbersToMove) {
		//Validation that its a legal move to an adjacent territory that is owned by the same owner
		boolean isAdjacent = false;
		for(Territory adjTerritory : sourceTerritory.adjacentTerritories){
			if(adjTerritory.name == destinationTerritory.name && adjTerritory.getOwner().name == destinationTerritory.getOwner().name){
				isAdjacent = true;
			}
		}
		//Verify that number of units to move is less than number available 
		if(!isAdjacent || !(sourceTerritory.getUnits() > numbersToMove)){
			return false;
		}
		sourceTerritory.removeUnits(numbersToMove);
		destinationTerritory.addUnits(numbersToMove);
		return true;
	}

	// Return true if the player will attack; decided by the AI (DO NOT RE-IMPLEMENT)
	public boolean prepareCombat() {		
		// Assign the territories
		chooseAttackerAndTarget();
		// Get the number of units to use for this round of attack
		if(this.attacker != null && this.target != null){
			chooseAttackingUnits();
			if(this.attackingUnits > 0 && this.attackingUnits <= this.attacker.getUnits()-1){
				// Attack will go on
				return true;
			} else {
				//System.out.println("Aborting combat preparation : bad units count");
			}
		} else {
			//System.out.println("Aborting combat preparation : no attacker or target territories");
		}
		// failed to prepare combat properly
		return false;
	}

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
	
	// Do not override
	public void combatAnalysis(int myLostUntis, int enemyLostUnits) {
		if(myLostUntis != 0 || enemyLostUnits  != 0){
			System.out.println(name+" lost "+myLostUntis+" units VS "+enemyLostUnits);
		}
		System.out.println();
		
		// Update our post combat model AI
		this.postCombatUpdateModel(myLostUntis, enemyLostUnits);
		
		int currentTerritoriesCount = occupiedTerritories.size() - 1;
		// Check if we got a new territory
		if (currentTerritoriesCount > numberOfTerritories && this.attacker != null && this.target != null){
			// Assign units to the new territory we got last round (refereed as target*)
			// Note : this could be any number as long as there is at least one remaining
			// unit on the attacking territory.
			this.didGainNewTerritory(this.target);
			System.out.println("New units moving from :" +this.attacker.name +" which has " + this.attacker.getUnits());
			System.out.println("New territory :"+target.name +" now has " + target.getUnits() + " units" );
		}
		numberOfTerritories = currentTerritoriesCount;
		
		// Forgets the attacker when done (critical)
		this.attacker = null;
	}
	
	// Update the AI model according to how many units were lost 
	// TO IMPLEMENT
	public abstract void postCombatUpdateModel(int myLostUntis, int enemyLostUnits);

	// Signifies the player when he won a new territory
	public abstract void didGainNewTerritory(Territory t);
	
	public boolean isAlive(){
		if(occupiedTerritories.size() == 0){
			return false;
		}
		return true;
	}
	
	public abstract ArrayList<Card> tradeCards();
	
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
