/*
 * Created on Mar 28, 2006
 *
 */
package aj.combat;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import aj.misc.Stuff;

public class Asteroid extends Thing implements CombatItem {

	int xp[],yp[],n;
		
	double spinRate;
	
	static int SMALL=6,MEDIUM=10,LARGE=15;
	static String asteroidType="-2";
	int randSeed=0;
	
	public Asteroid(String id, double x, double y, double d, double vx, double vy, String typeId,String sizeString,String randSeed) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = d;
		this.time = System.currentTimeMillis();

		this.randSeed=Integer.parseInt(randSeed);
		if (this.randSeed==0) this.randSeed=(int)(Math.random()*10000);
		Random r=new Random(this.randSeed);
		size=Integer.parseInt(sizeString);
		if (size==0) {
			this.size = LARGE;
			if (r.nextDouble()>.5) this.size=MEDIUM;
			else if (r.nextDouble()>.5) this.size=SMALL;
			this.time = System.currentTimeMillis();			
		}
		if (vx==0 && vy==0) {
			vx=Math.cos(d)*r.nextDouble()*2;
			vy=Math.sin(d)*r.nextDouble()*2;
		}
		this.vx = vx;
		this.vy = vy;
	}
	
	public void display(Graphics g) {
		updatePos();
		updateAsteroidShape();
		g.setColor(Color.white);
		g.drawLine((int)x,(int)y,(int)x,(int)y);
		g.fillPolygon(xp,yp,n);
	}
		
	private void updateAsteroidShape() {
		Random r=new Random(randSeed);
		n=size;
		xp=new int[n];
		yp=new int[n];
		for (int a=0;a<n-1;a++) {
			double ang=Math.PI*2/size;
			xp[a]=(int)(x+Math.cos(ang*a)*(r.nextDouble()*(size)+size/2));
			yp[a]=(int)(y+Math.sin(ang*a)*(r.nextDouble()*(size)+size/2));
		}
		xp[n-1]=xp[0];
		yp[n-1]=yp[0];	
	}
	
	public String toString() {
		return id + " " 
		+ Stuff.trunc(x,2) + " " + 
		Stuff.trunc(y,2) + " " + 
		Stuff.trunc(dir,3) + " " + 
		Stuff.trunc(vx,4) + " " + 
		Stuff.trunc(vy,4) +" "+ 
		asteroidType +" " +
		size+" "+
		randSeed;
	}

	public static Asteroid parse(String[] t) {
		Asteroid a=new Asteroid(t[0], 
				Double.parseDouble(t[1]), 
				Double.parseDouble(t[2]), 
				Double.parseDouble(t[3]), 
				Double.parseDouble(t[4]), 
				Double.parseDouble(t[5]), 
				t[6],t[7],t[8]);
		System.out.println("new asteroid ="+a);
		return a;
	}
}
