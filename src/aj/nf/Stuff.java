package aj.nf;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  Some tools that are general and may be used later. 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Stuff {

	/**
	 *  Returns String[] of all tokens seperated with defalut white space. default 
	 *  tokens = <spc>, <tab><ret>)("';: 
	 *
	 *@param  s  Description of Parameter 
	 *@return    The Tokens value 
	 */
	public static String[] getTokens(String s) {
		return getTokens(s, " ,\t\n)(\"\';:");
	}


	/**
	 *  Returns String[] of all tokens seperated. 
	 *
	 *@param  s   Description of Parameter 
	 *@param  to  Description of Parameter 
	 *@return     The Tokens value 
	 */
	public static String[] getTokens(String s, String to) {
		Vector v = new Vector();
		StringTokenizer st = new StringTokenizer(s, to);
		while (st.hasMoreTokens()) {
			v.addElement(st.nextToken());
		}
		String[] t = new String[v.size()];
		v.copyInto(t);
		return t;
	}


	public static String engNum(double d) {return money(d,0).substring(1);}
	public static String engNum(double d,int t) {return money(d,0).substring(1);}

	public static String money(double d) {return money(d,0);}
	public static String money(double d, int t) {
		if (Math.abs(trunc(d,2))<.01) return "$0.0";	
		double od = d;
		String head = "$";
		if (d < 0) {
			head = "-" + head;
			d *= -1;
		}
		int penney = (int) ((d - trunc(d, 0)) * 100);
		int dol;
		String s = "." + penney + (penney < 10 ? "0" : "");
		while (d > 999) {
			dol = (int) (d - trunc(d / 1000, 0) * 1000);
			d = trunc(d / 1000, 0);
			s = "," + (dol < 100 ? "0" : "") + (dol < 10 ? "0" : "") + dol + s;
		}
		dol = (int) d;
		s = dol + s;
		return head + s;
	}


	/**
	 *  degree to Radian converter 
	 */
	public static double dtr(double d) {
		return d * 3.14159 / 180;
	}

	/**
	 *  Radian to degree converter 
	 */
	public static double rtd(double d) {
		return d / 3.14159 * 180;
	}


	/**
	 *  Remove all substrings in string. 
	 *
	 *@param  old  Description of Parameter 
	 *@param  sub  Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public static String removeSubstring(String old, String sub) {
		String a = old;
		String b = sub;
		while (a.indexOf(b) >= 0) {
			a = a.substring(0, a.indexOf(b)) + a.substring(a.indexOf(b) + b.length());
		}
		return a;
	}


	public static double trunc(double d, int place) {
		double t = Math.pow(10, place);
		double x = Math.floor(d * t);
		if (d < 0) {
			x = Math.ceil(d * t);
		}
		return x / t;
	}


	/**
	 *  This removes multiple white space in a string. First string is source, 
	 *  second string is white space except " " which is assumed. 
	 *
	 *@param  s      Description of Parameter 
	 *@param  chars  Description of Parameter 
	 *@return        Description of the Returned Value 
	 */
	public static String superTrim(String s, String chars) {
		while (chars.indexOf(" ") >= 0) {
			chars = chars.substring(0, chars.indexOf(" ")) + chars.substring(chars.indexOf(" ") + 1);
		}
		int a;
		for (a = 0; a < chars.length(); a++) {
			char c = chars.charAt(a);
			String res="";
			String sub="";
			int count=0;
			int lastind=0;
			while (s.indexOf(c,lastind) >=0 && s.indexOf(c,lastind)>= lastind) {
				int ll=s.indexOf(c,lastind);
				sub+=s.substring(lastind,ll) + " ";
				lastind=ll+1;
				count++;
				if (count>100) {
					res+=sub;sub="";count=0;
				}
			}
			s=res+sub+s.substring(lastind).trim();
		}
		String res="";
		String sub="";
		int count=0;
		int lastind=0;
		while (s.indexOf("  ",lastind) >= lastind) {
			int ll=s.indexOf("  ",lastind);
			sub+=s.substring(lastind,ll);
			lastind=ll+1;
			count++;
			if (count>100){
				res+=sub;
				sub="";
				count=0;
			}
		}
		res+=sub+s.substring(lastind);
		return res.trim();
	}


	/**
	 *  Supertrims with default tab and ret. 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static String superTrim(String s) {
		return superTrim(s, "\n\t");
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		String test = "this    is  a \t \t\t \n\n\n test \n\t  of the+ ::EMS";
		System.out.println("<" + test + ">" + " <" + superTrim(test) + ">");
		System.out.println("<" + test + ">" + " <" + superTrim(test, "\n\t:+") + ">");
		System.out.println("penney " + money(-1118.304, 2));
		System.out.println("penney " + money(-223.604, 2));
		System.out.println("penney " + money(2500.00, 2));
		System.out.println("penney " + money(1507.400, 2));

	}

	public static double parseDouble(String s) throws NumberFormatException {
		return new Double(s).doubleValue();	
	}

}
