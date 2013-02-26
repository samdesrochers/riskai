package poly.game;

import java.util.Arrays;
import java.util.Random;

public class BattleManager {
	
	// Combat round, returns the winning player
	public static int[] executeAttackPhase(Territory attacker, Territory defender, int attackingUnits){
		Random ran = new Random();
		
		logAttackBegin(attacker, defender, attackingUnits);
		System.out.println("Defender has " + defender.getUnits() + " total units on territory");
		int attackerLostUnits = 0;
		int defenderLostUnits = 0;
		
		int[] defenderRolls;
		int[] attackerRolls;
		
		// Will always be between 1 and 3
		int[] attackerTempRolls = new int[attackingUnits];
		
		// Will always be between 1 and 2
		if(defender.getUnits() >= 2){
			defenderRolls = new int[2];
		} else {
			defenderRolls = new int[1];
		}
		
		// Attacker rolls
		for(int i = 0; i < attackerTempRolls.length; i++){
			int roll = ran.nextInt(6)+ 1;
			attackerTempRolls[i] = roll;
		}
		// Put the 2 maximum values at the end
		Arrays.sort(attackerTempRolls);
		
		if(attackerTempRolls.length > 1){
			attackerRolls = new int[2];
			attackerRolls[1] = attackerTempRolls[attackerTempRolls.length-1];		// highest value
			attackerRolls[0] = attackerTempRolls[attackerTempRolls.length-2];	// second highest value

		} else {
			attackerRolls = new int[1];
			attackerRolls[0] = attackerTempRolls[attackerTempRolls.length-1];	// second highest value
		}
		
		// Defender rolls
		for(int i = 0; i < defenderRolls.length; i++){
			int roll = ran.nextInt(6)+ 1;
			defenderRolls[i] = roll;
		}
		Arrays.sort(defenderRolls);
		
		// Check the outcome
		int i = attackerRolls.length - 1;
		int j = defenderRolls.length - 1;
		int iterations = Math.min(i, j);
		while(iterations >= 0){
			if(defenderRolls[j] >= attackerRolls[i]){
				System.out.println("ATT = "+attackerRolls[i]+" DEF = "+defenderRolls[j]+" | Defender wins");
				iterations--;
				defenderLostUnits ++;
			} else if (attackerRolls[i] > defenderRolls[j]){
				System.out.println("ATT = "+attackerRolls[i]+" DEF = "+defenderRolls[j]+" | Attacker wins");
				iterations--;
				attackerLostUnits ++;
			}
			i--; j--;
		}

		int[] unitsLost = new int[2];
		unitsLost[0] = attackerLostUnits; // Attacker units lost
		unitsLost[1] = defenderLostUnits; // Defender units lost

		attacker.removeUnits(attackerLostUnits);
		//defender.removeUnits(defenderLostUnits);
		
		return unitsLost;
	}

	
	
	private static void logAttackBegin(Territory attacker, Territory defender, int attackingUnits){
		System.out.println();
		System.out.println("&&&&&&&&&&&& ATTACK ACTION &&&&&&&&&&&&&");
		System.out.println(attacker.getOwner().name + " is attacking " + defender.getOwner().name);
		System.out.println("From " + attacker.name + " to " + defender.name + " with " + attackingUnits +" units");
	}
}
