package aj.glad;


/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Skill {
	//skills
	String name;
	//owner
	int type;
	//real, school, performance, training
	int supprise, speed;
	//natural skills
	int sword, polarm, club, handtohand;
	//attack skills
	int dodge, block, parry, entangle, simo;

	/**
	 *  Description of the Field 
	 */
	public static String DEFAULT = "name:null REAL speed:10 sword:10 polarm:10 club:10 handtohand:10 dodge:10 block:10 parry:10 simo:10 supprise:10 entangle:10";

	static int REAL = 0, SCHOOL = 1, PERFORMANCE = 2, TRAINING = 3;

	private static double LEARNCURVE = 2;


	//defense skills
	/**
	 *  Description of the Method 
	 *
	 *@param  skill  Description of Parameter 
	 *@return        Description of the Returned Value 
	 */
	public int improve(int skill) {
		if (Math.random() * 100 * LEARNCURVE > skill) {
			return 1;
		}
		else {
			return 0;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String s = "name:" + name + " ";
		if (type == REAL) {
			s += "REAL ";
		}
		else if (type == SCHOOL) {
			s += "SCHOOL ";
		}
		else if (type == PERFORMANCE) {
			s += "PERFORMANCE ";
		}
		else if (type == TRAINING) {
			s += "TRAINING ";
		}
		s += "speed:" + speed + " sword:" + sword + " polarm:" + polarm + " club:" + club + " handtohand:" + handtohand + " dodge:" + dodge + " block:" + block + " parry:" + parry + " simo:" + simo + " supprise:" + supprise + " entangle:" + entangle;
		return s;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Skill parse(String s) {
		String t;
		Skill p = new Skill();
		if (s.indexOf("REAL") >= 0) {
			p.type = REAL;
		}
		else if (s.indexOf("SCHOOL") >= 0) {
			p.type = SCHOOL;
		}
		else if (s.indexOf("PERFORMANCE") >= 0) {
			p.type = PERFORMANCE;
		}
		else if (s.indexOf("TRAINING") >= 0) {
			p.type = TRAINING;
		}
		if (s.indexOf("name:") >= 0) {
			t = s.substring(s.indexOf("name:") + 5).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.name = t;
		}
		if (s.indexOf("supprise:") >= 0) {
			t = s.substring(s.indexOf("supprise:") + 9).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.supprise = Integer.parseInt(t);
		}
		if (s.indexOf("speed:") >= 0) {
			t = s.substring(s.indexOf("speed:") + 6).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.speed = Integer.parseInt(t);
		}
		if (s.indexOf("sword:") >= 0) {
			t = s.substring(s.indexOf("sword:") + 6).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.sword = Integer.parseInt(t);
		}
		if (s.indexOf("polarm:") >= 0) {
			t = s.substring(s.indexOf("polarm:") + 7).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.polarm = Integer.parseInt(t);
		}
		if (s.indexOf("club:") >= 0) {
			t = s.substring(s.indexOf("club:") + 5).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.club = Integer.parseInt(t);
		}
		if (s.indexOf("handtohand:") >= 0) {
			t = s.substring(s.indexOf("handtohand:") + 11).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.handtohand = Integer.parseInt(t);
		}
		if (s.indexOf("dodge:") >= 0) {
			t = s.substring(s.indexOf("dodge:") + 6).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.dodge = Integer.parseInt(t);
		}
		if (s.indexOf("block:") >= 0) {
			t = s.substring(s.indexOf("dodge:") + 6).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.block = Integer.parseInt(t);
		}
		if (s.indexOf("parry:") >= 0) {
			t = s.substring(s.indexOf("parry:") + 6).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.parry = Integer.parseInt(t);
		}
		if (s.indexOf("entangle:") >= 0) {
			t = s.substring(s.indexOf("entangle:") + 9).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.entangle = Integer.parseInt(t);
		}
		if (s.indexOf("simo:") >= 0) {
			t = s.substring(s.indexOf("simo:") + 5).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.simo = Integer.parseInt(t);
		}
		return p;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
	}

}
