package aj.nf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author judda
 * @created July 21, 2000
 */
public class UniVar {
	public String sval;

	public static boolean DEBUG = false;// true;

	public UniVar(String s) {
		while (s.indexOf("\"") >= 0) {
			s = s.substring(0, s.indexOf("\""))
					+ s.substring(s.indexOf("\"") + 1);
		}
		s = s.trim();
		sval = s;
	}

	public String toString() {
		// return "\"" + sval + "\"";
		try {
			double d = Stuff.parseDouble(sval);
			if ((d + "").toUpperCase().indexOf("E") > 0)
				return "\"" + sval + "\"";
			else if (((long) d) == d)
				return "" + ((long) d);
			else
				return "" + d;
		} catch (NumberFormatException NFE) {
			if (sval.length() > 0 && sval.indexOf(" ") < 0
					&& sval.indexOf("\t") < 0 && sval.indexOf("_") < 0
					&& sval.indexOf("\n") < 0 && sval.indexOf(".") < 0
					&& sval.indexOf(",") < 0
					&& !Character.isDigit(sval.charAt(0))
					&& sval.indexOf("-") < 0 && sval.indexOf(">") < 0
					&& sval.indexOf("<") < 0 && sval.indexOf(":") < 0)
				return sval;
			else
				return "\"" + sval + "\"";
		}

	}

	public UniVar and(UniVar s) {
		boolean b1 = true;
		boolean b2 = true;
		try {
			b1 = Stuff.parseDouble(s.sval) != 0;
		} catch (NumberFormatException NFE) {
		}
		try {
			b2 = Stuff.parseDouble(sval) != 0;
		} catch (NumberFormatException NFE2) {
		}
		return new UniVar("" + (b1 && b2 ? 1 : 0));
	}

	public UniVar or(UniVar s) {
		boolean b1 = true;
		boolean b2 = true;
		try {
			b1 = Stuff.parseDouble(s.sval) != 0;
		} catch (NumberFormatException NFE) {
		}
		try {
			b2 = Stuff.parseDouble(sval) != 0;
		} catch (NumberFormatException NFE2) {
		}
		return new UniVar("" + (b1 || b2 ? 1 : 0));
	}

	public boolean isNumber() {
		if (sval.equals(""))
			return false;
		try {
			Stuff.parseDouble(sval);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public UniVar myEquals(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + (d1 == d2 ? 1 : 0));
		} catch (NumberFormatException NFE) {
		}
		return new UniVar("" + (s.sval.equals(sval) ? 1 : 0));
	}

	public UniVar notEquals(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + (d1 != d2 ? 1 : 0));
		} catch (NumberFormatException NFE) {
		}
		return new UniVar("" + (!s.sval.equals(sval) ? 1 : 0));
	}

	public UniVar greaterThanOrEquals(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + (d2 >= d1 ? 1 : 0));
		} catch (NumberFormatException NFE) {
		}
		return new UniVar("" + (s.sval.compareTo(sval) <= 0 ? 1 : 0));
	}

	public UniVar lessThanOrEquals(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + (d2 <= d1 ? 1 : 0));
		} catch (NumberFormatException NFE) {
		}
		return new UniVar("" + (s.sval.compareTo(sval) >= 0 ? 1 : 0));
	}

	public UniVar greaterThan(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + (d2 > d1 ? 1 : 0));
		} catch (NumberFormatException NFE) {
		}
		return new UniVar("" + (s.sval.compareTo(sval) < 0 ? 1 : 0));
	}

	public UniVar lessThan(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + (d2 < d1 ? 1 : 0));
		} catch (NumberFormatException NFE) {
		}
		return new UniVar("" + (s.sval.compareTo(sval) > 0 ? 1 : 0));
	}

	public UniVar add(UniVar s) {
		try {
			double d1;
			double d2;
			if (s.sval.equals("")) {
				d1 = 0;
			} else {
				d1 = Stuff.parseDouble(s.sval);
			}
			if (sval.equals("")) {
				d2 = 0;
			} else {
				d2 = Stuff.parseDouble(sval);
			}
			return new UniVar("" + (d1 + d2));
		} catch (NumberFormatException NFE) {
			return new UniVar("Error1");
		}
	}

	public UniVar sub(UniVar s) {
		try {
			double d1;
			double d2;
			if (s.sval.equals("")) {
				d1 = 0;
			} else {
				d1 = Stuff.parseDouble(s.sval);
			}
			if (sval.equals("")) {
				d2 = 0;
			} else {
				d2 = Stuff.parseDouble(sval);
			}
			return new UniVar("" + (d2 - d1));
		} catch (NumberFormatException NFE) {
			return new UniVar("Error1");
		}
	}

	public UniVar len() {
		String s = toString();
		while (s.indexOf("\"") >= 0) {
			s = s.substring(0, s.indexOf("\""))
					+ s.substring(s.indexOf("\"") + 1);
		}
		return new UniVar(Math.max(0, s.length()) + "");
	}

	public UniVar indexOf(UniVar s) {
		String s1;
		String s2;
		s2 = s.sval;
		s1 = sval;
		return new UniVar(s1.indexOf(s2) + "");
	}

	public UniVar unsubstring(UniVar s) {
		try {
			int d1;
			d1 = (int) Stuff.parseDouble(s.sval);
			String s1 = sval;
			if (s1.length() < Math.abs(d1)) {
				return new UniVar("Error2");
			}
			if (d1 > 0) {
				return new UniVar(s1.substring(0, d1));
			} else {
				return new UniVar(s1.substring(s1.length() + d1));// d1 is
				// negtive
			}
		} catch (NumberFormatException NFE) {
			return new UniVar("Error3");
		}
	}

	public UniVar substring(UniVar s) {
		try {
			int d1;
			d1 = (int) Stuff.parseDouble(s.sval);
			String s1 = sval;
			if (s1.length() < Math.abs(d1)) {
				return new UniVar("Error2");
			}
			if (d1 > 0) {
				return new UniVar(s1.substring(d1));
			} else {
				return new UniVar(s1.substring(0, s1.length() + d1));// note
				// d1 is
				// negitave
			}
		} catch (NumberFormatException NFE) {
			return new UniVar("Error3");
		}
	}

	public UniVar mul(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + (d1 * d2));
		} catch (NumberFormatException NFE) {
			return new UniVar("Error4");
		}
	}

	public UniVar pow(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + Math.pow(d2, d1));
		} catch (NumberFormatException NFE) {
			return new UniVar("Error5");
		}
	}

	public UniVar div(UniVar s) {
		try {
			double d1;
			double d2;
			d1 = Stuff.parseDouble(s.sval);
			d2 = Stuff.parseDouble(sval);
			return new UniVar("" + (d2 / d1));
		} catch (NumberFormatException NFE) {
			return new UniVar("Error6");
		}
	}

	public UniVar append(UniVar s) {
		String m = s.sval;
		return new UniVar(sval + m);
	}

	public static void main(String s[]) {
		for (int a = 0; a < s.length; a++) {
			System.out.println("get value of <" + s[a] + "> "
					+ getValue(s[a]).toString());
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			while (true) {
				String S = br.readLine();
				if (S == null) {
					break;
				}
				System.out.println("start work on <" + S + ">");
				String sss = getValue(S).toString();
				System.out.println("get value of (" + S + ") = " + sss);
			}
		} catch (IOException IOE) {
		}
	}

	public static String tokenBefore(String e, int i) {
		if (i <= 0) {
			return "";
		}
		String ans = "";
		String t = e.substring(0, i);
		if (t.trim().endsWith("\"")) {
			String s = t.substring(0, t.lastIndexOf("\""));
			if (s.indexOf("\"") >= 0) {
				ans = t.substring(s.lastIndexOf("\""));
				// System.out.println("token before "+e.substring(i,i+1)+" in
				// ("+e+") is ("+ans+")");
				return ans;
			} else {
				ans = tokenBefore(e, s.length()) + e.substring(s.length(), i);
				// System.out.println("token before "+e.substring(i,i+1)+" in
				// ("+e+") is ("+ans+")");
				return ans;
			}
		} else {
			int start = i - 1;
			while ((e.charAt(start) == ' ' || e.charAt(start) == '\t')
					&& start > 0) {
				start--;
			}
			while (start > 0
					&& ((e.charAt(start) >= 'a' && e.charAt(start) <= 'z')
							|| (e.charAt(start) >= 'A' && e.charAt(start) <= 'Z')
							|| (e.charAt(start) >= '0' && e.charAt(start) <= '9')
							|| e.charAt(start) == '.' || e.charAt(start) == '`'
							|| e.charAt(start) == '~' || e.charAt(start) == ':'
							|| e.charAt(start) == ';' || e.charAt(start) == '['
							|| e.charAt(start) == ']' || e.charAt(start) == '{'
							|| e.charAt(start) == '}' || e.charAt(start) == ','
							|| (e.charAt(start) == '-' && i - 1 != start)
							|| e.charAt(start) == '-' || e.charAt(start) == '_')) {
				// if (e.charAt(start)=='-') {start--;break;}
				start--;
			}
			if (start != 0) {
				start++;
			}
			ans = e.substring(start, i);
			// System.out.println("token before "+e.substring(i,i+1)+" in
			// ("+e+") is ("+ans+")");
			return ans;
		}
	}

	public static String tokenAfter(String e, int i) {
		String ans = "";
		int start = i + 1;
		if (e.length() < start) {
			return "";
		}
		String t = e.substring(start);
		if (t.trim().startsWith("\"")) {
			String s = t.substring(t.indexOf("\"") + 1);
			if (s.indexOf("\"") >= 0) {
				ans = e.substring(start, start + s.indexOf("\"") + 1
						+ t.indexOf("\"") + 1);
				// System.out.println("token after "+e.substring(i,i+1)+" in
				// ("+e+") is ("+ans+")");
				return ans;
			} else {
				ans = tokenAfter(e, start
						+ t.substring(t.indexOf("\"") + 1).length());
				// System.out.println("token after "+e.substring(i,i+1)+" in
				// ("+e+") is ("+ans+")");
				return ans;
			}
		} else {
			int end = start;
			if (end >= e.length()) {
				return "";
			}
			while ((e.charAt(end) == ' ' || e.charAt(end) == '\t')
					&& end < e.length()) {
				end++;
			}
			while (end < e.length()
					&& ((e.charAt(end) >= 'a' && e.charAt(end) <= 'z')
							|| (e.charAt(end) >= 'A' && e.charAt(end) <= 'Z')
							|| (e.charAt(end) >= '0' && e.charAt(end) <= '9')
							|| e.charAt(end) == '.' || e.charAt(end) == '`'
							|| e.charAt(end) == '~' || e.charAt(end) == ':'
							|| e.charAt(end) == ';' || e.charAt(end) == '['
							|| e.charAt(end) == ']' || e.charAt(end) == '{'
							|| e.charAt(end) == '}' || e.charAt(end) == ','
							|| e.charAt(end) == '-' ||
					// only allow - at begin of UniVar
					// (e.charAt(end) == '-' && end==start2) ||
					e.charAt(end) == '_')) {
				end++;
			}
			ans = e.substring(start, end);
			// System.out.println("token after "+e.substring(i,i+1)+" in ("+e+")
			// is ("+ans+")");
			return ans;
		}
	}

	public static String spaceOperators(String eq) {
		if (DEBUG)
			System.out.println("spacing eq=" + eq);
		for (int a = 0; a < eq.length(); a++) {
			if (eq.charAt(a) == '-') {
				String t2 = tokenBefore(eq, a);
				// if (DEBUG) System.out.println("eq="+eq+" toke before="+t2);
				UniVar u2 = new UniVar(t2);
				if (u2.isNumber()) {
					eq = eq.substring(0, a) + "+-" + eq.substring(a + 1);
					a++;
				}
			}
		}
		if (DEBUG)
			System.out.println("spaced eq=" + eq);
		while (eq.indexOf("- ") >= 0) {
			eq = eq.substring(0, eq.indexOf("- ")) + "-"
					+ eq.substring(eq.indexOf("- ") + 2);
		}
		while (eq.indexOf(" -") >= 0) {
			eq = eq.substring(0, eq.indexOf(" -")) + "-"
					+ eq.substring(eq.indexOf(" -") + 2);
		}
		while (eq.indexOf("+ ") >= 0) {
			eq = eq.substring(0, eq.indexOf("+ ")) + "+"
					+ eq.substring(eq.indexOf("+ ") + 2);
		}
		while (eq.indexOf(" +") >= 0) {
			eq = eq.substring(0, eq.indexOf(" +")) + "+"
					+ eq.substring(eq.indexOf(" +") + 2);
		}
		while (eq.indexOf("--") >= 0) {
			eq = eq.substring(0, eq.indexOf("--")) + "+ "
					+ eq.substring(eq.indexOf("--") + 2);
		}
		while (eq.indexOf("-+") >= 0) {
			eq = eq.substring(0, eq.indexOf("-+")) + "+-"
					+ eq.substring(eq.indexOf("-+") + 2);
		}
		while (eq.indexOf("++") >= 0) {
			eq = eq.substring(0, eq.indexOf("++")) + "+"
					+ eq.substring(eq.indexOf("++") + 2);
		}
		if (DEBUG) {
			System.out.println("- - + - +- -- fix/ eq=" + eq);
		}
		return eq;
	}

	static UniVar getValue(String eq) {
		eq = Stuff.superTrim(eq, " ");// eq.trim();
		// paren
		if (DEBUG) {
			System.out.println("eq=" + eq);
		}
		while (eq.indexOf(")") > 0 && eq.indexOf("(") >= 0
				&& eq.indexOf("(") < eq.indexOf(")")) {
			int end = eq.indexOf(")");
			int start = eq.substring(0, end).lastIndexOf("(");
			eq = eq.substring(0, start)
					+ getValue(eq.substring(start + 1, end))
					+ eq.substring(end + 1);
		}
		if (eq.indexOf(")") >= 0 || eq.indexOf("(") >= 0) {
			return new UniVar("Error7");
		}
		if (DEBUG) {
			System.out.println("() eq=" + eq);
		}
		// "##" == "hello" ## = 5
		while (eq.indexOf("##") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("##"));
			int start = eq.indexOf("##") - t1.length();
			int end = eq.indexOf("##") + 1;
			UniVar u = new UniVar(t1);
			u = u.len();
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("## eq=" + eq);
		}
		// "#" == "hello" # 3 = "lo"
		while (eq.indexOf("#") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("#"));
			String t2 = tokenAfter(eq, eq.indexOf("#"));
			int start = eq.indexOf("#") - t1.length();
			int end = eq.indexOf("#") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.indexOf(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("# eq=" + eq);
		}
		// "@" == "hello" @ "lo" = 3
		while (eq.indexOf("@@") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("@@"));
			String t2 = tokenAfter(eq, eq.indexOf("@@") + 1);
			int start = eq.indexOf("@@") - t1.length();
			int end = eq.indexOf("@@") + 1 + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.unsubstring(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("@ eq=" + eq);
		}
		while (eq.indexOf("@") >= 0) {
			// System.out.println("eq=("+eq+")");
			String t1 = tokenBefore(eq, eq.indexOf("@"));
			String t2 = tokenAfter(eq, eq.indexOf("@"));
			int start = eq.indexOf("@") - t1.length();
			int end = eq.indexOf("@") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.substring(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("@ eq=" + eq);
		}
		// ^
		eq = spaceOperators(eq);// must be after all uni operators
		while (eq.indexOf("^") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("^"));
			String t2 = tokenAfter(eq, eq.indexOf("^"));
			int start = eq.indexOf("^") - t1.length();
			int end = eq.indexOf("^") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.pow(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("^ eq=" + eq);
		}
		// *
		while (eq.indexOf("*") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("*"));
			String t2 = tokenAfter(eq, eq.indexOf("*"));
			int start = eq.indexOf("*") - t1.length();
			int end = eq.indexOf("*") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.mul(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("* eq=" + eq);
		}
		// "/"
		while (eq.indexOf("/") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("/"));
			String t2 = tokenAfter(eq, eq.indexOf("/"));
			int start = eq.indexOf("/") - t1.length();
			int end = eq.indexOf("/") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.div(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("/ eq=" + eq);
		}
		while (eq.indexOf("+") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("+"));
			String t2 = tokenAfter(eq, eq.indexOf("+"));
			int start = eq.indexOf("+") - t1.length();
			int end = eq.indexOf("+") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			// System.out.println("before add=("+eq+")"+u+" "+u2);
			u = u.add(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("after add=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("+ eq=" + eq);
		}
		// &&
		while (eq.indexOf("&&") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("&&"));
			String t2 = tokenAfter(eq, eq.indexOf("&&") + 1);
			int start = eq.indexOf("&&") - t1.length();
			int end = eq.indexOf("&&") + 1 + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.and(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("&& eq=" + eq);
		}
		// ||
		while (eq.indexOf("||") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("||"));
			String t2 = tokenAfter(eq, eq.indexOf("||") + 1);
			int start = eq.indexOf("||") - t1.length();
			int end = eq.indexOf("||") + 1 + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.or(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("|| eq=" + eq);
		}
		// "&"
		while (eq.indexOf("&") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("&"));
			String t2 = tokenAfter(eq, eq.indexOf("&"));
			int start = eq.indexOf("&") - t1.length();
			int end = eq.indexOf("&") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.append(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("& eq=" + eq);
		}
		// >=
		while (eq.indexOf(">=") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf(">="));
			String t2 = tokenAfter(eq, eq.indexOf(">=") + 1);
			int start = eq.indexOf(">=") - t1.length();
			int end = eq.indexOf(">=") + 1 + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.greaterThanOrEquals(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println(">= eq=" + eq);
		}
		// <=
		while (eq.indexOf("<=") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("<="));
			String t2 = tokenAfter(eq, eq.indexOf("<=") + 1);
			int start = eq.indexOf("<=") - t1.length();
			int end = eq.indexOf("<=") + 1 + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.lessThanOrEquals(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("<= eq=" + eq);
		}
		// >
		while (eq.indexOf(">") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf(">"));
			String t2 = tokenAfter(eq, eq.indexOf(">"));
			int start = eq.indexOf(">") - t1.length();
			int end = eq.indexOf(">") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.greaterThan(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("> eq=" + eq);
		}
		// <
		while (eq.indexOf("<") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("<"));
			String t2 = tokenAfter(eq, eq.indexOf("<"));
			int start = eq.indexOf("<") - t1.length();
			int end = eq.indexOf("<") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.lessThan(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("< eq=" + eq);
		}
		// !=
		while (eq.indexOf("!=") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("!="));
			String t2 = tokenAfter(eq, eq.indexOf("!=") + 1);
			int start = eq.indexOf("!=") - t1.length();
			int end = eq.indexOf("!=") + 1 + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			u = u.notEquals(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("!= eq=" + eq);
		}
		// ==
		while (eq.indexOf("==") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("=="));
			String t2 = tokenAfter(eq, eq.indexOf("==") + 1);
			int start = eq.indexOf("==") - t1.length();
			int end = eq.indexOf("==") + 1 + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			if (DEBUG) {
				System.out.println("tok1=(" + t1 + ")");
			}
			if (DEBUG) {
				System.out.println("tok2=(" + t2 + ")");
			}
			u = u.myEquals(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("== eq=" + eq);
		}
		while (eq.indexOf("=") >= 0) {
			String t1 = tokenBefore(eq, eq.indexOf("="));
			String t2 = tokenAfter(eq, eq.indexOf("="));
			int start = eq.indexOf("=") - t1.length();
			int end = eq.indexOf("=") + t2.length();
			UniVar u = new UniVar(t1);
			UniVar u2 = new UniVar(t2);
			if (DEBUG) {
				System.out.println("tok1=(" + t1 + ")");
			}
			if (DEBUG) {
				System.out.println("tok2=(" + t2 + ")");
			}
			u = u.myEquals(u2);
			eq = eq.substring(0, start) + u + eq.substring(end + 1);
			// System.out.println("fixed eq=("+eq+")");
		}
		if (DEBUG) {
			System.out.println("= eq=" + eq);
		}
		return new UniVar(eq);
	}

}
