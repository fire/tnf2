package aj.man;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;

import aj.awt.SimpleWindowManager;

public class View extends Canvas implements Runnable {
	static int SLEEPTIME=400;
	static int STEPCOUNT=8;
	static boolean DEBUG=true;

	static int pos[]={1,2};

	public static void main(String s[]) { 
		if (s.length>=2) {
			try {
				pos=new int[s.length];
				for (int a=0;a<s.length;a++) {
					pos[a]=Integer.parseInt(s[a]);
				}
			} catch (NumberFormatException e) {
				System.out.println("setup bad read");
				pos=new int[2];
				pos[0]=1;pos[1]=2;
			}
		}
		Frame f=new Frame();
		View v=new View();
		f.add("Center",v);
		f.setSize(new Dimension(400,250));
		f.setVisible(true);
		f.addWindowListener(new SimpleWindowManager());
		new Thread(v).start();
	}

	Man mp[]=null;

	public Dimension getPreferredSize() {
		return new Dimension (200,200);
	}

	public void paint(Graphics g) {
		Man m=null;
		if (mp!=null) {
			int stepcount=100/mp.length+1;
			int stid=count/stepcount;
			int enid=count/stepcount+1;
			if (enid==mp.length) enid=0;
			Man sm=mp[stid];
			Man em=mp[enid];
			m=Man.between(em.pos,sm.pos,(1.0*count%stepcount)/stepcount);
			m.x=50;m.y=50;
			m.draw(g);
		}
		else {
			mp=new Man[pos.length];
			for (int a=0;a<pos.length;a++) {
				mp[a]=new Man(pos[a]);
				mp[a].x=100+50*a;mp[a].y=50;
			}
		}
		for (int a=0;a<mp.length;a++) {
			if (DEBUG) mp[a].draw(g);
		}
	}

	int count=0;	
	public void run() {
		while (true) {
			long t=System.currentTimeMillis();
			count=(count+STEPCOUNT)%100;
			repaint();
			Thread.yield();
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
