package poly.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**********************************************************
 * 					  - RISK AI - 
 * 
 * School project developed for Ecole Polytechnique Montreal's
 * INF4215 Class : Artificial Intelligence.
 * 
 * This program implements the traditional RISK board game
 * while using intelligent agents to play the game.  The user
 * only launches the program and lets those AIs play against 
 * each other.  Each student was tasked to write its own
 * AI so we could host a final competition by the end of the 
 * project.  
 * 
 * Some rules may differ from the original Official Rules. 
 * 
 * 
 * Author : Samuel Des Rochers
 *  
 * Contributor : Maxim Essipovitch
 * 
 * Paritcipants :	Samuel Des Rochers		(SamAI)
 * 					Maxim Essipovitch		(MaxAI)
 * 					Hugo Cardin				(HugoAI)
 * 					Emile Ouelette-Delorme 	(PoreuxAI)
 * 					Philippe Rosa-Pong		(PlayerGhandi)
 * 
 **********************************************************/

public class RiskGame extends Canvas{

	/**
	 * Risk game global class
	 */
	private static final long serialVersionUID = 1L;
	private static long sleepTime = 50L;

	public static final int PHASE_INITIAL 		= 0;
	public static final int PHASE_TURN_BEGINS 	= 1;
	public static final int PHASE_REINFORCE		= 2;
	public static final int PHASE_ATTACK 		= 3;
	public static final int PHASE_MOVE_ARMIES 	= 4;
	public static final int PHASE_BONUS 		= 5;

	public ArrayList<Player> 	players;
	public ArrayList<Territory> territories;

	public Territory	attacker;
	public Territory	defender;

	public Player currentPlayer;
	public int currentPlayerIndex;

	public static int BONUS_UNITS_COUNTER 	= 3;
	protected static int STARTING_UNITS 	= 30;	//20 for 6, 25 for 5, 30 for 4 
	protected static int MAX_UNITS 			= 300;

	protected boolean isOver = false;
	public String winner = "";
	public boolean didEleminatePlayer = false;

	public static JFrame frame;
	public JButton slowSpeedButton;
	public JButton fastSpeedButton;
	public JButton extremeSpeedButton;

	public Image riskMap;
	public Font font;

	public static boolean isStarted = false;

	public RiskGame(){
		this.setupUI();
	}

	// Entry point
	public void startGame(){

		// Initial phase
		initPlayers();
		initTerritories();

		ditributeTerritories();
		placeRemainingUnits();
		frame.repaint();

		// Game phase
		playGame();
	}

	// Initialize all territories
	public void initTerritories(){
		Map map = new Map();
		territories = map.generate();
		System.out.println("------- STARTING TERRITORIES DISTRIBUTION -------");
		
		// Wait for user input
		while(!isStarted){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Initialize players
	public void initPlayers(){
		players = new ArrayList<Player>();
		currentPlayerIndex = -1;

		Player p1 	= new SamAI("Sam");
		p1.color = Color.YELLOW;

		Player p2 = new PoreuxAI("Emile");
		p2.color = Color.blue;

		Player p3 = new PlayerGandhi("Pong");
		p3.color = Color.green;

		Player p4 = new MaxAI("Maxim");
		p4.color = Color.red;

		Player p5 = new HugoAI("Hugo");
		p5.color = Color.black;

		Player p6 = new RandomAI("RANDOM");
		p6.color = Color.cyan;

		players.add(p1);
		//players.add(p2);
		players.add(p3);
		players.add(p4);
		players.add(p5);

	}

	// Players each pick a territory one after the other
	private void ditributeTerritories(){
		Random random = new Random();

		// Random player starts choosing his territory
		currentPlayerIndex = random.nextInt(players.size());

		int startingUnits = STARTING_UNITS;
		for(Player p : players){
			p.remainingUnits = startingUnits;
			p.public_allTerritories = territories;
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
					System.out.println("[Deployement - Reinforcements] : An error occured");
				}

				// No more reinforcements, player is out	
			} else { 
				currentPlayerIndex = (currentPlayerIndex + 1)%players.size();
			}
		}
		System.out.println("Initialization all done!");
		System.out.println();
	}

	// Check if all players have placed all of their units
	boolean allUnitsPlaced(){
		for(Player p : players){
			if(p.remainingUnits == 0){
				return true;
			}
		}
		return false;
	}

	private void playGame(){
		// Random first turn pick
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
		currentPlayer = players.get(currentPlayerIndex);

		while(!isOver){

			// Check if game is over
			executeGameOverCheck();

			// Execute the turn for currentPlayer
			executeTurn();

			// Check if game is over
			executeGameOverCheck();

			// Next player
			currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
			currentPlayer = players.get(currentPlayerIndex);
			winner = currentPlayer.name;
			// draw frame
			frame.repaint();
		}
		System.out.println("Game Over!");
	}

	private void executeTurn(){

		int currentNbTerritories = currentPlayer.myOccupiedTerritories.size() - 1;

		// Make sure the player still has a territory (isn't gameover)
		if(currentPlayer.myOccupiedTerritories.size() > 0){

			// Acquire and Place new reinforcements
			executeReinforcementsPhase();

			// Attack other territories
			executeAttackPhase();

			// draw frame
			frame.repaint();

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Hand out a card if necessary
			executePostAttackPhase(currentNbTerritories);		

			// Move units from a territory to another
			executeMovementPhase();
		}
	}

	private void executeReinforcementsPhase(){
		int newReinforcements = calculateNbReinforcements();

		currentPlayer.remainingUnits = newReinforcements;
		System.out.println("-------- Reinforcement --------");
		System.out.println(currentPlayer.name + " Recieved : "+newReinforcements + " new units");
		System.out.println("Number of cards owned : " + currentPlayer.cards.size());


		currentPlayer.printTerritories();

		while(currentPlayer.remainingUnits > 0 && players.size() > 1){
			currentPlayer.assignReinforcements();
		}
	}

	private void executeAttackPhase(){

		// Analyze board situation when about to enter combat (AI) 
		currentPlayer.updateModel();

		// Check if the player wants to attack
		while(currentPlayer.willAttack){

			currentPlayer.prepareCombat();

			// Try to get an attacking territory
			if((this.attacker = currentPlayer.getAttackingTerritory()) != null){

				// Get the defending territory
				this.defender = currentPlayer.getTargetTerritory();

				// Get the amount of units used for this fight (MAXIMUM 3, MINIMUM 1)
				int units = currentPlayer.getNbOfAttackingUnits();

				// Check if number of units is legal
				if(units <= 3 && units >= 1){
					if(attacker.name != defender.name){
						//String userInput = scan.nextLine();

						int[] unitsLost;
						unitsLost = BattleManager.executeAttackPhase(attacker, defender, units);

						// Analyze the outcome of the last combat round (AI)
						// passing the currentPlayer lost units and the defending player lost units
						// for results analysis or else
						currentPlayer.combatAnalysis(unitsLost[0], unitsLost[1]);

						// draw frame
						frame.repaint();

						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					System.out.println("Too many or too few units chosen : "+units);
				}
			} else {
				//System.out.println("No territory was chosen thus no attack");
			}		

			// Re-update our model
			currentPlayer.updateModel();

			// draw frame
			frame.repaint();
		}
		System.out.println("End of the ATTACK phase -------------");
	}

	private void executePostAttackPhase(int initialNbTerritories)
	{
		// Current player has won at least one territory during last combat phase
		if(initialNbTerritories < currentPlayer.myOccupiedTerritories.size() -1){
			Card newcard = new Card();
			if(Card.addCard(currentPlayer.cards, newcard)){
				System.out.println(currentPlayer.name+" recieved a new Card");
			}
		}
	}

	private void executeMovementPhase(){
		currentPlayer.chooseMovementTerritoriesAndUnits();

		Territory from = currentPlayer.getOriginMovementTerritory();
		Territory to = currentPlayer.getDestinationMovementTerritory();
		int units = currentPlayer.getMovementUnits();

		if(from != null && to != null && units > 0){
			if(currentPlayer.moveSoldiers(from, to, units)){
				System.out.println("Player made a units move");
			} else {
				System.out.println("No movement was made");
			}
		}
	}

	private void executeGameOverCheck(){
		try {
			synchronized (currentPlayer) {
				for(Player p : players){
					if(p.myOccupiedTerritories.size() == 0){
						System.out.println(p.name + " was Eliminated!");
						players.remove(p);
						
						tranferPlayerCardsToWinner(p);
						System.out.println("Transfering " +p.name + "'s card to " + currentPlayer.name);
					}
				}
			}
		} catch (Exception e) {
		}

		if(players.size() == 1){
			isOver = true;
			Player winner = players.get(0);
			System.out.println(winner.name + " WON THE GAME !!!");
			frame.setVisible(false); 
			frame.dispose();
		}
	}

	private int calculateNbReinforcements(){
		int num = 3;

		num += Map.getContinentReinforcements(currentPlayer.myOccupiedTerritories);

		int nbControlledTerritories = currentPlayer.myOccupiedTerritories.size();

		// Add by number of controlled territories
		if(nbControlledTerritories > 40){
			num += 11;
		} else if(nbControlledTerritories > 38 && nbControlledTerritories < 40){
			num += 10;
		} else if(nbControlledTerritories > 36 && nbControlledTerritories < 38){
			num += 9;
		} else if(nbControlledTerritories > 33 && nbControlledTerritories < 36){
			num += 8;
		} else if(nbControlledTerritories > 30 && nbControlledTerritories < 33){
			num += 7;
		} else if(nbControlledTerritories > 27 && nbControlledTerritories < 30){
			num += 6;
		} else if(nbControlledTerritories > 24 && nbControlledTerritories < 27){
			num += 5;
		} else if(nbControlledTerritories > 21 && nbControlledTerritories < 24){
			num += 4;
		} else if(nbControlledTerritories > 18 && nbControlledTerritories < 21){
			num += 3;
		} else if(nbControlledTerritories > 15 && nbControlledTerritories < 18){
			num += 2;
		} else if(nbControlledTerritories > 12 && nbControlledTerritories < 15){
			num += 1;
		}
		
		num += getCardUnits();
		if(currentPlayer.cards.size() > 5){
			num += forcePlayCards(currentPlayer);
		}

		int totalUnits = currentPlayer.countUnits();
		if(totalUnits + num > MAX_UNITS){
			System.out.println("Max Unit count reached");
			num = MAX_UNITS - totalUnits;
		} 
		return num;
	}

	private int getCardUnits(){
		ArrayList<Card> cards = currentPlayer.tradeCards();
		if(cards != null){
			if(cards.size() == 3){
				int bonus = Card.tradeCards(currentPlayer, cards.get(0), cards.get(1), cards.get(2));
				if(bonus != 0){
					return bonus;
				} else {
					System.out.println("Bad cards function");
				}
			}
			else {
				System.out.println("Bad cards count");
			}
		}
		return 0;
	}
	
	// Gain the eliminated player's card (up to 6 max)
	private void tranferPlayerCardsToWinner(Player loser){
		currentPlayer.cards.addAll(loser.cards);
		while(currentPlayer.cards.size() > 6){
			currentPlayer.cards.remove(currentPlayer.cards.size()-1);
		}
	}
	
	private int forcePlayCards(Player p){
		int value = 0;
		ArrayList<Card> cards = currentPlayer.cards;
		
		ArrayList<Card> inf_cards = new ArrayList<Card>();
		ArrayList<Card> cav_cards = new ArrayList<Card>();
		ArrayList<Card> art_cards = new ArrayList<Card>();
		ArrayList<Card> tri_cards = new ArrayList<Card>();
		
		for(int i = 0; i < cards.size(); i++){
			Card card = cards.get(i);
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
			value = Card.tradeCards(currentPlayer, cards.get(0), cards.get(1), cards.get(2));
		} else if(art_cards.size() >= 3){
			value = Card.tradeCards(currentPlayer, cards.get(0), cards.get(1), cards.get(2));
		} else if(cav_cards.size() >= 3){
			value = Card.tradeCards(currentPlayer, cards.get(0), cards.get(1), cards.get(2));
		} else if(inf_cards.size() >= 3){
			value = Card.tradeCards(currentPlayer, cards.get(0), cards.get(1), cards.get(2));
		} 
		return value;
	}

	public String getWinnerName(){
		return winner;
	}

	public void setupUI(){
		frame = new JFrame("Risk");
		try {
			riskMap = ImageIO.read(new FileInputStream("img/images/riskmap.png"));
		} catch (IOException e) {
			System.out.println("Error loading image from ressources");
		}
		font = new Font ("Verdana", Font.BOLD , 18);
		
		slowSpeedButton = new JButton("Speed - Slow");
		frame.add(slowSpeedButton);
		fastSpeedButton = new JButton("Speed - Fast");
		frame.add(fastSpeedButton);
		extremeSpeedButton = new JButton("Speed - Extreme");
		frame.add(extremeSpeedButton);
		
		slowSpeedButton.setLocation(5, 5);
		slowSpeedButton.setSize(140, 20);
		fastSpeedButton.setLocation(5, 30);
		fastSpeedButton.setSize(140, 20);
		extremeSpeedButton.setLocation(5, 55);
		extremeSpeedButton.setSize(140, 20);
		
		slowSpeedButton.addActionListener (new slowSpeedButtonClicked());
		fastSpeedButton.addActionListener (new fastSpeedButtonClicked());
		extremeSpeedButton.addActionListener (new extremeSpeedButtonClicked());

		frame.add(new MyPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 780);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public class MyPanel extends JPanel {
		/**
		 * Simple panel to display primitive graphics
		 */
		private static final long serialVersionUID = 1L;

		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(riskMap, 0, 0, null);
			g.setFont(font);

			// Draw all player units (with their respective color)
			try {
				synchronized (g2d) {
					for(Player p : players){
						synchronized (p) {
							for(Territory t : p.myOccupiedTerritories){
								synchronized (t) {
									g.setColor(p.color);
									g2d.drawString(Integer.toString(t.getUnits()), t.position.x, t.position.y);
								}
							}
						}
					}
				}
			} catch (Exception e) {}

			// Draw Combat names
			if(attacker != null && defender != null){
				g.setColor(Color.WHITE);
				String combatString = "- " + currentPlayer.name + "'s Turn ";
				g2d.drawString(combatString, 30, 440);
			}

			// Draw Player names
			int offset = 0;
			try {
				g2d.drawString("Player - Cards ", 30, 465 + offset);
				for(Player p : players){
					synchronized (p) {
						g.setColor(p.color);
						g2d.drawString(p.name + " - " +p.cards.size(), 30, 510 + offset);
						offset += 20;
					}

				}
			} catch (Exception e) {}
		}
	}
	
	static class slowSpeedButtonClicked implements ActionListener {        
			@Override
			public void actionPerformed(ActionEvent arg0) {
				RiskGame.sleepTime = 350L;
				RiskGame.isStarted = true;
			}
	}
	
	static class fastSpeedButtonClicked implements ActionListener {        
		@Override
		public void actionPerformed(ActionEvent arg0) {
			RiskGame.sleepTime = 10L;
			RiskGame.isStarted = true;
		}
	}
	
	static class extremeSpeedButtonClicked implements ActionListener {        
		@Override
		public void actionPerformed(ActionEvent arg0) {
			RiskGame.sleepTime = 0L;
			RiskGame.isStarted = true;
		}
	}
}
