package aj.glad;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Gladator {

	String owner;
	//players name
	String nickname;
	String name;
	String notes;
	String description;
	int size, agility, strength, intell, race;
	//attributes (physical characturistics)
	int kills;
	int totalwin, totallose;
	Vector battleHistory;
	//String ("date X vs Y win/lose")
	int moral;
	//personal moral
	int status;

	//skills
	Skill real, schoolPerformance, lastPerformance, trainingLevels;
	Skill attempt, pass;
	//for tracking hit/miss to make performance
	//plan
	Plan plan;
	Armor headArmor, lArmArmor, rArmArmor, lLegArmor, rLegArmor, bodyArmor;
	Weapon mainWeapon, altWeapon;

	int totalHP, maxhitpoints = 200, LAHP, RAHP, LLHP, RLHP, HHP, BHP;

	double init;

	int defType, attType;

	Gladator opponent;

	boolean blocked, dodged, parried, simo;

	boolean hit, specialHit;
	int specialHitType, specialPoints;

	int borrowedAttacks = 0;
	int attacks;

	int lastHitLocation, lastHitDamage;
	/**
	 *  Description of the Field 
	 */
	public static int ACTIVE = 0, PASSOUT = 1, EVACUATED = 2, KILLED = 3;
	//status values
	/**
	 *  Description of the Field 
	 */
	public static int ATTACK = 1, PICKUP = 2, SURRENDER = 3;
	//attack options
	/**
	 *  Description of the Field 
	 */
	public static int HEAD = 0, BODY = 1, LEFTARM = 2, RIGHTARM = 3, LEFTLEG = 4, RIGHTLEG = 5;

	private static int SPECIALHITVALUE = 50;
	private static int SCHOOLTEST = 20;


	//  int blood;
	//  int tired;
	//  int stunround;
	/**
	 *  Sets the Name attribute of the Gladator object 
	 *
	 *@param  s  The new Name value 
	 */
	public void setName(String s) {
		name = s;
	}


	/**
	 *  Sets the RealSkill attribute of the Gladator object 
	 *
	 *@param  s  The new RealSkill value 
	 */
	public void setRealSkill(Skill s) {
		real = s;
	}


	/**
	 *  Sets the Plan attribute of the Gladator object 
	 *
	 *@param  p  The new Plan value 
	 */
	public void setPlan(Plan p) {
		plan = p;
	}


	/**
	 *  Sets the Weapons attribute of the Gladator object 
	 *
	 *@param  main  The new Weapons value 
	 *@param  alt   The new Weapons value 
	 */
	public void setWeapons(Weapon main, Weapon alt) {
		mainWeapon = main;
		altWeapon = alt;
	}


	/**
	 *  Sets the Armor attribute of the Gladator object 
	 *
	 *@param  h   The new Armor value 
	 *@param  b   The new Armor value 
	 *@param  ll  The new Armor value 
	 *@param  rl  The new Armor value 
	 *@param  la  The new Armor value 
	 *@param  ra  The new Armor value 
	 */
	public void setArmor(Armor h, Armor b, Armor ll, Armor rl, Armor la, Armor ra) {
		headArmor = h;
		bodyArmor = b;
		lLegArmor = ll;
		rLegArmor = rl;
		lArmArmor = la;
		rArmArmor = ra;
	}


	/**
	 *  Sets the Opponent attribute of the Gladator object 
	 *
	 *@param  v  The new Opponent value 
	 */
	public void setOpponent(Vector v) {
		int a;
		for (a = 0; a < v.size(); a++) {
			Gladator g = (Gladator) v.elementAt(a);
			if (g.getStatus() != ACTIVE) {
				v.removeElement(g);
				a--;
			}
			if (v.size() == 0) {
				opponent = null;
				return;
			}
		}
		setOpponent((Gladator) v.elementAt((int) (Math.random() * v.size())));
	}


	/**
	 *  Sets the Opponent attribute of the Gladator object 
	 *
	 *@param  g  The new Opponent value 
	 */
	public void setOpponent(Gladator g) {
		opponent = g;
	}


	/**
	 *  Gets the Name attribute of the Gladator object 
	 *
	 *@return    The Name value 
	 */
	public String getName() {
		return name;
	}


	/**
	 *  Gets the Status attribute of the Gladator object 
	 *
	 *@return    The Status value 
	 */
	public int getStatus() {
		return status;
	}


	/**
	 *  Gets the Init attribute of the Gladator object 
	 *
	 *@return    The Init value 
	 */
	public double getInit() {
		return init;
	}


	/**
	 *  Gets the Speed attribute of the Gladator object 
	 *
	 *@return    The Speed value 
	 */
	public int getSpeed() {
		return Math.max(real.speed / 20, 2);
		//speed = 2-5
	}


	/**
	 *  Gets the Opponent attribute of the Gladator object 
	 *
	 *@return    The Opponent value 
	 */
	public Gladator getOpponent() {
		return opponent;
	}


	/**
	 *  Gets the Simo attribute of the Gladator object 
	 *
	 *@return    The Simo value 
	 */
	public boolean getSimo() {
		return simo;
	}


	/**
	 *  Gets the Blocked attribute of the Gladator object 
	 *
	 *@return    The Blocked value 
	 */
	public boolean getBlocked() {
		return blocked;
	}


	/**
	 *  Gets the Parried attribute of the Gladator object 
	 *
	 *@return    The Parried value 
	 */
	public boolean getParried() {
		return parried;
	}


	/**
	 *  Gets the Dodged attribute of the Gladator object 
	 *
	 *@return    The Dodged value 
	 */
	public boolean getDodged() {
		return dodged;
	}


	/**
	 *  Gets the Hit attribute of the Gladator object 
	 *
	 *@return    The Hit value 
	 */
	public boolean getHit() {
		return hit;
	}


	/**
	 *  Gets the SpecialHit attribute of the Gladator object 
	 *
	 *@return    The SpecialHit value 
	 */
	public boolean getSpecialHit() {
		return specialHit;
	}


	/**
	 *  Gets the SpecialPoints attribute of the Gladator object 
	 *
	 *@return    The SpecialPoints value 
	 */
	public int getSpecialPoints() {
		return specialPoints;
	}


	/**
	 *  Gets the SpecialType attribute of the Gladator object 
	 *
	 *@return    The SpecialType value 
	 */
	public int getSpecialType() {
		return specialHitType;
	}


	/**
	 *  Gets the NumberOfAttacks attribute of the Gladator object 
	 *
	 *@return    The NumberOfAttacks value 
	 */
	public int getNumberOfAttacks() {
		return attacks;
	}


	/**
	 *  Gets the LastHitLocation attribute of the Gladator object 
	 *
	 *@return    The LastHitLocation value 
	 */
	public String getLastHitLocation() {
		if (lastHitLocation == HEAD) {
			return "Head";
		}
		else if (lastHitLocation == BODY) {
			return "Body";
		}
		else if (lastHitLocation == LEFTLEG) {
			return "Left Leg";
		}
		else if (lastHitLocation == RIGHTLEG) {
			return "Right Leg";
		}
		else if (lastHitLocation == LEFTARM) {
			return "Left Arm";
		}
		else {
			return "Right Arm";
		}
	}


	/**
	 *  Gets the LastHitDamage attribute of the Gladator object 
	 *
	 *@return    The LastHitDamage value 
	 */
	public int getLastHitDamage() {
		return lastHitDamage;
	}


	/**
	 *  Description of the Method 
	 */
	public void reset() {
		//begin new battle (set full hit points and status)
		attempt = new Skill();
		pass = new Skill();
		status = ACTIVE;
		attacks = getSpeed();
		totalHP = maxhitpoints;
		LAHP = (int) (maxhitpoints * .1);
		RAHP = (int) (maxhitpoints * .1);
		LLHP = (int) (maxhitpoints * .2);
		RLHP = (int) (maxhitpoints * .2);
		HHP = (int) (maxhitpoints * .1);
		BHP = (int) (maxhitpoints * .3);
	}


	/**
	 *  Description of the Method 
	 */
	public void newRound() {
		attacks = Math.max(getSpeed() - borrowedAttacks, 0);
		borrowedAttacks = 0;
		//check surrender evacuate timeout
	}


	/**
	 *  Description of the Method 
	 */
	public void useAttack() {
		//System.out.println(name +" Attack! "+attacks+", <"+borrowedAttacks+"> ");
		if (attacks > 0) {
			attacks--;
		}
		else {
			borrowedAttacks++;
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void finish() {
		//determin last performance/ best overall performnce (max of school && last)
		if (lastPerformance == null) {
			lastPerformance = new Skill();
		}
		lastPerformance.type = Skill.PERFORMANCE;
		if (attempt.sword != 0) {
			lastPerformance.sword = 100 * pass.sword / attempt.sword;
		}
		if (attempt.club != 0) {
			lastPerformance.club = 100 * pass.club / attempt.club;
		}
		if (attempt.polarm != 0) {
			lastPerformance.polarm = 100 * pass.polarm / attempt.polarm;
		}
		if (attempt.handtohand != 0) {
			lastPerformance.handtohand = 100 * pass.handtohand / attempt.handtohand;
		}
		if (attempt.parry != 0) {
			lastPerformance.parry = 100 * pass.parry / attempt.parry;
		}
		if (attempt.dodge != 0) {
			lastPerformance.dodge = 100 * pass.dodge / attempt.dodge;
		}
		if (attempt.block != 0) {
			lastPerformance.block = 100 * pass.block / attempt.block;
		}
		if (attempt.simo != 0) {
			lastPerformance.simo = 100 * pass.simo / attempt.simo;
		}
		if (attempt.speed != 0) {
			lastPerformance.speed = 100 * pass.speed / attempt.speed;
		}
		//entangle
		//speed
		//supprise
	}


	/**
	 *  Description of the Method 
	 */
	public void schoolTest() {
		schoolPerformance = new Skill();
		schoolPerformance.type = Skill.SCHOOL;
		pass = new Skill();
		int a;
		for (a = 0; a < SCHOOLTEST; a++) {
			pass.sword += testRoll(real.sword);
			pass.club += testRoll(real.club);
			pass.polarm += testRoll(real.polarm);
			pass.handtohand += testRoll(real.handtohand);
			pass.speed += testRoll(real.speed);
			pass.dodge += testRoll(real.dodge);
			pass.simo += testRoll(real.simo);
			pass.parry += testRoll(real.parry);
			pass.block += testRoll(real.block);
		}
		schoolPerformance.sword = 100 * pass.sword / SCHOOLTEST;
		schoolPerformance.club = 100 * pass.club / SCHOOLTEST;
		schoolPerformance.polarm = 100 * pass.polarm / SCHOOLTEST;
		schoolPerformance.handtohand = 100 * pass.handtohand / SCHOOLTEST;
		schoolPerformance.speed = 100 * pass.speed / SCHOOLTEST;
		schoolPerformance.dodge = 100 * pass.dodge / SCHOOLTEST;
		schoolPerformance.simo = 100 * pass.simo / SCHOOLTEST;
		schoolPerformance.parry = 100 * pass.parry / SCHOOLTEST;
		schoolPerformance.block = 100 * pass.block / SCHOOLTEST;
		pass = new Skill();
	}


	/**
	 *  Description of the Method 
	 */
	public void rollInit() {
		init = Math.random();
		newRound();
		attempt.speed++;
		if (roll(real.speed)) {
			real.speed += real.improve(real.speed);
			pass.speed++;
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void chooseAtt() {
		attType = plan.getNextAttack();
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  v  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean kill(Vector v) {
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  v  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean pickup(Vector v) {
		int a;
		for (a = 0; a < v.size(); a++) {
			if (pickup((Item) v.elementAt(a))) {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  i  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean pickup(Item i) {
		if (i instanceof Weapon && plan.emptyPickup(i) && freeHand()) {
			return true;
		}
		else if (i instanceof Weapon && betterWeapon(i) && plan.dropPickup(i)) {
			return true;
		}
		else if (i instanceof Armor && needArmor(i)) {
			return true;
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public int attType() {
		return attType;
	}


	/**
	 *  Description of the Method 
	 */
	public void chooseDef() {
		if (borrowedAttacks >= getSpeed() || getStatus() != ACTIVE) {
			defType = Plan.NONE;
		}
		else {
			defType = plan.getNextDefense();
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public int defType() {
		return defType;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  x  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean roll(int x) {
		return x >= Math.random() * 100;
	}


	/**
	 *  A unit test for JUnit 
	 *
	 *@param  x  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public int testRoll(int x) {
		return (x >= Math.random() * 100 ? 1 : 0);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  x  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public int numRoll(int x) {
		return (int) (x - Math.random() * 100);
	}


	/**
	 *  Description of the Method 
	 */
	public void rollSimo() {
		attempt.simo++;
		simo = roll(real.simo);
		if (simo) {
			pass.simo++;
			real.simo += real.improve(real.simo);
		}
		if (!simo) {
			useAttack();
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void rollBlock() {
		attempt.block++;
		useAttack();
		chooseDefenseWeapon();
		blocked = roll(real.block);
		if (blocked) {
			pass.block++;
			real.block += real.improve(real.block);
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void rollParry() {
		attempt.parry++;
		useAttack();
		chooseDefenseWeapon();
		parried = roll(real.parry);
		if (parried) {
			pass.parry++;
			real.parry += real.improve(real.parry);
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void rollDodge() {
		attempt.dodge++;
		useAttack();
		chooseDefenseWeapon();
		dodged = roll(real.dodge);
		if (dodged) {
			pass.dodge++;
			real.dodge += real.improve(real.dodge);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  anti  Description of Parameter 
	 */
	public void rollBlock(int anti) {
		attempt.block++;
		useAttack();
		chooseDefenseWeapon();
		blocked = roll(real.block - anti);
		if (blocked) {
			pass.block++;
			real.block += real.improve(real.block);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  anti  Description of Parameter 
	 */
	public void rollParry(int anti) {
		attempt.parry++;
		useAttack();
		chooseDefenseWeapon();
		parried = roll(real.parry - anti);
		if (parried) {
			pass.parry++;
			real.parry += real.improve(real.parry);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  anti  Description of Parameter 
	 */
	public void rollDodge(int anti) {
		attempt.dodge++;
		useAttack();
		chooseDefenseWeapon();
		dodged = roll(real.dodge - anti);
		if (dodged) {
			pass.dodge++;
			real.dodge += real.improve(real.dodge);
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void rollHit() {
		useAttack();
		chooseAttackWeapon();
		Weapon U = mainWeapon;
		//what skill for weapon
		int sk;
		if (U.getType() == Weapon.SWORD) {
			attempt.sword++;
			sk = real.sword;
		}
		else if (U.getType() == Weapon.CLUB) {
			attempt.club++;
			sk = real.club;
		}
		else if (U.getType() == Weapon.POLARM) {
			attempt.polarm++;
			sk = real.polarm;
		}
		else {
			attempt.handtohand++;
			sk = real.handtohand;
		}
		int x = numRoll(sk);
		hit = x > 0;
		specialHit = x > SPECIALHITVALUE;
		if (hit) {
			if (U.getType() == Weapon.SWORD) {
				pass.sword++;
				real.sword += real.improve(real.sword);
			}
			else if (U.getType() == Weapon.CLUB) {
				pass.club++;
				real.club += real.improve(real.club);
			}
			else if (U.getType() == Weapon.POLARM) {
				pass.polarm++;
				real.polarm += real.improve(real.polarm);
			}
			else {
				pass.handtohand++;
				real.handtohand += real.improve(real.handtohand);
			}
		}
		if (specialHit) {
			specialPoints = Math.abs(x);
			specialHitType = plan.getNextSpecial();
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void rollEntangle() {
	}


	/**
	 *  Description of the Method 
	 */
	public void chooseDefenseWeapon() {
	}


	/**
	 *  Description of the Method 
	 */
	public void chooseAttackWeapon() {
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public int rollDamage() {
		//adjust for weapon effective ness
		return mainWeapon.getDamage();
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  d  Description of Parameter 
	 */
	public void hurt(int d) {
		//random location
		hurt((int) (Math.random() * 6), d);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  loc  Description of Parameter 
	 *@param  d    Description of Parameter 
	 */
	public void hurt(int loc, int d) {
		//specific location
		lastHitDamage = d;
		lastHitLocation = loc;
		totalHP -= d;
		if (loc == HEAD) {
			HHP -= d;
		}
		if (loc == BODY) {
			BHP -= d;
		}
		if (loc == LEFTARM) {
			LAHP -= d;
		}
		if (loc == RIGHTARM) {
			RAHP -= d;
		}
		if (loc == LEFTLEG) {
			LLHP -= d;
		}
		if (loc == RIGHTLEG) {
			RLHP -= d;
		}

		if (HHP < 0 || BHP < 0 || LAHP < 0 || RAHP < 0 || LLHP < 0 || RLHP < 0) {
			status = PASSOUT;
		}

	}


	/**
	 *  Description of the Method 
	 *
	 *@param  t  Description of Parameter 
	 */
	public void hurtParry(int t) {
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  t  Description of Parameter 
	 */
	public void hurtBlock(int t) {
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String s = "BEGIN GLADATOR\n";
		s += "name:" + getName() + " status:" + getStatus() + "\n";
		s += "HEALTH: head:" + HHP + " body:" + BHP + " rarm:" + RAHP + " larm:" + LAHP + " rleg:" + RLHP + " lleg:" + LLHP + "\n";
		s += "REAL SKILL\n";
		s += real + "\n";
		s += "SCHOOLPERFORMANCE SKILL\n";
		s += schoolPerformance + "\n";
		s += "LASTPERFORMANCE SKILL\n";
		s += lastPerformance + "\n";
		s += "CURRENT PLAN\n";
		s += plan + "\n";
		//    s+="PRIMARY WEAPON\n";
		//    s+=mainWeapon+"\n";
		//    s+="SECONDARY WEAPON\n";
		//    s+=altWeapon+"\n";
		s += "END GLADATOR\n";
		return s;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	private boolean freeHand() {
		return (mainWeapon != null && altWeapon != null);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  i  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	private boolean betterWeapon(Item i) {
		return false;
		//    if (i.getCost() > mainWeapon.getCost() || i.getCost() > altWeapon.getCost()) return true;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  a  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	private boolean needArmor(Item a) {
		return false;
	}

	//  public Gladator create() {
	//make random new gladator
	//default plan
	//hand weapons
	//no armor
	//default skills (randomly)
	//no school
	//no lastperformance
	//  }
}

/*
 * leg 0 or arm 0 = -33% #attacks
 * if (hit in area 0) then body blow.
 * 1 arm 0 = -33% to hit parry block
 * 1 leg 0 = -33% to dodge
 * arm==0 system shock
 * leg==0 system shock
 * 2 arms legs = passout
 * body=0 passout
 * head=0 passout
 * total hitpoint < surrender = evacuated
 */

/*
 * imporve skill rules
 * school standings only change from school testing
 * last performance = pass/attempts
 * if not attempted then last score passes forward
 * best overall performance = max of school and last performance
 */
