//simo on borrowed attacks?
//cannot defend if not active
//cannot attack not active only kill
//
package aj.glad;

import java.util.Vector;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Battle {

	Vector teams;

	Vector glads;

	/**
	 * Constructor for the Battle object
	 * 
	 * @param v
	 *            Description of Parameter
	 */
	public Battle(Vector v) {
		teams = v;
		glads = new Vector();
		for (int a = 0; a < teams.size(); a++) {
			Vector t = (Vector) teams.elementAt(a);
			for (int b = 0; b < t.size(); b++) {
				Gladator g = (Gladator) t.elementAt(b);
				g.reset();
				glads.addElement(g);
			}
		}
	}

	/**
	 * Gets the Opponents attribute of the Battle object
	 * 
	 * @param att
	 *            Description of Parameter
	 * @return The Opponents value
	 */
	public Vector getOpponents(Gladator att) {
		Vector alldef = new Vector();
		// make list of all gladators not on my team
		for (int a = 0; a < teams.size(); a++) {
			Vector team = (Vector) teams.elementAt(a);
			if (team.contains(att)) {
				continue;
			} else {
				for (int b = 0; b < team.size(); b++) {
					Gladator g = (Gladator) team.elementAt(b);
					alldef.addElement(g);
				}
			}
		}
		return alldef;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public boolean finished() {
		int teamcount = 0;
		for (int a = 0; a < teams.size(); a++) {
			Vector v = (Vector) teams.elementAt(a);
			int gladcount = 0;
			for (int b = 0; b < v.size(); b++) {
				Gladator g = (Gladator) glads.elementAt(a);
				if (g.getStatus() == Gladator.ACTIVE) {
					gladcount++;
				}
			}
			if (gladcount > 0) {
				teamcount++;
			}
		}
		// System.out.println("teams active ="+teamcount);
		return (teamcount < 2);
	}

	/**
	 * Description of the Method
	 */
	public void rollInit() {
		if (glads.size() < 1) {
			return;
		}
		for (int a = 0; a < glads.size(); a++) {
			Gladator g = (Gladator) glads.elementAt(a);
			g.rollInit();
		}
		Vector v = new Vector();
		while (glads.size() > 0) {
			Gladator g = (Gladator) glads.elementAt(0);
			for (int a = 0; a < glads.size(); a++) {
				Gladator n = (Gladator) glads.elementAt(a);
				if (g.getInit() < n.getInit()) {
					g = n;
				}
			}
			glads.removeElement(g);
			v.addElement(g);
		}
		glads = v;
	}

	/**
	 * Description of the Method
	 */
	public void begin() {
		for (int a = 0; a < glads.size(); a++) {
			Gladator att = (Gladator) glads.elementAt(a);
			att.reset();
		}
		while (!finished()) {
			// roll and sort by inits
			rollInit();
			// System.out.println("Rolled Init");
			for (int a = 0; a < glads.size(); a++) {
				Gladator att = (Gladator) glads.elementAt(a);
				if (att.getStatus() == Gladator.ACTIVE) {
					doAttack(att);
				}
			}
		}
		finalReport();
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 */
	public void doAttack(Gladator att) {
		while (att.getNumberOfAttacks() > 0
				&& att.getStatus() == Gladator.ACTIVE) {
			// ckeck kill option for attack
			// check pickup option for attack
			// check surrender option for attack
			att.setOpponent(getOpponents(att));
			Gladator def = att.getOpponent();
			if (def == null) {
				System.out.println(att.getName()
						+ " has nobody left to attack and ends his turn.");
				return;
			}
			def.chooseDef();
			if (def.defType() == Plan.SIMO) {
				doSimo(att, def);
			} else {
				doAttack(att, def);
			}
		}
		System.out.println(att.getName() + " turn ended");
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void doSimo(Gladator att, Gladator def) {
		def.setOpponent(att);
		def.rollSimo();
		if (def.getSimo()) {
			def.rollHit();
			att.rollHit();
		} else {
			att.rollHit();
		}
		if (att.getHit() && def.getSimo() && def.getHit()) {
			doDamage(att, def);
			doDamage(def, att);
			reportSimoHitHit(att, def);
		} else if (att.getHit() && (!def.getSimo() || !def.getHit())) {
			doDamage(att, def);
			reportSimoHitMiss(att, def);
		} else if (!att.getHit() && def.getSimo() && def.getHit()) {
			doDamage(def, att);
			reportSimoMissHit(att, def);
		} else if (!att.getHit() && (!def.getSimo() || !def.getHit())) {
			reportSimoMissMiss(att, def);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void doAttack(Gladator att, Gladator def) {
		att.rollHit();
		if (att.getHit()) {
			if (att.getSpecialHit() && att.getSpecialType() == Plan.ANTIDODGE
					&& def.defType() == Plan.DODGE) {
				def.rollDodge(att.getSpecialPoints());
			} else if (att.getSpecialHit()
					&& att.getSpecialType() == Plan.ANTIPARRY
					&& def.defType() == Plan.PARRY) {
				def.rollParry(att.getSpecialPoints());
			} else if (att.getSpecialHit()
					&& att.getSpecialType() == Plan.ANTIBLOCK
					&& def.defType() == Plan.BLOCK) {
				def.rollBlock(att.getSpecialPoints());
			} else if (def.defType == Plan.DODGE) {
				def.rollDodge();
			} else if (def.defType == Plan.PARRY) {
				def.rollParry();
			} else if (def.defType == Plan.BLOCK) {
				def.rollBlock();
			}
		}

		if (!att.getHit()) {
			reportMissed(att, def);
		} else if (def.defType() == Plan.DODGE && def.getDodged()) {
			reportDodged(att, def);
		} else if (def.defType() == Plan.DODGE) {
			doDamage(att, def);
			reportDodgedFailed(att, def);
		} else if (def.defType() == Plan.PARRY && def.getParried()) {
			doParryDamage(att, def);
			reportParried(att, def);
		} else if (def.defType() == Plan.PARRY) {
			doDamage(att, def);
			reportParriedFailed(att, def);
		} else if (def.defType() == Plan.BLOCK && def.getBlocked()) {
			doBlockDamage(att, def);
			reportBlocked(att, def);
		} else if (def.defType() == Plan.BLOCK) {
			doDamage(att, def);
			reportBlockedFailed(att, def);
		} else if (def.defType() == Plan.NONE) {
			doDamage(att, def);
			reportHit(att, def);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void doDamage(Gladator att, Gladator def) {
		int dam = att.rollDamage();
		if (att.getSpecialHit() && att.getSpecialType() == Plan.DDOUBLE) {
			dam *= 2;
		} else if (att.getSpecialHit() && att.getSpecialType() == Plan.ARM) {
			def.hurt((Math.random() * 2 > 1 ? Gladator.RIGHTARM
					: Gladator.LEFTARM), dam);
		} else if (att.getSpecialHit() && att.getSpecialType() == Plan.LEG) {
			def.hurt((Math.random() * 2 > 1 ? Gladator.RIGHTLEG
					: Gladator.LEFTLEG), dam);
		} else if (att.getSpecialHit() && att.getSpecialType() == Plan.BODY) {
			def.hurt(Gladator.BODY, dam);
		} else if (att.getSpecialHit() && att.getSpecialType() == Plan.HEAD) {
			def.hurt(Gladator.HEAD, dam);
		} else {
			def.hurt(dam);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void doBlockDamage(Gladator att, Gladator def) {
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void doParryDamage(Gladator att, Gladator def) {
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportDodged(Gladator att, Gladator def) {
		System.out.println(att.getName() + " attacked " + def.getName()
				+ " with his " + att.mainWeapon.getName() + " and was Dodged.");
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportParried(Gladator att, Gladator def) {
		System.out.println(att.getName() + " attacked " + def.getName()
				+ " with his " + att.mainWeapon.getName()
				+ " and was Parried by " + def.getName() + "'s "
				+ def.mainWeapon.getName() + ".");
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportBlocked(Gladator att, Gladator def) {
		System.out.println(att.getName() + " attacked " + def.getName()
				+ " with his " + att.mainWeapon.getName()
				+ " and was Blocked by " + def.getName() + "'s "
				+ def.mainWeapon.getName() + ".");
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportDodgedFailed(Gladator att, Gladator def) {
		System.out.print(att.getName() + " attacked and HIT " + def.getName()
				+ " with his " + att.mainWeapon.getName() + " after "
				+ def.getName() + "'s Dodge fail.");
		reportPlayerStatus(def);
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportParriedFailed(Gladator att, Gladator def) {
		System.out.print(att.getName() + " attacked and HIT " + def.getName()
				+ " with his " + att.mainWeapon.getName() + " after "
				+ def.getName() + "'s Parry fail.");
		reportPlayerStatus(def);
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportBlockedFailed(Gladator att, Gladator def) {
		System.out.print(att.getName() + " attacked and HIT " + def.getName()
				+ " with his " + att.mainWeapon.getName() + " after "
				+ def.getName() + "'s Block fail.");
		reportPlayerStatus(def);
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportHit(Gladator att, Gladator def) {
		System.out.print(att.getName() + " HIT " + def.getName() + " with his "
				+ att.mainWeapon.getName() + ".");
		reportPlayerStatus(def);
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportMissed(Gladator att, Gladator def) {
		System.out.println(att.getName() + " MISSED " + def.getName()
				+ " with his " + att.mainWeapon.getName() + ".");
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportSimoHitHit(Gladator att, Gladator def) {
		System.out.print(att.getName() + " and " + def.getName()
				+ " SIMO both HIT.");
		reportPlayerStatus(def, att);
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportSimoHitMiss(Gladator att, Gladator def) {
		System.out.print(att.getName() + " and " + def.getName()
				+ " SIMO, but only " + att.getName() + " HIT.");
		reportPlayerStatus(def);
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportSimoMissHit(Gladator att, Gladator def) {
		System.out.print(att.getName() + " and " + def.getName()
				+ " SIMO, but only " + def.getName() + " HIT.");
		reportPlayerStatus(att);
	}

	/**
	 * Description of the Method
	 * 
	 * @param att
	 *            Description of Parameter
	 * @param def
	 *            Description of Parameter
	 */
	public void reportSimoMissMiss(Gladator att, Gladator def) {
		System.out.println(att.getName() + " and " + def.getName()
				+ " SIMO, but both MISSED.");
	}

	/**
	 * Description of the Method
	 * 
	 * @param def
	 *            Description of Parameter
	 * @param att
	 *            Description of Parameter
	 */
	public void reportPlayerStatus(Gladator def, Gladator att) {
		System.out.println("  " + def.getName() + " took "
				+ def.getLastHitDamage() + " to his "
				+ def.getLastHitLocation() + " and " + att.getName() + " took "
				+ att.getLastHitDamage() + " to his "
				+ att.getLastHitLocation());
		if (att.getStatus() == Gladator.PASSOUT
				&& def.getStatus() == Gladator.PASSOUT) {
			System.out.println("Both " + att.getName() + " and "
					+ def.getName() + " pass out.");
		} else if (def.getStatus() == Gladator.PASSOUT) {
			System.out.println(def.getName() + " passes out.");
		} else if (att.getStatus() == Gladator.PASSOUT) {
			System.out.println(att.getName() + " passes out.");
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param def
	 *            Description of Parameter
	 */
	public void reportPlayerStatus(Gladator def) {
		System.out.println("  " + def.getName() + " took "
				+ def.getLastHitDamage() + " to his "
				+ def.getLastHitLocation() + ".");
		if (def.getStatus() == Gladator.PASSOUT) {
			System.out.println(def.getName() + " passes out.");
		}
	}

	/**
	 * Description of the Method
	 */
	public void finalReport() {
		for (int a = 0; a < teams.size(); a++) {
			System.out.println("NEW TEAM");
			int b;
			Vector v = (Vector) teams.elementAt(a);
			for (b = 0; b < v.size(); b++) {
				Gladator g = (Gladator) v.elementAt(b);
				g.finish();
				System.out.println(v.elementAt(b));
			}
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		Vector v = new Vector();
		Vector t1 = new Vector();
		Gladator g1 = new Gladator();
		g1.setName("Sparticus");
		g1.setPlan(Plan.parse(Plan.DEFAULT));
		g1.setRealSkill(Skill.parse(Skill.DEFAULT));
		g1
				.setWeapons(
						Weapon
								.parse(Weapon.allWeaps[(int) (Math.random() * Weapon.allWeaps.length)]),
						Weapon
								.parse(Weapon.allWeaps[(int) (Math.random() * Weapon.allWeaps.length)]));
		g1.schoolTest();
		t1.addElement(g1);
		v.addElement(t1);
		t1 = new Vector();
		g1 = new Gladator();
		g1.setName("Mad Max");
		g1.setPlan(Plan.parse(Plan.DEFAULT));
		g1.setRealSkill(Skill.parse(Skill.DEFAULT));
		g1
				.setWeapons(
						Weapon
								.parse(Weapon.allWeaps[(int) (Math.random() * Weapon.allWeaps.length)]),
						Weapon
								.parse(Weapon.allWeaps[(int) (Math.random() * Weapon.allWeaps.length)]));
		g1.schoolTest();
		t1.addElement(g1);
		v.addElement(t1);
		/*
		 * t1=new Vector(); g1=new Gladator(); g1.setName("Lifter");
		 * g1.setPlan(Plan.parse(Plan.DEFAULT));
		 * g1.setRealSkill(Skill.parse(Skill.DEFAULT));
		 * g1.setWeapons(Weapon.parse(Weapon.allWeaps[(int)(Math.random()*Weapon.allWeaps.length)]),Weapon.parse(Weapon.allWeaps[(int)(Math.random()*Weapon.allWeaps.length)]));
		 * g1.schoolTest(); t1.addElement(g1); g1=new Gladator();
		 * g1.setName("Flandar"); g1.setPlan(Plan.parse(Plan.DEFAULT));
		 * g1.setRealSkill(Skill.parse(Skill.DEFAULT)); g1.schoolTest();
		 * g1.setWeapons(Weapon.parse(Weapon.allWeaps[(int)(Math.random()*Weapon.allWeaps.length)]),Weapon.parse(Weapon.allWeaps[(int)(Math.random()*Weapon.allWeaps.length)]));
		 * t1.addElement(g1); v.addElement(t1);
		 */
		Battle b = new Battle(v);
		b.begin();
	}

}
