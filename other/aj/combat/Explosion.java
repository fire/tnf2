/*
 * Created on Mar 23, 2006
 *
 */
package aj.combat;

import java.awt.Color;
import java.awt.Graphics;

import aj.misc.Stuff;

public class Explosion extends Thing implements CombatItem {

	static String explosionType="E";
	
	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void display(Graphics g) {
		int rang=(int)(1000-this.lifeOver+this.time)/10;
		int srang=rang-2;
		g.setColor(Color.red);
		for (int a=0;a<20;a++) {
			double ang=Math.PI*2/20;			
			g.drawLine((int)(x+Math.cos(ang*a)*srang),(int)(y+Math.sin(ang*a)*srang),(int)(x+Math.cos(ang*a)*rang),(int)(y+Math.sin(ang*a)*rang));
		}
	}

	long lifeOver;
	double mx, my;
	private long minExplostionLife=1000;

	/**
	 *  Constructor for the Shot object 
	 *
	 *@param  id    Description of Parameter 
	 *@param  x     Description of Parameter 
	 *@param  y     Description of Parameter 
	 *@param  d     Description of Parameter 
	 *@param  vx    Description of Parameter 
	 *@param  vy    Description of Parameter 
	 *@param  life  Description of Parameter 
	 */
	public Explosion(String id, double x, double y, double d, double vx, double vy) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = d;
		this.size = 0;
		this.vx = vx;
		this.vy = vy;
		this.time = System.currentTimeMillis();
		lifeOver = time + minExplostionLife;
//		mx = Math.cos(dir) * size / 2;
//		my = Math.sin(dir) * size / 2;
	}

	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public boolean expired() {
		return lifeOver < System.currentTimeMillis();
	}


	public static CombatItem parse(String[] t) {
		double x=Double.parseDouble(t[2]);
		double y=Double.parseDouble(t[3]);
		double dir=Double.parseDouble(t[4]);
		double vx=Double.parseDouble(t[5]);
		double vy=Double.parseDouble(t[6]);

		return new Explosion(t[1],x,y,dir,vx,vy);
		
	}

	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		return explosionType+" "+
		id + " " + 
		Stuff.trunc(x,1) + " " + 
		Stuff.trunc(y,1) + " " + 
		Stuff.trunc(dir,3) + " " + 
		Stuff.trunc(vx,3) + " " + 
		Stuff.trunc(vy,3);
	}

}
