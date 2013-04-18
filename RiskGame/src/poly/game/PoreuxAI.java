package poly.game;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Random;

public class PoreuxAI extends Player {

	private Random ran;
	int nombreTours;
	public PoreuxAI(String name) {
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
		int r = ran.nextInt()%3;
		nombreTours = 0;
		
		//1/3 chances de commencer dans l'afrique, amérique du sud ou l'australie (le plus efficace suite à des tests)
		for(Territory t : public_allTerritories)
		{
			if(r==0)
				if(t.continent == Map.AFRICA && !t.isOccupied)
				{
					return t.name;
				}
			if(r==1)
				if(t.continent == Map.SOUTH_AMERICA && !t.isOccupied)
				{
					return t.name;
				}
			if(r==2)
				if(t.continent == Map.AUSTRALIA && !t.isOccupied)
				{
					return t.name;
				}
		}
		//si le continent choisi est plein
		for(Territory t : public_allTerritories)
		{
			if(!t.isOccupied)
			{
				return t.name;
			}
		}
		return "error";
	}

	public int chooseNbOfUnits(int remainingThisTurn) 
	{
		return remainingThisTurn;
	}
	
	
	public String pickReinforceTerritory() 
	{
		//toutes les unités de départ sont placées sur le même territoire, dans la mesure ou il a un pays voisin ennemi
		for(Territory t: myOccupiedTerritories)
		{
				for(Territory adjacentt: t.adjacentTerritories)
				{
					if(!myOccupiedTerritories.contains(adjacentt))
					{
						return t.name;
					}
				}
				
		}
		
		return "error";
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
		
		//choisit toujours de jouer une série dès qu'il en as une.
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
			} else if(art_cards.size() == 3){
				return art_cards;
			} else if(cav_cards.size() == 3){
				return cav_cards;
			} else if(inf_cards.size() == 3){
				return inf_cards;
			} 
		}
		return null;
	}
	
	//retourne le nombre de joueurs total restant dans la partie, INCLUANT soi-même
	private int nbJoueursRestants()
	{
		Vector<Player> players = new Vector<Player>();
		for(Territory t : public_allTerritories)
			if(!players.contains(t.getOwner()))
				players.add(t.getOwner());
		return players.size();
	}
	
	// REINFORCEMENT Phase, assign reinforcements as we want
	// Make sure [this.remainingUnits] reaches 0 here
	public void assignReinforcements() {

		int nbJoueurs = nbJoueursRestants();
		Territory OptimalTerritoire = null;
		int proieScore = -10000;
		for(Territory t: myOccupiedTerritories)
		{
			for(Territory tAdja: t.adjacentTerritories)
			{
				if(!myOccupiedTerritories.contains(tAdja))
				{
					//cette variable permet de prioritiser légèrement l'australie et l'amérique du sud face aux autres continents.
//					int variableInfluence = 0;
//					if(tAdja.continent == Map.AUSTRALIA)
//						variableInfluence +=5;
//					if(tAdja.continent == Map.SOUTH_AMERICA)
//						variableInfluence +=5;
					//le scorepotentiel agit comme heuristique
					int scorePotentiel = (int) (t.getUnits()- Math.pow(tAdja.getUnits(),1+((nbJoueurs-2)/2)))/*+variableInfluence*/;
					if(scorePotentiel > proieScore && (t.getUnits() + remainingUnits)< RiskGame.MAX_UNITS)
					{
						//proieScore agit comme heuristique maximale
						proieScore = scorePotentiel;
						//le terrotoire otpimal est le territoire associé à l'heuristique maximale
						OptimalTerritoire = t;
					}
				}
			}
		}
		//toutes les unités sont placées sur le territoire optimal.
		OptimalTerritoire.addUnits(remainingUnits);
		this.remainingUnits = 0;
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
	
	Territory territoireAttaquant = null;
	Territory territoireAttaquer = null;
	boolean aPrisUnPays;
	
	public void updateModel() {
		super.updateModel();
		
		Map.checkIfContinentOwned(Map.AFRICA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.NORTH_AMERICA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.EUROPE, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.ASIA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.AUSTRALIA, this.myOccupiedTerritories);
		Map.checkIfContinentOwned(Map.SOUTH_AMERICA, this.myOccupiedTerritories);
		
		int nbJoueurs = nbJoueursRestants();
		int proieScore = -10000;
		
		//Cette boucle sert a déterminer le territoire d'attaque et attaqué qui sont optimal
		for(Territory t: myOccupiedTerritories)
		{
			for(Territory tAdja: t.adjacentTerritories)
			{
				if(!myOccupiedTerritories.contains(tAdja))
				{
					//cette variable permet de prioritiser légèrement l'australie et l'amérique du sud face aux autres continents.
//					int variableInfluence = 0;
//					if(tAdja.continent == Map.AUSTRALIA)
//						variableInfluence +=5;
//					if(tAdja.continent == Map.SOUTH_AMERICA)
//						variableInfluence +=5;
					//le scorepotentiel agit comme heuristique
					int scorePotentiel = (int) (t.getUnits()- Math.pow(tAdja.getUnits(),1+((nbJoueurs-2)/2)))/*+variableInfluence*/;
					if(scorePotentiel > proieScore && t.getUnits()>2)
					{
						//proieScore agit comme heuristique maximale
						proieScore = scorePotentiel;
						//territoire cible
						territoireAttaquer = tAdja;
						//territoire source
						territoireAttaquant = t;
					}
				}
			}
		}
		
		//Si l'AI évalue pouvoir gagner la partie (son nombre total d'units > le reste de la carte)
		boolean jePeuxGagnerLaGame = false;
		int monScoretotal=0, leurScoretotal=0;
		for(Territory t : public_allTerritories)
		{
			if(myOccupiedTerritories.contains(t))
				monScoretotal += t.getUnits();
			else
				leurScoretotal += t.getUnits();	
		}
		if(monScoretotal > leurScoretotal)
			jePeuxGagnerLaGame = true;
		
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		int agressivite = 3;
		boolean agressivitePriseEnCompte = false;
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
		
		if(agressivitePriseEnCompte==false && nombreTours >1000)
			agressivitePriseEnCompte = true;
		
		if(agressivitePriseEnCompte)
		{}
		else
		{
			agressivite = 100000;
		}
		
		//Conditions d'attaque.
		if((proieScore > agressivite || nbJoueursRestants()==2 || aPrisUnPays == false || jePeuxGagnerLaGame == true) && territoireAttaquant.getUnits()>1)
		{
			this.willAttack = true;
		}
		else
			this.willAttack = false;
	}
	
	// Decides which territory to attack from and what territory to attack.  MUST be adjacent :)
	public void chooseAttackerAndTarget() 
	{
		this.target = territoireAttaquer;
		this.attacker = territoireAttaquant;
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
	public void postCombatUpdateModel(int myLostUntis, int enemyLostUnits) 
	{
		
	}

	// TO IMPLEMENT (AI) : called when our player wins a new territory (which was the targeted territory)
	public void didGainNewTerritory(Territory conqueredTerritory) {
		// Toutes les unités se déplacent de l'attaquant au pays conquis
		aPrisUnPays = true;
		conqueredTerritory.setUnits(this.attacker.getUnits() -1);
		this.attacker.setUnits(1);
	}
	
	// Set [this.moveOrigin] , [this.moveDestination] , [this.moveUnits]
	// if you want to move units from one territory to another (only once per turn)
	@Override
	public void chooseMovementTerritoriesAndUnits() 
	{
		nombreTours+=1;
		
		aPrisUnPays = false;
		int popMax = 0;
		boolean haveVoisins = false;
		Territory origineDeplacement = null;
		Territory targetDeplacement = null;
		
		//déterminer le pays d'origine
		for(Territory t: myOccupiedTerritories)
		{
			haveVoisins = false;
			for(Territory tAdja: t.adjacentTerritories)
			{
				if(!myOccupiedTerritories.contains(tAdja))
					haveVoisins = true;
			}
			if(t.getUnits()>popMax && haveVoisins == false)
			{
				origineDeplacement = t;
				popMax = t.getUnits();
			}
		}
		
		//déterminer la destination
		if(origineDeplacement!=null)
		{
			for(Territory tAdja1: origineDeplacement.adjacentTerritories)
			{
				for(Territory tAdja2: tAdja1.adjacentTerritories)
				{
					if(!myOccupiedTerritories.contains(tAdja2))
					{
						targetDeplacement = tAdja1;
						break;
					}
					
				}
				if(targetDeplacement != null)
					break;
			}
			if(targetDeplacement == null)
			{
				for(Territory tAdja1: origineDeplacement.adjacentTerritories)
				{
					for(Territory tAdja2: tAdja1.adjacentTerritories)
					{
						for(Territory tAdja3: tAdja2.adjacentTerritories)
						{
							if(!myOccupiedTerritories.contains(tAdja3))
								targetDeplacement = tAdja1;
						}
					}
				}
			}
			if(targetDeplacement == null)
			{
				for(Territory tAdja1: origineDeplacement.adjacentTerritories)
				{
					for(Territory tAdja2: tAdja1.adjacentTerritories)
					{
						for(Territory tAdja3: tAdja2.adjacentTerritories)
						{
							for(Territory tAdja4: tAdja3.adjacentTerritories)
							{
								if(!myOccupiedTerritories.contains(tAdja4))
									targetDeplacement = tAdja1;
							}
						}
					}
				}
			}
			
			this.moveOrigin =  origineDeplacement;
			this.moveDestination = targetDeplacement;
			this.moveUnits = this.moveOrigin.getUnits() - 1;
		}
	}
}
