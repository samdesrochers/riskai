package poly.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {

	// from : http://i451.photobucket.com/albums/qq234/awspeidel/RISKBoard31.jpg

	public static final String NORTH_AMERICA 		= "north_america";
	public static final String SOUTH_AMERICA 		= "south_america";
	public static final String EUROPE 				= "europe";
	public static final String ASIA					= "asia";
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

	// Countries by continent - Africa
	public static final String NORTH_AFRICA 		= "north_africa";
	public static final String EGYPT 				= "egypt";
	public static final String EAST_AFRICA 			= "east_africa";
	public static final String CONGO 				= "congo";
	public static final String SOUTH_AFRICA 		= "south_africa";
	public static final String MADAGASCAR 			= "madagascar";

	// Countries by continent - Europe
	public static final String ICELAND 				= "iceland";
	public static final String GREAT_BRITAIN 		= "great_britain";
	public static final String W_EUROPE 			= "w_europe";
	public static final String N_EUROPE 			= "n_europe";
	public static final String SCANDINAVIA 			= "scandinavia";
	public static final String UKRAINE 				= "ukraine";
	public static final String S_EUROPE 			= "s_europe";

	// Countries by continent - Asia
	public static final String URAL 				= "ural";
	public static final String AFGHANISTAN 			= "afghanistan";
	public static final String MIDDLE_EAST 			= "middle_east";
	public static final String INDIA 				= "india";
	public static final String SIAM 				= "siam";
	public static final String CHINA 				= "china";
	public static final String MONGOLIA 			= "mongolia";
	public static final String JAPAN 				= "japan";
	public static final String SIBERIA 				= "siberia";
	public static final String YAKUTSK 				= "yakutsk";
	public static final String IRKUTSK 				= "irkutsk";
	public static final String KAMATCHKA 			= "kamatchka";

	// Countries by continent - Australia
	public static final String INDONESIA 			= "indonesia";
	public static final String NEW_GUINEA 			= "new_guinea";
	public static final String W_AUSTRALIA 			= "w_australia";
	public static final String E_AUSTRALIA 			= "e_australia";

	public static Continent northAmerica;
	public static Continent southAmerica;
	public static Continent africa;
	public static Continent europe;
	public static Continent asia;
	public static Continent australia;
	public static ArrayList<Continent> continents;


	public ArrayList<Territory> generate(){
		ArrayList<Territory> map;
		continents = new ArrayList<Continent>();

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

		// Europe
		Territory iceland 			= new Territory(EUROPE, ICELAND);
		Territory great_britain 	= new Territory(EUROPE, GREAT_BRITAIN);
		Territory w_europe 			= new Territory(EUROPE, W_EUROPE);
		Territory n_europe			= new Territory(EUROPE, N_EUROPE);
		Territory s_europe			= new Territory(EUROPE, S_EUROPE);
		Territory ukraine			= new Territory(EUROPE, UKRAINE);
		Territory scandinavia		= new Territory(EUROPE, SCANDINAVIA);

		// Asia
		Territory ural 				= new Territory(ASIA, URAL);
		Territory afghanistan 		= new Territory(ASIA, AFGHANISTAN);
		Territory middle_east 		= new Territory(ASIA, MIDDLE_EAST);
		Territory india				= new Territory(ASIA, INDIA);
		Territory siam				= new Territory(ASIA, SIAM);
		Territory china				= new Territory(ASIA, CHINA);
		Territory mongolia			= new Territory(ASIA, MONGOLIA);
		Territory japan				= new Territory(ASIA, JAPAN);
		Territory yakutsk			= new Territory(ASIA, YAKUTSK);
		Territory irkutsk			= new Territory(ASIA, IRKUTSK);
		Territory siberia			= new Territory(ASIA, SIBERIA);
		Territory kamatchka			= new Territory(ASIA, KAMATCHKA);

		// Australia
		Territory indonesia 		= new Territory(AUSTRALIA, INDONESIA);
		Territory new_guinea 		= new Territory(AUSTRALIA, NEW_GUINEA);
		Territory w_australia 		= new Territory(AUSTRALIA, W_AUSTRALIA);
		Territory e_australia		= new Territory(AUSTRALIA, E_AUSTRALIA);

		// North America - Adjacent
		quebec.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( ontario, greenland, eastern_us ));
		ontario.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( quebec, greenland, eastern_us, western_us, nw_territory, alberta ));
		alberta.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( ontario, western_us, nw_territory, alaska ));
		greenland.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( iceland, quebec, ontario, nw_territory  ));
		eastern_us.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( cent_america, quebec, ontario, western_us  ));
		western_us.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( eastern_us, cent_america, alberta, ontario ));
		nw_territory.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( alberta, ontario, greenland, alaska ));
		alaska.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( alberta, nw_territory, kamatchka ));
		cent_america.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( venezuela, eastern_us, western_us  ));

		// South America - Adjacent
		venezuela.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( brazil, peru, cent_america ));
		brazil.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( north_africa, peru, venezuela, argentina  ));
		peru.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( brazil, venezuela, argentina ));
		argentina.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( brazil, peru ));

		// Africa - Adjacent
		north_africa.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( egypt, east_africa, congo, w_europe, s_europe ));
		egypt.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( north_africa, east_africa, s_europe, middle_east ));
		east_africa.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( egypt, north_africa, congo, madagascar, middle_east ));
		congo.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( north_africa, south_africa, east_africa ));
		south_africa.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( madagascar, congo, east_africa ));
		madagascar.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( south_africa, east_africa ));

		// Europe - Adjacent
		iceland.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( greenland, n_europe, great_britain, scandinavia ));
		great_britain.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( iceland, n_europe, w_europe, scandinavia ));
		n_europe.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( iceland, w_europe, s_europe, ukraine, scandinavia, great_britain ));
		s_europe.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( w_europe, ukraine, n_europe, egypt, north_africa, middle_east ));
		w_europe.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( north_africa, great_britain, n_europe, s_europe ));
		scandinavia.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( iceland, great_britain, n_europe, ukraine ));
		ukraine.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( scandinavia, n_europe, s_europe, ural, afghanistan, middle_east ));

		// Asia - Adjacent
		ural.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( ukraine, siberia, afghanistan, china ));
		afghanistan.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( ukraine, ural, afghanistan, china, middle_east, india  ));
		middle_east.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( ukraine, india, s_europe, egypt, east_africa, afghanistan ));
		india.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( middle_east, afghanistan, china, siam  ));
		siam.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( india, china, indonesia ));
		china.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( india, siam, mongolia, afghanistan, ural, siberia ));
		japan.adjacentTerritories 			= new ArrayList<Territory>(Arrays.asList( mongolia, kamatchka ));
		mongolia.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( japan, irkutsk, siberia, china, kamatchka ));
		irkutsk.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( mongolia, siberia, yakutsk, kamatchka));
		yakutsk.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( irkutsk, siberia, kamatchka ));
		siberia.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( ural, yakutsk, irkutsk, mongolia, china ));
		kamatchka.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( alaska, japan, yakutsk, irkutsk ));

		// Asia - Adjacent
		indonesia.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( siam, new_guinea, w_australia));
		new_guinea.adjacentTerritories 		= new ArrayList<Territory>(Arrays.asList( indonesia, e_australia  ));
		e_australia.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( w_australia, new_guinea));
		w_australia.adjacentTerritories 	= new ArrayList<Territory>(Arrays.asList( e_australia, indonesia ));

		// Continents
		northAmerica 	= new Continent(NORTH_AMERICA);
		southAmerica	= new Continent(SOUTH_AMERICA);
		africa			= new Continent(AFRICA);
		asia			= new Continent(ASIA);
		europe			= new Continent(EUROPE);
		australia		= new Continent(AUSTRALIA);

		northAmerica.territories 	= new ArrayList<Territory>(Arrays.asList( quebec, ontario, eastern_us, western_us, alaska, alberta, nw_territory, greenland, cent_america ));
		southAmerica.territories 	= new ArrayList<Territory>(Arrays.asList( peru, brazil, venezuela, argentina ));
		africa.territories 			= new ArrayList<Territory>(Arrays.asList( north_africa, egypt, south_africa, east_africa, congo, madagascar ));
		europe.territories 			= new ArrayList<Territory>(Arrays.asList( iceland, great_britain, n_europe, s_europe, w_europe, scandinavia, ukraine ));
		asia.territories			= new ArrayList<Territory>(Arrays.asList( ural, afghanistan, middle_east, india, siam, china, mongolia, japan, irkutsk, yakutsk, siberia, kamatchka ));
		australia.territories 		= new ArrayList<Territory>(Arrays.asList( indonesia, new_guinea, e_australia, w_australia ));

		continents.add(northAmerica);
		continents.add(southAmerica);
		continents.add(africa);
		continents.add(europe);
		continents.add(asia);
		continents.add(australia);
		
		// Positions - North America
		quebec.position = new Point(350, 230);
		ontario.position = new Point(270, 225);
		greenland.position = new Point(450, 100);
		nw_territory.position = new Point(200, 150);
		alaska.position = new Point(80, 150);
		alberta.position = new Point(190, 220);
		western_us.position = new Point(190, 280);
		eastern_us.position = new Point(290, 300);
		cent_america.position = new Point(207, 360);
		
		// Positions - South America
		venezuela.position = new Point(252, 445);
		brazil.position = new Point(352, 521);
		peru.position = new Point(275, 551);
		argentina.position = new Point(280, 654);
		
		// Positions - Africa
		congo.position = new Point(577, 565);
		east_africa.position = new Point(651, 500);
		egypt.position = new Point(600, 418);
		madagascar.position = new Point(680, 645);
		north_africa.position = new Point(512,458);
		south_africa.position = new Point(583, 671);
		
		// Positions - Europe
		great_britain.position = new Point(510, 249);
		iceland.position = new Point(501, 175);
		n_europe.position = new Point(573, 259);
		scandinavia.position = new Point(563, 168);
		s_europe.position = new Point(600,313);
		ukraine.position = new Point(673, 231);
		w_europe.position = new Point(500, 333);
		
		// Positions - Asia
		afghanistan.position = new Point(770, 294);
		china.position = new Point(900, 344);
		india.position = new Point(817, 422);
		irkutsk.position = new Point(947, 229);
		japan.position = new Point(1053,308);
		kamatchka.position = new Point(1110, 149);
		middle_east.position = new Point(676, 400);
		mongolia.position = new Point(981, 277);
		siam.position = new Point(910, 428);
		siberia.position = new Point(880, 196);
		ural.position = new Point(800, 207);
		yakutsk.position = new Point(987,166);
		
		// Positions - Australia
		e_australia.position = new Point(1037, 631);
		indonesia.position = new Point(935, 512);
		new_guinea.position = new Point(1064, 535);
		w_australia.position = new Point(930, 635);
		
		

		map = new ArrayList<Territory>(Arrays.asList( quebec, ontario, eastern_us, western_us, alaska, alberta, 
				nw_territory, greenland, cent_america, peru, brazil, venezuela, argentina,
				north_africa, egypt, south_africa, east_africa, congo, madagascar,
				iceland, great_britain, n_europe, s_europe, w_europe, scandinavia, ukraine,
				ural, afghanistan, middle_east, india, siam, china, mongolia, japan, irkutsk, yakutsk, siberia, kamatchka,
				indonesia, new_guinea, e_australia, w_australia));

		return map;
	}

	// Tries to assign the territory. Returns false if it is already owned by another player
	static boolean acquireTerritory(String territoryName, Player player, int nbUnits, ArrayList<Territory> territories){
		for(Territory t : territories){
			if(t.name == territoryName){

				// First time asking for this country
				if(!t.isOccupied){
					player.myOccupiedTerritories.add(t);
					t.setOwner(player);
					t.isOccupied = true;
					t.addUnits(nbUnits);
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
					t.addUnits(nbUnits);
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
		for(Continent c : continents){
			if(occupiedTerritories.size() >= c.territories.size()){

				// Number of territories controlled by the player for the current continent
				int nbTerrCont = 0;

				// For every territories owned by the player
				for(int i = 0; i < occupiedTerritories.size(); i++){
					Territory co = occupiedTerritories.get(i);
					for(int j = 0; j < c.territories.size(); j++){
						Territory ct = c.territories.get(j);
						if(co.name.equals(ct.name)){
							nbTerrCont ++;
							j = c.territories.size() + 1; // break
						}
					}
				}

				if(nbTerrCont == c.territories.size() && (c.name == NORTH_AMERICA || c.name == EUROPE)){
					bonusUnits += 5;
				} else if(nbTerrCont == c.territories.size() && (c.name == AUSTRALIA || c.name == SOUTH_AMERICA)){
					bonusUnits += 2;
				} else if(nbTerrCont == c.territories.size() && c.name == AFRICA){
					bonusUnits += 3;
				}  else if(nbTerrCont == c.territories.size() && c.name == ASIA){
					bonusUnits += 7;
				}
			}
		}
		return bonusUnits;
	}

	// Returns true if a player owns the continent provided in parameter (String continentName)
	public static boolean checkIfContinentOwned(String continentName, ArrayList<Territory> playerTerritories){
		Continent targetContinent = null;
		for(Continent cont : continents){
			if(cont.name == continentName){
				targetContinent = cont;
			} 
		}
		if(targetContinent != null){
			int count = 0;
			for(Territory t : playerTerritories){
				if(t.continent == continentName){
					count ++;
				}
			}
			if(count == targetContinent.territories.size()){
				//System.out.println("You own : " + continentName);
				return true;
			}
		} else {
			System.out.println("Bad continent name");
			return false;
		}
		return false;
	}
}
