package aj.nf;

import java.util.Vector;

/**
 * 
 * @author judda
 * @created July 21, 2000
 */
public class MarketItem {
	static double DEFAULTTEMP = 9999;

	static double DEFAULTPRICE = 1000;

	double REFINEVALUE = 1.2;

	double PRODUCEVALUE = 1.15;

	int MAXDEMAND = 3;

	double liquidTemp = DEFAULTTEMP;

	double gasTemp = DEFAULTTEMP;

	String type;

	String name;

	int amt;

	double oldPrice;

	double price;

	double basePrice;

	double baseMult = 1;

	double change;

	int goalSupply;

	boolean nativeElement = false;

	String refinedFrom = "";

	double occuranceRate = 0;

	Vector producedFrom = new Vector();

	static int count = 0;

	int id;

	public boolean isSolidAtTemp(double d) {
		return liquidTemp > d;
	}

	public boolean isLiquidAtTemp(double d) {
		return liquidTemp < d && gasTemp > d;
	}

	public boolean isGasAtTemp(double d) {
		return gasTemp < d;
	}

	public MarketItem(int i, String t, String n, double bp, int gs, int a,
			double p, boolean ne, String rf, double occ, Vector pf) {
		id = i;
		if (count < id)
			count = id + 1;
		if (id < 0) {
			count++;
			id = count;
		}
		name = n;
		amt = a;
		oldPrice = price = p;
		basePrice = bp;
		goalSupply = gs;
		type = t;
		nativeElement = ne;
		refinedFrom = rf;
		occuranceRate = occ;
		producedFrom = pf;
	}

	public String getId() {
		return "M" + id;
	}

	public boolean isRefined() {
		return refinedFrom != null && refinedFrom.length() != 0;
	}

	public boolean isProduced() {
		return producedFrom != null && producedFrom.size() != 0;
	}

	public String getRefineMaterial() {
		return refinedFrom;
	}

	public int getUnitSize() {
		int total = 0;
		for (int a = 0; a < producedFrom.size(); a++) {
			String m = (String) producedFrom.elementAt(a);
			int amt = 1;
			try {
				if (m.indexOf("x") > 0)
					amt = Integer.parseInt(m.substring(0, m.indexOf("x")));
			} catch (NumberFormatException NFE) {
			}
			total += amt;
		}
		return total;
	}

	public Vector getProducedMaterial() {
		return (Vector) producedFrom.clone();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public int getAmount() {
		return amt;
	}

	public double getPrice() {
		return price;
	}

	public void sell(int a) {
		amt += a;
		adjust();
	}

	public void buy(int a) {
		amt = Math.max(0, amt - a);
		adjust();
	}

	public void autoPrice() {
		if (isRefined()) {
			MarketItem mi = Universe.getMarket().getItemByName(
					getRefineMaterial());
			double np = mi.basePrice;
			basePrice = np * REFINEVALUE;
			if (baseMult > 0)
				basePrice *= baseMult;
		} else if (isProduced()) {
			int num = 0;
			double tot = 0;
			double max = 0;
			Vector v = getProducedMaterial();
			for (int a = 0; a < v.size(); a++) {
				String s = (String) v.elementAt(a);
				MarketItem mi = Universe.getMarket().getItemByName(s);
				tot += mi.basePrice;
				num++;
				max = Math.max(max, mi.basePrice);
			}
			tot += max;
			num++;
			double np = 1.0 * tot / num * Math.pow(PRODUCEVALUE, num);
			if (np > 0)
				basePrice = np;
			if (baseMult > 0)
				basePrice *= baseMult;
		} else {
		}
		price = basePrice;
	}

	public void adjust() {
		int amt = Math.max(1, this.amt);
		double demand = Math.max(1 / MAXDEMAND, Math.min(MAXDEMAND, 1.0
				* goalSupply / amt));
		price = basePrice * demand;
		price = price - (price - oldPrice) * .20;// price drift
		change = price - oldPrice;
	}

	public String toDisplay() {
		String fixedname = getId() + " " + name + "                         ";
		fixedname = fixedname.substring(0, 20);
		String fixedprice = "            " + Stuff.money(price, 2);
		fixedprice = fixedprice.substring(fixedprice.length() - 12, fixedprice
				.length());
		String fixedchange = "            " + (change >= 0 ? "+" : "")
				+ Stuff.trunc(change, 2);
		fixedchange = fixedchange.substring(fixedchange.length() - 12,
				fixedchange.length());
		String fixedamt = "            " + amt;
		fixedamt = fixedamt
				.substring(fixedamt.length() - 12, fixedamt.length());
		String make = "";
		if (isProduced()) {
			make = " Produced ";
			Vector v = getProducedMaterial();
			for (int a = 0; a < v.size(); a++) {
				if (a != 0)
					make += ", ";
				MarketItem mi = Universe.getMarket().getItemByName(
						(String) v.elementAt(a));
				if (mi != null) {
					make += mi.getName();
				} else {
					make += v.elementAt(a);
				}
			}
		}
		if (isRefined()) {
			MarketItem mi = Universe.getMarket().getItemByName(
					getRefineMaterial());
			if (mi != null) {
				make = " Refined " + mi.getName();
			} else {
				make = " Refined " + getRefineMaterial();
			}
		}
		return "" + fixedname + fixedamt + fixedprice + fixedchange + " "
				+ make;
	}

	public String toString() {
		return toGmlPair().toString();
	}

	public GmlPair toGmlPair() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("name", name);
		v.addElement(g);
		g = new GmlPair("id", id);
		v.addElement(g);
		g = new GmlPair("resType", type);
		v.addElement(g);
		g = new GmlPair("value", Stuff.trunc(basePrice, 2));
		v.addElement(g);
		g = new GmlPair("targetquantity", goalSupply);
		v.addElement(g);
		g = new GmlPair("quantity", amt);
		v.addElement(g);
		g = new GmlPair("price", Stuff.trunc(price, 2));
		v.addElement(g);
		if (liquidTemp != DEFAULTTEMP) {
			g = new GmlPair("liquidTemp", liquidTemp);
			v.addElement(g);
		}
		if (gasTemp != DEFAULTTEMP) {
			g = new GmlPair("gasTemp", gasTemp);
			v.addElement(g);
		}
		if (baseMult != 1) {
			g = new GmlPair("baseMult", baseMult);
			v.addElement(g);
		}
		if (nativeElement) {
			g = new GmlPair("nativeElement", occuranceRate);
			v.addElement(g);
		}
		if (isRefined()) {
			if (Universe.getMarketValue(refinedFrom) == 0) {
				System.out.println("MyError: Bad material in refined "
						+ refinedFrom + " in " + name);
			}
			g = new GmlPair("refined", refinedFrom);
			v.addElement(g);
		}
		if (isProduced()) {
			String slist = "";
			for (int a = 0; a < producedFrom.size(); a++) {
				if (Universe.getMarketValue((String) producedFrom.elementAt(a)) == 0) {
					System.out.println("MyError: Bad material in produced "
							+ producedFrom.elementAt(a) + " of " + name);
				}
				slist += (String) producedFrom.elementAt(a);
				if (producedFrom.size() > a + 1)
					slist += ",";
			}
			g = new GmlPair("producedFrom", slist);
			v.addElement(g);
		}
		return new GmlPair("marketitem", v);
	}

	public static MarketItem parse(GmlPair g) {

		if (!g.getName().equalsIgnoreCase("MarketItem")) {
			return null;
		}
		int id = -1;
		GmlPair n = g.getOneByName("id");
		if (n != null)
			id = (int) n.getDouble();

		String na = "UNNAMED";
		n = g.getOneByName("name");
		if (n != null)
			na = n.getString();

		String re = "X";
		n = g.getOneByName("resType");
		if (n != null)
			re = n.getString();

		double tp = DEFAULTPRICE;
		n = g.getOneByName("value");
		if (n != null)
			tp = n.getDouble();

		int tq = 5000;
		n = g.getOneByName("targetquantity");
		if (n != null)
			tq = (int) n.getDouble();

		int q = 5000;
		n = g.getOneByName("quantity");
		if (n != null)
			q = (int) n.getDouble();

		double p = DEFAULTPRICE;
		n = g.getOneByName("price");
		if (n != null)
			p = n.getDouble();

		double occ = 0;
		boolean ne = false;
		n = g.getOneByName("nativeElement");
		if (n != null) {
			ne = true;
			occ = n.getDouble();
		}
		String rf = "";
		n = g.getOneByName("refined");
		if (n != null) {
			rf = n.getString();
			String lookup = rf;
			if (lookup.indexOf("x") >= 0)
				lookup = lookup.substring(lookup.indexOf("x") + 1);
		}
		Vector pf = new Vector();
		n = g.getOneByName("producedFrom");
		if (n != null) {
			String tt[] = Stuff.getTokens(n.getString(), ", \t");
			for (int a = 0; a < tt.length; a++) {
				pf.addElement(tt[a]);
				String lookup = tt[a];
				if (lookup.indexOf("x") >= 0)
					lookup = lookup.substring(lookup.indexOf("x") + 1);
			}
		}
		MarketItem mi = new MarketItem(id, re, na, tp, tq, q, p, ne, rf, occ,
				pf);

		n = g.getOneByName("baseMult");
		if (n != null) {
			mi.baseMult = n.getDouble();
		}

		n = g.getOneByName("liquidTemp");
		if (n != null)
			mi.liquidTemp = n.getDouble();

		n = g.getOneByName("gasTemp");
		if (n != null)
			mi.gasTemp = n.getDouble();

		return mi;
	}
}
