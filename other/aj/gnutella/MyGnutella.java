package aj.gnutella;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import aj.awt.SimpleWindowManager;
import aj.misc.Stuff;

public class MyGnutella implements Runnable {
	static int BACKUPHOSTTIME = 1 * 60 * 1000;// 20 minutes

	Vector currentDownloadList = new Vector();

	Vector shareFileList = new Vector();

	Vector hostList = new Vector();

	int numshareFiles = 0;

	int numshareKilobytes = 0;

	Vector connectedList = new Vector();

	Vector connectedThreadList = new Vector();

	static String portBlockList = "";

	static String subPortList[] = null;

	public boolean validPort(int x) {
		if (subPortList == null) {
			subPortList = Stuff.getTokens(portBlockList, ",");
		}
		for (int a = 0; a < subPortList.length; a++) {
			try {
				if (subPortList[a].indexOf("-") >= 0) {
					String tt[] = Stuff.getTokens(subPortList[a], "-");
					int y = Integer.parseInt(tt[0]);
					int z = Integer.parseInt(tt[1]);
					if (x >= y && x <= z)
						return false;
					continue;
				}
				int y = Integer.parseInt(subPortList[a]);
				if (y == x)
					return false;
			} catch (NumberFormatException nfe) {
				String t[] = new String[subPortList.length];
				for (int b = 0; b < subPortList.length; b++) {
					if (b < a)
						t[b] = subPortList[b];
					if (b > a)
						t[b - 1] = subPortList[b];
				}
				subPortList = t;
				a--;
			}
		}
		return true;
	}

	public synchronized void killGnuThread(GnuProtocol gp) {
		for (int a = 0; a < connectedList.size(); a++) {
			if (connectedList.elementAt(a).equals(gp)) {
				Thread ttt = (Thread) connectedThreadList.elementAt(a);
				ttt.interrupt();
				connectedList.removeElementAt(a);
				connectedThreadList.removeElementAt(a);
			}
		}
	}

	static String shareDir = "";

	static String myip;

	static boolean clearhost = false;

	static boolean autohost = false;

	static boolean logping = false;

	static boolean logpong = false;

	static boolean logquery = false;

	static boolean logqueryhit = false;

	static boolean logpush = false;

	static boolean logunk = true;

	static boolean logToMe = false;

	static boolean logrout = false;

	static boolean logconnect = false;

	static boolean logFilter = false;

	static boolean otherpong = false;

	static boolean filterPong = false, filterHit = false, filterPush = false;

	static boolean hosttimereset = false;// clears old LOST, FAILED and
											// Recent connect times

	static int MINHOSTS = 4;

	static int MAXHOSTS = 4;

	static int MAXHOSTLISTSIZE = 5000;

	static int DEFAULTPORT = 6346;

	static int serverport = 7778;

	static int httpport = 7779;

	static MyGnutellaServer mgs = null;

	static boolean NOPONG = false;

	static boolean LIESHARE = false;

	static boolean NOGUI = false;

	static JTextArea activeHosts = null;

	static JTextArea queryText = null;

	static JTextArea queryHitText = null;

	static int MAXSEENPING = 200;

	static int MAXSEENQUERY = 1000;

	static int MAXSEENQUERYHIT = 100;

	static int MAXSENTHEADER = 2000;

	public Vector sentHeaders = new Vector();

	public Vector seenPings = new Vector();

	public Vector seenQuery = new Vector();

	public Vector seenQueryHit = new Vector();

	public boolean alreadySentHeader(Header h, GnuProtocol gp) {
		boolean sentBefore = false;
		Vector v = (Vector) sentHeaders.clone();
		for (int a = 0; a < v.size(); a++) {
			Header h2 = (Header) v.elementAt(a);
			if (h2.equals(h))
				sentBefore = true;
			;
		}
		if (sentBefore) {
			gp.dropSentBefore++;
			return true;
		}
		return false;
	}

	public void sendHeader(Header h, GnuProtocol gp) {
		gp.send(h);
		if (!sentHeaders.contains(h))
			sentHeaders.addElement(h);
		if (sentHeaders.size() > MAXSENTHEADER) {
			sentHeaders.removeElementAt(0);
		}
	}

	int descIdCount = (int) (Math.random() * 256 * 256);

	Vector mydescs = new Vector();

	public boolean myDescriptorId(String des) {
		for (int a = 0; a < mydescs.size(); a++) {
			String te = (String) mydescs.elementAt(a);
			if (te.equals(des))
				return true;
		}
		return false;
	}

	static int MAXDESCCOUNT = 100;// number of my own descriptor ides to track

	public synchronized String getDescriptorId() {
		String myid = "";
		for (int a = 0; a < 16; a++) {
			myid += (char) (Math.random() * 256);
		}
		mydescs.addElement(myid);
		if (mydescs.size() > MAXDESCCOUNT) {
			mydescs.removeElementAt(0);
		}
		return myid;
	}

	public static void main(String s[]) throws Exception {
		myip = InetAddress.getLocalHost().toString();
		if (myip.indexOf("/") >= 0)
			myip = myip.substring(myip.indexOf("/") + 1);

		// String host="127.0.0.1";
		if (s.length == 0 || s[0].indexOf("?") >= 0) {
			System.out.println("Format: java aj.MyGnutella [<host:[<port>]>]");
			System.out.println("   -AUTO<num> autohost connect");
			System.out.println("   -BLOCKEDPORTS#,#,#-#");
			System.out.println("   -FILTER<PONG|HIT|PUSH>");
			System.out
					.println("   -OTHERPONG Reply to ping with someone elses pong");
			System.out.println("   -NOGUI no gui support");
			System.out
					.println("   -LOG<PING|PONG|QUERY|HIT|PUSH|UNK|CONNECT|FILTER>");
			System.out.println("   <host:[<port>] of gnutella server");
			System.out.println("   -DIR:sharingdir directory to share");
			System.out.println("   -NOPONG don't pong to pings");
			System.out
					.println("   -LIESHARE lie about number and size of share");
			System.out
					.println("   -HOSTTIMERESET drops all delays for connecting to hosts");
			System.out.println("   -CLEARHOST clear host database");

			System.exit(0);
		}
		s = getOptions(s);
		if (!NOGUI) {
			queryText = new JTextArea();
			queryHitText = new JTextArea();
			activeHosts = new JTextArea();
			JFrame f = new JFrame();
			f.addWindowListener(new SimpleWindowManager());
			f.getContentPane().add("Center", activeHosts);
			f.setSize(new Dimension(300, 200));
			f.setVisible(true);
		}
		MyGnutella mg = new MyGnutella(s);
		new Thread(mg).start();
		mg.startServer();
	}

	public static String[] getOptions(String s[]) {
		for (int a = 0; a < s.length; a++) {
			String t[] = new String[s.length - 1];
			for (int b = 0; b < a; b++)
				t[b] = s[b];
			if (s[a].equalsIgnoreCase("-HOSTTIMERESET")) {
				System.out.println("HOST TIME RESET");
				hosttimereset = true;
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].toUpperCase().startsWith("-LOG")) {
				if (s[a].toUpperCase().indexOf("FILTER") >= 0) {
					System.out.println("LOG FILTER ENABLED");
					logFilter = true;
				}
				if (s[a].toUpperCase().indexOf("PING") >= 0) {
					System.out.println("LOG PING ENABLED");
					logping = true;
				}
				if (s[a].toUpperCase().indexOf("PONG") >= 0) {
					System.out.println("LOG PONG ENABLED");
					logpong = true;
				}
				if (s[a].toUpperCase().indexOf("QUERY") >= 0) {
					System.out.println("LOG QUERY ENABLED");
					logquery = true;
				}
				if (s[a].toUpperCase().indexOf("HIT") >= 0) {
					System.out.println("LOG QUERYHIT ENABLED");
					logqueryhit = true;
				}
				if (s[a].toUpperCase().indexOf("PUSH") >= 0) {
					System.out.println("LOG PUSH ENABLED");
					logpush = true;
				}
				if (s[a].toUpperCase().indexOf("UNK") >= 0) {
					System.out.println("LOG UNKNOWN ENABLED");
					logunk = true;
				}
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].equalsIgnoreCase("-DIR:")) {
				System.out.println("SHARE DIRECTORY ENABLED");
				shareDir = s[a].substring(5);
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].equalsIgnoreCase("-NOGUI")) {
				System.out.println("GUI display disabled");
				NOGUI = true;
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].equalsIgnoreCase("-LIESHARE")) {
				System.out.println("LIESHARE ENABLED");
				LIESHARE = true;
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].toUpperCase().startsWith("-BLOCKEDPORTS")) {
				portBlockList = s[a].substring(13);
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].toUpperCase().startsWith("-FILTER")) {
				if (s[a].toUpperCase().indexOf("PONG") >= 0) {
					filterPong = true;
					System.out.println("FILTERPONG ENABLED");
				}
				if (s[a].toUpperCase().indexOf("HIT") >= 0) {
					filterHit = true;
					System.out.println("FILTERHIT ENABLED");
				}
				if (s[a].toUpperCase().indexOf("PUSH") >= 0) {
					filterPush = true;
					System.out.println("FILTERPUSH ENABLED");
				}
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].equalsIgnoreCase("-NOPONG")) {
				System.out.println("NOPONG ENABLED");
				NOPONG = true;
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].equalsIgnoreCase("-ADDHOSTS")) {
				System.out.println("ADDING HOSTS BOOT");
				String u[] = new String[0];
				MyGnutella mg = new MyGnutella(u);
				System.out.println("ADDING HOSTS");
				for (int b = a + 1; b < s.length; b++) {
					String host = s[b];
					int port = DEFAULTPORT;
					if (host.indexOf(":") > 0) {
						try {
							port = Integer.parseInt(host.substring(host
									.indexOf(":") + 1));
						} catch (NumberFormatException nfe) {
							System.out.println("MyError: Bad port in " + s[b]);
						}
						host = host.substring(0, host.indexOf(":"));
					}
					System.out.println("adding host " + host + ":" + port);
					mg.addHost(host, port, null);
				}
				mg.lastWrite = 0;
				mg.writeHostList();
				System.exit(0);
			} else if (s[a].toUpperCase().startsWith("-AUTO")) {
				System.out.println("AUTO HOST CONNECT ENABLED");
				autohost = true;
				s[a] = s[a].substring("-AUTO".length());
				try {
					MAXHOSTS = Integer.parseInt(s[a]);
				} catch (NumberFormatException nfe) {
				}
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			} else if (s[a].equalsIgnoreCase("-CLEARHOST")) {
				System.out.println("CLEAR OLD HOSTS");
				clearhost = true;
				for (int b = a + 1; b < s.length; b++)
					t[b - 1] = s[b];
				s = t;
				a--;
			}
		}
		return s;
	}

	String oldHostTextDisplay = "";

	public MyGnutella(String hosts[]) {
		if (!clearhost)
			readHostList();
		readShareDir();
		for (int a = 0; a < hosts.length; a++) {
			// System.out.println("Connecting to remote server "+hosts[a]);
			String host = hosts[a];
			int port = DEFAULTPORT;
			if (host.indexOf(":") > 0) {
				try {
					port = Integer.parseInt(host
							.substring(host.indexOf(":") + 1));
				} catch (NumberFormatException nfe) {
					System.out.println("MyError: Bad port in " + hosts[a]);
				}
				host = host.substring(0, host.indexOf(":"));
			}
			MyHost mh = new MyHost(host, port, null);
			addHost(mh.getIp(), mh.port, null);
			GnuProtocol gp = new GnuProtocol(mh, this);
			Thread ttt = new Thread(gp);
			connectedList.addElement(gp);
			connectedThreadList.addElement(ttt);
			ttt.start();
		}
	}

	public void startServer() {
		mgs = new MyGnutellaServer(this);
		new Thread(mgs).start();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

			Vector v = (Vector) connectedList.clone();
			// System.out.println("Clear dead connections");
			for (int a = 0; a < v.size(); a++) {
				GnuProtocol gp = (GnuProtocol) v.elementAt(a);
				if (gp.active)
					gp.sendPing();
			}
			int activeCount = 0;
			for (int a = 0; a < v.size(); a++) {
				GnuProtocol gp = (GnuProtocol) v.elementAt(a);
				gp.checkTimeOut();
				if (gp.active) {
					activeCount++;
				}
				if (gp.bad) {
					killGnuThread(gp);
					// System.out.println("Dead connection.
					// "+gp.mh.getIp()+":"+gp.mh.port+" dropping!");
				}
			}
			// REMOVE EXTRA HOSTS ABOVE MIN HOSTS taht are active
			if (activeCount > MINHOSTS) {
				for (int a = 0; a < v.size(); a++) {
					GnuProtocol gp = (GnuProtocol) v.elementAt(a);
					gp.checkTimeOut();
					if (gp.active && activeCount > MINHOSTS) {
						// Thread ttt=(Thread)connectedList.elementAt(a);
						// ttt.interrupt();
						killGnuThread(gp);
						activeCount--;
					} else if (!gp.active) {
						killGnuThread(gp);
					}
				}
			}
			if (connectedList.size() < MINHOSTS && autohost) {
				// System.out.println("Go get auto connection");
				while (connectedList.size() < MAXHOSTS) {
					if (!addAutoConnect())
						break;
				}
			}

			v = (Vector) connectedList.clone();
			String hostTextDisplay = "";
			for (int a = 0; a < v.size(); a++) {
				GnuProtocol gp = (GnuProtocol) v.elementAt(a);
				hostTextDisplay += gp.toString() + "\n";
			}
			if (!oldHostTextDisplay.equals(hostTextDisplay)) {
				if (!NOGUI) {
					activeHosts.setText(hostTextDisplay);
				}
				oldHostTextDisplay = hostTextDisplay;
			}
		}
	}

	public MyHost getBestHost() {
		boolean nogood = true;
		int start = (int) (Math.random() * hostList.size());
		MyHost mh = (MyHost) hostList.elementAt(start);
		Vector v = (Vector) connectedList.clone();
		int count = 0;
		while (nogood) {
			count++;
			if (count > hostList.size())
				return null;
			int a = (start + count) % hostList.size();
			mh = (MyHost) hostList.elementAt(a);
			if (!validPort(mh.port)) {
				// System.out.println("port filtered on getBestHost "+mh.port);
				continue;
			}
			if (mh.getIp().equals(myip))
				continue;
			if (!mh.tryConnect())
				continue;
			double chance = (1.0 * mh.connectCount + 1)
					/ (mh.connectFailCount + 1);
			if (chance < 1) {
				if (Math.random() > chance) {
					// System.out.println("Skipping! Low available
					// ="+Stuff.trunc(chance,2));
					continue;
				}
				// System.out.println("Trying! Low available
				// ="+Stuff.trunc(chance,2));
			}
			boolean fresh = true;
			for (int b = 0; b < v.size(); b++) {
				GnuProtocol gp = (GnuProtocol) v.elementAt(b);
				if (gp.ip.equalsIgnoreCase(mh.getIp())) {
					fresh = false;
				}
			}
			if (!fresh) {
				continue;
			}
			// add last attempted
			nogood = false;
		}
		return mh;
	}

	private boolean addAutoConnect() {
		if (hostList.size() < 1) {
			// System.out.println("Cannot auto connect no hosts available");
			return false;
		}
		// choose connection with greatest connectCount
		// key 1 = connect count, #2 last seen, #3 lowest failcount
		// check not already connected
		MyHost mh = getBestHost();
		if (mh == null)
			return false;
		GnuProtocol gp = new GnuProtocol(mh, this);
		Thread ttt = new Thread(gp);
		ttt.start();
		connectedList.addElement(gp);
		connectedThreadList.addElement(ttt);
		// System.out.println("New auto connect to "+mh.getIp()+":"+mh.port+"
		// connectedList size= "+connectedList.size());
		return true;
	}

	private void readShareDir() {
		// clear old file shared list
		// read all files
		// numshareFiles=total file count
		// numshareKilobytes = number of kb shared
	}

	private void readHostList() {
		readHostFile("hosts");
		readHostFile("hosts2");
		System.out.println("Total hosts read " + hostList.size());
		for (int b = 0; b < hostList.size(); b++) {
			MyHost mh2 = (MyHost) hostList.elementAt(b);
			mh2.numhops = -1;
			if (hosttimereset) {
				mh2.lastSeenTime = 0;
				mh2.lastFailConnectTime = 0;
				mh2.lastConnectTime = 0;
				mh2.lastConnectLostTime = 0;
			}
		}
	}

	private void readHostFile(String hostfilename) {
		try {
			// System.out.println("reading host list A"+hostList.size());
			BufferedReader br = new BufferedReader(new FileReader(shareDir
					+ hostfilename));

			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				if (s.trim().equals(""))
					continue;
				MyHost mh = MyHost.parse(s);
				if (mh == null)
					continue;
				boolean found = false;
				for (int b = 0; b < hostList.size(); b++) {
					MyHost mh2 = (MyHost) hostList.elementAt(b);
					if (mh2.equals(mh))
						found = true;
				}
				if (!found) {
					if (validMyHost(mh)) {
						hostList.addElement(mh);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println("MyError: read hostlist file failed " + e);
		}
	}

	public boolean validMyHost(MyHost mh) {
		if (mh.getIp().startsWith("10.") || mh.getIp().startsWith("192.")
				|| mh.getIp().startsWith("127.") || mh.getIp().startsWith("0.")
				|| mh.port < 1 || mh.port > 65535)
			return false;
		return true;
	}

	long lastWrite = System.currentTimeMillis();

	public synchronized void writeHostList() {
		if (lastWrite + BACKUPHOSTTIME > System.currentTimeMillis())
			return;
		System.out.println("Writing host list");
		while (hostList.size() > MAXHOSTLISTSIZE) {
			hostList.removeElementAt(0);
		}
		lastWrite = System.currentTimeMillis();
		Vector v = (Vector) hostList.clone();
		try {
			PrintWriter oo = new PrintWriter(new FileWriter(shareDir + "hosts"));
			for (int a = 0; a < v.size(); a++) {
				MyHost mh = (MyHost) v.elementAt(a);
				if (mh.badRate())
					continue;
				oo.println(mh.toSaveString());
			}
			oo.flush();
			oo.close();
			oo = new PrintWriter(new FileWriter(shareDir + "hosts2"));
			for (int a = 0; a < v.size(); a++) {
				MyHost mh = (MyHost) v.elementAt(a);
				if (mh.badRate())
					continue;
				oo.println(mh.toSaveString());
			}
			oo.flush();
			oo.close();
		} catch (IOException e) {
			System.out.println("MyError: write hostlist file failed " + e);
			System.exit(0);
		}
		// System.out.println("done Writing host list");
	}

	// Vector ping

	public void addHost(String ip, int port, Pong p) {
		if (!validPort(port)) {
			// System.out.println("port filtered on addHost "+port);
			return;
		}
		MyHost mh = new MyHost(ip, port, p);
		if (!validMyHost(mh))
			return;
		Vector v = (Vector) hostList.clone();
		boolean oldfound = false;
		for (int a = 0; a < v.size(); a++) {
			MyHost mh2 = (MyHost) v.elementAt(a);
			if (mh.equals(mh2)) {
				mh2.foundPong(p);
				if (mh2.port != port) {
					// System.out.println("New port found for host
					// "+ip+":"+port);
					mh2.port = port;
				}
				// System.out.println("Old host seen again.");
				hostList.removeElement(mh2);
				hostList.addElement(mh2);
				oldfound = true;
			}
		}
		if (!oldfound) {
			hostList.addElement(mh);
		}
		writeHostList();
	}

	public void removeHost(MyHost mh) {
		hostList.removeElement(mh);
	}

	Pong otherPongSave = null;

	int copyPongCount = 0;

	public void logPong(Pong p) {
		if (otherpong && otherPongSave == null) {
			otherPongSave = p;
		}
		if (copyPongCount > 10) {
			copyPongCount = 0;
			otherPongSave = p;
		}
		copyPongCount++;
		addHost(p.ip, p.port, p);
	}

	public Ping getSeenPing(String desId) {
		for (int a = 0; a < seenPings.size(); a++) {
			Ping p = (Ping) seenPings.elementAt(a);
			if (p.desId.equals(desId))
				return p;
		}
		return null;
	}

	public Query getSeenQuery(String desId) {
		for (int a = 0; a < seenQuery.size(); a++) {
			Query p = (Query) seenQuery.elementAt(a);
			if (p.desId.equals(desId))
				return p;
		}
		return null;
	}

	public QueryHit getSeenQueryHit(String servantid) {
		for (int a = 0; a < seenQueryHit.size(); a++) {
			QueryHit p = (QueryHit) seenQueryHit.elementAt(a);
			if (p.servantid.equals(servantid))
				return p;
		}
		return null;
	}

	public void receiveReply(Header h, GnuProtocol gp) {
		if (h instanceof Pong) {
			gp.myPongCount++;
			if (logToMe)
				System.out
						.println("REPLY: Pong receive in reply to my Ping found");
		} else if (h instanceof QueryHit) {
			if (logToMe)
				System.out
						.println("REPLY: QueryHit receive in reply to my Query");
		} else if (h instanceof Push) {
			if (logToMe)
				System.out
						.println("REPLY: Push received at request froma QueryHit I sent");
		} else {
			gp.dropLoop++;
			// System.out.println("Loop detected: my desc on "+h);
		}
	}

	public void forward(Header h, GnuProtocol gp) {
		Header h2 = h.forward();
		if (myDescriptorId(h.desId)) {
			receiveReply(h, gp);
			return;
		}
		if (alreadySentHeader(h2, gp)) {
			return;
		}
		// System.out.println("Dropping TTL");
		String type = "";
		if (h instanceof Ping) {
			if (logping)
				System.out.println(">>Ping found desId="
						+ aj.io.Encode
								.encodeString(h.desId, "0123456789ABCDEF")
								.substring(17));
			type = "Ping";
			seenPings.addElement(h);
			if (seenPings.size() > MAXSEENPING) {
				// System.out.println("Pruning PingSeen list");
				seenPings.removeElementAt(0);
			}
			Vector v = (Vector) connectedList.clone();
			gp.pingCount++;
			if (logping && logrout)
				System.out.println("forwarding " + type + " to "
						+ (v.size() - 1) + " hosts");
			for (int a = 0; a < v.size(); a++) {
				GnuProtocol gp2 = (GnuProtocol) v.elementAt(a);
				if (gp2.bad) {
					killGnuThread(gp2);
					if (logping && logrout)
						System.out
								.println("  Cannot relay connection down.  Sending "
										+ type
										+ " from "
										+ gp.mh.getIp()
										+ ":"
										+ gp.mh.port
										+ " to "
										+ gp2.mh.getIp()
										+ ":" + gp2.mh.port);
					continue;
				}
				if (!gp2.active)
					continue;
				if (gp2 == gp)
					continue;
				if (logping && logrout)
					System.out.println("  Sending " + type + " from "
							+ gp.mh.getIp() + ":" + gp.mh.port + " to "
							+ gp2.mh.getIp() + ":" + gp2.mh.port);
				sendHeader(h2, gp2);
			}
			return;
		} else if (h instanceof Pong) {
			type = "Pong";
			if (logpong)
				System.out.println(">>Pong found");
			Ping p = getSeenPing(h.desId);
			if (p == null) {
				gp.dropUnseen++;
				if (logpong)
					System.out.println("Unseen ping for pong found "
							+ aj.io.Encode.encodeString(h.desId,
									"0123456789ABCDEF").substring(17));
				return;
			}
			gp.pongCount++;
			Pong PP = (Pong) h;
			if (!validPort(PP.port) && filterPong) {
				gp.filterPort++;
				if (logFilter)
					System.out.println("port filtered on Pong forward "
							+ PP.port);
				return;
			}
			if (!p.gp.bad && p.gp.active) {
				if (logpong && logrout)
					System.out.println("forwarding " + type + " to 1 hosts");
				if (logpong && logrout)
					System.out.println("  Sending " + type + " from "
							+ gp.mh.getIp() + ":" + gp.mh.port + " to "
							+ p.gp.mh.getIp() + ":" + p.gp.mh.port);
				sendHeader(h2, p.gp);
			} else if (!p.gp.bad) {
				if (logpong && logrout)
					System.out
							.println("  Cannot relay connection down.  Sending "
									+ type
									+ " from "
									+ gp.mh.getIp()
									+ ":"
									+ gp.mh.port
									+ " to "
									+ p.gp.mh.getIp()
									+ ":" + p.gp.mh.port);
			}
			return;
		} else if (h instanceof Push) {
			type = "Push";
			Push pp = (Push) h;
			if (logpush)
				System.out.println(">>Push found" + pp);
			QueryHit p = getSeenQueryHit(pp.servantid);
			if (p == null) {
				gp.dropUnseen++;
				if (logpush)
					System.out.println("Unseen QueryHit for Push found");
				return;
			}
			gp.pushCount++;
			Push PP = (Push) h;
			if (!validPort(PP.port) && filterPush) {
				gp.filterPort++;
				if (logFilter)
					System.out.println("port filtered on Push forward "
							+ PP.port);
				return;
			}
			if (!p.gp.bad && p.gp.active) {
				if (logpush && logrout)
					System.out.println("forwarding " + type + " to 1 hosts");
				if (logpush && logrout)
					System.out.println("  Sending " + type + " from "
							+ gp.mh.getIp() + ":" + gp.mh.port + " to "
							+ p.gp.mh.getIp() + ":" + p.gp.mh.port);
				sendHeader(h2, p.gp);
			} else if (!p.gp.bad) {
				if (logpush && logrout)
					System.out
							.println("  Cannot relay connection down.  Sending "
									+ type
									+ " from "
									+ gp.mh.getIp()
									+ ":"
									+ gp.mh.port
									+ " to "
									+ p.gp.mh.getIp()
									+ ":" + p.gp.mh.port);
			}
			return;
		} else if (h instanceof QueryHit) {
			type = "QueryHit";
			Query p = getSeenQuery(h.desId);
			if (logqueryhit)
				System.out.println(">>QueryHit found: " + ((QueryHit) h));
			seenQueryHit.addElement(h);
			if (seenQueryHit.size() > MAXSEENQUERYHIT) {
				// System.out.println("Pruning QueryHit list");
				seenQueryHit.removeElementAt(0);
			}
			if (p == null) {
				gp.dropUnseen++;
				if (logqueryhit)
					System.out.println("Unseen Query for QueryHit found");
				return;
			}
			gp.queryHitCount++;
			QueryHit QQ = (QueryHit) h;
			if (!validPort(QQ.port) && filterHit) {
				gp.filterPort++;
				if (logFilter)
					System.out.println("port filtered on QueryHit forward "
							+ QQ.port);
				return;
			}
			if (!p.gp.bad && p.gp.active) {
				if (logqueryhit && logrout)
					System.out.println("forwarding " + type + " to 1 hosts");
				if (logqueryhit && logrout)
					System.out.println("  Sending " + type + " from "
							+ gp.mh.getIp() + ":" + gp.mh.port + " to "
							+ p.gp.mh.getIp() + ":" + p.gp.mh.port);
				sendHeader(h2, p.gp);
			} else if (!p.gp.bad) {
				if (logqueryhit && logrout)
					System.out
							.println("  Cannot relay connection down.  Sending "
									+ type
									+ " from "
									+ gp.mh.getIp()
									+ ":"
									+ gp.mh.port
									+ " to "
									+ p.gp.mh.getIp()
									+ ":" + p.gp.mh.port);
			}
			return;
		} else if (h instanceof Query) {
			type = "Query";
			seenQuery.addElement(h);
			if (logquery)
				System.out.println(">>Query found: " + ((Query) h));
			if (seenQuery.size() > MAXSEENQUERY) {
				// System.out.println("Pruning QuerySeen list");
				seenQuery.removeElementAt(0);
			}
			gp.queryCount++;
			Vector v = (Vector) connectedList.clone();
			if (logquery && logrout)
				System.out.println("forwarding " + type + " to "
						+ (v.size() - 1) + " hosts");
			for (int a = 0; a < v.size(); a++) {
				GnuProtocol gp2 = (GnuProtocol) v.elementAt(a);
				if (gp2.bad) {
					killGnuThread(gp2);
					if (logquery && logrout)
						System.out
								.println("  Cannot relay connection down.  Sending "
										+ type
										+ " from "
										+ gp.mh.getIp()
										+ ":"
										+ gp.mh.port
										+ " to "
										+ gp2.mh.getIp()
										+ ":" + gp2.mh.port);
					continue;
				}
				if (!gp2.active)
					continue;
				if (gp2 == gp)
					continue;
				if (logquery && logrout)
					System.out.println("  Sending " + type + " from "
							+ gp.mh.getIp() + ":" + gp.mh.port + " to "
							+ gp2.mh.getIp() + ":" + gp2.mh.port);
				sendHeader(h2, gp2);
			}
			return;
		} else {
			type = "Unknown";
			Vector v = (Vector) connectedList.clone();
			if (logunk && logrout)
				System.out.println("forwarding " + type + " to "
						+ (v.size() - 1) + " hosts");
			for (int a = 0; a < v.size(); a++) {
				GnuProtocol gp2 = (GnuProtocol) v.elementAt(a);
				if (gp2.bad) {
					killGnuThread(gp2);
					if (logunk && logrout)
						System.out
								.println("  Cannot relay connection down.  Sending "
										+ type
										+ " from "
										+ gp.mh.getIp()
										+ ":"
										+ gp.mh.port
										+ " to "
										+ gp2.mh.getIp()
										+ ":" + gp2.mh.port);
					continue;
				}
				if (!gp2.active)
					continue;
				if (gp2 == gp)
					continue;
				if (logunk && logrout)
					System.out.println("  Sending " + type + " from "
							+ gp.mh.getIp() + ":" + gp.mh.port + " to "
							+ gp2.mh.getIp() + ":" + gp2.mh.port);
				sendHeader(h2, gp2);
			}
		}

	}

	public void removeConnect(GnuProtocol gp) {
		killGnuThread(gp);
		for (int a = 0; a < seenPings.size(); a++) {
			Ping p = (Ping) seenPings.elementAt(a);
			if (p.gp == gp) {
				seenPings.removeElement(p);
				p.gp = null;
				a--;
			}
		}
		for (int a = 0; a < seenQuery.size(); a++) {
			Query p = (Query) seenQuery.elementAt(a);
			if (p.gp == gp) {
				seenQuery.removeElement(p);
				a--;
			}
		}
		for (int a = 0; a < seenQueryHit.size(); a++) {
			QueryHit p = (QueryHit) seenQueryHit.elementAt(a);
			if (p.gp == gp) {
				seenQueryHit.removeElement(p);
				a--;
			}
		}
	}
}
