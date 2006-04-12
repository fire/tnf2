package aj.combat;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	private static String REPOPULATE="rep",CREATE="new ",DESTROY="dest ",UPDATE="mov ";
	private static String gname = "combat1.0";

	private static int SHIPMAXTURN = 12;
	static int MAXSHIPSPEED = 10;
	private static double SHIPMAXACCEL = .3;
	static int SHIPSIZE = 15;

	static int SHOTSIZE = 4;
	private static int MAXSHOTSPEED=15;
	static int ACTUALMAXSHOTSPEED=MAXSHOTSPEED-MAXSHIPSPEED;
	static double FRICTION = .998;

//command delays
	private static int REDRAWDELAY = 30;
	private static int AUTOSYNCSENDDELAY = 150;
	private static int MINSHOTDELAY = 150;
	private static int MINTURNDELAY = 10;
	private static int MINMOVEDELAY = 30;
	private static int NEXTCOMMANDCHECKDELAY=30;
	private static String playerDisplayName="Player";

	private OutputStream out=null;
	private BufferedReader br=null;
	private Socket socket=null;
	
	private int id=(int)(Math.random()*1000);
	private long lastfire;

	private Vector allItems=new Vector();
	private Vector myItems=new Vector();
	private Ship myShip;
	private long lastmove=0;

	MapView mapView;
	private long lastturn=0;
	private Player p;
	private static int MAXLASERSHOTS=8;
	private int activeLaserCount=0;

	private static int MAXMISSILEHOTS=2;
	private int activeMissileCount=0;

	private String currKeys="";

	private double targetDir=0;
	
	static String serverHostIP="127.0.0.1";
	static String serverPortVal="8080";
	
	static Scores scores;
	
	//TODO scoring - kill /vs kill  (tom kill mike  vs mike)
	//TODO game settings () limited asteroids
	//TODO map obsticals/walls
	//TODO cloak
	//TODO shields
	//TODO guided missile
	
	//TODO power-ups
	//TODO hyperspace
	//TODO use gun points in ship
	//TODO lights on ship
	//TODO thurster from ship (show dust)
	//TODO asteroids bounce

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
		final Player p=new Player();
		final JFrame f = new JFrame("Übêrstioid");
		f.addKeyListener(p);
		f.addMouseListener(p);
		f.addMouseMotionListener(p);

		f.getContentPane().setLayout(new BorderLayout());
		JMenuBar jmb=new JMenuBar();
		
		JMenu settings=new JMenu("Einstellungen");
		jmb.add(settings);
		JMenuItem playerSetup=new JMenuItem("Spieler");
		settings.add(playerSetup);
		playerSetup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog jd=new JDialog();
				JPanel jp=new JPanel(new GridLayout(0,1));
				jd.getContentPane().add("Center",jp);
				JPanel row=new JPanel(new FlowLayout());
				row.add(new JLabel("Wie heisst du"));
				final JTextField playerNameTextField=new JTextField(15);
				playerNameTextField.setText(p.myShip.getPlayerName());
				row.add(playerNameTextField);
				jp.add(row);
				row=new JPanel(new FlowLayout());
				final JComboBox jc=new JComboBox(Ship.shipColorNames);
				jc.setSelectedIndex(p.myShip.getColorIndex());
				row.add(new JLabel("Farbe"));
				row.add(jc);
				jp.add(row);
				row=new JPanel(new FlowLayout());
				JButton okay=new JButton("treffen Sie zu");
				row.add(okay);
				okay.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.myShip.setPlayerName(playerNameTextField.getText());
						p.myShip.setColorIndex(jc.getSelectedIndex());
						if (!scores.getPlayerName().equalsIgnoreCase(playerNameTextField.getText())) {
							scores=new Scores(playerNameTextField.getText());
						}
						jd.setVisible(false);
					}					
				});
				jp.add(row);
				jd.pack();
				jd.setModal(true);
				jd.setVisible(true);
			}
		});
		JMenuItem serverSetup=new JMenuItem("Bediener");
		settings.add(serverSetup);
		//server host,port
		serverSetup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog jd=new JDialog();
				JPanel jp=new JPanel(new GridLayout(0,1));
				jd.getContentPane().add("Center",jp);
				JPanel row=new JPanel(new FlowLayout());
				row.add(new JLabel("Bediener Host"));
				final JTextField serverHost=new JTextField(15);
				serverHost.setText(serverHostIP);
				row.add(serverHost);
				jp.add(row);
				row=new JPanel(new FlowLayout());
				row.add(new JLabel("Bediener Port"));
				final JTextField serverPort=new JTextField(15);
				serverPort.setText(serverPortVal);
				row.add(serverPort);
				jp.add(row);
				row=new JPanel(new FlowLayout());
				JButton okay=new JButton("Okay");
				row.add(okay);
				okay.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Player.serverHostIP=serverHost.getText();
						Player.serverPortVal=serverPort.getText();
						p.startNetworkConnection();
						jd.setVisible(false);
					}					
				});
				jp.add(row);
				jd.pack();
				jd.setModal(true);
				jd.setVisible(true);
			}
		});
//		JMenuItem gameSetup=new JMenuItem("Game");
//		settings.add(gameSetup);
		//game name, game map, players
		
		f.setJMenuBar(jmb);
		f.getContentPane().add("Center", p.mapView);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serverHostIP=args[0];
		serverPortVal=args[1];
		p.startThreads();
	}
	

	public Player() {
		myShip=Ship.rand(""+(int)(Math.random()*1000));
		myShip.setPlayerName(playerDisplayName);
		addMyItem(myShip);
		p=this;
		mapView=new MapView();
		mapView.setDisplayItems(allItems);
		mapView.setCenterItem(myShip);
		scores=new Scores(playerDisplayName);
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
			scores.addDeath("unknown");
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
//			don't do asteroid check here, check only if it hits My stuff below
			if (t instanceof Asteroid) {
				continue;
			}
			if (myItems.contains(t)) {
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
							sendNetworkMessage(DESTROY+t+myShip.getPlayerName());
							sendNetworkMessage(DESTROY+hit+myShip.getPlayerName());
							a--;
							break;
						}
						else if (hit instanceof Asteroid) {
							removeItem((CombatItem)t);
							sendNetworkMessage(DESTROY+t+myShip.getPlayerName());
							Asteroid A=((Asteroid)hit).breakup();
							if (A!=null) {
								addItem(A);
								sendNetworkMessage(CREATE+A);
							}
							else if (!((Asteroid)hit).isBreader()) {
								removeItem((CombatItem)hit);
								sendNetworkMessage(DESTROY+hit+myShip.getPlayerName());
							}
						}
						else if (hit instanceof Missile) {
							removeItem((CombatItem)t);
							removeItem((CombatItem)hit);
							sendNetworkMessage(DESTROY+t+myShip.getPlayerName());
							sendNetworkMessage(DESTROY+hit+myShip.getPlayerName());
							a--;
							break;
						}
					}
				}
			}
			
		}
	}	

	public void startThreads() {
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
		startNetworkConnection();
	}
	
	private void collision(Thing one,Thing two) {
		//special cases
		//if explosion - nothing
		//if laser - dest laser
		//if missil - dest missil + add small exp
		//if asteroid && !asteroid -- breakup asteroid (2x vs missile)
		//if ship && !my laser --
		//    check shield - armor - score then dest ship + exp
		//asteroid on asteroid == bounce
	}

	public void startNetworkConnection() {
		if (socket!=null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket=null;
			out=null;
			br=null;
		}
		//network commadn listener thread
		Socket s=null;
		try {
			s = new Socket(serverHostIP, Integer.parseInt(serverPortVal));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		final Socket socket=s;
		System.out.println("connect to "+serverHostIP+" "+serverPortVal);
		try {
			out=socket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		OutputStream o=socket.getOutputStream();
		new Thread() {
			public void run() {
				try {
					out.write(("__CREATE name:"+gname+" max:6 refill:1\n").getBytes());
					out.write(("__JOIN name:" + gname+"\n").getBytes());
					String r=br.readLine();
					if (r.indexOf("JOINED")>=0 && id==-1) 
						id = Integer.parseInt(r.substring(r.lastIndexOf(" ")).trim());
					p.join();
					while (socket!=null) {
						Thread.yield();
						r=br.readLine();
						if (r==null) break;
						receiveNetworkMessage(r);
					}
					System.out.println("close connection");
				} catch (Exception e) {
					System.out.println("MyError network message received error");
					e.printStackTrace();
				}
			}
		}.start();
		//sync network send update thread
		new Thread(){
			public void run() {
				try {
					long last=System.currentTimeMillis();
					while(socket!=null) {
						Thread.sleep(System.currentTimeMillis()-last+AUTOSYNCSENDDELAY);
						last=System.currentTimeMillis();
						p.sendSelfStatus();
					}
					System.out.println("close connection2");
				} catch (Exception e){
					System.out.println("MyError network status send error");
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
					scores.addDeath(s.substring(s.lastIndexOf(" ")+1));
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
	
	private void receiveUserCommand() {
		if (myShip==null) return;
//		System.out.println("currKey="+currKeys);
		myShip.dir=targetDir;

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
		p=new Point(p.x,p.y-50);
		double dir=Math.atan2(1.0*p.y-mapView.getHeight()/2.0,1.0*p.x-mapView.getWidth()/2.0);
		targetDir=dir;
	}

	public void mouseMoved(MouseEvent e) {
		Point p=e.getPoint();
		p=new Point(p.x,p.y-50);
		double dir=Math.atan2(1.0*p.y-mapView.getHeight()/2.0,1.0*p.x-mapView.getWidth()/2.0);
		targetDir=dir;
	}


	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


}
