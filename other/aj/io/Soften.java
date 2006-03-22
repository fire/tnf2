package aj.io;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Soften {
	
	public static void main (String s[]) {
		if (s.length == 0 || s.length > 1) {
			System.out.println ("FORMAT java aj.io.Soften [<filename>]");
			System.exit (0);
		}
		try {
			Vector v = new Vector();
			BufferedReader br = null;
			if (s.length == 1)br = new BufferedReader (new FileReader (s[0]));
			else br = new BufferedReader (new InputStreamReader (System.in));
			int maxlen = 0;
			while (true) {
				String t = br.readLine();
				if (t == null)break;
				v.addElement (t);
				maxlen = Math.max (t.length(), maxlen);
			}
			char c[][] = new char[v.size()][maxlen];
			char c2[][] = new char[v.size()][maxlen];
			for (int a = 0; a < v.size(); a++) {
				String ss = (String)v.elementAt (a);
				for (int b = 0; b < maxlen; b++) {
					if (ss.length() > b)c[a][b] = ss.charAt (b);
					else c[a][b] = ' ';
				}
			}
			for (int a = 0; a < c.length; a++) {
				for (int b = 0; b < c[a].length; b++) {
					String all = "" + c[a][b];
					if (a - 1 >= 0 && b - 1 >= 0)all += c[a - 1][b - 1];
					if (a - 1 >= 0)all += c[a - 1][b];
					if (a - 1 >= 0 && b + 1 < c[a].length)all += c[a - 1][b + 1];
					if (b - 1 >= 0)all += c[a][b - 1];
					if (b + 1 < c[a].length)all += c[a][b + 1];
					if (a + 1 < c.length && b - 1 >= 0)all += c[a + 1][b - 1];
					if (a + 1 < c.length)all += c[a + 1][b];
					if (a + 1 < c.length && b + 1 < c[a].length)all += c[a + 1][b + 1];
					c2[a][b] = mostFreq (all);
				}
			}
			for (int a = 0; a < c.length; a++) {
				for (int b = 0; b < c[a].length; b++) {
					System.out.print (c2[a][b]);
				}
				System.out.println ("");
			}
		}
		catch (IOException ioe) {
		}
	}
	
	public static char mostFreq (String all) {
		int count[] = new int[10];
		for (int a = 0; a < all.length(); a++) {
			for (int b = 0; b < all.length(); b++) {
				if (all.charAt (a) == all.charAt (b))count[a]++;
			}
		}
		int max = 0;
		int ind = 0;
		for (int a = 0; a < count.length; a++) {
			if (count[a] > max) {
				max = count[a];
				ind = a;
			}
		}
		return all.charAt (ind);
	}
}

