package aj.combat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import aj.awt.Arrow;
import aj.misc.Stuff;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000
 */
public class Ship extends Thing implements CombatItem {
	//2% reduce speed
	boolean alive = true;
	static int shotCount = 0;


	/**
	 *  Constructor for the Ship object 
	 *
	 *@param  id  Description of Parameter 
	 *@param  x   Description of Parameter 
	 *@param  y   Description of Parameter 
	 *@param  d   Description of Parameter 
	 *@param  vx  Description of Parameter 
	 *@param  vy  Description of Parameter 
	 */
	public Ship(String id, double x, double y, double d, double vx, double vy) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = d;
		this.size = Player.SHIPSIZE;
		this.vx = vx;
		this.vy = vy;
		this.time = System.currentTimeMillis();
	}


	/**
	 *  Sets the Alive attribute of the Ship object 
	 *
	 *@param  b  The new Alive value 
	 */
	public void setAlive(boolean b) {
		alive = b;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public Shot shoot() {
		return new Shot(id + "S" + (shotCount++), x + size * Math.cos(dir), y + size * Math.sin(dir), dir, vx + Player.MAXSHOTSPEED * Math.cos(dir), vy + Player.MAXSHOTSPEED * Math.sin(dir), Player.MAXSHOTRANGE);
	}


	/**
	 *  Description of the Method 
	 */
	public void fix() {
		double len = vx * vx + vy * vy;
		len = Math.sqrt(len);
		if (len > Player.MAXSHIPSPEED) {
			vx = vx / len * Player.MAXSHIPSPEED;
			vy = vy / len * Player.MAXSHIPSPEED;
		}
		vx = vx * Player.FRICTION;
		vy = vy * Player.FRICTION;
		if (dir > Math.PI * 2) {
			dir -= Math.PI * 2;
		}
		if (dir < 0) {
			dir += Math.PI * 2;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void display(Graphics g) {
		g.setColor(Color.lightGray);
		g.drawOval((int) (x - size / 2), (int) (y - size / 2), size, size);
		g.setColor(Color.black);
		if (!alive) {
			g.setColor(Color.red);
		}
		else {
			g.setColor(Color.black);
		}
		fix();
		updatePos();
		double my = Math.sin(dir) * size / 2;
		double mx = Math.cos(dir) * size / 2;
		g.drawLine((int) (x - mx), (int) (y - my), (int) (x + mx), (int) (y + my));
		Arrow a = new Arrow(new Point((int) (x - mx), (int) (y - my)), 
				new Point((int) (x + mx), (int) (y + my)), size / 4, size, true);
		a.display(g, 1.0);
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		return id + " " + Stuff.trunc(x,1) + " " + Stuff.trunc(y,1) + " " + Stuff.trunc(dir,2) + " " + Stuff.trunc(vx,2) + " " + Stuff.trunc(vy,2) + " -1";
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  id  Description of Parameter 
	 *@return     Description of the Returned Value 
	 */
	public static Ship rand(String id) {
		double ndir = ((int) (Math.random() * 360 / Player.SHIPMAXSHOTCOUNT));
		return new Ship(id, Math.random() * Player.ZONESIZE, 
				Math.random() * Player.ZONESIZE, 
				ndir, 0, 0);
	}

}

