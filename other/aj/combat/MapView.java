package aj.combat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import aj.misc.Stuff;

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

	static int ZONESIZE = 400;

	private Vector displayItems=new Vector();
	
	public void setDisplayItems(Vector v) {displayItems=v;}
	
	public Dimension getPreferredSize() {
		return new Dimension(ZONESIZE, ZONESIZE);
	}

	boolean infoOn;
	
	public MapView() {
		this.setOpaque(true);//(Color.black);
		this.setBackground(Color.black);
	}
	
	public void paint(Graphics g) {
		System.out.println("dis items="+displayItems.size());
		g.translate(getWidth()/2-(int)myShip.x,getHeight()/2-(int)myShip.y);
		g.setColor(Color.black);
		g.fillRect(1, 1, ZONESIZE - 1, ZONESIZE - 1);
		drawStars(g);
		if (infoOn) displayInfo(g);
		for (int a=0; a < displayItems.size(); a++) {
			CombatItem t = (CombatItem) displayItems.elementAt(a);
			t.display(g);
		}
	}

	int randomSeed=72532;//(int)(Math.random()*1000);
	private void drawStars(Graphics g) {
		Random r=new Random(randomSeed);
		for (int a=0;a<50;a++) {
			int x=Math.abs(r.nextInt()%ZONESIZE);
			int y=Math.abs(r.nextInt()%ZONESIZE);
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
		while (help.indexOf("\n")>=0) {
			String line=help.substring(0,help.indexOf("\n"));
			help=help.substring(0,help.indexOf("\n")+1);
			count++;
			g.drawString(line,0,15*count);
		}
	}

	Ship myShip;
	public void setCenterItem(Ship centerItem) {
		this.myShip=centerItem;
	}

	

}

