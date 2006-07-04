package aj.testing;

import aj.misc.Stuff;

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
public class odds {

	/**
	 * Odds of winning lottery. Chance of 54 choose 6 correctly.
	 * 
	 * @param s
	 *            Description of Parameter
	 */

	// P chose N = P! /N!/(P-N)!
	public static void main(String s[]) {
		if (s.length < 2) {
			System.out.println("Format: java odds <choices> <num choosen>");
			System.exit(0);
		}
		try {
			System.out.println("choose("
					+ s[0]
					+ ","
					+ s[1]
					+ ")="
					+ Stuff.engNum(choose(Integer.parseInt(s[0]), Integer
							.parseInt(s[1]))));
		} catch (NumberFormatException NFE) {
		}
	}

	public static double choose(int avail, int choose) {
		if (choose < 1 || avail < 1 || choose >= avail)
			return 1;
		double total = 1;
		for (int a = 0; a < choose; a++) {
			total = total * (avail - a);
		}
		for (int a = 0; a < choose; a++) {
			total /= (a + 1);
		}
		return total;
	}
}
