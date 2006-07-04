package aj.glad;

import java.util.Vector;

/**
 * How a gladator will fight. Ratio of each type of attack,pickup,slay Ratio of
 * each type of dodge,block,parry,simo Slay list Pickup/no Pickup list
 * 
 * @author judda
 * @created April 12, 2000
 */

public class Plan {
	// alternative attacks
	String name;

	// String description;
	// kill randomly use attack to kill helpless (passout) gladators
	int chanceKill, bloodKill;

	// %damage before surrender
	int surrender;

	// you will only pick up a weapon that is better than yours (more HD)
	// you will only pick up a weapon that is not nopick type
	int pickupchance;

	// chance of doing a pick up 100 = always try (only if available)
	String nopickup;

	// types you will not pick up
	String preferredPickup;

	// willing to drop other to pick up these
	int sameTarget;

	// 100=same until dead, 0 = never same if possible
	Vector bloodFude;

	// list of players/owners you will choose to attack first
	int targetStrength;

	// roll 0-100
	boolean targetWeak;

	// true = weakest false= strongest
	int antiblock, antidodge, antiparry, head, arm, leg, body, ddouble;

	// special hit rate add to 100
	int parry, block, dodge, simo, none;

	static int WEAKESTTARGET = 0, NORMALTARGET = 1, STRONGESTTARGET = 2,
			SAMETARGET = 3;

	// targetselection
	static int DODGE = 0, PARRY = 1, BLOCK = 2, NONE = 3, SIMO = 4;

	// defense
	static int ANTIDODGE = 0, ANTIPARRY = 1, ANTIBLOCK = 2, HEAD = 3, BODY = 4,
			ARM = 5, LEG = 6, DDOUBLE = 7;

	// special attack
	static int PICKUP = 0, KILL = 1, ATTACK = 2;

	static String DEFAULT = "name:default randomkill:0 bloodkill:0 surrender:90 sametarget:85 target:weak0 antiblock:12 antidodge:12 antiparry:12 head:12 arm:12 leg:12 body:12 double:12 doparry:20 doblock:20 dododge:20 donone:20 dosimo:20";

	/**
	 * Gets the NextDefense attribute of the Plan object
	 * 
	 * @return The NextDefense value
	 */
	public int getNextDefense() {
		int tot = (int) (Math.random() * 100);
		if (tot < parry) {
			return PARRY;
		}
		if (tot < parry + dodge) {
			return DODGE;
		}
		if (tot < parry + dodge + block) {
			return BLOCK;
		}
		if (tot < parry + dodge + block + simo) {
			return SIMO;
		} else {
			return NONE;
		}
	}

	/**
	 * Gets the NextAttack attribute of the Plan object
	 * 
	 * @return The NextAttack value
	 */
	public int getNextAttack() {
		return ATTACK;
	}

	/**
	 * Gets the NextTarget attribute of the Plan object
	 * 
	 * @return The NextTarget value
	 */
	public int getNextTarget() {
		return SAMETARGET;
	}

	// int antiblock,antidodge,antiparry,head,arm,leg,body;//special hit rate
	// add to 100
	/**
	 * Gets the NextSpecial attribute of the Plan object
	 * 
	 * @return The NextSpecial value
	 */
	public int getNextSpecial() {
		int tot = (int) (Math.random() * 100);
		if (tot < antidodge) {
			return ANTIDODGE;
		} else if (tot < antidodge + antiblock) {
			return ANTIBLOCK;
		} else if (tot < antidodge + antiblock + antiparry) {
			return ANTIPARRY;
		} else if (tot < antidodge + antiblock + antiparry + head) {
			return HEAD;
		} else if (tot < antidodge + antiblock + antiparry + head + arm) {
			return ARM;
		} else if (tot < antidodge + antiblock + antiparry + head + arm + leg) {
			return LEG;
		} else if (tot < antidodge + antiblock + antiparry + head + arm + leg
				+ body) {
			return BODY;
		} else {
			return DDOUBLE;
		}
	}

	// add to 100 chance of each when attacked.
	/**
	 * Description of the Method
	 * 
	 * @param i
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean emptyPickup(Item i) {
		return false;
	}

	/**
	 * Description of the Method
	 * 
	 * @param i
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean dropPickup(Item i) {
		return false;
	}

	/**
	 * Description of the Method
	 */
	public void normalize() {
		int tot = parry + block + dodge + simo + none;
		System.out.println(this);
		if (tot != 100) {
			if (tot == 0) {
				tot = 100;
				parry = 20;
				dodge = 20;
				block = 20;
				simo = 20;
				none = 20;
			}
			parry = parry * 100 / tot;
			dodge = dodge * 100 / tot;
			block = block * 100 / tot;
			simo = simo * 100 / tot;
			none = none * 100 / tot;
		}

		tot = antiblock + antidodge + antiparry + head + arm + leg + body
				+ ddouble;
		if (tot != 100) {
			if (tot == 0) {
				tot = 100;
				antiblock = 12;
				antidodge = 12;
				antiparry = 12;
				head = 12;
				arm = 12;
				leg = 12;
				body = 12;
				ddouble = 12;
			}
			antiblock = antiblock * 100 / tot;
			antidodge = antidodge * 100 / tot;
			antiparry = antiparry * 100 / tot;
			head = head * 100 / tot;
			arm = arm * 100 / tot;
			leg = leg * 100 / tot;
			body = body * 100 / tot;
			ddouble = ddouble * 100 / tot;
		}
		System.out.println(this);
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean bloodFude(String s) {
		return false;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		String s;
		s = "name:" + name + " randomKill:" + chanceKill + " bloodKill:"
				+ bloodKill + " surrender:" + surrender;
		s += " sametarget:" + sameTarget;
		if (targetWeak) {
			s += " target:weak" + targetStrength;
		} else {
			s += " target:strong" + targetStrength;
		}
		s += " antiblock:" + antiblock + " antidodge:" + antidodge
				+ " antiparry:" + antiparry + " head:" + head + " arm:" + arm;
		s += " leg:" + leg + " body:" + body + " parry:" + parry + " block:"
				+ block + " dodge:" + dodge + " none:" + none + " simo:" + simo;
		return s;
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Plan parse(String s) {
		Plan p = new Plan();
		String t;
		if (s.indexOf("name:") >= 0) {
			t = s.substring(s.indexOf("randomkill:") + 5).trim() + " ";
			t = t.substring(0, t.indexOf(" ")).trim();
			p.name = t;
		}
		if (s.indexOf("randomkill:") >= 0) {
			t = s.substring(s.indexOf("randomkill:") + 11).trim() + " ";
			t = t.substring(0, t.indexOf(" ")).trim();
			p.chanceKill = Integer.parseInt(t);
		}
		if (s.indexOf("bloodkill:") >= 0) {
			t = s.substring(s.indexOf("bloodkill:") + 10).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.bloodKill = Integer.parseInt(t);
		}
		if (s.indexOf("surrender:") >= 0) {
			t = s.substring(s.indexOf("surrender:") + 10).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.surrender = Integer.parseInt(t);
		}
		if (s.indexOf("sameTarget:") >= 0) {
			t = s.substring(s.indexOf("sameTarget:") + 11).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.sameTarget = Integer.parseInt(t);
		}
		if (s.indexOf("target:weak") >= 0) {
			t = s.substring(s.indexOf("target:weak") + 11).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.targetStrength = Integer.parseInt(t);
			p.targetWeak = true;
		}
		if (s.indexOf("target:strong") >= 0) {
			t = s.substring(s.indexOf("target:strong") + 13).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.targetStrength = Integer.parseInt(t);
			p.targetWeak = false;
		}
		if (s.indexOf("antiblock:") >= 0) {
			t = s.substring(s.indexOf("antiblock:") + 10).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.antiblock = Integer.parseInt(t);
		}
		if (s.indexOf("antidodge:") >= 0) {
			t = s.substring(s.indexOf("antidodge:") + 10).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.antidodge = Integer.parseInt(t);
		}
		if (s.indexOf("antiparry:") >= 0) {
			t = s.substring(s.indexOf("antiparry:") + 10).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.antiparry = Integer.parseInt(t);
		}
		if (s.indexOf("head:") >= 0) {
			t = s.substring(s.indexOf("head:") + 5).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.head = Integer.parseInt(t);
		}
		if (s.indexOf("arm:") >= 0) {
			t = s.substring(s.indexOf("arm:") + 4).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.arm = Integer.parseInt(t);
		}
		if (s.indexOf("leg:") >= 0) {
			t = s.substring(s.indexOf("leg:") + 4).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.leg = Integer.parseInt(t);
		}
		if (s.indexOf("body:") >= 0) {
			t = s.substring(s.indexOf("body:") + 5).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.body = Integer.parseInt(t);
		}
		if (s.indexOf("double:") >= 0) {
			t = s.substring(s.indexOf("double:") + 7).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.ddouble = Integer.parseInt(t);
		}
		if (s.indexOf("doblock:") >= 0) {
			t = s.substring(s.indexOf("doblock:") + 8).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.block = Integer.parseInt(t);
		}
		if (s.indexOf("doparry:") >= 0) {
			t = s.substring(s.indexOf("doparry:") + 8).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.parry = Integer.parseInt(t);
		}
		if (s.indexOf("dododge:") >= 0) {
			t = s.substring(s.indexOf("dododge:") + 8).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.dodge = Integer.parseInt(t);
		}
		if (s.indexOf("none:") >= 0) {
			t = s.substring(s.indexOf("donone:") + 7).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.none = Integer.parseInt(t);
		}
		if (s.indexOf("simo:") >= 0) {
			t = s.substring(s.indexOf("dosimo:") + 7).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			p.simo = Integer.parseInt(t);
		}

		// nopickup string
		// pickup string
		// bloodfude string
		p.normalize();
		return p;
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		String m = DEFAULT;
		Plan.parse(m);
	}

}

/*
 * default name:default randomkill:0 bloodkill:0 surrender:90 sametarget:85
 * target:weak0 antiblock:20 antidodge:50 antiparry:10 head:10 arm:0 leg:0
 * body:10 parry:10 block:10 dodge:30 none:50 simo:0 name:special1 randomkill:0
 * bloodkill:50 surrender:90 nopickup:ENTANGLE,HANDTOHAND preferredpick:SWORD
 * sametarget:85 bloodfude:Flandar,Sparticus target:weak90 antiblock:70
 * antidodge:20 antiparry:10 head:0 arm:0 leg:0 body:0 parry:10 block:20
 * dodge:40 none:20 simo:10
 */

/*
 * rules of combat kill: losers auto killed 0, killing allowed 1, killing
 * forbidden 2 teams: 2, 3, 4 ,5 , any 6 glads per team: 1, 2, 3, 4, 5, any 6
 * glads number same per team : true, false glad performance same per team:
 * +/-10% animals alowed: true, false
 */

/*
 * animals always on separate team.
 */
