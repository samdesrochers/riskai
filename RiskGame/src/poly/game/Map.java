package poly.game;

import java.util.ArrayList;
import java.util.Arrays;

public class Map {

	// from : http://i451.photobucket.com/albums/qq234/awspeidel/RISKBoard31.jpg
	
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
	
	// Countries by continent - South America
	public static final String VENEZUELA 			= "venezuela";
	public static final String BRAZIL 				= "brazil";
	public static final String PERU 				= "peru";
	public static final String ARGENTINA 			= "argentina";
	
	// Countries by continent - AFRICA
	public static final String NORTH_AFRICA 		= "north_africa";
	public static final String EGYPT 				= "egypt";
	public static final String EAST_AFRICA 			= "east_africa";
	public static final String CONGO 				= "congo";
	public static final String SOUTH_AFRICA 		= "south_africa";
	public static final String MADAGASCAR 			= "madagascar";
	
	private static ArrayList<Territory> northAmerica;
	private static ArrayList<Territory> southAmerica;
	private static ArrayList<Territory> africa;
	private static ArrayList<Territory> europe;
	private static ArrayList<Territory> asia;
	private static ArrayList<Territory> australia;
	private static ArrayList<ArrayList<Territory>> continents;


	public ArrayList<Territory> generate(){
		ArrayList<Territory> map;
		continents = new ArrayList<ArrayList<Territory>>();
		
		// North America
		Territory quebec 			= new Territory(NORTH_AMERICA, QUEBEC);
		Territory ontario 			= new Territory(NORTH_AMERICA, ONTARIO);
		Territory alberta 			= new Territory(NORTH_AMERICA, ALBERTA);
		Territory greenland			= new Territory(NORTH_AMERICA, GREENLAND);
		Territory eastern_us 		= new Territory(NORTH_AMERICA, EASTERN_US);
		Territory western_us 		= new Territory(NORTH_AMERICA, WESTERN_US);
		Territory nw_territory 		= new Territory(NORTH_AMERICA, NW_TERRITORY);
		Territory alaska 			= new Territory(NORTH_AMERICA, ALASKA);
		Territory cent_america 		= new Territory(NORTH_AMERICA, CENT_AMERICA);
		
		// South America
		Territory venezuela 		= new Territory(SOUTH_AMERICA, VENEZUELA);
		Territory brazil 			= new Territory(SOUTH_AMERICA, BRAZIL);
		Territory peru 				= new Territory(SOUTH_AMERICA, PERU);
		Territory argentina			= new Territory(SOUTH_AMERICA, ARGENTINA);
		
		// Africa
		Territory north_africa 		= new Territory(AFRICA, NORTH_AFRICA);
		Territory egypt 			= new Territory(AFRICA, EGYPT);
		Territory east_africa 		= new Territory(AFRICA, EAST_AFRICA);
		Territory congo				= new Territory(AFRICA, CONGO);
		Territory south_africa		= new Territory(AFRICA, SOUTH_AFRICA);
		Territory madagascar		= new Territory(AFRICA, MADAGASCAR);
		

		// North America - Adjacent
		quebec.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( ontario, greenland, eastern_us ));
		ontario.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( quebec, greenland, eastern_us, western_us, nw_territory, alberta ));
		alberta.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( ontario, western_us, nw_territory, alaska ));
		greenland.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( quebec, ontario, nw_territory ));
		eastern_us.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( quebec, ontario, western_us, cent_america ));
		western_us.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( eastern_us, cent_america, alberta, ontario ));
		nw_territory.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( alberta, ontario, greenland, alaska ));
		alaska.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( alberta, nw_territory ));
		cent_america.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( eastern_us, western_us, venezuela ));
		
		// South America - Adjacent
		venezuela.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( brazil, peru, cent_america ));
		brazil.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( peru, venezuela, argentina, north_africa ));
		peru.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( brazil, venezuela, argentina ));
		argentina.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( brazil, peru ));
		
		// Africa - Adjacent
		north_africa.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( egypt, east_africa, congo ));
		egypt.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( north_africa, east_africa ));
		east_africa.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( egypt, north_africa, congo, madagascar ));
		congo.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( north_africa, south_africa, east_africa ));
		south_africa.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( madagascar, congo, east_africa ));
		madagascar.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( south_africa, east_africa ));

		
		northAmerica = new ArrayList<Territory>(Arrays.asList( quebec, ontario, eastern_us, western_us, alaska, alberta, 
															   nw_territory, greenland, cent_america ));
		southAmerica = new ArrayList<Territory>(Arrays.asList( peru, brazil, venezuela, argentina ));
		africa = new ArrayList<Territory>(Arrays.asList( north_africa, egypt, south_africa, east_africa, congo, madagascar ));
		
		continents.add(northAmerica);
		continents.add(southAmerica);
		continents.add(africa);
		
		map = new ArrayList<Territory>(Arrays.asList( quebec, ontario, eastern_us, western_us, alaska, alberta, 
				   nw_territory, greenland, cent_america, peru, brazil, venezuela, argentina,
				   north_africa, egypt, south_africa, east_africa, congo, madagascar ));

		return map;
	}

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
					player.remainingUnits -= nbUnits;
					return true;
				}
				else {
					System.out.println("Bad owner choice");
				}
			}
		}
		return false;
	}
	
	public static int getContinentReinforcements(ArrayList<Territory> occupiedTerritories){
		int bonusUnits = 0;
		bonusUnits += getBonusUnitsFromContinents(occupiedTerritories);
		return bonusUnits;
	}
	
	private static int getBonusUnitsFromContinents(ArrayList<Territory> occupiedTerritories){
		int bonusUnits = 0;
		
		// For every continents
		for(ArrayList<Territory> continent : continents){
			if(occupiedTerritories.size() >= continent.size()){
				
				// Number of territories controlled by the player for the current continent
				int nbTerrCont = 0;
				
				// For every territories owned by the player
				for(int i = 0; i < occupiedTerritories.size(); i++){
					Territory co = occupiedTerritories.get(i);
					for(int j = 0; j < continent.size(); j++){
						Territory ct = continent.get(j);
						if(co.name.equals(ct.name)){
							nbTerrCont ++;
							j = continent.size() + 1; // break
						}
					}
				}
				
				if(nbTerrCont == continent.size() && continent.equals(northAmerica)){
					bonusUnits += 5;
				} else if(nbTerrCont == continent.size() && continent.equals(southAmerica)){
					bonusUnits += 3;
				}
			}
		}
		return bonusUnits;
	}
}
