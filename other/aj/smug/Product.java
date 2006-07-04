package aj.smug;

import java.util.Vector;

import aj.misc.GmlPair;

public class Product {
	String name, base;

	String nick;

	boolean legal;

	boolean hazard;

	double cost;

	public Product(String n, String b, boolean le, double c, boolean ha,
			String ni) {
		name = n;
		base = b;
		legal = le;
		cost = c;
		hazard = ha;
		nick = ni;
	}

	public static Product parse(GmlPair g) {
		String name = "EMPTY", base = "EMPTY";
		boolean legal = true, hazard = false;
		double cost = -1;

		GmlPair n = g.getOneByName("name");
		if (n != null)
			name = n.getString();
		n = g.getOneByName("base");
		if (n != null)
			base = n.getString();
		String nick = "";
		n = g.getOneByName("nick");
		if (n != null)
			nick = n.getString();
		n = g.getOneByName("legal");
		if (n != null)
			legal = (n.getString()).equalsIgnoreCase("true");
		n = g.getOneByName("hazard");
		if (n != null)
			hazard = (n.getString()).equalsIgnoreCase("true");
		n = g.getOneByName("cost");
		if (n != null)
			cost = n.getDouble();
		return new Product(name, base, legal, cost, hazard, nick);
	}

	public GmlPair toGml() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("name", name);
		v.addElement(g);
		g = new GmlPair("nick", nick);
		v.addElement(g);
		g = new GmlPair("base", base);
		v.addElement(g);
		if (!legal) {
			g = new GmlPair("legal", legal + "");
			v.addElement(g);
		}
		if (hazard) {
			g = new GmlPair("hazard", hazard + "");
			v.addElement(g);
		}
		g = new GmlPair("cost", cost);
		v.addElement(g);
		g = new GmlPair("Product", v);
		return g;
	}

	public static void main(String s[]) {
		Product p = new Product("name", "bas", true, 50, false, "nick");
		System.out.println(p.toGml());
	}

}
