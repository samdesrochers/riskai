package poly.game;

import java.util.ArrayList;

public class Map {

	public static final String NORTH_AMERICA 		= "north_america";
	public static final String SOUTH_AMERICA 		= "south_america";
	public static final String EUROPE 				= "europe";
	public static final String ASIA 				= "asia";
	public static final String AUSTRALIA 			= "australia";
	public static final String AFRICA 				= "africa";

	// Countries by continent - North America
	public static final String QUEBEC 				= "quebec";
	public static final String ONTARIO 				= "ontario";
	public static final String ALBERTA 				= "alberta";
	public static final String GREENLAND 			= "greenland";
	public static final String EASTERN_US 			= "eastern_us";
	public static final String WESTERN_US 			= "western_us";
	public static final String NW_TERRITORY 		= "nw_territory";
	public static final String ALASKA 				= "alaska";
	public static final String CENT_AMERICA 		= "cent_america";

	// Tries to assign the territory. Returns false if it is already owned by another player
	static boolean acquireTerritory(String territoryName, Player player, int nbUnits, ArrayList<Territory> territories){
		for(Territory t : territories){
			if(t.name == territoryName){

				// First time asking for this country
				if(!t.isOccupied){
					player.occupiedTerritories.add(t);
					t.setOwner(player);
					t.setUnits(nbUnits);
					return true;
				}
				else {
					return false; 
				}
			}
		}
		return false;
	}

	static boolean allTerritoriesAssigned( ArrayList<Territory> territories){
		for(Territory t : territories){
			if(!t.isOccupied)
				return false;
		}
		return true;
	}

	static boolean reinforceTerritoryWithUnits(String territoryName, Player player, int nbUnits, ArrayList<Territory> territories){
		for(Territory t : territories){
			if(t.name == territoryName){
				// make sure the player can assign units 
				if(player == t.getOwner()){
					t.setUnits(nbUnits);
					return true;
				}
				else {
					System.out.println("Bad owner choice");
				}
			}
		}
		return false;
	}
}
