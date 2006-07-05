package aj.iem;

import java.util.Vector;

import aj.misc.Stuff;

public class Bundle {
	private String name, id;

	String marketId;

	private Vector bundleContracts = new Vector();

	Market market;

	private int bundleValue = 1000;

	private IEMTool tool;

	int getBundleValue() {
		return bundleValue;
	}

	private boolean isFreeAllowed() {// 0 bid 1 ask
		String name = getName().toUpperCase();
		Vector v = tool.allowedFreeNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked free bundle "+name);
		return false;
	}

	private boolean isExtreamAllowed() {
		String name = getName().toUpperCase();
		Vector v = tool.allowedExtreamNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked nickel bundle "+name);
		return false;
	}

	private boolean isNickleAllowed() {
		String name = getName().toUpperCase();
		Vector v = tool.allowedNickleNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked nickel bundle "+name);
		return false;
	}

	private boolean isMiddleAllowed() {
		String name = getName().toUpperCase();
		Vector v = tool.allowedMiddleNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked middle bundle "+name);
		return false;
	}

	private boolean isAllowed() {
		String name = getName().toUpperCase();
		Vector v = tool.allowedBundleNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked entire bundle "+name);
		return false;
	}

	public Bundle(IEMTool toolref, String n, String i) {
		tool = toolref;
		name = n;
		id = i;
		if (name.endsWith("$2"))
			bundleValue = 2000;
		else
			bundleValue = 1000;
	}

	public boolean equals(Bundle b) {
		return id != null && b != null && b.id != null && b.id.equals(id);
	}

	public String getQuickSummary() {
		if (!isAllowed())
			return "";
		String report = "";
		String na = getName().toUpperCase();
		if (na.indexOf("COMP") >= 0)
			report = "C" + getName().substring(getName().length() - 1);
		else if (na.indexOf("DCONV") >= 0)
			report = "DC";
		else if (na.indexOf("FR") >= 0)
			report = "FR"
					+ getName().substring(getName().indexOf("$") + 1,
							getName().indexOf("$") + 3);
		else if (na.indexOf("MSF") >= 0)
			report = "MS" + getName().substring(getName().length() - 1);
		else if (na.startsWith("PRES"))
			report = "Pres";
		else if (na.indexOf("RECALL1") >= 0)
			report = "R1";
		else if (na.indexOf("RECALL2") >= 0)
			report = "R2";
		else if (na.startsWith("CONGRESS"))
			report = "CG";
		else if (na.startsWith("HOUSE"))
			report = "RH";
		else if (na.startsWith("SENATE"))
			report = "RS";
		else if (na.startsWith("GOOGLE"))
			report = "GG" + getName().substring(6);
		else
			report += getName();
		report += ":";
		int minheld = getMinHeld();
		if (getMaxHeld() - minheld == 0)
			return report;
		for (int a = 0; a < bundleContracts.size(); a++) {
			if (a > 0)
				report += ",";
			Contract cc = (Contract) bundleContracts.elementAt(a);
			int net = cc.getHeld() - minheld;
			if (net > 0)
				report += net;
		}
		return report;
	}

	String getSummary() {
		String members = "";
		for (int a = 0; a < bundleContracts.size(); a++) {
			if (members.length() > 0)
				members += ", ";
			Contract cc = (Contract) bundleContracts.elementAt(a);
			members += cc.getName() + ":" + cc.contractId;
		}
		return "Bundle name=" + name + ":" + id + " members=" + members
				+ "\n  bundBid=" + getMarketBidPrice() + " bundAsk="
				+ getMarketAskPrice() + " bundLast=" + getLastPrice()
				+ " bundVal=" + getBundleValue() + " "
				+ (isFreeAllowed() ? "F" : "-")
				+ (isNickleAllowed() ? "N" : "-")
				+ (isExtreamAllowed() ? "E" : "-")
				+ (isMiddleAllowed() ? "M" : "-") + (isAllowed() ? "A" : "-");
	}

	public String getOrdersReport() {
		String report = "";
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			report += cc.ordersString() + "\n";
		}
		return report;
	}

	String fullReport() {
		String report = "";
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			// report+=cc.toString()+" "+cc.valReport(this)+"\n";
			report += cc.toString(this) + "\n";
		}
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			report += cc.ordersReport() + "\n";
		}
		return report;
	}

	int getMinHeld() {
		if (bundleContracts.size() >= 1) {
			Contract cc = (Contract) bundleContracts.elementAt(0);
			int total = cc.held;
			for (int a = 0; a < bundleContracts.size(); a++) {
				cc = (Contract) bundleContracts.elementAt(a);
				total = Math.min(total, cc.held);
				if (cc.held < 0)
					return 0;
			}
			for (int a = 0; a < bundleContracts.size(); a++) {
				cc = (Contract) bundleContracts.elementAt(a);
				cc.netheld = total;
			}
			return total;
		}
		return 0;
	}

	public int getMaxHeld() {
		int total = 0;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			total = Math.max(total, cc.held);
		}
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			cc.deltaheld = cc.held - total;
		}
		return total;
	}

	private int getLastPrice() {
		if (bundleContracts.size() >= 2) {
			int total = 0;
			for (int a = 0; a < bundleContracts.size(); a++) {
				Contract cc = (Contract) bundleContracts.elementAt(a);
				total += cc.last;
			}
			return total;
		}
		return 0;
	}

	private String showMarketAskPrice() {
		String ret = "";
		if (bundleContracts.size() >= 2) {
			ret += "total=" + getMarketAskPrice() + " ";
			for (int a = 0; a < bundleContracts.size(); a++) {
				Contract cc = (Contract) bundleContracts.elementAt(a);
				if (ret.length() > 0)
					ret += ", ";
				ret += cc.getName() + " at " + cc.ask;
			}
		}
		return ret;
	}

	private int getMarketAskPrice() {
		if (bundleContracts.size() >= 2) {
			int total = 0;
			for (int a = 0; a < bundleContracts.size(); a++) {
				Contract cc = (Contract) bundleContracts.elementAt(a);
				total += cc.ask;
			}
			return total;
		}
		return 1001;
	}

	public String showMarketBidPrice() {
		String ret = "";
		if (bundleContracts.size() >= 2) {
			ret += "total=" + getMarketBidPrice() + " ";
			for (int a = 0; a < bundleContracts.size(); a++) {
				Contract cc = (Contract) bundleContracts.elementAt(a);
				if (ret.length() > 0)
					ret += ", ";
				ret += cc.getName() + " at " + cc.bid;
			}
		}
		return ret;
	}

	private int getMarketBidPrice() {
		if (bundleContracts.size() >= 2) {
			int total = 0;
			for (int a = 0; a < bundleContracts.size(); a++) {
				Contract cc = (Contract) bundleContracts.elementAt(a);
				total += cc.bid;
			}
			return total;
		}
		return 2;
	}

	void addContract(Contract cc) {
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract c = (Contract) bundleContracts.elementAt(a);
			if (c.getId().equals(cc.getId())) {
				cc.update(c);
				bundleContracts.removeElement(c);
				a--;
			}
		}
		bundleContracts.addElement(cc);
	}

	private void subToAllContracts(int x) {
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			cc.held -= x;
		}
	}

	private void addToAllContracts(int x) {
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			cc.held += x;
		}
	}

	private void nickelBid() {
		if (getMarketBidPrice() > getBundleValue())
			return;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (!cc.activeBid)
				continue;
			if (cc.hasNickelBid())
				continue;
			int nickelbid = cc.bid - 150;
			nickelbid -= nickelbid % 50;
			if (nickelbid < IEMTool.LOWESTBIDALLOWED || nickelbid > cc.bid)
				continue;
			tool.reportOrders("nb " + cc.getName() + " " + nickelbid + " from "
					+ cc.bid);
			cc.executeBidLimit((nickelbid) / 1000.0, "" + tool.NICK_BUND_SIZE,
					tool.NICKLEBIDASKDELAY);
			nickelbid -= 100;
			if (nickelbid < IEMTool.LOWESTBIDALLOWED || nickelbid > cc.bid)
				continue;
			tool.reportOrders("nb2 " + cc.getName() + " " + nickelbid
					+ " from " + cc.bid);
			cc.executeBidLimit((nickelbid) / 1000.0, ""
					+ (tool.NICK_BUND_SIZE * 2), tool.NICKLEBIDASKDELAY);
		}
	}

	private void extreamBid() {
		if (getMarketBidPrice() > getBundleValue())
			return;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (!cc.activeBid)
				continue;
			if (cc.hasExtreamBid())
				continue;
			int extreambid = (int) (cc.bid * .1);
			if (extreambid < 1 || extreambid > cc.bid)
				continue;
			tool.reportOrders("eb " + cc.getName() + " " + extreambid
					+ " from " + cc.bid);
			cc.executeBidLimit((extreambid) / 1000.0, ""
					+ tool.EXTREAM_BUND_SIZE, tool.EXTREAMBIDASKDELAY);
		}
	}

	private void extreamAsk() {
		if (getMarketAskPrice() < getBundleValue())
			return;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (cc.held == 0)
				continue;
			if (!cc.activeAsk)
				continue;
			if (cc.hasExtreamAsk())
				continue;
			int extreamask = (int) (1000 - (1000 - cc.ask) * .10);
			if (extreamask > 999 || extreamask < cc.ask)
				continue;
			tool.reportOrders("ea " + cc.getName() + " " + extreamask
					+ " from " + cc.ask);
			cc.executeAskLimit((extreamask) / 1000.0, ""
					+ tool.EXTREAM_BUND_SIZE, tool.EXTREAMBIDASKDELAY);
		}
	}

	private void nickelAsk() {
		if (getMarketAskPrice() < getBundleValue())
			return;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (cc.held == 0)
				continue;
			if (!cc.activeAsk)
				continue;
			if (cc.hasNickelAsk())
				continue;
			int nickelask = cc.ask + 150;
			nickelask -= nickelask % 50;
			if (nickelask > getBundleValue() - IEMTool.LOWESTBIDALLOWED
					|| nickelask < cc.ask)
				continue;
			tool.reportOrders("na " + cc.getName() + " " + nickelask + " from "
					+ cc.ask);
			cc.executeAskLimit((nickelask) / 1000.0, "" + tool.NICK_BUND_SIZE,
					tool.NICKLEBIDASKDELAY);
			nickelask += 100;
			if (nickelask > getBundleValue() - IEMTool.LOWESTBIDALLOWED
					|| nickelask < cc.ask)
				continue;
			tool.reportOrders("na " + cc.getName() + " " + nickelask + " from "
					+ cc.ask);
			cc.executeAskLimit((nickelask) / 1000.0, ""
					+ (tool.NICK_BUND_SIZE * 2), tool.NICKLEBIDASKDELAY);
		}
	}

	private void middleAsk() {
		if (getMarketAskPrice() < getBundleValue())
			return;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (!cc.activeAsk)
				continue;
			if (!cc.hasMiddleAsk()) {
				int middleAsk = cc.ask
						+ (int) (Math.max(IEMTool.LOWESTBIDALLOWED,
								(1000 - cc.ask) * IEMTool.MIDDLEMARGIN));
				middleAsk += middleAsk % 2;
				if (middleAsk > getBundleValue() - IEMTool.LOWESTBIDALLOWED
						|| middleAsk < cc.ask)
					continue;
				if (cc.held == 0)
					continue;
				tool.reportOrders("ma " + cc.getName() + " " + middleAsk
						+ " from " + cc.ask);
				cc.executeAskLimit((middleAsk) / 1000.0, ""
						+ tool.MID_BUND_SIZE, tool.MIDDLEBIDASKDELAY);
			}
		}
	}

	private void middleBid() {
		if (getMarketBidPrice() > getBundleValue())
			return;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (!cc.activeBid)
				continue;
			if (!cc.hasMiddleBid()) {
				int middleBid = cc.bid
						- (int) (Math.max(IEMTool.LOWESTBIDALLOWED, cc.bid
								* IEMTool.MIDDLEMARGIN));
				middleBid += middleBid % 2;
				if (middleBid < IEMTool.LOWESTBIDALLOWED || cc.bid < middleBid)
					continue;
				if (tool.cash < middleBid)
					continue;
				tool.reportOrders("mb " + cc.getName() + " " + middleBid
						+ " from " + cc.bid);
				cc.executeBidLimit((middleBid) / 1000.0, ""
						+ tool.MID_BUND_SIZE, tool.MIDDLEBIDASKDELAY);
			}
		}
	}

	int getAssetsValue() {
		int total = 0;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			total += contractSellValue(cc) * (cc.held - getMinHeld());
		}
		return total;
	}

	int contractSellValue(Contract c) {
		int cbid = c.bid;
		int bask = 0;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (cc == c)
				continue;
			bask += cc.ask;
		}
		bask = getBundleValue() - bask;
		int bestSellPrice = Math.max(bask, cbid);
		return bestSellPrice;
	}

	int contractBuyCost(Contract c) {
		int cask = c.ask;
		int bbid = 0;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (cc == c)
				continue;
			bbid += cc.bid;
		}
		bbid = getBundleValue() - bbid;
		int cheapestPriceCanBuy = Math.min(bbid, cask);
		return cheapestPriceCanBuy;
	}

	private void checkDiscountSell() {// always okay
		if (getMarketBidPrice() > getBundleValue() && getMinHeld() > 1) {
			int max = getMinHeld() - 1;
			tool.reportOrders("ds " + name + " " + showMarketBidPrice()
					+ " request " + max + " order amount");
			IEMTool.placeMarketOrder(IEMTool.sellMarkBundle, ""
					+ Stuff.trunc(getMarketBidPrice() / 1000.0, 3), "" + max,
					id, marketId, tool);
			market.lastUpdate = -1;
		}
	}

	private void checkDiscountBuy() {// always okay
		if (getMarketAskPrice() < getBundleValue()
				&& tool.cash + tool.MINCASHRESERVE > getMarketAskPrice()) {
			int max = (int) (tool.cash / getMarketAskPrice());
			tool.reportOrders("db " + name + "@" + showMarketAskPrice()
					+ " request " + max + " order amount");
			IEMTool.placeMarketOrder(IEMTool.buyMarkBundle, ""
					+ Stuff.trunc(getMarketAskPrice() / 1000.0, 3), "" + max,
					id, marketId, tool);
			market.lastUpdate = -1;
		}
	}

	private void checkSurplusSell() {
		if (getMinHeld() > tool.MAX_BUND_SIZE
				|| (tool.cash < tool.MINCASHRESERVE && getMinHeld() > tool.MIN_BUND_SIZE)) {
			int tosell = getMinHeld()
					- (tool.MAX_BUND_SIZE + tool.MIN_BUND_SIZE) / 2;
			if (tool.cash < tool.MINCASHRESERVE)
				tosell = getMinHeld() - tool.MIN_BUND_SIZE;
			tool.reportOrders("ss " + name + " #" + tosell);
			IEMTool.placeMarketOrder(IEMTool.sellFixBundle, "1", "" + tosell,
					id, marketId, tool);
			tool.cash += getBundleValue() * tosell;
			subToAllContracts(tosell);
		}
	}

	private boolean shortageBought = false;

	private void checkShortageBuy() {
		if (tool.cash > tool.MINCASHRESERVE && getMinHeld() >= 0
				&& getMinHeld() < tool.MIN_BUND_SIZE) {
			shortageBought = true;
			int tobuy = tool.MIN_BUND_SIZE;
			if (getMarketBidPrice() > getBundleValue()) {
				tobuy = tool.MAX_BUND_SIZE - getMinHeld() - 2;
			}
			if (tool.cash - tobuy < tool.MINCASHRESERVE)
				tobuy = (int) (tool.cash - tool.MINCASHRESERVE - 1);
			if (tobuy <= 0) {
				tool.reportOrders("bs " + name + " #" + tobuy
						+ " not enough cash reserves");
				return;
			}
			tool.reportOrders("bs " + name + " #" + tobuy);
			IEMTool.placeMarketOrder(IEMTool.buyFixBundle, "1", "" + tobuy, id,
					marketId, tool);
			tool.cash -= getBundleValue() * tobuy;
			addToAllContracts(tobuy);
		} else {
			shortageBought = false;
		}
	}

	void makeOrders() {
		if (!isAllowed())
			return;
		checkShortageBuy();
		checkSurplusSell();
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			if (isFreeAllowed())
				cc.checkFirstBid();
			if (!shortageBought && isFreeAllowed())
				cc.checkFirstAsk();
		}
		checkDiscountSell();
		checkDiscountBuy();

		if (getMarketBidPrice() < getBundleValue()) {// don't bid if doing
			// discount!
			if (isMiddleAllowed())
				middleBid();
			if (isNickleAllowed())
				nickelBid();
			if (isExtreamAllowed())
				extreamBid();
		}
		if (getMarketAskPrice() > getBundleValue()) {// don't ask if doing
			// discount!
			if (isNickleAllowed())
				nickelAsk();
			if (isMiddleAllowed())
				middleAsk();
			if (isExtreamAllowed())
				extreamAsk();
		}
	}

	String getName() {
		return name;
	}

}
