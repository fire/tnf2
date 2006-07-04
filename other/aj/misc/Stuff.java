package aj.misc;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Some tools that are general and may be used later.
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Stuff {

	/**
	 * Returns String[] of all tokens seperated with defalut white space.
	 * default tokens = <spc>, <tab><ret>)("';:
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return The Tokens value
	 */
	public static String[] getTokens(String s) {
		return getTokens(s, " ,\t\n)(\"\';:");
	}

	/**
	 * Returns String[] of all tokens seperated.
	 * 
	 * @param s
	 *            Description of Parameter
	 * @param to
	 *            Description of Parameter
	 * @return The Tokens value
	 */
	public static String[] getTokens(String s, String to) {
		Vector v = new Vector();
		if (s == null) {
			return new String[0];
		}
		StringTokenizer st = new StringTokenizer(s, to);
		while (st.hasMoreTokens()) {
			v.addElement(st.nextToken());
		}
		String[] t = new String[v.size()];
		v.copyInto(t);
		return t;
	}

	public static String engNum(double d) {
		return money(d, 0).substring(1);
	}

	public static String engNum(double d, int t) {
		return money(d, 0).substring(1);
	}

	public static String money(double d) {
		return money(d, 0);
	}

	public static String money(double d, int t) {
		if (Math.abs(trunc(d, 2)) < .01)
			return "$0.0";
		String head = "$";
		if (d < 0) {
			head = "-" + head;
			d *= -1;
		}
		int penney = (int) ((d - (int) (d)) * 100);
		int dol;
		String s = "." + penney + (penney < 10 ? "0" : "");
		while (d >= 1000) {
			dol = (int) (d - (int) (d / 1000) * 1000);
			d = (int) (d / 1000);
			s = "," + (dol < 100 ? "0" : "") + (dol < 10 ? "0" : "") + dol + s;
		}
		dol = (int) d;
		s = dol + s;
		return head + s;
	}

	/**
	 * degree to Radian converter
	 */
	public static double dtr(double d) {
		return d * 3.14159 / 180;
	}

	/**
	 * Radian to degree converter
	 */
	public static double rtd(double d) {
		return d / 3.14159 * 180;
	}

	/**
	 * Remove all substrings in string.
	 * 
	 * @param old
	 *            Description of Parameter
	 * @param sub
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	// MyTODO user indexOf(c,last);
	public static String removeSubstring(String old, String sub) {
		String a = old;
		String b = sub;
		while (a.indexOf(b) >= 0) {
			a = a.substring(0, a.indexOf(b))
					+ a.substring(a.indexOf(b) + b.length());
		}
		return a;
	}

	public static double trunc(double d, int place) {
		double t = Math.pow(10, place);
		if (d > 0)
			d = d * t + .5;
		else if (d < 0)
			d = d * t - .5;
		double x = Math.floor(d);
		if (d < 0) {
			x = Math.ceil(d);
		}
		return x / t;
	}

	/**
	 * This removes multiple white space in a string. First string is source,
	 * second string is white space except " " which is assumed.
	 * 
	 * @param s
	 *            Description of Parameter
	 * @param chars
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static String superTrim(String s, String chars) {
		while (chars.indexOf(" ") >= 0) {
			chars = chars.substring(0, chars.indexOf(" "))
					+ chars.substring(chars.indexOf(" ") + 1);
		}
		for (int a = 0; a < chars.length(); a++) {
			char c = chars.charAt(a);
			String res = "";
			String sub = "";
			int count = 0;
			int lastind = 0;
			while (s.indexOf(c, lastind) >= 0
					&& s.indexOf(c, lastind) >= lastind) {
				int ll = s.indexOf(c, lastind);
				sub += s.substring(lastind, ll) + " ";
				lastind = ll + 1;
				count++;
				if (count > 100) {
					res += sub;
					sub = "";
					count = 0;
				}
			}
			s = res + sub + s.substring(lastind).trim();
		}
		String res = "";
		String sub = "";
		int count = 0;
		int lastind = 0;
		while (s.indexOf("  ", lastind) >= lastind) {
			int ll = s.indexOf("  ", lastind);
			sub += s.substring(lastind, ll);
			lastind = ll + 1;
			count++;
			if (count > 100) {
				res += sub;
				sub = "";
				count = 0;
			}
		}
		res += sub + s.substring(lastind);
		return res.trim();
	}

	/**
	 * Supertrims with default tab and ret.
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static String superTrim(String s) {
		return superTrim(s, "\n\t");
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		double dd = -1118.304;
		System.out.println("penney " + dd + "=" + money(dd, 2));
		dd = -223.606;
		System.out.println("penney " + dd + "=" + money(dd, 2));
		dd = 2500.00;
		System.out.println("penney " + dd + "=" + money(dd, 2));
		dd = 1507.400;
		System.out.println("penney " + dd + "=" + money(dd, 2));
		dd = -1507.400;
		System.out.println("penney " + dd + "=" + money(dd, 2));
		String test = "";
		String spac = " _-:()[]\t";
		// String spac=" \t";
		int count = 1;
		for (int a = 0; a < 10000; a++) {
			int x = (int) (Math.random() * spac.length());
			if (Math.random() * 3 < 1) {
				test += count + count;
				count++;
			}
			test += spac.substring(x, x + 1);
		}
		System.out.println("superTrim test len=" + test.length());
		long ti = System.currentTimeMillis();
		String test2 = superTrim(test, spac);
		System.out.println("result1 len=" + test2.length() + " in "
				+ (System.currentTimeMillis() - ti) + "secs");
		if (test.length() < 200)
			System.out.println("pretrim =" + test);
		if (test.length() < 200)
			System.out.println("posttrim=" + test2);
		ti = System.currentTimeMillis();
		// test2=superTrim2(test,spac);
		// System.out.println("result2 len="+test2.length()+" in
		// "+(System.currentTimeMillis()-ti) + "secs");
		// if (test.length()<200) System.out.println("test="+test);
		// if (test.length()<200) System.out.println("test2="+test2);
		String bad = "bad", good = "good";
		for (int a = 0; a < 500; a++) {
			int x = (int) (Math.random() * 3);
			if (x < 1) {
				test += good;
			} else if (x < 2) {
				test += bad;
			}
		}
		double d = 1.002;
		System.out.println("trunc " + d + "=" + Stuff.trunc(d, 3));
		d = 1.001;
		System.out.println("trunc " + d + "=" + Stuff.trunc(d, 3));
		d = 0.999;
		System.out.println("trunc " + d + "=" + Stuff.trunc(d, 3));
		d = -0.99899;
		System.out.println("trunc " + d + "=" + Stuff.trunc(d, 3));
	}

	public static double parseDouble(String s) throws NumberFormatException {
		return new Double(s).doubleValue();
	}

}
