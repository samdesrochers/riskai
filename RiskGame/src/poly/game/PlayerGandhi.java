

import java.util.ArrayList;
import java.util.Random;


public class PlayerGandhi extends Player {
	
	private Random ran;
	static boolean firstPick = true;
	
/* STATES AND DECISION MAKING ATTRIBUTES
 * safeCountries notes which countries are internal (not on the frontline) 
 * AND not next to any frontline countries in order of the occupiedTerrotory ArrayList.
 * 
 * aloneCountries notes which countries are alone (surrounded by only enemy countries) 
 * in order of the occupiedTerrotory ArrayList.
 * 
 * frontlineCountries notes which countries have a mix of owned and enemy countries
 * next to them.
 * 
 * internalCountries notes which countries have no enemyCountry next to them.
 * 
 * largestTerritory note le plus grand territoire continu que je possede.	
 * */
	
	private ArrayList<Territory> safeCountries;
	private ArrayList<Territory> aloneCountries;
	private ArrayList<Territory> frontlineCountries;
	private ArrayList<Territory> internalCountries;
	private ArrayList<Territory> largestTerritory;
	
	public PlayerGandhi(String name) {
		super(name);
		ran = new Random();
	}
	
	/*******************************************************
	 * 
	 *  DEPLOYEMENT PHASE METHODS
	 * 
	 ******************************************************/
	
	// Picks the next non occupied territory
	public String chooseTerritory() 
	{
		if(!firstPick)
			updateTerritoryListing();
		for(Territory t : allTerritories)
		{
			if(firstPick == true)
			{
				if(t.name == "india") //Puisqu'il s'agit de Gandhi, il faut essayer de prendre l'Inde au début.
				{
					firstPick = false;
					if(!t.isOccupied){
						System.out.println("I got it!");
						return t.name;
					}
					else
					{	
						for(Territory t2 : t.adjacentTerritories)
							if(!t2.isOccupied)
							{
								return t2.name;
							}
					}
					return chooseTerritory();
				}
			}
			else if(occupiedTerritories.size() == 0 && !firstPick)	// Si par hasard, on est incapable de prendre l'inde, on essai un pays ayant moins de deux voisins.
			{
				if(t.adjacentTerritories.size() <= 2)
				{
					System.out.println(t.name);
					return t.name;
				}
			}
			else if(frontlineCountries.contains(t))	// Le reste des choix à faire
			{
				if(t.name == "india")	// Commencant par l'inde, si je l'ai.
				{
					for(Territory t2 : t.adjacentTerritories)	// On essai de prendre les terrains voisins
					{
						if(!t2.isOccupied)
							{
								return t2.name;
							}
					}
				}
				else if(!aloneCountries.contains(t))			// Si on réussi pas, on essai des terrains adjacents mais qui
				{
					for(Territory t2 : t.adjacentTerritories)	// ne sont tout de même pas seul
						{
							if(!t2.isOccupied)					
								return t2.name;
						}
				}
				else if(aloneCountries.size() == occupiedTerritories.size())	// Ici, ça signifie qu'on a rien trouver de bon donc au moins choisir
				{																// un terrain adjacent a un de nos terrain même si il est seul.
					for(Territory t2 : t.adjacentTerritories)
					{
						if(!t2.isOccupied)
						{
							return t2.name;
						}
					}
				}
			}
		}
		for(Territory t : allTerritories)
		{
			if(!t.isOccupied)
			{
				return t.name;
			}
		}
		return "error";
	}
	
	// Ceci est, en quelque sorte, mon percept() pout les territoires.
	public void updateTerritoryListing()
	{	
		safeCountries = new ArrayList<Territory>();
		aloneCountries = new ArrayList<Territory>();
		frontlineCountries = new ArrayList<Territory>();
		internalCountries = new ArrayList<Territory>();
		
		// Cette partie regarde quel pays est frontline et alone, puis, déduis donc les pays internes.
		boolean frontline, alone;
		for(Territory t : occupiedTerritories)
		{
			frontline = false;
			alone = true;
			for(Territory t2 : t.adjacentTerritories)
			{
				if(!t2.isOccupied || !(t2.getOwner().name == "Pong"))			// Change frontline si on trouve un pays qui m'appartient pas.
				{
					frontline = true;
				}
				if(t2.isOccupied && (t2.getOwner().name == "Pong"))			// Change alone si on trouve un pays adjacent qui m'appartient.
				{
					alone = false;
				}
			}
			if(frontline)
				frontlineCountries.add(t);
			else if(!frontline && !alone)
				internalCountries.add(t);
			if(alone)
				aloneCountries.add(t);
		}
		
		// Cette partie regarde quel pays sont au moins un pays plus loin que n'importe quel frontline.
		boolean isSafe;
		for(Territory t3 : internalCountries)
		{
			isSafe = true;
			for(Territory t4 : t3.adjacentTerritories)
			{
				if(frontlineCountries.contains(t4))
				{
					isSafe = false;
				}
			}
			if(isSafe)
				safeCountries.add(t3);
		}
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
	 *  REINFORCEMENT PHASE METHODS (Pre - Combat)
	 *  Assign 3 cards to be turned in for bonus units if you wish
	 *  After recieveing your units at the beginning of a turn,
	 *  place all of them as you wish by selecting one of
	 *  your territories and adding the new units (addUnits)
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

		// Remove the units that were placed from your units pool
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
	
	// Only called before preparing a combat round and after the attack, 
	// to decide if we want to attack again
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
	
	// Set [this.moveOrigin] , [this.moveDestination] , [this.moveUnits]
	// if you want to move units from one territory to another (only once per turn)
	@Override
	public void chooseMovementTerritoriesAndUnits() {
		this.moveOrigin = this.occupiedTerritories.get(ran.nextInt(occupiedTerritories.size()));
		this.moveDestination = this.moveOrigin.adjacentTerritories.get(ran.nextInt(moveOrigin.adjacentTerritories.size()));
		this.moveUnits = this.moveDestination.getUnits() - 1;
	}
}
