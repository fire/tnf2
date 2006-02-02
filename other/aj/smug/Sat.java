package aj.smug;

import java.awt.Color;
import java.awt.Graphics;

public class Sat {
	int orbsiz,siz;
	double sr=Math.random()*Math.PI*2;
	double day=Math.random();
	PlanetDisplay p;
	Color lite;
		
	public void setTime(double d){
		double dt=d-day;
		dt=dt*5*p.siz/orbsiz;///p.siz;
		//if (siz<3) dt=dt*2;//d-day;
		d=day+dt;
		if (d>1) d=0;
		if (d<0) d=1;
		day=d;
	}
	public double getTime() {return day;}

	public boolean front() {return (day>0 && day<.5);}
	public Sat(Color c,PlanetDisplay p,int s,int r) {
		this.p=p;
		orbsiz=s;
		siz=r;
		if (siz>2)
			lite=PlanetDisplay.moon;
		else
			lite=PlanetDisplay.sat;
		if (c!=null) lite=c;
	}
	public void display(Graphics g,double scale) {
		int curorb=(int)(orbsiz*Math.cos(Math.PI*2*day));
		int cx=p.cx+(int)(Math.cos(sr)*curorb);
		int cy=p.cy+(int)(Math.sin(sr)*curorb);
		if (siz>2) {
			cx=p.cx+(int)(Math.cos(p.mr)*curorb);
			cy=p.cy+(int)(Math.sin(p.mr)*curorb);
			PlanetDisplay.display(g,cx,cy,siz,p.day,lite);
		}
		else if (siz==2) {
			g.setColor(lite);
			g.fillRect(cx,cy,2,2);//cx,cy);
		}
		else {
			g.setColor(lite);
			g.drawLine(cx,cy,cx,cy);
		}
	}

}
