package aj.io;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 *  Takes a file and converts it into a text file of its hex values. 
 *
 *@author     judda 
 *@created    April 12, 2000 
 *
 * 8/29/01 - remove duplicate = in symbols
 */
public class Encode {
	
	static int CHUNKSIZE = 10000;
	
	static char buff1 = ' ', buff2 = '\t', eol = '\n';
	
	public static void main (String s[])throws IOException {
		String ofile = null, ifile = null;
		String code = "";
		String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String alpha = ALPHA.toLowerCase();
		String num = "0123456789";
		String sym = "=!@#$%^&*()_-+~`<,>.?/:;\"'{[}]|\\";
		String base = num + ALPHA;
		boolean encode = true;
		boolean wordlen = false;
		boolean trueword = false;
		int collen = - 1;
		Encode E=new Encode ();
		for (int a = 0; a < s.length; a++) {
			if (s[a].startsWith ("-"))s[a] = s[a].substring (1);
			if (s[a].equals ("?")) {
				System.out.println ("Format: java aj.io.Encode [options]");
				System.out.println (" -i <filename>");
				System.out.println (" -o <filename>");
				System.out.println (" -b <filename>");
				System.out.println (" -A =A-Z");
				System.out.println (" -a =a-z");
				System.out.println (" -N =0-9");
				System.out.println (" -S =" + sym);
				System.out.println (" -*= same as -A -a -S -N");
				System.out.println (" -B <num> = base 2-26 using (" + base + ")");
				System.out.println (" -C \"chars\"");
				System.out.println (" -U UncodeMode");
				System.out.println (" -w[buffchar] buffchar between bytes (default tab)");
				System.out.println (" -W[buffchar] buffchar between bytes and 32bit words");
				System.out.println (" -L <num>  set max colums");
				System.exit (0);
			}
			else if (s[a].toUpperCase().startsWith ("L")) {
				s[a] = s[a].substring (1);
				try {
					if (s[a].length() > 0)collen = Integer.parseInt (s[a]);
					else if (s.length > a + 1) {
						collen = Integer.parseInt (s[a + 1]);
						a++;
					}
					else {
						System.err.println ("MyError: bad number in colum");
						System.exit (0);
					}
				}
				catch (NumberFormatException NFE) {
					System.err.println ("MyError: bad number in colum");
					System.exit (0);
				}
			}
			else if (s[a].toUpperCase().startsWith ("C")) {
				s[a] = s[a].substring (1);
				if (s[a].length() > 0) {
					code = E.buildCode (code, s[a]);
				}
				else if (s.length > a + 1) {
					code = E.buildCode (code, s[a + 1]);
					a++;
				}
				else {
					System.err.println ("MyError: no chars in code.");
					System.exit (0);
				}
			}
			else if (s[a].startsWith ("B")) {
				s[a] = s[a].substring (1);
				String bs = null;
				if (s[a].length() > 0)bs = s[a];
				else if (s[a].length() < 0 && s.length > a + 1) {
					bs = s[a + 1];
					a++;
				}
				else {
					System.err.println ("MyError: no number in base.");
					System.exit (0);
				}
				try {
					int bt = Math.min (36, Math.max (2, Integer.parseInt (bs)));
					code = E.buildCode (code, base.substring (0, bt));
				}
				catch (NumberFormatException NFE) {
					System.err.println ("MyError: bad number in base " + bs);
					System.exit (0);
				}
			}
			else if (s[a].toUpperCase().startsWith ("W")) {
				if (s[a].startsWith ("W"))trueword = true;
				wordlen = true;
				s[a] = s[a].substring (1);
				if (s[a].length() > 0)buff1 = s[a].charAt (0);
			}
			else if (s[a].startsWith ("a")) {
				code = E.buildCode (code, alpha);
			}
			else if (s[a].startsWith ("A")) {
				code = E.buildCode (code, ALPHA);
			}
			else if (s[a].toUpperCase().startsWith ("N")) {
				code = E.buildCode (code, num);
			}
			else if (s[a].toUpperCase().startsWith ("S")) {
				code = E.buildCode (code, sym);
			}
			else if (s[a].toUpperCase().startsWith ("*")) {
				code = E.buildCode (code, num);
				code = E.buildCode (code, ALPHA);
				code = E.buildCode (code, alpha);
				code = E.buildCode (code, sym);
			}
			else if (s[a].startsWith ("b")) {
				s[a] = s[a].substring (1);
				if (s[a].length() > 0) {
					ofile = s[a];
					ifile=ofile+"~";
				}
				else if (s[a].length() == 0 && s.length > a + 1) {
					ofile = s[a + 1];
					ifile=ofile+"~";
					a++;
				}
				else {
					System.err.println ("MyError: no input file name.");
					System.exit (0);
				}
System.out.println("ifile ="+ifile+ " ofile="+ofile);
				//copy ofile to ifile~
				try {
					FileInputStream fi=new FileInputStream(ofile);
					FileOutputStream fo=new FileOutputStream(ifile);
					byte buf[]=new byte[CHUNKSIZE];
					while (true) {
						int c=fi.read(buf);
						if (c==-1) break;
						fo.write(buf,0,c);
					}
					fo.flush();fo.close();fi.close();
				} catch (IOException ioe) {
					System.out.println("MyError: cannot open close same file");
				}
			}
			else if (s[a].toUpperCase().startsWith ("I")) {
				s[a] = s[a].substring (1);
				if (s[a].length() > 0)ifile = s[a];
				else if (s[a].length() == 0 && s.length > a + 1) {
					ifile = s[a + 1];
					a++;
				}
				else {
					System.err.println ("MyError: no input file name.");
					System.exit (0);
				}
			}
			else if (s[a].toUpperCase().startsWith ("O")) {
				s[a] = s[a].substring (1);
				if (s[a].length() > 0)ofile = s[a];
				else if (s[a].length() == 0 && s.length > a + 1) {
					ofile = s[a + 1];
					a++;
				}
				else {
					System.err.println ("MyError: no output file name.");
					System.exit (0);
				}
			}
			else if (s[a].equalsIgnoreCase ("U")) {
				encode = false;
			}
		}
		if (code.indexOf (buff1 + "") >= 0) {
			code = code.substring (0, code.indexOf (buff1 + "")) + code.substring (code.indexOf (buff1 + "") + 1);
		}

		InputStream inf = System.in;
		OutputStream ouf = System.out;
		try {
			if (ifile != null) {
				inf = new FileInputStream (ifile);
			}
		}
		catch (IOException ioe) {
			System.err.println ("MyError: Bad inputfile, cannot open");
			System.exit (0);
		}
		try {
			if (ofile != null) {
				ouf = new FileOutputStream (ofile);
			}
		}
		catch (IOException IOE) {
			System.err.println ("MyError: Bad outputfile, cannot open");
			System.exit (0);
		}
		
		if (encode) {
			E.encodeStream(inf, ouf, code, buff1, wordlen, trueword, collen);
		}
		else {
			E.decodeStream(inf, ouf, code, buff1, wordlen, trueword, collen);
		}
	}
	
	public String buildCode (String orig, String add) {
		for (int a = 0; a < add.length(); a++) {
			if (orig.indexOf (add.charAt (a)) < 0) {
				orig = orig + add.charAt (a);
			}
			else {
				System.err.println ("MyError: duplicate char in code.  Ignoring.<" + add.charAt (a) + ">");
			}
		}
		return orig;
	}
	
	public Encode () {}

	public static String encodeString(String s,String code) {
		String out="";
		Encode E=new Encode();
		out=code + code.charAt (0)+E.convert (s.getBytes(), s.length(), code, ' ', false, false, 0);
		return out;
	}

	public void encodeStream (InputStream inf, OutputStream ouf, String code, char buff1,  boolean wordlen, boolean trueword, int collen) {
		if (code.length() < 2) {
			System.err.println ("MyError: code length less than 2, cannot encode.");
			System.exit (0);
		}
		byte b[] = new byte[CHUNKSIZE];
		try {
			ouf.write ((code + code.charAt (0) + (wordlen?buff1 + "":"")).getBytes());
			if (collen > 0)ouf.write ((eol + "").getBytes());
			while (true) {
				int c = inf.read (b);
				if (c == - 1)break;
				String o = convert (b, c, code, buff1, wordlen, trueword, collen);
				ouf.write (o.getBytes());
			}
			ouf.flush();
			ouf.close();
			inf.close();
		}
		catch (IOException IOE) {
			System.err.println ("MyError: Unable to read or write.");
			System.exit (0);
		}
	}
	public static String decodeString(String s) {
		String code = "";
		String out="";
		String intext = s;
		char buff1=' ';
		Encode E=new Encode();
		intext = E.removeFormat (intext, buff1);
		for (int a = 0; a < intext.length(); a++) {
			if (code.indexOf (intext.charAt (a)) < 0) {
				code += intext.charAt (a);
			}
			else {
				intext = intext.substring (a + 1);
				if (intext.length() > 0) {
					if (code.indexOf (intext.charAt (0)) < 0) {
						buff1 = intext.charAt (0);
						intext = intext.substring (1);
					}
				}
				break;
			}
		}
		intext = E.removeFormat (intext, buff1);
		byte bb[] = E.convert (intext, buff1, code);
		out+=new String(bb);
		return out;
	}

	public void decodeStream (InputStream inf, OutputStream ouf, String code, char buff1, boolean wordlen, boolean trueword, int collen) {
		boolean doneCode = false;
		byte b[] = new byte[CHUNKSIZE];
		try {
			code = "";
			while (true) {
				int c = inf.read (b);
				if (c == - 1)break;
				String intext = new String (b, 0, c);
				intext = removeFormat (intext, buff1);
				if ( !doneCode) {
					for (int a = 0; a < intext.length(); a++) {
						if (code.indexOf (intext.charAt (a)) < 0) {
							code += intext.charAt (a);
						}
						else {
							doneCode = true;
							intext = intext.substring (a + 1);
							if (intext.length() > 0) {
								if (code.indexOf (intext.charAt (0)) < 0) {
									buff1 = intext.charAt (0);
									intext = intext.substring (1);
								}
							}
							break;
						}
					}
				}
				intext = removeFormat (intext, buff1);
				byte bb[] = convert (intext, buff1, code);
				ouf.write (bb);
			}
			ouf.flush();
			ouf.close();
			inf.close();
		}
		catch (IOException IOE) {
			System.err.println ("MyError: Unable to read or write.");
			System.exit (0);
		}
	}
	
	int count = 0;
	
	public String convert (byte orig[], int origlen, String code, char buff1, boolean wordlen, boolean trueword, int collen) {
		StringBuffer ret = new StringBuffer();
		int len = code.length();
		double rt = Math.log (256) / Math.log (len);
		int rot = ((int)rt == rt? (int)rt: (int) (rt + 1));
		for (int a = 0; a < origlen; a++) {
			count++;
			int s = (int)orig[a];
			s = s & 0x000000ff;
			String res = "";
			for (int b = 0; b < rot; b++) {
				res = code.charAt (s % len) + res;
				s = (byte) (s / len);
			}
			ret.append(res);
			if (wordlen) {
				ret.append(buff1);
			}
			if (count % 4 == 0 && trueword) {
				ret.append(buff2);
			}
			int colcheck = Math.max (1, collen / (rot + (wordlen?1:0)));
			if (collen > 0 && (a + 1) / colcheck != a / colcheck) ret.append(eol);
		}
		return ret.toString();
	}
	String inc = "";
	
	//cuts spaces and \n and \r that are cannot be part of the code
	public String removeFormat (String orig, char buff1) {
		StringBuffer sb=new StringBuffer(orig);

		for (int a=0;a<sb.length();a++) {
			if (sb.charAt(a)==buff1 || sb.charAt(a)==buff2 || sb.charAt(a)=='\n' || sb.charAt(a)=='\r')
				sb.replace(a,a+1,"");
		}
		return sb.toString();
	}
	
	public byte[] convert (String orig, char buff1, String code) {
		orig = inc + orig;
		int len = code.length();
		double rt = Math.log (256) / Math.log (len);
		int rot = ((int)rt == rt? (int)rt: (int) (rt + 1));
		inc = orig.substring (orig.length() - orig.length() % rot);
		orig = orig.substring (0, orig.length() - orig.length() % rot);
		byte bb[] = new byte[orig.length() / rot];
		for (int a = 0; a < orig.length(); a += rot) {
			int r = 0;
			for (int b = 0; b < rot; b++) {
				r = r * len + code.indexOf (orig.charAt (a + b));
				if (code.indexOf (orig.charAt (a + b)) < 0) {
					System.err.println ("MyError: bad data in unencode. Cannot unencode. a=" + a + " b=" + b + " " + orig.charAt (a + b));
					System.exit (0);
				}
			}
			bb[a / rot] = (byte)r;
		}
		return bb;
	}
}

