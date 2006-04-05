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
	static String asteroidType="A";
	int randSeed=0;
	
	boolean rebirthType=false;

	private boolean breader=false;
	
	Color c;
	
	public Asteroid(String id, double x, double y, double d, double vx, double vy, int sizeString,int randSeed) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = d;
		this.time = System.currentTimeMillis();

		this.randSeed=randSeed;
		if (this.randSeed==0) this.randSeed=(int)(Math.random()*10000);
		Random r=new Random(this.randSeed);
		size=sizeString;
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
	
	public Asteroid breakup() {
		Asteroid a=new Asteroid("A"+asteroidCount++,x,y,dir,vx,vy,Asteroid.LARGE,(int)(Math.random()*1000));
		dir+=Math.random()*Math.PI/2;
		a.dir-=Math.random()*Math.PI/2;
		a.vx=vx+Math.random()*2-1;
		a.vy=vy+Math.random()*2-1;
		vx+=Math.random()*2-1;
		vy+=Math.random()*2-1;
		if (size==LARGE) {size=a.size=MEDIUM;}
		else if (size==MEDIUM) {size=a.size=SMALL;}
		else {
			x=Math.random()*MapView.ARENASIZE;
			y=Math.random()*MapView.ARENASIZE;
			size=LARGE;
			randSeed=(int)(Math.random()*1000);
			return null;
		}
		return a;
	}
	
	public void display(Graphics g) {
		updateAsteroidShape();
		g.setColor(c);
		g.drawLine((int)x,(int)y,(int)x,(int)y);
		g.fillPolygon(xp,yp,n);
	}

	Color colorList[]={Color.blue,new Color(255,0,255),Color.PINK,Color.green,Color.YELLOW,new Color(125,0,180)};
	
	private void updateAsteroidShape() {
		
		Random r=new Random(randSeed);
		c=colorList[(int)(Math.abs(r.nextInt()%colorList.length))];
		n=size;
		xp=new int[n];
		yp=new int[n];
		for (int a=0;a<n-1;a++) {
			double ang=Math.PI*2/size;
			xp[a]=(int)(x+Math.cos(ang*a)*(r.nextDouble()*(size)+size/2));
			yp[a]=(int)(y+Math.sin(ang*a)*(r.nextDouble()*(size)+size/2));
		}
//		double tx=Math.cos(a)
		xp[n-1]=xp[0];
		yp[n-1]=yp[0];
	}
	
	public String toString() {
		return 
		asteroidType +" " +
		id + " " 
		+ Stuff.trunc(x,2) + " " + 
		Stuff.trunc(y,2) + " " + 
		Stuff.trunc(dir,3) + " " + 
		Stuff.trunc(vx,4) + " " + 
		Stuff.trunc(vy,4) +" "+ 

		size+" "+
		randSeed;
	}

	public static Asteroid parse(String[] t) {
		double x=Double.parseDouble(t[2]);
		double y=Double.parseDouble(t[3]);
		double dir=Double.parseDouble(t[4]);
		double vx=Double.parseDouble(t[5]);
		double vy=Double.parseDouble(t[6]);
		int size=Integer.parseInt(t[7]);
		int rand=Integer.parseInt(t[8]); 
		Asteroid a=new Asteroid(t[1],x,y,dir,vx,vy,size,rand);
		return a;
	}
	
	static int asteroidCount=(int)(Math.random()*1000);
	public static Asteroid createRandom() {
		Asteroid a=new Asteroid(
				"A"+asteroidCount++, 
				Math.random()*MapView.ARENASIZE,
				Math.random()*MapView.ARENASIZE,
				Math.random()*360,
				Math.random()*2-1,
				Math.random()*2-1,
				Asteroid.LARGE,
				((int)Math.random()*1000));
		a.setBreader(true);
		return a;
		}

	private void setBreader(boolean b) {
		breader=b;
	}

	public boolean isBreader() {
		return breader;
	}
}
