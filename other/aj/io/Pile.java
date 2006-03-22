package aj.io;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Pile {
	
	static int MAXCHUNK = 1000000;
	/**
	 *  Constructor for the Pile object 
	 *
	 *@param  source  Description of Parameter 
	 */
	public Pile (String source) {
		//unPile
		InputStream FI;
		String files[];
		long lengths[];
		int size;
		try {
			FI = new FileInputStream (source);
			size = Integer.parseInt (myReadLine (FI));
			for (int a = 0; a < size; a++) {
				makeDir (cleanString (myReadLine (FI)));
			}
			size = Integer.parseInt (myReadLine (FI));
			files = new String[size];
			lengths = new long[size];
			for (int a = 0; a < files.length; a++) {
				files[a] = cleanString (myReadLine (FI));
				lengths[a] = Long.parseLong (myReadLine (FI));
			}
			for (int a = 0; a < files.length; a++) {
				if ( !readRaw (FI, files[a], lengths[a])) {
					break;
				}
			}
		}
		catch (IOException IOE) {
			System.out.println ("MyError: Cannot read source " + source);
			System.exit (0);
		}
		catch (NumberFormatException NFE2) {
			System.out.println ("MyError: Bad number in unPile" + NFE2);
			System.exit (0);
		}
	}
	/**
	 *  Constructor for the Pile object 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public Pile (String argv[]) {
		OutputStream FO = null;
		Vector DirList = new Vector();
		Vector FileList = new Vector();
		try {
			FO = new FileOutputStream (argv[0]);
		}
		catch (IOException IOE) {
			System.out.println ("MyError: Cannot write " + IOE);
			System.exit (0);
		}
		for (int a = 1; a < argv.length; a++) {
			File F = new File (argv[a]);
			if (F.isFile()) {
				FileList.addElement (argv[a]);
			}
			if (F.isDirectory()) {
				DirList.addElement (F);
			}
		}
		for (int a = 1; a < argv.length; a++) {
			DirList = combine (DirList, subDirs (argv[a]));
		}
		for (int a=0; a < DirList.size(); a++) {
			FileList = combine (FileList, dirFiles ((File)DirList.elementAt (a)));
		}
		noDups (DirList);
		noDups (FileList);
		try {
			FO.write ((DirList.size() + "\n").getBytes());
			for (int a=0; a < DirList.size(); a++) {
				File F = (File)DirList.elementAt (a);
				FO.write ((F.getPath() + "\n").getBytes());
			}
			FO.write ((FileList.size() + "\n").getBytes());
			for (int a=0; a < FileList.size(); a++) {
				String n = (String)FileList.elementAt (a);
				FO.write ((n + "\n").getBytes());
				long fsize = (new File (n)).length();
				FO.write ((fsize + "\n").getBytes());
			}
			for (int a=0; a < FileList.size(); a++) {
				dumpBytes (FO, (String)FileList.elementAt (a));
			}
		}
		catch (IOException IOE3) {
			System.out.println ("MyError: Cannot write in file" + IOE3);
			System.exit (0);
		}
	}
	/**
	 *  My version of a tar program. It Piles up files into one, can also unPile 
	 *  files back into original directory sturcture. 
	 *
	 *@param  one  Description of Parameter 
	 *@param  two  Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public Vector combine (Vector one, Vector two) {
		Vector V = new Vector();
		for (int a=0; a < one.size(); a++) {
			V.addElement (one.elementAt (a));
		}
		for (int a=0; a < two.size(); a++) {
			V.addElement (two.elementAt (a));
		}
		return V;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  f  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Vector subDirs (String f) {
		Vector V = new Vector();
		File F = new File (f);
		String S[];
		if ( !F.exists()) {
			return new Vector();
		}
		if (F.isFile()) {
			return new Vector();
		}
		else {
			V.addElement (F);
		}
		S = F.list();
		for (int a=0; a < S.length; a++) {
			V = combine (V, subDirs (f + File.separator + S[a]));
		}
		return V;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  F  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Vector dirFiles (File F) {
		Vector V = new Vector();
		String S[];
		if ( !F.exists()) {
			return new Vector();
		}
		if (F.isFile()) {
			V.addElement (F.getPath());
			return V;
		}
		S = F.list();
		for (int a=0; a < S.length; a++) {
			V = combine (V, dirFiles (new File (F.getPath() + File.separator + S[a])));
		}
		return V;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 */
	public void noDups (Vector V) {
		for (int a=0; a < V.size(); a++) {
			for (int b = a + 1; b < V.size(); b++) {
				if (V.elementAt (a).equals (V.elementAt (b))) {
					V.removeElementAt (b);
					b--;
				}
			}
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  dname  Description of Parameter 
	 *@return        Description of the Returned Value 
	 */
	public String cleanString (String dname) {
		String good = File.separator;
		String bad;
		if (good.equals ("/")) {
			bad = "\\";
		}
		else {
			bad = "/";
		}
		while (dname.indexOf (bad) >= 0) {
			dname = dname.substring (0, dname.indexOf (bad)) + good + dname.substring (dname.indexOf (bad) + 1);
		}
		return dname;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  dname  Description of Parameter 
	 */
	public void makeDir (String dname) {
		File D = new File (dname);
		if ( !D.exists()) {
			D.mkdirs();
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  FI     Description of Parameter 
	 *@param  fname  Description of Parameter 
	 *@param  size   Description of Parameter 
	 *@return        Description of the Returned Value 
	 */
	public boolean readRaw (InputStream FI, String fname, long size) {
		byte b[] = new byte[MAXCHUNK];
		try {
			OutputStream FO = new FileOutputStream (fname);
			while (size > 0) {
				if (size < MAXCHUNK) {
					b = new byte[ (int)size];
					size = 0;
				}
				else {
					size -= MAXCHUNK;
				}
				int r = FI.read (b);
				//end of pile error
				if (r == - 1) {
					System.out.println ("MyError: Unexpected end of pile found.");
					return false;
				}
				FO.write (b);
			}
			FO.flush();
			FO.close();
		}
		catch (IOException IOE) {
			System.out.println ("MyError: Cannot uPile in write " + IOE);
			System.exit (0);
		}
		return true;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  OUT    Description of Parameter 
	 *@param  Fname  Description of Parameter 
	 */
	public void dumpBytes (OutputStream OUT, String Fname) {
		FileInputStream IN = null;
		try {
			IN = new FileInputStream (Fname);
		}
		catch (IOException IOE1) {
			System.out.println ("MyError: Cannot open file>" + Fname);
			System.exit (0);
		}
		byte b[] = new byte[MAXCHUNK];
		try {
			while (IN.available() > 0) {
				if (IN.available() < MAXCHUNK) {
					b = new byte[IN.available()];
				}
				int r = IN.read (b);
				if (r == - 1) {
					System.out.println ("MyError: Cannot read input file fully <" + Fname + "> fail!");
					System.exit (0);
				}
				try {
					Thread.yield();
					OUT.write (b);
				}
				catch (IOException IOE2) {
					System.out.println ("MyError: Cannot write to Pile>" + IOE2);
					System.exit (0);
				}
			}
			IN.close();
		}
		catch (IOException IOE3) {
			System.out.println ("MyError: Cannot read file>" + Fname);
			System.exit (0);
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  i                Description of Parameter 
	 *@return                  Description of the Returned Value 
	 *@exception  IOException  Description of Exception 
	 */
	public String myReadLine (InputStream i)throws IOException {
		String a = "";
		int c = i.read();
		while (c != - 1) {
			if ((char)c == '\n') {
				return a;
			}
			a = a + (char)c;
			c = i.read();
		}
		return a;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public static void main (String argv[]) {
		if (argv.length == 0) {
			System.out.println ("FORMAT: java Pile <dest> <files & dirs ..> ");
			System.out.println ("FORMAT: java Pile <source>");
			System.exit (0);
		}
		if (argv.length == 1) {
			new Pile (argv[0]);
		}
		else {
			new Pile (argv);
		}
	}
}

