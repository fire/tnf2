package aj.gnubi;

import java.awt.Point;

/*
 * Created on Jun 29, 2003
 *
 * @author winme
 *
 */
public class Map {

	/**
	 * @param point
	 * @return
	 */
	public MapCell getMapCell(Point point) {
		return MapCell.createRandom();
	}

}
/*
   ____
---/   H 
   -----


platforms 
ladders
slopes?
doors into layers

*/
