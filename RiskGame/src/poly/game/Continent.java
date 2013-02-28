package poly.game;

import java.util.ArrayList;

public class Continent {
	public ArrayList<Territory> territories;
	public String name;
	
	public Continent(String name)
	{
		this.territories = new ArrayList<Territory>();
		this.name = name;
	}
}
