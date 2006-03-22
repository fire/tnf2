package aj.life;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;

/*
 * Dynamic screen size
 *
 * */
public class Life3 extends Applet implements ActionListener, MouseListener, MouseMotionListener{

//speed
//draw color
//rules


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String s[]) {
		for (int a=0;a<s.length;a++) {
			//if (s[a].toUpperCase().indexOf("SIZE")>=0
		}
		Frame F=new Frame("Life World 1.0");
		ld=new Life3Display();
		F.add("Center",ld);
		Panel p=new Panel(new FlowLayout());
		p.add(stop);
		p.add(step);
		Life3 l2=new Life3();
		stop.addActionListener(l2);
		step.addActionListener(l2);
		F.add("North",p);
		ld.addMouseListener(l2);
		ld.addMouseMotionListener(l2);
		F.setVisible(true);
		F.pack();
		new Thread(ld).start();
	}

	public void init() {
		try {
			String s = getParameter("size");
			Life3Map.defaultStartSize=Integer.parseInt(s);
			s = getParameter("colonies");
			Life3Map.startCellGroups=Integer.parseInt(s);
			s = getParameter("colsize");
			Life3Map.numCellsPerGroup=Integer.parseInt(s);

		} catch (NumberFormatException nfe) {}
		ld=new Life3Display();
		//this.setLayoutManager(new BorderLayout());
		this.add("Center",ld);
		Panel p=new Panel(new FlowLayout());
		p.add(stop);
		p.add(step);
		stop.addActionListener(this);
		step.addActionListener(this);
		this.add("North",p);
		ld.addMouseListener(this);
		ld.addMouseMotionListener(this);
		new Thread(ld).start();
	}
	static Life3Display ld;

	static Button stop=new Button("Stop");
	static Button step=new Button("Step");

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==stop) {
			ld.STOP=!ld.STOP;
			if (ld.STOP) stop.setLabel("Start");
			if (!ld.STOP) stop.setLabel("Stop");
		}
		if (ae.getSource()==step) {
			ld.next();
			ld.repaint();
			ld.STOP=true;
			if (ld.STOP) stop.setLabel("Start");
		}
	}

	public void mouseClicked(MouseEvent e) {
		Point p=e.getPoint();
		ld.clickPoint(p);
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void mouseDragged(MouseEvent e) {
		Point p=e.getPoint();
		ld.movePoint(p);
	}
	public void mouseMoved(MouseEvent e) {}
 


}


class Life3Display extends Canvas implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Life3Map lm;

	boolean STOP=false;

	public Life3Display() {
		lm=new Life3Map();
		lm.doRandomSetup();
	}

	Color clickColor=Color.blue;

	public void clickPoint(Point p) {
		double dx=Math.max(1,1.0*getWidth()/(lm.maxx-lm.minx));
		double dy=Math.max(1,1.0*getHeight()/(lm.maxy-lm.miny));
		int y=(int)(p.getY()/dy+lm.miny);
		int x=(int)(p.getX()/dx+lm.minx);

		if (lm.get(x,y)==null) {
			LifeCell lc=new LifeCell(x,y,clickColor,0);
			lm.set(lc);
		}
		else {
			lm.remove(x,y);
		}
		repaint();
	}

	int moveX,moveY;
	public void movePoint(Point p) {
		double dx=Math.max(1,1.0*getWidth()/(lm.maxx-lm.minx));
		double dy=Math.max(1,1.0*getHeight()/(lm.maxy-lm.miny));
		int y=(int)(p.getY()/dy+lm.miny);
		int x=(int)(p.getX()/dx+lm.minx);

		if (moveX==x && moveY==y) return;
		moveX=x;moveY=y;
		if (lm.get(x,y)==null) {
			LifeCell lc=new LifeCell(x,y,clickColor,0);
			lm.set(lc);
		}
		else {
			lm.remove(x,y);
		}
		repaint();
	}
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
				if (STOP) continue;
				next();
				repaint();
			}catch (Exception ei) {
			}
		}
	}

	public void next() {
		Life3Map next=new Life3Map();
		for (int a=lm.minx-3;a<=lm.maxx+3;a++) {
			for (int b=lm.miny-3;b<lm.maxy+3;b++) {
				int c=count(b,a);
				LifeCell lc=lm.get(b,a);
				if (lc==null  && c==3) {
					next.set(new LifeCell(b,a,avColor(b,a),0));
				}
				else if (lc!=null && (c==2 || c==3)) {
					lc.age++;
					next.set(lc);
				}
			}
		}
		lm=next;
	}

	public Color addColor(Color c,int ccount,Color m){
		if (ccount==0) return m;

		int red=c.getRed()*ccount+m.getRed();
		int green=c.getGreen()*ccount+m.getGreen();
		int blue=c.getBlue()*ccount+m.getBlue();
		ccount++;
		red=(red/ccount)%256;
		green=(green/ccount)%256;
		blue=(blue/ccount)%256;
		return new Color(red,green,blue);
	}

	public Color avColor(int x,int y) {
		Color c=Color.black;
		int ccount=0;
		LifeCell lc=lm.get(x,y-1);
		if (lc!=null) {c=addColor(c,ccount,lc.color);ccount++;}
		lc=lm.get(x-1,y-1);
		if (lc!=null) {c=addColor(c,ccount,lc.color);ccount++;}
		lc=lm.get(x-1,y);
		if (lc!=null) {c=addColor(c,ccount,lc.color);ccount++;}
		lc=lm.get(x+1,y);
		if (lc!=null) {c=addColor(c,ccount,lc.color);ccount++;}
		lc=lm.get(x+1,y+1);
		if (lc!=null) {c=addColor(c,ccount,lc.color);ccount++;}
		lc=lm.get(x,y+1);
		if (lc!=null) {c=addColor(c,ccount,lc.color);ccount++;}
		lc=lm.get(x-1,y+1);
		if (lc!=null) {c=addColor(c,ccount,lc.color);ccount++;}
		lc=lm.get(x+1,y-1);
		if (lc!=null) {c=addColor(c,ccount,lc.color);ccount++;}
		return c;
	}

	public int count(int x,int y) {
		int total=0;
		if (lm.get(x-1,y-1)!=null) total++;
		if (lm.get(x,y-1)!=null) total++;
		if (lm.get(x+1,y-1)!=null) total++;
		if (lm.get(x+1,y)!=null) total++;
		if (lm.get(x+1,y+1)!=null) total++;
		if (lm.get(x,y+1)!=null) total++;
		if (lm.get(x-1,y+1)!=null) total++;
		if (lm.get(x-1,y)!=null) total++;
		return total;
	}

	Image i;
	public void update(Graphics g) {paint(g);}
	public void paint(Graphics g) {
		if (g==null || lm==null) return;

		if (i==null) {
			i=createImage(getSize().width, getSize().height);
		}
		else if (i.getHeight(null) != getSize().height || i.getWidth(null) != getSize().width) {
			i = createImage(getSize().width, getSize().height);
		}	
		Graphics G = i.getGraphics();
		G.clearRect(0,0,i.getWidth(this),i.getHeight(this));
		//G.translate(-i.getWidth(this)/2,-i.getHeight(this)/2);

		double dx=Math.max(1,1.0*getWidth()/(lm.maxx-lm.minx));
		double dy=Math.max(1,1.0*getHeight()/(lm.maxy-lm.miny));

		//System.out.println("paint called "+lm.maxx+" "+lm.minx+" "+lm.maxy+" "+lm.miny);
		for (int a=lm.minx;a<=lm.maxx;a++) {
			for (int b=lm.miny;b<lm.maxy;b++) {
				LifeCell lc=lm.get(a,b);
				if (lc==null) continue;
				G.setColor(lc.color);
				G.fillRect((int)(dx*(a-lm.minx)),(int)(dy*(b-lm.miny)),(int)(dx),(int)(dy));
			}
		}
		g.drawImage(i,0,0,this);
	}
	public Dimension getPreferredSize(){
		return new Dimension(400,400);
	}
}


//rules - life born 23/3  liv/born
//high life 23/36
//lacy  /234
//amazing 12345/3
//rats 1234/37
//coagulation 125678/367
//coral 45678/3
//235678/3678

class Life3Map {
	static int startCellGroups=33;
	static int numCellsPerGroup=24;
	static int defaultStartSize=53;

	Hashtable ht=new Hashtable();
	int minx,miny,maxx,maxy;

	public LifeCell get(int x,int y) {
		return (LifeCell)ht.get(""+x+":"+y);
	}
	public void remove(int x,int y) {
		LifeCell lc=(LifeCell)ht.get(""+x+":"+y);
		if (lc!=null) ht.remove(lc);
	}
	public void set(LifeCell lc){
		if (ht.isEmpty()) {
			maxx=minx=lc.x;
			maxy=miny=lc.y;
		}
		maxx=Math.max(maxx,lc.x);
		maxy=Math.max(maxy,lc.y);
		minx=Math.min(minx,lc.x);
		miny=Math.min(miny,lc.y);
		ht.put(lc.x+":"+lc.y,lc);
	}
	public void doRandomSetup() {
		for (int a=0;a<startCellGroups;a++) {
			int y=(int)(Math.random()*defaultStartSize);
			int x=(int)(Math.random()*defaultStartSize);
			byte t=(byte)(Math.random()*3+1);
			Color ct=Color.blue;
			if (t==2) ct=Color.red;
			if (t==3) ct=Color.green;
			int num=(byte)(Math.random()*numCellsPerGroup/2+numCellsPerGroup/2);
			for (int b=0;b<num;b++) {
				int lx=x+(int)(Math.random()*Math.pow(numCellsPerGroup,.9)-(Math.pow(numCellsPerGroup,.9)/2));
				int ly=y+(int)(Math.random()*Math.pow(numCellsPerGroup,.9)-(Math.pow(numCellsPerGroup,.9)/2));
				if (lx<0) lx=0;
				if (ly<0) ly=0;
				if (lx>=defaultStartSize) lx=defaultStartSize-1;
				if (ly>=defaultStartSize) ly=defaultStartSize-1;
				LifeCell lc=new LifeCell(lx,ly,ct,0);
				set(lc);
			}
		}
	}
}

class LifeCell {
	Color color;
	int x,y;
	int age;
	public LifeCell(int x,int y, Color c, int age) {
		this.x=x;this.y=y;this.color=c;this.age=age;
	}
}
