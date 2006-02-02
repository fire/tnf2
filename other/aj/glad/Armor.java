package aj.glad;

import aj.misc.Stuff;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Armor extends Item {

	int location;
	int type;
	int protection;
	static int HEAD = 0, BODY = 1, ARM = 2, LEG = 3;
	static int PLATE = 0, CHAIN = 1, LEATHER = 2, UNKNOWN = 3;

	static String[] allArmor = {
			"Bracer lbs2  gp20 hp100 ARMOR PLATE ARM prot40", 
			"Chain_Armor lbs15 gp150 hp250 ARMOR CHAIN BODY prot30"};


	/**
	 *  Sets the Type attribute of the Armor object 
	 *
	 *@param  a  The new Type value 
	 */
	public void setType(int a) {
		type = a;
	}


	/**
	 *  Sets the Location attribute of the Armor object 
	 *
	 *@param  a  The new Location value 
	 */
	public void setLocation(int a) {
		location = a;
	}


	/**
	 *  Sets the Protection attribute of the Armor object 
	 *
	 *@param  a  The new Protection value 
	 */
	public void setProtection(int a) {
		protection = a;
	}


	/**
	 *  Gets the Location attribute of the Armor object 
	 *
	 *@return    The Location value 
	 */
	public int getLocation() {
		return location;
	}


	/**
	 *  Gets the Protection attribute of the Armor object 
	 *
	 *@return    The Protection value 
	 */
	public int getProtection() {
		return protection;
	}


	/**
	 *  Gets the Type attribute of the Armor object 
	 *
	 *@return    The Type value 
	 */
	public int getType() {
		return type;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String s = name + " lbs" + getWeight() + " gp" + getCost() + " hp" + getMaxHitPoints();
		s += " ARMOR ";
		if (getType() == PLATE) {
			s += "PLATE ";
		}
		else if (getType() == CHAIN) {
			s += "CHAIN ";
		}
		else if (getType() == LEATHER) {
			s += "LEATHER ";
		}

		if (getLocation() == BODY) {
			s += "BODY ";
		}
		else if (getLocation() == HEAD) {
			s += "HEAD ";
		}
		else if (getLocation() == LEG) {
			s += "LEG ";
		}
		else if (getLocation() == ARM) {
			s += "ARM ";
		}

		s += "prot" + getProtection();
		return s;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Armor parse(String s) {
		if (s.indexOf("ARMOR") == -1) {
			return null;
		}
		s = Stuff.superTrim(s);
		try {
			Armor w = new Armor();
			if (s.indexOf("PLATE") >= 0) {
				w.setType(PLATE);
			}
			else if (s.indexOf("CHAIN") >= 0) {
				w.setType(CHAIN);
			}
			else if (s.indexOf("LEATHER") >= 0) {
				w.setType(LEATHER);
			}
			else {
				w.setType(UNKNOWN);
			}

			if (s.indexOf("ARM") >= 0) {
				w.setLocation(ARM);
			}
			else if (s.indexOf("LEG") >= 0) {
				w.setLocation(LEG);
			}
			else if (s.indexOf("HEAD") >= 0) {
				w.setLocation(HEAD);
			}
			else if (s.indexOf("BODY") >= 0) {
				w.setLocation(BODY);
			}

			w.setName(s.substring(0, s.indexOf(" ")));
			String t;
			s = s + " ";
			t = s.substring(s.indexOf("prot") + 4).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			w.setProtection(Integer.parseInt(t));
			t = s.substring(s.indexOf("lbs") + 3).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			w.setWeight(Integer.parseInt(t));
			t = s.substring(s.indexOf("gp") + 2).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			w.setCost(Integer.parseInt(t));
			t = s.substring(s.indexOf("hp") + 2).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			w.setMaxHitPoints(Integer.parseInt(t));
			w.setHitPoints(w.getMaxHitPoints());
			return w;
		}
		catch (NumberFormatException NFE) {
		}
		return null;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		int a;
		for (a = 0; a < allArmor.length; a++) {
			Armor A = Armor.parse(allArmor[a]);
			System.out.println("<" + allArmor[a] + "> <" + A.toString() + ">");
		}
	}

}

