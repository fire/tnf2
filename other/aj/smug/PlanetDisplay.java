package aj.smug;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.util.Vector;

public class PlanetDisplay {
	static int minMoons=0;
	static int minSats=0;
	static int maxMoons=8;
	static int maxSats=30;

	static Color off=Color.darkGray;
	static Color planetcolor=Color.gray;
	static Color moon=Color.gray.brighter();
	static Color sat=Color.white;

	Color lite=planetcolor;
	double day=Math.random();
	double hour=1/24.0;
 	double year=1;
	int siz,cx,cy;
	Vector satlist=new Vector();
	Random r;
	double mr=Math.random()*Math.PI*2;

	//TODO rings
	//  draw three images
	//    first back ground ring
	//    second planet
	//    third for ground ring with clear color over planet

	public Random getRandom(){return r;}
	public boolean hasNebula() {return neb;}

	public void addSat(Sat s) {
		satlist.addElement(s);
	}
	boolean neb;
	public PlanetDisplay(Color c,int cx,int cy,int siz,int nummoons,int numsats,int neb,int seed) {
		if (seed==-1)
			r=new Random((int)(Math.random()*Integer.MAX_VALUE));
		else r=new Random(seed);
		if (c==null) {
			c=new Color((int)(100+155*r.nextDouble()),(int)(100+155*r.nextDouble()),(int)(100+155*r.nextDouble()));
		}		
		if (neb==-1) this.neb=r.nextDouble()*2>1;
		else this.neb=neb==1;
		if (c!=null) lite=c;
		else lite=planetcolor;
		this.cx=cx;this.cy=cy;this.siz=siz;
		if (nummoons<0) nummoons=(int)(r.nextDouble()*(maxMoons-minMoons)+minMoons);
		if (numsats<0) numsats=(int)(r.nextDouble()*(maxSats-minSats)+minSats);
//		if (nummoons>maxMoons) nummoons=maxMoons;
//		if (numsats>maxSats) numsats=maxSats;
		for (int b=0;b<numsats;b++) {
			Sat ss=new Sat(null,this,(int)(siz/2+siz/6*r.nextDouble()),(int)(r.nextDouble()*2+1));
			this.addSat(ss);
		}
		for (int a=0;a<nummoons;a++) {
			Color cc=new Color((int)(100+155*r.nextDouble()),(int)(100+155*r.nextDouble()),(int)(100+155*r.nextDouble()));
			Sat ss=new Sat(cc,this,(int)(siz/2+siz*r.nextDouble()),(int)(siz/4*r.nextDouble()+3));
			this.addSat(ss);
		}
	}
	
	public void setTime(double d){
		double dt=d-day;
		if (d>1) d=0;
		if (d<0) d=1;
		day=d;
		for (int a=0;a<satlist.size();a++) {
			Sat s=(Sat)satlist.elementAt(a);
			s.setTime(s.getTime()+dt);
		}

	}
	public double getTime() {return day;}
	public void display(Graphics g,double scale) {
		for (int a=0;a<satlist.size();a++) {
			Sat ss=(Sat)satlist.elementAt(a);
			if (!ss.front())
				ss.display(g,scale);
		}
		display(g,cx,cy,siz,day,lite);
		for (int a=0;a<satlist.size();a++) {
			Sat ss=(Sat)satlist.elementAt(a);
			if (ss.front())
				ss.display(g,scale);
		}
	}
	public static void display(Graphics g,int cx,int cy,int siz,double day,Color on) {
		Color off=on.darker().darker();
		double r=day;
		int s=siz;
		if (r<.25) {
			r*=4;
			g.setColor(on);
			g.fillArc(cx-s/2,cy-s/2,s,s,90,180);
			g.setColor(off);
			g.fillArc(cx-s/2,cy-s/2,s,s,270,180);
			g.fillOval((int)(cx-s/2+s/2*r),cy-s/2,(int)(s-s*r),s);
		}
		else if (r<.5) {
			r=1-(r-.25)*4;
			g.setColor(off);
			g.fillArc(cx-s/2,cy-s/2,s,s,270,180);
			g.setColor(on);
			g.fillArc(cx-s/2,cy-s/2,s,s,90,180);
			g.fillOval((int)(cx-s/2+s/2*r),cy-s/2,(int)(s-s*r),s);
		}
		else if (r<.75) {
			r=(r-.50)*4;
			g.setColor(off);
			g.fillArc(cx-s/2,cy-s/2,s,s,90,180);
			g.setColor(on);
			g.fillArc(cx-s/2,cy-s/2,s,s,270,180);
			g.fillOval((int)(cx-s/2+s/2*r),cy-s/2,(int)(s-s*r),s);
		}
		else if (r<1) {
			r=1-(r-.75)*4;
			g.setColor(on);
			g.fillArc(cx-s/2,cy-s/2,s,s,270,180);
			g.setColor(off);
			g.fillArc(cx-s/2,cy-s/2,s,s,90,180);
			g.fillOval((int)(cx-s/2+s/2*r),cy-s/2,(int)(s-s*r),s);
		}
	}

}
