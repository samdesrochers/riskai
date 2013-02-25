package poly.game;

public class BattleManager {
	
	// Combat round, returns the winning player
	public static int[] executeAttackPhase(Territory attacker, Territory defender, int attackingUnits){
		logAttackBegin(attacker, defender, attackingUnits);
		
		int[] unitsLost = new int[2];
		unitsLost[0] = 1; // Attacker units lost
		unitsLost[1] = 2; // Defender units lost

		attacker.removeUnits(attackingUnits);
		
		return unitsLost;
	}
	
	private static void logAttackBegin(Territory attacker, Territory defender, int attackingUnits){
		System.out.println("&&&&&&&&&&&& ATTACK ACTION &&&&&&&&&&&&&");
		System.out.println(attacker.getOwner().name + " is attacking " + defender.getOwner().name
							+ " . Attacking from " + attacker.name + " to " + defender.name + " with " + attackingUnits +" units");
	}
}
