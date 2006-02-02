package aj.io;
import java.io.File;
import java.util.Vector;
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class FileSystem {
	
	public static void main (String s[]) {
		if (s.length != 1 && s.length!=2) {
			System.out.println ("FORMAT: java aj.misc.FileSystem <rootdirectory> -d");
			System.out.println (" -d directory only");
			System.exit (0);
		}
		if (s.length==2) {
			FileSystem.directoryOnly=true;
		}
		FileSystem fs = new FileSystem (s[0]);
		//TextArea = sorted file list
		//Pie chart = file groups
		//top 100%,50%,20%,10%,5%
		//sort by LENGTH, EXTENTION, FOLDER
	}
	static boolean directoryOnly=false;
	Vector allFiles = new Vector();
	Vector allDir = new Vector();
	
	public FileSystem (String s) {
		includeFile (new File (s));
		System.out.println ("files found =" + allFiles.size());
		int a, b;
		long t = 0;
		for (a = 0; a < allFiles.size(); a++) {
			String f = (String)allFiles.elementAt (a);
			f = f.substring (f.lastIndexOf (" ") + 1);
			try {
				t = t + Integer.parseInt (f);
			}
			catch (NumberFormatException NFE) {
			}
		}
		System.out.println ("total length =" + t);
		while (allFiles.size() > 0) {
			String f = (String)allFiles.elementAt (0);
			int fi = 0;
			try {
				String len=f.substring(0,f.indexOf(" ")).trim();
				fi=Integer.parseInt(len);
			}
			catch (NumberFormatException NFE) {
			}
			for (a = 1; a < allFiles.size(); a++) {
				String f2 = (String)allFiles.elementAt (a);
				int f2i = 0;
				try {
					f2i = Integer.parseInt (f2.substring (0,f2.indexOf (" ") ).trim());
				}
				catch (NumberFormatException NFE) {
				}
				if (f2i < fi) {
					f = f2;
					fi = f2i;
				}
			}
			System.out.println (f);
			allFiles.removeElement (f);
		}
	}
	
	public long includeFile (File f) {
		if (f.exists()) {
			long total=0;
			if (f.isFile()) {
				String len=+f.length()+"            ";
				len=len.substring(0,12);
				if (!directoryOnly) allFiles.addElement (len+f.getAbsolutePath());
				total=f.length();
			}
			else if (f.isDirectory()) {
				File fl[] = f.listFiles();
				for (int a = 0; a < fl.length; a++) {
					total+=includeFile (fl[a]);
				}
				String len=total+"            ";
				len=len.substring(0,12);
				if (directoryOnly) allFiles.addElement (len+f.getAbsolutePath());
			}
			return total;
		}
		return 0;
	}
}

