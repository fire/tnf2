package aj.nf;

import java.util.Vector;

/**
 * 
 * @author judda
 * @created July 21, 2000
 */

public class ActiveDesign implements ITThing {
	String name;

	Vector partners = new Vector();

	Vector functions = new Vector();

	Vector productionCosts = new Vector();

	double leasePrice = -1;

	int activeDesignId;

	String type;

	double maxendurance, cargocapacity;

	int mass;

	static int count = 0;

	public ActiveDesign(String t, String n, Vector p, Vector f, Vector pc,
			double lp, double me, double cc) {
		maxendurance = me;
		cargocapacity = cc;
		type = t;
		activeDesignId = count;
		count++;
		name = n;
		partners = p;
		functions = f;
		productionCosts = pc;
		leasePrice = lp;
		calcMass();
	}

	public ActiveDesign(String t, String n, Vector p, Vector f, Vector pc,
			double lp, double me, double cc, int sid) {
		maxendurance = me;
		cargocapacity = cc;
		type = t;
		activeDesignId = sid;
		count = Math.max(count, sid + 1);
		name = n;
		partners = p;
		functions = f;
		productionCosts = pc;
		leasePrice = lp;
		calcMass();
	}

	public void setCargoCap(int x) {
		cargocapacity = x;
	}

	public boolean hasTech(String s) {
		for (int a = 0; a < functions.size(); a++) {
			String sss = (String) functions.elementAt(a);
			if (sss.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	public void payLease() {
		if (isPublic())
			return;
		if (leasePrice <= 0)
			return;
		double l = leasePrice / partners.size();
		for (int a = 0; a < partners.size(); a++) {
			String p = (String) partners.elementAt(a);
			Corp c = Universe.getCorpByTick(p);
			if (c == null) {
				partners.removeElement(p);
				a--;
				continue;
			}
			c.receiveLease(name, l);
		}
	}

	public void setLeasePrice(double lp) {
		leasePrice = lp;
		if (leasePrice == 0) {
			partners = new Vector();
			partners.addElement("public");
		}
	}

	public boolean isShip() {
		return type.toLowerCase().indexOf("s") >= 0;
	}

	public boolean isRobot() {
		return type.toLowerCase().indexOf("r") >= 0;
	}

	public String getId() {
		return "AD" + activeDesignId;
	}

	public void redesign() {
		productionCosts = new Vector();
		growRandom();
	}

	public void growRandom() {
		Vector v = getAllProductionCosts();
		Vector additional = new Vector();
		for (int a = 0; a < v.size(); a++) {
			String s = (String) v.elementAt(a);
			try {
				int cnt = 1;
				if (s.indexOf("x") > 0) {
					cnt = Integer.parseInt(s.substring(0, s.indexOf("x")));
					s = s.substring(s.indexOf("x") + 1);
				}
				cnt = (int) (cnt * (Math.random() + .5));
				if (cnt > 0)
					additional.addElement(cnt + "x" + s);
			} catch (NumberFormatException NFE) {
			}
		}
		productionCosts = additional;
		calcMass();
	}

	public void buyMarketItems() {
		Vector v = getAllProductionCosts();
		for (int a = 0; a < v.size(); a++) {
			String s = (String) v.elementAt(a);
			try {
				int cnt = 1;
				if (s.indexOf("x") > 0) {
					cnt = Integer.parseInt(s.substring(0, s.indexOf("x")));
					s = s.substring(s.indexOf("x") + 1);
				}
				Universe.getMarket().buy(s, cnt);
			} catch (NumberFormatException NFE) {
				System.out.println("MyError: bad consume in Active design");
			}
		}
	}

	public Vector getAllProductionCosts() {
		Vector v = (Vector) productionCosts.clone();
		for (int a = 0; a < functions.size(); a++) {
			String s = (String) functions.elementAt(a);
			Tech t = Universe.getTechByName(s);
			if (t == null) {
				continue;
			}
			Vector v2 = t.getProductionCost();
			int b;
			for (b = 0; b < v2.size(); b++) {
				String u = (String) v2.elementAt(b);
				int cnt = 1;
				if (u.indexOf("x") > 0) {
					cnt = Integer.parseInt(u.substring(0, u.indexOf("x")));
					u = u.substring(u.indexOf("x") + 1);
				}
				if (isShip())
					cnt *= t.getMultiplyerForType("s");
				else if (isRobot())
					cnt *= t.getMultiplyerForType("r");
				v.addElement(cnt + "x" + u);
			}
		}
		v = Universe.cleanXList(v);
		return v;
	}

	public double getProducedValue() {
		double d = 0;
		Vector v = getAllProductionCosts();
		for (int a = 0; a < v.size(); a++) {
			String s = (String) v.elementAt(a);
			try {
				int cnt = 1;
				if (s.indexOf("x") > 0) {
					cnt = Integer.parseInt(s.substring(0, s.indexOf("x")));
					s = s.substring(s.indexOf("x") + 1);
				}
				d += Universe.getMarketValue(s) * cnt;
			} catch (NumberFormatException NFE) {
			}
		}
		return d * 1.5;
	}

	public void setName(String nn) {
		name = nn;
	}

	public String getName() {
		return name;
	}

	public boolean keepOnRestart() {
		return isPartner("XXX") && partners.size() == 1;
	}

	public boolean isPublic() {
		return leasePrice == 0 || isPartner("public");
	}

	public boolean isPartner(String tic) {
		for (int a = 0; a < partners.size(); a++) {
			if (partners.elementAt(a).toString().equalsIgnoreCase(tic)) {
				return true;
			}
		}
		return false;
	}

	public boolean isLeased() {
		return leasePrice > 0;
	}

	public double getLeasePrice() {
		return leasePrice;
	}

	public double getDesignEndurance() {
		return maxendurance;
	}

	public int getDesignMass() {
		return mass;
	}

	public String display() {
		return display("none");
	}

	public String display(String tic) {
		boolean pub = isPublic();
		boolean part = isPartner(tic);
		boolean sec = isPartner("none");
		boolean priv = !pub && !part;

		String s = (isShip() ? "Ship" : "Robot") + "Design:" + getId() + " "
				+ name;
		if (pub)
			s += " (PUBLIC)";
		else if (part) {
			s += " (PARTNER of " + partners.size();
			if (leasePrice > 0)
				s += " LEASING at " + Stuff.money(leasePrice, 2) + ")";
			else
				s += " held privately)";
		} else if (sec)
			s += " (SECRET)";
		else if (leasePrice > 0)
			s += " (LEASE for " + Stuff.money(leasePrice, 2) + ")";
		else if (priv)
			s += " (PRIVATE)";
		if (pub || part || leasePrice > 0) {
			s += " EarthMarketCost " + Stuff.money(getProducedValue() * 1.5, 2);
			s += "\n  Specs ( Cargo Capacity " + cargocapacity + ", endurance "
					+ Stuff.trunc(maxendurance, 2);
		}
		s += " Mass " + mass + ")";
		s += "\n  Functions (";
		for (int a = 0; a < functions.size(); a++) {
			String tt = (String) functions.elementAt(a);
			ITThing itt = Universe.getITThingByName(tt);
			if (itt != null)
				s += itt.getName();
			else
				s += tt;
			if (a + 1 < functions.size()) {
				s += ", ";
			}
		}
		s += ")";
		if (pub || part || leasePrice > 0) {
			s += "\n  Production_Cost (";
			Vector v = getAllProductionCosts();
			for (int a = 0; a < v.size(); a++) {
				String t = (String) v.elementAt(a);
				int cnt1 = 1;
				if (t.indexOf("x") > 0) {
					cnt1 = Integer.parseInt(t.substring(0, t.indexOf("x")));
					t = t.substring(t.indexOf("x") + 1);
				}
				s += cnt1 + "x" + t;
				if (a + 1 < v.size()) {
					s += ", ";
				}
			}
			s += ")";
		}
		return s;
	}

	public void addPartner(String tic) {
		if (!partners.contains(tic)) {
			partners.addElement(tic);
		}
	}

	public void removePartner(String tic) {
		partners.removeElement(tic);
		if (partners.size() == 0) {
			// TODO check for existing actives using design
			// if no existion actives then remove.
			// other wise keep
			// keep unless chech working
			// Universe.remove(this);
			// or give to abandonded
		}
	}

	public void calcMass() {
		int tot = 0;
		Vector v = getAllProductionCosts();
		for (int a = 0; a < v.size(); a++) {
			String s = (String) v.elementAt(a);
			try {
				int cnt = 1;
				if (s.indexOf("x") > 0) {
					cnt = Integer.parseInt(s.substring(0, s.indexOf("x")));
				}
				tot += cnt;
			} catch (NumberFormatException NFE) {
			}
		}
		mass = tot;
	}

	public String toSaveString() {
		return toGmlPair().toString();
	}

	public GmlPair toGmlPair() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("Name", name);
		v.addElement(g);
		g = new GmlPair("ActiveDesignId", activeDesignId);
		v.addElement(g);
		String ss = "";
		for (int a = 0; a < partners.size(); a++) {
			ss += partners.elementAt(a);
			if (a + 1 < partners.size())
				ss += ",";
		}
		v.addElement(new GmlPair("Partner", ss));
		ss = "";
		for (int a = 0; a < functions.size(); a++) {
			String tt = (String) functions.elementAt(a);
			ITThing itt = Universe.getITThingByName(tt);
			if (itt != null)
				ss += itt.getId();
			else
				ss += tt;
			if (a + 1 < functions.size())
				ss += ",";
		}
		v.addElement(new GmlPair("Function", ss));
		ss = "";
		for (int a = 0; a < productionCosts.size(); a++) {
			ss += productionCosts.elementAt(a);
			if (a + 1 < productionCosts.size())
				ss += ",";
		}
		v.addElement(new GmlPair("ProductionCost", ss));
		g = new GmlPair("LeasePrice", leasePrice);
		v.addElement(g);
		g = new GmlPair("CargoCapacity", cargocapacity);
		v.addElement(g);
		g = new GmlPair("MaxEndurance", maxendurance);
		v.addElement(g);
		g = new GmlPair("Type", type);
		v.addElement(g);
		g = new GmlPair("ActiveDesign", v);
		return g;
	}

	public boolean checkTechnology(String s) {
		for (int a = 0; a < functions.size(); a++) {
			String t = ((String) functions.elementAt(a)).toLowerCase().trim();
			if (s.toLowerCase().trim().equals(t)) {
				return true;
			}
		}
		return false;
	}

	public static ActiveDesign parse(GmlPair g) {
		if (!g.getName().equals("ActiveDesign")) {
			return null;
		}
		GmlPair n[] = g.getAllByName("Name");
		String na = n[0].getString();
		n = g.getAllByName("ActiveDesignId");
		int rdid = (int) n[0].getDouble();
		n = g.getAllByName("MaxEndurance");
		int me = (int) n[0].getDouble();
		n = g.getAllByName("CargoCapacity");
		int cc = (int) n[0].getDouble();
		n = g.getAllByName("Type");
		String t = n[0].getString();
		n = g.getAllByName("Partner");
		Vector p = new Vector();
		for (int a = 0; a < n.length; a++) {
			String tt[] = Stuff.getTokens(n[a].getString());
			for (int b = 0; b < tt.length; b++) {
				p.addElement(tt[b]);
			}
		}
		n = g.getAllByName("Function");
		Vector f = new Vector();
		for (int a = 0; a < n.length; a++) {
			String tt[] = Stuff.getTokens(n[a].getString());
			for (int b = 0; b < tt.length; b++) {
				f.addElement(tt[b]);
			}
		}
		n = g.getAllByName("ProductionCost");
		Vector pc = new Vector();
		for (int a = 0; a < n.length; a++) {
			String tt[] = Stuff.getTokens(n[a].getString());
			for (int b = 0; b < tt.length; b++) {
				pc.addElement(tt[b]);
			}
		}
		n = g.getAllByName("LeasePrice");
		double lp = -1;
		if (n.length > 0) {
			lp = n[0].getDouble();
		}
		return new ActiveDesign(t, na, p, f, pc, lp, me, cc, rdid);
	}
}
