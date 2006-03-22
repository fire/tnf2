package aj.smug;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import aj.awt.SimpleWindowManager;

public class PlanetView extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Color backcolor=Color.black;
	static int screensize=200;
	static int maxSize=230;
	static int minSize=15;

	int size;
	int cent;
	PlanetDisplay p;

	public static void main(String s[]) {
		PlanetView pv=null;
		try {
			if (s.length==1) {
				pv=new PlanetView(Integer.parseInt(s[0]),-1,-1,-1,-1);
			}
			else if (s.length==2) {
				pv=new PlanetView(Integer.parseInt(s[0]),Integer.parseInt(s[1]),-1,-1,-1);
			}
			else if (s.length==3) {
				pv=new PlanetView(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2]),-1,-1);
			}
			else if (s.length==4) {
				pv=new PlanetView(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2]),Integer.parseInt(s[3]),-1);
			}
			else if (s.length==5) {
				pv=new PlanetView(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2]),Integer.parseInt(s[3]),Integer.parseInt(s[4]));
			}
			else {
				pv=new PlanetView(-1,-1,-1,-1,-1);
				System.out.println("FORMAT: java aj.smug.PlanetView <diameter|-1> <moons|-1> <satalites|-1> <neb 0,1|-1> <seed|-1>");
			}
		}catch (NumberFormatException NFE) {
			pv=new PlanetView(-1,-1,-1,-1,-1);
		}
		if (pv!=null) {
			Frame f=new Frame();
			f.add("Center",pv);
			f.setSize(new Dimension(screensize+5,screensize+25));
			f.setVisible(true);
			f.addWindowListener(new SimpleWindowManager());
		}
	}

	public PlanetView(int s,int moons,int sat,int neb,int seed) {
		size=s;
		if (size==-1){
			size=(int)(Math.random()*(maxSize-minSize)+minSize);
		}
		if (size<minSize) size=minSize;
		if (size>maxSize) size=maxSize;
		screensize=size*4;
		cent=size*2;
		p=new PlanetDisplay(null,cent,cent,size,moons,sat,neb,seed);
		setBackground(backcolor);
	}

	boolean stop=false;
	public void start() {repaint();}
	public void stop() {stop=true;}

	Image i=null,i2=null;
	public void update(Graphics g) {paint(g);}
	public void paint(Graphics g) {
		if (i==null) {	
			i=createImage(screensize,screensize);
		}
		if (i2==null) {
			Random r=p.getRandom();
			i2=createImage(screensize,screensize);
			Graphics G2=i2.getGraphics();
			Color c=new Color((int)(100+155*r.nextDouble()),(int)(100+155*r.nextDouble()),(int)(100+155*r.nextDouble()));
			if (p.hasNebula()) {
				aj.awt.Neb.build(G2,c,screensize,6,screensize*screensize*60,false,false,r);
			}
//stars
			for (int a=0;a<screensize*screensize/30;a++) {
				c=new Color((int)(100+155*r.nextDouble()),(int)(100+155*r.nextDouble()),(int)(100+155*r.nextDouble()));
				G2.setColor(c);
				int xx = (int) (screensize*2*r.nextDouble());
				int yy = (int) (screensize*2*r.nextDouble());
				G2.drawLine(xx, yy, xx, yy);
			}
		}
		Graphics G=i.getGraphics();
		G.clearRect(0,0,screensize,screensize);
		G.drawImage(i2,0,0,this);
		p.setTime(p.getTime()+p.year/1000);
		p.display(G,1.0);
		g.drawImage(i,0,0,this);
		try {Thread.sleep(200);}catch(Exception e) {}
		if (!stop) repaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension(screensize,screensize);
	}
}
