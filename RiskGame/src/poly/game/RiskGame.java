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

		initPlayers();
		initTerritories();
		ditributeTerritories();
		placeRemainingUnits();

		this.playGame();
	}

	// Initialize all territories
	public void initTerritories(){
		MapGenerator gen = new MapGenerator();
		territories = gen.generate();
	}

	// Initialize players
	public void initPlayers(){
		players = new ArrayList<Player>();
		currentPlayerIndex = -1;

		Player sam 	= new PlayerSam("Sam");
		Player sam2 = new PlayerSam("Test");
		Player sam3 = new PlayerSam("bob");
		
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

		int startingUnits = 30;
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
			if(currentPlayer.remainingUnits != 0){
				
				// Selection of the territory and with how many units
				int nbReinforcement = currentPlayer.chooseNbOfUnits(remainingUnitsThisRound);
				String territory = currentPlayer.reinforceTerritory();
	
				// Try to reinforce
				if(Map.reinforceTerritoryWithUnits(territory, currentPlayer, nbReinforcement, territories)){
					System.out.println(currentPlayer.name + " assigned " + nbReinforcement + " units on : " + territory);
					currentPlayer.remainingUnits -= nbReinforcement;
					remainingUnitsThisRound -= nbReinforcement;
	
					if(remainingUnitsThisRound == 0){
						currentPlayerIndex = (currentPlayerIndex + 1)%players.size();
						remainingUnitsThisRound = 3;
						
						if(currentPlayer.remainingUnits - remainingUnitsThisRound < 0){
							remainingUnitsThisRound = currentPlayer.remainingUnits;
						}
					}
				} else{
					System.out.println("An error occured");
				}
			// No more reinforcements, player is out	
			} else { 
				currentPlayerIndex = (currentPlayerIndex + 1)%players.size();
				for (Player p:players){
					System.out.println(p.name + " has " +p.remainingUnits + " left");
				}
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

		while(players.size() != 1){
			String userInput = scan.nextLine();
			System.out.println(userInput);
			players.remove(1);
		}
	}

}
