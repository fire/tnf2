package aj.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class HTMLPrinter {

	static String nott[] = {"HR", "BR", "P", "FRAME", "IMG", "BASE", "META", "LI", "DD", "PARAM", "AREA"};


	//specal case
	public static boolean check(String s) {
		if (s.startsWith("-")) {
			return true;
		}
		if (s.startsWith("!")) {
			return true;
		}
		if (s.startsWith("/")) {
			return true;
		}
		int a;
		for (a = 0; a < nott.length; a++) {
			if (s.toUpperCase().equals(nott[a])) {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ind   Description of Parameter 
	 *@param  stop  Description of Parameter 
	 *@param  all   Description of Parameter 
	 *@return       Description of the Returned Value 
	 */
	public static String format(String ind, String stop, String all) {
		String bak = all;

		while (all.length() > 0) {
			if (all.indexOf("<") < 0) {
				return all;
			}
			if (all.indexOf(">") < all.indexOf("<")) {
				return all;
			}

			//print value between
			String t = ind + all.substring(0, all.indexOf("<"));
			if (!t.trim().equals("")) {
				while (t.length() > 60) {
					String sub = t.substring(0, 60);
					t = t.substring(60);

					if (t.indexOf(" ") > 0) {
						sub += t.substring(0, t.indexOf(" "));
						t = t.substring(t.indexOf(" ") + 1);
					}
					else if (t.indexOf(" ") < 0) {
						sub += t;
						t = "";
					}
					System.out.println(sub);
				}
				System.out.println(t);
			}
			all = all.substring(all.indexOf("<") + 1);

			//get token
			String fulltoke = all.substring(0, all.indexOf(">")).trim();

			if (fulltoke.startsWith("!--") && all.indexOf("-->") > 0) {
				fulltoke = all.substring(0, all.indexOf("-->") + 2);
				all = all.substring(all.indexOf("-->") + 3);
				//cut remark toke
			}
			else if (fulltoke.indexOf("<") < fulltoke.indexOf(">")) {
				System.out.println("MyError: nested < <>> at " + all.substring(60));
				all = all.substring(all.indexOf(">") + 1);
				return null;
			}
			else {
				//cut normal toke
				all = all.substring(all.indexOf(">") + 1);
			}

			String toke = Stuff.superTrim(fulltoke).trim().toUpperCase();
			if (toke.indexOf(" ") > 0) {
				toke = toke.substring(0, toke.indexOf(" ")).toUpperCase().trim();
			}
			fulltoke = fulltoke.substring(0, fulltoke.toUpperCase().indexOf(toke.toUpperCase())) + toke + fulltoke.substring(fulltoke.toUpperCase().indexOf(toke.toUpperCase()) + toke.length());

			if (toke.toUpperCase().equals("/" + stop.toUpperCase())) {
				return all;
			}
			else if (check(toke.toUpperCase()) || toke.startsWith("!")) {
				System.out.println(ind + "<" + fulltoke + ">");
			}
			else {
				System.out.println(ind + "<" + fulltoke + ">");
				all = format(ind + "  ", toke, all);
				System.out.println(ind + "</" + toke + ">");
			}
		}
		System.out.println("MYERROR: </" + stop + "> not found at " + Stuff.superTrim(bak.substring(0, Math.min(bak.length(), 60))));
		return all;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		if (s.length!=0) {
			System.out.println("Usage: java aj.misc.HTMLPrinter <infile >outfile");
			System.exit(0);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String all = "";
		try {
			while (true) {
				String S = br.readLine();
				if (S == null) {
					break;
				}
				all += S + "\n";
			}
		}
		catch (IOException E) {
		}

		all = Stuff.superTrim(all);
		if (all.startsWith("<") && all.indexOf(">") > 0) {
			all = all.substring(1).trim();
			String toke = all.substring(0, all.indexOf(">")).trim().toUpperCase();
			all = all.substring(all.indexOf(">") + 1);

			System.out.println("<" + toke + ">");
			HTMLPrinter.format("  ", toke, all);
			System.out.println("</" + toke + ">");
		}

	}

}
