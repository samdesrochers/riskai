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

	public ArrayList<Player> 			players;
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

		int startingUnits = 35;
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
						remainingUnitsThisRound = 3;
						
						if(currentPlayer.remainingUnits - remainingUnitsThisRound <= 0){
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
		while(i < 3){
			//String userInput = scan.nextLine();
			//System.out.println(userInput);
				
			// Execute the turn for currentPlayer
			executeTurn();
			
			// Next player
			currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
			currentPlayer = players.get(currentPlayerIndex);
			i++;
		}
	}
	
	private void executeTurn(){
		
		int newReinforcements = calculateNbReinforcements();
		currentPlayer.remainingUnits = newReinforcements;
		
		while(currentPlayer.remainingUnits > 0){
			// Place all new units
			currentPlayer.assignReinforcements();
		}
		currentPlayer.printTerritories();

	}
	
	private int calculateNbReinforcements(){
		int num = 3;
		
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
