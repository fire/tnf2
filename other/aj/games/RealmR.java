package aj.games;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class RealmR   {
	
	public static void displayHelp() {
		System.out.println ("FORMAT: java aj.games.RealmR [options]");
		System.out.println ("-h <host[:port]>   client connect to host ip at port");
		System.out.println ("-s <serverport>  wait for server connect to local port");
		System.out.println ("-n <name>        player name");
		System.exit (0);
	}
	
	public static void main (String s[]) {
		if (s.length == 0) {
			displayHelp();
		}
		Socket nl = null;
		String playername = null;
		for (int a = 0; a < s.length; a++) {
			//System.out.println("NOTE checking option "+s[a]);
			if (s[a].startsWith ("-"))s[a] = s[a].substring (1).trim();
			if (s[a].startsWith ("?")) {
				displayHelp();
			}
			else if (s[a].toUpperCase().startsWith ("S")) {
				s[a] = s[a].substring (1);
				if (s[a].length() == 0 && s.length > a + 1) {
					a++;
				}
				try {
					int port = Integer.parseInt (s[a]);
					System.out.println ("Passive Server mode.  Waiting for server call.");
					ServerSocket ss = new ServerSocket (port);
					Socket sss = ss.accept();
					nl = sss;
				}
				catch (IOException ioe) {
					System.out.println ("MyError: cannot make server at port");
				}
				catch (NumberFormatException nfe) {
					System.out.println ("MyError: bad port in server request");
				}
			}
			else if (s[a].toUpperCase().startsWith ("N")) {
				s[a] = s[a].substring (1);
				if (s[a].length() == 0 && s.length > a + 1) {
					a++;
				}
				playername = s[a];
			}
			else if (s[a].toUpperCase().startsWith ("H")) {
				s[a] = s[a].substring (1);
				if (s[a].length() == 0 && s.length > a + 1) {
					a++;
				}
				try {
					System.out.println ("Active Client mode.  Calling server." + s[a]);
					String host = s[a];
					int port = DEFAULTPORT;
					if (host.indexOf (":") > 0) {
						port = Integer.parseInt (host.substring (host.indexOf (":") + 1));
						host = host.substring (0, host.indexOf (":"));
					}
					nl = new Socket (host, port);
				}
				catch (NumberFormatException nfe) {
					System.out.println ("MyError: bad server port number.");
					System.exit (0);
				}
				catch (IOException ioe) {
					System.out.println ("MyError: cannot connect to server.");
					System.exit (0);
				}
			}
		}
		if (nl == null) {
			System.out.println ("MyError: bad network connection.   Please use ? for help.");
			System.exit (0);
		}
		if (playername == null) {
			System.out.println ("MyError: missing username.   Please use ? for help.");
			System.exit (0);
		}
		new RealmR (nl, playername);
	}
	
	static int DEFAULTPORT = 55320;
	//for server mode
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
	
	public RealmR (final Socket nl, String name) {
		readMap (defaultMap);
		this.name = name;
		this.nl = nl;
		send(nl,"LOGIN " + name);

		new Thread() {
			public void run() {
				BufferedReader br;
				try {
					br = new BufferedReader(new InputStreamReader(nl.getInputStream()));
					while (true) {
						String s=br.readLine();
						if (s==null) break;
						actionPerformed(nl,s);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public void send(Socket s,String line) {
		OutputStream o;
		line+="\n";
		try {
			o = s.getOutputStream();
			o.write(line.getBytes());
			o.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void updateDisplay() {
		int m = (int) (Math.random() * 5);
		if (status == 0)send(nl,"MOVE 0");
		else if (m == 0)send(nl,"MOVE 2");
		else if (m == 1)send(nl,"MOVE 4");
		else if (m == 2)send(nl,"MOVE 6");
		else if (m == 3)send(nl,"MOVE 8");
		else if (m == 4)send(nl,"MOVE 5");
	}
	
	public void actionPerformed (Socket nl,String c) {
		//System.out.println("NOTE received command"+ae);
		
		if (c.trim().equalsIgnoreCase ("connection_closed")) {
			System.out.println ("MyError: Server disconected.");
			System.exit (0);
		}
		else if (c.startsWith ("#")) {
			if (c.toUpperCase().indexOf ("LOGIN FAIL") >= 0) {
				System.out.println ("MyError: User alread logged in.  Login Failed.");
				System.exit (0);
			}
			else {
				System.out.println ("NOTE RECEIVED:" + c.substring (1));
			}
		}
		else if (c.startsWith (RealmS.MAPINTRO)) {
			//note char "M"
			readMap (c);
		}
		else if (c.startsWith (RealmS.USERSINTRO)) {
			//note char "U"
			//long oldtime=System.currentTimeMillis();
			readUsers (c);
			updateDisplay();
			//System.out.println("NOTE Time to update="+(System.currentTimeMillis()-oldtime));
		}
	}
	
	public void readUsers (String s) {
		if (s.startsWith (RealmS.USERSINTRO))s = s.substring (RealmS.USERSINTRO.length()).trim();
		users = new Vector();
		while (s.length() > 0) {
			String cur = s;
			if (s.indexOf (" ") >= 0) {
				cur = cur.substring (0, cur.indexOf (" "));
			}
			s = s.substring (cur.length()).trim();
			RealmU ru = RealmU.parse (cur);
			users.addElement (ru);
			if (ru != null && ru.name.equals (name)) {
				x = ru.x;
				y = ru.y;
				dir = ru.dir;
				status = ru.status;
			}
		}
	}
	
	public void readMap (String s) {
		//MAP X Y DATA
		//System.out.println("NOTE: map received="+s);
		int rows = 0, cols = 0;
		if (s.startsWith (RealmS.MAPINTRO))s = s.substring (RealmS.MAPINTRO.length());
		try {
			if (s.indexOf (" ") >= 0) {
				rows = Integer.parseInt (s.substring (0, s.indexOf (" ")));
				s = s.substring (s.indexOf (" ") + 1);
			}
			if (s.indexOf (" ") >= 0) {
				cols = Integer.parseInt (s.substring (0, s.indexOf (" ")));
				s = s.substring (s.indexOf (" ") + 1);
			}
		}
		catch (NumberFormatException nfe) {
			System.out.println ("MyError: cannot read map");
		}
		map = new char[rows][];
		for (int a = 0; a < rows; a++) {
			map[a] = new char[cols];
			for (int b = 0; b < cols; b++) {
				map[a][b] = s.charAt (a * cols + b);
			}
		}
	}
}

