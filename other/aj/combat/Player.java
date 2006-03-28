package aj.combat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
public class Player extends JPanel implements KeyListener,MouseListener,MouseMotionListener {
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
	int id=(int)(Math.random()*1000);
	boolean changed = false;
	long lastfire;
	Vector items = new Vector();
	Hashtable itemsHashtable = new Hashtable();
	Ship myShip;
	Vector myShots = new Vector();
	static String gname = "combat";

	private boolean mouseOn=true;
	private boolean autoCenterMode=true;


	public Player() {
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void sendSelfStatus() {
		myShip.updatePos();
		if (myShip.alive)
			sendNetworkMessage("mov " + myShip);
		for (int a=0;a<items.size();a++) {
			String t=(String)items.elementAt(a);
			CombatItem c=(CombatItem)itemsHashtable.get(t);
			if (c instanceof Asteroid) {
				Asteroid A=(Asteroid)c;
				A.updatePos();
				if (Math.random()<.25) sendNetworkMessage("mov "+ A.toString());
			}
		}
	}
	
	public void sendNetworkMessage(String s) {
		if (out!=null)
			try {
				if (!s.endsWith("\n")) s+="\n";
				out.write((s).getBytes());
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
		sendNetworkMessage("rep");
		//repost all ids
		myShip = Ship.rand("" + id);
		sendNetworkMessage("new " + myShip);
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
			sendNetworkMessage("new " + w.toString());
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
			else if (t instanceof Explosion && ((Explosion)t).expired()) {
				itemsHashtable.remove(t.id);
				items.removeElement(t.id);
				a--;				
			}
			else if (myShip.inside(t) && myShip.alive) {
				myShip.setAlive(false);
				itemsHashtable.remove(t.id);
				items.removeElement(t.id);
				a--;
				sendNetworkMessage("dest " + myShip.id);
				sendNetworkMessage("dest " + t.id);
			}
			else {
				int a2;
				for (a2 = 0; a2 < myShots.size(); a2++) {
					Shot t2 = (Shot) myShots.elementAt(a2);
					if (t2.inside(t)) {
						sendNetworkMessage("dest " + t2.id);
						sendNetworkMessage("dest " + t.id);
						itemsHashtable.remove(t.id);
						items.removeElement(t.id);
						myShots.removeElement(t2);
						if (t instanceof Ship) {
							Explosion e=((Ship)t).explode();
							itemsHashtable.put(e.id,e);
							items.addElement(e.id);
						}
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
		g.setColor(Color.black);
		g.fillRect(0, 0, ZONESIZE, ZONESIZE);
		drawStars(g);
		g.setColor(Color.white);
		if (infoOn) displayInfo(g);
//		g.drawRect(0, 0, ZONESIZE - 1, ZONESIZE - 1);
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
		if (autoCenterMode) {
			double ratew=(1.0*getWidth())/ZONESIZE;
			double rateh=(1.0*getHeight())/ZONESIZE;
			G.translate((int)(getWidth()/2.0),(int)(getHeight()/2.0));
			G.translate(-(int)(myShip.x*ratew),-(int)(myShip.y*rateh));
		}
		for (int a=-1;a<2;a++) {
			for (int b=-1;b<2;b++) {
				G.translate(a*getWidth(),b*getHeight());
				G.drawImage(i, 0, 0,this.getWidth(),this.getHeight(), null);
				G.translate(-a*getWidth(),-b*getHeight());
			}
		}
		REDRAWDELAY = (int) (System.currentTimeMillis() - elap + MINREDRAWDELAY);
	}


	private void displayInfo(Graphics g) {
		String info1="move      (asdw) (hjkl) (2468)";
		String info2="fire      <space> (5)";
		String info3="center    'c'";
		String info5="mouseTurn 'm'";
		String info4="help      '?' '/'";

		g.drawString(info1,0,15);
		g.drawString(info2,0,30);
		g.drawString(info3,0,45);
		g.drawString(info5,0,60);
		g.drawString(info4,0,75);

	}

	int randomSeed=7532;//(int)(Math.random()*1000);
	private void drawStars(Graphics g) {
		Random r=new Random(randomSeed);
		for (int a=0;a<50;a++) {
			int x=Math.abs(r.nextInt()%ZONESIZE);
			int y=Math.abs(r.nextInt()%ZONESIZE);
			Color c[]={Color.red,Color.yellow,Color.lightGray,Color.blue,Color.green,Color.magenta,Color.cyan,Color.orange,Color.pink};
//			Color c[]={Color.red};
			int ci=(int)(r.nextDouble()*c.length);
			int size=(int)(r.nextDouble()*3+1);
			g.setColor(c[ci]);
			g.fillRoundRect(x,y,size,size,2,2);
//			g.fillRoundRect()
//			g.drawString(""+a,x,y);
			
		}
	}

	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public void receiveNetworkMessage(String s) {
			if (s.startsWith("rep")) {
				if (myShip.alive) {
					sendNetworkMessage("mov " + myShip);
				}
				for (int a=0; a < myShots.size(); a++) {
					Shot t2 = (Shot) myShots.elementAt(a);
					sendNetworkMessage("mov " + t2.toString());
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
						Thing t=(Thing)itemsHashtable.get(id2);
						if (t instanceof Ship) {
							Explosion e=((Ship)t).explode();
							items.addElement(e.id);							
							itemsHashtable.put(e.id,e);
						}
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

	private boolean infoOn;
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
	
	String temp="";

	public void receiveUserCommand() {
		if (myShip==null) return;
//		System.out.println("currKey="+currKeys);
		double MINCHANGE=.3;
		double ddir=targetDir-myShip.dir;
		while (ddir>Math.PI) ddir-=Math.PI*2;
		while (ddir<-Math.PI) ddir+=Math.PI*2;
		if (Math.abs(ddir)>MINCHANGE && mouseOn) {
//			double myd=myShip.dir;
//			while (myd<-Math.PI*2) myd+=Math.PI*2;
//			while (myd>Math.PI*2) myd-=Math.PI*2;
//			System.out.println("dir="+targetDir+" myshipdir="+myShip.dir+" ddir="+ddir);
			if (ddir>MINCHANGE) {
				if (!temp.equals("counter ="+ddir)) {
					temp="counter ="+ddir;
//					System.out.println(temp);
				}
				char ch='D';
				if (currKeys.indexOf(ch)<0) currKeys+=ch;
			}
			else if (ddir<-MINCHANGE) {
				if (!temp.equals("clock ="+ddir)) {
					temp="clock ="+ddir;
//					System.out.println(temp);
				}
				char ch='A';
				if (currKeys.indexOf(ch)<0) currKeys+=ch;
			}
		}
		else if (mouseOn) {
				if (!temp.equals("cancel ="+ddir)) {
					temp="cancel ="+ddir;
//					System.out.println(temp);
				}
				char ch='A';
				if (currKeys.indexOf(ch)>=0) currKeys=currKeys.substring(0,currKeys.indexOf(ch))+currKeys.substring(currKeys.indexOf(ch)+1);
				ch='D';
				if (currKeys.indexOf(ch)>=0) currKeys=currKeys.substring(0,currKeys.indexOf(ch))+currKeys.substring(currKeys.indexOf(ch)+1);				
			}

		if (currKeys.indexOf("T")>=0) addAsteroid();
		if (currKeys.indexOf("C")>=0) autoCenterMode=!autoCenterMode;
		if (currKeys.indexOf("X")>=0 || currKeys.indexOf("Q")>=0) {//ch == 'X' || ch == 'Q') {
			System.exit(1);
		}
		if (currKeys.indexOf("R")>=0 && !myShip.alive) {
			myShip = Ship.rand("" + id);
			sendNetworkMessage("new " + myShip.toString());
			myShip.setAlive(true);
		}
		if (currKeys.indexOf("2")>=0 || currKeys.indexOf("K")>=0 || currKeys.indexOf("W")>=0) {
			moveUp();
		}
		if (currKeys.indexOf("8")>=0 || currKeys.indexOf("J")>=0 || currKeys.indexOf("S")>=0 ) {
			moveDown();
		}
		if (currKeys.indexOf("5")>=0 || currKeys.indexOf(" ")>=0 ) {
			fire();
		}
		if (currKeys.indexOf("4")>=0 || currKeys.indexOf("H")>=0 || currKeys.indexOf("A")>=0 ) {
			moveLeft();
		}
		if (currKeys.indexOf("6")>=0 || currKeys.indexOf("L")>=0 || currKeys.indexOf("D")>=0 ) {
			moveRight();
		}
		if (currKeys.indexOf("?")>=0 || currKeys.indexOf("/")>=0) {
			infoOn=!infoOn;
		}
		if (currKeys.indexOf("M")>=0) {
			mouseOn= !mouseOn;
		}

	}

	int acount=1;
	public void addAsteroid() {
//			double ndir = ((int) (Math.random() * 360 / Player.SHIPMAXSHOTCOUNT));
//			Asteroid w=new Asteroid(id+"A"+(acount++),Math.random() * Player.ZONESIZE, 
//				Math.random() * Player.ZONESIZE, 
//				ndir, 0, 0,Asteroid.asteroidType,"0","0");
//		sendNetworkMessage("new " + w.toString());
//		items.addElement(w.id);
//		itemsHashtable.put(w.id,w);
//		System.out.println("add asteroid "+w);
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
			if (t[6].equals(Ship.shipType)) {
				return Ship.parse(t);
			}
			else if (t[6].equals(Shot.shotType)){
				return Shot.parse(t);
			}
			else if (t[6].equals(Asteroid.asteroidType)){
				return Asteroid.parse(t);
			}
			else return null;
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
		//repaint thread
		new Thread(){
			public void run() {
				try {
					while(true) {
						Thread.sleep(10);//REDRAWDELAY);
						repaint();//.repaint();
					}
				} catch (Exception e){
				}
			}
		}.start();

		//command input thread
		new Thread(){
			public void run() {
				try {
					while(true) {
						Thread.sleep(MINCOMMANDDELAY);
						receiveUserCommand();
//						System.out.println("move="+p.currKeys);
					}
				} catch (Exception e){
					System.out.println("Error in user input");
				}
			}
		}.start();
		
		//draw thread
		new Thread(){
			public void run() {
				try {
					while(true) {
						Thread.sleep(SENDDELAY);
//						if (changed) {
							sendSelfStatus();
							changed = false;
							Thread.yield();
//							System.out.println("auto send");
//						}
					}
				} catch (Exception e){
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
					receiveNetworkMessage(r);
				}
				} catch (Exception e) {
				}
			}
		}.start();
		join();
	}

	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
		char ch=' ';
		if (e.getButton()==MouseEvent.BUTTON3) ch ='W';
		else if (e.getButton()==MouseEvent.BUTTON1) ch=' ';
		else return;
		
		if (currKeys.indexOf(ch)<0) currKeys+=ch;
	}
	public void mouseReleased(MouseEvent e) {
		char ch=' ';
		if (e.getButton()==MouseEvent.BUTTON3) ch ='W';
		else if (e.getButton()==MouseEvent.BUTTON1) ch=' ';
		else return;

		if (currKeys.indexOf(ch)>=0) currKeys=currKeys.substring(0,currKeys.indexOf(ch))+currKeys.substring(currKeys.indexOf(ch)+1);
	}
			
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

	double targetDir=0;
	public void mouseDragged(MouseEvent e) {
		Point p=e.getPoint();
		double ratew=(1.0*getWidth())/ZONESIZE;
		double rateh=(1.0*getHeight())/ZONESIZE;
		
		if (autoCenterMode) {
//			G.translate((int)(getWidth()/2.0),(int)(getHeight()/2.0));
//			G.translate(-(int)(myShip.x*ratew),-(int)(myShip.y*rateh));
			p=new Point(p.x-getWidth()/2,p.y-getHeight()/2);
		}
		else  {
			p=new Point((int)((p.x-myShip.x)*ratew),(int)((p.y-myShip.y)*rateh));
		}
//		System.out.println("act point="+p.x+" "+p.y);
		double dir=Math.atan2(1.0*p.y,1.0*p.x);
		targetDir=dir;
	}

	public void mouseMoved(MouseEvent e) {
		Point p=e.getPoint();
		double ratew=(1.0*getWidth())/ZONESIZE;
		double rateh=(1.0*getHeight())/ZONESIZE;
		
		if (autoCenterMode) {
//			G.translate((int)(getWidth()/2.0),(int)(getHeight()/2.0));
//			G.translate(-(int)(myShip.x*ratew),-(int)(myShip.y*rateh));
			p=new Point(p.x-getWidth()/2,p.y-getHeight()/2);
		}
		else  {
			p=new Point((int)((p.x-myShip.x)*ratew),(int)((p.y-myShip.y)*rateh));
		}
//		System.out.println("act point="+p.x+" "+p.y);
		double dir=Math.atan2(1.0*p.y,1.0*p.x);
		targetDir=dir;
	}
}

