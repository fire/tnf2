package aj.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

//MyTODO CONVERT TO STRINGBUFFER
//MyTODO user indexOf(c,last); 
//StringBuffer.append(), 
//StringBuffer.toString(), 
//StringBuffere.substring(), 
//StringBuffer
public class Indent {

	static private String operators[] = { "[]", "};", "()", "+=", "-=", "*=",
			"/=", "%=", "++", "--", "->", "**", "/", "^", "%", "::", "==",
			">=", "<=", ",", "!=", "!", "~", ">>", "<<", ">", "<", "&&", "||",
			"&", "|", ".*", ".", "[", "]", "(", ")", "+", "-", "*", "{", "}",
			"#", "=", ";" };

	static private String spaceBeforeList[] = { "+=", "-=", "*=", "/=", "%=",
			"/", "^", "%", "==", ">=", "<=", "!=", "!", "~", ">", "<", "&&",
			"||", "&", "|", "+", "-", "*", "=" };

	static private String spaceAfterList[] = { "+=", "-=", "*=", "/=", "%=",
			"**", "/", "^", "%", "==", ">=", "<=", ",", "!=", ">", "<", "&&",
			"||", "&", "|", "+", "-", "*", "=", ";" };

	static private boolean verbose = false;

	static private boolean progress = false;

	static private String INDENTCHAR = "\t";

	private Vector replaceList = new Vector();

	private String REPLACE = "REM";

	private String REPLACEWITH = "REMM";

	private String FIND = REPLACE + "_";

	boolean prevWasOper = true;

	static int MAXSTR = 500;

	// for fast string dividing
	public static void main(String s[]) {
		String ofile = null, ifile = null;
		for (int a = 0; a < s.length; a++) {
			if (s[a].startsWith("-")) {
				s[a] = s[a].substring(1);
			}
			if (s[a].equals("?")) {
				System.out.println("Format: java LineFix [options]");
				System.out.println(" -i <filename>  input file");
				System.out.println(" -o <filename>  output file");
				System.out.println(" -b <filename>  inoutfile (back ~)");
				System.out
						.println(" -n <number of space to indent> 0=cut all spaces");
				System.out.println(" -t <use tabs to indent>");
				System.out.println(" -v <verbose mode>");
				System.out.println(" -p <verbose mode>");
				System.exit(0);
			} else if (s[a].toUpperCase().startsWith("I")) {
				s[a] = s[a].substring(1);
				if (s[a].length() > 0) {
					ifile = s[a];
				} else if (s[a].length() == 0 && s.length > a + 1) {
					ifile = s[a + 1];
					a++;
				} else {
					System.err.println("MyError: no input file name.");
					System.exit(0);
				}
			} else if (s[a].toUpperCase().startsWith("O")) {
				s[a] = s[a].substring(1);
				if (s[a].length() > 0) {
					ofile = s[a];
				} else if (s[a].length() == 0 && s.length > a + 1) {
					ofile = s[a + 1];
					a++;
				} else {
					System.err.println("MyError: no output file name.");
					System.exit(0);
				}
			} else if (s[a].toUpperCase().startsWith("B")) {
				s[a] = s[a].substring(1);
				if (s[a].length() > 0) {
					ifile = ofile = s[a];
				} else if (s[a].length() == 0 && s.length > a + 1) {
					ifile = ofile = s[a + 1];
					a++;
				} else {
					System.err.println("MyError: no input/output file name.");
					System.exit(0);
				}
			} else if (s[a].toUpperCase().startsWith("T")) {
				INDENTCHAR = "\t";
			} else if (s[a].toUpperCase().startsWith("V")) {
				Indent.verbose = true;
			} else if (s[a].toUpperCase().startsWith("P")) {
				Indent.progress = true;
			} else if (s[a].toUpperCase().startsWith("N")) {
				s[a] = s[a].substring(1);
				try {
					if (s[a].length() > 0) {
						setIndent(Integer.parseInt(s[a]));
					} else if (s[a].length() == 0 && s.length > a + 1) {
						setIndent(Integer.parseInt(s[a + 1]));
						a++;
					}
				} catch (NumberFormatException NFE) {
					System.err
							.println("MyError: Bad number in indent setting.");
					System.exit(0);
				}
			}
		}
		InputStream inf = System.in;
		OutputStream ouf = System.out;
		// open input file
		Indent indent = new Indent();
		try {
			if (ifile != null) {
				inf = new FileInputStream(ifile);
			}
		} catch (IOException IOE) {
			System.err.println("MyError: Unable to read or write.");
			System.exit(0);
		}
		if (progress)
			System.out.print("begin readfile");
		checkTime();
		String all = indent.read(inf);
		if (progress)
			System.out.println("done \t" + checkTime());
		if (ifile != null && ifile.equals(ofile)) {
			try {
				if (progress)
					System.out.print("begin backup");
				checkTime();
				indent.display(new FileOutputStream(ifile + "~"), all);
				if (progress)
					System.out.println("done \t" + checkTime());
			} catch (IOException IOE) {
				System.err.println("MyError: Unable to write.");
				System.exit(0);
			}
		}
		all = indent.doIndent(all);
		try {
			if (ofile != null) {
				ouf = new FileOutputStream(ofile);
			}
			if (progress)
				System.out.print("begin save");
			checkTime();
			indent.display(ouf, all);
			if (progress)
				System.out.println("done \t" + checkTime());
		} catch (IOException IOE) {
			System.err.println("MyError: Unable to write.");
			System.exit(0);
		}
	}

	private static void setIndent(int x) {
		INDENTCHAR = "";
		for (int a = 0; a < x; a++)
			INDENTCHAR += " ";
	}

	public static long lt = 0;

	public static int checkTime() {
		if (lt == 0)
			lt = System.currentTimeMillis();
		int nt = (int) (System.currentTimeMillis() - lt);
		lt = System.currentTimeMillis();
		return nt;
	}

	public String doIndent(String all) {
		if (verbose)
			System.out.print("***ORIG\n" + all);
		if (progress)
			System.out.print("begin prepRep");
		checkTime();
		all = prepReplace(all);
		if (progress)
			System.out.println("done \t" + checkTime());
		if (verbose)
			System.out.print("***prepReplace DONE result below\n" + all);
		if (progress)
			System.out.print("begin replStrs");
		checkTime();
		all = replaceStrings(all);
		if (progress)
			System.out.println("done \t" + checkTime());
		if (verbose)
			System.out.print("***replaceStrings done result below\n" + all);
		if (progress)
			System.out.print("begin Indentit");
		checkTime();
		all = indentIt(all);
		if (progress)
			System.out.println("done \t" + checkTime());
		if (verbose)
			System.out.print("***indentIt done result below\n" + all);
		if (progress)
			System.out.print("begin restStrs");
		checkTime();
		all = restoreStrings(all);
		if (progress)
			System.out.println("done \t" + checkTime());
		if (verbose)
			System.out.print("***restoreStrings done result below\n" + all);
		if (progress)
			System.out.print("begin remStrs");
		checkTime();
		all = removeReplace(all);
		if (progress)
			System.out.println("done \t" + checkTime());
		if (verbose)
			System.out.print("***removeReplace done result belowE\n" + all);
		return all;
	}

	private String indentIt(String all) {
		all = all.trim();
		int indent = 0;
		String fixed = "";
		String fixed2 = "";
		boolean newline = true;
		int insideParen = 0;
		while (all.length() > 0) {
			String tok = getToken(all);
			all = all.substring(tok.length()).trim();
			tok = tok.trim();
			char cc = tok.charAt(0);
			boolean isTok = ((cc >= 'A' && cc <= 'Z')
					|| (cc >= 'a' && cc <= 'z') || (cc >= '0' && cc <= '9') || cc == '_');
			if (tok.startsWith(FIND)) {
				isTok = false;
			}
			// must not be token to avoid leading spaces
			if (tok.equals("{")) {
				indent++;
				if (fixed.length() > 0
						&& fixed.charAt(fixed.length() - 1) != ' '
						&& optionalSpacesAllowed()) {
					fixed += " ";
				}
				fixed += tok;
				if (optionalSpacesAllowed()) {
					fixed += "\n";
				}
				newline = true;
				insideParen = 0;
				prevWasOper = true;
			} else if (tok.equals("}") || tok.equals("};")) {
				indent--;
				if (!newline && optionalSpacesAllowed()) {
					fixed += "\n";
				}
				fixed += getIndent(indent) + tok;
				if (optionalSpacesAllowed()) {
					// change
					fixed += "\n";
				}
				newline = true;
				insideParen = 0;
				prevWasOper = true;
			} else if ((tok.equals(";") || tok.equals(":")) && insideParen == 0) {
				fixed += tok;
				if (optionalSpacesAllowed()) {
					fixed += "\n";
				}
				newline = true;
				prevWasOper = true;
			} else if (isTok && !prevWasOper) {
				// no two tokens can be adjacent need space
				if (newline) {
					fixed += getIndent(indent);
					newline = false;
				}
				fixed += " ";
				fixed += tok;
				prevWasOper = !isTok;
			} else if (isTok
					&& (tok.equals("public") || tok.equals("private") || tok
							.equals("static"))) {
				if (newline) {
					fixed += getIndent(indent);
					newline = false;
				}
				if (optionalSpacesAllowed()) {
					fixed += "\n" + getIndent(indent);
				}
				fixed += tok;
				newline = false;
				prevWasOper = !isTok;
			} else {
				if (newline) {
					fixed += getIndent(indent);
					newline = false;
				}
				if (tok.equals("(")) {
					insideParen++;
				}
				if (tok.equals(")")) {
					insideParen--;
				}
				boolean spaceAfter = false, spaceBefore = false;
				if (!isTok) {
					for (int a = 0; a < spaceAfterList.length; a++) {
						if (spaceAfterList[a].equals(tok)) {
							spaceAfter = true;
							break;
						}
					}
					for (int a = 0; a < spaceBeforeList.length; a++) {
						if (spaceBeforeList[a].equals(tok)) {
							spaceBefore = true;
							break;
						}
					}
				}
				if (fixed.length() > 0
						&& fixed.charAt(fixed.length() - 1) != ' '
						&& spaceBefore
						&& optionalSpacesAllowed()
						&& (!tok.equals("(") || (fixed.length() > 0 && fixed
								.charAt(fixed.length() - 1) != '('))) {
					fixed += " ";
				}
				fixed += tok;
				if (spaceAfter
						&& optionalSpacesAllowed()
						&& (!tok.equals(")") || fixed.length() > 0
								&& fixed.charAt(fixed.length() - 1) != ')')) {
					fixed += " ";
				}
				prevWasOper = !isTok;
			}
			if (fixed.length() > MAXSTR && fixed.lastIndexOf(" ") > 0) {
				fixed2 += fixed.substring(0, fixed.lastIndexOf(" "));
				fixed = fixed.substring(fixed.lastIndexOf(" "));
			}
		}
		return fixed2 + fixed;
	}

	public boolean optionalSpacesAllowed() {
		return INDENTCHAR.length() != 0;
	}

	private String getIndent(int i) {
		String res = "";
		for (int a = 0; a < i; a++)
			res += INDENTCHAR;
		return res;
	}

	private String getToken(String all) {
		String res = all;
		int len = 0;
		char c = res.charAt(len);
		boolean tok = false;
		if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
				|| (c >= '0' && c <= '9') || c == '_') {
			tok = true;
		}
		len++;
		if (tok) {
			while (len < res.length()) {
				c = res.charAt(len);
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
						|| (c >= '0' && c <= '9') || c == '_') {
					len++;
				} else
					break;
			}
		} else if (!tok) {
			for (int a = 0; a < operators.length; a++) {
				if (res.startsWith(operators[a])) {
					len = operators[a].length();
					break;
				}
			}
		}
		String res2 = all.substring(0, all.indexOf(res) + len);
		return res2;
	}

	private void display(OutputStream o, String all) {
		PrintWriter oo = new PrintWriter(new OutputStreamWriter(o));
		while (all.indexOf("\n") >= 0) {
			oo.println(all.substring(0, all.indexOf("\n")));
			all = all.substring(all.indexOf("\n") + 1);
		}
		oo.println(all);
		oo.flush();
	}

	private String prepReplace(String all) {
		String res = "";
		while (all.indexOf(REPLACE) >= 0) {
			res = res + all.substring(0, all.indexOf(REPLACE)) + REPLACEWITH;
			all = all.substring(all.indexOf(REPLACE) + REPLACE.length());
		}
		res += all;
		return res;
	}

	private String removeReplace(String all) {
		String res = "";
		while (all.indexOf(REPLACEWITH) >= 0) {
			res = res + all.substring(0, all.indexOf(REPLACEWITH)) + REPLACE;
			all = all
					.substring(all.indexOf(REPLACEWITH) + REPLACEWITH.length());
		}
		res += all;
		return res;
	}

	private String restoreStrings(String all) {
		String all2 = "";
		String all3 = "";
		while (all.indexOf(FIND) >= 0) {
			String mark = all.substring(all.indexOf(FIND) + FIND.length());
			mark = mark.substring(0, mark.indexOf("_"));
			String TAKE = FIND + mark + "_";
			int num = Integer.parseInt(mark);
			String rem = (String) replaceList.elementAt(num - 1);
			// working
			if (rem.startsWith("//") || rem.startsWith("/*")) {
				if (all.length() > all.indexOf(TAKE) + TAKE.length()
						&& all.charAt(all.indexOf(TAKE) + TAKE.length()) != '\n') {
					String lastret = all.substring(0, all.indexOf(FIND));
					if (lastret.indexOf("\n") >= 0) {
						// System.out.println("case 1 "+rem);
						lastret = lastret.substring(lastret.lastIndexOf("\n"));
					} else {
						// System.out.println("case 2 "+rem);
						lastret = "";
					}
					if (lastret.trim().length() != 0) {
						// System.out.println("case 3 "+rem);
						lastret = "\n";
					}
					// only copy whitespace
					if (rem.startsWith("/*") && !optionalSpacesAllowed()) {
						// System.out.println("case 4 "+rem);
						lastret = "";
					}
					// optinal returns after hard comment
					rem = rem.substring(0, rem.length() - 1) + lastret;
				} else {
					// System.out.println("case 5 "+rem);
					rem = rem.substring(0, rem.length() - 1);
				}
			}
			all = all.substring(0, all.indexOf(TAKE)) + rem
					+ all.substring(all.indexOf(TAKE) + TAKE.length());
			// if(all2.length() > MAXSTR) {
			// all3 += all2;
			// all2 = "";
			// }
		}
		return all3 + all2 + all;
	}

	private String replaceStrings(String all) {
		String all2 = "";
		int comb = all.indexOf("/*");
		int com = all.indexOf("//");
		int quo = all.indexOf("\"");
		int chr = all.indexOf("'");
		int pnd = all.indexOf("#");
		while (comb >= 0 || com >= 0 || quo >= 0 || chr >= 0 || pnd >= 0) {
			// System.out.println("looking for strings loop");
			if (comb >= 0 && (comb < com || com < 0) && (comb < quo || quo < 0)
					&& (comb < chr || chr < 0) && (comb < pnd || pnd < 0)) {
				// System.out.println("working on hard comm");
				String rem = all.substring(comb, all.length());
				if (rem.indexOf("*/") > 0) {
					rem = rem.substring(0, rem.indexOf("*/") + 2);
				}
				replaceList.addElement(rem + "\n");
				all = all.substring(0, comb) + FIND + replaceList.size() + "_ "
						+ all.substring(comb + rem.length(), all.length());
				// space separates tokens
			} else if (com >= 0 && (com < comb || comb < 0)
					&& (com < quo || quo < 0) && (com < chr || chr < 0)
					&& (com < pnd || pnd < 0)) {
				// System.out.println("working on comm");
				String rem = all.substring(com, all.length());
				if (rem.indexOf("\n") >= 0) {
					rem = rem.substring(0, rem.indexOf("\n"));
				}
				replaceList.addElement(rem + "\n");
				all = all.substring(0, com) + FIND + replaceList.size() + "_ "
						+ all.substring(com + rem.length(), all.length());
				// space separates tokens
			} else if (quo >= 0 && (quo < comb || comb < 0)
					&& (quo < com || com < 0) && (quo < chr || chr < 0)
					&& (quo < pnd || pnd < 0)) {
				// System.out.println("working on quote ");
				String rem = all.substring(quo + 1, all.length());
				String hold = "";
				int quof = rem.indexOf("\"");
				int quoquof = rem.indexOf("\\");
				while (quoquof < quof && quoquof >= 0) {
					hold += rem.substring(0, quoquof + 2);
					rem = rem.substring(quoquof + 2);
					quof = rem.indexOf("\"");
					quoquof = rem.indexOf("\\");
				}
				if (quof >= 0 && (quof < quoquof || quoquof < 0)) {
					rem = rem.substring(0, quof + 1);
				}
				rem = "\"" + hold + rem;
				replaceList.addElement(rem);
				// System.out.println("quote found="+rem);
				all = all.substring(0, quo) + FIND + replaceList.size() + "_ "
						+ all.substring(quo + rem.length(), all.length());
				// space separates tokens
			} else if (chr >= 0 && (chr < comb || comb < 0)
					&& (chr < com || com < 0) && (chr < quo || quo < 0)
					&& (chr < pnd || pnd < 0)) {
				// System.out.println("working on char");
				String rem = all.substring(chr + 1, chr + 3);
				if (rem.indexOf("'") >= 0) {
					rem = rem.substring(0, rem.indexOf("'"));
				}
				rem = "'" + rem + "'";
				replaceList.addElement(rem);
				all = all.substring(0, chr) + FIND + replaceList.size() + "_ "
						+ all.substring(chr + rem.length(), all.length());
				// space separates tokens
			} else if (pnd >= 0 && (pnd < comb || comb < 0)
					&& (pnd < com || com < 0) && (pnd < quo || quo < 0)
					&& (pnd < chr || chr < 0)) {
				// System.out.println("working on pnd");
				String rem = all.substring(pnd, all.length());
				if (rem.indexOf("\n") >= 0) {
					rem = rem.substring(0, rem.indexOf("\n"));
				}
				replaceList.addElement(rem + "\n");
				all = all.substring(0, pnd) + FIND + replaceList.size() + "_ "
						+ all.substring(pnd + rem.length(), all.length());
				// space separates tokens
			}
			if (all.lastIndexOf(FIND) > MAXSTR) {
				all2 += all.substring(0, all.lastIndexOf(FIND));
				all = all.substring(all.lastIndexOf(FIND));
			}
			comb = all.indexOf("/*");
			com = all.indexOf("//");
			quo = all.indexOf("\"");
			chr = all.indexOf("'");
			pnd = all.indexOf("#");
			// System.out.println("current vals ="+comb+" "+com+" "+quo+"
			// "+chr+" "+pnd);
		}
		return all2 + all;
	}

	private String read(InputStream i) {
		String all = "";
		String all2 = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(i));
			while (true) {
				String t = br.readLine();
				if (t == null) {
					break;
				}
				all += t + "\n";
				if (all.length() > MAXSTR) {
					all2 += all;
					all = "";
				}
			}
		} catch (IOException ioe) {
			System.err.println("MyError: cannot read data");
			System.exit(0);
		}
		return all2 + all;
	}
}
