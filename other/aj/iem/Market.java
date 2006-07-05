package aj.iem;

import java.util.Vector;

import aj.io.MLtoText;
import aj.misc.Stuff;

public class Market implements Runnable {
	private String marketName, marketId;

	private Vector marketBundles = new Vector();

	private Vector marketContracts = new Vector();

	private boolean running = false;

	long lastUpdate = -1;

	private boolean ready = false;

//	private long elaspedTime = -1;

	private long updateComplete = -1;

	private IEMTool tool;

	boolean isReady() {
		return ready;
	}

	private boolean isAllowedMarket() {
		String name = getName().toUpperCase();
		Vector v = tool.allowedMarketNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked market found "+name);
		return false;
	}

	boolean isValidMarket() {
		return marketName != null && marketId != null
				&& marketName.length() > 0 && marketId.length() > 0;
	}

	boolean isRunning() {
		return running;
	}

	private synchronized void setMarketContracts(Vector v) {
		marketContracts = v;
	}

	private synchronized Vector getMarketContractsClone() {
		return (Vector) marketContracts.clone();
	}

	private synchronized void setMarketBundles(Vector v) {
		marketBundles = v;
	}

	private synchronized Vector getMarketBundlesClone() {
		return (Vector) marketBundles.clone();
	}

	private long getUpdateDelay() {
		if (getName().equals("Comp_Ret"))
			return 1000 * 60 * 2;
		else if (getName().equals("MSFT_Price"))
			return 1000 * 60 * 2;
		else if (getName().equals("DConv04"))
			return 1000 * 60 * 3;
		else if (getName().startsWith("Pres04_"))
			return 1000 * 40;
		else if (getName().equals("FedPolicyB"))
			return 1000 * 60 * 3;
		else if (getName().startsWith("RH"))
			return 1000 * 60 * 3;
		else if (getName().startsWith("RS"))
			return 1000 * 60 * 3;
		return 1000 * 60 * 3;
	}

//	public boolean equals(Market m) {
//		return m != null && marketId != null && m.marketId != null
//				&& m.marketId.equals(marketId);
//	}

	public Market(IEMTool toolRef, String n, String i) {
		tool = toolRef;
		marketName = n;
		marketId = i;
	}

	public void run() {
		running = true;
		while (running) {
			if (!tool.isLoggedIn())
				break;
			long delay = getUpdateDelay() + lastUpdate
					- System.currentTimeMillis();
			if (delay > 0) {
				try {
					Thread.sleep((int) (delay * (Math.random() * .1 + 1)));
				} catch (Exception ei) {
				}
			}
			ready = false;
			lastUpdate = System.currentTimeMillis();
			if (isAllowedMarket()) {
				tool.reportTool(getName().substring(0, 1).toLowerCase());
				downloadBundles();
				downloadContracts();
			}
			updateComplete = System.currentTimeMillis();
			Vector v = getMarketBundlesClone();
			for (int a = 0; a < v.size(); a++) {
				Bundle b = (Bundle) v.elementAt(a);
				b.getMinHeld();
				b.getMaxHeld();
			}
			ready = true;
			tool.reportStatus();
			v = getMarketBundlesClone();
			for (int a = 0; a < v.size(); a++) {
				Bundle b = (Bundle) v.elementAt(a);
				b.makeOrders();
			}
		}
		running = false;
	}

	int getElaspedTime() {
		return (int) (System.currentTimeMillis() - lastUpdate);
	}

	private int getUpdateCompleteTime() {
		if (updateComplete <= lastUpdate)
			return -1;
		return (int) (updateComplete - lastUpdate);
	}

	String getQuickSummary() {
		String report = "";
		Vector v = getMarketBundlesClone();
		for (int a = 0; a < v.size(); a++) {
			Bundle b = (Bundle) v.elementAt(a);
			report += b.getQuickSummary() + " ";
		}
		return report;
	}

	String getOrdersReport() {
		String report = "";
		Vector v = getMarketBundlesClone();
		for (int a = 0; a < v.size(); a++) {
			Bundle b = (Bundle) v.elementAt(a);
			report += b.getOrdersReport();
		}
		return report;
	}

	String getReport() {
		if (!isReady()) {
			return "Market " + getName() + " Loading for "
					+ (getElaspedTime() / 1000.0) + " secs "
					+ (isAllowedMarket() ? "Allowed" : "Not Allowed") + "\n\n";
		}
		String report = "Market " + getName() + " in "
				+ (getUpdateCompleteTime() / 1000.0) + " secs "
				+ (isAllowedMarket() ? "Allowed" : "Not Allowed") + " "
				+ ((getElaspedTime() - getUpdateCompleteTime()) / 1000.0)
				+ " secs old\n";
		Vector v = getMarketBundlesClone();
		for (int a = 0; a < v.size(); a++) {
			Bundle b = (Bundle) v.elementAt(a);
			report += b.fullReport() + b.getSummary() + "\n\n";
		}
		return report + "\n";
	}

	int getAssetsValue() {
		int asset = 0;
		Vector v = getMarketBundlesClone();
		for (int a = 0; a < v.size(); a++) {
			Bundle b = (Bundle) v.elementAt(a);
			asset += b.getAssetsValue();
		}
		return asset;
	}

	int getLiquidValue() {
		int liquid = 0;
		Vector v = getMarketBundlesClone();
		for (int a = 0; a < v.size(); a++) {
			Bundle b = (Bundle) v.elementAt(a);
			liquid += b.getMinHeld() * b.getBundleValue();
		}
		return liquid;
	}

	private void downloadBundles() {
		Vector newMarketBundles = new Vector();
		Vector newMarketContracts = new Vector();
		if (!tool.isLoggedIn())
			return;
		if (marketId == null || marketId.equals("")) {
			tool
					.reportTool("MyError: market download requested for null marketid");
			tool.doLogout();
			return;
		}
		String ordersUrl = "/webex/WebEx.dll?TraderInterfaceHandler?USERTYPE=trader&LOGIN="
				+ tool.userId
				+ "&SESSIONID="
				+ tool.sessionId
				+ "&LANGUAGE=english&Markets=" + marketId + "&Panel_id=orders";
		String all = IEMTool.readAllSocket("GET " + ordersUrl
				+ " HTTP/1.0\r\n\r\n", "", "iemweb.biz.uiowa.edu", 80);
		Thread.yield();
		if (all == null) {
			tool.doLogout();
			return;
		}
		all = Stuff.superTrim(all);
		if (all.indexOf("RequestCode") >= 0) {
			all = all.substring(all.indexOf("RequestCode") + 11);
		}
		if (all.indexOf("/SELECT") >= 0) {
			all = all.substring(0, all.indexOf("/SELECT"));
		}
		while (all.indexOf("VALUE=\"") >= 0) {
			String val = all.substring(all.indexOf("VALUE=\"") + 7);
			val = val.substring(0, val.indexOf("\""));
			all = all.substring(all.indexOf("VALUE=\"") + 7);
			all = all.substring(all.indexOf(">") + 1).trim();
			while (all.indexOf("&nbsp;") >= 0) {
				all = all.substring(0, all.indexOf("&nbsp;")).trim() + " "
						+ all.substring(all.indexOf("&nbsp;") + 6).trim();
			}
			String nam = all.substring(0, all.indexOf("<")).trim();
			if (nam.indexOf(" ") >= 0)
				nam = nam.substring(0, nam.indexOf(" ")).trim();
			if (!val.equals("00")) {
				if (val.startsWith("D")) {
					Bundle b = new Bundle(tool, nam, val.substring(1));
					b.marketId = getId();
					b.market = this;
					newMarketBundles.addElement(b);
				} else if (val.startsWith("S")) {
					Contract c = new Contract(tool, nam, val.substring(1));
					newMarketContracts.addElement(c);
				}
			}
		}
		Vector fix = new Vector();
		Vector v = getMarketBundlesClone();
		for (int a = 0; a < newMarketBundles.size(); a++) {
			Bundle bb = (Bundle) newMarketBundles.elementAt(a);
			for (int b = 0; b < v.size(); b++) {
				Bundle bbb = (Bundle) v.elementAt(b);
				if (bbb.equals(bb)) {
					bb = bbb;
					v.removeElement(bbb);
					b--;
				}
			}
			fix.addElement(bb);
		}
		setMarketBundles(fix);
		fix = new Vector();
		v = getMarketContractsClone();
		for (int a = 0; a < newMarketContracts.size(); a++) {
			Contract bb = (Contract) newMarketContracts.elementAt(a);
			for (int b = 0; b < v.size(); b++) {
				Contract bbb = (Contract) v.elementAt(b);
				if (bbb.getId().equals(bb.getId())) {
					bb = bbb;
					v.removeElement(bbb);
					b--;
				}
			}
			fix.addElement(bb);
		}
		setMarketContracts(fix);

		v = getMarketBundlesClone();
		for (int a = 0; a < v.size(); a++) {
			Bundle bb = (Bundle) v.elementAt(a);
			String tail = bb.getName();
			if (tail.indexOf("$") >= 0)
				tail = tail.substring(tail.indexOf("$") + 1);
			Vector v2 = getMarketContractsClone();
			for (int b = 0; b < v2.size(); b++) {
				Contract cc = (Contract) v2.elementAt(b);
				if (cc.getName().indexOf(tail) >= 0 || v.size() == 1) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				// special case for September 2004 only (remove soon)
				if (cc.getName().indexOf("0904") >= 0
						&& bb.getName().indexOf("FR1$1004") >= 0) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				if (tail.equals("RET_Prac") && cc.getName().equals("AFD")) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				if (tail.equals("RET_Prac") && cc.getName().equals("QQM")) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				if (tail.equals("RET_Prac") && cc.getName().equals("XYZ")) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				if (tail.equals("VS_Prac") && cc.getName().equals("V.TRU")) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				if (tail.equals("VS_Prac") && cc.getName().equals("V.DEW")) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				if (tail.equals("WTA_Prac") && cc.getName().equals("TRUMAN")) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				if (tail.equals("WTA_Prac") && cc.getName().equals("DEWEY")) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
				if (tail.equals("WTA_Prac") && cc.getName().equals("ROF")) {
					cc.setBundle(bb);
					bb.addContract(cc);
					continue;
				}
			}
		}
		tool.reportStatus();
	}

	private void downloadContracts() {
		if (!tool.isLoggedIn())
			return;
		if (marketId == null || marketId.equals("")) {
			tool
					.reportTool("MyError: market download requested for null market");
			tool.doLogout();
			return;
		}

		String dataUrl = "/webex/WebEx.dll?TraderInterfaceHandler?USERTYPE=trader&LOGIN="
				+ tool.userId
				+ "&SESSIONID="
				+ tool.sessionId
				+ "&LANGUAGE=english&Markets=" + marketId + "&Panel_id=data";
		String all = IEMTool.readAllSocket("GET " + dataUrl
				+ " HTTP/1.0\r\n\r\n", "", "iemweb.biz.uiowa.edu", 80);
		Thread.yield();
		if (all == null) {
			tool.doLogout();
			return;
		}
		all = MLtoText.cutMLaddSpaces(all);
		// if (getName().indexOf("FedPolicyB")>=0)
		// HERE
		// System.out.println("all=>"+all);
		all = Stuff.superTrim(all);
		if (all.indexOf("YourAsks") >= 0) {
			String cs = "";
			if (all.indexOf("$:") >= 0)
				cs = all.substring(all.indexOf("$:") + 2);
			if (cs.indexOf(".") >= 0)
				cs = cs.substring(0, cs.indexOf(".") + 4);
			try {
				tool.cash = (int) (Double.parseDouble(cs.trim()) * 1000);
			} catch (NumberFormatException nfe) {
				tool
						.reportTool("MyError numberformate error 3 download market "
								+ marketName + ">" + cs);
			}
			all = all.substring(all.indexOf("YourAsks") + 8).trim();
		} else {
			tool.reportTool("Bad page requested =" + dataUrl);
			tool.doLogout();
			return;
		}
		String mm[] = Stuff.getTokens(all, " ()");
		for (int a = 0; a < mm.length; a += 7) {
			if (mm[a + 1].toUpperCase().indexOf("INACTIVE") >= 0) {
				a++;
				continue;
			}
			String name = mm[a];
			String bid = mm[a + 1];
			String ask = mm[a + 2];
			String last = mm[a + 3];
			String held = mm[a + 4];
			String mybid = mm[a + 5];
			String myask = mm[a + 6];
			Vector v2 = getMarketContractsClone();
			for (int b = 0; b < v2.size(); b++) {
				Contract cc = (Contract) v2.elementAt(b);
				if (cc.getName().equals(name)) {
					cc.setVals(name, bid, ask, last, held, mybid, myask,
							marketId);
					cc.downloadOrders();
				}
			}
		}
		tool.reportStatus();
	}

	private String getId() {
		return marketId;
	}

	private String getName() {
		return marketName;
	}

}
