package aj.combat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class MapView extends JPanel { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int ARENASIZE = 1000;
	
	private Vector displayItems=new Vector();
	
	public void setDisplayItems(Vector v) {displayItems=v;}
	
	public Dimension getPreferredSize() {
		return new Dimension(800, 640);
	}

	boolean infoOn;
	
	public MapView() {
		this.setOpaque(true);//(Color.black);
		this.setBackground(Color.black);
	}
	
	Image i=null;
	public void paint(Graphics G) {
		if (i==null || i.getWidth(this)!=getWidth() || i.getHeight(this)!=getHeight()) {
			i=createImage(getWidth(),getHeight());
		}
		Graphics g=i.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0,0,getWidth(),getHeight());
		if (infoOn) displayInfo(g);

		g.setColor(Color.white);
		
		g.translate(getWidth()/2-(int)myShip.x,getHeight()/2-(int)myShip.y);

		drawStars(g);
		g.setColor(Color.darkGray);
		g.drawRect(0,0,ARENASIZE,ARENASIZE);
		for (int c=0; c < displayItems.size(); c++) {
			CombatItem t = (CombatItem) displayItems.elementAt(c);
			if (t==null) System.out.println("t==null");
			if (g==null) System.out.println("g==null");
			t.display(g);
		}

		for (int a=-1;a<2;a++) {
			for (int b=-1;b<2;b++) {
				if (b==0 && a==0) continue;
				if (myShip.x>MapView.ARENASIZE/4 && a==-1) {
					continue;
				}
				if (myShip.x<MapView.ARENASIZE*3/4 && a==1) {
					continue;
				}
				if (myShip.y>MapView.ARENASIZE/4 && b==-1) {
					continue;
				}
				if (myShip.y<MapView.ARENASIZE*3/4 && b==1) {
					continue;
				}

				g.translate(a*ARENASIZE,b*ARENASIZE);
				drawStars(g);
				for (int c=0; c < displayItems.size(); c++) {
					CombatItem t = (CombatItem) displayItems.elementAt(c);
					Thing tt=(Thing)t;
//					if (
//					(myShip.x<getWidth()/2 && tt.x>getWidth()/2 && a==-1) ||
//					(myShip.x>getWidth()/2 && tt.x<getWidth()/2 && a==1) 
//					|| (myShip.y>getHeight()/2 && tt.y<getHeight()/2 && b==1) 
//					|| (myShip.y<getHeight()/2 && tt.y>getHeight()/2 && b==-1) 
//					)
					t.display(g);
				}
				g.translate(-a*ARENASIZE,-b*ARENASIZE);
			}
		}
		G.drawImage(i,0,0,this);
	}

	int randomSeed=72532;//(int)(Math.random()*1000);
	private void drawStars(Graphics g) {
		Random r=new Random(randomSeed);
		for (int a=0;a<50;a++) {
			int x=Math.abs(r.nextInt()%ARENASIZE);
			int y=Math.abs(r.nextInt()%ARENASIZE);
			Color c[]={Color.red,Color.yellow,Color.lightGray,Color.blue,Color.green,Color.magenta,Color.cyan,Color.orange,Color.pink};
//			Color c[]={Color.red};
			int ci=(int)(r.nextDouble()*c.length);
			g.setColor(c[ci]);
			g.drawLine(x,y,x,y);
		}
	}

	private void displayInfo(Graphics g) {
		String help="move      (asdw) (hjkl) (2468)\n" +
				"fire      <space> (5)\n"+
				"center    'c'\n"+
				"mouseTurn 'm'\n"+
				"help      '?' '/'";
		int count=0;
		g.setColor(Color.white);
		while (help.indexOf("\n")>=0) {
			String line=help.substring(0,help.indexOf("\n"));
			help=help.substring(help.indexOf("\n")+1);
			count++;
			g.drawString(line,50,50+15*count);
		}
	}

	Ship myShip;
	public void setCenterItem(Ship centerItem) {
		this.myShip=centerItem;
	}

	

}

