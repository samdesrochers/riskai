package poly.game;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class RiskGame {

	public static final int PHASE_INITIAL 		= 0;
	public static final int PHASE_TURN_BEGINS 	= 1;
	public static final int PHASE_REINFORCE		= 2;
	public static final int PHASE_ATTACK 		= 3;
	public static final int PHASE_MOVE_ARMIES 	= 4;
	public static final int PHASE_BONUS 		= 5;

	public ArrayList<Player> 				players;
	public ArrayList<Territory> 		territories;

	public Player currentPlayer;
	public int currentPlayerIndex;

	// Entry point
	public void startGame(){

		// Initial phase
		initPlayers();
		initTerritories();
		ditributeTerritories();
		placeRemainingUnits();
		
		// Game phase
		playGame();
	}

	// Initialize all territories
	public void initTerritories(){
		Map map = new Map();
		territories = map.generate();
		System.out.println("------- STARTING TERRITORIES DISTRIBUTION -------");

	}

	// Initialize players
	public void initPlayers(){
		players = new ArrayList<Player>();
		currentPlayerIndex = -1;

		Player sam 	= new PlayerSam("Sam");
		Player sam2 = new PlayerSam("Gino");
		Player sam3 = new PlayerSam("Bob");
		
		sam.occupiedTerritories = new ArrayList<Territory>();
		sam2.occupiedTerritories = new ArrayList<Territory>();
		sam3.occupiedTerritories = new ArrayList<Territory>();

		players.add(sam);
		players.add(sam2);
		players.add(sam3);
		
	}

	// Players each pick a territory one after the other
	private void ditributeTerritories(){
		Random random = new Random();

		// Random player starts choosing his territory
		currentPlayerIndex = random.nextInt(players.size());

		int startingUnits = 15;
		for(Player p : players){
			p.remainingUnits = startingUnits;
			p.allTerritories = territories;
		}

		// Choosing round if any territories not yet assigned
		while(!Map.allTerritoriesAssigned(territories)){

			currentPlayer = players.get(currentPlayerIndex);
			String territory = currentPlayer.chooseTerritory();

			// Try to acquire a chosen territory and place 1 unit on it
			if(Map.acquireTerritory(territory, currentPlayer, 1, territories)){
				System.out.println(currentPlayer.name + " selected and got " + territory);
				currentPlayer.remainingUnits -= 1;

				// Next player turn
				currentPlayerIndex = (currentPlayerIndex + 1)%players.size();
			} else {
				System.out.println(currentPlayer.name + " tried to select " + territory + " but couldn't");
			}
		}    
		System.out.println();
		System.out.println("------- ALL TERRITORIES DISTRIBUTED -------");
	}

	// Initial phase, place all remaining units until all players have 0
	private void placeRemainingUnits(){
		int remainingUnitsThisRound = 3;

		while(!allUnitsPlaced()){
			currentPlayer = players.get(currentPlayerIndex);
			
			// Check if the player still has reinforcements to place
			if(currentPlayer.remainingUnits > 0){
				
				// Selection of the territory and with how many units
				int nbReinforcement = currentPlayer.chooseNbOfUnits(remainingUnitsThisRound);
				String territory = currentPlayer.pickReinforceTerritory();
	
				// Try to reinforce
				if(Map.reinforceTerritoryWithUnits(territory, currentPlayer, nbReinforcement, territories)){
					System.out.println(currentPlayer.name + " assigned " + nbReinforcement + " units on : " + territory);
					remainingUnitsThisRound -= nbReinforcement;
	
					if(remainingUnitsThisRound == 0){
						currentPlayerIndex = (currentPlayerIndex + 1)%players.size();
						currentPlayer = players.get(currentPlayerIndex);
						if(currentPlayer.remainingUnits >= 3){
							remainingUnitsThisRound = 3;
						} else {
							remainingUnitsThisRound = currentPlayer.remainingUnits;
						}
	
					}
				} else{
					System.out.println("An error occured");
				}
			
			// No more reinforcements, player is out	
			} else { 
				currentPlayerIndex = (currentPlayerIndex + 1)%players.size();
			}
		}
		System.out.println("Initialization all done!");
		System.out.println();
		System.out.println("************* FIRST TURN ****************");
		System.out.println();
	}

	// Check if all players have placed all of their units
	boolean allUnitsPlaced(){
		if(players.get(0).remainingUnits == 0  && players.get(1).remainingUnits == 0 && players.get(2).remainingUnits == 0){
			return true;
		}
		return false;
	}

	private void playGame(){
		Scanner scan = new Scanner(System.in);
		
		// Random first turn pick
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
		currentPlayer = players.get(currentPlayerIndex);
		
		int i = 0;
		while(i < 6){
			String userInput = scan.nextLine();
			System.out.println(userInput);
				
			// Execute the turn for currentPlayer
			executeTurn();
			
			// Next player
			currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
			currentPlayer = players.get(currentPlayerIndex);
			i++;
		}
		System.out.println("Game Over!");
	}
	
	private void executeTurn(){
		// Acquire and Place new reinforcements
		executeReinforcementsPhase();
		
		// Attack other territories
		executeAttackPhase();

	}
	
	private void executeReinforcementsPhase(){
		int newReinforcements = calculateNbReinforcements();
		currentPlayer.remainingUnits = newReinforcements;
		
		while(currentPlayer.remainingUnits > 0){
			currentPlayer.assignReinforcements();
		}
		currentPlayer.printTerritories();

	}
	
	private void executeAttackPhase(){
		
		// Analyze board situation when about to enter combat (AI) 
		currentPlayer.combatAnalysis(0,0);
		
		// Check if the player wants to attack
		while(currentPlayer.isAttacking()){
	
			Territory attacker;
			
			// Try to get an attacking territory
			if((attacker = currentPlayer.getAttackingTerritory()) != null){
				
				// Get the defending territory
				Territory defender = currentPlayer.getTargetTerritory();
				
				// Get the amount of units used for this fight (MAXIMUM 3, MINIMUM 1)
				int units = currentPlayer.getNbOfAttackingUnits();
				
				// Check if number of units is legal
				if(units <= 3 && units >= 1){
					if(attacker.name != defender.name){
						int[] unitsLost;
						unitsLost = BattleManager.executeAttackPhase(attacker, defender, units);
						
						// Analyze the outcome of the last combat round (AI)
						// passing the currentPlayer lost units and the defending player lost units
						// for results analysis or else
						currentPlayer.combatAnalysis(unitsLost[1], unitsLost[0]);
					}
				} else {
					System.out.println("Too many or too few units chosen : "+units);
				}
			} else {
				System.out.println("No territory was chosen thus no attack");
			}		
		}
		System.out.println("End of the attack phase -----------------");
	}
	
	private int calculateNbReinforcements(){
		int num = 15;
		
		num += Map.getContinentReinforcements(currentPlayer.occupiedTerritories);
		
		int nbControlledTerritories = currentPlayer.occupiedTerritories.size();
		
		// Add by number of controlled territories
		if(nbControlledTerritories > 33){
			num += 7;
		} else if(nbControlledTerritories > 30 && nbControlledTerritories < 33){
			num += 6;
		} else if(nbControlledTerritories > 27 && nbControlledTerritories < 30){
			num += 5;
		} else if(nbControlledTerritories > 24 && nbControlledTerritories < 27){
			num += 4;
		} else if(nbControlledTerritories > 21 && nbControlledTerritories < 24){
			num += 3;
		} else if(nbControlledTerritories > 18 && nbControlledTerritories < 21){
			num += 2;
		} else if(nbControlledTerritories > 15 && nbControlledTerritories < 18){
			num += 1;
		}
		
		return num;
		
	}

}
