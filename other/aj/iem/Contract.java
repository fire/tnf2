package aj.iem;

import java.util.Vector;

import aj.io.MLtoText;
import aj.misc.Stuff;

public class Contract {
	static int INITIALNUM = 5000;

	static int count;

	int countId;

	String contractName, contractId;

	String marketId;

	String bundleId;

	String bundleName;

	Bundle bundle;

	boolean imBidding = false, imAsking = false;

	boolean activeBid = true, activeAsk = true;

	int bid, ask, last, held, netheld = -1, deltaheld = 0, myBid, myAsk;

	boolean refreshOrders = false;

	Vector allOrders = null;

	public int getHeld() {
		return held;
	}

	public Contract(String n, String id) {
		countId = count++;
		contractName = n;
		contractId = id;
	}

	public boolean hasExtreamBid() {
		for (int b = 0; b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			int extreamBid = (int) (bid * .1);
			if (iemo.val + 50 > extreamBid && iemo.val - 50 < extreamBid)
				return true;
		}
		return false;
	}

	public boolean hasNickelBid() {
		for (int b = 0; b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			int nickleBid = bid - 150;
			nickleBid -= nickleBid % 50;
			if (iemo.val % 50 == 0 && iemo.val + 150 > nickleBid
					&& iemo.val - 150 < nickleBid)
				return true;
		}
		return false;
	}

	public boolean hasExtreamAsk() {
		for (int b = 0; b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			int extreamAsk = (int) (1000 - (1000 - ask) * .1);
			if (iemo.val + 50 > extreamAsk && iemo.val - 50 < extreamAsk)
				return true;
		}
		return false;
	}

	public boolean hasNickelAsk() {
		for (int b = 0; b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			int nickleAsk = ask + 150;
			nickleAsk -= nickleAsk % 50;
			if (iemo.val % 50 == 0 && iemo.val + 150 > nickleAsk
					&& iemo.val - 150 < nickleAsk)
				return true;
		}
		return false;
	}

	public boolean hasMiddleAsk() {
		for (int b = 0; b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			int middleAsk = ask
					+ (int) (Math.max(IEMTool.LOWESTBIDALLOWED, (1000 - ask)
							* IEMTool.MIDDLEMARGIN));
			middleAsk -= middleAsk % 2;
			if (iemo.val + 50 > middleAsk && iemo.val - 50 < middleAsk)
				return true;
		}
		return false;
	}

	public boolean hasMiddleBid() {
		for (int b = 0; b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			int middleBid = bid
					- (int) (Math.max(IEMTool.LOWESTBIDALLOWED, bid
							* IEMTool.MIDDLEMARGIN));
			middleBid += middleBid % 2;
			if (iemo.val + 50 > middleBid && iemo.val - 50 < middleBid)
				return true;
		}
		return false;
	}

	public int numZeroBid() {
		if (myBid == 0)
			return 0;
		int count = 0;
		for (int b = 0; b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			if (iemo.zeroRiskBid())
				count++;
		}
		return count;
	}

	public int numOneAsk() {
		if (myAsk == 0)
			return 0;
		int count = 0;
		for (int b = 0; b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			if (iemo.zeroRiskAsk())
				count++;
		}
		return count;
	}

	public void executeBidLimit(double m, String num, int timeOut) {
		IEMTool.placeLimitOrder(IEMTool.placeBid, "" + m, num, contractId,
				marketId, timeOut);
	}

	public void executeAskLimit(double m, String num, int timeOut) {
		IEMTool.placeLimitOrder(IEMTool.placeAsk, "" + m, num, contractId,
				marketId, timeOut);
	}

	public void downloadOrders() {
		if (allOrders == null)
			allOrders = new Vector();
		else
			return;
		if (myBid > 0) {
			String rawdata = IEMTool
					.readAllSocket(
							"GET "
									+ "/webex/WebEx.dll?OutstandingOrderHandler?USERTYPE=trader&LOGIN="
									+ IEMTool.userId
									+ "&SESSIONID="
									+ IEMTool.sessionId
									+ "&LANGUAGE=english&Markets="
									+ marketId
									+ "&Asset="
									+ contractId
									+ "&Panel_id=main&OrderType=B HTTP/1.0\r\n\r\n",
							"", "iemweb.biz.uiowa.edu", 80);
			Thread.yield();
			if (rawdata == null) {
				IEMTool.doLogout();
				return;
			}
			String tabledata = MLtoText.cutMLaddSpaces(rawdata);
			rawdata = Stuff.superTrim(rawdata);
			tabledata = Stuff.superTrim(tabledata);
			if (tabledata.indexOf("Order Details") >= 0)
				tabledata = tabledata.substring(tabledata
						.indexOf("Order Details"));
			String mm[] = Stuff.getTokens(tabledata, " ()");
			Vector newOrders = new Vector();
			for (int b = 0; b < mm.length; b++) {
				if (mm[b].equals("Order") && mm[b + 1].equals("Details")) {
					try {
						int v = (int) (Double.parseDouble(mm[b + 2]) * 1000);
						int q = (int) (Double.parseDouble(mm[b + 4]));
						IEMOrder iemo = new IEMOrder(true, this, v, q);
						newOrders.addElement(iemo);
					} catch (NumberFormatException nfe) {
						IEMTool.reportTool("MyError: bad bid found>\n"
								+ rawdata + "\n>>\n>" + tabledata);
					}
				}
			}
			while (rawdata.indexOf("DeleteOrder") >= 10) {
				String loc = rawdata
						.substring(rawdata.indexOf("DeleteOrder") - 10);
				rawdata = loc.substring(loc.indexOf("\""));
				loc = loc.substring(0, loc.indexOf("\""));
				if (newOrders.size() > 0) {
					IEMOrder iemo = (IEMOrder) newOrders.elementAt(0);
					newOrders.removeElementAt(0);
					iemo.setLink(loc);
					allOrders.addElement(iemo);
				}
			}
		}
		if (myAsk > 0) {
			String rawdata = IEMTool
					.readAllSocket(
							"GET "
									+ "/webex/WebEx.dll?OutstandingOrderHandler?USERTYPE=trader&LOGIN="
									+ IEMTool.userId
									+ "&SESSIONID="
									+ IEMTool.sessionId
									+ "&LANGUAGE=english&Markets="
									+ marketId
									+ "&Asset="
									+ contractId
									+ "&Panel_id=main&OrderType=A HTTP/1.0\r\n\r\n",
							"", "iemweb.biz.uiowa.edu", 80);
			Thread.yield();
			if (rawdata == null) {
				IEMTool.doLogout();
				return;
			}
			String tabledata = MLtoText.cutMLaddSpaces(rawdata);
			rawdata = Stuff.superTrim(rawdata);
			tabledata = Stuff.superTrim(tabledata);
			if (tabledata.indexOf("Order Details") >= 0)
				tabledata = tabledata.substring(tabledata
						.indexOf("Order Details"));
			String mm[] = Stuff.getTokens(tabledata, " ()");
			Vector newOrders = new Vector();
			for (int b = 0; b < mm.length; b++) {
				if (mm[b].equals("Order") && mm[b + 1].equals("Details")) {
					try {
						int v = (int) (Double.parseDouble(mm[b + 2]) * 1000);
						int q = (int) (Double.parseDouble(mm[b + 4]));
						IEMOrder iemo = new IEMOrder(false, this, v, q);
						newOrders.addElement(iemo);
					} catch (NumberFormatException nfe) {
						IEMTool.reportTool("MyError: bad ask found>\n"
								+ rawdata + "\n>>\n>" + tabledata);
					}
				}
			}
			while (rawdata.indexOf("DeleteOrder") >= 10) {
				String loc = rawdata
						.substring(rawdata.indexOf("DeleteOrder") - 10);
				rawdata = loc.substring(loc.indexOf("\""));
				loc = loc.substring(0, loc.indexOf("\""));
				if (newOrders.size() > 0) {
					IEMOrder iemo = (IEMOrder) newOrders.elementAt(0);
					newOrders.removeElementAt(0);
					iemo.setLink(loc);
					allOrders.addElement(iemo);
				}
			}
		}
		for (int a = 0; a < allOrders.size(); a++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(a);
			iemo.setBundleVal(bundle.getBundleValue() / 1000);
			if (iemo.zeroRiskBid() && iemo.quant == 1)
				activeBid = false;
			if (iemo.zeroRiskAsk() && iemo.quant == 1)
				activeAsk = false;
		}
		IEMTool.reportStatus();
	}

	public void setVals(String n, String b, String a, String l, String h,
			String mb, String ma, String mi) {
		contractName = n;
		if (b.endsWith("*")) {
			imBidding = true;
			b = b.substring(0, b.length() - 1);
		} else {
			imBidding = false;
		}
		if (a.endsWith("*")) {
			imAsking = true;
			a = a.substring(0, a.length() - 1);
		} else {
			imAsking = false;
		}
		try {
			bid = (int) (Double.parseDouble(b) * 1000);
		} catch (NumberFormatException nfe) {
			bid = -1;
		}
		try {
			ask = (int) (Double.parseDouble(a) * 1000);
		} catch (NumberFormatException nfe) {
			ask = 1001;
		}
		try {
			last = (int) (Double.parseDouble(l) * 1000);
		} catch (NumberFormatException nfe) {
			last = -1;
		}
		int newHeld = -1, newMyBid = -1, newMyAsk = -1;
		try {
			newHeld = Integer.parseInt(h);
		} catch (NumberFormatException nfe) {
			held = -1;
		}
		try {
			newMyBid = Integer.parseInt(mb);
		} catch (NumberFormatException nfe) {
			myBid = -1;
		}
		try {
			newMyAsk = Integer.parseInt(ma);
		} catch (NumberFormatException nfe) {
			myAsk = -1;
		}
		marketId = mi;
		boolean newOrders = (allOrders == null) || newHeld != held
				|| newMyBid != myBid || newMyAsk != myAsk;
		myBid = newMyBid;
		held = newHeld;
		myAsk = newMyAsk;
		if (bid == ask) {
			bid = 0;
			ask = 1001;
			IEMTool.doLogout();
		}
		if (newOrders) {
			allOrders = null;
		}
		netheld = -1;
		deltaheld = 0;
	}

	public String valReport(Bundle b) {
		int sv = b.contractSellValue(this);
		int bc = b.contractBuyCost(this);
		return " sellVal=" + sv + (sv == bid ? "" : "+") + " buyCost=" + bc
				+ (bc == ask ? "" : "+");
	}

	public String ordersReport() {
		String ret = contractName + " bids=";
		Vector sort = new Vector();
		while (allOrders != null && allOrders.size() > 0) {
			IEMOrder best = (IEMOrder) allOrders.elementAt(0);
			for (int b = 1; b < allOrders.size(); b++) {
				IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
				if (iemo.val < best.val)
					best = iemo;
			}
			allOrders.removeElement(best);
			sort.addElement(best);
		}
		allOrders = sort;
		boolean foundfirst = false;
		for (int b = 0; allOrders != null && b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			if (foundfirst && iemo.isBid)
				ret += ",";
			if (iemo.isBid)
				foundfirst = true;
			if (iemo.isBid && iemo.val == 0 && held == 1)
				ret += "lock";
			else if (iemo.isBid && iemo.val == 0)
				ret += "min";
			else if (iemo.isBid)
				ret += "" + (iemo.val % 50 == 0 ? "n" : "") + iemo.quant + "@"
						+ iemo.val;
		}
		ret += "       asks=";
		foundfirst = false;
		for (int b = 0; allOrders != null && b < allOrders.size(); b++) {
			IEMOrder iemo = (IEMOrder) allOrders.elementAt(b);
			if (foundfirst && !iemo.isBid)
				ret += ",";
			if (!iemo.isBid)
				foundfirst = true;
			if (!iemo.isBid && iemo.val == 1000 && held == 1)
				ret += "lock";
			else if (!iemo.isBid && iemo.val == 1000)
				ret += "max";
			else if (!iemo.isBid)
				ret += "" + (iemo.val % 50 == 0 ? "n" : "") + iemo.quant + "@"
						+ iemo.val;
		}
		return ret;

	}

	public void checkFirstBid() {
		if (numZeroBid() < 2 && !IEMTool.isPract()) {
			IEMTool.reportOrders("ib " + contractName + " ");
			if (numZeroBid() > 0) {
				IEMTool.placeLimitOrder(IEMTool.placeBid, "0.0", ""
						+ INITIALNUM, contractId, marketId,
						IEMTool.FOREVERDELAY);
			} else
				IEMTool.placeLimitOrder(IEMTool.placeBid, "0.0", ""
						+ INITIALNUM, contractId, marketId, IEMTool.FOURWEEKS);
		}
	}

	public void checkFirstAsk() {
		if (numOneAsk() < 2 && !IEMTool.isPract()) {
			IEMTool.reportOrders("ia " + contractName + " ");
			int val = bundle.getBundleValue() / 1000;
			if (numOneAsk() > 0) {
				IEMTool.placeLimitOrder(IEMTool.placeAsk, val + "", ""
						+ INITIALNUM, contractId, marketId,
						IEMTool.FOREVERDELAY);
			} else
				IEMTool.placeLimitOrder(IEMTool.placeAsk, val + "", ""
						+ INITIALNUM, contractId, marketId, IEMTool.FOURWEEKS);
		}
	}

	public void setBundle(Bundle b) {
		bundleId = b.getId();
		bundleName = b.getName();
		bundle = b;
	}

	public void update(Contract c) {
		allOrders = c.allOrders;
		activeBid = c.activeBid;
		activeAsk = c.activeAsk;
	}

	public String getId() {
		return contractId;
	}

	public String getName() {
		return contractName;
	}

	public String toString(Bundle b) {
		int sv = b.contractSellValue(this);
		int bc = b.contractBuyCost(this);
		String ret = contractName + "                     ";
		ret = ret.substring(0, 18);

		String bs = (bid < 100 ? " " : "")
				+ (bid < 10 ? " " : "")
				+ bid
				+ (sv == bid ? "" : "(" + (sv < 100 ? " " : "")
						+ (sv < 10 ? " " : "") + sv + ")")
				+ (imBidding ? "*" : " ") + (activeBid ? " " : "&")
				+ "          ";
		ret += " bid=" + bs.substring(0, 10);
		String as = (ask < 100 ? " " : "")
				+ (ask < 10 ? " " : "")
				+ ask
				+ (bc == ask ? "" : "(" + (bc < 100 ? " " : "")
						+ (bc < 10 ? " " : "") + bc + ")")
				+ (imAsking ? "*" : " ") + (activeAsk ? " " : "&")
				+ "          ";
		ret += " ask=" + as.substring(0, 10);
		String ls = (last < 100 ? " " : "") + (last < 10 ? " " : "") + last;
		ret += "   last=" + ls;
		ret += "   held=" + held;
		ret += "   myBid=" + myBid;

		ret += " myAsk=" + myAsk;
		return ret;
	}

	public String toString() {
		String ret = contractName + "                    ";
		ret = ret.substring(0, 18);
		String bs = (bid < 100 ? " " : "") + (bid < 10 ? " " : "") + bid;
		ret += " bid=" + bs + (imBidding ? "*" : " ") + (activeBid ? " " : "&");
		String as = (ask < 100 ? " " : "") + (ask < 10 ? " " : "") + ask;
		ret += " ask=" + as + (imAsking ? "*" : " ") + (activeAsk ? " " : "&");
		String ls = (last < 100 ? " " : "") + (last < 10 ? " " : "") + last;
		ret += "   last=" + ls;
		ret += "   held=" + held;
		ret += "   myBid=" + myBid;

		ret += " myAsk=" + myAsk;
		return ret;
	}

	public String ordersString() {
		return ""
				+ contractId
				+ " "
				+ marketId
				+ " "
				+ IEMTool.sessionId
				+ " "
				+ getName()
				+ " "
				+ bundleName
				+ " "
				+ bid
				+ (imBidding ? "*" : "")
				+ " "
				+ ask
				+ (imAsking ? "*" : "")
				+ " "
				+ (netheld == -1 ? held + "*" : (held - netheld) + "")
				+ " "
				+ deltaheld
				+ " "
				+ (marketId == null || contractId == null || getName() == null
						|| bundleName == null ? "*PARTIAL*" : "");
	}
}
