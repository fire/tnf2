package aj.nf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * @author judda
 * @created July 21, 2000
 */
public class Universe {

	static long MINUTE = 1000 * 60, HOUR = MINUTE * 60, DAY = HOUR * 24,
			WEEK = DAY * 7, MONTH = DAY * 30;

	static double RECCHANCE = .05;// chance of terrain being resource

	static double MOTHERCHANCE = .05;// chance of resource being mother load

	static double SUBTERMOD = 2;// multiple for subterainum

	static long lastdam = 0;

	static String notice = null;

	static Market market = new Market();

	static Auction auction = new Auction();

	private static Vector allThings = new Vector();

	static Vector allBodies = new Vector();

	static Vector allCorps = new Vector();

	static Vector allITThings = new Vector();

	static Vector actions = new Vector();

	static long time = 0;

	static double intrate = .04;

	static double materialTax = .75;

	static long execTime = WEEK;

	static long interTime = HOUR;// time each active completes uninterupted.

	static long CHECKTIME = DAY;// time between enviroemnt checks and endurance

	// charge

	static boolean holdsPrerequisitsFor(String tick, Tech t) {
		Vector v = t.getAllPrerequisits();
		for (int a = 0; a < v.size(); a++) {
			String tt = (String) v.elementAt(a);
			ITThing itt = Universe.getTechByName(tt);
			if (itt == null)
				return false;
			if (!itt.isPartner(tick) && !itt.isPublic())
				return false;
		}
		return true;
	}

	public static String getSystemScan(String loc) {
		String res = "System Report for " + loc + "_";
		for (int a = 0; a < allBodies.size(); a++) {
			Body b = (Body) allBodies.elementAt(a);
			Location l = b.getLocation();
			if (l.toString().startsWith(loc)) {
				res += "  " + b.getSystemReport() + "_";
			}
		}
		return res;
	}

	public static String getResearchReport(Corp c) {
		if (Main.DEBUG)
			System.out.println("DEBUG: Building research report for "
					+ c.getTick());
		String report = "\nResearch Contracts Available\n---------------------------------\n";
		for (int a = 0; a < allThings.size(); a++) {
			NFObject nfo2 = (NFObject) allThings.elementAt(a);
			if (nfo2 instanceof Facility) {
				Facility f = (Facility) nfo2;
				if (f.hasTech("T69")) {
					if (Main.DEBUG)
						System.out.println("DEBUG: facility " + f.getId()
								+ " has research at " + f.getContractPrice());
					if (f.newContractPrice > 0) {
						if (Main.DEBUG)
							System.out
									.println("DEBUG: price greater than 0 put on list");
						report += f.getResearchReport() + "\n";
					} else if (f.getCorpTick().equalsIgnoreCase(c.getTick())) {
						if (Main.DEBUG)
							System.out
									.println("DEBUG: own facility put on list");
						report += f.getResearchReport() + "\n";
					} else {
						if (Main.DEBUG)
							System.out
									.println("DEBUG: cannot put on list, no price and not own");
					}
				}
			}
		}
		return report;
	}

	public static NFObject getNextResearch(String mytick, double d) {
		NFObject nfo = null;
		double bestprice = d;
		for (int a = 0; a < allThings.size(); a++) {
			NFObject nfo2 = (NFObject) allThings.elementAt(a);
			if (nfo2 instanceof Facility) {
				Facility f = (Facility) nfo2;
				if (!f.isBusy() && f.hasTech("T69")) {
					if (f.getCorpTick().equalsIgnoreCase(mytick)) {
						nfo = nfo2;
						bestprice = 0;
					} else if (f.getContractPrice() > 0
							&& f.getContractPrice() < bestprice) {
						nfo = nfo2;
						bestprice = f.getContractPrice();
					}
				}
			}
		}
		return nfo;
	}

	public static void restart() {
		market.restart();
		auction.restart();
		actions.removeAllElements();// =new Vector();
		allThings.removeAllElements();// =new Vector();
		expireOrders();
		allCorps.removeAllElements();// s=new Vector();
		for (int a = 0; a < allITThings.size(); a++) {
			ITThing i = (ITThing) allITThings.elementAt(a);
			if (i instanceof Tech) {
				Tech t = (Tech) i;
				t.restart();
			} else if (i instanceof ActiveDesign) {
				ActiveDesign ad = (ActiveDesign) i;
				if (!ad.keepOnRestart()) {
					allITThings.removeElement(i);
					a--;
				}
			} else if (i instanceof FacilityDesign) {
				FacilityDesign fd = (FacilityDesign) i;
				if (!fd.keepOnRestart()) {
					allITThings.removeElement(i);
					a--;
				}
			} else if (i instanceof Program) {
				allITThings.removeElement(i);
				a--;
			}
		}
		redesign();
		// signup XFG
		Facility f = new Facility("XXX", "FD1", Location.parse("01.03.S0.0.0"));
		Location ll = Location.parse(f.getLocation() + "." + f.getId());
		Corp c = new Corp("Abandoned", "XXX", ll, "flandar@yahoo.com",
				"23DFasdfopqabn", "SYSOP Access Only");
		add(f);
		register(c);
		f = new Facility("UFO", "FD1", Location.parse("01.03.S0.0.0"));
		ll = Location.parse(f.getLocation() + "." + f.getId());
		c = new Corp("Alien", "UFO", ll, "flandar@yahoo.com", "23DFasdfopqabn",
				"SYSOP Access Only");
		add(f);
		register(c);
		saveCorpIndex();
		save();
		makeTurnReports();
		GenPlanet g = new GenPlanet();
		String s[] = new String[0];
		g.generateAll(s);
		System.exit(0);
	}

	public static void redesign() {
		for (int a = 0; a < allITThings.size(); a++) {
			ITThing i = (ITThing) allITThings.elementAt(a);
			if (i instanceof ActiveDesign) {
				ActiveDesign aa = (ActiveDesign) i;
				aa.redesign();
			}
			if (i instanceof FacilityDesign) {
				FacilityDesign ff = (FacilityDesign) i;
				ff.redesign();
			}
		}
	}

	public Universe() {
	}

	public static int facilityCount(Location l) {
		Body b = getBodyByLocation(l);
		if (b == null)
			return 4;
		String c = b.getRealSurf(l);
		if (c.equals("C"))
			return 4;
		if (c.equals("c"))
			return 3;
		// must count
		Vector v = getNFObjectsByLocation(l);
		int total = 0;
		for (int a = 0; a < v.size(); a++) {
			NFObject nfo = (NFObject) v.elementAt(a);
			if (nfo instanceof Facility)
				total++;
		}
		return total;
	}

	public static boolean locationContainsTechnology(Location l, String t) {
		Vector v = getNFObjectsByLocation(l);
		for (int a = 0; a < v.size(); a++) {
			NFObject nfo = (NFObject) v.elementAt(a);
			if (nfo instanceof Facility) {
				Facility fc = (Facility) nfo;
				if (fc.hasTech(t))
					return true;
			}
		}
		return false;
	}

	public static boolean locationHasTechnology(Location l, String t) {
		if (l.isFacility()) {
			String fac = l.getFac();
			if (!fac.toUpperCase().startsWith("F"))
				fac = "F" + fac;
			NFObject nfo = getNFObjectById(fac);
			if (nfo instanceof Facility) {
				Facility fc = (Facility) nfo;
				return fc.hasTech(t);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static Vector getNFObjectsByLocation(Location l) {
		Vector v = new Vector();
		for (int a = 0; a < allThings.size(); a++) {
			NFObject n = (NFObject) allThings.elementAt(a);
			if (n.getLocation().equalsIgnoreCase(l)) {
				v.addElement(n);
			}
		}
		return v;
	}

	// TODO add 5 gasses 6 liquids for condensor and distilation

	// desnity 0=none, 4=plenty
	public static int getMaterialDensity(Location l, String na) {
		// change loc to outside of facility
		na = Market.getMarketName(na);
		if (na == null) {
			// System.out.println("MyError: bad name in density check");
			return 0;
		}
		Body b = getBodyByLocation(l);
		if (b == null) {
			// System.out.println("MyError: bad locaiton in density check");
			return 0;
		}
		return b.getMaterialDensity(l, na);
	}

	public static String getActionReport(Corp c) {
		String h = "\n--------Upcomming Action Report-------\n";
		String s = "";
		for (int a = 0; a < actions.size(); a++) {
			Action aa = (Action) actions.elementAt(a);
			if (aa.getCorpTick().equalsIgnoreCase(c.getTick())) {
				s += aa.getReport() + "\n";
			}
		}
		if (s.length() > 0)
			s = h + s + "\n";
		Main.logTiming("Aucton report time done");
		return s;
	}

	public static String getNFObjectReport(Corp c) {
		StringBuffer s = new StringBuffer();
		s.append("\n--------NFObject Report-------\n");
		s.append("ID :  Description\n\n");
		Vector v = getNFObjectsByTick(c.getTick());
		// sort by location
		Vector sort = new Vector();

		// TODO sort by GROUP, Location, Nick, ID
		while (v.size() > 0) {
			NFObject nfo = (NFObject) v.elementAt(0);
			for (int a = 1; a < v.size(); a++) {
				NFObject nfo2 = (NFObject) v.elementAt(a);
				String nfoloc = nfo.getLocation().toString();
				String nfoloc2 = nfo2.getLocation().toString();
				if (nfoloc.compareToIgnoreCase(nfoloc2) > 0) {
					nfo = nfo2;
				}
			}
			v.removeElement(nfo);
			sort.addElement(nfo);
		}
		v = sort;

		// TODO make on loop and four StringBuffers for Tech, FacDes, ShipDes,
		// RobDes and Programs

		boolean found = false;
		for (int a = 0; a < v.size(); a++) {
			NFObject n = (NFObject) v.elementAt(a);
			if (n instanceof StockPile) {
				s.append(n.display() + "\n");
				found = true;
			}
		}
		if (found) {
			s.append("\n");
			found = false;
		}
		for (int a = 0; a < v.size(); a++) {
			NFObject n = (NFObject) v.elementAt(a);
			if (n instanceof Prefab) {
				s.append(n.display() + "\n");
				found = true;
			}
		}
		if (found) {
			s.append("\n");
			found = false;
		}
		for (int a = 0; a < v.size(); a++) {
			NFObject n = (NFObject) v.elementAt(a);
			if (n instanceof Facility) {
				s.append(n.display() + "\n");
				found = true;
			}
		}
		if (found) {
			s.append("\n");
			found = false;
		}
		// v = getNFObjectsByTick(c.getTick());
		for (int a = 0; a < v.size(); a++) {
			NFObject n = (NFObject) v.elementAt(a);
			if (n instanceof Active && ((Active) (n)).isShip())
				s.append(n.displayHeader() + "\n");
			// s .append( n.display() + "\n");
		}
		s.append("\n");
		for (int a = 0; a < v.size(); a++) {
			NFObject n = (NFObject) v.elementAt(a);
			if (n instanceof Active && ((Active) (n)).isRobot())
				s.append(n.displayHeader() + "\n");
			// s .append( n.display() + "\n");
		}
		s.append("\n---------Active Logging-----------\n");
		for (int a = 0; a < v.size(); a++) {
			NFObject n = (NFObject) v.elementAt(a);
			if (n instanceof Active && ((Active) (n)).isShip())
				s.append(n.display() + "\n");
		}
		for (int a = 0; a < v.size(); a++) {
			NFObject n = (NFObject) v.elementAt(a);
			if (n instanceof Active && ((Active) (n)).isRobot())
				s.append(n.display() + "\n");
		}
		return s.toString();
	}

	public static String getNotice() {
		if (notice != null) {
			return notice;
		} else {
			try {
				BufferedReader br = new BufferedReader(new FileReader(
						Main.DIRDATA + Main.noticeFileName));
				while (true) {
					String s = br.readLine();
					if (s == null)
						break;
					if (notice == null)
						notice = "";
					notice += s + "\n";
				}
			} catch (IOException IOE) {
				System.out.println("MyError: reading notices");
				notice = "";
			}
			return notice;
		}
	}

	static String saveRank = null;

	public static String getCorpRanking(Corp c, String level, boolean lock) {
		if (lock && saveRank != null)
			return saveRank;
		StringBuffer cRank = new StringBuffer();
		cRank.append("\n------------Corporate Rankings-------------\n");
		cRank
				.append("Tick Cash            Assets          Networth        Profit           R--S--F--PF--SP\n");
		Vector sort1 = (Vector) allCorps.clone();
		while (sort1.size() > 0) {
			Corp best = (Corp) sort1.elementAt(0);
			for (int a = 1; a < sort1.size(); a++) {
				Corp test = (Corp) sort1.elementAt(a);
				if (test.getNetValue() > best.getNetValue()) {
					best = test;
				}
			}
			sort1.removeElement(best);
			String cr = best.getRankReport();
			if (cr == null)
				continue;
			cRank.append(cr + "\n");
		}
		if (lock)
			saveRank = cRank.toString();
		return cRank.toString();
	}

	public static String getMarketReport() {
		String s = market.getFullReport();
		Main.logTiming("Market report time done");
		return s;
	}

	public static String getAuctionReport(Corp c) {
		String s = auction.getFullReport();
		Main.logTiming("Auction report time done");
		return s;
	}

	// TODO fix speed up by cutting out nforeport check.
	// just have PUBLIC, PARNTER, PRIVATE, and SECRET
	// 
	public static StringBuffer getITMarketReport(Corp c, String level,
			String nforeport) {
		level = level.toUpperCase();
		StringBuffer s = new StringBuffer();
		// ALL, OWN, ACTIVE
		s.append("\n--------IT Market Report " + level + "---------\n");
		StringBuffer tec = new StringBuffer();
		StringBuffer fac = new StringBuffer();
		StringBuffer actshi = new StringBuffer();
		StringBuffer actrob = new StringBuffer();
		StringBuffer prog = new StringBuffer();
		for (int a = 0; a < allITThings.size(); a++) {
			ITThing i = (ITThing) allITThings.elementAt(a);
			String dis = i.display(c.getTick());
			if (dis.indexOf("(SECRET") >= 0)
				continue;
			if (level.indexOf("ALL") >= 0) {
				dis += "\n\n";
			} else if (dis.indexOf("(PUBLIC") >= 0
					&& level.indexOf("PUBLIC") >= 0) {
				dis += "\n\n";
			} else if (dis.indexOf("(PRIVATE") >= 0
					&& level.indexOf("PRIVATE") >= 0) {
				dis += "\n\n";
			} else if (dis.indexOf("(PARTNER") >= 0
					&& level.indexOf("PARTNER") >= 0) {
				dis += "\n\n";
			} else if (dis.indexOf("LEAS") >= 0 && level.indexOf("LEASE") >= 0) {
				dis += "\n\n";
			} else if (dis.indexOf("RESEAR") >= 0
					&& level.indexOf("RESEAR") >= 0) {
				dis += "\n\n";
			} else
				dis = "";
			if (i instanceof Tech) {
				tec.append(dis);
			}
			if (i instanceof FacilityDesign) {
				fac.append(dis);
			}
			if (i instanceof ActiveDesign) {
				ActiveDesign AA = (ActiveDesign) i;
				if (!AA.isShip()) {
					actshi.append(dis);
				} else {
					actrob.append(dis);
				}
			}
			if (i instanceof Program) {
				prog.append(dis);
			}
		}
		s.append(tec);
		s.append(fac);
		s.append(actshi);
		s.append(actrob);
		s.append(prog);
		Main.logTiming("ITMarket report time done");
		return s;
	}

	public static void register(Body b) {
		if (!allBodies.contains(b)) {
			allBodies.addElement(b);
		}
	}

	public static void register(Corp c) {
		if (!allCorps.contains(c)) {
			allCorps.addElement(c);
		}
	}

	public static void register(ITThing c) {
		if (!allITThings.contains(c)) {
			allITThings.addElement(c);
		}
	}

	public static void expireOrders() {
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			c.expireOrders();
		}
	}

	public static void executeCorpCommands() {
		allCorps = randomizeVector(allCorps);
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			c.loadOrders();
			c.expireOrders();
			c.payInterest();
			c.payTax();
		}
		activateFacilities();
		boolean done = false;
		while (!done) {
			done = true;
			allCorps = randomizeVector(allCorps);
			for (int a = 0; a < allCorps.size(); a++) {
				Corp c = (Corp) allCorps.elementAt(a);
				done = done & !c.executeOrder();
			}
		}
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			c.delayOrders();
		}
	}

	public static void executeProgramables() {
		System.out.println("Turn length="
				+ Stuff.trunc(1.0 * execTime / DAY, 2) + " days");
		System.out.println("Active intervals="
				+ Stuff.trunc(1.0 * interTime / DAY, 2) + " days");
		Vector act = new Vector();
		Vector dam = new Vector();
		for (int a = 0; a < allThings.size(); a++) {
			NFObject n = (NFObject) allThings.elementAt(a);
			if (n instanceof Active) {
				act.addElement(n);
				dam.addElement(n);
			} else if (n instanceof Facility) {
				dam.addElement(n);
			}
		}
		// lookup corp runlog and timelog
		for (int b = 0; b < act.size(); b++) {
			Active actor = (Active) act.elementAt(b);
			Corp c = Universe.getCorpByTick(actor.getCorpTick());
			if (c == null)
				continue;
			actor.verbose = c.verbose;
			actor.advanceClockTo(time);
		}
		// power check for facilities
		Main.logTiming("Begin Turn ");
		// run turn time
		for (int a = 0; a < execTime / interTime; a++) {
			time += interTime;
			// check action list for completes
			// if (Main.DEBUG) System.out.println("DEBUG Checking actions");
			for (int b = 0; b < actions.size(); b++) {
				// if action item done create and remove
				Action A = (Action) actions.elementAt(b);
				if (A.complete(time)) {
					A.post();
					actions.removeElementAt(b);
					b--;
				}
			}
			// daily enviorment checks for facilities
			// if (Main.DEBUG) System.out.println("DEBUG Checking damage");
			if (lastdam < time) {
				lastdam = time + CHECKTIME;
				for (int b = 0; b < dam.size(); b++) {
					if (dam.elementAt(b) instanceof Active) {
						Active actor = (Active) dam.elementAt(b);
						if (!actor.isDestroyed())
							actor.enviromentalEffects();
					} else if (dam.elementAt(b) instanceof Facility) {
						Facility facil = (Facility) dam.elementAt(b);
						if (!facil.isDestroyed())
							facil.enviromentalEffects();
					}
				}
			}
			// move time for program in actives
			// if (Main.DEBUG) System.out.println("DEBUG Running programs");
			for (int b = 0; b < act.size(); b++) {
				Active actor = (Active) act.elementAt(b);
				if (actor.isPowered())
					actor.advanceClockTo(time);
				// robots just stop if out of endurance
				// ships move charge endurance
			}
		}
		Main.logTiming("Turn done");
	}

	public static void makeTurnReports() {
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			c.makeTurnReport(true);
			if (Main.DEBUG)
				System.out.print(".");
		}
		Main.logTiming("Making Turn Reports done");
	}

	public static void sendTurnReports() {
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			if (!c.isSysop())
				c.sendTurnReport();
			if (Main.DEBUG)
				System.out.print(".");
		}
		Main.logTiming("Sending Turn Reports done");
	}

	public static void load() {
		try {
			Main.logTiming("Universe LOAD Data.");
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA
					+ Main.universeDataFileName));
			GmlPair n = g.getOneByName("time");
			time = (long) n.getDouble();
			n = g.getOneByName("intrate");
			intrate = n.getDouble();
			n = g.getOneByName("materialtax");
			materialTax = n.getDouble();
			n = g.getOneByName("execTime");
			if (n == null)
				execTime = WEEK;
			else if (n.getString().equalsIgnoreCase("HOUR"))
				execTime = HOUR;
			else if (n.getString().equalsIgnoreCase("DAY"))
				execTime = DAY;
			else if (n.getString().equalsIgnoreCase("WEEK"))
				execTime = WEEK;
			else if (n.getString().equalsIgnoreCase("MONTH"))
				execTime = MONTH;
			n = g.getOneByName("interTime");
			if (n == null)
				interTime = HOUR;
			else if (n.getString().equalsIgnoreCase("HOUR"))
				interTime = HOUR;
			else if (n.getString().equalsIgnoreCase("DAY"))
				interTime = DAY;
			else if (n.getString().equalsIgnoreCase("WEEK"))
				interTime = WEEK;
			else if (n.getString().equalsIgnoreCase("MONTH"))
				interTime = MONTH;
			n = g.getOneByName("checkTime");
			if (n == null)
				CHECKTIME = HOUR;
			else if (n.getString().equalsIgnoreCase("HOUR"))
				CHECKTIME = HOUR;
			else if (n.getString().equalsIgnoreCase("DAY"))
				CHECKTIME = DAY;
			else if (n.getString().equalsIgnoreCase("WEEK"))
				CHECKTIME = WEEK;
			else if (n.getString().equalsIgnoreCase("MONTH"))
				CHECKTIME = MONTH;

			GmlPair nn[] = g.getAllByName("action");
			for (int a = 0; a < nn.length; a++) {
				Action AA = Action.parse(nn[a]);
				// System.out.println("Action found " +
				// AA.toGmlPair().toString());
				actions.addElement(AA);
			}
		} catch (IOException IOE) {
			System.out.println("MyError: load error in universe " + IOE);
			System.exit(0);
		}
		market.load();
		Main.logTiming("Universe LOAD Market done.");
		loadBodies();
		Main.logTiming("Universe LOAD Bodies. done");
		loadITThings();
		Main.logTiming("Universe LOAD ITThings. done");
		// corps must be after itthings for project tech parse
		loadCorps();
		Main.logTiming("Universe LOAD Corps. done");
		loadNFObjects();
		Main.logTiming("Universe LOAD NFObjects. done");
		// must be after ITThings because some have designs
		auction.load();
		Main.logTiming("Universe LOAD Auctions. done");
		// must be after object since it needs to look up object ids
	}

	public static void save() {
		market.adjust();
		Main.logTiming("adjust market. done");
		market.save();
		Main.logTiming("SAVE market. done");
		auction.advance();
		Main.logTiming("advance auction. done");
		auction.save();
		Main.logTiming("SAVE auction. done");
		saveCorpIndex();
		Main.logTiming("SAVE corps index. done");
		// save for deleted corps
		saveCorps();
		Main.logTiming("SAVE corps. done");
		// planets don't change don't need to save
		saveBodies();
		saveNFObjects();
		Main.logTiming("SAVE nfobjects. done");
		saveITThings();
		Main.logTiming("SAVE itthings. done");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(Main.DIRDATA
					+ Main.universeDataFileName));
			Vector v = new Vector();
			v.addElement(new GmlPair("time", "" + time));
			v.addElement(new GmlPair("intrate", "" + intrate));
			v.addElement(new GmlPair("materialtax", "" + materialTax));
			String et = (execTime < DAY ? "HOUR" : (execTime < WEEK ? "DAY"
					: (execTime < MONTH ? "WEEK" : "MONTH")));
			String it = (interTime < DAY ? "HOUR" : (interTime < WEEK ? "DAY"
					: (interTime < MONTH ? "WEEK" : "MONTH")));
			String ct = (CHECKTIME < DAY ? "HOUR" : (CHECKTIME < WEEK ? "DAY"
					: (CHECKTIME < MONTH ? "WEEK" : "MONTH")));
			// String mt = (MINTIME<HOUR?"MINUTE":(MINTIME < DAY?"HOUR":(MINTIME
			// < WEEK?"DAY":(MINTIME < MONTH?"WEEK":"MONTH"))));
			v.addElement(new GmlPair("execTime", "" + et));
			v.addElement(new GmlPair("interTime", "" + it));
			v.addElement(new GmlPair("checktime", "" + ct));
			v.addElement(new GmlPair("SYSTEMCOMMAND", Main.SYSTEMCOMMAND));
			v.addElement(new GmlPair("SYSTEMEMAIL", Main.SYSTEMEMAIL));
			v.addElement(new GmlPair("NONGAMEEMAIL", Main.NONGAMEEMAIL));
			v.addElement(new GmlPair("MESSAGE", Main.MESSAGE));
			// v.addElement(new GmlPair("mintime", "" + mt));
			for (int b = 0; b < actions.size(); b++) {
				Action AA = (Action) actions.elementAt(b);
				v.addElement(AA.toGmlPair());
			}
			GmlPair g = new GmlPair("UniverseAll", v);
			pw.println(g.prettyPrint());
			pw.flush();
			pw.close();
		} catch (IOException IOE) {
			System.out.println("MyError: save error in universe");
			System.exit(0);
		}
		Main.logTiming("SAVE universe. done");
	}

	public static void loadNFObjects() {
		try {
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA
					+ Main.NFObjectDataFileName));
			GmlPair nn[] = g.getList();
			for (int a = 0; a < nn.length; a++) {
				NFObject n = StockPile.parse(nn[a]);
				if (n == null) {
					n = Facility.parse(nn[a]);
				}
				if (n == null) {
					n = Active.parse(nn[a]);
				}
				if (n == null) {
					n = Prefab.parse(nn[a]);
				}
				if (n != null) {
					add(n);
				} else {
					System.out.println("MyError: unable to parse NFObject "
							+ nn[a]);
				}
			}
		} catch (IOException IOE) {
			System.out.println("MyError: load error in NFObjectes " + IOE);
			System.exit(0);
		}
	}

	public static void saveNFObjects() {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(Main.DIRDATA
					+ Main.NFObjectDataFileName));
			Vector v = new Vector();
			for (int a = 0; a < allThings.size(); a++) {
				NFObject n = (NFObject) allThings.elementAt(a);
				v.addElement(n.toGmlPair());
			}
			GmlPair g = new GmlPair("NFObjectsAll", v);
			// pw.println("NFObjectsAll [");
			// for(int a = 0; a < allThings.size(); a++) {
			// NFObject n = (NFObject)allThings.elementAt(a);
			// pw.println(n.toSaveString());
			// }
			// pw.println(" ]");
			pw.println(g.prettyPrint());
			pw.flush();
			pw.close();
		} catch (IOException IOE) {
			System.out.println("MyError: save error in NFObjectes");
			System.exit(0);
		}
	}

	public static void loadITThings() {
		try {
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA
					+ Main.ITThingDataFileName));
			GmlPair n[] = g.getList();
			for (int a = 0; a < n.length; a++) {
				ITThing i = FacilityDesign.parse(n[a]);
				if (i == null) {
					i = ActiveDesign.parse(n[a]);
				}
				if (i == null) {
					i = Program.parse(n[a]);
				}
				if (i == null) {
					i = Tech.parse(n[a]);
				}
				if (i != null) {
					register(i);
				} else {
					System.out.println("MyError: unable to parse ITThing "
							+ n[a]);
				}
			}
		} catch (IOException IOE) {
			System.out.println("MyError: load error in ITTHINGS " + IOE);
			System.exit(0);
		}
	}

	public static void saveITThings() {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(Main.DIRDATA
					+ Main.ITThingDataFileName));
			// pw.println("ITThingsAll [");
			// for(int a = 0; a < allITThings.size(); a++) {
			// ITThing i = (ITThing)allITThings.elementAt(a);
			// pw.println(i.toSaveString());
			// }
			// pw.println("]");
			Vector v = new Vector();
			for (int a = 0; a < allITThings.size(); a++) {
				ITThing i = (ITThing) allITThings.elementAt(a);
				v.addElement(i.toGmlPair());
			}
			GmlPair g = new GmlPair("ITThingsAll", v);
			pw.println(g.prettyPrint());
			pw.flush();
			pw.close();
		} catch (IOException IOE) {
			System.out.println("MyError: save error in ITThings");
			System.exit(0);
		}
	}

	// SIGNUP HERE
	public static String newCorp(String frommail, String t[]) {
		String bad = "Signup requires the following fields\n  corpname: \n  ticker: \n  realname: \n  password: \n  location: \n";
		String ticker = null;
		String cname = null;
		String cpass = null;
		String owner = null;
		Location hqloc = null;
		for (int a = 0; a < t.length; a++) {
			if (t[a].toLowerCase().indexOf("corpname") >= 0) {
				cname = t[a].substring(
						t[a].toLowerCase().indexOf("corpname") + 8).trim();
				if (cname.startsWith(":"))
					cname = cname.substring(1).trim();
				if (cname.indexOf(" ") > 0)
					cname = cname.substring(0, cname.indexOf(" "));
				if (cname.length() < 6) {
					return "Signup Error: Bad corpname.  Corpname must be at least 6 characters. "
							+ cname + " is not.\n" + bad;
				}
				for (int b = 0; b < allCorps.size(); b++) {
					Corp c = (Corp) allCorps.elementAt(b);
					if (c.getName().equalsIgnoreCase(cname)) {
						return "Signup Error: Bad corpname " + cname
								+ " not unique try again.\n" + bad;
					}
				}
			} else if (t[a].toLowerCase().indexOf("ticker") >= 0) {
				ticker = t[a].substring(
						t[a].toLowerCase().indexOf("ticker") + 6).trim();
				if (ticker.startsWith(":"))
					ticker = ticker.substring(1).trim();
				ticker = ticker.toUpperCase();
				if (ticker.indexOf(" ") > 0)
					ticker = ticker.substring(0, ticker.indexOf(" "));
				if (ticker.length() != 3) {
					return "Signup Error: Bad ticker.  Ticker must be at exactly 3 characters. "
							+ ticker + " is not.\n" + bad;
				}
				if (ticker.equalsIgnoreCase("ALL")
						|| ticker.equalsIgnoreCase("XXX")
						|| ticker.equalsIgnoreCase("TAX")
						|| ticker.equals("UFO")) {
					return "Signup Error: Bad ticker.  TAX, ALL, UFO and XXX are reserved \n"
							+ bad;
				}
				for (int b = 0; b < allCorps.size(); b++) {
					Corp c = (Corp) allCorps.elementAt(b);
					if (c.getTick().equalsIgnoreCase(ticker)) {
						return "Signup Error: Bad ticker " + ticker
								+ " not unique try again.\n" + bad;
					}
				}
			} else if (t[a].toLowerCase().indexOf("password") >= 0) {
				cpass = t[a].substring(
						t[a].toLowerCase().indexOf("password") + 8).trim();
				if (cpass.startsWith(":"))
					cpass = cpass.substring(1).trim();
				if (cpass.indexOf(" ") > 0)
					cpass = cpass.substring(0, cpass.indexOf(" "));
				if (cpass.length() < 6) {
					return "Signup Error: Bad password.  Password must be at least 6 characters. "
							+ cpass + " is not.\n" + bad;
				}
				if (cpass == null || cpass.indexOf(" ") > 0
						|| cpass.indexOf("\t") > 0) {
					return "Signup Error: Bad password.  Password cannot have spaces or tabs (one word). "
							+ cpass + " does.\n" + bad;
				}
			} else if (t[a].toLowerCase().indexOf("realname") >= 0) {
				owner = t[a].substring(
						t[a].toLowerCase().indexOf("realname") + 8).trim();
				if (owner.startsWith(":"))
					owner = owner.substring(1).trim();
				if (owner.equals("")) {
					return "Signup Error: Bad realname.  Corps without realnames risk being deleted.  Try again.\n"
							+ bad;
				}
			} else if (t[a].toLowerCase().indexOf("location") >= 0) {
				String l;
				l = t[a].substring(t[a].toLowerCase().indexOf("location") + 8)
						.trim();
				if (l.startsWith(":"))
					l = l.substring(1).trim();
				if (l.indexOf(" ") > 0)
					l = l.substring(0, l.indexOf(" "));
				// cut trailing spaces
				if (l.toLowerCase().indexOf("f") >= 0
						|| l.toLowerCase().indexOf("l") >= 0) {
					return "Signup Error: Bad locaiton.  Locaiton must be on surface of Earth.  Try 01.03.S0.22.12.  Try again.\n"
							+ bad;
				}
				hqloc = Location.parse(l);
				if (hqloc == null || !hqloc.isSurface() || !hqloc.valid()
						|| !hqloc.toString().startsWith("01.03.S")) {
					return "Signup Error: Bad locaiton.  Locaiton must be on surface of Earth.  Try 01.03.S0.22.12.  Try again.\n"
							+ bad;
				}
			}
		}
		if (ticker == null) {
			return "Signup Error: missing ticker.\n" + bad;
		} else if (cname == null) {
			return "Signup Error: missing corpname.\n" + bad;
		} else if (cpass == null) {
			return "Signup Error: missing password.\n" + bad;
		} else if (owner == null) {
			return "Signup Error: missing realname.\n" + bad;
		} else if (hqloc == null) {
			return "Signup Error: missing locaiton.\n" + bad;
		} else if (frommail == null) {
			return "Signup Error: missing frommail.\n" + bad;
		}
		// add corphq
		Facility f = new Facility(ticker, "FD1", hqloc);
		add(f);
		f.setActive(true);
		f.setActive(true);
		Location ll = Location.parse(f.getLocation() + "." + f.getId());
		Corp c = new Corp(cname, ticker, ll, frommail, cpass, owner);
		register(c);
		// add apollo
		Active AA = new Active(c.getTick(), "AD1", c.getHome(), -1, "", "",
				new Hashtable());
		Universe.add(AA);
		// add ablejack
		Active AAA = new Active(c.getTick(), "AD2", c.getHome(), -1, "", "",
				new Hashtable());
		Universe.add(AAA);
		// save corp and nfobjects
		saveCorpIndex();
		c.save();
		saveNFObjects();
		String good = "Signup complete\n";
		good += "\nWelcome TNF2\n";
		good += "To send orders use\n";
		good += "---clip-----\n";
		good += "@LOGIN ORDERS " + cname + " " + cpass + "\n";
		good += "@END\n";
		good += "---clip-----\n";
		c.makeTurnReport(false);
		c.sendTurnReport();
		return good;
	}

	public static void loadCorps() {
		try {
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA
					+ Main.corpDataFileName));
			GmlPair n[] = g.getAllByName("tick");
			for (int a = 0; a < n.length; a++) {
				String s = n[a].getString();
				Corp c = Corp.load("Corp_" + s + ".gml");
				// System.out.println("Loading corp "+s);
				if (c != null) {
					// System.out.println("Loaded corp "+s);
					register(c);
				} else {
					System.out.println("MyError: cannot load Corp " + s);
				}
			}
		} catch (IOException IOE) {
			System.out.println("MyError: load error in corps " + IOE);
			System.exit(0);
		}
	}

	public static void saveCorpIndex() {
		Vector v = new Vector();
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			v.addElement(new GmlPair("tick", c.getTick()));
		}
		GmlPair g = new GmlPair("all", v);
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(Main.DIRDATA
					+ Main.corpDataFileName));
			pw.println(g.prettyPrint());
			pw.flush();
			pw.close();
		} catch (IOException IOE) {
			System.out.println("MyError: Unable to save CorpIndex");
			System.exit(0);
		}
	}

	public static void saveCorps() {
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			c.save();
		}
	}

	public static void loadBodies() {
		try {
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA
					+ Main.bodyDataFileName));
			GmlPair n[] = g.getList();
			for (int a = 0; a < n.length; a++) {
				Body b = Body.parse(n[a]);
				if (b != null) {
					register(b);
				}
			}
		} catch (IOException IOE) {
			System.out.println("MyError: load error in bodies " + IOE);
			System.exit(0);
		}
	}

	public static void saveBodies() {
		PrintWriter pw = null;
		// new PrintWriter(new OutputStreamWriter(System.out));
		try {
			pw = new PrintWriter(new FileWriter(Main.DIRDATA
					+ Main.bodyDataFileName));
		} catch (IOException IOE) {
			System.out.println("MyError: save error in Bodies");
			System.exit(0);
		}
		// pw.println("all [");
		// for(int a = 0; a < allBodies.size(); a++) {
		// Body b = (Body)allBodies.elementAt(a);
		// pw.println(b.toSaveString());
		// }
		// pw.println("]");
		Vector v = new Vector();
		for (int a = 0; a < allBodies.size(); a++) {
			Body b = (Body) allBodies.elementAt(a);
			v.addElement(b.toGmlPair());
		}
		GmlPair g = new GmlPair("all", v);
		pw.println(g.prettyPrint());
		pw.flush();
		pw.close();
	}

	public static Corp loginCorp(String name, String pass) {
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			if ((c.getTick().equalsIgnoreCase(name) || c.getName()
					.equalsIgnoreCase(name))
					&& c.getPassword().equals(pass)) {
				c.login();
				return c;
			}
			if (c.getName().equalsIgnoreCase(name))
				return null;
		}
		return null;
	}

	public static long getTime() {
		return time;
	}

	// checks for available stockpiles at location
	// returns amount available
	public static int availableMaterial(Location l, String name) {
		name = Market.getMarketName(name);
		int tot = 0;
		for (int a = 0; a < allThings.size(); a++) {
			if (!(allThings.elementAt(a) instanceof StockPile)) {
				continue;
			}
			StockPile A = (StockPile) allThings.elementAt(a);
			if (A.getMaterial().equalsIgnoreCase(name)
					&& A.getLocation().equalsIgnoreCase(l)) {
				tot += A.getAmount();
			}
		}
		return tot;
	}

	// removes material from stockpiles.
	// returns amount available taken (of availble or max asked for)
	public static int takeMaterial(String tick, Location l, String name, int max) {
		name = Market.getMarketName(name);
		int tot = 0;
		Vector opts = new Vector();
		// list all owned tick
		for (int a = 0; a < allThings.size(); a++) {
			if (!(allThings.elementAt(a) instanceof StockPile)) {
				continue;
			}
			StockPile A = (StockPile) allThings.elementAt(a);
			if (A.getCorpTick().equalsIgnoreCase(tick)
					&& A.getLocation().equalsIgnoreCase(l)
					&& A.getMaterial().equalsIgnoreCase(name)) {
				opts.addElement(A);
			}
		}
		// add all others
		for (int a = 0; a < allThings.size(); a++) {
			if (!(allThings.elementAt(a) instanceof StockPile)) {
				continue;
			}
			StockPile A = (StockPile) allThings.elementAt(a);
			if (!A.getCorpTick().equalsIgnoreCase(tick)
					&& A.getLocation().equalsIgnoreCase(l)
					&& A.getMaterial().equalsIgnoreCase(name)) {
				opts.addElement(A);
			}
		}
		for (int a = 0; a < opts.size(); a++) {
			StockPile A = (StockPile) opts.elementAt(a);
			if (A.getAmount() <= max - tot) {
				tot += A.getAmount();
				remove(A);
				// remove from master list
				opts.removeElement(A);
				a--;
			} else {
				A.take(max - tot);
				tot = max;
				break;
			}
		}
		return tot;
	}

	public static StockPile getStockPile(StockPile s) {
		for (int a = 0; a < allThings.size(); a++) {
			if (!(allThings.elementAt(a) instanceof StockPile)) {
				continue;
			}
			StockPile A = (StockPile) allThings.elementAt(a);
			if (A.getCorpTick().equalsIgnoreCase(s.getCorpTick())
					&& A.getMaterial().equalsIgnoreCase(s.getMaterial())
					&& A.getLocation().equalsIgnoreCase(s.getLocation())) {
				return A;
			}
		}
		return null;
	}

	public static Corp getCorpByTick(String s) {
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			if (c.getTick().equalsIgnoreCase(s)) {
				return c;
			}
		}
		return null;
	}

	public static Corp getCorpByName(String s) {
		for (int a = 0; a < allCorps.size(); a++) {
			Corp c = (Corp) allCorps.elementAt(a);
			if (c.getName().equalsIgnoreCase(s)) {
				return c;
			}
			if (c.getTick().equalsIgnoreCase(s)) {
				return c;
			}
		}
		return null;
	}

	public static Body getBodyByName(String n) {
		for (int a = 0; a < allBodies.size(); a++) {
			Body b = (Body) allBodies.elementAt(a);
			if (b.getName().equalsIgnoreCase(n))
				return b;
		}
		return null;
	}

	public static int getMaxPlanet(int x) {
		int r = 0;
		for (int a = 0; a < allBodies.size(); a++) {
			Body b = (Body) allBodies.elementAt(a);
			Location l = b.getLocation();
			if (l.getSolar() == x)
				r = Math.max(r, l.getPlanet());
		}
		return r;
	}

	public static int getMaxSolar() {
		int r = 0;
		for (int a = 0; a < allBodies.size(); a++) {
			Body b = (Body) allBodies.elementAt(a);
			Location l = b.getLocation();
			r = Math.max(r, l.getSolar());
		}
		return r;
	}

	public static Body getBodyByLocation(Location l) {
		l = l.getBodyLocation();
		if (l == null)
			return null;
		for (int a = 0; a < allBodies.size(); a++) {
			Body b = (Body) allBodies.elementAt(a);
			if (b.getLocation().equalsIgnoreCase(l))
				return b;
		}
		return null;
	}

	public static double getTempByLocation(Location l) {
		double SPACETEMP = -273.0;
		double ROOMTEMP = 23.0;
		if (l.isInside())
			return ROOMTEMP;
		if (l.equalsIgnoreCase(l.getBodyLocation()))
			return SPACETEMP;
		Body b = getBodyByLocation(l);
		if (b != null)
			return b.getTemp(l);
		// System.out.println("MyError: bad location in temp check " + l);
		return ROOMTEMP;
	}

	public static String getSurfaceByLocation(Location l) {
		if (l.isFacility() || l.isOrbit() || l.isLevel() || l.isInside())
			return "-";
		Body b = getBodyByLocation(l);
		if (b != null)
			return b.getSurf(l);
		System.out.println("MyError: bad location in surf check " + l);
		return "-";
	}

	public static String getResourcesByLocation(Location l) {
		if (l.isOrbit())
			return "-";
		if (l.isInside() && !l.isFacility())
			return "-";
		Body b = getBodyByLocation(l);
		if (b != null)
			return b.getResourceAtLocation(l);
		System.out.println("MyError: bad location in rec check " + l);
		return "-";
	}

	public static NFObject getNFObjectById(String s) {
		if (s == null)
			return null;
		for (int a = 0; a < allThings.size(); a++) {
			NFObject c = (NFObject) allThings.elementAt(a);
			if (c.getId().equalsIgnoreCase(s)) {
				return c;
			} else if (c.getNick().equalsIgnoreCase(s)) {
				return c;
			}
		}
		return null;
	}

	public static ITThing getITThingByName(String s) {
		for (int a = 0; a < allITThings.size(); a++) {
			ITThing c = (ITThing) allITThings.elementAt(a);
			if (c.getId().equalsIgnoreCase(s)
					|| c.getName().equalsIgnoreCase(s)) {
				return c;
			}
			if (c instanceof Tech
					&& c.getName().toUpperCase().startsWith(s.toUpperCase())) {
				return c;
			}
		}
		return null;
	}

	public static int countProg(String tic) {
		int tot = 0;
		for (int a = 0; a < allITThings.size(); a++) {
			ITThing c = (ITThing) allITThings.elementAt(a);
			if (c instanceof Program && c.isPartner(tic)) {
				tot++;
			}
		}
		return tot;
	}

	public static Tech getTechByName(String s) {
		for (int a = 0; a < allITThings.size(); a++) {
			ITThing c = (ITThing) allITThings.elementAt(a);
			if ((c.getName().equalsIgnoreCase(s) || c.getId().equalsIgnoreCase(
					s))
					&& c instanceof Tech) {
				return (Tech) c;
			}
		}
		return null;
	}

	public static Market getMarket() {
		return market;
	}

	public static double getMarketValue(String m) {
		return market.getValue(m);
	}

	public static Auction getAuction() {
		return auction;
	}

	public static Vector getNFObjectsByTick(String cn) {
		Vector v = new Vector();
		for (int a = 0; a < allThings.size(); a++) {
			NFObject n = (NFObject) allThings.elementAt(a);
			if (n.getCorpTick().equalsIgnoreCase(cn)) {
				v.addElement(n);
			}
		}
		return v;
	}

	public static void addAction(Action a) {
		if (!actions.contains(a)) {
			actions.addElement(a);
		}
	}

	public static void add(NFObject o) {
		if (!allThings.contains(o)) {
			allThings.addElement(o);
		}
	}

	public static void remove(ITThing i) {
		if (i instanceof Tech)
			return;
		allITThings.removeElement(i);
	}

	public static void remove(NFObject o) {
		allThings.removeElement(o);
	}

	public static void mergeStockPiles() {
		for (int a = 0; a < allThings.size(); a++) {
			if (!(allThings.elementAt(a) instanceof StockPile)) {
				continue;
			}
			StockPile A = (StockPile) allThings.elementAt(a);
			for (int b = a + 1; b < allThings.size(); b++) {
				if (!(allThings.elementAt(b) instanceof StockPile)) {
					continue;
				}
				StockPile B = (StockPile) allThings.elementAt(b);
				if (A.merge(B)) {
					allThings.removeElement(B);
					b--;
				}
			}
			if (A.getAmount() <= 0) {
				allThings.removeElement(A);
				a--;
			}
		}
		for (int a = 0; a < allThings.size(); a++) {
			if ((allThings.elementAt(a) instanceof Active)) {
				Active aa = (Active) allThings.elementAt(a);
				aa.getCargoMass();
			}
			if ((allThings.elementAt(a) instanceof Facility)) {
				// check 200 cargo limit
			}
		}
	}

	public static void activateFacilities() {
		allThings = randomizeVector(allThings);
		Vector allFac = new Vector();
		for (int a = 0; a < allThings.size(); a++) {
			if (allThings.elementAt(a) instanceof Facility) {
				Facility f = (Facility) allThings.elementAt(a);
				f.setActive(false);
				if (f.getCorpTick().equalsIgnoreCase("AUCTION"))
					continue;
				allFac.addElement(f);
			}
		}
		Vector allFac2 = (Vector) allFac.clone();
		if (Main.DEBUG)
			System.out.println("found " + allFac.size() + " Facilities");
		Vector groups = new Vector();
		while (allFac.size() > 0) {
			Facility f = (Facility) allFac.elementAt(0);
			Vector v = new Vector();
			v.addElement(f);
			allFac.removeElementAt(0);
			for (int a = 0; a < allFac.size(); a++) {
				Facility ff = (Facility) allFac.elementAt(a);
				if (ff.getLocation().equals(f.getLocation())) {
					v.addElement(ff);
					allFac.removeElement(ff);
					a--;
				}
			}
			groups.addElement(v);
		}
		if (Main.DEBUG)
			System.out.println("made " + groups.size() + " default groups");
		Vector supplygroup = null;
		for (int a = 0; a < allFac2.size(); a++) {
			Facility f = (Facility) allFac2.elementAt(a);
			if (!f.hasAbility("T60")) {
				continue;
			}
			for (int b = 0; b < groups.size(); b++) {
				Vector v = (Vector) groups.elementAt(b);
				if (v.contains(f)) {
					supplygroup = v;
				}
			}
			if (supplygroup == null) {
				System.out
						.println("MyError: canot find supply group for connector supply");
				break;
			}
			Location l = f.getLocation();
			Location l1 = l.reference("N");
			if (l1 != null)
				mergeSupplyGroup(groups, supplygroup, l1);
			l1 = l.reference("S");
			if (l1 != null)
				mergeSupplyGroup(groups, supplygroup, l1);
			l1 = l.reference("E");
			if (l1 != null)
				mergeSupplyGroup(groups, supplygroup, l1);
			l1 = l.reference("W");
			if (l1 != null)
				mergeSupplyGroup(groups, supplygroup, l1);
			l1 = l.reference("DOWN");
			if (l1 != null && !l1.isSurface()) {
				mergeSupplyGroup(groups, supplygroup, l1);
			}
			l1 = l.reference("UP");
			if (l1 != null && !l1.isOrbit()) {
				mergeSupplyGroup(groups, supplygroup, l1);
			}
		}

		if (Main.DEBUG)
			System.out.println("made " + groups.size() + " connected groups");
		// merge adjoining groups with connector power/supply
		// can find all?
		for (int a = 0; a < groups.size(); a++) {
			Vector v = (Vector) groups.elementAt(a);
			boolean changed = true;
			boolean keep = true;
			while (changed) {
				if (Main.DEBUG)
					System.out.println("change found repeating group " + a);
				changed = false;
				for (int b = 0; b < v.size(); b++) {
					Facility f = (Facility) v.elementAt(b);
					if (Main.DEBUG)
						System.out.println("test activate for" + f.getId());
					if (f.isDisabled())
						continue;
					if (f.isActive())
						continue;
					if (f.getConsumeList().size() == 0) {
						if (Main.DEBUG)
							System.out.println("self activating " + f.getId());
						f.setActive(true);
						changed = true;
					} else {
						if (Main.DEBUG)
							System.out.println("need consumabes to active "
									+ f.getId());
						changed = changed | seekConsume(f, v, keep);
					}
				}
				if (!changed && keep) {
					if (Main.DEBUG)
						System.out.println("Done keep check, do share check");
					keep = false;
					changed = true;
				}
			}
		}
	}

	public static boolean seekConsume(Facility f, Vector v, boolean keep) {
		if (Main.DEBUG)
			System.out.println("seeking consumables for " + f.getId());
		Vector cons = f.getConsumeList();
		Vector prov = new Vector();
		boolean found = true;
		for (int a = 0; a < cons.size(); a++) {
			String what = (String) cons.elementAt(a);
			int amt = 1;
			if (what.indexOf("x") > 0) {
				try {
					amt = Integer
							.parseInt(what.substring(0, what.indexOf("x")));
					what = what.substring(what.indexOf("x") + 1);
				} catch (NumberFormatException nfe) {
					System.out
							.println("MyError: Bad number in consume " + what);
				}
			}
			if (Main.DEBUG)
				System.out.println("need to consume " + amt + " of " + what);
			if (what.startsWith("M")) {
				String na = Market.getMarketName(what);
				if (na != null) {
					if (Main.DEBUG)
						System.out.println("Material full available");
					int amt2 = availableMaterial(f.getInsideLoc(), na);
					if (amt2 >= amt)
						amt = 0;
				}
			}
			for (int b = 0; b < v.size() && amt > 0; b++) {
				Facility check = (Facility) v.elementAt(b);
				Corp checkOwn = Universe.getCorpByTick(check.getCorpTick());
				if (keep && !check.getCorpTick().equals(f.getCorpTick())) {
					if (Main.DEBUG)
						System.out.println("Private facilty blocked "
								+ check.getId());
					continue;// not own
				} else if (!keep) {
					Corp fown = Universe.getCorpByTick(f.getCorpTick());
					if (fown == null || checkOwn == null) {
						System.out
								.println("MyError:  Bad corp in facility activate fee");
						continue;
					}
					if (fown.getMaxFee(what) <= checkOwn.getSetFee(what))
						continue;
				}
				if (check.isDisabled() || !check.isActive()) {
					if (Main.DEBUG)
						System.out.println("Facilty disabled or not active "
								+ check.getId());
					continue;// not on
				}
				int provamt = check.getProvidesAmt(what);
				if (Main.DEBUG)
					System.out.println("found facility " + check.getId()
							+ " that provides " + provamt + " of " + what);
				if (provamt >= amt) {
					prov.addElement(check);
					amt = 0;
					if (Main.DEBUG)
						System.out.println("full available found " + what);
					break;
				} else if (provamt > 0 && provamt < amt) {
					prov.addElement(check);
					amt -= provamt;
					if (Main.DEBUG)
						System.out.println("partial available found " + what
								+ " only " + provamt);
				}
			}
			if (amt != 0) {
				found = false;
				if (Main.DEBUG)
					System.out
							.println("cannot find enough available consumable");
			}
		}
		if (found) {
			if (Main.DEBUG)
				System.out
						.println("found all required available to do activate");
			for (int a = 0; a < cons.size(); a++) {
				String what = (String) cons.elementAt(a);
				int amt = 1;
				if (what.indexOf("x") > 0) {
					try {
						amt = Integer.parseInt(what.substring(0, what
								.indexOf("x")));
						what = what.substring(what.indexOf("x") + 1);
					} catch (NumberFormatException nfe) {
						System.out.println("MyError: Bad number in consume "
								+ what);
					}
				}
				if (what.startsWith("M")) {
					String na = Market.getMarketName(what);
					if (na != null) {
						if (Main.DEBUG)
							System.out.println("Material full consume");
						takeMaterial(f.getCorpTick(), f.getInsideLoc(), na, amt);
						amt = 0;
					}
				}
				for (int b = 0; b < prov.size() && amt > 0; b++) {
					Facility check = (Facility) prov.elementAt(b);
					double theFee = 0;
					Corp fown = Universe.getCorpByTick(f.getCorpTick());
					Corp checkOwn = Universe.getCorpByTick(check.getCorpTick());
					if (fown == null || checkOwn == null) {
						System.out
								.println("MyError:  Bad corp in facility activate fee found");
						continue;
					}
					if (keep && !check.getCorpTick().equals(f.getCorpTick()))
						continue;// not own
					else if (!keep) {
						// check if fee okay
						if (fown.getMaxFee(what) <= checkOwn.getSetFee(what))
							continue;
						theFee = checkOwn.getSetFee(what);
					}
					int provamt = check.getProvidesAmt(what);
					if (provamt >= amt) {
						if (theFee > 0) {
							fown.payFee(what, theFee * amt);
							checkOwn.receiveFee(what, theFee * amt);
						}
						check.consume(what, amt);
						if (Main.DEBUG)
							System.out.println("full consume");
						break;
					} else if (provamt > 0 && provamt < amt) {
						if (theFee > 0) {
							fown.payFee(what, theFee * provamt);
							checkOwn.receiveFee(what, theFee * provamt);
						}
						check.consume(what, provamt);
						if (Main.DEBUG)
							System.out.println("parital consume");
						amt -= provamt;
					}
				}
			}
			f.setActive(true);
			if (Main.DEBUG)
				System.out.println("facility activated!");
			return true;
		}
		if (Main.DEBUG)
			System.out.println("facility not activated");
		return false;
	}

	public static void mergeSupplyGroup(Vector groups, Vector supplygroup,
			Location l1) {
		Vector v = getNFObjectsByLocation(l1);
		for (int b = 0; b < v.size(); b++) {
			NFObject nfoo = (NFObject) v.elementAt(b);
			if (nfoo instanceof Facility) {
				for (int c = 0; c < groups.size(); c++) {
					Vector vv = (Vector) groups.elementAt(c);
					if (vv.contains(nfoo) && vv != supplygroup) {
						for (int d = 0; d < vv.size(); d++) {
							supplygroup.addElement(vv.elementAt(d));
						}
						groups.removeElement(vv);
						return;
					}
				}
			}
		}
	}

	public static Vector cleanXList(Vector V) {
		Vector v = (Vector) V.clone();
		for (int a = 0; a < v.size(); a++) {
			String t = (String) v.elementAt(a);
			int cnt1 = 1;
			if (t.indexOf("x") > 0) {
				cnt1 = Integer.parseInt(t.substring(0, t.indexOf("x")));
				t = t.substring(t.indexOf("x") + 1);
			}
			for (int b = a + 1; b < v.size(); b++) {
				String u = (String) v.elementAt(b);
				int cnt2 = 1;
				if (u.indexOf("x") > 0) {
					cnt2 = Integer.parseInt(u.substring(0, u.indexOf("x")));
					u = u.substring(u.indexOf("x") + 1);
				}
				if (u.equalsIgnoreCase(t)) {
					cnt1 += cnt2;
					v.removeElementAt(b);
					v.setElementAt(cnt1 + "x" + t, a);
					b--;
				}
			}
			if (cnt1 <= 0) {
				v.removeElementAt(a);
				a--;
				continue;
			}
		}
		return v;
	}

	public static Vector mergeXList(Vector V) {
		Vector v = (Vector) V.clone();
		for (int a = 0; a < v.size(); a++) {
			String t = (String) v.elementAt(a);
			int cnt1 = 1;
			if (t.indexOf("x") > 0) {
				cnt1 = Integer.parseInt(t.substring(0, t.indexOf("x")));
				t = t.substring(t.indexOf("x") + 1);
			}
			for (int b = a + 1; b < v.size(); b++) {
				String u = (String) v.elementAt(b);
				int cnt2 = 1;
				if (u.indexOf("x") > 0) {
					cnt2 = Integer.parseInt(u.substring(0, u.indexOf("x")));
					u = u.substring(u.indexOf("x") + 1);
				}
				if (u.equalsIgnoreCase(t)) {
					cnt1 += cnt2;
					v.removeElementAt(b);
					v.setElementAt(cnt1 + "x" + t, a);
					b--;
				}
			}
		}
		return v;
	}

	public static void consume(String materialname, int amnt, Location lo) {
		if (lo == null)
			return;
		if (lo.isFacility())
			lo = lo.getOutsideFacilityLocation();
		Body b = getBodyByLocation(lo);
		if (b == null)
			return;
		b.consume(materialname, amnt, lo);
	}

	public static Vector randomizeVector(Vector V) {
		Vector v = new Vector();
		while (V.size() > 0) {
			Object o = V.elementAt((int) (Math.random() * V.size()));
			v.addElement(o);
			V.removeElement(o);
		}
		return v;
	}
}
