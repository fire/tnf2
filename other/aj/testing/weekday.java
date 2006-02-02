package aj.testing;

import java.util.Date;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class weekday {
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		long sec = 1000;
		long min = sec * 60;
		long hour = min * 60;
		long day = hour * 24;
		long year = (long) (day * 365.24219);

		Date d = new Date();
		String l = (d + "").toLowerCase();
		if (l.indexOf("mon") >= 0) {
			System.out.println("MONDAY: " + d);
		}
		if (l.indexOf("tue") >= 0) {
			System.out.println("TUESDAY: " + d);
		}
		if (l.indexOf("sun") >= 0) {
			System.out.println("SUNDAY: " + d);
		}
		if (l.indexOf("sat") >= 0) {
			System.out.println("SATURDAY: " + d);
		}
		d = new Date(year * 79 + 2 * day);
		System.out.println(d + "= " + (year * 79 + 2 * day));
	}
}
