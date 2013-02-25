package poly.game;

import java.util.Random;

public class PlayerSam extends Player {

	private Random ran;
	protected Territory attacker;
	private Territory target;
	
	public PlayerSam(String name) {
		super(name);
		ran = new Random();
	}

	@Override
	public void updateModel() {

	}

	@Override
	public String chooseTerritory() {
		for(Territory t : allTerritories){
			if(!t.isOccupied){
				return t.name;
			}
		}
		return "problem";
	}

	@Override
	public int chooseNbOfUnits(int remainingThisTurn) {
		// Random nb of units between 1 and the remaining units placable this turn (3 being the max)
		int r = ran.nextInt(remainingThisTurn) + 1;
		return r;
	}

	public String pickReinforceTerritory() {
		int r = ran.nextInt(occupiedTerritories.size());
		Territory pick = occupiedTerritories.get(r);
		
		return pick.name;
	}

	public void assignReinforcements() {
		//random territory
		int rt = ran.nextInt(occupiedTerritories.size());
		
		// random nb of units
		int ru = ran.nextInt(this.remainingUnits) + 1;

		// assign random nb of units on the random territory
		Territory pick = occupiedTerritories.get(rt);
		pick.setUnits(ru);
		
		this.remainingUnits -= ru;
	}

	public boolean isAttacking() {
		
		// Always attack!
		return true;
	}

	@Override
	public Territory getAttackingTerritory() {
		int numberTerritoriesChecked = 0;
		while(numberTerritoriesChecked < occupiedTerritories.size()){
			
			// try a random territory
			int rt = ran.nextInt(occupiedTerritories.size());
			Territory attacker = occupiedTerritories.get(rt);
			
			// Check all adjacent territories and try to find an enemy
			for(Territory t : attacker.adjacentTerritories){
				if(t.getOwner().name != attacker.getOwner().name){
					target = t;
					return attacker;
				}
			}
			// Go to next territory if not
			numberTerritoriesChecked ++;
		}
		return null;
	}

	@Override
	public Territory getTargetTerritory(Territory originTerritory) {
		return target;
	}

	@Override
	public int getNbOfAttackingUnits(Territory attackingTerritory) {
		if(attackingTerritory.getUnits() - 1 > 0){
			return attackingTerritory.getUnits() - 1;
		}
		return 0;
	}
}
