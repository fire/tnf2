package aj.combat;

import java.awt.BorderLayout;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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
	public static final int MISSILESIZE = 4;
	static String REPOPULATE="rep",CREATE="new ",DESTROY="dest ",UPDATE="mov ";
	static String gname = "combat1.0";

	static int SHIPMAXTURN = 12;
	static int MAXSHIPSPEED = 10;
	static double SHIPMAXACCEL = .3;
	static int SHIPSIZE = 15;

	static int SHOTSIZE = 4;
	static int MAXSHOTSPEED=15;
	static int ACTUALMAXSHOTSPEED=MAXSHOTSPEED-MAXSHIPSPEED;
	static double FRICTION = .998;

//command delays
	static int REDRAWDELAY = 30;
	static int AUTOSYNCSENDDELAY = 150;
	static int MINSHOTDELAY = 150;
	static int MINTURNDELAY = 10;
	static int MINMOVEDELAY = 30;
	static int NEXTCOMMANDCHECKDELAY=30;
	private static String playerDisplayName="Player";

	OutputStream out=null;
	int id=(int)(Math.random()*1000);
	long lastfire;

	private boolean mouseOn=true;

	Vector allItems=new Vector();
	Vector myItems=new Vector();
	Ship myShip;
	
	MapView mapView;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length >= 3) {
			Player.gname = args[2];
		}
		if (args.length >= 4) {
			Player.playerDisplayName=args[3];
		}
		Player p=new Player();
		final JFrame f = new JFrame();
		f.addKeyListener(p);
		f.addMouseListener(p);
		f.addMouseMotionListener(p);

		f.getContentPane().setLayout(new BorderLayout());
		JMenuBar jmb=new JMenuBar();
		
		JMenu settings=new JMenu("Settings");
		jmb.add(settings);
		JMenuItem nameMenu=new JMenuItem("Player");
		settings.add(nameMenu);
		JMenuItem serverSetup=new JMenuItem("Server");
		settings.add(serverSetup);
		JMenuItem gameSetup=new JMenuItem("Game");
		settings.add(gameSetup);
		
		f.setJMenuBar(jmb);
		f.getContentPane().add("Center", p.mapView);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p.startThreads(args[0],args[1]);
	}
	
	
	Player p;

	public Player() {
		myShip=Ship.rand(""+(int)(Math.random()*1000));
		myShip.playerName=playerDisplayName;
		addMyItem(myShip);
		p=this;
		mapView=new MapView();
		mapView.setDisplayItems(allItems);
		mapView.setCenterItem(myShip);
	}

	private void sendSelfStatus() {
		if (myShip.isAlive())
			sendNetworkMessage(UPDATE + myShip);
	}

	private void sendNetworkMessage(String s) {
		if (!s.endsWith("\n")) s+="\n";
		if (out!=null) {
			try {
				out.write((s).getBytes());
			} catch (IOException e) {
				out=null;
			}			
		}
	}


	private void quit() {}

	private void join() {
		sendNetworkMessage(REPOPULATE);
		//repost all ids
		sendNetworkMessage(CREATE + myShip);
	}

	long lastmove=0;
	private void moveUp() {
		if (!myShip.isAlive()) {
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
	private void moveDown() {
		if (!myShip.isAlive()) {
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
	private void moveLeft() {
		if (!myShip.isAlive()) {
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
	private void moveRight() {
		if (!myShip.isAlive()) {
			return;
		}
		if (lastturn + MINTURNDELAY < System.currentTimeMillis()) {
			myShip.dir += SHIPMAXTURN / 180.0 * Math.PI;
			lastturn = System.currentTimeMillis();
		}
	}

	static int MAXLASERSHOTS=8;
	int activeLaserCount=0;
	private void fireLaser() {
		if (!myShip.isAlive()) {
			return;
		}
		if (lastfire + MINSHOTDELAY < System.currentTimeMillis() && activeLaserCount<MAXLASERSHOTS) {
			Shot w = myShip.shootLaser();
			addMyItem(w);
			sendNetworkMessage(CREATE + w.toString());
			lastfire = System.currentTimeMillis();
			activeLaserCount++;
		}
	}

	static int MAXMISSILEHOTS=2;
	int activeMissileCount=0;
	private void fireMissile() {
		if (!myShip.isAlive()) {
			return;
		}
		if (lastfire + MINSHOTDELAY < System.currentTimeMillis() && activeMissileCount<MAXMISSILEHOTS) {
			Missile w = myShip.shootMissile();
			addMyItem(w);
			sendNetworkMessage(CREATE + w.toString());
			lastfire = System.currentTimeMillis();
			activeMissileCount++;
		}
	}

	
	private void removeItem(CombatItem c) {
		Thing t=(Thing)c;
		String id=t.getId();
		if (id.equals(myShip.getId())) {
			myShip.setAlive(false);
			return;
		}
		for (int a=0;a<allItems.size();a++) {
			Thing test=(Thing)allItems.elementAt(a);
			if (test.getId().equals(id)) {
				allItems.removeElement(test);
				if (myItems.contains(test) && test instanceof Shot) {
					activeLaserCount--;
				}
				if (myItems.contains(test) && test instanceof Missile) {
					activeMissileCount--;
				}
				myItems.removeElement(test);
			}
		}
	}

	private void addItem(CombatItem c) {
		if (!allItems.contains(c)) allItems.addElement(c);
	}

	private void addMyItem(CombatItem c) {
		if (!myItems.contains(c)) myItems.add(c);
		allItems.addElement(c);
	}

	private void updateItem(CombatItem c) {
		boolean found=false;
		Thing t=(Thing)c;
		for (int a=0;a<allItems.size();a++) {
			Thing tt=(Thing)allItems.elementAt(a);
			if (tt.id.equals(t.id)) {
				tt.copyVals(t);
				if (tt instanceof Ship) {
					((Ship)tt).copyShipVals((Ship)t);
				}
				found=true;
				break;
			}
		}
		if (!found) addItem(c);
	}
	
	private void checkCollision() {
		for (int a = 0; a < allItems.size(); a++) {
			Thing t=(Thing)allItems.elementAt(a);
			t.updatePos();

			if (t instanceof Explosion) {
				if (((Explosion)t).expired()) {
					removeItem((CombatItem)t);
					a--;
					}
				continue;
			}
			if (t instanceof Shot && ((Shot)t).expired()) {
				removeItem((CombatItem)t);
				a--;
				continue;
			}
			if (myItems.contains(t) && !(t instanceof Asteroid)) {
				if (t ==myShip && !myShip.isAlive()) {
					continue;
				}
				for (int b=0;b<allItems.size();b++) {
					Thing hit=(Thing)allItems.elementAt(b);
					if (myItems.contains(hit) && !(hit instanceof Asteroid)) continue;
					if (t.inside(hit)) {
						if (hit instanceof Ship) {
							Explosion e=((Ship)hit).explode();
							addItem(e);
							sendNetworkMessage(CREATE+e);
							removeItem((CombatItem)t);
							removeItem((CombatItem)hit);
							sendNetworkMessage(DESTROY+t);
							sendNetworkMessage(DESTROY+hit);
							a--;
							break;
						}
						else if (hit instanceof Asteroid) {
							removeItem((CombatItem)t);
							sendNetworkMessage(DESTROY+t);
							Asteroid A=((Asteroid)hit).breakup();
							if (A!=null) {
								addItem(A);
								sendNetworkMessage(CREATE+A);
							}
							else if (!((Asteroid)hit).isBreader()) {
								removeItem((CombatItem)hit);
								sendNetworkMessage(DESTROY+hit);
							}
						}
						else if (hit instanceof Missile) {
							removeItem((CombatItem)t);
							removeItem((CombatItem)hit);
							sendNetworkMessage(DESTROY+t);
							sendNetworkMessage(DESTROY+hit);
							a--;
							break;
						}
					}
				}
			}
			
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
						mapView.repaint();
					}
				} catch (Exception e){
					System.out.println("MyError repaint error");
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
						receiveUserCommand();
//						System.out.println("move="+p.currKeys);
					}
				} catch (Exception e){
					System.out.println("MyError user command error");
					e.printStackTrace();
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
					System.out.println("MyError network status send error");
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
					String r=br.readLine();
					if (r.indexOf("JOINED")>=0 && id==-1) 
						id = Integer.parseInt(r.substring(r.lastIndexOf(" ")).trim());
					p.join();
					while (true) {
						Thread.yield();
						r=br.readLine();
						if (r==null) break;
						receiveNetworkMessage(r);
					}
				} catch (Exception e) {
					System.out.println("MyError network message received error");
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	private void receiveNetworkMessage(String s) {
			if (s.startsWith(REPOPULATE)) {
				for (int a=0; a < myItems.size(); a++) {
					CombatItem t2 = (CombatItem) myItems.elementAt(a);
					if (t2!=myShip || myShip.isAlive())
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
				}
			}
			else if (s.startsWith(CREATE)|| s.startsWith(UPDATE)) {
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
	}
	public void keyReleased(KeyEvent evt) {
		char ch = Character.toUpperCase(evt.getKeyChar());
		if (currKeys.indexOf(ch)>=0) currKeys=currKeys.substring(0,currKeys.indexOf(ch))+currKeys.substring(currKeys.indexOf(ch)+1);
	}

	public void keyTyped(KeyEvent evt) {
	}
	
	String temp="";

	private void receiveUserCommand() {
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

		if (currKeys.indexOf("X")>=0 || currKeys.indexOf("Q")>=0) {//ch == 'X' || ch == 'Q') {
			System.exit(1);
		}
		if (currKeys.indexOf("R")>=0 && !myShip.isAlive()) {
			myShip.setAlive(true);
			sendNetworkMessage("new " + myShip.toString());
		}
		if (currKeys.indexOf("2")>=0 || currKeys.indexOf("K")>=0 || currKeys.indexOf("W")>=0) {
			moveUp();
		}
		if (currKeys.indexOf("8")>=0 || currKeys.indexOf("J")>=0 || currKeys.indexOf("S")>=0 ) {
			moveDown();
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
		if (currKeys.indexOf("X")>=0 || currKeys.indexOf("Q")>=0) {
			quit();
		}
		if (currKeys.indexOf("5")>=0 || currKeys.indexOf(" ")>=0 ) {
			fireLaser();
		}
		if (currKeys.indexOf("M")>=0) {
			fireMissile();
		}
		if (currKeys.indexOf("N")>=0) {
			myShip.setRandomShipShape();
			myShip.colorIndex=(int)(Math.random()*4);
		}
		if (currKeys.indexOf("T")>=0) {
			addAsteroid();
		}

	}

	private void addAsteroid() {
		Asteroid a=Asteroid.createRandom();
		addMyItem(a);
		this.sendNetworkMessage(CREATE+a.toString());
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
	private CombatItem parse(String s) {
		try {
			String t[] = Stuff.getTokens(s);
			if (t[0].equals(Ship.shipType)) {
				return Ship.parse(t);
			}
			else if (t[0].equals(Shot.shotType)){
				return Shot.parse(t);
			}
			else if (t[0].equals(Asteroid.asteroidType)){
				return Asteroid.parse(t);
			}
			else if (t[0].equals(Explosion.explosionType)){
				return Explosion.parse(t);
			}
			else if (t[0].equals(Missile.missileType)){
				return Missile.parse(t);
			}
			else {
System.out.println("bad value "+s);
				return null;
			}
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
