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
/**
 *  Takes a file and converts it into a text file of its hex values. 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class LineFix {
	public static int MAXCHUNK=100000;
	
	public static void main (String s[])throws IOException {
		String ofile = null, ifile = null;
		for (int a = 0; a < s.length; a++) {
			if (s[a].startsWith ("-"))s[a] = s[a].substring (1);
			if (s[a].equals ("?")) {
				System.out.println ("Format: java LineFix [options]");
				System.out.println (" -i <filename>");
				System.out.println (" -o <filename>");
				System.out.println (" -b <filename>");
				System.exit (0);
			}
			else if (s[a].toUpperCase().startsWith ("B")) {
				s[a] = s[a].substring (1);
				if (s[a].length() > 0)ifile = s[a];
				else if (s[a].length() == 0 && s.length > a + 1) {
					ofile = s[a + 1];
					ifile=ofile+"~";
					a++;
				}
				else {
					System.err.println ("MyError: no input file name.");
					System.exit (0);
				}
				//copy ofile to ifile~
				try {
					FileInputStream fi=new FileInputStream(ofile);
					FileOutputStream fo=new FileOutputStream(ifile);
					byte buf[]=new byte[MAXCHUNK];
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
		}
		InputStream inf = System.in;
		OutputStream ouf = System.out;
		//open input file
		try {
			if (ifile != null) {
				inf = new FileInputStream (ifile);
			}
			if (ofile != null) {
				ouf = new FileOutputStream (ofile);
			}
			BufferedReader br = new BufferedReader (new InputStreamReader (inf));
			PrintWriter pw = new PrintWriter (new OutputStreamWriter (ouf));
			while (true) {
				String ss = br.readLine();
				if (ss == null)break;
				while (ss.indexOf ("\r") >= 0) {
					ss = ss.substring (0, ss.indexOf ("\r")) + ss.substring (ss.indexOf ("\r") + 1);
				}
				pw.println (ss);
			}
			pw.flush();
			pw.close();
			br.close();
		}
		catch (IOException IOE) {
			System.err.println ("MyError: Unable to read or write.");
			System.exit (0);
		}
	}
}

