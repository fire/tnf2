package aj.testing;

import java.util.Vector;

/*
 * auction
 * choose start bid and max price
 * choose offer price
 * highest start bid looks first
 * must pay highest price below bid
 * or pay lowest price above bid and below max
 * seller sells at offer price or doesn't sell at all
 */

/**
 * Description of the Interface
 * 
 * @author judda
 * @created August 29, 2000
 */
interface Bidder {
	/**
	 * Gets the Bid attribute of the Bidder object
	 * 
	 * @return The Bid value
	 */
	public int getBid();

	/**
	 * Gets the MaxBid attribute of the Bidder object
	 * 
	 * @return The MaxBid value
	 */
	public int getMaxBid();

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 * @param price
	 *            Description of Parameter
	 */
	public void buy(Seller s, int price);

	/**
	 * Gets the Id attribute of the Bidder object
	 * 
	 * @return The Id value
	 */
	public int getId();
}

/**
 * Description of the Interface
 * 
 * @author judda
 * @created August 29, 2000
 */
interface Seller {

	/**
	 * Gets the Offer attribute of the Seller object
	 * 
	 * @return The Offer value
	 */
	public int getOffer();

	/**
	 * Description of the Method
	 * 
	 * @param b
	 *            Description of Parameter
	 * @param price
	 *            Description of Parameter
	 */
	public void sell(Bidder b, int price);

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public boolean sold();

	/**
	 * Gets the Id attribute of the Seller object
	 * 
	 * @return The Id value
	 */
	public int getId();
}

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
class DockBidder implements Bidder {

	int id;

	int bid, max, price;

	Seller seller;

	static int idcount = 10;

	/**
	 * Constructor for the DockBidder object
	 * 
	 * @param bid
	 *            Description of Parameter
	 * @param max
	 *            Description of Parameter
	 */
	public DockBidder(int bid, int max) {
		id = idcount++;
		this.bid = bid;
		this.max = max;
	}

	/**
	 * Gets the Id attribute of the DockBidder object
	 * 
	 * @return The Id value
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the Bid attribute of the DockBidder object
	 * 
	 * @return The Bid value
	 */
	public int getBid() {
		return bid;
	}

	/**
	 * Gets the MaxBid attribute of the DockBidder object
	 * 
	 * @return The MaxBid value
	 */
	public int getMaxBid() {
		return max;
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 * @param price
	 *            Description of Parameter
	 */
	public void buy(Seller s, int price) {
		seller = s;
		this.price = price;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		String s = "B#" + getId() + "Bidder start=" + bid + " max=" + max;
		if (seller != null) {
			s = s + " final=" + price + "(" + (price - bid) + ","
					+ (max - price) + ") from=S#" + seller.getId();
		} else {
			s = s + " NO DEAL!";
		}
		return s;
	}
}

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
class DockSeller implements Seller {

	int id;

	int offer, price;

	Bidder bidder;

	boolean sold = false;

	static int idcount = 10;

	/**
	 * Constructor for the DockSeller object
	 * 
	 * @param offer
	 *            Description of Parameter
	 */
	public DockSeller(int offer) {
		this.offer = offer;
		id = idcount++;
	}

	/**
	 * Gets the Id attribute of the DockSeller object
	 * 
	 * @return The Id value
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the Offer attribute of the DockSeller object
	 * 
	 * @return The Offer value
	 */
	public int getOffer() {
		return offer;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public boolean sold() {
		return sold;
	}

	/**
	 * Description of the Method
	 * 
	 * @param b
	 *            Description of Parameter
	 * @param price
	 *            Description of Parameter
	 */
	public void sell(Bidder b, int price) {
		bidder = b;
		this.price = price;
		sold = true;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		String s = "S#" + getId() + "Seller offer=" + offer;
		if (bidder != null) {
			s += " final_price=" + price + " (" + (price - offer)
					+ " profit)  from=B#" + bidder.getId();
		} else {
			s += " NO SALE!";
		}
		return s;
	}
}

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
public class Auction {

	Vector bidders, sellers;

	/**
	 * Constructor for the Auction object
	 * 
	 * @param b
	 *            Description of Parameter
	 * @param s
	 *            Description of Parameter
	 */
	public Auction(Vector b, Vector s) {
		bidders = b;
		sellers = s;
	}

	/**
	 * Description of the Method
	 */
	public void begin() {
		// sort Bidders by + MaxBid then by Bid
		Vector s = new Vector();
		while (bidders.size() > 0) {
			Bidder b = (Bidder) bidders.elementAt(0);
			for (int a = 1; a < bidders.size(); a++) {
				Bidder c = (Bidder) bidders.elementAt(a);
				if (c.getMaxBid() > b.getMaxBid()
						|| (c.getMaxBid() == b.getMaxBid() && c.getBid() > b
								.getBid())) {
					b = c;
				}
			}
			s.addElement(b);
			bidders.removeElement(b);
		}
		bidders = s;
		s = new Vector();
		// sort Sellers by - Min
		while (sellers.size() > 0) {
			Seller b = (Seller) sellers.elementAt(0);
			for (int a = 1; a < sellers.size(); a++) {
				Seller c = (Seller) sellers.elementAt(a);
				if (c.getOffer() > b.getOffer()) {
					b = c;
				}
			}
			s.addElement(b);
			sellers.removeElement(b);
		}
		sellers = s;
		doAuction();
	}

	/**
	 * Description of the Method
	 */
	public void doAuction() {
		Vector Lbidders = (Vector) bidders.clone();
		Vector Lsellers = (Vector) sellers.clone();
		for (int a = 0; a < Lsellers.size(); a++) {
			Seller high = (Seller) Lsellers.elementAt(a);
			if (Lbidders.size() == 0) {
				break;
			}
			// no more buyers no sales
			Bidder top = (Bidder) Lbidders.elementAt(0);
			if (high.getOffer() > top.getMaxBid()) {
				continue;
			}
			// no sale too high sell price
			if (Lbidders.size() == 1) {
				int pri = Math.max(top.getBid(), high.getOffer());
				top.buy(high, pri);
				// no body bidding I get at start price
				high.sell(top, pri);
			} else {
				Bidder sec = (Bidder) Lbidders.elementAt(1);
				int pri = Math.max(sec.getMaxBid(), Math.max(top.getBid(), high
						.getOffer()));
				top.buy(high, pri);
				// I get at secode hight price
				high.sell(top, pri);
			}
			Lbidders.removeElementAt(0);
		}
	}

	/**
	 * Description of the Method
	 */
	public void report() {
		System.out.println("Report!");
		for (int a = 0; a < bidders.size(); a++) {
			System.out.println(bidders.elementAt(a));
		}
		for (int a = 0; a < sellers.size(); a++) {
			System.out.println(sellers.elementAt(a));
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		Vector bidders = new Vector();
		Vector sellers = new Vector();
		int a = randInt() / 10;
		while (a > 0) {
			a--;
			int bid = Math.abs(randInt() - randInt() / 2);
			int max = Math.max(bid, randInt());
			DockBidder db = new DockBidder(bid, max);
			bidders.addElement(db);
		}
		a = randInt() / 10;
		while (a > 0) {
			a--;
			int cc = randInt();
			DockSeller ds = new DockSeller(cc);
			sellers.addElement(ds);
		}
		Auction AA = new Auction(bidders, sellers);
		AA.begin();
		AA.report();
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public static int randInt() {
		return (int) (Math.random() * 100);
	}
}

/*
 * min - max - off - Bidder highest max bid first. Pay next lowest max bid
 * price, or sta price if no others lower 3@50 10,30 #3 (10) 20,30 #2 (31) 50,60
 * #1 (31)
 */
