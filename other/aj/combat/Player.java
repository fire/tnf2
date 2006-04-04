package aj.combat;

import java.awt.BorderLayout;
import java.awt.Graphics;
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
import java.util.Vector;

import javax.swing.JFrame;

import aj.misc.Stuff;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Player implements KeyListener, MouseListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static String REPOPULATE="rep",CREATE="new ",DESTROY="dest ",UPDATE="mov ";
	static String gname = "combat1.0";

//	static int SHIPMAXSHOTCOUNT = 6;
	static int SHIPMAXTURN = 12;
	static int MAXSHIPSPEED = 7;
	static double SHIPMAXACCEL = .4;
	static int SHIPSIZE = 8;

	static int SHOTSIZE = 2;
	static int MAXSHOTRANGE=1000;
	static int MAXSHOTSPEED=4;
	static double FRICTION = .995;

//command delays
	static int REDRAWDELAY = 1000/30;
	static int AUTOSYNCSENDDELAY = 150;
//	static int MINREDRAWDELAY = 50;
	static int MINSHOTDELAY = 150;
	static int MINTURNDELAY = 10;
	static int MINMOVEDELAY = 30;
	static int NEXTCOMMANDCHECKDELAY=30;

	OutputStream out=null;
	int id=(int)(Math.random()*1000);
	long lastfire;

	private boolean mouseOn=true;
	private boolean autoCenterMode=true;

	Vector allItems=new Vector();
	Vector myItems=new Vector();
	Ship myShip;
	
	MapView mapView;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Player p=new Player();
		if (args.length == 3) {
			Player.gname = args[2];
		}
		final JFrame f = new JFrame();
		f.addKeyListener(p);
		f.addMouseListener(p);
		f.addMouseMotionListener(p);

		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add("Center", p.mapView);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p.startThreads(args[0],args[1]);
	}

	Player p;

	public Player() {
		myShip=Ship.rand(""+(int)(Math.random()*1000));
		addMyItem(myShip);
		p=this;
		mapView=new MapView();
		mapView.setDisplayItems(allItems);
		mapView.setCenterItem(myShip);
	}

	public void sendSelfStatus() {
		myShip.updatePos();
		if (myShip.alive)
			sendNetworkMessage(UPDATE + myShip);
	}

	public void sendNetworkMessage(String s) {
		if (!s.endsWith("\n")) s+="\n";
		if (out!=null) {
			try {
				out.write((s).getBytes());
			} catch (IOException e) {
				out=null;
			}			
		}
	}


	public void quit() {}

	public void join() {
		sendNetworkMessage(REPOPULATE);
		//repost all ids
		myShip = Ship.rand("" + id);
		sendNetworkMessage(CREATE + myShip);
	}

	long lastmove=0;
	public void moveUp() {
		if (!myShip.alive) {
			return;
		}
		if (lastmove + MINMOVEDELAY < System.currentTimeMillis()) {
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
			lastturn = System.currentTimeMillis();
		}
	}

	public void fire() {
		if (!myShip.alive) {
			return;
		}
		if (lastfire + MINSHOTDELAY < System.currentTimeMillis()) {
			Shot w = myShip.shoot();
			myItems.addElement(w);
			sendNetworkMessage(CREATE + w.toString());
			lastfire = System.currentTimeMillis();
		}
	}

	private void removeItem(CombatItem c) {
		allItems.removeElement(c);
		myItems.removeElement(c);
	}

	private void addItem(CombatItem c) {
		allItems.addElement(c);
	}

	private void addMyItem(CombatItem c) {
		myItems.add(c);
		allItems.addElement(c);
	}

	private void updateItem(CombatItem c) {
		Thing t=(Thing)c;
		for (int a=0;a<allItems.size();a++) {
			Thing tt=(Thing)allItems.elementAt(a);
			if (tt.id==t.id) {
				tt.copyVals(t);
			}
		}
			
	}
	
	public void checkCollision() {
		for (int a = 0; a < allItems.size(); a++) {
			Thing t=(Thing)allItems.elementAt(a);
			if (t instanceof Shot && ((Shot)t).expired()) {
				removeItem((CombatItem)t);
				a--;
				continue;
			}
			t.updatePos();
		}
	}	

	public void startThreads(final String host,final String port) {
		//repaint thread
		new Thread(){
			public void run() {
				try {
					long last=System.currentTimeMillis();
					while(true) {
						Thread.sleep(System.currentTimeMillis()-last+REDRAWDELAY);
						last=System.currentTimeMillis();
						checkCollision();
						mapView.repaint();//.repaint();
					}
				} catch (Exception e){
					System.out.println("MyError 1");
				}
			}
		}.start();
		//keyboard command listener and mouse listener thread
		new Thread(){
			public void run() {
				try {
					long last=System.currentTimeMillis();
					while(true) {
						Thread.sleep(System.currentTimeMillis()-last+NEXTCOMMANDCHECKDELAY);
						last=System.currentTimeMillis();
						p.receiveUserCommand();
//						System.out.println("move="+p.currKeys);
					}
				} catch (Exception e){
					System.out.println("MyError 5");
				}
			}
		}.start();
		//sync network send update thread
		new Thread(){
			public void run() {
				try {
					long last=System.currentTimeMillis();
					while(true) {
						Thread.sleep(System.currentTimeMillis()-last+AUTOSYNCSENDDELAY);
						last=System.currentTimeMillis();
						p.sendSelfStatus();
					}
				} catch (Exception e){
					System.out.println("MyError 2");
				}
			}
		}.start();
		
		//network commadn listener thread
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
						p.sendNetworkMessage(r);
					}
				} catch (Exception e) {
					System.out.println("MyError 3");
				}
			}
		}.start();
		join();
	}

	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public void receiveNetworkMessage(String s) {
			if (s.startsWith(REPOPULATE)) {
				for (int a=0; a < myItems.size(); a++) {
					CombatItem t2 = (CombatItem) myItems.elementAt(a);
					if (t2!=myShip || myShip.alive)
						sendNetworkMessage(UPDATE + t2.toString());
				}
			}
			else if (s.startsWith(DESTROY)) {
				String id = s.substring(s.indexOf(" ") + 1);
				s = s.substring(s.indexOf(" ") + 1);
				CombatItem c = parse(s);
				if (id.equals(myShip.id)) {
					myShip.setAlive(false);
				}
				else {
					removeItem(c);
					if (c instanceof Ship) {
						Explosion e=((Ship)c).explode();
						addMyItem(e);
					}
				}
			}
			else if (s.startsWith(CREATE)) {
				s = s.substring(s.indexOf(" ") + 1);
				CombatItem c = parse(s);
				addItem(c);
			}
			else if (s.startsWith(UPDATE)) {
				s = s.substring(s.indexOf(" ") + 1);
				CombatItem c = parse(s);
				if (c != null) {
					updateItem(c);
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
System.out.println("key typed="+ch);
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
			mapView.infoOn=!mapView.infoOn;
		}
		if (currKeys.indexOf("M")>=0) {
			mouseOn= !mouseOn;
		}

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

	public void mouseDragged(MouseEvent e) {
		Point p=e.getPoint();
		p=new Point(p.x-mapView.getWidth()/2,p.y-mapView.getHeight()/2);
		double dir=Math.atan2(1.0*p.y,1.0*p.x);
		targetDir=dir;
	}

	public void mouseMoved(MouseEvent e) {
		Point p=e.getPoint();
		p=new Point(p.x-mapView.getWidth()/2,p.y-mapView.getHeight()/2);
		double dir=Math.atan2(1.0*p.y,1.0*p.x);
		targetDir=dir;
	}


	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

	double targetDir=0;
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
