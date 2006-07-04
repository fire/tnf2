package aj.smug;

import java.util.Vector;

import aj.misc.GmlPair;
import aj.misc.Stuff;

public class Planet {
	String loc, name;

	double agr, tech, min, ind;

	int x, y;

	String nick;

	int law;

	String type;

	String gov;

	public Planet(String n, String l, double a, double t, double m, double i,
			String nic, int la, String typ, String go) {
		nick = nic;
		law = la;
		type = typ;
		gov = go;
		name = n;
		loc = l;
		agr = a;
		tech = t;
		min = m;
		ind = i;
		String tt[] = Stuff.getTokens(loc, "x");
		if (tt.length >= 2)
			try {
				x = Integer.parseInt(tt[0]);
				y = Integer.parseInt(tt[1]);
			} catch (NumberFormatException nfe) {
			}
	}

	public static Planet parse(GmlPair g) {
		String loc = "EMPTY", name = "EMPTY";
		double agr = -1, tech = -1, min = -1, ind = -1;

		GmlPair n = g.getOneByName("name");
		if (n != null)
			name = n.getString();

		n = g.getOneByName("loc");
		if (n != null)
			loc = n.getString();
		String nick = "";
		n = g.getOneByName("nick");
		if (n != null)
			nick = n.getString();
		String type = "";
		n = g.getOneByName("type");
		if (n != null)
			type = n.getString();
		String gov = "";
		n = g.getOneByName("gov");
		if (n != null)
			gov = n.getString();
		int law = 1;
		n = g.getOneByName("law");
		if (n != null)
			law = (int) n.getDouble();
		n = g.getOneByName("agr");
		if (n != null)
			agr = n.getDouble();
		n = g.getOneByName("tech");
		if (n != null)
			tech = n.getDouble();
		n = g.getOneByName("min");
		if (n != null)
			min = n.getDouble();
		n = g.getOneByName("ind");
		if (n != null)
			ind = n.getDouble();
		return new Planet(name, loc, agr, tech, min, ind, nick, law, type, gov);
	}

	public GmlPair toGml() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("name", name);
		v.addElement(g);
		g = new GmlPair("loc", loc);
		v.addElement(g);
		g = new GmlPair("nick", nick);
		v.addElement(g);
		g = new GmlPair("law", law + "");
		v.addElement(g);
		g = new GmlPair("type", type);
		v.addElement(g);
		g = new GmlPair("gov", gov);
		v.addElement(g);
		g = new GmlPair("agr", agr);
		v.addElement(g);
		g = new GmlPair("tech", tech);
		v.addElement(g);
		g = new GmlPair("min", min);
		v.addElement(g);
		g = new GmlPair("ind", ind);
		v.addElement(g);
		g = new GmlPair("Planet", v);
		return g;
	}

	public double getBaseMod(String s) {
		s = s.toUpperCase();
		if (s.equals("AGR"))
			return agr;
		if (s.equals("TECH"))
			return tech;
		if (s.equals("MIN"))
			return min;
		if (s.equals("IND"))
			return ind;
		System.out.println("MyError: unknown industry: " + s);
		return 0;
	}

	public double getDist(Planet p) {
		return Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
	}
}
