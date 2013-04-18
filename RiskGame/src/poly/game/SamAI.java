package poly.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

public class SamAI extends Player{

	//private Random ran;
	private int state;

	// List of all territories and members associated
	private HashMap<String, Integer> allTerritoriesValues;
	private HashMap<String, Integer> myTerritoriesValues;
	private HashMap<String, Integer> moveTerritoriesValues;

	private HashMap<String, Integer> initialHeuristicValues;
	private HashMap<String, Integer> playersRanking;


	private int territoryPickCounter;

	// Attack members
	private boolean didRecieveCardThisTurn = false;
	private int nbOfAttacksTried = 0;

	public SamAI(String name) {
		super(name);
		//ran = new Random();
		allTerritoriesValues = 		new HashMap<String, Integer>();
		myTerritoriesValues =	 	new HashMap<String, Integer>();
		initialHeuristicValues = 	new HashMap<String, Integer>();
		moveTerritoriesValues = 	new HashMap<String, Integer>();
		playersRanking = 			new HashMap<String, Integer>();
		
		lastMoveOriginTerritory = 		new Territory("none", "blank", false);
		lastMoveDestinationTerritory = 	new Territory("none", "blank", false);

		territoryPickCounter = 0;
		state = STATE_DEFENSIVE;
	}

	/*******************************************************
	 * 
	 *  DEPLOYEMENT PHASE METHODS
	 * 
	 ******************************************************/
	@Override
	// Choose the territory you want for this turn of territories selection
	// Simply return an empty territory name from the map.  Actual territories 
	// and their status can be found in [this.public_allTerritories].
	public String chooseTerritory() {

		// First pass, assign heuristic values
		if(this.myOccupiedTerritories.size() == 0){

			// Assign initial values
			this.assignInitialValuesToAllTerritories();
		} else {	
			// Update current values
			this.deploy_updateUtilityValues();
		}

		// Try and pick the most useful territory
		Territory pick = deploy_pickMostValuableTerritory();

		return pick.name;
	}

	// Choose a number of units to place on a territory during
	// the initial deployment phase. Must use between 1 and 3 units
	@Override
	public int chooseNbOfUnits(int remainingThisTurn) {
		return remainingThisTurn; // Always place max units
	}

	// Choose a territory to reinforce with 1 to 3 units
	// Returns the territory to reinforce
	@Override
	public String pickReinforceTerritory() {

		// Update values before choosing
		deploy_updateUtilityValues();

		// nb of optimized territories, still gives normalized distribution on our "x" best territories
		int secretOptimization = 3;

		// Pick next territory
		Territory turnPick = reinforce_getNextTerritoryToReinforce(territoryPickCounter);
		territoryPickCounter = (territoryPickCounter + 1)%secretOptimization;

		return turnPick.name;
	}

	/*******************************************************
	 * 
	 *  REINFORCEMENT PHASE METHODS (Pre - Combat)
	 *  Assign 3 cards to be returned for bonus units if you wish
	 *  After receiving your units at the beginning of a turn,
	 *  place all of them as you wish by selecting one of
	 *  your territories and adding the new units (addUnits)
	 * 
	 ******************************************************/

	@Override
	// Make sure [this.remainingUnits] reaches 0 here by assigning
	// all of your received units to a territory you own in [this.myOccupiedTerritories]
	public void assignReinforcements() {

		// Reset some values
		turnAttackThreshold = ATTACK_THRESHOLD;
		
		// Check if we are in early game; if so, attack less
		turnAttackThreshold = (nbTurnsPlayed < EARLY_GAME_COUNTER) ? turnAttackThreshold - 4 : turnAttackThreshold;

		// Updates the values and sorts the territories
		reinforce_updateUtilityValues();

		int nbTerritories = myOccupiedTerritories.size();
		territoryPickCounter = 0;

		while(nbTerritories > 0 && this.remainingUnits > 0){
			Territory turnPick = reinforce_getNextTerritoryToReinforce(territoryPickCounter);
			if(turnPick != null && this.canAttackOtherTerritory(turnPick)){
				
				int unitsToPlace = this.remainingUnits;
				turnPick.addUnits(unitsToPlace);
				this.remainingUnits -= unitsToPlace;
				nbTerritories --;

				System.out.println("Sam placed "+ unitsToPlace+ " units on: "+turnPick.name);
				territoryPickCounter ++;
			} else {
				territoryPickCounter ++;
				nbTerritories --;
			}
		}
		
		// Evaluate the players ranking to decide the playstyle
		evaluatePlayersRank();
		turnAttackThreshold = (state == STATE_OFFENSIVE) ? turnAttackThreshold + OFFENSE_BONUS_THRESHOLD :
														   turnAttackThreshold - DEFENSE_PENALTY_THRESHOLD;
	}

	@Override // Trade in cards trio whenever they are available
	public ArrayList<Card> tradeCards() {
		if(this.cards.size() >= 3){
			ArrayList<Card> inf_cards = new ArrayList<Card>();
			ArrayList<Card> cav_cards = new ArrayList<Card>();
			ArrayList<Card> art_cards = new ArrayList<Card>();
			ArrayList<Card> tri_cards = new ArrayList<Card>();

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
			
			if(art_cards.size() > 0 && cav_cards.size() > 0 && inf_cards.size() > 0){
				tri_cards.add(art_cards.get(0));
				tri_cards.add(cav_cards.get(0));
				tri_cards.add(inf_cards.get(0));
				return tri_cards;
			} else if(art_cards.size() >= 3){
				return art_cards;
			} else if(cav_cards.size() >= 3){
				return cav_cards;
			} else if(inf_cards.size() >= 3){
				return inf_cards;
			} 
		}
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

		// Check if any continent is owned before staring turn
		if(Map.checkIfContinentOwned(Map.AFRICA, this.myOccupiedTerritories)){
			CONTINENT_NA_UTILITY_VALUE = owned_af;
		}
		if(Map.checkIfContinentOwned(Map.NORTH_AMERICA, this.myOccupiedTerritories)){
			CONTINENT_NA_UTILITY_VALUE = owned_na;
		}
		if(Map.checkIfContinentOwned(Map.EUROPE, this.myOccupiedTerritories)){
			CONTINENT_NA_UTILITY_VALUE = owned_eu;
		}
		if(Map.checkIfContinentOwned(Map.ASIA, this.myOccupiedTerritories)){
			CONTINENT_NA_UTILITY_VALUE = owned_as;
		}
		if(Map.checkIfContinentOwned(Map.AUSTRALIA, this.myOccupiedTerritories)){
			CONTINENT_NA_UTILITY_VALUE = owned_au;
		}
		if(Map.checkIfContinentOwned(Map.SOUTH_AMERICA, this.myOccupiedTerritories)){
			CONTINENT_NA_UTILITY_VALUE = owned_sa;
		}

		// Update values 
		combat_updateUtilityValues();

		// Combat decision
		if(nbOfAttacksTried < turnAttackThreshold){
			this.willAttack = true;
			nbOfAttacksTried ++;
		} else {
			this.willAttack = false;
		}
	}

	@Override
	protected void chooseAttackerAndTarget() {

		// Custom implementation
		pickMostPowerfulAttackerAndBestTarget();
	}

	@Override
	protected void chooseAttackingUnits() {
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

	// Analyze combat outcome
	@Override
	public void postCombatUpdateModel(int myLostUntis, int enemyLostUnits) {

		// Takes differential from last fight and update the implicated territories values
		int lastAttackerValue = allTerritoriesValues.get(this.attacker.name);
		int lastTargetValue = allTerritoriesValues.get(this.target.name);
		int differential = 2*(enemyLostUnits - myLostUntis);

		if(differential == 2){
			turnAttackThreshold += 2; // Get cocky if total victory
		} else if (differential == 1) {
			Random ran = new Random();
			int val = (ran.nextFloat() > 0.63) ? 1 : 0; // 0.63% chance of winning when attacking 3 Vs 2 (best odds possible)
			turnAttackThreshold += val;
		} 

		// Slow down if you already have won a card
		if(this.didRecieveCardThisTurn){
			turnAttackThreshold --;
		}
		
		// Good odds of victory on next attack
		if(this.attacker.getUnits() > 2*this.target.getUnits()){
			turnAttackThreshold ++;
		}

		// Player is almost eliminated
		if(this.target.getOwner().myOccupiedTerritories.size() <= 2){
			turnAttackThreshold ++;
		}
		
		allTerritoriesValues.put(this.attacker.name, lastAttackerValue + differential);
		allTerritoriesValues.put(this.target.name, lastTargetValue + differential);
	}

	@Override
	public void didGainNewTerritory(Territory conqueredTerritory) {
		// Add all units from the attacking territory to the new we just conquered
		// (MUST leave at least one on the territory we attacked with)

		this.didRecieveCardThisTurn = true;

		// Leave 2 units if possible (my own strategy)
		if(this.attacker.getUnits() > 2) {
			conqueredTerritory.setUnits(this.attacker.getUnits() - 2);
			this.attacker.setUnits(2);
		} else {
			conqueredTerritory.setUnits(this.attacker.getUnits() -1);
			this.attacker.setUnits(1);
		}
		
		turnAttackThreshold = (nbTurnsPlayed < EARLY_GAME_COUNTER) ? turnAttackThreshold - 1 : turnAttackThreshold;

		if(this.state == STATE_DEFENSIVE) {
			turnAttackThreshold --;
		}  
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
		move_findMostValuableMovement();
		endTurn();
	}


	/*******************************************************
	 * 
	 *  INITIAL UTILITY VALUE ASSIGNATION
	 * 
	 ******************************************************/
	private void assignInitialValuesToAllTerritories()
	{
		// All equals at start (1) and some get better values according to continents and k-connectivity
		for (int i = 0; i < this.public_allTerritories.size(); i++) {
			Territory currentTerritory = this.public_allTerritories.get(i);
			int value = 1;

			if(currentTerritory.continent.equals(Map.NORTH_AMERICA)){
				value = CONTINENT_NA_UTILITY_VALUE;
			} else if(currentTerritory.continent.equals(Map.SOUTH_AMERICA)){
				value = CONTINENT_SA_UTILITY_VALUE;
			} else if(currentTerritory.continent.equals(Map.AFRICA)){
				value = CONTINENT_AF_UTILITY_VALUE;
			} else if(currentTerritory.continent.equals(Map.EUROPE)){
				value = CONTINENT_EU_UTILITY_VALUE;
			} else if(currentTerritory.continent.equals(Map.ASIA)){
				value = CONTINENT_AS_UTILITY_VALUE;
			} else if(currentTerritory.continent.equals(Map.AUSTRALIA)){
				value = CONTINENT_AU_UTILITY_VALUE;
			}

			// Specific territories get better values (Choke points)
			if(currentTerritory.name.equals(Map.GREENLAND)){
				value += 15;
			}  else if(currentTerritory.name.equals(Map.INDONESIA)){
				value += 18;
			} else if(currentTerritory.name.equals(Map.ALASKA)){
				value += 7;
			} else if(currentTerritory.name.equals(Map.SIAM)){
				value += 18;
			}  else if(currentTerritory.name.equals(Map.BRAZIL)){
				value += 5;
			} else if(currentTerritory.name.equals(Map.KAMATCHKA)){
				value += 7;
			}  else if(currentTerritory.name.equals(Map.CHINA)){
				value += 19;
			} else if(currentTerritory.name.equals(Map.INDIA)){
				value += 19;
			}  else if(currentTerritory.name.equals(Map.QUEBEC)){
				value += 5;
			} 

			value += currentTerritory.adjacentTerritories.size(); // Connectivity to other territories is a +

			// Gradually generate the myTerritoriesValues hashmap (which won't be sorted at this point)
			allTerritoriesValues.put(currentTerritory.name, value);
			initialHeuristicValues.put(currentTerritory.name, value - 5);
		}
	}

	/*******************************************************
	 * 
	 *  AI - DEPLOYEMENT METHODS
	 * 
	 ******************************************************/
	// When first choosing territories (empty Map, first phase)
	static int deploy_lastValue = 1000;
	static String deploy_lastPick = "";
	private Territory deploy_pickMostValuableTerritory(){
		Territory t = null;
		int highestValue = -1000;
		
		// Try and pick most valued territory
		for(int i = 0; i < this.public_allTerritories.size(); i++){
			Territory temp = this.public_allTerritories.get(i);
			int value = allTerritoriesValues.get(temp.name);

			if(value >= highestValue && value <= deploy_lastValue && !temp.name.equals(deploy_lastPick) && temp.getOwner() == null){
				t = temp;
				highestValue = value;
			}
		}
		
		// if none were picked, try at random
		if(t == null){
			boolean found = false;
			while(!found){
				Random ran = new Random();
				t = this.public_allTerritories.get(ran.nextInt(public_allTerritories.size()));
				if(t.getOwner() == null){
					found = true;
				}
			}
		}
		
		deploy_lastPick = t.name;
		deploy_lastValue = highestValue;
		myTerritoriesValues.put(deploy_lastPick, deploy_lastValue);
		return t;
	}

	// Update while deploying
	private void deploy_updateUtilityValues()
	{	
		for (int i = 0; i < this.public_allTerritories.size(); i++) {
			Territory currentTerritory = this.public_allTerritories.get(i);
			String name = currentTerritory.name;
			int newValue = deploy_updateTerritoryValue(currentTerritory);

			// If the value goes over 100, limit the value
			newValue = (newValue > 100) ? 100 : newValue;
			newValue = (newValue < 1) ? 1 : newValue;
			allTerritoriesValues.put(name, newValue);
		}	
	}

	private int deploy_updateTerritoryValue(Territory t)
	{	
		int newValue = allTerritoriesValues.get(t.name);
		if(t.getOwner() != null){
			if(!t.getOwner().name.equals(myName)){
				if(t.getUnits() >= 5){			// If enemy territory is largely occupied, penalize
					newValue -= 4;
				} else if (t.getUnits() < 5 && t.getUnits() > 2){  	
					newValue -= 2;
				} else {						// If enemy territory is almost empty, +value
					newValue += 1;
				}
			} else {	// I own the adjacent territory
				newValue += 3;
				for(int i = 0; i < t.adjacentTerritories.size(); i++){
					Territory adjacent = t.adjacentTerritories.get(i);
					if(adjacent.getOwner() != null && !adjacent.getOwner().name.equals(myName)){ //enemy again
						if(adjacent.getUnits() > t.getUnits()){
							newValue -= 1;
						} 
					} else {
						newValue += 2;
					}
				}
			}
		}
		return newValue;
	}

	/*******************************************************
	 * 
	 *  AI - REINFORCEMENT METHODS
	 * 
	 ******************************************************/
	private void reinforce_updateUtilityValues()
	{	
		myTerritoriesValues.clear();
		for (int i = 0; i < this.myOccupiedTerritories.size(); i++) {
			Territory currentTerritory = this.myOccupiedTerritories.get(i);

			if(currentTerritory != null) {		
				String name = currentTerritory.name;
				int newValue = reinforce_updateMyTerritoryValue(currentTerritory);

				// If the value goes over 250, limit the value
				newValue = (newValue > 250) ? 250 : newValue;
				newValue = (newValue < 1) ? 1 : newValue;
				allTerritoriesValues.put(name, newValue);
				myTerritoriesValues.put(name, newValue);	

			} else {
				System.out.println("[SAM] : Reinforce error");
			}
		}	
	}

	private int reinforce_updateMyTerritoryValue(Territory t)
	{
		int newValue = 1;

		// Stores the initial values of my territory
		int units = t.getUnits();					// Nb of units of the currently evaluated territory
		String continent = t.continent;
		int highestEnemyUnits = -1;

		// Value helping in the reinforcement decision based on enemy adjacent territories
		int enemyDecider = 0;

		// Value helping in the reinforcement decision based on allied adjacent territories
		int allyDecider = 0;

		// Update adjacent territories
		for(int i = 0; i < t.adjacentTerritories.size(); i++){
			Territory adjacent = t.adjacentTerritories.get(i);

			if(!adjacent.getOwner().name.equals(myName)){ // Territory is enemy
				highestEnemyUnits = (adjacent.getUnits() > highestEnemyUnits) ? adjacent.getUnits() : highestEnemyUnits;

				// If in the same continent, +
				int cont_value = (continent.equals(adjacent.continent)) ? 3*getContinentUtilityValueByName(continent) : 0;

				// Enemy Territory Utility formula!
				enemyDecider += (-adjacent.getUnits()/2 + cont_value);
				
			} else { // One of my territory
				int cont_value = (continent.equals(adjacent.continent)) ? 2*getContinentUtilityValueByName(continent) : 0;
				allyDecider += cont_value + adjacent.getUnits();
			}
		}

		// Update current territory
		int attackCapability = (canAttackOtherTerritory(t)) ? 0 : -25; // Hard penalty if can't attack this turn
		int cont_value = getContinentUtilityValueByName(continent);
		int h_value = initialHeuristicValues.get(t.name);

		// Owned Territory Utility formula!
		newValue = attackCapability + 2*units + enemyDecider + allyDecider - highestEnemyUnits + 2*cont_value + h_value ;
		return newValue;
	}

	// Get next territory to reinforce
	private Territory reinforce_getNextTerritoryToReinforce(int counter){
		Territory t = null;
		ValueComparator comp =  new ValueComparator(this.myTerritoriesValues);
		TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(comp);
		sorted_map.putAll(this.myTerritoriesValues);

		// Chooses the next territory name in the sorted tree map according to the counter position
		String key = ""; int idx = 0;
		for (Entry<String, Integer> entry : sorted_map.entrySet())
		{
			if(counter == idx){
				key = entry.getKey();
				break;
			}
			idx ++;
		}

		// Selects the chosen territory from its name
		for(int i = 0; i < this.myOccupiedTerritories.size(); i++){
			Territory temp = myOccupiedTerritories.get(i);
			if(temp.name.equals(key)){
				t = temp;
			}
		}
		return t;
	}

	/*******************************************************
	 * 
	 *  AI - ATTACK METHODS
	 *  
	 ******************************************************/
	private void combat_updateUtilityValues()
	{	
		for (int i = 0; i < this.myOccupiedTerritories.size(); i++) {
			Territory currentTerritory = this.myOccupiedTerritories.get(i);

			if(currentTerritory != null) {		
				String name = currentTerritory.name;
				int newValue = combat_updateMyTerritoryValue(currentTerritory);

				// If the value goes over 500, limit the value
				newValue = (newValue > 500) ? 500 : newValue;
				newValue = (newValue < 1) ? 1 : newValue;
				allTerritoriesValues.put(name, newValue);
				myTerritoriesValues.put(name, newValue);	

			} else {
				System.out.println("[SAM] : Update error");
			}
		}	
	}

	// Updates one of my territory and all of its adjacent territories
	// Those with high value will be more likely to be attacked
	private int combat_updateMyTerritoryValue(Territory t)
	{
		int newValue = 1;

		// Stores the initial values of my territory
		int units = t.getUnits();					// Nb of units of the currently evaluated territory
		int k_value = t.adjacentTerritories.size();	// Connectivity value, the higher it is, the worst
		String continent = t.continent;
		int highestEnemyUnits = -1;
		int lowestEnemyUnits = 1000;
		int easyTargetBonus = 0;

		// Update adjacent territories
		for(int i = 0; i < t.adjacentTerritories.size(); i++){
			Territory adjacent = t.adjacentTerritories.get(i);

			if(!adjacent.getOwner().name.equals(myName)){ // Territory is enemy
				highestEnemyUnits = (adjacent.getUnits() > highestEnemyUnits) ? adjacent.getUnits() : highestEnemyUnits;

				lowestEnemyUnits = (adjacent.getUnits() < lowestEnemyUnits) ? adjacent.getUnits() : lowestEnemyUnits;

				// If in the same continent, +
				int cont_value = (continent.equals(adjacent.continent)) ? 4*getContinentUtilityValueByName(adjacent.continent) : 0;

				// Check if there is a good victory chance, penalize if not
				int victoryOddsValue = (units > adjacent.getUnits()) ? 4 : -6;
				
				// Check if the territory is easily seizable, big + if so
				int imminentDestruction = (adjacent.getUnits() < 3) ? 20 : 0;
				
				// Check if the enemy is too concentrated on a single territory
				int tooConentrated = getEnemyTooConcentratedValue(adjacent.getOwner(), adjacent);
				
				// Enemy Territory Utility formula!
				int adjValue = 3*units + cont_value + victoryOddsValue - adjacent.getUnits()/2 + imminentDestruction + tooConentrated;
				
				adjValue = (adjValue > 500) ? 500 : adjValue;
				adjValue = (adjValue < 1) ? 1 : adjValue;

				// Check if new value is superior, only modify if true
				if(allTerritoriesValues.get(adjacent.name) != null){
					int lastValue = allTerritoriesValues.get(adjacent.name);
					if(adjValue > lastValue){
						allTerritoriesValues.put(adjacent.name, adjValue);
					}
				} else{
					allTerritoriesValues.put(adjacent.name, adjValue);
				}

			} else { // One of my adjacent territory
				int cont_value = (continent.equals(adjacent.continent)) ? getContinentUtilityValueByName(continent) : 0;
				int adjValue = k_value + units + cont_value;

				// Check if new value is superior, only modify if true
				int lastValue = allTerritoriesValues.get(adjacent.name);
				if(adjValue > lastValue){
					allTerritoriesValues.put(adjacent.name, adjValue);
				}
			}
		}
		
		// LOW ENEMY = 1
		easyTargetBonus = (lowestEnemyUnits == 1) ? 25 : 0;
		
		// Update current territory
		int attackCapability = (canAttackOtherTerritory(t)) ? 0 : -15;
		int attackCapacity = (units > 1) ? 0 : -15;
		int cont_value = getContinentUtilityValueByName(t.continent);

		// Owned Territory Utility formula!
		newValue = (int) (2*cont_value + attackCapability + attackCapacity + 3*units - highestEnemyUnits + easyTargetBonus );

		return newValue;
	}

	// Pick the current most valuable territory I own and 
	// try to find an adjacent territory to attack that has
	// a high value
	private void pickMostPowerfulAttackerAndBestTarget(){

		int highestVal = -1;
		int att_highestVal = -1;
		int lowestUnits = 1000;
		Territory bestAttackCandidate = null;
		Territory targetCandidate = null;

		int allTerritoriesCount = this.myOccupiedTerritories.size();
		String lastAttckerTried = "";	// If a candidate is discarded, the algorithm won't pick the same choice indefinitely because of this value

		// Get the potential attacker candidates, which are those that actually CAN attack
		ArrayList<Territory> attackerCandidates = new ArrayList<Territory>();
		for(Territory t : myOccupiedTerritories){
			if(canAttackOtherTerritory(t) && t.getUnits() >= 2){
				attackerCandidates.add(t);
			}
		}
		if(attackerCandidates.size() > 0) { // if the candidate list is empty, skip attack
			while(allTerritoriesCount > 0) { 

				// Get attacker according to best value
				for (int i = 0; i < attackerCandidates.size(); i++) {
					Territory tempAttackCandidate = attackerCandidates.get(i);
					int value = myTerritoriesValues.get(tempAttackCandidate.name);

					if(!tempAttackCandidate.name.equals(lastAttckerTried)){ // match against last best candidate if the last try failed
						if(value > att_highestVal){
							att_highestVal = value;
							bestAttackCandidate = tempAttackCandidate;
						}
					} 
				}

				// Get target with a UCS graph algorithm with a single level, 
				// the adjacent territories of the attacker candidate, according
				// to the calculated utility value
				if(bestAttackCandidate!= null) {
					lastAttckerTried = bestAttackCandidate.name;
					int candidatesTried = 0;
					while(candidatesTried < 42){ 														// Tries all of the 42 territories

						for (int j = 0; j < bestAttackCandidate.adjacentTerritories.size(); j++) { 		// Check the attackerTarget adjacent territories
							Territory tempTargetCandidate = bestAttackCandidate.adjacentTerritories.get(j);

							if(!tempTargetCandidate.getOwner().name.equals(myName)){					// Check if we aren't attacking ourself
								int value = allTerritoriesValues.get(tempTargetCandidate.name);			// Value from HashMap

								if(tempTargetCandidate.getUnits() <= lowestUnits - candidatesTried){	// Check if it's the lowest nb of units around
									lowestUnits = tempTargetCandidate.getUnits();
									if(value > highestVal){ 											// If the value is better than the last best
										highestVal = value;
										targetCandidate = tempTargetCandidate;
									}
								} 
							}
						}
						if(targetCandidate == null){
							candidatesTried += 1;	//Next pass won't be as sever
						} else {
							candidatesTried = 1000;	// Breaks out of loop, found a target
						}
					}

					if(targetCandidate != null) {
						for(int i = 0; i < this.public_allTerritories.size(); i++){
							Territory t = this.public_allTerritories.get(i);
							if(t.name.equals(bestAttackCandidate.name)){
								this.attacker = t;
							} else if(t.name.equals(targetCandidate.name)){
								this.target = t;
							}
						}
						
						if(this.attacker != null && this.target != null){ 	// If attacker and target found, break out of loop
							allTerritoriesCount = -1000;
						} else {
							allTerritoriesCount --;							// else, retry the whole process with lower expectations
						}
					} else {
						System.out.println("[SAM] : Not attacking this turn : no target");
						allTerritoriesCount--;
					}
				} else {
					System.out.println("[SAM] : Not attacking this turn : no attacker");
					allTerritoriesCount--;
				}
			} 
		} else {
			System.out.println("[SAM] : No attacking candidates");
		}
	}
	
	/*******************************************************
	 * 
	 *  AI - UNITS MOVEMENTS 
	 *  
	 ******************************************************/
	
	// Using utility values, find the best origin and destination for a movement
	private void move_findMostValuableMovement()
	{	
		ArrayList<Territory> originCandidates = new ArrayList<Territory>();
		moveTerritoriesValues.clear();
		
		// First pass : check if any territory is incapable of attacking, meaning it is surrounded by allied territories.
		for(Territory t : myOccupiedTerritories){
			if(!canAttackOtherTerritory(t) && t.getUnits() > 1){
				originCandidates.add(t);
			}
		}

		// If no territories were chosen in the loop above, there is no point moving (not optimal)
		if(originCandidates.size() != 0){	
			int maxUnits = -1000;
			int maxValue = -1000;
			Territory bestOriginCandidate = null;
			Territory bestDestinationCandidate = null;

			// Get the best origin candidate (the one with the most Units)
			for(Territory t : originCandidates){
				int units = t.getUnits();
				if(units > maxUnits){	
					maxUnits = units;
					bestOriginCandidate = t;
				}
			}

			// Upadte utility values
			move_updateTerritoryValues(bestOriginCandidate);
			
			// Get the best destination candidate (the one with the best Value)
			for(Territory t : bestOriginCandidate.adjacentTerritories){
				int value = moveTerritoriesValues.get(t.name);
				if(value > maxValue && t.getOwner().name.equals(myName)){	
					maxValue = value;
					bestDestinationCandidate = t;
				}
			}
			
			// Prevent circular looping each 2 turns, goes for a random in that case
			if(lastMoveOriginTerritory.name.equals(bestDestinationCandidate.name)){
				Random ran = new Random();
				bestDestinationCandidate = bestOriginCandidate.adjacentTerritories.get(ran.nextInt(bestOriginCandidate.adjacentTerritories.size()));
				if(!bestDestinationCandidate.getOwner().name.equals(myName)){
					System.out.println("[SAM] : Wrong owner pick");
				}
			}
			
			// Final check
			if(bestOriginCandidate != null && bestDestinationCandidate != null && maxUnits >= 2 
					&& (!lastMoveOriginTerritory.name.equals(bestOriginCandidate.name) 
					 || !lastMoveDestinationTerritory.name.equals(bestDestinationCandidate.name))){
				
				this.moveDestination = bestDestinationCandidate;
				this.moveOrigin = bestOriginCandidate;
				this.moveUnits = maxUnits - 2;							//FIXME: 2 pourrait etre 1
				lastMoveDestinationTerritory = this.moveDestination;
				lastMoveOriginTerritory = this.moveOrigin;
				
			} else {
				System.out.println("[SAM] :  No move candidates");
			}
		} else {
			System.out.println("[SAM] : No move candidates");
		}	
	}
	
	private Territory lastMoveDestinationTerritory;
	private Territory lastMoveOriginTerritory;
	
	// Updates the origin's adjacent territories value
	private void move_updateTerritoryValues(Territory origin){
		for(Territory ter : origin.adjacentTerritories){				// All of my territories
			int value = 1;
			
			for(Territory adj : ter.adjacentTerritories){				// All territories adjacent to my territories
				if(adj.getOwner().name.equals(myName)){ 				// ally territory
					value += (canAttackOtherTerritory(ter)) ? 15 : 0;	// the allied territory can attack another one next turn
					value += (adj.getUnits() > origin.getUnits()) ? 40 : 0; // Help a fellow territory if he's powerful already
				} else { 												// enemy territory
					value += (adj.getUnits() < 2) ? 2 : 0;
					value += (adj.getUnits() > ter.getUnits()) ? 3 : 0;
					value += 3;
				}
			}

			value += initialHeuristicValues.get(ter.name);
			value += (ter.getUnits() < 2) ? 12 : 0;
			
			moveTerritoriesValues.put(ter.name, value);

		}
	}
	
	/*******************************************************
	 * 
	 *  UTILITIES
	 *  
	 ******************************************************/
	// Determine if a territory can attack another one next turn
	private boolean canAttackOtherTerritory(Territory t){
		for(int i = 0; i < t.adjacentTerritories.size(); i ++){
			Territory a = t.adjacentTerritories.get(i);
			if(!a.getOwner().name.equals(t.getOwner().name)){
				return true;
			}	
		}
		return false;
	}

	// Reset state values
	private void endTurn(){
		nbTurnsPlayed ++;
		this.didRecieveCardThisTurn = false;
		this.nbOfAttacksTried = 0;
		turnAttackThreshold = 4;
	}

	// HashMap sorting (via TreeMap) - from StackOverflow : 
	// http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
	class ValueComparator implements Comparator<String> {

		HashMap<String, Integer> base;
		public ValueComparator(HashMap<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	// Calculate utility values by continent for this turn
	private int getContinentUtilityValueByName(String name){
		int value = 0;
		float continentValue = 25 * getPercentageOfContinentOwned(name);
		if(name.equals(Map.NORTH_AMERICA)){
			value = CONTINENT_NA_UTILITY_VALUE;
		} else if(name.equals(Map.SOUTH_AMERICA)){
			value = CONTINENT_SA_UTILITY_VALUE;
		} else if(name.equals(Map.AUSTRALIA)){
			value = CONTINENT_AU_UTILITY_VALUE;
		} else if(name.equals(Map.AFRICA)){
			value = CONTINENT_AF_UTILITY_VALUE;
		} else if(name.equals(Map.EUROPE)){
			value = CONTINENT_EU_UTILITY_VALUE;
		} else if(name.equals(Map.ASIA)){
			value = CONTINENT_AS_UTILITY_VALUE;			  
		}
		return 2*value + (int)continentValue;
	}

	// Get the % of a continent based on name
	private float getPercentageOfContinentOwned(String name){
		Continent continent = null;

		if(name.equals(Map.NORTH_AMERICA)){
			continent = Map.northAmerica;
		} else if(name.equals(Map.SOUTH_AMERICA)){
			continent = Map.southAmerica;
		} else if(name.equals(Map.AUSTRALIA)){
			continent = Map.australia;
		} else if(name.equals(Map.AFRICA)){
			continent = Map.africa;
		} else if(name.equals(Map.EUROPE)){
			continent = Map.europe;
		} else if(name.equals(Map.ASIA)){
			continent = Map.asia;			  
		} else {
			return -1; // error occured
		}

		float targetCount = continent.territories.size();
		float actualCount = 0;
		for(Territory t : myOccupiedTerritories){
			if(t.continent.equals(continent.name)){
				actualCount ++;
			}
		}
		float percentage = actualCount / targetCount;
		return percentage;
	}
	
	// Ranks the players according to their units count and their nb of territories owned
	private void evaluatePlayersRank(){
		for(Territory t : public_allTerritories){
			try {
				int value = playersRanking.get(t.getOwner().name);
				playersRanking.put(t.getOwner().name, value+t.getUnits()+2*t.getOwner().myOccupiedTerritories.size());
			} catch (Exception e) {
				playersRanking.put(t.getOwner().name,t.getUnits());
			}
		}
		
		ValueComparator comp =  new ValueComparator(this.playersRanking);
		TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(comp);
		sorted_map.putAll(this.playersRanking);

		// Chooses the next territory name in the sorted tree map according to the counter position
		int idx = 0;
		for (Entry<String, Integer> entry : sorted_map.entrySet())
		{
			this.state = STATE_DEFENSIVE;
			if(entry.getKey().equals(myName) && idx < 2){
				this.state = STATE_OFFENSIVE;
				break;
			}
			idx ++;
		}
	}
	
	// Checks if a players is using a STACKER strategy (pretty cheap :) and tries
	// to prevent him from stacking
	private int getEnemyTooConcentratedValue(Player p, Territory t){
		int value = 0;
		float ratio = t.getUnits() / p.countUnits();
		
		if(ratio > 0.6){
			value = 20;
		}
		
		return value;
	}

	/*******************************************************
	 * 
	 *  PRIVATE FIELDS
	 * 
	 ******************************************************/
	
	// Heuristic values - Continents
	private int CONTINENT_NA_UTILITY_VALUE = 15;
	private int CONTINENT_SA_UTILITY_VALUE = 5;
	private int CONTINENT_AF_UTILITY_VALUE = 1;	
	private int CONTINENT_AS_UTILITY_VALUE = 9;
	private int CONTINENT_AU_UTILITY_VALUE = 15;
	private int CONTINENT_EU_UTILITY_VALUE = 1;
	
	// Heuristic values - Owned Continents
	private int owned_na = 12;
	private int owned_sa = 7;
	private int owned_eu = 14;
	private int owned_au = 10;
	private int owned_as = 20;
	private int owned_af = 5;

	// Heuristic values - States
	private final int STATE_DEFENSIVE = 0;
	private final int STATE_OFFENSIVE = 1;
	
	private final int OFFENSE_BONUS_THRESHOLD = 6;
	private final int DEFENSE_PENALTY_THRESHOLD = 1;
	private final int EARLY_GAME_COUNTER = 4;
	private final int ATTACK_THRESHOLD = 10;

	// Will attack variables
	private int turnAttackThreshold = ATTACK_THRESHOLD;
	
	int nbTurnsPlayed = 0;

	private final String myName = this.name;

}