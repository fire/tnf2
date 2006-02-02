package aj.glad;

import aj.misc.Stuff;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Weapon extends Item {
	int type;
	boolean entangle;
	boolean block;
	boolean attack;
	boolean parry;
	String hitdice;
	int platebonus, chainbonus, leatherbonus;

	int entangledround;
	static int SWORD = 0, POLARM = 1, CLUB = 2, HANDTOHAND = 3, UNKNOWN = 4;

	static String allWeaps[] = {
			"Net lbs2  gp10 hp100  WEAPON CLUB		hd1d2   (attack parry entangle)	plate+0 chain+0 leather+0", 
			"Whip	lbs2  gp15 hp200  WEAPON CLUB			hd1d4   (attack entangle)	plate+0 chain+0 leather+0", 
			"Club lbs2  gp5  hp400  WEAPON CLUB			hd2d4   (attack) 		plate+5 chain+1 leather+1", 
			"Shield lbs5  gp30 hp1000 WEAPON CLUB		hd1d2+1 (attack block)  	plate+0 chain+0 leather+0", 
			"Dagger lbs5  gp20 hp1000 WEAPON SWORD		hd2d6   (attack parry) 		plate+0 chain+5 leather+1", 
			"Sword lbs15 gp90 hp2000 WEAPON SWORD		hd4d6+1 (attack parry) 		plate+2 chain+2 leather+3", 
			"Gauntlet lbs5  gp12 hp600  WEAPON HANDTOHAND	hd1d6+1 (attack block)  	plate+0 chain+0 leather+0", 
			"Trident lbs12 gp250 hp800 WEAPON POLARM		hd2d6   (attack block entangle)	plate+1 chain+4 leather+2",
			};


	/**
	 *  Sets the Entangle attribute of the Weapon object 
	 *
	 *@param  b  The new Entangle value 
	 */
	public void setEntangle(boolean b) {
		entangle = b;
	}


	/**
	 *  Sets the Block attribute of the Weapon object 
	 *
	 *@param  b  The new Block value 
	 */
	public void setBlock(boolean b) {
		block = b;
	}


	/**
	 *  Sets the Attack attribute of the Weapon object 
	 *
	 *@param  b  The new Attack value 
	 */
	public void setAttack(boolean b) {
		attack = b;
	}


	/**
	 *  Sets the Parry attribute of the Weapon object 
	 *
	 *@param  b  The new Parry value 
	 */
	public void setParry(boolean b) {
		parry = b;
	}


	/**
	 *  Sets the HitDice attribute of the Weapon object 
	 *
	 *@param  s  The new HitDice value 
	 */
	public void setHitDice(String s) {
		hitdice = s.toLowerCase().trim();
	}


	/**
	 *  Sets the Type attribute of the Weapon object 
	 *
	 *@param  t  The new Type value 
	 */
	public void setType(int t) {
		type = t;
	}


	/**
	 *  Sets the PlateBonus attribute of the Weapon object 
	 *
	 *@param  t  The new PlateBonus value 
	 */
	public void setPlateBonus(int t) {
		platebonus = t;
	}


	/**
	 *  Sets the ChainBonus attribute of the Weapon object 
	 *
	 *@param  t  The new ChainBonus value 
	 */
	public void setChainBonus(int t) {
		chainbonus = t;
	}


	/**
	 *  Sets the LeatherBonus attribute of the Weapon object 
	 *
	 *@param  t  The new LeatherBonus value 
	 */
	public void setLeatherBonus(int t) {
		leatherbonus = t;
	}


	/**
	 *  Sets the EntangledRound attribute of the Weapon object 
	 *
	 *@param  t  The new EntangledRound value 
	 */
	public void setEntangledRound(int t) {
		entangledround = t;
	}


	/**
	 *  Gets the Entangle attribute of the Weapon object 
	 *
	 *@return    The Entangle value 
	 */
	public boolean isEntangle() {
		return entangle;
	}


	/**
	 *  Gets the Block attribute of the Weapon object 
	 *
	 *@return    The Block value 
	 */
	public boolean isBlock() {
		return block;
	}


	/**
	 *  Gets the Attack attribute of the Weapon object 
	 *
	 *@return    The Attack value 
	 */
	public boolean isAttack() {
		return attack;
	}


	/**
	 *  Gets the Parry attribute of the Weapon object 
	 *
	 *@return    The Parry value 
	 */
	public boolean isParry() {
		return parry;
	}


	/**
	 *  Gets the HitDice attribute of the Weapon object 
	 *
	 *@return    The HitDice value 
	 */
	public String getHitDice() {
		return hitdice;
	}


	/**
	 *  Gets the Type attribute of the Weapon object 
	 *
	 *@return    The Type value 
	 */
	public int getType() {
		return type;
	}


	/**
	 *  Gets the PlateBonus attribute of the Weapon object 
	 *
	 *@return    The PlateBonus value 
	 */
	public int getPlateBonus() {
		return platebonus;
	}


	/**
	 *  Gets the ChainBonus attribute of the Weapon object 
	 *
	 *@return    The ChainBonus value 
	 */
	public int getChainBonus() {
		return chainbonus;
	}


	/**
	 *  Gets the LeatherBonus attribute of the Weapon object 
	 *
	 *@return    The LeatherBonus value 
	 */
	public int getLeatherBonus() {
		return leatherbonus;
	}


	/**
	 *  Gets the EntangledRound attribute of the Weapon object 
	 *
	 *@return    The EntangledRound value 
	 */
	public int getEntangledRound() {
		return entangledround;
	}


	/**
	 *  Gets the Damage attribute of the Weapon object 
	 *
	 *@return    The Damage value 
	 */
	public int getDamage() {
		int dam = 0;
		try {
			int num = 0;
			int dice = 0;
			int plus = 0;
			if (hitdice.indexOf("d") > 0) {
				num = Integer.parseInt(hitdice.substring(0, hitdice.indexOf("d")));
				if (hitdice.indexOf("+") > 0) {
					dice = Integer.parseInt(hitdice.substring(hitdice.indexOf("d") + 1, hitdice.indexOf("+")).trim());
					plus = Integer.parseInt(hitdice.substring(hitdice.indexOf("+") + 1).trim());
				}
				else {
					dice = Integer.parseInt(hitdice.substring(hitdice.indexOf("d") + 1).trim());
				}
			}
			else {
				plus = Integer.parseInt(hitdice);
			}
			int a;
			for (a = 0; a < num; a++) {
				dam += Math.random() * dice + 1;
			}
			dam += plus;
		}
		catch (NumberFormatException NFE) {
			System.out.println("Bad hitdice in weapons");
		}
		return dam;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String s = name + " lbs" + getWeight() + " gp" + getCost() + " hp" + getMaxHitPoints();
		s += " WEAPON ";
		if (getType() == CLUB) {
			s += "CLUB";
		}
		else if (getType() == SWORD) {
			s += "SWORD";
		}
		else if (getType() == POLARM) {
			s += "POLARM";
		}
		else if (getType() == HANDTOHAND) {
			s += "HANDTOHAND";
		}
		s += " hd" + getHitDice();
		s += " (";
		if (isEntangle()) {
			s += "entangle ";
		}
		if (isAttack()) {
			s += "attack ";
		}
		if (isParry()) {
			s += "parry ";
		}
		if (isBlock()) {
			s += "block ";
		}
		s += ") ";
		s += " plate+" + platebonus;
		s += " chain+" + chainbonus;
		s += " leather+" + leatherbonus;
		return s;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Weapon parse(String s) {
		if (s.indexOf("WEAPON") == -1) {
			return null;
		}
		s = Stuff.superTrim(s);
		try {
			Weapon w = new Weapon();
			if (s.indexOf("CLUB") >= 0) {
				w.setType(CLUB);
			}
			else if (s.indexOf("SWORD") >= 0) {
				w.setType(SWORD);
			}
			else if (s.indexOf("POLARM") >= 0) {
				w.setType(POLARM);
			}
			else if (s.indexOf("HANDTOHAND") >= 0) {
				w.setType(HANDTOHAND);
			}
			else {
				w.setType(UNKNOWN);
			}
			if (s.indexOf("attack") >= 0) {
				w.setAttack(true);
			}
			if (s.indexOf("parry") >= 0) {
				w.setParry(true);
			}
			if (s.indexOf("block") >= 0) {
				w.setBlock(true);
			}
			if (s.indexOf("entangle") >= 0) {
				w.setEntangle(true);
			}
			w.setName(s.substring(0, s.indexOf(" ")));
			String t;
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
			t = s.substring(s.indexOf("hd") + 2).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			w.setHitDice(t);
			t = s.substring(s.indexOf("plate+") + 6).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			w.setPlateBonus(Integer.parseInt(t));
			t = s.substring(s.indexOf("chain+") + 6).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			w.setChainBonus(Integer.parseInt(t));
			t = s.substring(s.indexOf("leather+") + 8).trim() + " ";
			t = t.substring(0, t.indexOf(" "));
			w.setLeatherBonus(Integer.parseInt(t));
			return w;
		}
		catch (NumberFormatException NFE) {
		}
		return null;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  args  Description of Parameter 
	 */
	public static void main(String args[]) {
		Weapon w = Weapon.parse(allWeaps[0]);
		w = Weapon.parse(w.toString());
		System.out.println(w);
	}

}


//entangle weapons  do less damage, break easy
//rent weapons?
