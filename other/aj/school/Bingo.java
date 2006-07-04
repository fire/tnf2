package aj.school;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import aj.misc.GmlPair;

public class Bingo {
	// ascii type box characters
	static char DdDr = 'É', Drl = 'Í', DdDrl = 'Ë', DdDl = '»', Dud = 'º',
			DudDr = 'Ì', DudDrl = 'Î', DudDl = '¹', DuDr = 'È', DuDrl = 'Ê',
			DuDl = '¼';

	static char SdSr = 'Ú', Srl = 'Ä', SdSrl = 'Â', SdSl = '¿', Sud = '³',
			SudSr = 'Ã', SudSrl = 'Å', SudSl = '´', SuSr = 'À', SuSrl = 'Á',
			SuSl = 'Ù';

	public static void main(String s[]) {
		if (s.length != 1) {
			System.out.println("FORMAT java aj.bingo.Bingo <bingo.cfg>");
			System.exit(0);
		}
		try {
			GmlPair g = GmlPair.parse(new File(s[0]));
			int h = 60, w = 80, r = 5, c = 5;
			GmlPair n[] = n = g.getAllByName("width");
			if (n.length > 0) {
				w = (int) n[0].getDouble();
			}
			n = g.getAllByName("height");
			if (n.length > 0) {
				h = (int) n[0].getDouble();
			}
			n = g.getAllByName("rows");
			if (n.length > 0) {
				r = (int) n[0].getDouble();
			}
			n = g.getAllByName("cols");
			if (n.length > 0) {
				c = (int) n[0].getDouble();
			}
			GmlPair cells[] = g.getAllByName("cell");
			new Bingo(h, w, r, c, cells);
		} catch (IOException IOE) {
			System.out.println("FORMAT java aj.bingo.Bingo <bingo.cfg>");
			System.out.println("Bad config file found " + s[0]);
		}
	}

	public Bingo(int hei, int wid, int rows, int cols, GmlPair cells[]) {
		hei = (hei / rows) * rows;
		wid = (wid / cols) * cols;
		char map[][] = new char[hei + 1][wid + 1];
		map[0][0] = SdSr;
		map[0][map[0].length - 1] = SdSl;
		map[map.length - 1][0] = SuSr;
		map[map.length - 1][map[map.length - 1].length - 1] = SuSl;
		for (int a = 1; a < map.length - 1; a++) {
			map[a][0] = Sud;
			map[a][map[a].length - 1] = Sud;
		}
		for (int b = 1; b < map[0].length - 1; b++) {
			map[0][b] = Srl;
			map[map.length - 1][b] = Srl;
		}
		int rowh = map.length / rows;
		int colw = map[0].length / cols;
		for (int a = 0; a < map.length; a++) {
			for (int b = 0; b < map[a].length; b++) {
				if (a == 0 || b == 0 || a == map.length - 1
						|| b == map[0].length - 1) {
					if (a == 0 && (b == 0 || b == map[0].length - 1))
						continue;
					else if (a == map.length - 1
							&& (b == 0 || b == map[0].length - 1))
						continue;
					else if (a == 0 && b % colw == 0)
						map[a][b] = SdSrl;
					else if (a == map.length - 1 && b % colw == 0)
						map[a][b] = SuSrl;
					else if (b == 0 && a % rowh == 0)
						map[a][b] = SudSr;
					else if (b == map[0].length - 1 && a % rowh == 0)
						map[a][b] = SudSl;
				} else if (a % rowh == 0 && b % colw == 0 && a != 0 && b != 0
						&& a != map.length - 1 && b != map[0].length - 1)
					map[a][b] = SudSrl;
				else if (a % rowh == 0)
					map[a][b] = Srl;
				else if (b % colw == 0)
					map[a][b] = Sud;
			}
		}
		for (int a = 0; a < rows; a++) {
			for (int b = 0; b < cols; b++) {
				insert(map, choose(a, b, cells), colw * b + 1, rowh * a + 1,
						colw * (b + 1) - 1, rowh * (a + 1) - 1);
			}
		}
		for (int a = 0; a < map.length; a++) {
			for (int b = 0; b < map[a].length; b++) {
				System.out.print(map[a][b]);
			}
			System.out.println();
		}

	}

	public void insert(char map[][], String s, int minx, int miny, int maxx,
			int maxy) {
		char c[][] = new char[maxy - miny + 1][maxx - minx + 1];
		Vector v = new Vector();
		while (s.length() > c[0].length) {
			int ind = Math.min(c[0].length, s.substring(0, c[0].length)
					.lastIndexOf(" "));
			if (ind < 0)
				ind = c[0].length;
			v.addElement(s.substring(0, ind));
			s = s.substring(ind).trim();
		}
		v.addElement(s);
		int rowoff = Math.max(0, c.length / 2 - v.size() / 2);
		for (int a = 0; a < c.length && a < v.size(); a++) {
			String vs = (String) v.elementAt(a);
			int coloff = Math.max(0, c[a].length / 2 - vs.length() / 2);
			for (int b = 0; b < c[a].length && vs.length() > b; b++) {
				map[miny + a + rowoff][minx + b + coloff] = vs.charAt(b);
			}
		}
	}

	public String choose(int r, int c, GmlPair cells[]) {
		// perfect matches
		Vector v = new Vector();
		for (int a = 0; a < cells.length; a++) {
			GmlPair nr[] = cells[a].getAllByName("row");
			if (nr.length == 0)
				continue;
			GmlPair nc[] = cells[a].getAllByName("col");
			if (nc.length == 0)
				continue;
			if (nr[0].getDouble() == r && nc[0].getDouble() == c
					&& nr[0].isDouble() && nc[0].isDouble())
				v.addElement(cells[a]);
		}
		// row or col match
		if (v.size() == 0) {
			for (int a = 0; a < cells.length; a++) {
				GmlPair nr[] = cells[a].getAllByName("row");
				if (nr.length == 0)
					continue;
				GmlPair nc[] = cells[a].getAllByName("col");
				if (nc.length == 0)
					continue;
				if (nr[0].getDouble() == r && !nc[0].isDouble())
					v.addElement(cells[a]);
				else if (!nr[0].isDouble() && nc[0].getDouble() == c)
					v.addElement(cells[a]);
			}
		}
		// any any matches
		if (v.size() == 0) {
			for (int a = 0; a < cells.length; a++) {
				GmlPair nr[] = cells[a].getAllByName("row");
				if (nr.length == 0)
					continue;
				GmlPair nc[] = cells[a].getAllByName("col");
				if (nc.length == 0)
					continue;
				if (!nr[0].isDouble() && !nc[0].isDouble())
					v.addElement(cells[a]);
			}
		}
		// choose among matches
		if (v.size() > 0) {
			GmlPair g = (GmlPair) v.elementAt((int) (v.size() * Math.random()));
			GmlPair n[] = g.getAllByName("val");
			if (n.length == 0)
				return "ERROR";
			GmlPair rep[] = g.getAllByName("repeat");
			for (int a = 0; a < cells.length && rep.length <= 0; a++) {
				if (cells[a] == g)
					cells[a] = new GmlPair("used", 0);
			}
			return n[0].getString();
		} else
			return "ERROR no cells match";
	}

}

/*
 * datafile = bingo [ maxrow 5 maxcol 5 width 79 height 60 cell [ row any col
 * any val "BONUS FREE"] cell [ row 1 col any val "1" ] cell [ row 3 col 3 val
 * "Free" ] ]
 */