package aj.games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class RealmS {

	static String MAPINTRO = "M";

	static String COMMENTINTRO = "#";

	static String USERSINTRO = "S";

	int MAPSIZE = 40;

	boolean VALIDMAP = false;// set to true if ' ' is read in map

	boolean OBSTICALSBLOCK = true;// cannot move over obsticals

	boolean STAYONMAP = true;// cannot move beyond map boundarys

	boolean NOJUMPING = true;// cannot jump over users

	int UPDATETIME = 400;// MILLIS TILL AUTOTURN PASS AND SEND UPDATE 200=
							// 5/sec

	int SHOTRANGE = 3;

	boolean WALLSTOPSHOT = true;

	boolean ONMAPONLYSHOT = true;

	boolean MULTIKILLSHOT = false;

	boolean KILLSELFALLOWED = false;

	boolean ABOUTFACE = true;

	// boolean REFLECTSHOT;

	Vector connects = new Vector();

	Vector users = new Vector();

	char map[][];

	String mapSend;

	String usersSend = USERSINTRO;

	int currTurn = 0;

	public static void help() {
		System.out.println("Format java aj.games.RealmS [-p<portnum>]");
		System.exit(0);
	}

	public static void main(String s[]) {
		RealmS rs = null;
		if (s.length == 0)
			help();
		for (int a = 0; a < s.length; a++) {
			if (s[a].startsWith("-"))
				s[a] = s[a].substring(1);
			if (s[a].startsWith("?")) {
				help();
			}
			if (s[a].toLowerCase().startsWith("p")) {
				s[a] = s[a].substring(1);
				if (s[a].length() < 1 && s.length - 1 > a)
					a++;
				try {
					rs = new RealmS(Integer.parseInt(s[a]));
				} catch (NumberFormatException nfe) {
					System.out.println("MyError: bad number in port command."
							+ s[a]);
					System.exit(0);
				}
			}
		}
		if (rs == null) {
			// default case
			rs = new RealmS(RealmC.DEFAULTSERVERPORT);
		}
	}

	int delay = 100;

	public RealmS(int port) {
		readMap();
		readCallList();
		try {
			ServerSocket ss = new ServerSocket(port);
			new Thread() {
				public void run() {
					try {
						Thread.sleep(delay);
						currTurn++;
						sendUpdate();
					} catch (Exception e) {
					}
				}
			}.start();
			while (true) {
				final Socket nl = ss.accept();
				connects.add(nl);
				sendMap(nl);

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
		} catch (IOException ioe) {
			System.out.println("MyError: unexpected error found 1");
		}
	}

	public void addCalledUser(String host, int port) {
		try {
			final Socket nl = new Socket(host, port);
			connects.add(nl);
			sendMap(nl);

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
		} catch (IOException ioe) {
			System.out.println("MyError: cannot connect to user at " + host
					+ " " + port);
		}
	}

	public void makeDefaultMap() {
		VALIDMAP = true;
		map = new char[MAPSIZE][];
		for (int a = 0; a < MAPSIZE; a++) {
			map[a] = new char[MAPSIZE];
			for (int b = 0; b < MAPSIZE; b++) {
				map[a][b] = ' ';
				if (a == 0 || b == 0 || a == MAPSIZE - 1 || b == MAPSIZE - 1)
					map[a][b] = 'X';
			}
		}
		buildMapSend();
	}

	public void sendMap(Socket nl) {
		send(nl, mapSend);
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

	public void readCallList() {
		try {
			Class aClass = getClass();
			InputStream in = aClass.getResourceAsStream("CALL.TXT");
			if (in == null)
				return;
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				s = s.trim();
				if (s.startsWith("#"))
					continue;
				String host = s.trim();
				int port = RealmC.DEFAULTCLIENTPORT;
				if (s.indexOf(" ") > 0) {
					port = Integer.parseInt(host.substring(
							host.indexOf(" ") + 1).trim());
					host = host.substring(0, host.indexOf(" "));
				}
				System.out.println("NOTE calling user at " + host + " " + port);
				addCalledUser(host, port);
			}
			br.close();
		} catch (IOException ioe) {
			System.out.println("MyError: cannot read callup file");
		} catch (NumberFormatException nfe) {
			System.out.println("MyError: bad number in callup file");
		}
	}

	public void readMap() {
		try {
			Class aClass = getClass();
			InputStream in = aClass.getResourceAsStream("MAP.TXT");
			if (in == null) {
				makeDefaultMap();
				return;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			Vector v = new Vector();
			while (true) {
				String t = br.readLine();
				if (t == null)
					break;
				v.addElement(t);
			}
			map = new char[v.size()][];
			VALIDMAP = false;
			for (int a = 0; a < v.size(); a++) {
				String t = (String) v.elementAt(a);
				map[a] = new char[t.length()];
				for (int b = 0; b < Math.min(t.length(), map[0].length); b++) {
					map[a][b] = t.charAt(b);
					if (map[a][b] == ' ')
						VALIDMAP = true;
				}
				for (int b = map[a].length; b < map[0].length; b++) {
					map[a][b] = 'X';
				}
			}
			br.close();
		} catch (IOException ioe) {
			System.out.println("MyError: unexpected error found 2");
		}
		buildMapSend();
	}

	public void buildMapSend() {
		mapSend = MAPINTRO + "" + map.length + " " + map[0].length + " ";
		for (int a = 0; a < map.length; a++) {
			for (int b = 0; b < map[0].length; b++) {
				mapSend += map[a][b];
			}
		}
	}

	public void actionPerformed(Socket nl, String c) {
		// check for volital only 1 users at a time in here
		// System.out.println("NOTE received "+ae.getActionCommand());
		boolean changed = false;
		if (true) {

			if (c.toLowerCase().trim().equalsIgnoreCase("connection_closed")) {
				// loggin out
				// System.out.println("NOTE user logging out
				// "+ae.getActionCommand());
				connects.removeElement(nl);
				for (int a = 0; a < users.size(); a++) {
					RealmU ru = (RealmU) users.elementAt(a);
					if (ru.nl == nl) {
						users.removeElement(ru);
						break;
					}
				}
				c = "NOOP";
				changed = true;
			} else if (c.toUpperCase().startsWith("#SPEED ")) {
				UPDATETIME = Integer.parseInt(c.substring(6).trim());
				UPDATETIME = Math.max(100, UPDATETIME);
				delay = UPDATETIME;
				send(nl, "#UPDATE SPEED CHANGED TO " + UPDATETIME);
			} else if (c.toUpperCase().startsWith("#CALL ")) {
				// callup user at IP and host
				c = c.substring(5).trim();
				if (c.indexOf(":") < 0)
					return;
				String host = c.substring(0, c.indexOf(":"));
				int port = Integer.parseInt(c.substring(c.indexOf(":") + 1)
						.trim());
				send(nl, "#CALLING REQUESTED COMPUTER.  " + host + " " + port);
				addCalledUser(host, port);
			} else if (c.toUpperCase().startsWith("LOGOUT")) {
				for (int a = 0; a < users.size(); a++) {
					RealmU ru = (RealmU) users.elementAt(a);
					if (ru.nl == nl) {
						send(nl, "#LOGGEDOUT");
						users.removeElement(ru);
						return;
					}
				}
				send(nl, "#LOGGEDOUT FAILED. NOT LOGGED IN.");
			} else if (c.toUpperCase().startsWith("GHOST")) {
				String name = c.substring(5).trim();
				if (name.length() == 0) {
					// default name
					name = "Ghost" + users.size() % 10;
				}
				// default name may be bad
				for (int a = 0; a < users.size(); a++) {
					RealmU ru = (RealmU) users.elementAt(a);
					if (ru.nl == nl) {
						ru.name = name;
						ru.status = 0;
						send(nl, "#RELOGGEDIN GHOST " + name);
						return;
					}
					if (ru.name.equals(name)) {
						send(nl, "#LOGIN GHOST FAIL");
						return;
					}
				}
				send(nl, "#LOGGEDIN GHOST " + name);
				RealmU ghost = new RealmU(name, 0, 0, '>', 0);
				// make ghost realm useer
				ghost.nl = nl;
				users.addElement(ghost);
			} else if (c.toUpperCase().startsWith("LOGIN")) {
				String name = c.substring(5).trim();
				if (name.length() == 0) {
					// default name
					name = "User" + users.size() % 100;
				}
				// default name may be bad
				for (int a = 0; a < users.size(); a++) {
					RealmU ru = (RealmU) users.elementAt(a);
					if (ru.nl == nl) {
						ru.name = name;
						send(nl, "#RELOGGEDIN " + name);
						return;
					}
					if (ru.name.equals(name)) {
						send(nl, "#LOGIN FAIL");
						return;
					}
				}
				send(nl, "#LOGGEDIN " + name);
				// good name
				RealmU ru = new RealmU(name, 0, 0, '>', 1);
				ru.y = (int) (Math.random() * map.length);
				ru.x = (int) (Math.random() * map[ru.y].length);
				if (VALIDMAP && OBSTICALSBLOCK) {
					while (map[ru.y][ru.x] != ' ') {
						ru.y = (int) (Math.random() * map.length);
						ru.x = (int) (Math.random() * map[ru.y].length);
					}
				}
				ru.nl = nl;
				// add to user list
				users.addElement(ru);
				changed = true;
				// System.out.println("NOTE LOGIN COMPLETE");
			} else if (c.toUpperCase().startsWith("MOVE ")) {
				// System.out.println("NOTE MOVE FOUND");
				// find who moved
				RealmU me = null;
				for (int a = 0; a < users.size(); a++) {
					RealmU ru = (RealmU) users.elementAt(a);
					if (ru.nl == nl) {
						me = ru;
						break;
					}
				}
				if (me == null) {
					System.out
							.println("MyError: Move found with no user.  Ignoring command :"
									+ c);
					return;
				}
				// check user time
				if (!readyToMove(me)) {
					// System.out.println("NOTE: user not allowd to move. Too
					// fast.");
					return;
				}
				char cmd = c.charAt(5);
				if (cmd == '2' || cmd == '4' || cmd == '6' || cmd == '8') {
					int x = me.x, y = me.y;
					char dir = me.dir;
					if (cmd == '2') {
						if (dir != '^' || !ABOUTFACE)
							y++;
						dir = 'v';
					} else if (cmd == '8') {
						if (dir != 'v' || !ABOUTFACE)
							y--;
						dir = '^';
					} else if (cmd == '6') {
						if (dir != '<' || !ABOUTFACE)
							x++;
						dir = '>';
					} else if (cmd == '4') {
						if (dir != '>' || !ABOUTFACE)
							x--;
						dir = '<';
					}
					// check map collision
					if (STAYONMAP
							&& !(y >= 0 && x >= 0 && y < map.length && x < map[y].length)) {
						// System.out.println("NOTE moved off map not allowed");
						y = me.y;
						x = me.x;
					}
					if (OBSTICALSBLOCK && y >= 0 && x >= 0 && y < map.length
							&& x < map[y].length && map[y][x] != ' ') {
						// System.out.println("NOTE movement over obsticals not
						// allowed");
						y = me.y;
						x = me.x;
					}
					// check users collision
					for (int a = 0; a < users.size(); a++) {
						RealmU ru = (RealmU) users.elementAt(a);
						if (ru.x == x && ru.y == y && ru != me
								&& ru.status != 0 && NOJUMPING) {
							// System.out.println("NOTE movement over users not
							// allowed");
							y = me.y;
							x = me.x;
						}
					}
					// applymove
					changed = (me.x != x | me.y != y || me.dir != dir);
					me.x = x;
					me.y = y;
					me.dir = dir;
					me.turnNumber = currTurn;
					me.lastMoveTime = System.currentTimeMillis();
				} else if (cmd == '5') {
					me.turnNumber = currTurn;
					// take turn
					me.lastMoveTime = System.currentTimeMillis();
					// take turn
					// System.out.println("NOTE shooting not complete");
					// check user hit and map block
					// kill user
					int dx = 0, dy = 0;
					if (me.dir == '^')
						dy = -1;
					if (me.dir == 'v')
						dy = 1;
					if (me.dir == '>')
						dx = 1;
					if (me.dir == '<')
						dx = -1;
					for (int a = 1; a < SHOTRANGE; a++) {
						// dont shot my square
						// System.out.println("shooting lv"+a+" "+dx+" "+dy);
						int xx = me.x + a * dx, yy = me.y + a * dy;
						if (ONMAPONLYSHOT
								&& !(yy >= 0 && xx >= 0 && yy < map.length && xx < map[yy].length)) {
							// System.out.println("NOTE stop bullit leaving
							// map");
							// REFLECTSHOT
							break;
						}
						if (WALLSTOPSHOT && yy >= 0 && xx >= 0
								&& yy < map.length && xx < map[yy].length
								&& map[yy][xx] != ' ') {
							// System.out.println("NOTE stop bullit over wall
							// ");
							// REFLECTSHOT
							break;
						}
						// System.out.println("NOTE bullit moving on");
						for (int b = 0; b < users.size(); b++) {
							RealmU ru = (RealmU) users.elementAt(b);
							if (ru.x == xx && ru.y == yy
									&& (ru != me || KILLSELFALLOWED)) {
								ru.status = 0;
								changed = true;
								// ru.killer=me.name;
								if (!MULTIKILLSHOT)
									break;
							}
						}
					}
					if (!changed) {
						// System.out.println("NOTES missed");
					}
				} else if (cmd == '0') {
					if (me.status == 0) {
						me.status = 1;
						me.y = (int) (Math.random() * map.length);
						me.x = (int) (Math.random() * map[me.y].length);
						if (VALIDMAP && OBSTICALSBLOCK) {
							while (map[me.y][me.x] != ' ') {
								me.y = (int) (Math.random() * map.length);
								me.x = (int) (Math.random() * map[me.y].length);
							}
						}
						changed = true;
					}
				}
			} else if (c.equalsIgnoreCase("NOOP")) {
			} else if (c.startsWith(MAPINTRO)) {
				System.out
						.println("MyError: Connect to self.  Received map. Killing connection. "
								+ c);
				send(nl, "#Map received killing connection.");
				try {
					nl.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("MyError: bad command received. " + c);
				send(nl, "#Command not understood");
				// nl.close();
			}
		}

		if (changed) {
			// rebuild user list
			changed = false;
			usersSend = USERSINTRO;
			for (int a = 0; a < users.size(); a++) {
				usersSend += ((RealmU) users.elementAt(a)).toString();
				if (a + 1 < users.size())
					usersSend += " ";
			}
			// System.out.println("NOTE: usersSend updated");
		}
	}

	long lastupdate = 0;

	public void sendUpdate() {
		lastupdate = System.currentTimeMillis();
		for (int a = 0; a < users.size(); a++) {
			RealmU ru = (RealmU) users.elementAt(a);
			send(ru.nl, usersSend);
		}
	}

	public boolean readyToMove(RealmU me) {
		if (me.turnNumber < currTurn) {
			// System.out.println("NOTE "+me.name+" moving in old turn
			// "+currTurn);
			return true;
			// Have you moved this turn?
		}
		if (me.lastMoveTime + UPDATETIME < System.currentTimeMillis()) {
			// is it time for a new turn?
			currTurn++;
			// System.out.println("NOTE user "+me.name+" moving UPDATETIME
			// passed. New turn "+currTurn);
			sendUpdate();
			return true;
		}
		boolean alldone = true;
		for (int a = 0; a < users.size(); a++) {
			// check all moved (do we need to go faster?)
			RealmU ru = (RealmU) users.elementAt(a);
			if (ru.turnNumber < currTurn && ru.status != 0) {
				// System.out.println("NOTE waiting for user "+ru.name+" to
				// finish turn "+currTurn+" still has
				// "+((me.lastMoveTime+UPDATETIME)-System.currentTimeMillis()));
				alldone = false;
				break;
			}
		}
		if (alldone) {
			currTurn++;
			// System.out.println("NOTE all users moved early. New turn
			// "+currTurn+" Actual turn
			// time="+(System.currentTimeMillis()-me.lastMoveTime));
			sendUpdate();
		}
		return alldone;
	}
}
/*
 * communication protocol
 * 
 * user - logout (based on connection) login <name> (one login per connection)
 * move <2,4,6,8,5,0> call <ip[:port]> speed <millis> ghost <name> //login with
 * dead status
 * 
 * 
 * server - M <rows> <cols> <data> U <users>[' '<users>]* # echo notes
 * (including LOGIN FAIL, CALLING, SPEED SET)
 * 
 * status - alive dead ghost (observer) invis (points) invul (sysop) passwall
 * (points) offmap shootpasswall (points) shootoffmap change map (points and
 * bandwidth warning) hitpoints (1-10) shieldpoints (1-10) non-reporting (sysop
 * no datamode) score team shotlimit
 * 
 * flags add to score add hitpoint add shieldpoint add shotlimit add
 * clone/onelife robot helper kill all opponents (capture flag) kill non-team
 * create new flag increase sense/vis increase shotrange add multi shot -
 * rev/lef/rig resurect point (nopickup) nextflag point (nopickup) kill self
 * (mine)
 * 
 * flags invisible or visible flags pickup or nopickup flags repeating, flag
 * group, replace, use up
 * 
 */
