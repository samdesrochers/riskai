package poly.game;

import java.util.ArrayList;

public class MaxAI extends Player{

	
	private ArrayList<Territory> owned;
	private ArrayList<Continent> currentContinent;
	private int minValue = 20;
	public static final String NORTH_AMERICA 		= "north_america";
	public static final String SOUTH_AMERICA 		= "south_america";
	public static final String EUROPE 				= "europe";
	public static final String ASIA					= "asia";
	public static final String AUSTRALIA 			= "australia";
	public static final String AFRICA 				= "africa";
	public enum State{PriseContinent, DefenseContinent, AttaqueContinent};
	public static boolean wonCard = false;
	public static boolean sabotage = false;;
	public State CurrentState = State.PriseContinent;
	public static int continentToAttackIndex;
	public MaxAI(String name) {
		super(name);
		
		owned = new ArrayList<Territory>();
		currentContinent = new ArrayList<Continent>();
		currentContinent.add(new Continent(SOUTH_AMERICA));
		currentContinent.add(new Continent(NORTH_AMERICA));
		currentContinent.add(new Continent(AFRICA));
		currentContinent.add(new Continent(AUSTRALIA));
		currentContinent.add(new Continent(EUROPE));
		currentContinent.add(new Continent(ASIA));
		continentToAttackIndex = -1;
		
	}

	protected int GetTerritoryValueInitial(Territory territory, Continent continent) {
		int numberOfAdjacent = territory.adjacentTerritories.size();
		int territoryValue = numberOfAdjacent + minValue; 
		if (territory.getUnits() > 0) {
			territoryValue += numberOfAdjacent;
			territoryValue -= territory.getUnits();
		}
		if (numberOfAdjacent < 3) {
			territoryValue -= 2;
		}
		int neighborBonus = 0;
		int neighbors = 0;
		//defense
		for (int k = 0; k < numberOfAdjacent; k++) {
			Territory t = territory.adjacentTerritories.get(k);
			if (t.getOwner() != null && t.getOwner().name == this.name) {
				neighborBonus-=t.getUnits();
				neighbors++;
			} else if (t.getOwner() != null) {
				territoryValue+=(t.getUnits()/2 + t.getUnits()%2);
			}
		}
		
		//attack
		for (int k = 0; k < numberOfAdjacent; k++) {
			Territory t = territory.adjacentTerritories.get(k);
			if (t.getOwner() != null && t.getOwner().name == this.name) {
				neighborBonus-=t.getUnits();
				neighbors++;
			} else if (t.getOwner() == null && t.continent != territory.continent) {
				territoryValue--;
			}
		}
		
		neighbors = neighbors/2 + neighbors%2;
		territoryValue += neighborBonus/4 + neighborBonus%2;
		
		/*if (neighbors > 1) {
			territoryValue -= Math.pow(neighbors, 2);
			territoryValue = Math.max(1, territoryValue);
			
		}*/
		int numberOfEmptyTerritory = 0;
		int numberOfOwnedTerritory = 0;
		int numberOfEnnemiTerritory = 0;
		for(Territory t : continent.territories)
		{
			if(!t.isOccupied)
			{
				numberOfEmptyTerritory += 1;
			}
			else if(t.getOwner().name == this.name)
			{
				numberOfOwnedTerritory += 1;
			}
			else
			{
				numberOfEnnemiTerritory += 1;
			}
		}
		territoryValue += numberOfEnnemiTerritory;
		territoryValue += numberOfEmptyTerritory;
		territoryValue -= numberOfOwnedTerritory;
		
		if(territory.isContinentFrontier && numberOfOwnedTerritory > 0)
			territoryValue -= 5;
		else if(territory.isContinentFrontier)
			territoryValue -= 2;
		
		//Depending on continent bonus and continent worth progression bonus
		if((continent.name == NORTH_AMERICA)){
			territoryValue -= 5; //Bonus
			territoryValue -= 3; //usefull continent since related to SA
		} else if(continent.name == EUROPE){
			territoryValue -= 5; //Bonus
			territoryValue += 3; //Not usefull continent since related to ASIA
		}else if(continent.name == AUSTRALIA){
			territoryValue -= 2; //Bonus
			territoryValue += 10; //Not usefull continent since related to ASIA
		} else if(continent.name == SOUTH_AMERICA){
			territoryValue -= 2; //Bonus
			territoryValue -= 2; //Usefull since easy to protect and related to Africa and NA
		} else if(continent.name == AFRICA){
			territoryValue -= 3; //Bonus
			territoryValue -= 3; //usefull continent since related to SA
		}  else if(continent.name == ASIA){
			territoryValue -= 7; //Bonus
			territoryValue += 7; //Not usefull continent since too big
		}
		
		return territoryValue;
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
		int bestValue = 1000;
		Territory bestTerritory= new Territory("toto", "tata", false);
		int value = 0;
		FillContinent();
		for(Continent c : currentContinent)
		{
			for(Territory t : c.territories)
			{
				if(!t.isOccupied)
				{
					value = GetTerritoryValueInitial(t,c);
					if(value <= bestValue )
					{
						bestTerritory = t;
						bestValue = value;
					}
				}
			}
			c.territories.clear();
		}
		
		owned.add(bestTerritory);
		return bestTerritory.name;
	}
		
		
	

	private void FillContinent() {
		for(Territory t : public_allTerritories)
		{
			if(t.continent == SOUTH_AMERICA)
			{
				currentContinent.get(0).territories.add(t);
			}
			else if(t.continent == NORTH_AMERICA)
			{
				currentContinent.get(1).territories.add(t);
			}
			else if(t.continent == AFRICA)
			{
				currentContinent.get(2).territories.add(t);
			}
			else if(t.continent == AUSTRALIA)
			{
				currentContinent.get(3).territories.add(t);
			}
			else if(t.continent == EUROPE)
			{
				currentContinent.get(4).territories.add(t);
			}
			else if(t.continent == ASIA)
			{
				currentContinent.get(5).territories.add(t);
			}
						
		}
		
	}
	
	private void EraseContinent() {
		for(Continent c : currentContinent)
		{
			c.territories.clear();
						
		}
		
	}

	// Choose a number of units to place on a territory during
	// the initial deployment phase. Must use between 1 and 3 units
	@Override
	public int chooseNbOfUnits(int remainingThisTurn) {
		return 1;
	}
	
	// Choose a territory to reinforce with 1 to 3 units
	// Returns the territory to reinforce
	@Override
	public String pickReinforceTerritory() {
		wonCard = false;
		Territory chosen = GetTerritoryToAddUnits();
		if(chosen == null)
		{
			chosen = this.myOccupiedTerritories.get(0);
		}
		return chosen.name;
	}
	
	private Territory GetTerritoryToAddUnits()
	{
		try
		{
			EraseContinent();
			FillContinent();
			Territory chosen = null;
			CurrentState = ChooseState(currentContinent);
					
			if(CurrentState.equals(State.DefenseContinent))
			{
				ArrayList<Continent> OwnedContinent = GetContinentOwned(currentContinent);
				Continent toDefend = FindWeakestContinent(OwnedContinent);
				chosen = MostVulnerableTerritory(toDefend);
			}
			else if(CurrentState.equals(State.PriseContinent))
			{
				int index = GetContinentToOccupyNext(currentContinent);
				for(Territory t: currentContinent.get(index).territories)
				{
					if(t.getOwner().name == name && HasAdjacentEnnemiInContinent(t) && HasAdjacentEnnemiInRange(t))
					{
						chosen = t;
						break;
					}
				}
				if(chosen == null)
				{
					chosen = FindContinentFrontierNearContinent(currentContinent.get(index));
					if(chosen == null)
					{
						Continent toDefendNext = currentContinent.get(index);
						chosen = MostVulnerableTerritory(toDefendNext);
					}
				}
			}
			else
			{
				chosen = FindNearestTerritoryToEnnemi();
			}
			return chosen;
		}
		catch(RuntimeException e)
		{
			System.out.println("Exception occured in Choosing the Territory " + e.getMessage());
			return null;
			
		}
	}
	
	
	private Territory FindNearestTerritoryToEnnemi() {
		for(Continent c: currentContinent)
		{
			for(Territory t: c.territories)
			{
				if(t.getOwner().name == name)
				{
					for(Territory adj: t.adjacentTerritories)
					{
						if(adj.getOwner().name != name)
						{
							return t;
						}
					}
				}
			}
		}
		return null;
	}

	private Territory FindContinentFrontierNearContinent(Continent continent) {
		int maxUnits = 0;
		Territory chosen = null;
		for(Territory t: continent.territories)
		{
			for(Territory adj: t.adjacentTerritories)
			{
				if(adj.continent != t.continent && adj.getOwner().name == name)
				{
					if(adj.getUnits() > maxUnits)
					{
						maxUnits = adj.getUnits();
						chosen = adj;
					}
				}
			}
		}
		return chosen;
	}

	private State ChooseState(ArrayList<Continent> currentContinent) {
		ArrayList<Continent> owned = GetContinentOwned(currentContinent);
		if(owned.size() == 0)
		{
			return State.PriseContinent;
		}
		else if(remainingUnits > 30)
		{
			return State.AttaqueContinent;
		}
		else
		{
			Continent weakest = FindWeakestContinent(owned);
			if(weakest == null)
			{
				return State.PriseContinent;
			}
			else
			{
				Territory weakestT = MostVulnerableTerritory(weakest);
				if(weakestT == null)
				{
					return State.PriseContinent;
				}
				return State.DefenseContinent;
			}
		}
	}

	private Territory MostVulnerableTerritory(Continent toDefend) {
		int numberUnitsDifference = 100;
		Territory vulnerable = null;
		for(Territory t: toDefend.territories)
		{
			if(t.isContinentFrontier && HasAdjacentEnnemi(t))
			{
				for(Territory adj: t.adjacentTerritories)
				{
					if(adj.getOwner().name != name && t.getOwner().name == name)
					{
						int unitDiff = t.getUnits()- adj.getUnits(); 
						if(unitDiff < numberUnitsDifference)
						{
							numberUnitsDifference = unitDiff;
							vulnerable = t;
						}
					}
				}
			}
		}
		return vulnerable;
	}

	private Continent FindWeakestContinent(ArrayList<Continent> ownedContinent) {
		int maxThreats = 0;
		int indexMaxThreats = -1;
		int i = 0;
		while( i < ownedContinent.size())
		{
			int ContinentThreat = 0;
			for(Territory t: ownedContinent.get(i).territories)
			{
				if(t.isContinentFrontier && HasAdjacentEnnemi(t))
				{
					for(Territory adj: t.adjacentTerritories)
					{
						if(adj.getOwner().name != name && (adj.getUnits()+4 > t.getUnits()))
						{
							ContinentThreat+=1;
						}
					}
				}
			}
			if(ContinentThreat > maxThreats)
			{
				maxThreats = ContinentThreat;
				indexMaxThreats = i;
			}
			i++;
		}
		if(maxThreats == 0)
		{
			return null;
		}
		return ownedContinent.get(indexMaxThreats);
	}

	private boolean HasAdjacentEnnemi(Territory t) {
		for(Territory adj: t.adjacentTerritories)
		{
			if(adj.continent != t.continent && adj.getOwner().name != name)
			{
				return true;
			}
				
		}
		return false;
	}
	
	private boolean HasAdjacentEnnemiInContinent(Territory t) {
		for(Territory adj: t.adjacentTerritories)
		{
			if(adj.continent == t.continent && adj.getOwner().name != name)
			{
				return true;
			}
				
		}
		return false;
	}
	
	private boolean HasAdjacentEnnemiInRange(Territory t) {
		for(Territory adj: t.adjacentTerritories)
		{
			int unitDiff = t.getUnits() - adj.getUnits();
			if(unitDiff < 3 && adj.getOwner().name != name)
			{
				for(Territory adjOwned: adj.adjacentTerritories)
				{
					int unitDiff2 = adjOwned.getUnits() - adj.getUnits();
					if(adjOwned.getOwner().name == name && unitDiff2 >= 3  )
					{
						return false;
					}
				}
				return true;
			}
				
		}
		return false;
	}
	
	

	private ArrayList<String> GetContinentNeigbours(Continent continent)
	{
		ArrayList<String> Continents = new ArrayList<String>();
		for(Territory t: continent.territories)
		{
			for(Territory adj:t.adjacentTerritories)
			{
				if(!Continents.contains(adj.continent) && adj.continent != t.continent)
				{
					Continents.add(adj.continent);
				}
			}
		}
		return Continents;
		
	}
	private int GetContinentToOccupyNext(ArrayList<Continent> currentContinent) {
		ArrayList<Continent> ownedContinent = GetContinentOwned(currentContinent);
		boolean notFoundNext = false;
		if(continentToAttackIndex != -1)
		{
			Continent next = currentContinent.get(continentToAttackIndex);
			for(Continent owned: ownedContinent)
			{
				if(owned.name == next.name)
				{
					notFoundNext = true;
				}
			}
			if(notFoundNext)
			{
				return continentToAttackIndex;
			}
		}
		
		ArrayList<String> possibleContinentName = new ArrayList<String>();
		if(ownedContinent.size() == 0)
		{
			return GetContinentMostOccupied(currentContinent);
		}
		for(Continent c: ownedContinent)
		{
			possibleContinentName.addAll(GetContinentNeigbours(c));
		}
		int countContinentMax = 0;
		String continentName = "";
		int i = 0;
		while(i < possibleContinentName.size())
		{
			int countContinent = 0;
			for(String name : possibleContinentName)
			{
				
				if(name == possibleContinentName.get(i))
				{
					countContinent += 1;
				}
							
			}
			if(countContinent > countContinentMax && IsNotOwned(possibleContinentName.get(i)))
			{
				countContinentMax = countContinent;
				continentName = possibleContinentName.get(i);
			}
			i++;
		}
		boolean found = false;
		i = 0;
		int indexOccupied = -1;
		while(i < currentContinent.size() && !found)
		{
			if(currentContinent.get(i).name == continentName)
			{
				found = true;
				indexOccupied = i;
			}
			i++;
		}
		
		return indexOccupied;
	}
	
	private boolean IsNotOwned(String name) {
		
		for(Continent c: GetContinentOwned(currentContinent))
		{
			if(c.name == name)
			{
				return false;
			}	
			
		}
		return true;
	}

	private ArrayList<Continent> GetContinentOwned(ArrayList<Continent> currentContinent) {
		int i = 0;
		ArrayList<Continent> occupied = new ArrayList<Continent>();
		while(i < currentContinent.size())
		{
			float numberOccupied = 0;
			for(Territory t: currentContinent.get(i).territories)
			{
				if(t.getOwner().name == name)
				{
					numberOccupied += 1;
				}
			}
			double currentlyOccupied = numberOccupied/currentContinent.get(i).territories.size();
			if(currentlyOccupied == 1)
			{
				occupied.add(currentContinent.get(i));
			}
			i++;
				
		}
		return occupied;
	}

	private int GetContinentMostOccupied(ArrayList<Continent> currentContinent) {
		double MaxOccupiedProportion = 0.0;
		int indexOccupied = -1;
		int i = 0;
		while(i < currentContinent.size())
		{
			float numberOccupied = 0;
			for(Territory t: currentContinent.get(i).territories)
			{
				if(t.getOwner().name == name)
				{
					numberOccupied += 1;
				}
			}
			double currentlyOccupied = numberOccupied/currentContinent.get(i).territories.size();
			if(indexOccupied == -1 || currentlyOccupied > MaxOccupiedProportion)
			{
				MaxOccupiedProportion = currentlyOccupied;
				indexOccupied = i;
			}
			i++;
				
		}
		return indexOccupied;
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
		while(remainingUnits > 0)
		{
			Territory chosen = GetTerritoryToAddUnits();
			if(chosen == null)
			{
				chosen = this.myOccupiedTerritories.get(0);
			}
			chosen.addUnits(1);
			remainingUnits -= 1;
		}
		
	}

	@Override
	public ArrayList<Card> tradeCards() {
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
		if(FindBestTerritoryToAttack() != null)
		{
			this.willAttack = true;
		}
		else
		{
			this.willAttack = false;
		}
		EraseContinent();
		FillContinent();
	}
	
	// Decides which territory to attack from and what territory to attack.  MUST be adjacent :)
	public void chooseAttackerAndTarget() {
		this.target = FindBestTerritoryToAttack();
		this.attacker = FindTerritoryToAttackFrom(target);
		
	}

	private Territory FindBestTerritoryToAttack() {
		Territory toAttack = null;
		int maxUnitDiff = 0;
		
		if(CurrentState == State.PriseContinent)
		{
			for(Territory owned: this.myOccupiedTerritories)
			{
				for(Territory adj : owned.adjacentTerritories)
				{
					if(adj.getOwner().name != name && adj.continent == owned.continent && ((adj.getUnits() + 2) < owned.getUnits()) && shouldAttack(owned,adj))
					{
						int unitDiff = owned.getUnits() - adj.getUnits();
						if(unitDiff > maxUnitDiff)
						{
							toAttack = adj;
							maxUnitDiff = owned.getUnits() - adj.getUnits();
						}
						
					}
				}
			}
			if(toAttack == null)
			{
				for(Territory owned: this.myOccupiedTerritories)
				{
					for(Territory adj : owned.adjacentTerritories)
					{
						if(adj.getOwner().name != name && ((adj.getUnits() + 2) < owned.getUnits()) && shouldAttack(owned,adj))
						{
							int unitDiff = owned.getUnits() - adj.getUnits();
							if(unitDiff > maxUnitDiff)
							{
								toAttack = adj;
								maxUnitDiff = owned.getUnits() - adj.getUnits();
							}
							
						}
					}
				}
			}
		}
		else 
		{
			//Defense de son continent 
			if(!wonCard)
			{
				ArrayList<Continent> owned = GetContinentOwned(currentContinent);
				for(Continent c: owned)
				{
					for(Territory t: c.territories)
					{
						if(HasAdjacentEnnemi(t))
						{
							for(Territory adj: t.adjacentTerritories)
							{
								if(adj.getOwner().name != name && (adj.getUnits() < t.getUnits()))
								{
									toAttack = adj;
								}
										
							}
						}
					}
				}
			}
		}
		if(toAttack == null)
		{
			for(Territory owned: this.myOccupiedTerritories)
			{
				for(Territory adj : owned.adjacentTerritories)
				{
					if(adj.getOwner().name != name && adj.continent != owned.continent && ((adj.getUnits() + 2) < owned.getUnits()))
					{
						int units = GetTotalUnitsFromContinent(GetContinentFromName(adj.continent));
						if(units +3 < owned.getUnits())
						{
							continentToAttackIndex = GetIndexContinentFromName(adj.continent);
						}
						toAttack = adj;
						sabotage = true;
						return toAttack;						
					}
				}
			}
			
			
		}
		return toAttack;
		
	}
	
	private Continent GetContinentFromName(String name) {
		for(Continent c : currentContinent)
		{
			if(c.name == name)
			{
				return c;
			}
		}
		return null;
	}
	private int GetIndexContinentFromName(String name) {
		int index = 0;
		for(Continent c : currentContinent)
		{
			if(c.name == name)
			{
				return index;
			}
			index++;
		}
		return -1;
	}

	private boolean shouldAttack(Territory owned, Territory adj) {
		Continent toAttack = currentContinent.get(GetContinentToOccupyNext(currentContinent));
		if(owned.continent == adj.continent)
			return true;
		if(toAttack.name != adj.continent)
			return false;
		int ennemiUnits = GetTotalUnitsFromContinent(toAttack);
		if((ennemiUnits +2) >= owned.getUnits() )
		{
			return false;
		}
		
		return true;
	}

	

	private int GetTotalUnitsFromContinent(Continent continent) {
		int ennemiUnits = 0;
		for(Territory t: continent.territories)
		{
			if(t.getOwner().name != name)
			{
				ennemiUnits += t.getUnits();
			}
		}
		return ennemiUnits;
	}

	private Territory FindTerritoryToAttackFrom(Territory target) {
		Territory best = null;
		int maxUnits = 0;
		for(Territory t: this.myOccupiedTerritories)
		{
			for(Territory adj: t.adjacentTerritories)
			{
				if(adj.name == target.name)
				{
					if( t.getUnits() > (target.getUnits()) && t.getUnits() > maxUnits)
					{
						maxUnits = t.getUnits();
						best = t;
					}
				}
			}
		}
		return best;
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
		if(sabotage)
		{
			if(this.attacker.getUnits() > 3)
			{
				conqueredTerritory.setUnits(3);
				this.attacker.setUnits(this.attacker.getUnits()-3);
			}
			else
			{
				conqueredTerritory.setUnits(this.attacker.getUnits() -1);
				this.attacker.setUnits(1);
			}
			sabotage = false;
		}
		else
		{
			conqueredTerritory.setUnits(this.attacker.getUnits() -1);
			this.attacker.setUnits(1);
		}
		wonCard = true;
	}
	
		

	/*******************************************************
	 * 
	 *  MOVEMENT PHASE METHODS
	 *  Set [this.moveOrigin] , [this.moveDestination] , [this.moveUnits]
	 *  when you want to move units from one territory to another 
	 *  (only once at the end of your turn)
	 * 
	 ******************************************************/
	// Set [this.moveOrigin] , [this.moveDestination] , [this.moveUnits]
	// if you want to move units from one territory to another (only once per turn)
	@Override
	public void chooseMovementTerritoriesAndUnits() {
		Territory toAdd = GetTerritoryToAddUnits();
		this.moveOrigin = toAdd;
		this.moveDestination = toAdd;
		this.moveUnits = 0;
		if(toAdd != null)
		{
			for(Territory adj: toAdd.adjacentTerritories)
			{
				if(adj.getOwner().name == name && adj.getUnits() > 1 && (!adj.isContinentFrontier))
				{
					this.moveOrigin = adj;
					this.moveDestination = toAdd;
					this.moveUnits = adj.getUnits()-1;
					break;
				}
			}
		}
		if(this.moveUnits == 0)
		{
			//Find territory with most units and transfer them closer to ennemi
			int maxUnitsUnused = 0;
			boolean setOrigin = false;
			for(Territory t: this.myOccupiedTerritories)
			{
				if(t.getUnits() > maxUnitsUnused && !HasAdjacentEnnemi(t))
				{
					maxUnitsUnused = t.getUnits();
					this.moveOrigin = t;
					setOrigin = true;
				}
			}
			if(setOrigin )
			{
				for(Territory adj: this.moveOrigin.adjacentTerritories)
				{
					if(HasAdjacentEnnemi(adj))
					{
						this.moveDestination = adj;
					}
				}
			}
			this.moveUnits = this.moveOrigin.getUnits()-1;
			
		}
	}
	/*******************************************************
	 * 
	 *  Utilities
	 * 
	 ******************************************************/
}
