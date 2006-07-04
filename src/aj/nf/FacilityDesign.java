package aj.nf;

import java.util.Vector;

/**
 * 
 * @author judda
 * @created July 21, 2000
 */
public class FacilityDesign implements ITThing {

	static int count = 0;

	String name;

	Vector partners = new Vector();

	Vector functions = new Vector();

	Vector productionCosts = new Vector();

	double leasePrice = -1;

	int facilityDesignId;

	int mass;

	public FacilityDesign(String n, Vector p, Vector f, Vector pc, double lp) {
		count++;
		facilityDesignId = count;
		name = n;
		partners = p;
		functions = f;
		productionCosts = pc;
		leasePrice = lp;
		calcMass();
	}

	public FacilityDesign(String n, Vector p, Vector f, Vector pc, double lp,
			int sid) {
		facilityDesignId = sid;
		count = Math.max(count, sid + 1);
		name = n;
		partners = p;
		functions = f;
		productionCosts = pc;
		leasePrice = lp;
		calcMass();
	}

	public boolean hasTech(String s) {
		for (int a = 0; a < functions.size(); a++) {
			String sss = (String) functions.elementAt(a);
			if (sss.toUpperCase().equals(s.toUpperCase()))
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

	public String getId() {
		return "FD" + facilityDesignId;
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
				System.out
						.println("MyError: bad buyMarketItems in Facility design");
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
				cnt *= t.getMultiplyerForType("f");
				v.addElement(cnt + "x" + u);
			}
		}
		return Universe.cleanXList(v);
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
			if (partners.elementAt(a).toString().equals(tic)) {
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

	public int getDesignMass() {
		calcMass();
		return mass;
	}

	public String display() {
		return display("none");
	}

	public String display(String tic) {
		boolean pub = isPublic();
		boolean part = isPartner(tic);
		boolean priv = !pub && !part;
		boolean sec = isPartner("none")
				|| name.toUpperCase().startsWith("BASIC");
		pub = pub && !sec;
		part = part && !sec;
		priv = priv && !sec;
		String s = "FDesign:" + getId() + " " + name;
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
		else
			s += " UNKOWN";
		if (pub || part || leasePrice > 0)
			s += " EarthMarketCost " + Stuff.money(getProducedValue() * 1.5, 2);
		calcMass();
		s += " Mass " + mass;
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
			v = getProvideList();
			if (v.size() > 0)
				s += "\n  Provides (";
			for (int a = 0; a < v.size(); a++) {
				String t = (String) v.elementAt(a);
				int cnt1 = 1;
				if (t.indexOf("x") > 0) {
					cnt1 = Integer.parseInt(t.substring(0, t.indexOf("x")));
					t = t.substring(t.indexOf("x") + 1);
				}
				if (Market.getMarketName(t) != null) {
					t = Market.getMarketName(t);
				}
				s += cnt1 + "x" + t;
				if (a + 1 < v.size()) {
					s += ", ";
				}
			}
			if (v.size() > 0)
				s += ")";
			v = getConsumeList();
			if (v.size() > 0)
				s += "\n  Consumes (";
			for (int a = 0; a < v.size(); a++) {
				String t = (String) v.elementAt(a);
				int cnt1 = 1;
				if (t.indexOf("x") > 0) {
					cnt1 = Integer.parseInt(t.substring(0, t.indexOf("x")));
					t = t.substring(t.indexOf("x") + 1);
				}
				if (Market.getMarketName(t) != null) {
					t = Market.getMarketName(t);
				}
				s += cnt1 + "x" + t;
				if (a + 1 < v.size()) {
					s += ", ";
				}
			}
			if (v.size() > 0)
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
			// TODO check for existing actives using design before delete
			// Universe.remove(this);
		}
	}

	public void calcMass() {
		int tot = 0;
		Vector v = getAllProductionCosts();
		for (int a = 0; a < v.size(); a++) {
			String s = (String) v.elementAt(a);
			int cnt = 1;
			try {
				if (s.indexOf("x") > 0) {
					cnt = Integer.parseInt(s.substring(0, s.indexOf("x")));
				}
			} catch (NumberFormatException NFE) {
			}
			tot += cnt;
		}
		mass = tot;
	}

	public boolean containsFunction(String f) {
		for (int a = 0; a < functions.size(); a++) {
			if (((String) functions.elementAt(a)).equals(f)) {
				return true;
			}
		}
		return false;
	}

	public String toSaveString() {
		return toGmlPair().toString();
	}

	public GmlPair toGmlPair() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("Name", name);
		v.addElement(g);
		g = new GmlPair("FacilityDesignId", facilityDesignId);
		v.addElement(g);
		String ss = "";
		for (int a = 0; a < partners.size(); a++) {
			ss += partners.elementAt(a);
			if (a + 1 < partners.size())
				ss += ",";
		}
		g = new GmlPair("Partner", ss);
		v.addElement(g);
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
		g = new GmlPair("Function", ss);
		v.addElement(g);
		ss = "";
		for (int a = 0; a < productionCosts.size(); a++) {
			ss += productionCosts.elementAt(a);
			if (a + 1 < productionCosts.size())
				ss += ",";
		}
		g = new GmlPair("ProductionCost", ss);
		v.addElement(g);
		g = new GmlPair("LeasePrice", leasePrice);
		v.addElement(g);
		g = new GmlPair("FacilityDesign", v);
		return g;
	}

	public static FacilityDesign parse(GmlPair g) {
		if (!g.getName().equals("FacilityDesign")) {
			return null;
		}
		GmlPair n[] = g.getAllByName("Name");
		String na = n[0].getString();
		n = g.getAllByName("FacilityDesignId");
		int fdid = (int) n[0].getDouble();
		n = g.getAllByName("Partner");
		Vector p = new Vector();
		for (int a = 0; a < n.length; a++) {
			if (n[a] == null)
				continue;
			String tt[] = Stuff.getTokens(n[a].getString());
			for (int b = 0; b < tt.length; b++) {
				p.addElement(tt[b]);
			}
		}
		n = g.getAllByName("Function");
		Vector f = new Vector();
		for (int a = 0; a < n.length; a++) {
			if (n[a] == null)
				continue;
			String tt[] = Stuff.getTokens(n[a].getString());
			for (int b = 0; b < tt.length; b++)
				f.addElement(tt[b]);
		}
		n = g.getAllByName("ProductionCost");
		Vector pc = new Vector();
		for (int a = 0; a < n.length; a++) {
			if (n[a] == null)
				continue;
			String tt[] = Stuff.getTokens(n[a].getString());
			for (int b = 0; b < tt.length; b++)
				pc.addElement(tt[b]);
		}
		n = g.getAllByName("LeasePrice");
		double lp = -1;
		if (n.length > 0) {
			lp = n[0].getDouble();
		}
		return new FacilityDesign(na, p, f, pc, lp, fdid);
	}

	public Vector getProvideList() {
		Vector v = new Vector();
		if (hasTech("T84"))
			return v;
		for (int a = 0; a < functions.size(); a++) {
			String s = (String) functions.elementAt(a);
			Tech t = Universe.getTechByName(s);
			if (t == null) {
				System.out
						.println("MyError: bad provide list in facilitydesign "
								+ getId());
				continue;
			}
			Vector vv = t.getProvideList();
			for (int b = 0; b < vv.size(); b++) {
				v.addElement(vv.elementAt(b));
			}
			Vector takeout2 = t.getConsumeList();
			for (int b = 0; b < takeout2.size(); b++) {
				v.addElement("-" + takeout2.elementAt(b));
			}
		}
		v = Universe.cleanXList(v);
		return v;
	}

	public Vector getConsumeList() {
		Vector v = new Vector();
		if (hasTech("T84"))
			return v;
		for (int a = 0; a < functions.size(); a++) {
			String s = (String) functions.elementAt(a);
			Tech t = Universe.getTechByName(s);
			if (t == null) {
				System.out
						.println("MyError: bad provide list in facilitydesign "
								+ getId());
				continue;
			}
			Vector vv = t.getConsumeList();
			for (int b = 0; b < vv.size(); b++) {
				v.addElement(vv.elementAt(b));
			}
			Vector takeout2 = t.getProvideList();
			for (int b = 0; b < takeout2.size(); b++) {
				v.addElement("-" + takeout2.elementAt(b));
			}
		}
		v = Universe.cleanXList(v);
		return v;
	}
}
