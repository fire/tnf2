package aj.neaconing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Description of the Class
 * 
 * @author judda
 * @created January 10, 2001
 */
public class DiffTree {
	Vector allLines = new Vector();

	Vector allFinger = new Vector();

	static String file = "nmap-os-fingerprints";

	/**
	 * Constructor for the DiffTree object
	 */
	public DiffTree() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (true) {
				String l = br.readLine();
				if (l == null) {
					break;
				}
				if (l.trim().startsWith("#")) {
					continue;
				}
				if (l.trim().equals("")) {
					continue;
				}
				allLines.addElement(l);
			}
		} catch (IOException IOE) {
		}
		FingerPrint f = null;
		for (int a = 0; a < allLines.size(); a++) {
			String l = (String) allLines.elementAt(a);
			if (l.toUpperCase().startsWith("FINGERPRINT")) {
				if (f != null) {
					allFinger.addElement(f);
				}
				f = new FingerPrint();
				f.addLine(l);
			} else if (f != null) {
				f.addLine(l);
			} else {
				System.out.println("MyError: unhandeled text on line " + a
						+ " " + l);
			}
		}
		if (f != null) {
			allFinger.addElement(f);
		}
	}

	public void showAllFinger() {
		for (int a = 0; a < allFinger.size(); a++) {
			FingerPrint fp = (FingerPrint) allFinger.elementAt(a);
			System.out.println("FingerPrint found " + fp);
		}
	}

	public void compair(String s) {
		FingerPrint f = null;
		for (int a = 0; a < allFinger.size(); a++) {
			FingerPrint g = (FingerPrint) allFinger.elementAt(a);
			if (g.name.equalsIgnoreCase(s)) {
				f = g;
				break;
			}
		}
		int MAXDIFF = 7;
		int MAXSHOWDIFF = 20;
		int diff[] = new int[MAXDIFF];
		if (f == null) {
			System.out.println("Cannot compair. " + s + " not found");
			return;
		}
		// match rate table
		// System.out.println(f.toString());
		// System.out.println(toString());
		for (int a = 0; a < allFinger.size(); a++) {
			FingerPrint g = (FingerPrint) allFinger.elementAt(a);
			diff[Math.min(MAXDIFF - 1, FingerPrint.dist(f, g))]++;
		}
		System.out.println("****" + f.name + " comparision");
		for (int a = 0; a < diff.length; a++)
			System.out.println("total fingers " + a + " different =" + diff[a]);
		// top 10 matches
		for (int a = 0; a < MAXSHOWDIFF; a++) {
			FingerPrint g = (FingerPrint) allFinger.elementAt(0);
			int dd = FingerPrint.dist(f, g);
			for (int b = 1; b < allFinger.size(); b++) {
				FingerPrint h = (FingerPrint) allFinger.elementAt(b);
				int ddd = FingerPrint.dist(f, h);
				if (FingerPrint.dist(f, h) < dd) {
					g = h;
					dd = ddd;
				}
			}
			if (f != g)
				System.out.println(dd + " from " + g.sname + "\n"
						+ FingerPrint.change(f, g));
			allFinger.removeElement(g);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		if (s.length != 0) {
			DiffTree.file = s[0];
		}
		DiffTree d = new DiffTree();
		// d.compair("testing");
		d.compair("Linux Kernel 2.4.0-test5");
		// d.compair("Linux 2.1.122 - 2.2.16");
		// d.compair("FreeBSD 2.2.1 - 4.1");
		// d.compair("OpenBSD 2.6-2.7");
		// d.compair("NetBSD 1.5_ALPHA i386");
		// d.compair("Sun Solaris 8 early acces beta through actual release");
		// d.compair("Windows NT4 / Win95 / Win98");
		// d.compair("HP-UX 10.20");
		// d.compair("woff");
		// d.compair("toff");
		// d.compair("wtoff");
		// where is 2.8? $30USD
		// freeBSD 3.2 (have CD), 4.2 newest ,3.4 $3 cheapbytes
		// redhat 7.0 (2.2.16-22) & (2.4.0), 6.1 have CD
		// netBSD 1.5 (latest) $7.5-$16.50
		// openBSD 2.4 $0 cheapbytes, 2.8 $8.80

	}
}
