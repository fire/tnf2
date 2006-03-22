package aj.combat;

import java.awt.BorderLayout;
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
public class Player extends JPanel implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int ZONESIZE = 400;

	static int SHIPMAXSHOTCOUNT = 10;
	static int SHIPMAXTURN = 15;
	static int MAXSHIPSPEED = 8;
	static double SHIPMAXACCEL = .4;
	static int SHIPSIZE = 8;

	static int SHOTSIZE = 2;
	static int MAXSHOTRANGE=1000;
	static int MAXSHOTSPEED=10;
	static double FRICTION = .995;

//command delays
	static int REDRAWDELAY = 30;
	static int SENDDELAY = 150;
	static int MINREDRAWDELAY = 50;
	static int MINSHOTDELAY = 150;
	static int MINTURNDELAY = 30;
	static int MINMOVEDELAY = 30;
	static int MINCOMMANDDELAY=30;

	OutputStream out=null;
	int id=-1;
	boolean changed = false;
	long lastfire;
	Vector items = new Vector();
	Hashtable itemsHashtable = new Hashtable();
	Ship myShip;
	Vector myShots = new Vector();
	static String gname = "combat";

	public Player() {
	}

	public void doSend() {
		send("mov " + myShip);
	}
	public void send(String s) {
		if (out!=null)
			try {
				out.write((s+"\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public Dimension getPreferredSize() {
		return new Dimension(ZONESIZE, ZONESIZE);
	}

	public void quit() {
	}

	public void join() {
		send("rep");
		//repost all ids
		myShip = Ship.rand("" + id);
		send("new " + myShip);
	}

	long lastmove=0;
	public void moveUp() {
		if (!myShip.alive) {
			return;
		}
		if (lastmove + MINMOVEDELAY < System.currentTimeMillis()) {
			changed = true;
			myShip.vx += SHIPMAXACCEL * Math.cos(myShip.dir);
			myShip.vy += SHIPMAXACCEL * Math.sin(myShip.dir);
			lastmove= System.currentTimeMillis();
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void moveDown() {
		if (!myShip.alive) {
			return;
		}
		if (lastmove + MINMOVEDELAY < System.currentTimeMillis()) {
			changed = true;
			myShip.vx -= SHIPMAXACCEL * Math.cos(myShip.dir);
			myShip.vy -= SHIPMAXACCEL * Math.sin(myShip.dir);
			lastmove= System.currentTimeMillis();
		}
	}


	/**
	 *  Description of the Method 
	 */
	long lastturn=0;
	public void moveLeft() {
		if (!myShip.alive) {
			return;
		}
		if (lastturn + MINTURNDELAY < System.currentTimeMillis()) {
			myShip.dir -= SHIPMAXTURN / 180.0 * Math.PI;
			changed = true;
			lastturn = System.currentTimeMillis();
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void moveRight() {
		if (!myShip.alive) {
			return;
		}
		if (lastturn + MINTURNDELAY < System.currentTimeMillis()) {
			myShip.dir += SHIPMAXTURN / 180.0 * Math.PI;
			changed = true;
			lastturn = System.currentTimeMillis();
		}
	}

	public void fire() {
		if (!myShip.alive) {
			return;
		}
		if (lastfire + MINSHOTDELAY < System.currentTimeMillis() && myShots.size()<SHIPMAXSHOTCOUNT) {
			Shot w = myShip.shoot();
			myShots.addElement(w);
			send("new " + w.toString());
			lastfire = System.currentTimeMillis();
		}
		repaint();
	}

	public void checkCollision() {
		for (int a = 0; a < myShots.size(); a++) {
			Shot t2 = (Shot) myShots.elementAt(a);
			if (t2.expired()) {
				myShots.removeElement(t2);
				a--;
			}
		}

		for (int a = 0; a < items.size(); a++) {
			String s = (String) items.elementAt(a);
			Thing t = (Thing) itemsHashtable.get(s);
			if (t == null) {
				items.remove(s);
				a--;
				continue;
			}
			if (t instanceof Shot && ((Shot) t).expired()) {
				itemsHashtable.remove(t.id);
				items.removeElement(t.id);
				a--;
			}
			else if (myShip.inside(t) && myShip.alive) {
				myShip.setAlive(false);
				itemsHashtable.remove(t.id);
				items.removeElement(t.id);
				a--;
				send("dest " + myShip.id);
				send("dest " + t.id);
			}
			else {
				int a2;
				for (a2 = 0; a2 < myShots.size(); a2++) {
					Shot t2 = (Shot) myShots.elementAt(a2);
					if (t2.inside(t)) {
						send("dest " + t2.id);
						send("dest " + t.id);
						itemsHashtable.remove(t.id);
						items.removeElement(t.id);
						myShots.removeElement(t2);
						a2--;
						a--;
					}
				}
			}
		}
	}

	public void update(Graphics g) {
		changed=true;
		paint(g);
	}

	Image i=null;
	public void paint(Graphics G) {
		long elap = System.currentTimeMillis();
		checkCollision();
		if (i == null) {
			i = createImage(ZONESIZE, ZONESIZE);
			if (i == null) {
				System.out.println("cannot create image");
				System.exit(0);
			}
		}
		Graphics g = i.getGraphics();
		g.clearRect(0, 0, ZONESIZE, ZONESIZE);
		g.drawRect(0, 0, ZONESIZE - 1, ZONESIZE - 1);
		if (myShip==null) return;
		myShip.display(g);
		for (int a=0; a < myShots.size(); a++) {
			CombatItem t = (CombatItem) myShots.elementAt(a);
			t.display(g);
		}
		for (int a=0; a < items.size(); a++) {
			String s = (String) items.elementAt(a);
			CombatItem t = (CombatItem) itemsHashtable.get(s);
			if (t == null) {
				items.remove(s);
				continue;
			}
			//System.out.println("displaying item "+t);
			t.display(g);
		}
		G.drawImage(i, 0, 0,this.getWidth(),this.getHeight(), null);
		REDRAWDELAY = (int) (System.currentTimeMillis() - elap + MINREDRAWDELAY);
//		REDRAWDELAY = 20;//(int) (System.currentTimeMillis() - elap + MINREDRAWDELAY);
		//System.out.println(drawClock.getTick()+" redraw rate");
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public void command(String s) {
			if (s.startsWith("rep")) {
				if (myShip.alive) {
					send("mov " + myShip);
				}
				for (int a=0; a < myShots.size(); a++) {
					Shot t2 = (Shot) myShots.elementAt(a);
					send("mov " + t2.toString());
				}
			}
			else if (s.startsWith("dest ")) {
				String id = s.substring(s.indexOf(" ") + 1);
				if (id.equals(myShip.id)) {
					myShip.setAlive(false);
				}
				for (int a=0; a < myShots.size(); a++) {
					Thing t2 = (Thing) myShots.elementAt(a);
					if (t2.id.equals(id)) {
						myShots.removeElement(t2);
						break;
					}
				}

				for (int a=0; a < items.size(); a++) {
					String id2 = (String) items.elementAt(a);
					if (id2.equals(id)) {
						items.removeElement(id2);
						itemsHashtable.remove(id2);
					}
				}
			}
			else if (s.startsWith("mov ") || s.startsWith("new ")) {
				s = s.substring(s.indexOf(" ") + 1);
				CombatItem c = parse(s);
				if (c != null) {
					if (!items.contains(((Thing) c).id)) {
						items.addElement(((Thing) c).id);
					}
					itemsHashtable.put(((Thing) c).id, c);
				}
				else {
					System.out.println("bad combat item parse!" + s);
				}
			}
	}


	String currKeys="";
	public void keyPressed(KeyEvent evt) {
		char ch = Character.toUpperCase(evt.getKeyChar());
		if (currKeys.indexOf(ch)<0) currKeys+=ch;
	}
	public void keyReleased(KeyEvent evt) {
		char ch = Character.toUpperCase(evt.getKeyChar());
		if (currKeys.indexOf(ch)>=0) currKeys=currKeys.substring(0,currKeys.indexOf(ch))+currKeys.substring(currKeys.indexOf(ch)+1);
	}

	public void keyTyped(KeyEvent evt) {
	}
	
	public void command() {
//		System.out.println("currKey="+currKeys);
		if (currKeys.indexOf("X")>=0 || currKeys.indexOf("Q")>=0) {//ch == 'X' || ch == 'Q') {
			System.exit(1);
		}
		if (currKeys.indexOf("R")>=0 && !myShip.alive) {
			myShip = Ship.rand("" + id);
			send("new " + myShip.toString());
			myShip.setAlive(true);
		}
		if (currKeys.indexOf("2")>=0 || currKeys.indexOf("K")>=0) {
			moveUp();
		}
		if (currKeys.indexOf("8")>=0 || currKeys.indexOf("J")>=0 || currKeys.indexOf("S")>=0 ) {
			moveDown();
		}
		if (currKeys.indexOf("5")>=0 || currKeys.indexOf(" ")>=0 || currKeys.indexOf("W")>=0) {
			fire();
		}
		if (currKeys.indexOf("4")>=0 || currKeys.indexOf("H")>=0 || currKeys.indexOf("A")>=0 ) {
			moveLeft();
		}
		if (currKeys.indexOf("6")>=0 || currKeys.indexOf("L")>=0 || currKeys.indexOf("D")>=0 ) {
			moveRight();
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public CombatItem parse(String s) {
		try {
			String t[] = Stuff.getTokens(s);
			if (t.length < 7) {
				return null;
			}
			if (t[6].equals("-1")) {
				return new Ship(t[0], Double.parseDouble(t[1]), 
						Double.parseDouble(t[2]), 
						Double.parseDouble(t[3]), 
						Double.parseDouble(t[4]), 
						Double.parseDouble(t[5]));
			}
			else {
				return new Shot(t[0], Double.parseDouble(t[1]), 
						Double.parseDouble(t[2]), 
						Double.parseDouble(t[3]), 
						Double.parseDouble(t[4]), 
						Double.parseDouble(t[5]), 
						Integer.parseInt(t[6]));
			}
		}
		catch (NumberFormatException NFE) {
			return null;
		}
	}

	public static void main(final String s[]) {
		try {
			final Player p= new Player();
			if (s.length == 3) {
				Player.gname = s[2];
			}
			p.startThreads(s[0],s[1]);
			final JFrame f = new JFrame();
			f.addKeyListener(p);
			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add("Center", p);
			f.pack();
			f.setVisible(true);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		catch (Exception e) {
			System.out.println("Format: java Player host port [game]");
			System.exit(0);
		}
	}


	public void startThreads(final String host,final String port) {
		new Thread(){
			public void run() {
				try {
					while(true) {
						Thread.sleep(10);//REDRAWDELAY);
						repaint();//.repaint();
					}
				} catch (Exception e){
					System.out.println("MyError 1");
				}
			}
		}.start();
		new Thread(){
			public void run() {
				try {
					while(true) {
						Thread.sleep(MINCOMMANDDELAY);
						command();
//						System.out.println("move="+p.currKeys);
					}
				} catch (Exception e){
					System.out.println("MyError 5");
				}
			}
		}.start();
		//draw thread
		new Thread(){
			public void run() {
				try {
					while(true) {
						Thread.sleep(SENDDELAY);
						if (changed) {
							doSend();
							changed = false;
							Thread.yield();
//							System.out.println("auto send");
						}
					}
				} catch (Exception e){
					System.out.println("MyError 2");
				}
			}
		}.start();
		new Thread() {
			public void run() {
				try {
				Socket ss=new Socket(host, Integer.parseInt(port));
				out=ss.getOutputStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(ss.getInputStream()));
				OutputStream o=ss.getOutputStream();
				o.write(("__CREATE name:"+gname+" max:6 refill:1\n").getBytes());
				o.write(("__JOIN name:" + gname+"\n").getBytes());
				while (true) {
					Thread.yield();
					String r=br.readLine();
					if (r.indexOf("JOINED")>=0 && id==-1) 
						id = Integer.parseInt(r.substring(r.lastIndexOf(" ")).trim());
//					System.out.println("received");
					command(r);
				}
				} catch (Exception e) {
					System.out.println("MyError 3");
				}
			}
		}.start();
		join();
	}
}

