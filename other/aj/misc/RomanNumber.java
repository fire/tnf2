package aj.misc;


/**
 *  RomanNumber system. Create, print and get value of roman numbers. 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class RomanNumber extends Number {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String rep = "";
	//MDCLXVI
	private String fix[] = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};
	private int find[] = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};

	private int x;


	/**
	 *  Constructor for the RomanNumber object 
	 *
	 *@param  x  Description of Parameter 
	 */
	public RomanNumber(int x) {
		this.x = x;
		rep = encode(x);
	}


	/**
	 *  Creates a Roman Number from a String. The string can be either a parsable 
	 *  integer number (75), or a number in Roman Number format (ie XXIV). Roman 
	 *  Numbers end at 1000, so large numbers like 20K will be written with 20 Ms. 
	 *  Roman Numbers are best in the range of 4K-1. I Don't believe their is a 
	 *  zero. 
	 *
	 *@param  s                          Description of Parameter 
	 *@exception  NumberFormatException  Description of Exception 
	 */

	public RomanNumber(String s) throws NumberFormatException {
		try {
			x = Integer.parseInt(s);
			rep = encode(x);
			return;
		}
		catch (NumberFormatException NFE) {
		}
		s = s.toUpperCase().trim();
		x = 0;
		if (s.indexOf("IIII") >= 0 || 
				s.indexOf("VV") >= 0 || 
				s.indexOf("XXXX") >= 0 || 
				s.indexOf("LL") >= 0 || 
				s.indexOf("CCCC") >= 0 || 
				s.indexOf("DD") >= 0) {
			throw new NumberFormatException();
		}
		for (int a=fix.length - 1; a >= 0; a--) {
			while (s.startsWith(fix[a])) {
				s = s.substring(s.indexOf(fix[a]) + fix[a].length());
				x = x + find[a];
			}
		}
		if (s.length() > 0) {
			throw new NumberFormatException();
		}
		rep = encode(x);
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public byte byteValue() {
		return new Double(x).byteValue();
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public double doubleValue() {
		return new Double(x).doubleValue();
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public float floatValue() {
		return new Double(x).floatValue();
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public int intValue() {
		return new Double(x).intValue();
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public long longValue() {
		return new Double(x).longValue();
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public short shortValue() {
		return new Double(x).shortValue();
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		return rep;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  x  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	private String encode(int x) {
		String r = "";
		for (int a=fix.length - 1; a > -1; a--) {
			while (x >= find[a]) {
				x -= find[a];
				r = r + fix[a];
			}
		}
		return r;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		for (int a = 0; a < s.length; a++) {
			try {
				System.out.println("Test RomanNumber.encode(" + s[a] + ") = " + new RomanNumber(s[a]).toString());
			}
			catch (NumberFormatException e) {
			}
		}
		System.out.println("Test RomanNumber.encode(147) = " + new RomanNumber(147).toString());
		System.out.println("Test RomanNumber.encode(6147) = " + new RomanNumber(6147).toString());
		System.out.println("Test RomanNumber.encode(\"MM\") = " + new RomanNumber("MM").toString());
		System.out.println("Test RomanNumber.encode(1997) = " + new RomanNumber(1997).toString());
	}
}
