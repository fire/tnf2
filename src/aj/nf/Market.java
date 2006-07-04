package aj.nf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * 
 * @author judda
 * @created July 21, 2000
 */

public class Market {

	static double SELLADJUST = .95;// sell at 95% of market value

	static Vector allItems = new Vector();

	public void restart() {
		for (int a = 0; a < allItems.size(); a++) {
			MarketItem mi = (MarketItem) allItems.elementAt(a);
			if (mi.amt > 0)
				mi.amt = (int) (500 + Math.random() * 200);
			if (mi.goalSupply > 0)
				mi.goalSupply = 500;
			mi.autoPrice();
			mi.adjust();
			mi.adjust();
			mi.adjust();
			mi.adjust();
		}
	}

	public double getValue(String n) {
		MarketItem mi = getItemByName(n);
		if (mi == null) {
			return 0;
		} else {
			return mi.getPrice();
		}
	}

	public String getOwnReport(Vector v) {
		String s = "\n--------Market Report Own--------\n";
		s += "Name                   \tSupply \tPrice \tChange \n";
		double vt = 0;
		int num = 0;
		for (int a = 0; a < allItems.size(); a++) {
			MarketItem mi = (MarketItem) allItems.elementAt(a);
			for (int b = 0; b < v.size(); b++) {
				String mm = (String) v.elementAt(b);
				if (mm.equalsIgnoreCase(mi.name)) {
					// if(mi.getAmount() <= 0)continue;
					s += mi.toDisplay() + "\n";
					vt += mi.getPrice();
					num++;
					break;
				}
			}
		}
		s += "\nSummary    listed=" + num + "  Average price="
				+ Stuff.trunc(vt / num, 2) + "\n";
		return s;
	}

	public String getActiveReport() {
		String s = "\n--------Market Report Active--------\n";
		s += "Name                   \tSupply \tPrice \tChange \n";
		double vt = 0;
		int num = 0;
		for (int a = 0; a < allItems.size(); a++) {
			MarketItem mi = (MarketItem) allItems.elementAt(a);
			if (Math.abs(mi.change) > 0.01) {
				s += mi.toDisplay() + "\n";
				vt += mi.getPrice();
				num++;
			}
		}
		s += "\nSummary    listed=" + num + "  Average price="
				+ Stuff.trunc(vt / num, 2) + "\n";
		return s;
	}

	public String getFullReport() {
		String s = "\n--------Market Report All--------\n";
		s += "Name                   \tSupply \tPrice \tChange \n";
		double vt = 0;
		for (int a = 0; a < allItems.size(); a++) {
			MarketItem mi = (MarketItem) allItems.elementAt(a);
			s += mi.toDisplay() + "\n";
			vt += mi.getPrice();
		}
		s += "\nSummary    listed=" + allItems.size() + "  Average price="
				+ Stuff.trunc(vt / allItems.size(), 2) + "\n";
		return s;
	}

	// string name of recources choose
	public Vector getRandomRecources() {
		Vector v = new Vector();
		for (int a = 0; a < allItems.size(); a++) {
			MarketItem mi = (MarketItem) allItems.elementAt(a);
			if (mi.nativeElement && mi.occuranceRate != 0
					&& mi.occuranceRate > Math.random()) {
				v.addElement(mi.getName());
			}
		}
		return v;
	}

	public MarketItem getItemByName(String n) {
		if (n == null || n.length() == 0)
			return null;
		while (n.indexOf("\"") >= 0)
			n = n.substring(0, n.indexOf("\""))
					+ n.substring(n.indexOf("\"") + 1);
		for (int a = 0; a < allItems.size(); a++) {
			MarketItem mi = (MarketItem) allItems.elementAt(a);
			if (mi.getName().equalsIgnoreCase(n)
					|| mi.getId().equalsIgnoreCase(n)) {
				return mi;
			}
		}
		return null;
	}

	public void load() {
		try {
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA
					+ Main.marketFileName));
			GmlPair mi[] = g.getAllByName("marketItem");
			for (int a = 0; a < mi.length; a++) {
				MarketItem am = MarketItem.parse(mi[a]);
				if (am != null) {
					allItems.addElement(am);
				}
			}
		} catch (IOException IOE) {
			System.out.println("MyError: Market load error");
			System.exit(0);
		}
	}

	public double buy(String n, int amt) {
		MarketItem mi = getItemByName(n);
		if (mi == null) {
			return 0;
		} else {
			mi.buy(amt);
		}
		return mi.getPrice() * amt;
	}

	public double sell(StockPile s) {
		return sell(s.getMaterial(), s.getAmount());
	}

	public double sell(String n, int amt) {
		MarketItem mi = getItemByName(n);
		if (mi == null) {
			return 0;
		} else {
			mi.sell(amt);
		}
		return mi.getPrice() * amt * SELLADJUST;
	}

	public int available(String n) {
		MarketItem mi = getItemByName(n);
		if (mi == null) {
			return 0;
		} else {
			return mi.getAmount();
		}
	}

	public boolean available(String n, int amt) {
		MarketItem mi = getItemByName(n);
		if (mi == null) {
			return false;
		} else {
			return mi.getAmount() >= amt;
		}
	}

	public void adjust() {
		for (int a = 0; a < allItems.size(); a++) {
			MarketItem mi = (MarketItem) allItems.elementAt(a);
			mi.adjust();
		}
	}

	public void save() {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(Main.DIRDATA
					+ Main.marketFileName));

			// pw.println("all [");
			// for(int a = 0; a < allItems.size(); a++) {
			// pw.println(allItems.elementAt(a));
			// }
			// pw.println("]");

			Vector v = new Vector();
			for (int a = 0; a < allItems.size(); a++) {
				v.addElement(((MarketItem) allItems.elementAt(a)).toGmlPair());
			}
			GmlPair g = new GmlPair("all", v);
			pw.println(g.prettyPrint());

			pw.flush();
			pw.close();
		} catch (IOException IOE) {
			System.out.println("MyError: Market Save error");
			System.exit(0);
		}
	}

	public String toString() {
		String s = "Market Standing " + allItems.size() + " items listed\n";
		for (int a = 0; a < allItems.size(); a++) {
			s += ((MarketItem) allItems.elementAt(a)).toDisplay() + "\n";
		}
		return s;
	}

	public static Vector getAllItems() {
		return allItems;
	}

	public static String getMarketName(String n) {
		if (n == null || n.length() == 0)
			return null;
		n = n.trim();
		for (int a = 0; a < allItems.size(); a++) {
			MarketItem mi = (MarketItem) allItems.elementAt(a);
			if (mi.getName().equalsIgnoreCase(n)
					|| mi.getId().equalsIgnoreCase(n)) {
				return mi.getName();
			}
		}
		return null;
	}

	public static void main(String s[]) {
		Market m = new Market();
		m.load();
		System.out.println(m);
		System.out.println(m);
		m.save();
	}
}
