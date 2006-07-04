package aj.iem;

import java.util.Vector;

import aj.misc.Stuff;

public class Bundle {
	String name, id;

	String marketId;

	Vector bundleContracts = new Vector();

	Market market;

	int bundleValue = 1000;

	public int getBundleValue() {
		return bundleValue;
	}

	public boolean isFreeAllowed() {// 0 bid 1 ask
		String name = getName().toUpperCase();
		Vector v = IEMTool.allowedFreeNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked free bundle "+name);
		return false;
	}

	public boolean isExtreamAllowed() {
		String name = getName().toUpperCase();
		Vector v = IEMTool.allowedExtreamNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked nickel bundle "+name);
		return false;
	}

	public boolean isNickleAllowed() {
		String name = getName().toUpperCase();
		Vector v = IEMTool.allowedNickleNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked nickel bundle "+name);
		return false;
	}

	public boolean isMiddleAllowed() {
		String name = getName().toUpperCase();
		Vector v = IEMTool.allowedMiddleNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked middle bundle "+name);
		return false;
	}

	public boolean isAllowed() {
		String name = getName().toUpperCase();
		Vector v = IEMTool.allowedBundleNames;
		for (int a = 0; a < v.size(); a++) {
			String test = (String) v.elementAt(a);
			if (name.startsWith(test.toUpperCase()))
				return true;
		}
		// IEMTool.reportOrders("Blocked entire bundle "+name);
		return false;
	}

	public Bundle(String n, String i) {
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

	public String getSummary() {
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

	public String fullReport() {
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

	public int getMinHeld() {
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

	public int getLastPrice() {
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

	public String showMarketAskPrice() {
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

	public int getMarketAskPrice() {
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

	public int getMarketBidPrice() {
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

	public void addContract(Contract cc) {
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

	public void subToAllContracts(int x) {
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			cc.held -= x;
		}
	}

	public void addToAllContracts(int x) {
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			cc.held += x;
		}
	}

	public void nickelBid() {
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
			IEMTool.reportOrders("nb " + cc.getName() + " " + nickelbid
					+ " from " + cc.bid);
			cc.executeBidLimit((nickelbid) / 1000.0, ""
					+ IEMTool.NICK_BUND_SIZE, IEMTool.NICKLEBIDASKDELAY);
			nickelbid -= 100;
			if (nickelbid < IEMTool.LOWESTBIDALLOWED || nickelbid > cc.bid)
				continue;
			IEMTool.reportOrders("nb2 " + cc.getName() + " " + nickelbid
					+ " from " + cc.bid);
			cc.executeBidLimit((nickelbid) / 1000.0, ""
					+ (IEMTool.NICK_BUND_SIZE * 2), IEMTool.NICKLEBIDASKDELAY);
		}
	}

	public void extreamBid() {
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
			IEMTool.reportOrders("eb " + cc.getName() + " " + extreambid
					+ " from " + cc.bid);
			cc.executeBidLimit((extreambid) / 1000.0, ""
					+ IEMTool.EXTREAM_BUND_SIZE, IEMTool.EXTREAMBIDASKDELAY);
		}
	}

	public void extreamAsk() {
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
			IEMTool.reportOrders("ea " + cc.getName() + " " + extreamask
					+ " from " + cc.ask);
			cc.executeAskLimit((extreamask) / 1000.0, ""
					+ IEMTool.EXTREAM_BUND_SIZE, IEMTool.EXTREAMBIDASKDELAY);
		}
	}

	public void nickelAsk() {
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
			IEMTool.reportOrders("na " + cc.getName() + " " + nickelask
					+ " from " + cc.ask);
			cc.executeAskLimit((nickelask) / 1000.0, ""
					+ IEMTool.NICK_BUND_SIZE, IEMTool.NICKLEBIDASKDELAY);
			nickelask += 100;
			if (nickelask > getBundleValue() - IEMTool.LOWESTBIDALLOWED
					|| nickelask < cc.ask)
				continue;
			IEMTool.reportOrders("na " + cc.getName() + " " + nickelask
					+ " from " + cc.ask);
			cc.executeAskLimit((nickelask) / 1000.0, ""
					+ (IEMTool.NICK_BUND_SIZE * 2), IEMTool.NICKLEBIDASKDELAY);
		}
	}

	public void middleAsk() {
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
				IEMTool.reportOrders("ma " + cc.getName() + " " + middleAsk
						+ " from " + cc.ask);
				cc.executeAskLimit((middleAsk) / 1000.0, ""
						+ IEMTool.MID_BUND_SIZE, IEMTool.MIDDLEBIDASKDELAY);
			}
		}
	}

	public void middleBid() {
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
				if (IEMTool.cash < middleBid)
					continue;
				IEMTool.reportOrders("mb " + cc.getName() + " " + middleBid
						+ " from " + cc.bid);
				cc.executeBidLimit((middleBid) / 1000.0, ""
						+ IEMTool.MID_BUND_SIZE, IEMTool.MIDDLEBIDASKDELAY);
			}
		}
	}

	public int getAssetsValue() {
		int total = 0;
		for (int a = 0; a < bundleContracts.size(); a++) {
			Contract cc = (Contract) bundleContracts.elementAt(a);
			total += contractSellValue(cc) * (cc.held - getMinHeld());
		}
		return total;
	}

	public int contractSellValue(Contract c) {
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

	public int contractBuyCost(Contract c) {
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

	public void checkDiscountSell() {// always okay
		if (getMarketBidPrice() > getBundleValue() && getMinHeld() > 1) {
			int max = getMinHeld() - 1;
			IEMTool.reportOrders("ds " + name + " " + showMarketBidPrice()
					+ " request " + max + " order amount");
			IEMTool.placeMarketOrder(IEMTool.sellMarkBundle, ""
					+ Stuff.trunc(getMarketBidPrice() / 1000.0, 3), "" + max,
					id, marketId);
			market.lastUpdate = -1;
		}
	}

	public void checkDiscountBuy() {// always okay
		if (getMarketAskPrice() < getBundleValue()
				&& IEMTool.cash + IEMTool.MINCASHRESERVE > getMarketAskPrice()) {
			int max = (int) (IEMTool.cash / getMarketAskPrice());
			IEMTool.reportOrders("db " + name + "@" + showMarketAskPrice()
					+ " request " + max + " order amount");
			IEMTool.placeMarketOrder(IEMTool.buyMarkBundle, ""
					+ Stuff.trunc(getMarketAskPrice() / 1000.0, 3), "" + max,
					id, marketId);
			market.lastUpdate = -1;
		}
	}

	public void checkSurplusSell() {
		if (getMinHeld() > IEMTool.MAX_BUND_SIZE
				|| (IEMTool.cash < IEMTool.MINCASHRESERVE && getMinHeld() > IEMTool.MIN_BUND_SIZE)) {
			int tosell = getMinHeld()
					- (IEMTool.MAX_BUND_SIZE + IEMTool.MIN_BUND_SIZE) / 2;
			if (IEMTool.cash < IEMTool.MINCASHRESERVE)
				tosell = getMinHeld() - IEMTool.MIN_BUND_SIZE;
			IEMTool.reportOrders("ss " + name + " #" + tosell);
			IEMTool.placeMarketOrder(IEMTool.sellFixBundle, "1", "" + tosell,
					id, marketId);
			IEMTool.cash += getBundleValue() * tosell;
			subToAllContracts(tosell);
		}
	}

	boolean shortageBought = false;

	public void checkShortageBuy() {
		if (IEMTool.cash > IEMTool.MINCASHRESERVE && getMinHeld() >= 0
				&& getMinHeld() < IEMTool.MIN_BUND_SIZE) {
			shortageBought = true;
			int tobuy = IEMTool.MIN_BUND_SIZE;
			if (getMarketBidPrice() > getBundleValue()) {
				tobuy = IEMTool.MAX_BUND_SIZE - getMinHeld() - 2;
			}
			if (IEMTool.cash - tobuy < IEMTool.MINCASHRESERVE)
				tobuy = (int) (IEMTool.cash - IEMTool.MINCASHRESERVE - 1);
			if (tobuy <= 0) {
				IEMTool.reportOrders("bs " + name + " #" + tobuy
						+ " not enough cash reserves");
				return;
			}
			IEMTool.reportOrders("bs " + name + " #" + tobuy);
			IEMTool.placeMarketOrder(IEMTool.buyFixBundle, "1", "" + tobuy, id,
					marketId);
			IEMTool.cash -= getBundleValue() * tobuy;
			addToAllContracts(tobuy);
		} else {
			shortageBought = false;
		}
	}

	public void makeOrders() {
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

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
}
