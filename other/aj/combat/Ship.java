package aj.combat;

import java.awt.Color;
import java.awt.Graphics;

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

	static String shipType="-1";

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

	

	public Explosion explode() {
		Explosion e=new Explosion(id + "E",x,y,0,vx,vy,1000);
		return e;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public Shot shoot() {
		return new Shot(id + "S" + (shotCount++), x + size * Math.cos(dir), y + size * Math.sin(dir), dir, vx + Player.MAXSHOTSPEED * Math.cos(dir), vy + Player.MAXSHOTSPEED * Math.sin(dir), Shot.shotType);
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
		fix();
		updatePos();
		double my = Math.sin(dir) * size / 2;
		double mx = Math.cos(dir) * size / 2;
		double dy= Math.sin(dir+Math.PI/2) * size / 3;
		double dx= Math.cos(dir+Math.PI/2) * size / 3;
		
		g.setColor(Color.gray);
//		g.drawOval((int) (x - size / 2), (int) (y - size / 2), size, size);
		if (!alive) {
			g.setColor(Color.red);
		}
		else {
			g.setColor(Color.white);
		}
//		g.drawLine((int) (x - mx), (int) (y - my), (int) (x + mx), (int) (y + my));

		g.drawLine((int) (x - mx-dx), (int) (y - my-dy), (int) (x + mx), (int) (y + my));
		g.drawLine((int) (x - mx+dx), (int) (y - my+dy), (int) (x + mx), (int) (y + my));
		g.drawLine((int) (x - mx-dx), (int) (y - my-dy), (int) (x - mx+dx), (int) (y - my+dy));

		//		Arrow a = new Arrow(new Point((int) (x - mx), (int) (y - my)), 
//				new Point((int) (x + mx), (int) (y + my)), size / 4, size, true);
//		a.display(g, 1.0);
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
		double ndir = ((int) (Math.random() * 360));
		return new Ship(id, Math.random() * MapView.ZONESIZE, 
				Math.random() * MapView.ZONESIZE, 
				ndir, 0, 0);
	}


	public static CombatItem parse(String[] t) {
		return new Ship(t[0], Double.parseDouble(t[1]), 
				Double.parseDouble(t[2]), 
				Double.parseDouble(t[3]), 
				Double.parseDouble(t[4]), 
				Double.parseDouble(t[5]));
	}

}


