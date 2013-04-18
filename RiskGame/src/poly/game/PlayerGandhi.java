package poly.game;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


public class PlayerGandhi extends Player {
	
	private Random ran;
	static boolean firstPick = true;
	boolean firstPlacement = true;
	boolean deplacementTerminer = true;
	String bestIsolatedStart = null;
	Territory bestAssaultFrontline = null; // Might use this to determine the best territory to place excessive troops 
										   // so that they may gain the most territories.
	Territory lastBestOrigin = null;
	
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
	

	//private CopyOnWriteArrayList<Territory> disabledCountries; // Countries who are going to be unable to act this turn.
	private CopyOnWriteArrayList<Territory> checkedCountries; // Checked countries used for when looking further ahead of the enemy lines.
	private ArrayList<Territory> safeCountries;
	private ArrayList<Territory> aloneCountries;
	private ArrayList<Territory> frontlineCountries;
	private ArrayList<Territory> internalCountries;
	//private ArrayList<Territory> largestTerritory;
	
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
		for(Territory t : public_allTerritories)
		{
			if(firstPick == true)
			{
				if(t.name == "india") //Puisqu'il s'agit de Gandhi, il faut essayer de prendre l'Inde au début.
				{
					firstPick = false;
					if(!t.isOccupied){
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
			else if(myOccupiedTerritories.size() == 0 && !firstPick)	// Si par hasard, on est incapable de prendre l'inde, on essai un pays ayant moins de deux voisins.
			{
				if(t.adjacentTerritories.size() <= 2)
				{
					if(!t.isOccupied)
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
				else if(aloneCountries.size() == myOccupiedTerritories.size())	// Ici, ça signifie qu'on a rien trouver de bon donc au moins choisir
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
		for(Territory t : public_allTerritories)
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
		for(Territory t : myOccupiedTerritories)
		{
			frontline = false;
			alone = true;
			for(Territory t2 : t.adjacentTerritories)
			{
				if(!t2.isOccupied || !(t2.getOwner().name == "Pong")) // Change frontline si on trouve un pays qui m'appartient pas.
				{
					frontline = true;
				}
				if(t2.isOccupied && (t2.getOwner().name == "Pong"))	 // Change alone si on trouve un pays adjacent qui m'appartient.
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
	
	public void chooseBestAssaultFronline() {
		// This part will try to determine de best front line to attack if I have a internal countries
		// Otherwise, it will determine the best front line to choose from countries that are not alone
		// In case I only have alone countries, it will choose the best one it can.
		// The best would be the one that returns the lowest score
		// It would look in twice in it's non friendly adjacent countries adding those countries units to the score
		// the second layer of country units would count for half.
		// I should also probably divide by the number of countries visited since not all countries have the same
		// number of adjacent countries...
		double score;
		double bestScore = 10000.00;
		int countryCount = 0;
		if(internalCountries.size()!=0 )
		{
			for(Territory t : frontlineCountries)
			{
				score = 0;
				checkedCountries = new CopyOnWriteArrayList<Territory>();
				if(!(aloneCountries.contains(t)))
				{
					for(Territory t2 : t.adjacentTerritories)
					{
						if(!(myOccupiedTerritories.contains(t2)))
						{
							score += t2.getUnits();
							countryCount++;
							checkedCountries.add(t2);
						}
					}
				}
				for(Territory t3 : checkedCountries)
				{
					for(Territory t4 : t3.adjacentTerritories)
					{
						if( !checkedCountries.contains(t4) || !myOccupiedTerritories.contains(t4) )
						{
							score += 0.5*(t4.getUnits());
							countryCount++;
							checkedCountries.add(t4);
						}
					}
				}
				if(score/countryCount < bestScore)
				{
					bestScore = score/countryCount;
					bestAssaultFrontline = t;
				}
			}
		}
		else
		{
			for(Territory t : myOccupiedTerritories)
			{
				score = 0;
				if(aloneCountries.contains(t) && myOccupiedTerritories.size()>6)
				{				// I think reinforcing the main stucture would be the most important
					score += 3;	// But I think the handicap is small enough so that the AI sees
				}				// a opportunity if there is a big one. Also, it shouldn't be important
				// if I have less than 5 countries.
				checkedCountries = new CopyOnWriteArrayList<Territory>();
				if(!(aloneCountries.contains(t)))
				{
					for(Territory t2 : t.adjacentTerritories)
					{
						if(!(myOccupiedTerritories.contains(t2)))
						{
							score += t2.getUnits();
							countryCount++;
							checkedCountries.add(t2);
						}
					}
				}
				for(Territory t3 : checkedCountries)
				{
					for(Territory t4 : t3.adjacentTerritories)
					{
						if( !checkedCountries.contains(t4) || !myOccupiedTerritories.contains(t4) )
						{
							score += 0.5*(t4.getUnits());
							countryCount++;
							checkedCountries.add(t4);
						}
					}
				}
				if(score/countryCount < bestScore)
				{
					bestScore = score/countryCount;
					bestAssaultFrontline = t;
				}
			}
		}
	}
	
	public int chooseNbOfUnits(int remainingThisTurn) {
		// Random nb of units between 1 and the remaining units placable this turn (3 being the max)
		int r = ran.nextInt(remainingThisTurn) + 1; 
		return r;
	}
	
	public String pickReinforceTerritory() {
		updateTerritoryListing();
		if(internalCountries.size()>0 || myOccupiedTerritories.size()!=aloneCountries.size())
		{
			String bestFrontlineChoice = "Nothing";
			double bestScore = 1000;	// The best score will likely be the lowest value
			double score;				// The metric would start at the countries
			double dangerScore;			// current number of units
			for(Territory t : frontlineCountries)
			{
				if(!(t.getUnits()>7))
				{
					//////////////////////////////////
					//Début de la meilleur situation//
					//////////////////////////////////
					
					/* La meilleur étant celle ou on a la chance de
					 * renforcer des frontières car on a un suffisament
					 * gros territoire.
					 */
					
					score = 0;
					dangerScore = 0;
					score += t.getUnits();
					for(Territory t2 : t.adjacentTerritories)
					{
						if(!(t2.getOwner().name == "Pong"))
						{
							if(t2.getUnits() <= 2)
							{
								dangerScore += -1;	// I would probably attack these or they are going to be attacked anyway.
							}
							else if(t2.getUnits() == 3)
							{
								dangerScore += 0;	// 3 units does not signify a great enough threat.
							}
							else if(t2.getUnits() < 8)
							{
								dangerScore += t2.getUnits() - 3;	// Can attack with an advantage, to be considered.
							}
							else
							{
								dangerScore += -t2.getUnits() + 3;	// the reason behind this is that a
							}										// big army could be a serious threat and
						}											// any more army would be a waste against them.
					}
					score -= 0.2*dangerScore;
					
					if(bestScore > score)
					{
						bestScore = score;
						bestFrontlineChoice = t.name;
					}
				}
			}
			
			// Apres la boucle qui verifie le "meilleur" territoire
			// Envoi du choix
			if(bestFrontlineChoice == "Nothing")
			{
				double bestInternalScore = 1000;
				double internalScore = 0;
				String bestInternalChoice = "Nothing";
				// In case I haven't found any that suit my need
				// I will check to reinforce internal countries that aren't "safe" countries.
				for(Territory t : internalCountries)
				{
					internalScore = 0;
					internalScore += t.getUnits();
					for(Territory t2 : t.adjacentTerritories)
					{
						if(frontlineCountries.contains(t2))
						{
							internalScore += t2.getUnits();
							for(Territory t3 : t.adjacentTerritories)
							{
								if(t3.getOwner().name != "Pong")
								{
									if(t3.getUnits()<6)
										internalScore -= t3.getUnits()*0.40;
									else
										internalScore -= t3.getUnits();
								}
							}
							
						}
					}
					if(internalScore<bestInternalScore)
					{
						bestInternalScore = internalScore;
						bestInternalChoice = t.name;
					}
				}
				return bestInternalChoice;
			}
			else
				return bestFrontlineChoice;
		}
		/////////////////////////////
		//Fin de meilleur situation//
		/////////////////////////////
		else // If there are mostly isolated countries for the first reinforcement of the game, then it may be best to pick
		{    // a single country surround with the least amount of ennemies and thus allowing easy expansion
			double score;
			double enemyCount = 0;
			double bestScore = 1000;
			for(Territory t : myOccupiedTerritories)
			{
				score = 0;
				if(t.name == "india")
				{
					score -= 3;
				}
				for(Territory t2 : t.adjacentTerritories)
				{
					enemyCount = 0;
					if(t2.getOwner().name!="Pong")
					{
						enemyCount++;
						score+=t2.getUnits();
					}
				}
				if(enemyCount != 0 && score/enemyCount<bestScore)
				{
					bestScore = score/enemyCount;
					bestIsolatedStart = t.name;
				}
			}
			return bestIsolatedStart;
		}
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
	// I will probably make my choice one by one like how I would
	// do it if I played.
	// It's going to be a loooooong game.
	public void assignReinforcements() {
		if(firstPlacement)			// I want to place at least one unit on india if I have it to keep it strong
		{							// Little by little.
			updateTerritoryListing();
			chooseBestAssaultFronline();
			firstPlacement = false;	// Since I likely won't be moving units in and out of it.
			for(Territory t : myOccupiedTerritories)
			{
				if(t.name == "india")
				{
					System.out.println("Pong chooses to place a unit on India");
					t.addUnits(1);
					this.remainingUnits -= 1;
				}
				if(myOccupiedTerritories.contains(bestAssaultFrontline))
				{
					bestAssaultFrontline.addUnits(1);
					this.remainingUnits -= 1;
				}
			}
		}
		// Very similar to how the first placement was made, reinforcing territories takes
		// the amount of units I have into account, my occupied territories and my ennemies on the frontline.
		if(internalCountries.size()>0)
		{
			Territory bestFrontlineChoice = null;
			Territory bestInternalChoice = null;
			double bestScore = 1000;	// The best score will likely be the lowest value
			double score;				// The metric would start at the countries
			double dangerScore;			// current number of units
			for(Territory t : frontlineCountries)
			{
				if(!(t.getUnits()>8) || myOccupiedTerritories.size() < 4 || myOccupiedTerritories.size() > 15) //Je ne souhaite pas mettre tout mes forces uniquement sur les frontieres
				{														  //Sauf si j'ai quand meme des frontieres mais peu de pays ou beaucoup de pays donc pourrais nécessité plus de troupe
					score = 0;
					dangerScore = 0;
					score += t.getUnits();
					for(Territory t2 : t.adjacentTerritories)
					{
						if(!(t2.getOwner().name == "Pong"))
						{
							if(t2.getUnits() <= 2)
							{
								dangerScore += -1;	// I would probably attack these or they are going to be attacked anyway.
							}
							else if(t2.getUnits() == 3)
							{
								dangerScore += 0;	// 3 units does not signify a great enough threat.
							}
							else if(t2.getUnits() < 8)
							{
								dangerScore += t2.getUnits() - 3;	// Can attack with an advantage, to be considered.
							}
							else
							{
								dangerScore += -t2.getUnits() + 3;	// the reason behind this is that a
							}										// big army could be a serious threat and
						}											// any more army would be a waste against them.
					}
					score -= 0.2*dangerScore;
					
					if(bestScore > score)
					{
						bestScore = score;
						bestFrontlineChoice = t;
					}
				}
			}
			if(bestFrontlineChoice!=null)
			{
				System.out.println("Pong chooses to place a unit on " + bestFrontlineChoice.name);
				bestFrontlineChoice.addUnits(1);
				this.remainingUnits -= 1;
			}
			else //Si on a des frontlines mais qu'ils sont assez fortifier pour pouvoir renforcer les internes
			{
				if(this.remainingUnits > 0)
				{
					for(Territory t : internalCountries)
					{
						score = 0;
						score += t.getUnits();
						for(Territory t2 : t.adjacentTerritories)
						{
							if(frontlineCountries.contains(t2))
							{
								score += 0.3*t2.getUnits();
							}
						}
						if(score < bestScore)
						{
							bestScore = score;
							bestInternalChoice = t;
						}
					}
					if(bestInternalChoice != null) //Ceci ne devrais jamais arriver puisque je ne limite aucunement la des internes
					{
						bestInternalChoice.addUnits(1);
						this.remainingUnits -=1;
					}
					else						   //C'est juste au cas ou...
					{
						int rt = ran.nextInt(myOccupiedTerritories.size());
						int ru = 0;
						// random nb of units

						ru = ran.nextInt(this.remainingUnits) + 1;
					

						// assign random nb of units on the random territory
						Territory pick = myOccupiedTerritories.get(rt);
						pick.addUnits(ru);
						// Remove the units that were placed from your units pool
						this.remainingUnits -= ru;
					}
				}
			}
		}
		else if(aloneCountries.size() == myOccupiedTerritories.size()) 	//Dans le cas ou on a seulement des pays seuls, je vais me concentrer sur le plus probable
		{															 	// de faire des gains.
			if(this.remainingUnits > 0)
			{
				bestAssaultFrontline.addUnits(1);
				this.remainingUnits -= 1;
			}
		}
		else
		{
			if(this.remainingUnits > 0)
			{
				int rt = ran.nextInt(myOccupiedTerritories.size());
				int ru = 0;
				// random nb of units

				ru = ran.nextInt(this.remainingUnits) + 1;

				// assign random nb of units on the random territory
				Territory pick = myOccupiedTerritories.get(rt);
				pick.addUnits(ru);
				// Remove the units that were placed from your units pool
				this.remainingUnits -= ru;
			}
		}
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
		updateTerritoryListing();
		Map.checkIfContinentOwned(Map.AFRICA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.NORTH_AMERICA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.EUROPE, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.ASIA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.AUSTRALIA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.SOUTH_AMERICA, this.myOccupiedTerritories);
		this.willAttack = false;
		for(Territory t : frontlineCountries) // Je vais faire en sorte qu<il y est différente raison qu'on décide de vouloir attaquer.
		{										 // De plus, je vais prendre note des pays qui ne peuvent définitivement pas attaquer (ou effectuer un deplacement)
			if(t.getUnits() > 1)
			{
				for(Territory t2 : t.adjacentTerritories)
				{
					if(!myOccupiedTerritories.contains(t2))
					{
						if(t.getUnits() > 3 && t.getUnits() > t2.getUnits()) // If I have more than enough units and still more then him, I should attack.
							this.willAttack = true;
						else if(t.getUnits() > t2.getUnits()) // If the unit count is low but still have more than him, it would be best to attack.
							this.willAttack = true;
						else if(t.getUnits() == t2.getUnits()) // Equal would put me at a disadvantage so I randomise my choice.
						{
							if(ran.nextInt(3) > 2)
							{
								this.willAttack = true;
							}
						}
						else if(t.getUnits() < t2.getUnits() && t.getUnits() > 3) // Here I hesitate to attack the larger force but do so if I have more then 4 units.
						{
							if(ran.nextInt(4) > 3)
							{
								this.willAttack = true;
							}
						}
					}
				}
			}
		}
		int count = 0;
		for(Territory t : myOccupiedTerritories)
		{
			count += t.getUnits();
		}
		if(count < myOccupiedTerritories.size()*1.7)
			this.willAttack = false;
	}

	// Decides which territory to attack from and what territory to attack.  MUST be adjacent :)
	public void chooseAttackerAndTarget() {			
		updateTerritoryListing();
		firstPlacement = true;
		Territory bestTarget = null;
		int bestTargetsUnits = 400;
		
	    for(Territory t : frontlineCountries)
		{
	    	bestTarget = null;
			bestTargetsUnits = 400;
			if(t.getUnits()>1) // On regarde tout notre frontline
			{								   // pour les pays validé d'attaquer. (Ou plutot non disabled)
				for(Territory t2 : t.adjacentTerritories)
				{
					if(t2.getOwner().name != "Pong")
					{
						if(t2.getUnits() < bestTargetsUnits)
						{
							bestTargetsUnits = t2.getUnits();
							bestTarget = t2;
						}
					}
				}
				if(bestTarget != null)
				{
					if(t.getUnits() == 2 && bestTargetsUnits != 1)
						continue;
					this.attacker = t;
					this.target = bestTarget;
				}
			}
		}
		
		// Code abandonné
		/*for(Territory t3 : bestTarget.adjacentTerritories)
		{
			numberOfEnemy_t3 = 0;
			if(frontlineCountries.contains(t3) && t3!=t)
			{
				for(Territory t4 : t3.adjacentTerritories)
				{
					if(t4.getOwner().name != "Pong")
						numberOfEnemy_t3 += 1;
				}
				if(bestTarget.getUnits() < t3.getUnits() && bestTarget.getUnits() < t.getUnits()) // On doit choisir le meilleur pour cette situation.
				{																				  // Ici on va prendre celui qui a le moins d'unité si et seulement si
					if(numberOfEnemy_t != 1)													  // celui avec le plus d'unité n'a pas juste enemy
						this.attacker = t3;
				}
				else if(bestTarget.getUnits() > t3.getUnits() && bestTarget.getUnits() > t.getUnits())
				{
					
				}
				else if(bestTarget.getUnits() > t3.getUnits() && bestTarget.getUnits() > t.getUnits())
				{
					
				}
			}
			
			while(numberTerritoriesChecked < myOccupiedTerritories.size())
		{

			bestTargetsUnits = 400;
			
			// On essai les frontlines un a un.
			Territory attacker = (myOccupiedTerritories.get(numberTerritoriesChecked));
			// Check all adjacent territories and try to find an enemy and then the best choice.
			for(Territory t : attacker.adjacentTerritories)
			{
				if(t.getOwner().name != attacker.getOwner().name && attacker.getUnits() > 1)
				{
					if(t.getUnits() < bestTargetsUnits)
					{
						bestTargetsUnits = t.getUnits();
						bestTarget = t;
					}
				}
			}
			if(bestTarget != null)
			{
				this.target = bestTarget;
				this.attacker = attacker;
			}
			numberTerritoriesChecked ++;
		}
		}*/
		
	}

	// Decides how many units to send for this round (MAX is 3, minimum is 1, 0 cancels the attack)
	public void chooseAttackingUnits() {
		// Attack with full capacity without leaving the territory empty
		/*if(this.attacker.getUnits() >= 6 ){
			this.attackingUnits = 3;
		} else if(this.attacker.getUnits() >= 5 ){
			this.attackingUnits = 2;
		} else if(this.attacker.getUnits() >= 3 ){
			this.attackingUnits = 1;
		} else {
			this.attackingUnits = 0; // Attack cancelled
		}*/
		if(this.attacker.getUnits() >= 6){
			this.attackingUnits = 3;
		} else if(this.attacker.getUnits() >= 4 ){
			this.attackingUnits = 2;
		} else if(this.attacker.getUnits() >= 3 || this.target.getUnits()==1 ){
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
		Territory bestOrigin = null;
		//Territory bestDestination = null;
		if(!myOccupiedTerritories.contains(lastBestOrigin)) // Au cas ou on a perdu notre terrain :<
			deplacementTerminer = true;
		
		if(deplacementTerminer)
		{
			if(internalCountries.size()>0 && safeCountries.size() == 0) // Deplacement simple quand on il n'y a pas de pays "safe"
			{
				int score = 0;
				for(Territory t : internalCountries)
				{
					if(t.getUnits()>2 && t.getUnits()>score)
					{
						score = t.getUnits();
						bestOrigin = t;
					}
				}
				this.moveOrigin = bestOrigin;
				score = 1000;
				if(bestOrigin != null){
				for(Territory t2 : bestOrigin.adjacentTerritories)
				{
					if(t2.getUnits() < score && t2.getOwner().name == "Pong")
					{
						score = t2.getUnits();
						this.moveDestination = t2;
					}
				}
				this.moveUnits = bestOrigin.getUnits() - 2;
				}
			}
			else if(internalCountries.size() == 0) // Deplacement d'uniformisation dans le cas ou on a pas de pays interne.
			{
				int score = 0;
				for(Territory t : frontlineCountries)
				{
					if(t.getUnits()>4 && t.getUnits()>score && !aloneCountries.contains(t))
					{
						score = t.getUnits();
						bestOrigin = t;
					}
				}
				this.moveOrigin = bestOrigin;
				score = 1000;
				if(bestOrigin != null){
				for(Territory t2 : bestOrigin.adjacentTerritories)
				{
					if(t2.getUnits() < score && t2.getOwner().name == "Pong")
					{
						score = t2.getUnits();
						this.moveDestination = t2;
					}
				}
				this.moveUnits = bestOrigin.getUnits() - this.moveDestination.getUnits() - 2;
				}
			}
			else
			{
				deplacementTerminer = false;
				int score = 0;
				for(Territory t : safeCountries)
				{
					if(t.getUnits()>2 && t.getUnits()>score)
					{
						score = t.getUnits();
						bestOrigin = t;
					}
				}
				score = 1000;
				this.moveOrigin = bestOrigin;
				if(bestOrigin != null){
				for(Territory t2 : bestOrigin.adjacentTerritories)
				{
					if(t2.getUnits() < score && t2.getOwner().name == "Pong")
					{
						score = t2.getUnits();
						this.moveDestination = t2;
						lastBestOrigin = t2;
					}
				}
				this.moveUnits = bestOrigin.getUnits() - 2;
				}
			}
		}
		else
		{	
			for(Territory t : lastBestOrigin.adjacentTerritories)
			{
				if(frontlineCountries.contains(t))
				{
					bestOrigin = t;
					deplacementTerminer = true;
				}
				else if(!safeCountries.contains(t) && !frontlineCountries.contains(bestOrigin))
					bestOrigin = t;
				else if(bestOrigin == null)
					bestOrigin = t;
			}
			this.moveOrigin = lastBestOrigin;
			this.moveDestination = bestOrigin; // Best origin devrait peut-etre best destination.. erreur de ma part
			lastBestOrigin = bestOrigin;
			this.moveUnits = this.moveOrigin.getUnits() - 3;
		}
	}
}
