package poly.game;

import java.util.ArrayList;
import java.util.Arrays;

public class MapGenerator {
	
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
	
	public ArrayList<Territory> generate(){
		ArrayList<Territory> map = new ArrayList<Territory>();
		
		Territory quebec 		= new Territory(NORTH_AMERICA, QUEBEC);
		Territory ontario 		= new Territory(NORTH_AMERICA, ONTARIO);
		Territory alberta 		= new Territory(NORTH_AMERICA, ALBERTA);
		Territory greenland		= new Territory(NORTH_AMERICA, GREENLAND);
		Territory eastern_us 	= new Territory(NORTH_AMERICA, EASTERN_US);
		Territory western_us 	= new Territory(NORTH_AMERICA, WESTERN_US);
		Territory nw_territory 	= new Territory(NORTH_AMERICA, NW_TERRITORY);
		Territory alaska 		= new Territory(NORTH_AMERICA, ALASKA);
		Territory cent_america 	= new Territory(NORTH_AMERICA, CENT_AMERICA);
		
		quebec.adjacentTerritories = new ArrayList<Territory>(Arrays.asList( ontario, greenland, eastern_us ));
		ontario.adjacentTerritories = new ArrayList<Territory>(Arrays.asList( quebec, greenland, eastern_us, western_us, nw_territory, alberta ));
		alberta.adjacentTerritories = new ArrayList<Territory>(Arrays.asList( ontario, western_us, nw_territory, alaska ));
		greenland.adjacentTerritories = new ArrayList<Territory>(Arrays.asList( quebec, ontario, nw_territory ));

		map.add(quebec);
		map.add(ontario);
		map.add(alberta);
		map.add(greenland);

		return map;
	}
	
}
