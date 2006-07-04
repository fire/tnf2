package aj.neaconing;

import java.util.Vector;

import aj.misc.Stuff;

/**
 * Description of the Class
 * 
 * @author judda
 * @created January 10, 2001
 */
public class FingerPrint {
	/*
	 * TSeq(Class=C) T1(DF=N%W=400%ACK=S++%Flags=AS%Ops=M)
	 * T2(Resp=Y%DF=N%W=0%ACK=S%Flags=APR%Ops=)
	 * T3(Resp=Y%DF=N%W=400%ACK=S++%Flags=AS%Ops=M)
	 * T4(DF=N%W=0%ACK=S%Flags=APR%Ops=) T5(DF=N%W=0%ACK=S++%Flags=APR%Ops=)
	 * T6(DF=N%W=0%ACK=S%Flags=APR%Ops=) T7(DF=N%W=0%ACK=S%Flags=APR%Ops=)
	 * PU(DF=N%TOS=0%IPLEN=38%RIPTL=148%RID=E%RIPCK=E%UCK=E%ULEN=134%DAT=E)
	 */

	String name = null, sname = null;

	String tseqclass, tseqgcd, tseqsi;

	String resp[] = new String[7], df[] = new String[7], w[] = new String[7],
			ack[] = new String[7], flags[] = new String[7],
			ops[] = new String[7];

	Vector lines = new Vector();

	/**
	 * Adds a feature to the Line attribute of the FingerPrint object
	 * 
	 * @param l
	 *            The feature to be added to the Line attribute
	 */
	public void addLine(String l) {
		lines.addElement(l);
		l = l.trim();
		if (l.indexOf("#") >= 0)
			l = l.substring(0, l.indexOf("#"));
		if (l.toUpperCase().startsWith("TSEQ(")) {
			if (l.toUpperCase().indexOf("CLASS=") >= 0) {
				if (l.toUpperCase().indexOf("CLASS=") >= 0) {
					tseqclass = l
							.substring(l.toUpperCase().indexOf("CLASS=") + 6);
					tseqclass = clean(tseqclass);
				}
				if (l.toUpperCase().indexOf("GCD=") >= 0) {
					tseqgcd = l.substring(l.toUpperCase().indexOf("GCD=") + 4);
					tseqgcd = clean(tseqgcd);
				}
				if (l.toUpperCase().indexOf("SI=") >= 0) {
					tseqsi = l.substring(l.toUpperCase().indexOf("SI=") + 3);
					tseqsi = clean(tseqsi);
				}
			}
		}
		if (l.toUpperCase().startsWith("FINGERPRINT")) {
			name = l.substring(12).trim();
			sname = name;
			// if (sname.length()>20) sname=sname.substring(0,20)+"...";
		}

		int ind = -1;
		if (l.toUpperCase().startsWith("T1("))
			ind = 0;
		if (l.toUpperCase().startsWith("T2("))
			ind = 1;
		if (l.toUpperCase().startsWith("T3("))
			ind = 2;
		if (l.toUpperCase().startsWith("T4("))
			ind = 3;
		if (l.toUpperCase().startsWith("T5("))
			ind = 4;
		if (l.toUpperCase().startsWith("T6("))
			ind = 5;
		if (l.toUpperCase().startsWith("T7("))
			ind = 6;
		if (ind == -1)
			return;

		if (l.toUpperCase().indexOf("RESP=") >= 0) {
			resp[ind] = l.substring(l.toUpperCase().indexOf("RESP=") + 5);
			resp[ind] = clean(resp[ind]);
		}
		if (l.toUpperCase().indexOf("DF=") >= 0) {
			df[ind] = l.substring(l.toUpperCase().indexOf("DF=") + 3);
			df[ind] = clean(df[ind]);
		}
		if (l.toUpperCase().indexOf("W=") >= 0) {
			w[ind] = l.substring(l.toUpperCase().indexOf("W=") + 2);
			w[ind] = clean(w[ind]);
		}
		if (l.toUpperCase().indexOf("ACK=") >= 0) {
			ack[ind] = l.substring(l.toUpperCase().indexOf("ACK=") + 4);
			ack[ind] = clean(ack[ind]);
		}
		if (l.toUpperCase().indexOf("FLAGS=") >= 0) {
			flags[ind] = l.substring(l.toUpperCase().indexOf("FLAGS=") + 6);
			flags[ind] = clean(flags[ind]);
		}
		if (l.toUpperCase().indexOf("OPS=") >= 0) {
			ops[ind] = l.substring(l.toUpperCase().indexOf("OPS=") + 4);
			ops[ind] = clean(ops[ind]);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public String clean(String s) {
		if (s == null)
			return null;
		if (s.indexOf("%") >= 0) {
			s = s.substring(0, s.indexOf("%"));
		}
		if (s.indexOf(")") >= 0) {
			s = s.substring(0, s.indexOf(")"));
		}
		return s;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		String ret = "name=" + name;
		ret += "\ntseqclass=" + tseqclass;
		ret += "\ntseqgcd=" + tseqgcd;
		ret += "\ntseqsi=" + tseqsi;
		for (int a = 0; a < 7; a++)
			ret += "\nT" + a + " resp=" + resp[a] + " df=" + df[a] + " w="
					+ w[a] + " ack=" + ack[a] + " flags=" + flags[a] + " ops="
					+ ops[a];
		return ret;
	}

	public static boolean compat(String tta, String ttb) {
		if (tta.length() == 0 && ttb.length() != 0)
			return false;
		if (ttb.length() == 0 && tta.length() != 0)
			return false;
		if (ttb.length() == 0 && tta.length() == 0)
			return true;
		String t[] = Stuff.getTokens(tta, "|&"), u[] = Stuff.getTokens(ttb,
				"|&");

		try {
			for (int a = 0; a < t.length; a++) {
				for (int b = 0; b < u.length; b++) {
					if (t[a].equals(u[b])) {
						return true;
					}
					int ta = 0, ub = 0;
					boolean talt = false, tagt = false, taeq = false;
					boolean ublt = false, ubgt = false, ubeq = false;
					while (t[a].startsWith("<") || t[a].startsWith("=")
							|| t[a].startsWith(">")) {
						if (t[a].startsWith("<"))
							talt = true;
						if (t[a].startsWith("="))
							taeq = true;
						if (t[a].startsWith(">"))
							tagt = true;
						t[a] = t[a].substring(1);
					}
					while (u[b].startsWith("<") || u[b].startsWith("=")
							|| u[b].startsWith(">")) {
						if (u[b].startsWith("<"))
							ublt = true;
						if (u[b].startsWith("="))
							ubeq = true;
						if (u[b].startsWith(">"))
							ubgt = true;
						u[b] = u[b].substring(1);
					}
					try {
						ta = Integer.parseInt(t[a]);
						ub = Integer.parseInt(u[b]);
						if (ta < ub && (tagt || ublt))
							return true;
						if (ta > ub && (talt || ubgt))
							return true;
						if (ta == ub && ubeq && taeq)
							return true;
					} catch (NumberFormatException NfE) {
					}
					try {
						ta = Integer.parseInt(t[a], 16);
						ub = Integer.parseInt(u[b], 16);
						if (ta < ub && (tagt || ublt))
							return true;
						if (ta > ub && (talt || ubgt))
							return true;
						if (ta == ub && ubeq && taeq)
							return true;
					} catch (NumberFormatException NfE) {
					}
				}
			}
		} catch (NumberFormatException nfe) {
		}
		return false;
	}

	public static String change(FingerPrint f, FingerPrint t) {
		// System.out.println(f.toString());
		// System.out.println(t.toString());
		String cha = "";
		String sub = "";
		if (f.tseqclass != null && t.tseqclass != null
				&& !compat(f.tseqclass, t.tseqclass))
			sub += "tseqclass(" + f.tseqclass + "," + t.tseqclass + ") ";
		if (f.tseqgcd != null && t.tseqgcd != null
				&& !compat(f.tseqgcd, t.tseqgcd))
			sub += "tseqgcd(" + f.tseqgcd + "," + t.tseqgcd + ") ";
		if (f.tseqsi != null && t.tseqsi != null && !compat(f.tseqsi, t.tseqsi))
			sub += "tseqsi(" + f.tseqsi + "," + t.tseqsi + ") ";
		if (sub.length() > 0)
			cha += "TSEQ( " + sub + ")\n";
		for (int a = 0; a < 7; a++) {
			sub = "";
			if (f.resp[a] != null && t.resp[a] != null
					&& !t.resp[a].equals(f.resp[a])) {
				sub += "resp(" + f.resp[a] + "," + t.resp[a] + ") ";
				continue;
			}
			if (f.df[a] != null && t.df[a] != null && !compat(f.df[a], t.df[a]))
				sub += "df(" + f.df[a] + "," + t.df[a] + ") ";
			if (f.w[a] != null && t.w[a] != null && !compat(f.w[a], t.w[a]))
				sub += "w(" + f.w[a] + "," + t.w[a] + ") ";
			if (f.ack[a] != null && t.ack[a] != null
					&& !compat(f.ack[a], t.ack[a]))
				sub += "ack(" + f.ack[a] + "," + t.ack[a] + ") ";
			if (f.flags[a] != null && t.flags[a] != null
					&& !compat(f.flags[a], t.flags[a]))
				sub += "flags(" + f.flags[a] + "," + t.flags[a] + ") ";
			if (f.ops[a] != null && t.ops[a] != null
					&& !compat(f.ops[a], t.ops[a]))
				sub += "ops(" + f.ops[a] + "," + t.ops[a] + ") ";
			if (sub.length() > 0)
				cha += "T" + a + "( " + sub + ")\n";
		}
		return cha;
	}

	// MAJOR DIFF - RESPOND or SEQ num
	public static int dist2(FingerPrint f, FingerPrint t) {
		int count = 0;
		if (f.tseqclass != null && t.tseqclass != null
				&& !f.tseqclass.equals(t.tseqclass))
			count++;
		if (f.tseqgcd != null && t.tseqgcd != null
				&& !compat(f.tseqgcd, t.tseqgcd))
			count++;
		if (f.tseqsi != null && t.tseqsi != null && !compat(f.tseqsi, t.tseqsi))
			count++;
		if (count > 1)
			count = 1;
		for (int a = 0; a < 7; a++) {
			if (f.resp[a] != null && t.resp[a] != null
					&& !t.resp[a].equals(f.resp[a]))
				count++;
		}
		return count;
	}

	// Major diffs and Type of Diff (ie 1 for all DNF diffs)
	public static int dist(FingerPrint f, FingerPrint t) {
		int count = 0;
		if (f.tseqclass != null && t.tseqclass != null
				&& !f.tseqclass.equals(t.tseqclass))
			count++;
		if (f.tseqgcd != null && t.tseqgcd != null
				&& !compat(f.tseqgcd, t.tseqgcd))
			count++;
		if (f.tseqsi != null && t.tseqsi != null && !compat(f.tseqsi, t.tseqsi))
			count++;
		if (count > 1)
			count = 1;
		int c[] = new int[5];
		for (int a = 0; a < 7; a++) {
			// if (f.resp[a]!=null && t.resp[a]!=null &&
			// !t.resp[a].equals(f.resp[a])) continue;
			if (f.df[a] != null && t.df[a] != null && !compat(f.df[a], t.df[a]))
				c[0] = 1;
			if (f.w[a] != null && t.w[a] != null && !compat(f.w[a], t.w[a]))
				c[1] = 1;
			if (f.ack[a] != null && t.ack[a] != null
					&& !compat(f.ack[a], t.ack[a]))
				c[2] = 1;
			if (f.flags[a] != null && t.flags[a] != null
					&& !compat(f.flags[a], t.flags[a]))
				c[3] = 1;
			if (f.ops[a] != null && t.ops[a] != null
					&& !compat(f.ops[a], t.ops[a]))
				c[4] = 1;
		}
		count += c[0] + c[1] + c[2] + c[3] + c[4];
		return count;
	}

	/*
	 * public static int dist(FingerPrint f,FingerPrint t) { int count=0; if
	 * (f.tseqclass!=null && t.tseqclass!=null &&
	 * !f.tseqclass.equals(t.tseqclass)) count+=8; if (f.tseqgcd!=null &&
	 * t.tseqgcd!=null && !f.tseqgcd.equals(t.tseqgcd)) count+=8; if
	 * (f.tseqsi!=null && t.tseqsi!=null && !f.tseqsi.equals(t.tseqsi))
	 * count+=8; for (int a=0;a<7;a++) { if (f.resp[a]!=null && t.resp[a]!=null &&
	 * !t.resp[a].equals(f.resp[a])) {count+=8;continue;} if (f.df[a]!=null &&
	 * t.df[a]!=null && !compat(f.df[a],t.df[a])) count+=8; if (f.w[a]!=null &&
	 * t.w[a]!=null && !compat(f.w[a],t.w[a])) count++; if (f.ack[a]!=null &&
	 * t.ack[a]!=null && !compat(f.ack[a],t.ack[a])) count+=8; if
	 * (f.flags[a]!=null && t.flags[a]!=null && !compat(f.flags[a],t.flags[a]))
	 * count+=2; if (f.ops[a]!=null && t.ops[a]!=null &&
	 * !compat(f.ops[a],t.ops[a])) count++; } return count; }
	 */

	/*
	 * diff (t1,t2,t3,t4,t5,t6,t7,t8)
	 * 
	 */

	public static void main(String s[]) {
		String S = "", T = "";
		if (s.length > 0)
			S = s[0];
		if (s.length > 1)
			T = s[1];
		System.out.println("S=" + S + " T=" + T + " compat =" + compat(S, T));
	}

}
