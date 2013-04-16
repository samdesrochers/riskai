package poly.game;

import java.util.ArrayList;
import java.util.Random;

public class HugoAI extends Player
{

  public static int m_numeroTerritoireARenforcir;
	private ArrayList<String> m_listeTerritoiresVoulusInitial;
	
	
	public HugoAI(String name)
	{
		super(name);
		
		m_listeTerritoiresVoulusInitial = new ArrayList<String>();
		
		m_listeTerritoiresVoulusInitial.add("argentina");
		m_listeTerritoiresVoulusInitial.add("peru");
		m_listeTerritoiresVoulusInitial.add("south_africa");
		m_listeTerritoiresVoulusInitial.add("madagascar");
		m_listeTerritoiresVoulusInitial.add("brazil");
		m_listeTerritoiresVoulusInitial.add("venezuela");
		m_listeTerritoiresVoulusInitial.add("cent_america");
		m_listeTerritoiresVoulusInitial.add("western_us");
		m_listeTerritoiresVoulusInitial.add("eastern_us");
		
		m_numeroTerritoireARenforcir = 0;
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
	public String chooseTerritory()
	{
		String territoireVoulu = "";
		int territoireARegarder = 0;

		while (territoireVoulu.isEmpty())
		{
			// si ma liste de territoires en contient encore
			if(territoireARegarder < public_allTerritories.size())
			{
				// on prend le territoire de la liste a chercher
				String name = public_allTerritories.get(territoireARegarder).name;
				
				for(Territory t : public_allTerritories)
				{
					// si c'est le territoire recherche
					if(t.name == name)
					{
						// s'il n'est pas occupe on le prend
						if(!t.isOccupied)
						{
							territoireVoulu = t.name;
						}
						// sinon on arrete immediatement la boucle pour recommencer
						// en enlevant le territoire qui etait recherche
						else
						{
							territoireARegarder++;
						}
						
						// sort de la boucle pour faire recommencer le while
						// si le territoire etait trouve il arretera, sinon il
						// recommence la recherche
						break;
					}
				}
			}
			// si la liste est vide on prend au hasard le 1er territoire libre
			else
			{
				for(Territory t : public_allTerritories)
				{
					if(!t.isOccupied)
					{
						territoireVoulu = t.name;
					}
				}
			}
			
		}
		
		
		return territoireVoulu;
	}

	// Choose a number of units to place on a territory during
	// the initial deployment phase. Must use between 1 and 3 units
	@Override
	public int chooseNbOfUnits(int remainingThisTurn)
	{
		if(remainingThisTurn < 2)
		{
			return remainingThisTurn;
		}
		else
		{
			return 2;
		}
	}

	// Choose a territory to reinforce with 1 to 3 units
	// Returns the territory to reinforce
	@Override
	public String pickReinforceTerritory() {
		// TODO Auto-generated method stub
		int territoire = m_numeroTerritoireARenforcir;
		
		m_numeroTerritoireARenforcir++;
		
		// on verifie que m_numeroTerritoireARenforcir ne sort pas hors domaine
		if(m_numeroTerritoireARenforcir >= this.myOccupiedTerritories.size())
		{
			m_numeroTerritoireARenforcir = 0;
		}
		
		return myOccupiedTerritories.get(territoire).name;
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
	
	// Make sure [this.remainingUnits] reaches 0 here by assigning
	// all of your received units to a territory you own in [this.myOccupiedTerritories]
	@Override
	public void assignReinforcements()
	{
		// TODO Auto-generated method stub
		
		if(remainingUnits == 0)
		{
			return;
		}
		
		int unitesParTerritoire = (int)Math.ceil((double)remainingUnits / (double)myOccupiedTerritories.size());

		for(Territory t : this.myOccupiedTerritories)
		{
			// s'il y a le nombre souhaiter d'unite on les met
			if(remainingUnits >= unitesParTerritoire)
			{
				t.addUnits(unitesParTerritoire);
				remainingUnits -= unitesParTerritoire;
			}
			// sinon on met celles restantes
			else
			{
				t.addUnits(remainingUnits);
				remainingUnits = 0;				
				break;
			}
			
		}

	}

	@Override
	public ArrayList<Card> tradeCards()
	{
		if(this.cards.size() >= 3)
		{
			ArrayList<Card> inf_cards = new ArrayList<Card>();
			ArrayList<Card> cav_cards = new ArrayList<Card>();
			ArrayList<Card> art_cards = new ArrayList<Card>();
			
			for(int i = 0; i < this.cards.size(); i++)
			{
				Card card = this.cards.get(i);
				if(card.type == Card.TYPE_INFANTRY)
				{
					inf_cards.add(card);
				}
				else if(card.type == Card.TYPE_CAVALRY)
				{
					cav_cards.add(card);
				}
				else if(card.type == Card.TYPE_ARTILERY)
				{
					art_cards.add(card);
				} 
			}
			
			// on verifie si ya possibilit̩ d'une carte de chaque pcq
			// ca vaut plus d'armees
			if(inf_cards.size() >= 1 && cav_cards.size() >= 1 && art_cards.size() >= 1)
			{
				ArrayList<Card> carteToutesDifferentes = new ArrayList<Card>();
				
				carteToutesDifferentes.add(inf_cards.get(0));
				carteToutesDifferentes.add(cav_cards.get(0));
				carteToutesDifferentes.add(art_cards.get(0));
				
				return carteToutesDifferentes;
			}
			
			// cavalier en premier pcq ca vaut plus d'unites
			if(cav_cards.size() == 3)
			{
				return cav_cards;
			}
			// ensuite l'infanterie
			else if(inf_cards.size() == 3)
			{
				return inf_cards;
			}
			// finalement l'artillerie
			else if(art_cards.size() == 3)
			{
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

	public void updateModel()
	{
		super.updateModel();
		
		int nombreUniteCible = 1000;
		
		int differenceUniteUneUnite = 0;
		Territory attaquantPossiblePourUneUnite = null;
		Territory ciblePossibleAyantUneUnite = null;
		
		Boolean cibleTrouveSuivantRegle = false;
		
		target = null;
		attacker = null;
		
		// la recherche cherche un territoire ou la difference d'unite entre l'attaquant et la cible respecte la regle de 1.5 superieur
		for(Territory monTerritoire : this.myOccupiedTerritories)
		{
			int nbrUnites = monTerritoire.getUnits();
			
			if(nbrUnites > 1)
			{
				for(Territory territoireVoisin : monTerritoire.adjacentTerritories)
				{
					if(estAMoi(territoireVoisin))
					{
						continue;
					}
					int diff = monTerritoire.getUnits() - territoireVoisin.getUnits();
					
					// si mon territoire a 1.5 fois le nombre d'unites de la cible et que le nombre d'unite ennemis est inferieur a l'ancienne valeur
					if (territoireVoisin.getUnits() <= 1.5 * monTerritoire.getUnits() + 1 && (territoireVoisin.getUnits() < nombreUniteCible))
					{
						nombreUniteCible = territoireVoisin.getUnits();
						attacker = monTerritoire;
						target = territoireVoisin;
						willAttack = true;
						
						cibleTrouveSuivantRegle = true;
					}
					
					// on cherche le plus gros diferentiel ou le territoire ennemi n'a qu'une armee dessus
					if(territoireVoisin.getUnits() == 1 && diff > differenceUniteUneUnite)
					{
						attaquantPossiblePourUneUnite = monTerritoire;
						ciblePossibleAyantUneUnite = territoireVoisin;
						differenceUniteUneUnite = diff;
					}
				}
			}
		}
		
		
		// si aucun territoire respectait la regle, on va essaye d'aller chercher un territoire ou il n'y a qu'une armee
		// s'il y avait un tel territoire
		if(!cibleTrouveSuivantRegle && attaquantPossiblePourUneUnite != null && ciblePossibleAyantUneUnite != null)
		{
			attacker = attaquantPossiblePourUneUnite;
			target = ciblePossibleAyantUneUnite;
			willAttack = true;
		}
		
		if(attacker == null || target == null)
		{
			willAttack = false;
		}
	}
	
	private Boolean estAMoi(Territory territoire)
	{
		return territoire.getOwner().name == name;
	}
	
	@Override
	protected void chooseAttackerAndTarget()
	{
		
	}

	@Override
	protected void chooseAttackingUnits()
	{
		if(attacker == null || target == null)
		{
			this.attackingUnits = 0;
		}
		else if(this.attacker.getUnits() > 3 )
		{
			this.attackingUnits = 3;
		}
		else if(this.attacker.getUnits() == 3 )
		{
			this.attackingUnits = 2;
		}
		// si la cible ne lui reste qu'une unite, je met les 2 pour augmenter mes chances
		else if(this.attacker.getUnits() == 2  && this.target.getUnits() == 1)
		{
			this.attackingUnits = 2;
		}
		// si la cible a 2 armees, je n'en met qu'une pour garder mon territoire
		else if(this.attacker.getUnits() == 2  && this.target.getUnits() >= 2)
		{
			this.attackingUnits = 1;
		}
		else if(this.attacker.getUnits() == 1 )
		{
			this.attackingUnits = 0; // Attack cancelled
		}
	}

	// Analyze combat outcome, if required
	@Override
	public void postCombatUpdateModel(int myLostUntis, int enemyLostUnits)
	{
		
	}

	@Override
	public void didGainNewTerritory(Territory conqueredTerritory)
	{		
		// le territoire qui attaquait a au moins 1 territoire ennemi encore
		// je partage donc les armees en 2
		if(territoireToucheEnemies(conqueredTerritory))
		{
			int nbrUnites = attacker.getUnits() / 2;
			conqueredTerritory.setUnits(this.attacker.getUnits() - nbrUnites);
			this.attacker.setUnits(nbrUnites);
		}
		// le territoire n'a plus aucun voisin ennemi, j'envoie donc toutes
		// les armees sur le nouveau
		/*else if(attacker.getUnits() > 2)
		{
			conqueredTerritory.setUnits(this.attacker.getUnits() - 2);
			this.attacker.setUnits(2);
		}*/
		else
		{
			conqueredTerritory.setUnits(this.attacker.getUnits() - 1);
			this.attacker.setUnits(1);
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
	public void chooseMovementTerritoriesAndUnits()
	{
		Territory de = null;
		Territory a = null;
		Territory aTemp = null;
		
		// on parcourt mes territoires
		for(Territory source : myOccupiedTerritories)
		{
			// si le territoire n'a pas plus qu'une armee c'est clair qu'on pourra pas en bouger
			if(source.getUnits() > 1)
			{
				// si le territoire n'est pas a la frontiere
				if(!territoireToucheEnemies(source))
				{
					int profondeur = 1;
					
					// on va chercher le territoire de frontiere le plus pres
					while(aTemp == null)
					{
						aTemp = trouverTerritoirePlusPresDeLaFrontiere(source, profondeur);
						profondeur++;
					}
					
					de = source;
					a = aTemp;
				}
			}
		}
		
		if(a != null && de != null)
		{
			// on procede au mouvement des armees
			int nbr = de.getUnits() - 1;
			
			a.addUnits(nbr);
			de.setUnits(1);
			
			
		}
	}
	
	private Territory trouverTerritoirePlusPresDeLaFrontiere(Territory source, int profondeur)
	{
		Territory destination = null;
		
		ArrayList<String> territoireVerifie = new ArrayList<String>();
		
		int nbrUnite = 0;
		
		switch(profondeur)
		{
		// recherche en une de profondeur
		case(1):
			for(Territory t : source.adjacentTerritories)
			{
				if(territoireToucheEnemies(t) && t.getUnits() > nbrUnite)
				{
					destination = t;
					nbrUnite = t.getUnits();
				}
			}
			break;
			// recherche avec une profondeur de 2
		case(2):
			for(Territory t : source.adjacentTerritories)
			{
				if(territoireVerifie.contains(t.name))
				{
					continue;
				}
				else
				{
					territoireVerifie.add(t.name);
				}
				for(Territory t2 : t.adjacentTerritories)
				{
					if(territoireVerifie.contains(t2.name))
					{
						continue;
					}
					else
					{
						territoireVerifie.add(t2.name);
					}
					
					if(t2 != source && territoireToucheEnemies(t2) && t2.getUnits() > nbrUnite)
					{
						destination = t;
						nbrUnite = t2.getUnits();
					}
				}
			}
			break;
			// recherche avec une profondeur de 3
		case(3):
			for(Territory t : source.adjacentTerritories)
			{
				if(territoireVerifie.contains(t.name))
				{
					continue;
				}
				else
				{
					territoireVerifie.add(t.name);
				}
				for(Territory t2 : t.adjacentTerritories)
				{
					if(territoireVerifie.contains(t2.name))
					{
						continue;
					}
					else
					{
						territoireVerifie.add(t2.name);
					}
					for(Territory t3 : t2.adjacentTerritories)
					{
						if(territoireVerifie.contains(t3.name))
						{
							continue;
						}
						else
						{
							territoireVerifie.add(t3.name);
						}
						if(t3 != source && territoireToucheEnemies(t3) && t3.getUnits() > nbrUnite)
						{
							destination = t;
							nbrUnite = t3.getUnits();
						}
					}
				}
			}
			break;
			// s'il est rendu a 4, on ne calcul plus on y va au hasard
		default:
			int rand = new Random().nextInt(source.adjacentTerritories.size());
			
			destination = source.adjacentTerritories.get(rand);
			break;
		}
		
		return destination;
	}
	
	private Boolean territoireToucheEnemies(Territory t)
	{
		for(Territory destination : t.adjacentTerritories)
		{
			// si le voisin de l'attaquant n'est pas a moi
			if(!estAMoi(destination))
			{
				return true;
			}
		}
		
		return false;
	}

}