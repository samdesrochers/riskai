package poly.game;

public class BattleManager {
	
	public static void executeAttackPhase(Territory attacker, Territory defender, int attackingUnits){
		System.out.println("&&&&&&&&&&&& ATTACK ACTION &&&&&&&&&&&&&");
		System.out.println(attacker.getOwner().name + " is attacking " + defender.getOwner().name
							+ " . Attacking from " + attacker.name + " to " + defender.name + " with " + attackingUnits +" units");
	}
}
