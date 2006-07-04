package aj.games;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import aj.awt.SimpleWindowManager;

public class RealmC implements KeyListener, ActionListener {

	static int DEFAULTSERVERPORT = 55320;

	// for client mode
	static int DEFAULTCLIENTPORT = 55321;

	// for server mode
	public static void displayHelp() {
		System.out.println("FORMAT: java aj.games.RealmR [options]");
		System.out
				.println("-s <serverport>  wait for server connect to local port (default mode)");
		System.out
				.println("-h <host[:port]>   client connect to host ip at port");
		System.out
				.println("-g             use text (no GUI) mode (requires hit or winhit");
		System.out.println("-n <name>        player name");
		System.out.println("-a               syssop mode");
		System.exit(0);
	}

	public static void main(String s[]) {
		if (s.length == 0) {
			displayHelp();
		}
		Socket nl = null;
		String playername = null;
		for (int a = 0; a < s.length; a++) {
			// System.out.println("NOTE checking option "+s[a]);
			if (s[a].startsWith("-"))
				s[a] = s[a].substring(1).trim();
			if (s[a].startsWith("?")) {
				displayHelp();
			} else if (s[a].toUpperCase().startsWith("A")) {
				RealmC.SYSOPMODE = true;
			} else if (s[a].toUpperCase().startsWith("G")) {
				RealmC.GUI = false;
			} else if (s[a].toUpperCase().startsWith("S")) {
				s[a] = s[a].substring(1);
				if (s[a].length() == 0 && s.length > a + 1) {
					a++;
				}
				try {
					int port = Integer.parseInt(s[a]);
					System.out
							.println("Passive Server mode.  Waiting for server call on port "
									+ port + ".");
					ServerSocket ss = new ServerSocket(port);
					Socket sss = ss.accept();
					nl = sss;
				} catch (IOException ioe) {
					System.out.println("MyError: cannot make server at port");
				} catch (NumberFormatException nfe) {
					System.out.println("MyError: bad port in server request");
				}
			} else if (s[a].toUpperCase().startsWith("N")) {
				s[a] = s[a].substring(1);
				if (s[a].length() == 0 && s.length > a + 1) {
					a++;
				}
				playername = s[a];
				// System.out.println("User name found "+playername);
			} else if (s[a].toUpperCase().startsWith("H")) {
				s[a] = s[a].substring(1);
				if (s[a].length() == 0 && s.length > a + 1) {
					a++;
				}
				try {
					System.out.println("Active Client mode.  Calling server."
							+ s[a]);
					String host = s[a];
					int port = DEFAULTSERVERPORT;
					if (host.indexOf(":") > 0) {
						port = Integer.parseInt(host.substring(host
								.indexOf(":") + 1));
						host = host.substring(0, host.indexOf(":"));
					}
					nl = new Socket(host, port);
				} catch (NumberFormatException nfe) {
					System.out.println("MyError: bad server port number.");
					System.exit(0);
				} catch (IOException ioe) {
					System.out.println("MyError: cannot connect to server.");
					System.exit(0);
				}
			}
		}
		if (nl == null) {
			try {
				System.out
						.println("Passive Server mode.  Waiting for server call on port "
								+ DEFAULTCLIENTPORT + ".");
				ServerSocket ss = new ServerSocket(DEFAULTCLIENTPORT);
				Socket sss = ss.accept();
				nl = sss;
			} catch (IOException IOE) {
				System.out
						.println("MyError: bad network connection.   Please use ? for help. 2");
			}
		}
		if (nl == null) {
			System.out
					.println("MyError: bad network connection.   Please use ? for help.");
			System.exit(0);
		}
		if (playername == null)
			new RealmC(nl, "User" + ((int) (Math.random() * 100)));
		else
			new RealmC(nl, playername);
	}

	static boolean GUI = true;

	static boolean SYSOPMODE = false;

	static int VIEW = 3;

	static int SENSERAN = 5;

	static int VIEWXPOS = 1;

	static int VIEWYPOS = 1;

	static int FONTSIZE = 18;

	static int SCREENY = 0, SCREENX = 0;

	Frame f;

	TextArea dis;

	TextField sysop;

	String defaultMap = RealmS.MAPINTRO + "5 5 XXXXXX   XX   XX   XXXXXX";

	char map[][];

	char screen[] = null;

	Vector users = new Vector();

	long lastcommandtime = 0;

	int MINTIME = 100;

	Socket nl;

	String name;

	int x, y;

	char dir;

	int status;

	public RealmC(final Socket nl, String name, Applet a) {
		readMap(defaultMap);
		SCREENY = Math.max(VIEW * 2 + 2 + VIEWYPOS + 1, SCREENY);
		SCREENX = Math.max(VIEW * 2 + 2 + VIEWXPOS + 1, SCREENX);
		this.name = name;
		this.nl = nl;
		dis = new TextArea(SCREENY + 1, SCREENX + 1);
		// left 5 are info area
		dis.addKeyListener(this);
		dis.setFont(new Font("Courier", Font.PLAIN, FONTSIZE));
		dis.setText("");
		// dis.setEditable(false);
		sysop = new TextField(20);
		sysop.addActionListener(this);
		sysop.setText("");
		sysop.setFont(new Font("Courier", Font.PLAIN, FONTSIZE));
		a.setLayout(new BorderLayout());
		a.add("Center", dis);
		if (SYSOPMODE)
			a.add("South", sysop);

		send(nl, "LOGIN " + name);
		new Thread() {
			public void run() {
				BufferedReader br;
				try {
					br = new BufferedReader(new InputStreamReader(nl
							.getInputStream()));
					while (true) {
						String s = br.readLine();
						if (s == null)
							break;
						actionPerformed(nl, s);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	public RealmC(final Socket nl, String name) {
		readMap(defaultMap);
		SCREENY = Math.max(VIEW * 2 + 2 + VIEWYPOS + 1, SCREENY);
		SCREENX = Math.max(VIEW * 2 + 2 + VIEWXPOS + 1, SCREENX);
		this.name = name;
		this.nl = nl;
		if (GUI) {
			f = new Frame();
			dis = new TextArea(SCREENY + 1, SCREENX + 1);
			// left 5 are info area
			dis.addKeyListener(this);
			dis.setFont(new Font("Courier", Font.PLAIN, FONTSIZE));
			dis.setText("");
			// dis.setEditable(false);
			sysop = new TextField(20);
			sysop.addActionListener(this);
			sysop.setText("");
			sysop.setFont(new Font("Courier", Font.PLAIN, FONTSIZE));
			f.setLayout(new BorderLayout());
			f.add("Center", dis);
			if (SYSOPMODE)
				f.add("South", sysop);
			f.addWindowListener(new SimpleWindowManager());
			f.pack();
			f.setVisible(true);
		}
		if (SYSOPMODE || !GUI) {
			InputListener il = new InputListener(new BufferedReader(
					new InputStreamReader(System.in)));
			il.addActionListener(this);
			new Thread(il).start();
		}
		// login
		// System.out.println("NOTE sending login");
		send(nl, "LOGIN " + name);

		new Thread() {
			public void run() {
				BufferedReader br;
				try {
					br = new BufferedReader(new InputStreamReader(nl
							.getInputStream()));
					while (true) {
						String s = br.readLine();
						if (s == null)
							break;
						actionPerformed(nl, s);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	String lastDisplay = "";

	public void updateDisplay() {
		// System.out.println("NOTE y="+y+" x="+x);
		if (screen == null) {
			screen = new char[SCREENY * SCREENX];
			for (int a = 0; a < SCREENY * SCREENX; a++)
				screen[a] = ' ';
		}
		for (int a = 0; a < SCREENY; a++)
			screen[a * SCREENX + SCREENX - 1] = '\n';
		for (int a = -1 * VIEW; a < VIEW + 1; a++) {
			for (int b = -1 * VIEW; b < VIEW + 1; b++) {
				if (y + a >= 0 && y + a < map.length
						&& x + b < map[y + a].length && x + b >= 0) {
					screen[(VIEW + a + VIEWYPOS) * SCREENX + VIEW + b
							+ VIEWXPOS] = map[y + a][x + b];
				} else {
					screen[(VIEW + a + VIEWYPOS) * SCREENX + VIEW + b
							+ VIEWXPOS] = ' ';
				}
			}
		}
		// System.out.println("NOTE TOTAL USERS="+users.size());
		String youSee = "", youSense = "";
		for (int a = 0; a < users.size(); a++) {
			RealmU ru = (RealmU) users.elementAt(a);
			if (ru.name.equals(name) || ru.status == 0)
				continue;
			// don't see/show/sense self or dead
			int showx = ru.x - x;
			int showy = ru.y - y;
			// System.out.println("NOTE OTHER AT "+showx+" "+showy);
			if (Math.abs(showx) <= VIEW && Math.abs(showy) <= VIEW) {
				// System.out.println("NOTE SEE USER");
				screen[(VIEW + showy + VIEWYPOS) * SCREENX + VIEW + showx
						+ VIEWXPOS] = ru.dir;
				if (youSee.length() == 0)
					youSee += "You see ";
				else
					youSee += ", ";
				youSee += ru.name;
			} else if (Math.abs(showx) <= SENSERAN
					&& Math.abs(showy) <= SENSERAN) {
				if (youSense.length() == 0)
					youSense += "You sense someone near you";
			}
		}
		if (status != 0)
			screen[(VIEW + 0 + VIEWYPOS) * SCREENX + VIEW + 0 + VIEWXPOS] = dir;
		// +1 position on screen
		int xoff = VIEWXPOS - 1;
		int yoff = VIEWYPOS - 1;
		screen[xoff + yoff * SCREENX] = '+';
		screen[VIEW * 2 + 2 + xoff] = '+';
		screen[(VIEW * 2 + 2 + yoff) * SCREENX] = '+';
		screen[(VIEW * 2 + 2 + yoff) * SCREENX + VIEW * 2 + 2 + xoff] = '+';
		for (int a = 1; a < (VIEW * 2 + 2); a++) {
			screen[a * SCREENX] = screen[a * SCREENX + (VIEW * 2 + 2)] = '|';
			screen[a] = screen[(VIEW * 2 + 2) * SCREENX + a] = '-';
		}
		String scr = new String(screen);
		if (youSee.length() > 0 && status != 0) {
			scr = scr.substring(0, scr.lastIndexOf("\n")) + youSee
					+ scr.substring(scr.lastIndexOf("\n"));
		} else if (youSense.length() > 0 && status != 0) {
			scr = scr.substring(0, scr.lastIndexOf("\n")) + youSense
					+ scr.substring(scr.lastIndexOf("\n"));
		}
		if (GUI) {
			if (!dis.getText().equals(scr)) {
				dis.setText(scr);
				lastDisplay = scr;
			}
		} else {
			String clrhm = (char) 27 + "[H" + (char) 27 + "[J";
			System.out.print(clrhm);
			while (scr.indexOf("\n") > 0) {
				System.out.println(scr.substring(0, scr.indexOf("\n")) + "\r");
				scr = scr.substring(scr.indexOf("\n") + 1);
			}
			System.out.print(scr);
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		// check time
		if (GUI) {
			dis.setText(lastDisplay);
		}
		if (lastcommandtime + MINTIME > System.currentTimeMillis()) {
			// System.out.println("NOTE users must wait "+MINTIME+" NOT
			// "+(System.currentTimeMillis()-lastcommandtime));
			return;
		}
		sendMove(e.getKeyChar());
	}

	public void sendMove(char c) {
		// check 2,4,6,8,5,0
		// rouge keys hjkl ' '
		if (c == 'k')
			c = '8';
		else if (c == 'j')
			c = '2';
		else if (c == 'h')
			c = '4';
		else if (c == 'l')
			c = '6';
		else if (c == ' ')
			c = '5';
		else if (c != '2' && c != '4' && c != '6' && c != '8' && c != '5'
				&& c != '0') {
			// System.out.println("NOTE users must key 2,4,6,8,5,0 (also
			// h,j,k,l) not "+c);
			return;
		}
		if (status == 0 && c != '0') {
			// System.out.println("NOTE no dead man walk or fire");
			return;
		}
		send(nl, "MOVE " + c);
		lastcommandtime = System.currentTimeMillis();
	}

	public void actionPerformed(ActionEvent ae) {
		String c = ae.getActionCommand();
		if (ae.getSource() == sysop) {
			send(nl, c);
			// allow send "CALL ip:port" or "SPEED #"
		} else if (ae.getSource() instanceof InputListener) {
			if (c.length() == 0)
				return;
			else if (c.length() == 1) {
				sendMove(c.charAt(0));
			} else {
				// System.out.println("sending command "+c);
				// for (int a=0;a<c.length();a++)
				// System.out.println("c["+a+"]= "+c.charAt(a));
				// System.out.println("c.length= "+c.length());
				// char cc=c.charAt(0);
				// if ((cc>='a' && cc<='z') || (cc>='A' && cc<='Z') || (cc>='0'
				// && cc<='9'))
				// watch out for CURSES JUNK
				if (!c.startsWith("(") && !c.startsWith("[")) {
					send(nl, c);
					// allow send "CALL ip:port" or "SPEED #"
				}
			}
			// send(nl,"MOVE "+c);
		}

	}

	public void actionPerformed(Socket S, String c) {
		// System.out.println("NOTE received command"+ae);
		if (c.trim().equalsIgnoreCase("connection_closed")) {
			System.out.println("MyError: Server disconected.");
			System.exit(0);
		} else if (c.startsWith("#")) {
			if (c.toUpperCase().indexOf("LOGGEDIN") >= 0) {
				name = c.substring(c.toUpperCase().indexOf("LOGGEDIN") + 8)
						.trim();
			} else if (c.toUpperCase().indexOf("LOGIN FAIL") >= 0) {
				System.out
						.println("MyError: User alread logged in.  Login Failed.");
				System.exit(0);
			} else {
				System.out.println("NOTE RECEIVED:" + c.substring(1));
			}
		} else if (c.startsWith(RealmS.MAPINTRO)) {
			// note char "M"
			readMap(c);
		} else if (c.startsWith(RealmS.USERSINTRO)) {
			// note char "U"
			// long oldtime=System.currentTimeMillis();
			readUsers(c);
			updateDisplay();
			// System.out.println("NOTE Time to
			// update="+(System.currentTimeMillis()-oldtime));
		}

	}

	public void send(Socket s, String line) {
		OutputStream o;
		line += "\n";
		try {
			o = s.getOutputStream();
			o.write(line.getBytes());
			o.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readUsers(String s) {
		if (s.startsWith(RealmS.USERSINTRO))
			s = s.substring(RealmS.USERSINTRO.length()).trim();
		users = new Vector();
		while (s.length() > 0) {
			String cur = s;
			if (s.indexOf(" ") >= 0) {
				cur = cur.substring(0, cur.indexOf(" "));
			}
			s = s.substring(cur.length()).trim();
			RealmU ru = RealmU.parse(cur);
			users.addElement(ru);
			if (ru != null && ru.name.equals(name)) {
				x = ru.x;
				y = ru.y;
				dir = ru.dir;
				status = ru.status;
			}
		}
	}

	public void readMap(String s) {
		// MAP X Y DATA
		// System.out.println("NOTE: map received="+s);
		int rows = 0, cols = 0;
		if (s.startsWith(RealmS.MAPINTRO))
			s = s.substring(RealmS.MAPINTRO.length());
		try {
			if (s.indexOf(" ") >= 0) {
				rows = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ") + 1);
			}
			if (s.indexOf(" ") >= 0) {
				cols = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ") + 1);
			}
		} catch (NumberFormatException nfe) {
			System.out.println("MyError: cannot read map");
		}
		map = new char[rows][];
		for (int a = 0; a < rows; a++) {
			map[a] = new char[cols];
			for (int b = 0; b < cols; b++) {
				map[a][b] = s.charAt(a * cols + b);
			}
		}
		// for (int a=0;a<map.length;a++) {
		// for (int b=0;b<map[a].length;b++) {
		// System.out.print(map[a][b]);
		// }
		// System.out.println("");
		// }
	}

	boolean loggedOut = false;

	public void logout() {
		send(nl, "LOGOUT");
		loggedOut = true;
	}

	public void relogin() {
		if (loggedOut)
			send(nl, "LOGIN " + name);
		loggedOut = false;
	}
}
