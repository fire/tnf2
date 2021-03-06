package aj.combat;

import java.awt.Color;
import java.awt.Graphics;

import aj.misc.Stuff;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Shot extends Thing implements CombatItem {
	public static final String shotType = "L";

	long minShotLife = 2000;

	long lifeOver;

	double mx, my;

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
	public Shot(String id, double x, double y, double d, double vx, double vy) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = d;
		this.size = Player.SHOTSIZE;
		this.vx = vx;
		this.vy = vy;
		this.time = System.currentTimeMillis();
		lifeOver = time + minShotLife;
		mx = Math.cos(dir) * size / 2;
		my = Math.sin(dir) * size / 2;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public boolean expired() {
		return lifeOver < System.currentTimeMillis();
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 */
	public void display(Graphics g) {
		g.setColor(Color.white);
		g.drawLine((int) (x - mx), (int) (y - my), (int) (x + mx),
				(int) (y + my));
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		return shotType + " " + id + " " + Stuff.trunc(x, 1) + " "
				+ Stuff.trunc(y, 1) + " " + Stuff.trunc(dir, 2) + " "
				+ Stuff.trunc(vx, 3) + " " + Stuff.trunc(vy, 3) + " ";
	}

	public static CombatItem parse(String[] t) {
		double x = Double.parseDouble(t[2]);
		double y = Double.parseDouble(t[3]);
		double dir = Double.parseDouble(t[4]);
		double vx = Double.parseDouble(t[5]);
		double vy = Double.parseDouble(t[6]);
		return new Shot(t[1], x, y, dir, vx, vy);
	}

}
