package aj.testing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.Vector;

public class StarField extends Canvas implements Runnable {
	public static void main(String s[]) {
		Frame f=new Frame();
		StarField sf=new StarField();
		f.add("Center",	sf);
		f.addWindowListener(new aj.awt.SimpleWindowManager());
		f.setSize(new Dimension(200,200));
		f.setVisible(true);
		new Thread(sf).start();
	}

	Vector allStars=null;
	public StarField() {
		setBackground(Color.black);
		setForeground(Color.white);
	}

	//public void update(Graphics g) {
		//paint(g);
	//}
	public void paint(Graphics g) {
		if (g==null) return;
		int w=(int)getSize().getWidth();
		int h=(int)getSize().getHeight();
		g.translate(w/2,h/2);
		if (allStars==null) {
			allStars=new Vector();
			for (int a=0;a<100;a++) {
				Star s=new Star(w,h);
				allStars.addElement(s);
			}
		}
		int numstar=(int)Math.pow(w*h,.5);
		while (allStars.size()<numstar) {
			Star s=new Star(w,h);
			allStars.addElement(s);
		}
		while (allStars.size()>numstar) {
			allStars.removeElementAt((int)(allStars.size()*Math.random()));
		}
		
		for (int a=0;a<allStars.size();a++) {
			Star s=(Star)allStars.elementAt(a);
			s.display(g);
			s.age(w,h);
		}
	}
	
	public void run() {
		while (true) {
			long t=System.currentTimeMillis();
			repaint();
			int SLEEPTIME=50;
			long t2=System.currentTimeMillis();
			while (t2-t<SLEEPTIME) {
				try {
					Thread.yield();
					Thread.sleep(t2-t);
				} catch (Exception e) {
				}	
				Thread.yield();
				t2=System.currentTimeMillis();
			}
		}
	}
}

class Star {
	int startx,starty,age;
	public Star(int w,int h) {
		startx=(int)(Math.random()*w-w/2);
		starty=(int)(Math.random()*h-h/2);
		if (startx==0) startx=(Math.random()<.5?1:-1);
		if (starty==0) starty=(Math.random()<.5?1:-1);
		age=0;
	}
		
	public void age(int w,int h) {
		age=Math.max(age+1,(int)Math.pow(age,1.05));
		int ex=startx+startx*age/20;
		int ey=starty+starty*age/20;
		if (Math.abs(ex)>w || Math.abs(ey)>h) {
			startx=(int)(Math.random()*w-w/2);
			starty=(int)(Math.random()*h-h/2);
			if (startx==0) startx=(Math.random()<.5?1:-1);
			if (starty==0) starty=(Math.random()<.5?1:-1);
			age=0;
		}
	}

	public void display(Graphics g) {
		int ex=startx+startx*age/20;
		int ey=starty+starty*age/20;
		g.setColor(Color.white);
		if (age<20) {
			g.setColor(new Color(age*255/20,age*255/20,age*255/20));
		}
		if (age>30) {
			if (age/15>8) {age=10000;return;}
			//g.fillOval(ex-age/30,ey-age/30,age/15,age/15);
			g.drawLine(ex,ey,ex-startx/30,ey-starty/30);
		}
		else {
			//g.drawLine(ex,ey,ex,ey);
			g.fillOval(ex-1,ey-1,2,2);
		}
	}
}
