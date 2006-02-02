/*
gmlfile [
 A "<A>b<A>"
 A "B"
]

*/
package aj.misc;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Bnf {
	GmlPair g;
	
	public static void main (String s[]) {
		if (s.length == 0) {
			System.out.println ("FORMAT: java aj.testing.Bnf \"string\" <bnf_file> [<seed>]");
			System.exit (0);
		}
		GmlPair g = null;
		try {
			System.out.println ("parseing Gmlfile " + s[1]);
			g = GmlPair.parse (new File (s[1]));
			System.out.println ("Gmlfile okay");
		}
		catch (IOException IOE) {
			System.out.println ("MyError: bad gml file");
			System.exit (0);
		}
		Bnf b = new Bnf (g);
		if (s.length == 3) {
			int r = (int) (Math.random() * Integer.MAX_VALUE);
			try {
				r = Integer.parseInt (s[2]);
			}
			catch (NumberFormatException nfe) {
				System.out.println ("bad seed using random: " + r);
			}
			Random R = new Random (r);
			System.out.println ("Starting with:" + s[0] + " and fixed random seed");
			System.out.println ("Result:" + b.process (s[0], R));
		}
		else {
			System.out.println ("Starting with:" + s[0]);
			System.out.println ("Result:" + b.process (s[0]));
		}
	}
	
	public Bnf (GmlPair gg) {
		g = gg;
	}
	
	public String process (String s) {
		while (s.indexOf ("<") >= 0 && s.lastIndexOf (">") > s.indexOf ("<")) {
			String sub = s.substring (s.indexOf ("<") + 1, s.indexOf ("<") + 1 + s.substring (s.indexOf ("<") + 1).indexOf (">"));
			String rep = findRep (sub);
			s = s.substring (0, s.indexOf ("<")) + rep + s.substring (s.indexOf ("<") + 2 + sub.length());
		}
		return s;
	}
	
	public String process (String s, Random R) {
		while (s.indexOf ("<") >= 0 && s.lastIndexOf (">") > s.indexOf ("<")) {
			String sub = s.substring (s.indexOf ("<") + 1, s.indexOf ("<") + 1 + s.substring (s.indexOf ("<") + 1).indexOf (">"));
			String rep = findRep (sub, R);
			s = s.substring (0, s.indexOf ("<")) + rep + s.substring (s.indexOf ("<") + 2 + sub.length());
		}
		return s;
	}
	String findRep (String sub) {
		GmlPair n[] = g.getAllByName (sub);
		if (n.length == 0)return sub;
		return n[ (int) (Math.random() * n.length)].getString();
	}
	String findRep (String sub, Random R) {
		GmlPair n[] = g.getAllByName (sub);
		if (n.length == 0)return sub;
		return n[ (int) (R.nextDouble() * n.length)].getString();
	}
}

