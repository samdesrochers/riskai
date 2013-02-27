package poly.game;

import java.util.ArrayList;
import java.util.Random;

public class  Card {
	
	public static int TYPE_SOLIDER 	= 0;
	public static int TYPE_CAVALRY 	= 1;
	public static int TYPE_CANON 	= 2;

	public int type;
	
	public Card(){
		Random ran = new Random();
		this.type = ran.nextInt(3);
	}
	
	public static boolean addCard(ArrayList<Card> cards, Card card){
		
		if(cards.size() -1 < 5){
			cards.add(card);
			return true;
		}
		
		// Couldn't add card : player already has 5 cards
		return false;
	}
}
