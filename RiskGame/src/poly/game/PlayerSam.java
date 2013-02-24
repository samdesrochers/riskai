package poly.game;

public class PlayerSam extends Player {

	public PlayerSam(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateModel() {
		// TODO Auto-generated method stub

	}

	@Override
	public String chooseTerritory() {
		for(Territory t : allTerritories){
			if(!t.isOccupied){
				return t.name;
			}
		}
		return Map.QUEBEC;
	}

	@Override
	public int chooseNbOfUnits(int remainingThisTurn) {
		// TODO Auto-generated method stub
		if(this.remainingUnits - remainingThisTurn < 0){
			return this.remainingUnits;
		}
		return remainingThisTurn;
	}

	@Override
	public String reinforceTerritory() {
		for(Territory t : occupiedTerritories){
			return t.name;
		}
		return "none";
	}
}
