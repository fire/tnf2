package aj.nf;

import java.util.Vector;

public class AuctionItem {
	public String origOwner;

	private int close;

	private String nfoid;

	private double reserve;

	private String highBidder = "NONE";

	private double bidamount = 0;

	private double maxbidamount = 0;

	public boolean isOwner(String t) {
		return t.equalsIgnoreCase(origOwner) || highBidder.equalsIgnoreCase(t);
	}

	public AuctionItem(String n, double r, String o, int c, String hb, double ba) {
		nfoid = n;
		reserve = r;
		origOwner = o;
		close = c;
		highBidder = hb;
		bidamount = ba;
		bidamount = ba;
	}

	public AuctionItem(String i, double r, String o, int c) {
		nfoid = i;
		reserve = r;
		origOwner = o;
		close = c;
	}

	public String getId() {
		return nfoid;
	}

	public int getClosing() {
		return close;
	}

	public void advance() {
		close -= 1;
	}

	public String toString() {
		NFObject n = Universe.getNFObjectById(nfoid);
		if (n == null) {
			System.out.println("MyError: Auction item " + nfoid + " not found");
			return "System Error.";
		}
		String s = "";
		if (n instanceof Active) {
			s = ((Active) n).displayHeader();
		} else
			s = n.display();
		if (bidamount == 0) {
			s += "\n  Reserve " + Stuff.money(reserve, 2);
		} else {
			s += "\n  High bidder is " + highBidder + " at "
					+ Stuff.money(bidamount, 2) + ".";
		}
		s += "\n  Closing ";
		if (close > 2) {
			s += "in " + (close - 1) + " turns\n";
		} else {
			s += "this turn\n";
		}
		return s;
	}

	public boolean newHighBidder(Corp c, double amt) {
		String hb = c.getTick();
		if (hb.equalsIgnoreCase(origOwner)) {
			c.report += "  AUCTION NOTICE:  You cannot bid on your own auction "
					+ nfoid + "\n";
			return false;
		}

		boolean alreadyHighBidder = hb.equalsIgnoreCase(highBidder);
		if (alreadyHighBidder) {
			if (amt > maxbidamount) {
				maxbidamount = amt;
				c.report += "  AUCTION NOTICE: Proxy bid on " + nfoid
						+ " set to " + amt + "\n";
				return true;
			} else if (amt > bidamount && amt < maxbidamount) {
				c.report += "  AUCTION NOTICE: Cannot lower proxy bid on "
						+ nfoid + ".  Prxoy bid remains at " + maxbidamount
						+ "\n";
				return false;
			} else if (amt < bidamount) {
				c.report += "  AUCTION NOTICE: Cannot lower winning bid on "
						+ nfoid + ".  Bid remains at " + bidamount + "\n";
				return false;
			}
		} else {
			if (reserve > amt) {
				c.report += "  AUCTION NOTICE: Insufficient bid on " + nfoid
						+ ".  Reserve not met.\n";
				return false;
			}
			if (maxbidamount * 1.1 > amt) {
				amt = Math.min(maxbidamount, amt);
				Corp l = Universe.getCorpByTick(highBidder);
				if (l != null) {
					l.addReport("  AUCTION NOTICE: Proxy bid on " + nfoid
							+ " made with bid of " + Stuff.money(amt, 2)
							+ " to match bid by " + c.getTick() + "\n");
					l.makePayment(amt - bidamount);
				}
				bidamount = amt;
				c.report += "  AUCTION NOTICE: Insufficient bid on " + nfoid
						+ ".  Out bid by " + highBidder + " proxy bid of "
						+ amt + ".\n";
				return false;
			}
			if (maxbidamount * 1.1 <= amt) {
				Corp l = Universe.getCorpByTick(highBidder);
				if (l != null) {
					l.addReport("  AUCTION NOTICE: Outbid on " + nfoid + " by "
							+ c.getTick() + " with bid of "
							+ Stuff.money(amt, 2) + "\n");
					l.cash += bidamount;

					c.report += "  AUCTION NOTICE:  You are current highbidder on "
							+ nfoid
							+ " exceeding "
							+ highBidder
							+ " bid of "
							+ Stuff.money(bidamount, 2) + "\n";
					bidamount = maxbidamount;
					maxbidamount = amt;
					c.makePayment(bidamount);
				} else {
					c.report += "  AUCTION NOTICE:  You are current highbidder on "
							+ nfoid + " exceeding the reserve price.\n";
					bidamount = reserve;
					maxbidamount = amt;
					c.makePayment(reserve);
				}
				highBidder = hb;
			}
		}
		return false;
	}

	public void closeAuctionItem() {
		if (highBidder.equalsIgnoreCase("NONE")) {
			returnToOwner();
		} else {
			transferToBidder();
		}
	}

	public void transferToBidder() {
		NFObject n = Universe.getNFObjectById(nfoid);
		Corp c = Universe.getCorpByTick(highBidder);
		c.addReport("AUCTION NOTICE you have won your bid on " + nfoid
				+ " paying " + Stuff.money(bidamount, 2) + "\n");
		Corp l = Universe.getCorpByTick(origOwner);
		l.addReport("AUCTION CLOSED on " + nfoid + " EARNED "
				+ Stuff.money(bidamount, 2) + "\n");
		l.cash += bidamount;
		n.setCorpTick(highBidder);
		if (n.isMoveable())
			n.setLocation(c.getHome());
		Universe.mergeStockPiles();
	}

	public void returnToOwner() {
		NFObject n = Universe.getNFObjectById(nfoid);
		Corp c = Universe.getCorpByTick(origOwner);
		c.addReport("AUCTION CLOSED.  No sufficent bids received on" + nfoid
				+ ".\n");
		n.setCorpTick(origOwner);
		if (n.isMoveable())
			n.setLocation(c.getHome());
		Universe.mergeStockPiles();
	}

	public String toSaveString() {
		return toGmlPair().toString();
	}

	public GmlPair toGmlPair() {
		Vector v = new Vector();
		GmlPair g;
		g = new GmlPair("origOwner", origOwner);
		v.addElement(g);
		g = new GmlPair("close", close);
		v.addElement(g);
		g = new GmlPair("nfoid", nfoid);
		v.addElement(g);
		g = new GmlPair("reserve", reserve);
		v.addElement(g);
		g = new GmlPair("highBidder", highBidder);
		v.addElement(g);
		g = new GmlPair("bidamount", bidamount);
		v.addElement(g);
		g = new GmlPair("maxbidamount", maxbidamount);
		v.addElement(g);
		g = new GmlPair("AuctionItem", v);
		return g;
	}

	public static AuctionItem parse(GmlPair g) {
		if (!g.getName().equalsIgnoreCase("AuctionItem")) {
			return null;
		}
		GmlPair gg[] = g.getAllByName("origOwner");
		String oo = gg[0].getString();
		gg = g.getAllByName("nfoid");
		String id = gg[0].getString();
		gg = g.getAllByName("highBidder");
		String hb = gg[0].getString();
		gg = g.getAllByName("close");
		int c = (int) (gg[0].getDouble());
		gg = g.getAllByName("reserve");
		double r = gg[0].getDouble();
		gg = g.getAllByName("bidamount");
		double ba = gg[0].getDouble();
		AuctionItem ai = new AuctionItem(id, r, oo, c, hb, ba);
		GmlPair ggg = g.getOneByName("bidamount");
		if (ggg != null) {
			ai.maxbidamount = gg[0].getDouble();
		}
		return ai;
	}
}
