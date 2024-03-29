package poly.game;

import java.awt.Point;
import java.util.ArrayList;

public class Territory {
	
	private Player owner;
	public String name = "None";
	private int units;
	public ArrayList<Territory> adjacentTerritories;
	public String continent = "None";
	public Point position;
	public boolean isContinentFrontier = false;
	
	public int value = 0;	// Heuristic value of some sort
	public boolean isOccupied = false;
	
	public Territory(String cont, String nm, boolean isFrontier){
		this.setOwner(null);
		this.adjacentTerritories = null;
		this.continent = cont;
		this.name = nm;
		this.setUnits(0);	
		this.isOccupied = false;
		this.isContinentFrontier = isFrontier;
		position = new Point(0,0);
	}
	
	public void printAdjacent(){
		System.out.println("Adjacent territories to "+ this.name + " :");
		for (Territory t : adjacentTerritories) {
			System.out.print(t.name + " ");
		}
	}
	
	// Manage the action of conquering a territory, using the new owner and the desired territory
	public void conquerTerritory(Player newP, Territory t){
		System.out.println(newP.name + " conquered " + t.getOwner().name +" territory : "+t.name);
		t.getOwner().myOccupiedTerritories.remove(t);
		t.setOwner(newP);
		newP.myOccupiedTerritories.add(t);
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.isOccupied = true;
		this.owner = owner;
	}
	
	public void removeUnits(int u){
		this.units -= u;
	}
	
	public void addUnits(int u){
		this.units += u;
	}
}
