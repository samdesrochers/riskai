package poly.game;

import java.util.Random;

public class PlayerSam extends Player {

	public PlayerSam(String name) {
		super(name);
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
		if(this.remainingUnits - remainingThisTurn < 0){
			return this.remainingUnits;
		}
		return remainingThisTurn;
	}

	@Override
	public String pickReinforceTerritory() {
		Random ran = new Random();
		int r = ran.nextInt(occupiedTerritories.size());
		Territory pick = occupiedTerritories.get(r);
		
		return pick.name;
	}

	@Override
	public void assignReinforcements() {
		Random ran = new Random();
		
		//random territory
		int rt = ran.nextInt(occupiedTerritories.size());
		
		// random nb of units
		int ru = ran.nextInt(this.remainingUnits) + 1;

		// assign random nb of units on the random territory
		Territory pick = occupiedTerritories.get(rt);
		pick.setUnits(ru);
		
		this.remainingUnits -= ru;
	}
}
