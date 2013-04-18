package poly.game;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	public static void main(String[] args) {
		ArrayList<String> winnersList = new ArrayList<String>();
		HashMap<Integer, String> winners = new HashMap<Integer, String>();

		int nbGamesPlayed = 0;
		int targetNbGames = 100;

		try {
			for(;nbGamesPlayed < targetNbGames; nbGamesPlayed++){
				RiskGame game = new RiskGame();
				game.startGame();
				winnersList.add(game.getWinnerName());
				winners.put(game.getWinnerName().hashCode(), game.getWinnerName());
			}
		} catch (Exception e) {
			// One or many players AI crashed, aborting and prinitng known results.
			e.printStackTrace();
		}

		System.out.println("Number of non-executed games : " + (targetNbGames - nbGamesPlayed));
		System.out.println("-------------" + nbGamesPlayed + " Games results -------------");

		int winCounter = 0;
		for(String name : winners.values()){     
			System.out.print(name + "\t : ");
			for(String winner : winnersList){
				if(name.equals(winner)){
					winCounter++;
					System.out.print("*");
				}
			}
			System.out.print(" " + winCounter);
			winCounter = 0;
			System.out.println();
		}
		try { Thread.sleep(8000);} catch (InterruptedException e) {}
		System.exit(0);
	}
}
