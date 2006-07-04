package aj.nf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

/**
 * 
 * @author judda
 * @created July 21, 2000
 */

public class Body {

	static double DEPLETERATE = 1.0 / 500;

	static String MATSEP = ",";

	static double AU = 1.496E11;

	Vector geophysical = new Vector();

	Vector thermal = new Vector();

	Vector resource = new Vector();

	Vector usedResources;// loc,name,amount

	Vector colonies;// loc,size

	boolean solid;

	String name;

	Location loc;

	String orbits;

	double radious;

	String fileName;

	int numsect = 4;

	double maxtemp, mintemp;

	double atmos, weather, tectonic, radation;

	Vector solids = new Vector();

	Vector liquids = new Vector();

	Vector gasses = new Vector();

	int size;

	double mass;

	double surfrad;

	String resourceTypes;

	Vector depleted = new Vector();

	public double getGravity() {
		return 6.67e-11 * mass / (surfrad * surfrad) / 9.8;
	}

	public String getSectorScan(Active A, int sec) {
		String h1 = "   ", h2 = "   ";
		for (int a = 0; a < size * 2; a++) {
			if (a % 10 == 0)
				h1 += a / 10;
			else
				h1 += " ";
			h2 += a % 10;
		}
		String EOL = "_";
		String r = "";
		r += "Name:" + name + EOL;
		r += "Location:" + loc + ".S" + sec + EOL;
		r += "Size:" + size + EOL;
		r += "Gravity:" + Stuff.trunc(getGravity(), 2) + EOL;
		if (A.hasTech("T43"))
			r += "Atmosphere:" + atmos + EOL;
		if (A.hasTech("T39"))
			r += "Temp:" + mintemp + " to " + maxtemp + EOL;
		if (A.hasTech("T43"))
			r += "Weather:" + weather + EOL;
		if (A.hasTech("T41"))
			r += "Tectonics:" + tectonic + EOL;
		if (A.hasTech("T42"))
			r += "Radiation:" + radation + EOL;
		if (sec > 3 || sec < 0)
			return r;
		if (A.hasTech("T38")) {
			r += EOL + "Geo" + EOL;
			r += h1 + EOL + h2 + EOL + EOL;
			for (int a = 0; a < size * 2; a++) {
				String t = (String) geophysical.elementAt(a);
				if (a % 10 == 0)
					r += a / 10;
				else
					r += " ";
				r += a % 10 + " ";
				r += t.substring(0 + size * 2 * sec, size * 2 * sec + size * 2)
						+ EOL;
			}
		}
		if (A.hasTech("T39")) {
			r += EOL + "Threm" + EOL;
			r += h1 + EOL + h2 + EOL + EOL;
			for (int a = 0; a < size * 2; a++) {
				String t = (String) thermal.elementAt(a);
				if (a % 10 == 0)
					r += a / 10;
				else
					r += " ";
				r += a % 10 + " ";
				r += t.substring(0 + size * 2 * sec, size * 2 * sec + size * 2)
						+ EOL;
			}
		}
		if (A.hasTech("T40")) {
			r += EOL + "Res" + EOL;
			r += h1 + EOL + h2 + EOL + EOL;
			for (int a = 0; a < size * 2; a++) {
				String t = (String) resource.elementAt(a);
				if (!A.hasTech("T42")) {
					while (t.indexOf("R") > 0) {
						t = t.substring(0, t.indexOf("R")) + "H"
								+ t.substring(t.indexOf("R") + 1);
					}
					while (t.indexOf("r") > 0) {
						t = t.substring(0, t.indexOf("r")) + "h"
								+ t.substring(t.indexOf("r") + 1);
					}
				}
				if (a % 10 == 0)
					r += a / 10;
				else
					r += " ";
				r += a % 10 + " ";
				String n = t.substring(0 + size * 2 * sec, size * 2 * sec
						+ size * 2)
						+ EOL;
				r += n;
			}
		}
		return r;
	}

	public String toSaveString() {
		return toGmlPair().toString();
	}

	public GmlPair toGmlPair() {
		if (size < 1)
			return null;
		Vector v = new Vector();
		GmlPair g;
		g = new GmlPair("name", name);
		v.addElement(g);
		g = new GmlPair("location", loc.toString());
		v.addElement(g);
		g = new GmlPair("orbits", orbits);
		v.addElement(g);
		g = new GmlPair("distance", radious);
		v.addElement(g);
		g = new GmlPair("mass", mass);
		v.addElement(g);
		g = new GmlPair("surfrad", surfrad);
		v.addElement(g);
		g = new GmlPair("solid", (solid ? 1 : 0));
		v.addElement(g);
		if (solid) {
			g = new GmlPair("resourceTypes", resourceTypes);
			v.addElement(g);
			g = new GmlPair("atmos", atmos);
			v.addElement(g);
			g = new GmlPair("weather", weather);
			v.addElement(g);
			g = new GmlPair("mintemp", mintemp);
			v.addElement(g);
			g = new GmlPair("maxtemp", maxtemp);
			v.addElement(g);
			g = new GmlPair("tectonic", tectonic);
			v.addElement(g);
			g = new GmlPair("radation", radation);
			v.addElement(g);
			String sols = "";
			for (int a = 0; a < solids.size(); a++) {
				sols += (String) solids.elementAt(a);
				if (solids.size() > a + 1)
					sols += ",";
			}
			if (sols.length() > 0) {
				g = new GmlPair("Solids", sols);
				v.addElement(g);
			}
			String deps = "";
			for (int a = 0; a < depleted.size(); a++) {
				if (deps.length() > 0)
					deps += ",";
				deps += (String) depleted.elementAt(a);
			}
			if (deps.length() > 0) {
				g = new GmlPair("depleted", deps);
				v.addElement(g);
			}
			sols = "";
			for (int a = 0; a < liquids.size(); a++) {
				sols += (String) liquids.elementAt(a);
				if (liquids.size() > a + 1)
					sols += ",";
			}
			if (sols.length() > 0) {
				g = new GmlPair("liquids", sols);
				v.addElement(g);
			}
		}
		String sols = "";
		for (int a = 0; a < gasses.size(); a++) {
			sols += (String) gasses.elementAt(a);
			if (gasses.size() > a + 1)
				sols += ",";
		}
		if (sols.length() > 0) {
			g = new GmlPair("gasses", sols);
			v.addElement(g);
		}
		g = new GmlPair("body", v);
		return g;
	}

	public boolean hasAtmosphere() {
		return atmos != 0;
	}

	public DPoint getDPoint(long t) {
		// get orbit body and find position
		Body OB = Universe.getBodyByName(orbits);
		DPoint d = new DPoint(0, 0);
		if (OB != null)
			d = OB.getDPoint(t);
		double period = radious;
		// sec/rotation
		d = new DPoint(d.getX() + Math.cos(t / period) * radious, d.getY()
				+ Math.sin(t / period) * radious);
		return d;
	}

	public int getMaterialDensity(Location l, String na) {
		String s = getResourceAtLocation(l);
		String t[] = Stuff.getTokens(s, MATSEP);
		if (t[0].equals("-"))
			return 0;
		for (int a = 0; a < t.length; a++) {
			try {
				int den = Integer.parseInt(t[a].charAt(0) + "");
				t[a] = t[a].substring(2);
				if (t[a].equalsIgnoreCase(na)) {
					return den;
				}
			} catch (NumberFormatException nfe) {
				System.out
						.println("MyError: bad resource found in density check "
								+ t[a]);
			}
		}
		return 0;
	}

	public double myRandom(String s) {
		Random r = new Random(s.hashCode());
		return r.nextDouble();
	}

	public String getResourceAtLocation(Location l) {
		if (l.isFacility())
			l = l.getOutsideFacilityLocation();
		if (l == null)
			return "-";
		if (name.equals("Earth"))
			return ".";
		double mod = 1;
		if (l.isLevel())
			mod = Universe.SUBTERMOD;
		double TRACE = .1 * mod;
		double MOTHERVEIN = .2 * mod;
		double VEINTRACE = .2 * mod;
		double MOTHERTRACE = .5 * mod;
		double MULTIMOTHER = .1;
		double MULTIVEIN = .2;
		double MULTITRACE = .1;
		Random r = new Random((l.toString() + l.toString()).hashCode());
		String t = getRec(l);
		boolean mother = t.toUpperCase().equals(t) && !t.equals(".");
		boolean vein = !t.equals(".") && !t.toUpperCase().equals(t);
		boolean trace = t.equals(".") && r.nextDouble() < TRACE;
		if (mother) {
			vein = vein || r.nextDouble() < MOTHERVEIN;
			trace = trace || r.nextDouble() < MOTHERTRACE;
		}
		if (vein) {
			trace = trace || r.nextDouble() < VEINTRACE;
		}
		Vector pos = new Vector();
		for (int a = 0; a < solids.size(); a++) {
			MarketItem mi = Universe.getMarket().getItemByName(
					(String) solids.elementAt(a));
			if (mi != null && mi.getType().equalsIgnoreCase(t)) {
				pos.addElement(mi);
			}
		}
		Vector allpos = new Vector();
		for (int a = 0; a < solids.size(); a++) {
			MarketItem mi = Universe.getMarket().getItemByName(
					(String) solids.elementAt(a));
			if (mi == null) {
				System.out.println("MyError: unknown material in " + this + " "
						+ solids.elementAt(a));
			} else {
				allpos.addElement(mi);
			}
		}
		String res = "";
		if (mother && pos.size() > 0) {
			do {
				MarketItem mi = (MarketItem) pos
						.elementAt((int) (pos.size() * r.nextDouble()));
				if (res.indexOf("x" + mi.getName()) < 0) {
					if (res.length() > 0)
						res += MATSEP;
					res += ((int) (r.nextDouble() * 2) + 4) + "x"
							+ mi.getName();
				}
			} while (r.nextDouble() < MULTIMOTHER);
			pos = allpos;
			// vein in mother can have any
		}
		if (vein && pos.size() > 0) {
			do {
				MarketItem mi = (MarketItem) pos
						.elementAt((int) (pos.size() * r.nextDouble()));
				if (res.indexOf("x" + mi.getName()) < 0) {
					if (res.length() > 0) {
						res += MATSEP;
					}
					res += ((int) (r.nextDouble() * 2) + 2) + "x"
							+ mi.getName();
				}
			} while (r.nextDouble() < MULTIVEIN);
			pos = allpos;
		}
		if (trace && pos.size() > 0) {
			do {
				MarketItem mi = (MarketItem) allpos.elementAt((int) (allpos
						.size() * r.nextDouble()));
				if (res.indexOf("x" + mi.getName()) < 0) {
					if (res.length() > 0) {
						res += MATSEP;
					}
					res += "1x" + mi.getName();
				}
			} while (r.nextDouble() < MULTITRACE);
		}
		String sur = getSurf(l);
		if (sur.equalsIgnoreCase("w")) {
			for (int a = 0; a < liquids.size(); a++) {
				MarketItem mi = Universe.getMarket().getItemByName(
						(String) liquids.elementAt(a));
				if (mi != null) {
					if (res.length() > 0) {
						res += MATSEP;
					}
					res += "7x" + mi.getName();
				}
			}
		}
		for (int a = 0; a < gasses.size(); a++) {
			MarketItem mi = Universe.getMarket().getItemByName(
					(String) gasses.elementAt(a));
			if (mi != null) {
				if (res.length() > 0) {
					res += MATSEP;
				}
				res += "6x" + mi.getName();
			}
		}
		// if (Main.DEBUG) System.out.println("DEBUG: resource check before
		// deplete "+res);
		if (res.length() == 0)
			return "-";
		// clean out depleted
		Vector v = new Vector();
		while (res.length() > 0) {
			if (res.indexOf(MATSEP) > 0) {
				v.addElement(res.substring(0, res.indexOf(MATSEP)));
				res = res.substring(res.indexOf(MATSEP) + 1);
			} else {
				v.addElement(res);
				res = "";
			}
		}
		// if (Main.DEBUG) System.out.println("DEBUG: Body "+l+" has depleted
		// size of "+depleted.size());
		for (int a = 0; a < depleted.size(); a++) {
			String m = (String) depleted.elementAt(a);
			String mamnt = m.substring(0, m.indexOf("x"));
			String mname = m.substring(m.indexOf("x") + 1);
			String mloc = mname.substring(mname.indexOf("x") + 1);
			mname = mname.substring(0, mname.indexOf("x"));
			// if (Main.DEBUG) System.out.println("DEBUG: "+m+" amt="+mamnt+"
			// mname="+mname+" mloc="+mloc);
			if (!mloc.equals(l.toString()))
				continue;
			// if (Main.DEBUG) System.out.println("DEBUG: Locaiton "+l+" has
			// depleted "+m);
			v.addElement(mamnt + "x" + mname);
		}
		v = Universe.cleanXList(v);
		res = "";
		for (int a = 0; a < v.size(); a++) {
			if (res.length() > 0)
				res += MATSEP;
			res += v.elementAt(a);
		}
		while (res.indexOf("x") >= 0)
			res = res.substring(0, res.indexOf("x")) + "."
					+ res.substring(res.indexOf("x") + 1);
		// if (Main.DEBUG) System.out.println("DEBUG: resource check after
		// deplete "+res);
		// add liquids && gasses (after deplete so they don't deplete)
		return res;
	}

	public void consume(String materialname, int amnt, Location lo) {
		int den = Universe.getMaterialDensity(lo, materialname);
		if (den > 5)
			return;// don't deplete gass or liquids
		if (Main.DEBUG)
			System.out
					.println("DEBUG: deplete check for " + materialname + " "
							+ amnt + " chance="
							+ Math.pow(1 - DEPLETERATE, amnt) + "%");
		boolean depleteIt = Math.pow(1 - DEPLETERATE, amnt) < Math.random();
		if (depleteIt) {
			if (Main.DEBUG)
				System.out.println("DEBUG: deplete " + materialname);
			depleted.addElement("-1x" + materialname + "x" + lo.toString());
			depleted = Universe.mergeXList(depleted);
		}
	}

	public Body(double mass, double surfrad, String n, Location l, String o,
			double r, boolean sol, double at, double we, double mint,
			double maxt, double tet, double rad, Vector sv, Vector lv, Vector gv) {
		this.mass = mass;
		this.surfrad = surfrad;
		solid = sol;
		name = n;
		loc = l;
		radious = r;
		orbits = o;
		atmos = at;
		weather = we;
		mintemp = mint;
		maxtemp = maxt;
		tectonic = tet;
		radation = rad;
		solids = sv;
		liquids = lv;
		gasses = gv;
		// surface of circle = 4*PI*r^2
		double EARTHSIZE = 32;
		// 32=256x64
		double ess = 5.11e14 / (EARTHSIZE * EARTHSIZE * 4);
		double sa = 4 * Math.PI * Math.pow(surfrad, 2);
		int ts = (int) (sa / ess);
		// based on earth 256x64 map (sector size=64) total square = 16384x4,
		fileName = "Body_" + name + ".txt";
		size = (int) Math.pow(ts / 4, .5);
		// System.out.println("name "+name+" loc "+loc+" size "+size);
		if (solid) {
			readFile();
		}
	}

	public void saveFile() {
		// System.out.println("DEBUG: saving planet data file ");
		String s = "GoePhysical\n";
		Vector v = geophysical;
		for (int a = 0; a < v.size(); a++) {
			s += v.elementAt(a) + "\n";
		}
		s += "END\n\nThermal\n";
		v = thermal;
		for (int a = 0; a < v.size(); a++) {
			s += v.elementAt(a) + "\n";
		}
		s += "END\n\nResource\n";
		v = resource;
		for (int a = 0; a < v.size(); a++) {
			s += v.elementAt(a) + "\n";
		}
		s += "END\n";
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(Main.DIRBODIES
					+ fileName));
			pw.println(s);
			pw.close();
		} catch (IOException ioe) {
			System.out.println("MyError: in saving planet");
			System.exit(0);
		}
	}

	public void setSurfaceLocation(Location l, char ch) {
		if (Main.DEBUG)
			System.out.println("DEBUG: changing planet surface to " + ch);
		if (l == null)
			return;
		if (Main.DEBUG)
			System.out.println("DEBUG: changing planet surface 2 to " + ch);
		String t = "";
		int s = l.getSector();
		int r = l.getRow();
		r = Math.max(0, Math.min(r, size * 2 - 1));
		int c = l.getCol() + s * size * 2;
		c = Math.max(0, Math.min(c, size * 2 * 4 - 1));
		if (r >= geophysical.size()) {
			System.out.println("MyError: surf row loc out of bounds (" + r
					+ ")" + l);
		}
		t = (String) geophysical.elementAt(r);
		if (c >= t.length()) {
			System.out.println("MyError: surf col loc out of bounds (" + c
					+ ")" + l);
		}
		if (Main.DEBUG)
			System.out.println("DEBUG: changing planet surface 3 to " + ch);
		t = t.substring(0, c) + ch + t.substring(c + 1, t.length());
		geophysical.setElementAt(t, r);
		if (Main.DEBUG)
			System.out.println("DEBUG: changing planet surface 4 to " + ch);
		saveFile();
		if (Main.DEBUG)
			System.out.println("DEBUG: changing planet surface 5 to " + ch);
	}

	public String getSystemReport() {
		String res = loc.toString() + "  " + name;
		if (orbits.equalsIgnoreCase("na"))
			res += " a Star";
		else if (!solid)
			res += " a gas giant";
		else if (loc.isPlanet())
			res += " a size " + size + " planet";
		else if (loc.isMoon() && loc.getMoon().toUpperCase().startsWith("A"))
			res += " a size " + size + " minor planet";
		else if (loc.isMoon() && !loc.getMoon().toUpperCase().startsWith("A"))
			res += " a size " + size + " moon";
		if (!orbits.equals("na")) {
			res += " orbiting " + orbits + " at distance "
					+ Stuff.trunc(radious / AU, 2) + " AU  1G =";
			double acc = .61;
			double t = Math.pow(radious / 2 / acc, .5) * 2;
			t *= 1000;
			if (t > Universe.MONTH)
				res += Stuff.trunc(t / Universe.MONTH, 2) + " Months";
			else if (t > Universe.DAY)
				res += Stuff.trunc(t / Universe.DAY, 2) + " Days";
			else
				res += Stuff.trunc(t / Universe.HOUR, 2) + " Hours";
		}
		return res;

	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return loc;
	}

	public int getMaxCol() {
		return size * 2;
	}

	public int getMaxRow() {
		return size * 2;
	}

	public int getMaxLevel() {
		return size / 2;
	}

	public String getRealSurf(Location l) {
		String t = "";
		int s = l.getSector();
		int r = l.getRow();
		r = Math.max(0, Math.min(r, size * 2 - 1));
		int c = l.getCol() + s * size * 2;
		c = Math.max(0, Math.min(c, size * 2 * 4 - 1));
		if (r >= geophysical.size()) {
			System.out.println("MyError: surf row loc out of bounds (" + r
					+ ")" + l);
		}
		t = (String) geophysical.elementAt(r);
		if (c >= t.length()) {
			System.out.println("MyError: surf col loc out of bounds (" + c
					+ ")" + l);
		}
		String ss = t.substring(c, c + 1);
		return ss;
	}

	public String getSurf(Location l) {
		String ss = getRealSurf(l);
		if (ss.equalsIgnoreCase("C"))
			ss = "p";
		return ss;
	}

	public String getRec(Location l) {
		String t = "";
		int s = l.getSector();
		int r = l.getRow();
		int ll = l.getLevel();
		r = Math.max(0, Math.min(r, size * 2 - 1));
		int c = l.getCol() + s * size * 2;
		c = Math.max(0, Math.min(c, size * 2 * 4 - 1));
		if (r >= resource.size()) {
			System.out.println("MyError: temp row loc out of bounds (" + r
					+ ")" + l);
		}
		t = (String) resource.elementAt(r);
		if (c >= t.length()) {
			System.out.println("MyError: temp col loc out of bounds (" + c
					+ ")" + l);
		}
		t = t.substring(c, c + 1);
		if (ll > 0) {
			Random rr = new Random((l.toString() + l.toString()).hashCode());
			if (rr.nextDouble() < Universe.RECCHANCE * Universe.SUBTERMOD) {
				int ind = (int) (resourceTypes.length() * rr.nextDouble());
				t = resourceTypes.substring(ind, ind + 1);
				if (rr.nextDouble() > Universe.MOTHERCHANCE
						* Universe.SUBTERMOD) {
					t = t.toLowerCase();
				}
			} else
				t = ".";
		}
		return t;
	}

	public double getTemp(Location l) {
		String t = "";
		int s = l.getSector();
		int r = l.getRow();
		r = Math.max(0, Math.min(r, size * 2 - 1));
		int c = l.getCol() + s * size * 2;
		c = Math.max(0, Math.min(c, size * 2 * 4 - 1));
		if (r >= thermal.size()) {
			System.out.println("MyError: temp row loc out of bounds" + l);
		}
		t = (String) thermal.elementAt(r);
		if (c >= t.length()) {
			System.out.println("MyError: temp col loc out of bounds" + l);
		}
		t = t.substring(c, c + 1);
		try {
			double thrm = Integer.parseInt(t);
			thrm = (maxtemp - mintemp) / 8 * (thrm - 1) + mintemp;
			return thrm;
		} catch (NumberFormatException NFE) {
			System.out.println("MyError: Bad value on temp map." + l);
		}
		System.out.println("MyError: cannot find temp in body");
		return 23.0;
	}

	public void readFile() {
		// read body file
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					Main.DIRBODIES + fileName));
			while (true) {
				String s = br.readLine();
				if (s == null) {
					break;
				}
				s = s.trim();
				if (s.equals("") || s.startsWith(";")) {
					continue;
				}
				if (s.toLowerCase().equals("goephysical")) {
					readGraph(geophysical, br);
				}
				// add C and c
				if (s.toLowerCase().equals("thermal")) {
					readGraph(thermal, br);
				}
				if (s.toLowerCase().equals("resource")) {
					readGraph(resource, br);
				}
			}
		} catch (IOException IOE) {
			System.out.println("MyError: cannot read file " + fileName);
		}
	}

	public void print() {
		System.out.println("Name " + name);
		if (!solid) {
			return;
		}
		System.out.println("Geophysical view");
		System.out.print("   ");
		for (int a = 0; a < size * 2 * numsect; a++) {
			System.out.print(a / 10 + "");
		}
		System.out.println();
		System.out.print("   ");
		for (int a = 0; a < size * 2 * numsect; a++) {
			System.out.print(a % 10 + "");
		}
		System.out.println();
		Vector v = geophysical;
		for (int a = 0; a < v.size(); a++) {
			System.out.println((a < 10 ? "0" + a : "" + a) + ":"
					+ v.elementAt(a));
		}
		v = thermal;
		System.out.println("Thermal view");
		for (int a = 0; a < v.size(); a++) {
			System.out.println((a < 10 ? "0" + a : "" + a) + ":"
					+ v.elementAt(a));
		}
		v = resource;
		System.out.println("Resource view");
		for (int a = 0; a < v.size(); a++) {
			System.out.println((a < 10 ? "0" + a : "" + a) + ":"
					+ v.elementAt(a));
		}
	}

	public void readGraph(Vector h, BufferedReader br) throws IOException {
		while (true) {
			String s = br.readLine();
			if (s == null) {
				return;
			}
			if (s.toLowerCase().equals("end")) {
				return;
			}
			h.addElement(s);
		}
	}

	public static Body parse(GmlPair g) {
		if (!g.getName().equalsIgnoreCase("body")) {
			return null;
		}
		String na = "UNKNOWN";
		GmlPair n = g.getOneByName("name");
		if (n != null)
			na = n.getString();
		n = g.getOneByName("location");
		Location l = null;
		if (n != null)
			l = Location.parse(n.getString());
		if (l == null) {
			System.out
					.println("MyError: Bad location found in body parsing sval"
							+ n.getString() + " nval " + n.getDouble());
			return null;
		}
		boolean s = true;
		n = g.getOneByName("solid");
		if (n != null)
			s = n.getDouble() != 0;
		double d = 1;
		n = g.getOneByName("distance");
		if (n != null)
			d = n.getDouble();
		double mass = 1;
		n = g.getOneByName("mass");
		if (n != null)
			mass = n.getDouble();
		double surfrad = 1;
		n = g.getOneByName("surfrad");
		if (n != null)
			surfrad = n.getDouble();
		String or = null;
		n = g.getOneByName("orbits");
		if (n != null)
			or = n.getString();
		double at = 0, we = 0, mintemp = -273, maxtemp = -273;
		double tet = 0, rad = 0;
		Vector solids = new Vector();
		Vector liquids = new Vector();
		Vector gasses = new Vector();
		n = g.getOneByName("atmos");
		if (n != null)
			at = n.getDouble();
		n = g.getOneByName("weather");
		if (n != null)
			we = n.getDouble();
		n = g.getOneByName("mintemp");
		if (n != null)
			mintemp = n.getDouble();
		n = g.getOneByName("maxtemp");
		if (n != null)
			maxtemp = n.getDouble();
		n = g.getOneByName("tectonic");
		if (n != null)
			tet = n.getDouble();
		n = g.getOneByName("radation");
		if (n != null)
			rad = n.getDouble();
		GmlPair nn[] = g.getAllByName("solids");
		for (int a = 0; a < nn.length; a++) {
			String t[] = Stuff.getTokens(nn[a].getString(), " ,\t");
			for (int b = 0; b < t.length; b++) {
				solids.addElement(t[b]);
			}
		}
		nn = g.getAllByName("liquids");
		for (int a = 0; a < nn.length; a++) {
			String t[] = Stuff.getTokens(nn[a].getString(), " ,\t");
			for (int b = 0; b < t.length; b++) {
				liquids.addElement(t[b]);
			}
		}
		nn = g.getAllByName("gasses");
		for (int a = 0; a < nn.length; a++) {
			String t[] = Stuff.getTokens(nn[a].getString(), " ,\t");
			for (int b = 0; b < t.length; b++) {
				gasses.addElement(t[b]);
			}
		}
		Body b = new Body(mass, surfrad, na, l, or, d, s, at, we, mintemp,
				maxtemp, tet, rad, solids, liquids, gasses);
		n = g.getOneByName("resourceTypes");
		if (n != null)
			b.resourceTypes = n.getString();
		nn = g.getAllByName("depleted");
		for (int a = 0; a < nn.length; a++) {
			String t[] = Stuff.getTokens(nn[a].getString(), " ,\t");
			for (int c = 0; c < t.length; c++) {
				b.depleted.addElement(t[c]);
			}
		}
		return b;
	}

	public static void makeRandom(Location l, String type, String name,
			String orbits) {
		Body BBB = Universe.getBodyByName(name);
		if (BBB != null) {
			System.out.println("Name not unique " + name + ".  Skipping.");
			return;
		}
		Vector v = Universe.allBodies;
		Vector mat = new Vector();
		for (int a = 0; a < v.size(); a++) {
			Body b = (Body) v.elementAt(a);
			Location lll = b.getLocation();
			if (!b.solid && lll.isPlanet() && type.equalsIgnoreCase("Gass")) {
				mat.addElement(b);
			} else if (!b.solid && lll.isSolar()
					&& type.equalsIgnoreCase("Star")) {
				mat.addElement(b);
			} else if (b.solid && lll.isPlanet()
					&& type.equalsIgnoreCase("Planet")) {
				mat.addElement(b);
			} else if (lll.isMoon()
					&& !lll.getMoon().toUpperCase().startsWith("A")
					&& type.equalsIgnoreCase("Moon")) {
				mat.addElement(b);
			} else if (lll.isMoon()
					&& lll.getMoon().toUpperCase().startsWith("A")
					&& type.equalsIgnoreCase("Minor")) {
				mat.addElement(b);
			}
		}

		// min max mass
		double rate = Math.random();
		double min = 0, max = 0;
		for (int a = 0; a < mat.size(); a++) {
			Body b = (Body) mat.elementAt(a);
			if (min == 0 && max == 0)
				max = min = b.mass;
			min = Math.min(b.mass, min);
			max = Math.max(b.mass, max);
		}
		double mass = min + (max - min) * rate;
		// System.out.println(" mass min = "+min+" max="+max+" act="+mass);
		min = 0;
		max = 0;
		for (int a = 0; a < mat.size(); a++) {
			Body b = (Body) mat.elementAt(a);
			if (min == 0 && max == 0)
				max = min = b.surfrad;
			min = Math.min(b.surfrad, min);
			max = Math.max(b.surfrad, max);
		}
		double surfrad = min + (max - min) * rate;
		// System.out.println(" surfrad min = "+min+" max="+max+"
		// act="+surfrad);
		min = 0;
		max = 0;
		for (int a = 0; a < mat.size(); a++) {
			Body b = (Body) mat.elementAt(a);
			if (min == 0 && max == 0)
				max = min = b.radious;
			min = Math.min(b.radious, min);
			max = Math.max(b.radious, max);
		}
		double dist = min + (max - min) * Math.random();
		// System.out.println(" radious/dist min = "+min+" max="+max+"
		// act="+dist);
		boolean solid = !type.equalsIgnoreCase("Gass")
				&& !type.equalsIgnoreCase("STAR");
		double at = -1, we = -1, tet = -1, rad = -1;
		double mintemp = 0, maxtemp = 0;
		if (solid && mass > 9e22) {
			at = (int) (Math.random() * 6 + 1);
			if (at >= 1) {
				we = (int) (Math.random() * 3 + 1);
			}
			if (Math.random() * 2 > 1) {
				tet = (int) (Math.random() * 3);
			}
		}
		if (Math.random() * 3 > 1) {
			rad = (int) (Math.random() * 4);
		}
		if (type.equalsIgnoreCase("MOON")) {
			Body bb = Universe.getBodyByName(orbits);
			double chang = Math.random();
			mintemp = bb.mintemp + 50 * (chang - .5);
			maxtemp = bb.maxtemp + 50 * (chang - .5);
		} else {
			double bestdist = 0;
			for (int a = 0; a < mat.size(); a++) {
				Body bb = (Body) mat.elementAt(a);
				if (bb.radious > bestdist && bb.radious < dist) {
					double chang = Math.random();
					mintemp = bb.mintemp + 50 * (chang - .5);
					maxtemp = bb.maxtemp + 50 * (chang - .5);
					bestdist = bb.radious;
				}
			}
		}
		mintemp = Math.min(552, Math.max(-263, mintemp));
		maxtemp = Math.min(612, Math.max(-253, maxtemp));
		// System.out.println("RESULT "+name+" radious "+dist+" surfrad
		// "+surfrad);
		Body newB = new Body(mass, surfrad, name, l, orbits, dist, solid, at,
				we, mintemp, maxtemp, tet, rad, new Vector(), new Vector(),
				new Vector());
		Universe.register(newB);
		GenPlanet g = new GenPlanet();
		String s[] = new String[1];
		s[0] = name;
		g.generateAll(s);
	}
}
