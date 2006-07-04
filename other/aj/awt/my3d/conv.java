package aj.awt.my3d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import aj.misc.Stuff;

/**
 * Description of the Class converts files into GML format
 * 
 * @author judda
 * @created July 21, 2000
 */
public class conv {
	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String all = "";
			String hold = "";
			while (true) {
				String t = br.readLine();
				if (t == null) {
					break;
				}
				hold += t + " ";
				if (hold.length() > 10000) {
					all += hold + " ";
					hold = "";
				}
			}
			all += hold;
			String t[] = Stuff.getTokens(all, " ");
			int a = 0;
			while (t.length > a) {
				System.out.println(" poly [ color [r 255 g 0 b 0 ] ");
				System.out.println(" point [ x \"" + t[a] + "\" y \""
						+ t[a + 1] + "\" z \"" + t[a + 2] + "\" nx \""
						+ t[a + 3] + "\" ny \"" + t[a + 4] + "\" nz \""
						+ t[a + 5] + "\" ] ");
				a = a + 6;
				System.out.println(" point [ x \"" + t[a] + "\" y \""
						+ t[a + 1] + "\" z \"" + t[a + 2] + "\" nx \""
						+ t[a + 3] + "\" ny \"" + t[a + 4] + "\" nz \""
						+ t[a + 5] + "\" ] ");
				a = a + 6;
				System.out.println(" point [ x \"" + t[a] + "\" y \""
						+ t[a + 1] + "\" z \"" + t[a + 2] + "\" nx \""
						+ t[a + 3] + "\" ny \"" + t[a + 4] + "\" nz \""
						+ t[a + 5] + "\" ] ");
				System.out.println(" ] ");

			}

		} catch (IOException IOE) {
		}
	}
}
