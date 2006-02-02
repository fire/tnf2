package aj.life;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;

/*
 * First versoin.
 * Slow.
 * random startup colonies
 * looks like bad algorythm
 * non-interactive
 * */
public class Life extends Applet {

	public static void main(String s[]) {
		Frame F=new Frame("Life World 1.0");
		LifeDisplay ld=new LifeDisplay();
		F.add("Center",ld);
		F.setVisible(true);
		F.pack();
		new Thread(ld).start();
	}
//color button - drop new coloney
//LifeForm rules 2-4,3-5,2-4 (min-maxbreed.min-maxlife.min-maxprey.prey)
//world size
//number of types

	public void init() {
		try {
			String s = getParameter("size");
			LifeDisplay.MAX=Integer.parseInt(s);
			s = getParameter("colonies");
			LifeDisplay.COLONIES=Integer.parseInt(s);
			s = getParameter("colsize");
			LifeDisplay.COLSIZE=Integer.parseInt(s);
			
		} catch (NumberFormatException nfe) {}
		LifeDisplay ld=new LifeDisplay();
		this.add("Center",ld);
		new Thread(ld).start();
	}
}

class LifeDisplay extends Canvas implements Runnable {
	static int MAX=100;
	static int COLONIES=100;	
	static int COLSIZE=8;

	byte map[][];
	public LifeDisplay() {
		map=new byte[MAX][];
	      for (int a=0;a<MAX;a++) {
			map[a]=new byte[MAX];
			for (int b=0;b<MAX;b++) {
				map[a][b]=0;
			}
		}
		doRandomSetup();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(300);
				next();
				repaint();
			}catch (Exception ei) {
			}
		}
	}

	public void next() {
		byte next[][]=new byte[MAX][];
		for (int a=0;a<MAX;a++) {
			next[a]=new byte[MAX];
		}
		for (int a=0;a<MAX;a++) {
			for (int b=0;b<MAX;b++) {
				int inc=(int)(Math.random()*4+1);
				for (int d=0;d<4;d++) {
					int t=(inc+d)%4+1;
					int c=count(a,b,t);
					if (c>=3 && c<=4 && next[a][b]==0) next[a][b]=(byte)t;
				}
			}
		}
		map=next;
	}

	public int count(int y,int x,int type) {
		int total=0;

		if (map[y][x]==1) total++;

		if (x-1>=0 && y-1>=0 && map[y-1][x-1]==type) total++;
		if (x-1>=0 && y+1<MAX && map[y+1][x-1]==type) total++;

		if (x+1<MAX && y-1>=0 && map[y-1][x+1]==type) total++;
		if (x+1<MAX && y+1<MAX && map[y+1][x+1]==type) total++;

		if (x-1>=0 && map[y][x-1]==type) total++;
		if (x+1<MAX && map[y][x+1]==type) total++;

		if (y+1<MAX && map[y+1][x]==type) total++;
		if (y-1>=0 && map[y-1][x]==type) total++;

		return total;
	}
	public void doRandomSetup() {
		for (int a=0;a<COLONIES;a++) {
			int y=(int)(Math.random()*MAX);
			int x=(int)(Math.random()*MAX);
			byte t=(byte)(Math.random()*4+1);
			int num=(byte)(Math.random()*COLSIZE/2+COLSIZE/2);
			for (int b=0;b<num;b++) {
				int lx=x+(int)(Math.random()*Math.pow(COLSIZE,.5)-(Math.pow(COLSIZE,.5)/2));
				int ly=y+(int)(Math.random()*Math.pow(COLSIZE,.5)-(Math.pow(COLSIZE,.5)/2));
				if (lx<0) lx=0;
				if (ly<0) ly=0;
				if (lx>=MAX) lx=MAX-1;
				if (ly>=MAX) ly=MAX-1;
				map[ly][lx]=t;
			}
		}
	}
	public void paint(Graphics g) {
		if (g==null || map==null) return;
		double dx=Math.max(1,1.0*getWidth()/MAX);
		double dy=Math.max(1,1.0*getHeight()/MAX);
		for (int a=0;a<MAX;a++) {
			for (int b=0;b<MAX;b++) {
				if (map[a][b]!=0) {
					if (map[a][b]==1) g.setColor(Color.blue);
					if (map[a][b]==2) g.setColor(Color.red);
					if (map[a][b]==3) g.setColor(Color.gray);
					if (map[a][b]==4) g.setColor(Color.green);
					g.fillRect((int)(dx*a),(int)(dy*b),(int)(dx),(int)(dy));
				}
			}
		}
	}
	public Dimension getPreferredSize(){
		return new Dimension(400,400);
	}
}
//class LifeWorld {
//}
//class LifeForm {
//  int bornNum=2;
//  int starveNum=4;
//  int minAge=0;
//  int maxAge=100;
//  int currAge=0;
//  int id=0;
//}
