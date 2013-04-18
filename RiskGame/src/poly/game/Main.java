package poly.game;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	public static void main(String[] args) {
		ArrayList<String> winnersList = new ArrayList<String>();
		HashMap<Integer, String> winners = new HashMap<Integer, String>();
		
		int nbGamesPlayed = 0;
		int targetNbGames = 100;
		
			for(;nbGamesPlayed < targetNbGames; nbGamesPlayed++){
		        RiskGame game = new RiskGame();
		        game.startGame();
		        winnersList.add(game.getWinnerName());
		        winners.put(game.getWinnerName().hashCode(), game.getWinnerName());
			}
			
		System.out.println("-------------" + nbGamesPlayed + " Games results -------------");
		for(String name : winners.values()){     
			System.out.print(name + "\t : ");
			for(String winner : winnersList){
				if(name.equals(winner)){
					System.out.print("*");
				}
			}
			System.out.println();
		}
		try { Thread.sleep(8000);} catch (InterruptedException e) {}
		System.exit(0);
    }
}
