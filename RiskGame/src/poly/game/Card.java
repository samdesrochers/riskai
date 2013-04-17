package poly.game;

import java.util.ArrayList;
import java.util.Random;

public class  Card {
	
	public static int TYPE_INFANTRY = 0;
	public static int TYPE_CAVALRY 	= 1;
	public static int TYPE_ARTILERY = 2;

	public static int bonus_count = 2;
	
	public int type;
	
	public Card(){
		Random ran = new Random();
		this.type = ran.nextInt(3);
	}
	
	public static boolean addCard(ArrayList<Card> cards, Card card){
		
		if(cards.size() < 5){
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

		if(c1.type == c2.type && c1.type == c3.type){
			bonus_count += (bonus_count < 12) ? 2 : 5;
			p.cards.remove(c3);
			p.cards.remove(c2);
			p.cards.remove(c1);

		} else if(c1.type != c2.type && c1.type != c3.type && c2.type != c3.type ){
			bonus_count += (bonus_count < 12) ? 2 : 5;
			p.cards.remove(c3);
			p.cards.remove(c2);
			p.cards.remove(c1);
		} 
		
		bonus_count = (bonus_count > 35) ? 35 : bonus_count;
	
		System.out.println("Nb units given out : " + bonus_count);
		return bonus_count;
	}
}
