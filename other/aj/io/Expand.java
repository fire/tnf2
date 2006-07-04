package aj.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Expand {

	public static void main(String s[]) {
		if (s.length == 0 || s.length > 2) {
			System.out.println("FORMAT java aj.io.Expand <scale> [filename>]");
			System.exit(0);
		}
		try {
			Vector v = new Vector();
			BufferedReader br = null;
			if (s.length == 2)
				br = new BufferedReader(new FileReader(s[1]));
			else
				br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String t = br.readLine();
				if (t == null)
					break;
				v.addElement(t);
			}
			try {
				double scale = Double.parseDouble(s[0]);
				for (int a = 0; a < v.size(); a++) {
					String w = (String) v.elementAt(a);
					int ab = ((int) ((a + 1) * scale)) - ((int) ((a) * scale));
					// System.err.println("ab="+ab+" a1="+(int)((a+1)*scale)+"
					// a2="+(int)(a*scale));
					for (int b = 0; b < ab; b++) {
						for (int c = 0; c < w.length(); c++) {
							int cd = ((int) ((c + 1) * scale))
									- ((int) (c * scale));
							// System.err.println("cd="+cd);
							for (int d = 0; d < cd; d++) {
								System.out.print(w.charAt(c));
							}
						}
						System.out.println("");
					}
				}
			} catch (NumberFormatException NFE) {
			}
		} catch (IOException ioe) {
		}
	}
}
/*
 * scale = 1.5 (int)(scale*a) (int)(scale*(a-1)) 1 = 1 1 0 1 2 = 2 3 1 2 3 = 1 4
 * 3 1 4 = 2 6 4 2 5 = 1 7 6 1 6 = 2 9 7 2
 * 
 */
