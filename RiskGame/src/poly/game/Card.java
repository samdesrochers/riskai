package poly.game;

import java.util.ArrayList;
import java.util.Random;

public class  Card {
	
	public static int TYPE_INFANTRY = 0;
	public static int TYPE_CAVALRY 	= 1;
	public static int TYPE_ARTILERY = 2;

	public int type;
	
	public Card(){
		Random ran = new Random();
		this.type = ran.nextInt(3);
	}
	
	public static boolean addCard(ArrayList<Card> cards, Card card){
		
		if(cards.size() -1 < 5){
			cards.add(card);
			return true;
		} else if (cards.size() >= 5){
			System.out.println("Deck is already full (5 cards)");
		}
		
		// Couldn't add card : player already has 5 cards
		return false;
	}
	
	// Trades tree cards for a specific amount of untis
	public static int tradeCards(Player p, Card c1, Card c2, Card c3){
		int bonusUnits = 0;

		if(c1.type == c2.type && c1.type == c3.type){
			if(c1.type == TYPE_INFANTRY){
				bonusUnits = 4;
			} else if(c1.type == TYPE_CAVALRY){
				bonusUnits = 6;
			} else if(c1.type == TYPE_ARTILERY){
				bonusUnits = 8;
			}
			p.cards.remove(c3);
			p.cards.remove(c2);
			p.cards.remove(c1);

		} else if(c1.type != c2.type && c1.type != c3.type && c2.type != c3.type ){
			bonusUnits = 10;
			p.cards.remove(c3);
			p.cards.remove(c2);
			p.cards.remove(c1);
		} 
		
		return bonusUnits;
	}
}
