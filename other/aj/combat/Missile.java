package aj.combat;

import java.awt.Color;
import java.awt.Graphics;

import aj.misc.Stuff;

public class Missile extends Thing implements CombatItem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static final String missileType = "M";

	// double friction=1;
	/**
	 * Constructor for the Shot object
	 * 
	 * @param id
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param d
	 *            Description of Parameter
	 * @param vx
	 *            Description of Parameter
	 * @param vy
	 *            Description of Parameter
	 * @param life
	 *            Description of Parameter
	 */
	public Missile(String id, double x, double y, double d, double vx, double vy) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = d;
		this.size = Player.MISSILESIZE;
		vx = Math.round(vx * 100) / 100.0;
		vy = Math.round(vy * 100) / 100.0;
		this.vx = vx;
		this.vy = vy;
		this.time = System.currentTimeMillis();
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 */
	public void display(Graphics g) {
		g.setColor(Color.white);
		g.fillRoundRect((int) (x - size / 2), (int) (y - size / 2),
				(int) (size), (int) (size), 2, 2);// (int) (x + mx), (int) (y
		// + my));
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		return missileType + " " + id + " " + Stuff.trunc(x, 1) + " "
				+ Stuff.trunc(y, 1) + " " + Stuff.trunc(dir, 2) + " "
				+ Stuff.trunc(vx, 3) + " " + Stuff.trunc(vy, 3) + " ";
	}

	public static CombatItem parse(String[] t) {
		double x = Double.parseDouble(t[2]);
		double y = Double.parseDouble(t[3]);
		double dir = Double.parseDouble(t[4]);
		double vx = Double.parseDouble(t[5]);
		double vy = Double.parseDouble(t[6]);
		return new Missile(t[1], x, y, dir, vx, vy);
	}

}
