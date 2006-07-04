package aj.smug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import aj.misc.GmlPair;

public class SmugMain {
	static String gameEmail = "judda@neaconing1.spawar.navy.mil";

	public static void main(String s[]) {
		if (s.length > 0 && s[0].toUpperCase().indexOf("EXEC") >= 0) {
			System.out.println("Exec mode");
			SmugMain m = new SmugMain();
			m.executeTurn();
		} else if (s.length > 0 && s[0].toUpperCase().indexOf("MAIL") >= 0) {
			System.out.println("Mail mode");
			SmugMain m = new SmugMain();
			m.receiveMail();
		} else {
			System.out
					.println("Format: java aj.smug.Main <execturn|exec|mail|procmail>");
			System.exit(0);
		}
	}

	Vector players = new Vector();

	Vector planets = new Vector();

	Vector products = new Vector();

	Vector items = new Vector();

	Vector skills = new Vector();

	private static String DATAFILE = "data.gml";

	public void executeTurn() {
		loadData();
		loadOrders();
		execOrders();
		saveData();
		saveReports();
		// mailReports();
	}

	public void loadData() {
		System.out.println("loading data");
		try {
			GmlPair g = GmlPair.parse(new File(DATAFILE));
			GmlPair n[] = g.getAllByName("Player");
			for (int a = 0; a < n.length; a++) {
				Player p = Player.parse(n[a]);
				if (p != null)
					players.addElement(p);
				else {
					System.out.println("MyError: bad player found");
				}
			}
			n = g.getAllByName("Planet");
			for (int a = 0; a < n.length; a++) {
				Planet p = Planet.parse(n[a]);
				if (p != null)
					planets.addElement(p);
				else {
					System.out.println("MyError: bad planet found");
				}
			}
			n = g.getAllByName("Product");
			for (int a = 0; a < n.length; a++) {
				Product p = Product.parse(n[a]);
				if (p != null)
					products.addElement(p);
				else {
					System.out.println("MyError: bad product found");
				}
			}
			n = g.getAllByName("Item");
			for (int a = 0; a < n.length; a++) {
				Item i = Item.parse(n[a]);
				if (i != null)
					items.addElement(i);
				else {
					System.out.println("MyError: bad item found");
				}
			}
			n = g.getAllByName("Skill");
			for (int a = 0; a < n.length; a++) {
				Skill sk = Skill.parse(n[a]);
				if (sk != null)
					skills.addElement(sk);
				else {
					System.out.println("MyError: bad skill found");
				}
			}
		} catch (IOException IEO) {
			System.out.println("MyError: Cannot load data file Error in data");
			System.exit(0);
		}
	}

	public void saveData() {
		System.out.println("saving data");
		Vector v = new Vector();
		for (int a = 0; a < players.size(); a++) {
			v.addElement(((Player) players.elementAt(a)).toGml());
		}
		for (int a = 0; a < planets.size(); a++) {
			v.addElement(((Planet) planets.elementAt(a)).toGml());
		}
		for (int a = 0; a < products.size(); a++) {
			v.addElement(((Product) products.elementAt(a)).toGml());
		}
		for (int a = 0; a < items.size(); a++) {
			v.addElement(((Item) items.elementAt(a)).toGml());
		}
		for (int a = 0; a < skills.size(); a++) {
			Skill sk = (Skill) skills.elementAt(a);
			GmlPair g = sk.toGml();
			v.addElement(g);
		}
		GmlPair g = new GmlPair("Smug", v);
		// System.out.println(g+"");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(DATAFILE));
			String pp = g.prettyPrint("");
			while (pp.indexOf("\n") >= 0) {
				pw.println(pp.substring(0, pp.indexOf("\n")));
				pp = pp.substring(pp.indexOf("\n") + 1);
			}
			pw.println(pp);
			pw.flush();
			pw.close();
		} catch (IOException IEO) {
			System.out.println("MyError: Cannot save data file.");
			System.exit(0);
		}
	}

	public void loadOrders() {
		System.out.println("loading orders");
		for (int a = 0; a < players.size(); a++) {
			Player p = (Player) players.elementAt(a);
			p.readOrders();
		}
	}

	public void execOrders() {
		System.out.println("executing orders");
		boolean done = false;
		while (!done) {
			done = true;
			for (int a = 0; a < players.size(); a++) {
				Player p = (Player) players.elementAt(a);
				p.execOrder(this);
				if (!p.doneOrders())
					done = false;
			}
		}
	}

	public void saveReports() {
		System.out.println("saving reports");
		for (int a = 0; a < players.size(); a++) {
			Player p = (Player) players.elementAt(a);
			p.saveReport();
		}
	}

	public void mailReports() {
		System.out.println("mailing reports");
		for (int a = 0; a < players.size(); a++) {
			Player p = (Player) players.elementAt(a);
			p.mailReport();
		}
	}

	public void receiveMail() {
		loadData();
		System.out.println("receiving mail");
	}

	public void saveOrders() {
		System.out.println("saving orders");
	}

	public void mailReceipt() {
		System.out.println("mailing receipt");
	}

	public Planet getPlanetAtLoc(String s) {
		s = s.trim();
		if (s.toUpperCase().endsWith("S"))
			s = s.substring(0, s.length() - 2);
		if (s.toUpperCase().endsWith("O"))
			s = s.substring(0, s.length() - 2);
		for (int a = 0; a < planets.size(); a++) {
			Planet p = (Planet) planets.elementAt(a);
			if (s.trim().equals(p.loc.trim()))
				return p;
		}
		return null;
	}

	public Planet getPlanetByName(String s) {
		for (int a = 0; a < planets.size(); a++) {
			Planet p = (Planet) planets.elementAt(a);
			if (s.equals(p.name))
				return p;
		}
		return null;
	}

	public Item getRandomItem() {
		while (true) {
			Item i = (Item) items
					.elementAt((int) (items.size() * Math.random()));
			if (i.stealable())
				return i;
		}
	}

	public Item getItemByName(String s) {
		for (int a = 0; a < items.size(); a++) {
			Item i = (Item) items.elementAt(a);
			if (s.equals(i.name))
				return i;
		}
		return null;
	}

	public Product getProductByName(String s) {
		for (int a = 0; a < products.size(); a++) {
			Product p = (Product) products.elementAt(a);
			if (s.equalsIgnoreCase(p.name))
				return p;
		}
		for (int a = 0; a < products.size(); a++) {
			Product p = (Product) products.elementAt(a);
			if (s.equalsIgnoreCase(p.nick))
				return p;
		}
		return null;
	}

	Vector illc = null, lc = null;

	public Vector getIllegalCargo() {
		if (illc == null) {
			illc = new Vector();
			lc = new Vector();
			for (int a = 0; a < products.size(); a++) {
				Product p = (Product) products.elementAt(a);
				if (!p.legal)
					illc.addElement(p);
				else
					lc.addElement(p);
			}
		}
		return illc;
	}

	public Vector getLegalCargo() {
		if (lc == null)
			getIllegalCargo();
		return lc;
	}
}
