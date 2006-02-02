package aj.io;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 *  Looks through binary files for ascii strings and prints them out. 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class GetText {
	int MINWORD = 4;
	String Fname;
	/**
	 *  Constructor for the GetText object 
	 *
	 *@param  FN  Description of Parameter 
	 *@param  mw  Description of Parameter 
	 */
	public GetText (String FN, int mw) {
		Fname = FN;
		MINWORD = mw;
	}
	/**
	 *  Constructor for the GetText object 
	 *
	 *@param  FN  Description of Parameter 
	 */
	public GetText (String FN) {
		Fname = FN;
	}
	/**
	 *  Main processing method for the GetText object 
	 */
	public void run() {
		boolean flag = false;
		BufferedReader IN;
		String buff = "";
		char c;
		int count = 1;
		try {
			IN = new BufferedReader (new FileReader (Fname));
			String all = "`-=[];\',./~!@#$%^&*()_+{}:\"<>? \t\n\r";
			while (IN.ready()) {
				c = (char)IN.read();
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || all.indexOf (c + "") >= 0) {
					flag = true;
					buff = buff + c;
				}
				else if (flag && buff.length() > MINWORD) {
					flag = false;
					System.out.println ((count++) + ">" + buff);
					buff = "";
				}
				else {
					buff = "";
					flag = false;
				}
			}
		}
		catch (IOException IOE3) {
			System.out.println ("myError: processing io error");
			System.exit (0);
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public static void main (String argv[]) {
		if (argv.length == 1) {
			GetText g = new GetText (argv[0]);
			g.run();
			return;
		}
		else if (argv.length == 2) {
			if (argv[1].toUpperCase().startsWith ("-W")) {
				try {
					GetText g = new GetText (argv[0], Integer.parseInt (argv[1].substring (2).trim()));
					g.run();
					return;
				}
				catch (NumberFormatException NFE) {
				}
			}
		}
		System.out.println ("FORMAT: java GetText <filename> [-w#]");
		System.out.println ("-w = word lenght (-w4= words at least size 4) default");
		System.exit (0);
	}
}

