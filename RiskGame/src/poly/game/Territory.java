package poly.game;

import java.util.ArrayList;

public class Territory {
	
	private Player owner;
	public ArrayList<Territory> adjacentTerritories;
	private int units;
	public String continent = "None";
	public String name = "None";
	public boolean isOccupied = false;
	
	public Territory(String cont, String nm){
		this.setOwner(null);
		this.adjacentTerritories = null;
		this.continent = cont;
		this.name = nm;
		this.setUnits(0);	
		this.isOccupied = false;

	}
	
	public void printAdjacent(){
		System.out.println("Adjacent territories to "+ this.name + " :");
		for (Territory t : adjacentTerritories) {
			System.out.print(t.name + " ");
		}
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units += units;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.isOccupied = true;
		this.owner = owner;
	}
}
