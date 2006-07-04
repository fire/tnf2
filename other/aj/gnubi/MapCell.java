package aj.gnubi;

/*
 * Created on Jun 29, 2003
 *

 * @author winme
 */
import java.awt.Point;
import java.util.Vector;

public class MapCell {
	Block array[][];

	static int defaultSize = 12;

	int size = defaultSize;

	public int getSize() {
		return size;
	}

	// /public Block getBlockAt(int x,int y){
	// if (array!=null & y>=0 && x>=0 && y<array.length && x<array[y].length)
	// return array[y][x];
	// else return null;
	// }
	public Vector getAllBlocks() {
		Vector v = new Vector();
		for (int a = 0; a < array.length; a++) {
			if (array[a] == null)
				continue;
			for (int b = 0; b < array[a].length; b++) {
				if (array[a][b] != null && array[a][b].isSolid())
					v.addElement(array[a][b]);
			}
		}
		return v;
	}

	public static MapCell createRandom() {
		MapCell m = new MapCell();
		Block bb[][] = new Block[defaultSize][];
		for (int a = 0; a < defaultSize; a++) {
			bb[a] = new Block[defaultSize];
			for (int b = 0; b < defaultSize; b++) {
				bb[a][b] = new Block();
				bb[a][b].setSolid(Math.random() + .35 > 1);
				if (a == 0 || b == 0 || a == defaultSize - 1
						|| b == defaultSize - 1)
					bb[a][b].setSolid(true);
				bb[a][b].setCellPos(new Point(b, a));
				// System.out.print(bb[a][b].isSolid()?"X":"0");
			}
			// System.out.println("");
		}
		// System.out.println("done");
		m.array = bb;
		m.size = defaultSize;
		return m;
	}

	/**
	 * @param cx
	 * @param cy
	 * @param i
	 * @return
	 */
	public Vector getAllBlocks(int cx, int cy, int i) {
		Vector v = new Vector();
		for (int a = 0; a < array.length; a++) {
			if (array[a] == null)
				continue;
			for (int b = 0; b < array[a].length; b++) {
				if (b < cy + i && b > cy - i && a < cx + i && a > cx - i
						&& array[a][b] != null && array[a][b].isSolid())
					v.addElement(array[a][b]);
			}
		}
		return v;
	}
}

/*
 */
