package aj.net;

import java.io.IOException;
import java.net.ServerSocket;

import aj.misc.Stuff;

/**
 * Description of the Class
 * 
 * @author judda
 * @created July 21, 2000
 */
public class ServerScan {

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		if (s.length != 2) {
			System.out
					.println("Usage: java aj.net.ServerScan <minport> <maxport>");
			System.exit(0);
		}
		int min = 0;
		int max = 0;
		try {
			min = Integer.parseInt(s[0]);
			max = Integer.parseInt(s[1]);
		} catch (Exception E) {
			System.out.println("Usage: java aj.net.SelfScan minport maxport");
			System.exit(0);
		}

		String result = "";
		int range = max - min;
		for (int a = min; a <= max; a++) {
			try {
				ServerSocket S = new ServerSocket(a);
				if (S == null) {
					throw new IOException();
				} else {
					result += a + ",";
				}
				if (a % (range / 10) == 0) {
					System.out.print(".");
				}
			} catch (IOException IOE) {
			}
		}
		String all[] = Stuff.getTokens(result, ", \t");
		result = "";
		for (int a = 0; a < all.length - 1; a++) {
			try {
				int f = Integer.parseInt(all[a]);
				int n = Integer.parseInt(all[a + 1]);
				if (n != f + 1) {
					result += f + ",";
				} else if (!result.endsWith("-")) {
					result += f + "-";
				}
			} catch (Exception E) {
			}
		}
		result += all[all.length - 1];
		System.out.println("");
		System.out.println("Available Server Ports=" + result);
	}
}
