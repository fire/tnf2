package aj.net;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.CRC32;

import aj.awt.SimpleWindowManager;
import aj.misc.GmlPair;
import aj.misc.Stuff;

public class MyNap implements ActionListener, Runnable {
	long minFileSizeForRateCheck = 10000;

	long maxFileSize = 30000 * 100000L;

	int cp = defaultConnectPort;

	Hashtable shareList = new Hashtable(), requestList = new Hashtable();

	ServerSocket connectServer;

	Vector mySearch = new Vector(); // string of id

	Hashtable relaySearch = new Hashtable(); // id to Socket relationship

	Vector oldSearch = new Vector();

	Vector found = new Vector();

	static int tempPortTimeOut = 10000;

	static int defaultConnectPort = 17382;

	static String shareListFile = "sharelist.gml";

	static Vector connectList = new Vector();

	static double bestRate = 0;

	static boolean firewall = false;

	static TextField bestRateText = new TextField(10);

	static TextField firewallText = new TextField(10);

	static TextField connectListCountText = new TextField(10);

	static TextArea connectListArea = new TextArea(20, 20);

	static Panel statusPanel = new Panel(new BorderLayout());

	static Panel searchPanel = new Panel(new BorderLayout());

	static Panel mainPanel = new Panel(new BorderLayout());

	static Panel panelBar = new Panel(new FlowLayout());

	static List allSearchList = new List(5);

	static List searchResults = new List(20);

	static Button downloadButton = new Button("Download");

	static Button searchButton = new Button("Search");

	static TextField searchKeys = new TextField(8);

	static Frame statusFrame = new Frame("Status Frame");

	static Frame searchFrame = new Frame("Search Frame");

	static Frame shareFrame = new Frame("Share Frame");

	static List shareGuiList = new List();

	static Button unshare = new Button("Unshare");

	static Button addShare = new Button("Add Share");

	static int maxTTL = 5;

	static boolean gui = true;

	public MyNap() {
		cp = defaultConnectPort;
		try {
			connectServer = new ServerSocket(defaultConnectPort);
		} catch (IOException IOE) {
			System.out.println("MyError: cannot oper server socket");
			System.exit(0);
		}
	}

	public MyNap(int dc) {
		cp = dc;
		try {
			connectServer = new ServerSocket(dc);
		} catch (IOException IOE) {
			System.out.println("MyError: cannot oper server socket");
			System.exit(0);
		}
	}

	public void setBestRate(double d) {
		if (bestRate > 10000) {
			return;
		}
		bestRate = d;
		// System.out.println("new bestrate found=" + bestRate + " KBytes/sec");
		bestRateText.setText(bestRate + "");
	}

	public void setFireWall(boolean b) {
		firewall = b;
		firewallText.setText("" + firewall);
	}

	public void setupGui() {
		Panel top = new Panel(new FlowLayout());
		top.add(new Label("Rate", Label.RIGHT));
		top.add(bestRateText);
		top.add(new Label("Firewall", Label.RIGHT));
		top.add(firewallText);
		top.add(new Label("Connected", Label.RIGHT));
		top.add(connectListCountText);
		statusPanel.add("North", top);
		statusPanel.add("Center", connectListArea);

		statusFrame.setLayout(new BorderLayout());
		statusFrame.add("Center", statusPanel);
		statusFrame.pack();
		statusFrame.setVisible(true);
		statusFrame.addWindowListener(new SimpleWindowManager());

		shareFrame.setLayout(new BorderLayout());
		shareFrame.add("Center", shareGuiList);
		Panel ppp = new Panel(new FlowLayout());
		ppp.add(unshare);
		ppp.add(addShare);
		shareFrame.add("South", ppp);
		shareFrame.pack();
		shareFrame.setVisible(true);
		shareFrame.addWindowListener(new SimpleWindowManager());

		searchPanel.add("North", allSearchList);
		searchPanel.add("Center", searchResults);
		Panel sp = new Panel(new FlowLayout());
		sp.add(downloadButton);
		sp.add(searchKeys);
		sp.add(searchButton);
		searchPanel.add("South", sp);

		searchFrame.setLayout(new BorderLayout());
		searchFrame.add("Center", searchPanel);
		searchFrame.pack();
		searchFrame.setVisible(true);
		searchFrame.addWindowListener(new SimpleWindowManager());

		updateGui();

		downloadButton.addActionListener(this);
		searchKeys.addActionListener(this);
		searchButton.addActionListener(this);

		unshare.addActionListener(this);
		addShare.addActionListener(this);

	}

	public void attachTo(String s) {
		if (s == null) {
			System.out.println("MyError: connection requested");
			return;
		}
		if (s.equals("")) {
			System.out.println("MyError: connection requested");
			return;
		}
		String host = s;
		int port = defaultConnectPort;
		if (s.indexOf(":") <= 0) {
			host = s;
		} else {
			host = s.substring(0, s.indexOf(":")).trim();
			String t = s.substring(s.indexOf(":") + 1).trim();
			try {
				port = Integer.parseInt(t);
			} catch (NumberFormatException NFE) {
				System.out.println("MyError: bad port in connection " + t
						+ ", using default");
			}
		}
		try {
			final Socket S = new Socket(host, port);
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					S.getInputStream()));
			new Thread() {
				public void run() {
					while (true) {
						String s;
						try {
							s = br.readLine();
							if (s == null)
								break;
							actionPerformed(S, s);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			addConnection(S);
		} catch (IOException IOE) {
			System.out.println("MyError: unable to connect to " + s);
		}
	}

	public void readShareList() {
		try {
			GmlPair all = GmlPair.parse(new File(shareListFile));
			GmlPair n[] = all.getAllByName("file");
			for (int a = 0; a < n.length; a++) {
				FileData fd = FileData.parse(n[a]);
				if (fd == null) {
					continue;
				}
				shareList.put(fd.shareAs, fd);
			}
			saveShareList();// drop bad shares
			shareGuiList.removeAll();
			for (Enumeration e = shareList.keys(); e.hasMoreElements();) {
				FileData fd = (FileData) shareList.get(e.nextElement());
				shareGuiList.add(fd.toGmlString());
			}
		} catch (IOException IOE) {
			System.out.println("MyError: cannot read sharelist");
		}
	}

	public void saveShareList() {
		try {
			PrintWriter o = new PrintWriter(new FileWriter(shareListFile));
			o.println("sharelist [");
			// System.out.println("rebuilding share list");
			for (Enumeration e = shareList.keys(); e.hasMoreElements();) {
				FileData fd = (FileData) shareList.get(e.nextElement());
				o.println(fd.toGmlString());
				// System.out.println(fd.toGmlString());
			}
			o.println("]");
			o.flush();
			o.close();
		} catch (IOException IOE) {
		}
	}

	public void run() {
		readShareList();
		if (gui)
			setupGui();
		System.out.println("MyNap started. Connect at " + cp);
		while (true) {
			try {
				final Socket c = connectServer.accept();

				final BufferedReader br = new BufferedReader(
						new InputStreamReader(c.getInputStream()));
				new Thread() {
					public void run() {
						while (true) {
							String s;
							try {
								s = br.readLine();
								if (s == null)
									break;
								actionPerformed(c, s);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
				addConnection(c);
			} catch (IOException IOE) {
				System.out
						.println("MyError: connection error in general connect");
			}
		}
	}

	public Socket lookupConnection(String host, int port) {
		try {
			InetAddress ia = InetAddress.getByName(host);
			for (int a = 0; a < connectList.size(); a++) {
				Socket nl = (Socket) connectList.elementAt(a);
				if (nl.isClosed()) {
					removeConnection(nl);
				}
				if (nl.getInetAddress().equals(ia) && nl.getPort() == port) {
					return nl;
				}
			}
			final Socket nl = new Socket(host, port);

			final BufferedReader br = new BufferedReader(new InputStreamReader(
					nl.getInputStream()));
			new Thread() {
				public void run() {
					while (true) {
						String s;
						try {
							s = br.readLine();
							if (s == null)
								break;
							actionPerformed(nl, s);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			addConnection(nl);
			return nl;
		} catch (IOException IOE) {
			System.out.println("MyError: bad host " + host);
			return null;
		}
	}

	public void addConnection(Socket nl) {
		if (connectList.contains(nl))
			return;
		connectList.addElement(nl);
		connectListCountText.setText(connectList.size() + "");
		String c = "";
		for (int a = 0; a < connectList.size(); a++) {
			nl = (Socket) connectList.elementAt(a);
			if (nl.isClosed()) {
				removeConnection(nl);
			} else {
				c += "Host " + nl.getInetAddress() + ":" + nl.getPort() + "\n";
			}
		}
		connectListArea.setText(c);
	}

	public void removeConnection(Socket nl) {
		connectList.removeElement(nl);
		connectListCountText.setText(connectList.size() + "");
		String c = "";
		for (int a = 0; a < connectList.size(); a++) {
			nl = (Socket) connectList.elementAt(a);
			if (nl.isClosed()) {
				removeConnection(nl);
			} else {
				c += "Host " + nl.getInetAddress() + ":" + nl.getPort() + "\n";
			}
		}
		connectListArea.setText(c);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == downloadButton) {
			// file dialoge
			FileDialog fd = new FileDialog(searchFrame, "Save Location",
					FileDialog.SAVE);
			fd.setVisible(true);// ();
			String savename = fd.getDirectory() + fd.getFile();
			if (savename == null)
				return;
			GmlPair gm = null;
			try {
				String st = searchResults.getSelectedItem();
				gm = GmlPair.parse(st);
			} catch (IOException IOE) {
				// System.out.println("MyError: unparsed search request"+st);
				return;
			}
			// look for old Socket
			Socket nl = lookupConnection(gm.getOneByName("host").getString(),
					(int) (gm.getOneByName("port").getDouble()));
			if (nl == null) {
				System.out.println("MyError: cannot connect to host "
						+ gm.getOneByName("host").getString());
				return;
			}
			makeRequest(gm, savename, nl);
		}
		if (ae.getSource() == searchButton || ae.getSource() == searchKeys) {
			doSearch(searchKeys.getText());
		}

	}

	public void actionPerformed(Socket nl, String c) {
		c = c.trim();

		// System.out.println("command found="+c);
		if (nl.isClosed()) {
			removeConnection(nl);
			return;
		}
		if (c.toUpperCase().startsWith("MYNAPERROR ")) {
			System.out.println(c);
		}
		if (c.toUpperCase().startsWith("SEARCH ")) {
			try {
				GmlPair g = GmlPair.parse(c);
				receiveSearch(g, nl);
			} catch (IOException IOE) {
			}
		}
		if (c.toUpperCase().startsWith("FOUND ")) {
			try {
				GmlPair g = GmlPair.parse(c);
				receiveFound(g);
			} catch (IOException IOE) {
			}
		}
		if (c.toUpperCase().startsWith("REQUESTFILE ")) {
			// requestfile name
			// setup up server port to deliever file
			String fileName = c.substring(c.indexOf(" ") + 1).trim();
			FileData fd = (FileData) shareList.get(fileName);
			if (fd == null) {
				send(nl, "MYNAPERROR: No such file " + fileName);
				return;
			}
			int port = oneTimeFileServer(fd.location);
			// reply with "pickup filename port"
			if (port > 0) {
				// System.out.println("file "+fileName+" AKA "+fd.location+"
				// available at port "+port);
				send(nl, "pickup " + fileName + " port:" + port);
			} else {
				send(nl, "MYNAPERROR: No such file " + fileName);
			}
		}
		if (c.toUpperCase().startsWith("SEND ")) {
			// send filename port
			try {
				int port = Integer.parseInt(c.substring(
						c.toUpperCase().indexOf("PORT:") + 5).trim());
				// check remaining pickup list
				// get save file name from pickup list hash
				String fileName = c.substring(c.indexOf(" ") + 1,
						c.lastIndexOf(" ")).trim();
				if (fileName.equals("*")) {
					String ind = "";
					for (Enumeration e = shareList.keys(); e.hasMoreElements();) {
						FileData ffd = (FileData) shareList
								.get(e.nextElement());
						ind += ffd.toString() + "\n";
					}
					File f = File.createTempFile("MyNapTemp", null);
					f.deleteOnExit();
					OutputStream o = new FileOutputStream(f);
					o.write(ind.getBytes());
					o.close();
					transfer(f, new Socket(nl.getInetAddress(), port));
					return;
				}
				FileData fd = (FileData) shareList.get(fileName);
				if (fd == null) {
					send(nl, "MYNAPERROR: No such file " + fileName);
					return;
				}
				transfer(new File(fd.location), new Socket(nl.getInetAddress(),
						port));
			} catch (NumberFormatException NFE) {
				System.out.println("MyError: bad send orders port.");
				send(nl, "MYNAPERROR: BAD PORT IN SEND");
			} catch (IOException IOE) {
				System.out.println("MyError: bad send orders. connection.");
				send(nl, "MYNAPERROR: UNABLE TO OPEN PORT on host "
						+ nl.getInetAddress());
			} catch (Exception E) {
				System.out.println("MyError: bad send orders. bad file name.");
				send(nl, "MYNAPERROR: NOSUCH FILE in send");
			}
		}
		if (c.toUpperCase().startsWith("PICKUP ")) {
			// System.out.println("pickup order "+c);
			// pickup filename port
			try {
				int port = Integer.parseInt(c.substring(
						c.toUpperCase().indexOf("PORT:") + 5).trim());
				// check remaining pickup list
				// get save file name from pickup list hash
				String pickupFileName = c.substring(c.indexOf(" ") + 1,
						c.lastIndexOf(" ")).trim();
				GmlPair foundReply = (GmlPair) requestList.get(pickupFileName);
				String destFileName = foundReply.getOneByName("saveName")
						.getString();
				// remove pickup from request list
				// so as to not accept additional pick
				// requests
				if (destFileName == null) {
					send(nl, "MYNAPERROR: Did not request file "
							+ pickupFileName);
					return;
				}
				transfer(new Socket(nl.getInetAddress(), port), new File(
						destFileName));
				autoshare(foundReply, destFileName);
			} catch (NumberFormatException NFE) {
				System.out.println("MyError: bad pickup orders port." + NFE);
			} catch (IOException IOE) {
				System.out.println("MyError: bad pickup orders. connection."
						+ IOE);
				// check firewall block connect
			} catch (Exception E) {
				System.out.println("MyError: bad pickup orders. bad file name."
						+ E);
			}
		}
	}

	public void autoshare(GmlPair foundReply, String loc) {
		String shareas = foundReply.getOneByName("file").getString();
		String desc = "";
		GmlPair ggg = foundReply.getOneByName("desc");
		if (ggg != null)
			desc = ggg.getString();
		FileData fd = new FileData(shareas, loc, -1, -1, -1, desc);
		if (shareList.containsKey(shareas)) {
			shareas += " copy";
		}
		shareList.put(shareas, fd);
		saveShareList();
		shareGuiList.removeAll();
		for (Enumeration e = shareList.keys(); e.hasMoreElements();) {
			fd = (FileData) shareList.get(e.nextElement());
			shareGuiList.add(fd.toGmlString());
		}
	}

	public void makeRequest(GmlPair foundReply, String saveName, Socket nl) {
		foundReply.add(new GmlPair("saveName", saveName));
		requestList
				.put(foundReply.getOneByName("file").getString(), foundReply);
		send(nl, "REQUESTFILE " + foundReply.getOneByName("file").getString());
	}

	// public void makeFireWallRequest(String requestName, String saveName,
	// Socket nl) {
	// foundReply.add(new GmlPair("saveName",saveName));
	// requestList.put(requestName, foundReply);
	// int port = oneTimeFilePickup(foundReply, requestName);
	// if (port > 0) {
	// nl.send("SEND " + requestName + " port:" + port);
	// }
	// }

	public int oneTimeFileServer(String fileName) {
		try {
			final ServerSocket ss = new ServerSocket(0);
			final String tFileName = fileName;
			ss.setSoTimeout(tempPortTimeOut);
			int rep = ss.getLocalPort();
			new Thread() {
				/**
				 * Main processing method for the MyNap object
				 */
				public void run() {
					try {
						Socket s = ss.accept();
						transfer(new File(tFileName), s);
					} catch (IOException IOE) {
						System.out
								.println("MyError: transfer error at connection");
					}
				}
			}.start();
			return rep;
		} catch (IOException IOE) {
			System.out
					.println("MyError: transfer error. at create one time server");
		}
		return -1;
	}

	// public int oneTimeFilePickup(GmlPair foundReply, String saveFileName) {
	// try {
	// final ServerSocket ss = new ServerSocket(0);
	// final String tFileName = saveFileName;
	// final GmlPair tFoundReply=foundReply;
	// final
	// ss.setSoTimeout(tempPortTimeOut);
	// int rep = ss.getLocalPort();
	// new Thread() {
	// /**
	// * Main processing method for the MyNap object
	// */
	// public void run() {
	// try {
	// Socket s = ss.accept();
	// transfer(s, new File(tFoundReply.getOneByName("file").getString()));
	// autoshare(tFoundReply,tFileName);
	// }
	// catch (IOException IOE) {
	// System.out.println("MyError: transfer error at connection");
	// //firewall warning
	// }
	// }
	// }
	// .start();
	// return rep;
	// }
	// catch (IOException IOE) {
	// System.out.println("MyError: transfer error. at create one time server");
	// }
	// return -1;
	// }

	public void transfer(Socket socket, File file) {
		try {
			transfer(socket.getInputStream(), new FileOutputStream(file), 0,
					maxFileSize);
		} catch (IOException IOE) {
			System.out.println("MyError: transfer error. int socket to file");
		}
	}

	public void transfer(File file, Socket socket) {
		try {
			// System.out.println("file "+file+" transfering via socket");
			transfer(new FileInputStream(file), socket.getOutputStream(), 0,
					30000 * 100000L);
		} catch (IOException IOE) {
			System.out.println("MyError: transfer error. in file to socket="
					+ IOE);
		}
	}

	public void transfer(InputStream i, OutputStream o, long start, long end) {
		if (i == null || o == null) {
			System.out
					.println("MyError: transfer failed.  Unable to open input or output");
		}
		long stime = System.currentTimeMillis();
		long tbyte = 0;
		byte b[] = new byte[100000];
		try {
			if (start > 0) {
				i.skip(start - 1);
			}
			while (true) {
				int c = i.read(b);
				if (c == -1) {
					break;
				}
				if (end < c) {
					o.write(b, 0, (int) end);
					break;
				} else {
					o.write(b, 0, c);
				}
				o.flush();
				Thread.yield();
				end -= c;
				tbyte += c;
			}
			long ttim = System.currentTimeMillis() - stime;
			double rate = 1.0 * tbyte / ttim;
			// System.out.println("rate =" + rate + " KBytes/sec");
			if (tbyte > minFileSizeForRateCheck && bestRate < rate) {
				setBestRate(rate);
			}
			o.flush();
			i.close();
			o.close();
		} catch (IOException IOE) {
			System.out.println("MyError: transfer error in main transfer");
		}
	}

	public void firewallTest() {
		Socket nl = null;
		while (connectList.size() > 0) {
			nl = (Socket) connectList
					.elementAt((int) (connectList.size() * Math.random()));
			if (nl.isClosed()) {
				removeConnection(nl);
			}
		}
		try {
			final ServerSocket ss = new ServerSocket(0);
			ss.setSoTimeout(tempPortTimeOut);
			int rep = ss.getLocalPort();
			new Thread() {
				/**
				 * Main processing method for the MyNap object
				 */
				public void run() {
					try {
						Socket s = ss.accept();
						firewall = false;
						File f = File.createTempFile("MyNapTemp", null);
						f.deleteOnExit();
						transfer(s, f);
					} catch (IOException IOE) {
						firewall = true;
					}
				}
			}.start();
			send(nl, "SEND * port:" + rep);
		} catch (IOException IOE) {
			System.out
					.println("MyError: transfer error. at create one time server");
		}
	}

	public void updateGui() {
		connectListCountText.setText("" + connectList.size());
		bestRateText.setText("" + bestRate);
		firewallText.setText("" + firewall);
		String c = "";
		for (int a = 0; a < connectList.size(); a++) {
			Socket nl = (Socket) connectList.elementAt(a);
			if (nl.isClosed()) {
				removeConnection(nl);
			} else {
				c += "Host " + nl.getInetAddress() + ":" + nl.getPort() + "\n";
			}
		}
		connectListArea.setText(c);
	}

	public void receiveFound(GmlPair g) {
		String id = g.getAllByName("id")[0].getString();
		if (mySearch.contains(id)) {
			for (int a = 0; a < found.size(); a++) {
				GmlPair tt = (GmlPair) found.elementAt(a);
				GmlPair c1 = tt.getOneByName("crc"), c2 = g.getOneByName("crc");
				if (c1 != null && c2 != null) {
					if (c1.getString().trim().equalsIgnoreCase(
							c2.getString().trim()))
						return;
				}
			}
			// check share list for current copy
			long crc = (long) g.getOneByName("crc").getDouble();
			for (Enumeration e = shareList.keys(); e.hasMoreElements();) {
				FileData fd = (FileData) shareList.get(e.nextElement());
				if (fd.crc == crc) {
					// System.out.println("CRC duplicat file found in search
					// "+g);
					return;
				}
			}

			found.addElement(g);
			searchResults.removeAll();
			for (int a = 0; a < found.size(); a++) {
				g = (GmlPair) found.elementAt(a);
				searchResults.add(g.toString());
			}
			return;
		}
		Socket nl = (Socket) relaySearch.get(id);
		if (nl == null) {
			return;
		} else {
			send(nl, g.toString());
		}
	}

	public void receiveSearch(GmlPair g, Socket nl) {
		// System.out.println("Search received");
		String id = g.getAllByName("id")[0].getString();
		if (mySearch.contains(id)) {
			// System.out.println("ignore my own search received");
			return;
		}
		// skip my search self
		if (relaySearch.get(id) != null) {
			// System.out.println("ignore already relayed search received");
			return;
		}
		// skip already relayed search
		// do internal seach and reply with found
		// System.out.println("checking locally");
		localSearch(g, nl);

		// check time to live
		int ttl = (int) (g.getAllByName("ttl")[0].getDouble());
		if (ttl < 1 || ttl > maxTTL) {
			// System.out.println("too old don't realy");
			return;
		}
		// System.out.println("relaying");
		relaySearch(g, nl);
	}

	public void localSearch(GmlPair g, Socket nl) {
		String k = g.getAllByName("key")[0].getString().toUpperCase().trim();
		if (k.equals("")) {
			return;
		}
		// System.out.println("local search for "+k);
		String keys[] = Stuff.getTokens(k);
		if (keys.length == 0) {
			return;
		}
		for (int a = 0; a < keys.length; a++) {
			// System.out.println("key "+a+":"+keys[a]);
		}
		for (Enumeration e = shareList.keys(); e.hasMoreElements();) {
			boolean match = true;
			FileData fd = (FileData) shareList.get(e.nextElement());
			// System.out.println("checking file "+fd.shareAs);
			for (int a = 0; a < keys.length; a++) {
				if (fd.shareAs.toUpperCase().indexOf(keys[a]) < 0) {
					match = false;
				}
			}
			if (match) {
				sendFound(g, fd, nl);
			}
		}
	}

	public void sendFound(GmlPair G, FileData fd, Socket nl) {
		Vector v = new Vector();
		GmlPair g = new GmlPair("id", G.getAllByName("id")[0].getString());
		v.addElement(g);
		g = new GmlPair("file", fd.shareAs);
		v.addElement(g);
		g = new GmlPair("crc", fd.crc + "");
		v.addElement(g);
		g = new GmlPair("size", fd.size + "");
		v.addElement(g);
		g = new GmlPair("date", fd.date + "");
		v.addElement(g);
		g = new GmlPair("host", nl.getLocalAddress().getHostAddress());
		v.addElement(g);
		g = new GmlPair("port", cp + "");
		v.addElement(g);
		if (fd.desc.length() > 0) {
			g = new GmlPair("desc", fd.desc);
			v.addElement(g);
		}
		g = new GmlPair("found", v);
		send(nl, g.toString());
		// found [ id searchhash file name crc num size num date num host name
		// port num ]
	}

	public void send(Socket S, String line) {
		OutputStream o;
		try {
			o = S.getOutputStream();
			line += "\n";
			o.write(line.getBytes());
			o.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doSearch(String key) {
		while (key.indexOf("\"") >= 0) {
			key = key.substring(0, key.indexOf("\""))
					+ key.substring(key.indexOf("\"") + 1);
		}
		Vector v = new Vector();
		String id = (connectServer.getInetAddress().getHostAddress() + ":" + cp + new Date())
				.hashCode()
				+ "";
		GmlPair g = new GmlPair("id", id);
		v.addElement(g);
		g = new GmlPair("ttl", maxTTL + "");
		v.addElement(g);
		g = new GmlPair("key", key);
		v.addElement(g);
		g = new GmlPair("search", v);
		// System.out.println("search created = "+g);
		mySearch.addElement(id);
		sendToAll(g.toString());
	}

	public void relaySearch(GmlPair g, Socket nl) {
		String id = g.getAllByName("id")[0].getString();
		relaySearch.put(id, nl);
		GmlPair m = g.getAllByName("ttl")[0];
		m.setValue("" + (int) (m.getDouble() - 1));
		sendToAll(g.toString());
	}

	public void sendToAll(String s) {
		for (int a = 0; a < connectList.size(); a++) {
			Socket nl = (Socket) connectList.elementAt(a);
			send(nl, s);
		}
	}

	public static void main(String s[]) {
		System.out
				.println("Format: java aj.net.MyNap [server:port] [NOGUI] [<remotehost>:port]*");
		MyNap mn;
		int port = defaultConnectPort;
		for (int a = 0; a < s.length; a++) {
			if (s[a].toUpperCase().startsWith("NOGUI"))
				MyNap.gui = false;
			if (s[a].toUpperCase().startsWith("SERVER:")) {
				try {
					port = Integer.parseInt(s[a].substring(7).trim());
				} catch (NumberFormatException NFE) {
					System.out.println("MyError: bad port number");
				}
			}
		}
		mn = new MyNap(port);
		new Thread(mn).start();
		for (int a = 0; a < s.length; a++) {
			if (!s[a].toUpperCase().startsWith("SERVER:")
					&& !s[a].toUpperCase().startsWith("NOGUI")) {
				mn.attachTo(s[a]);
			}
		}
	}

}

class FileData {
	String shareAs = null, location = null;

	String desc = "";

	long date = -1, crc = -1, size = -1;

	public String fileForm(String f) {
		if (f == null)
			return "";
		while (f.indexOf("\\") >= 0)
			f = f.substring(0, f.indexOf("\\")) + "/"
					+ f.substring(f.indexOf("\\") + 1);
		return f;
	}

	public boolean isValid() {
		return crc != -1 && size != -1 && date != -1 && !shareAs.equals("")
				&& !location.equals("");
	}

	public FileData(String sa, String lo, long d, long c, long s, String dec) {
		shareAs = fileForm(sa);
		location = fileForm(lo);
		date = d = -1;
		crc = c = -1;
		size = s = -1;
		desc = fileForm(dec);
		while (desc.indexOf("\"") >= 0) {
			desc = desc.substring(0, desc.indexOf("\""))
					+ desc.substring(desc.indexOf("\"") + 1);
		}
		if (date == -1) {
			date = new File(location).lastModified();
		}
		if (date != new File(location).lastModified()) {
			size = new File(location).length();
			try {
				crc = getCRC(new FileInputStream(location));
			} catch (IOException IOE) {
			}
		}
		if (size == -1) {
			size = new File(location).length();
		}
		if (crc == -1) {
			try {
				crc = getCRC(new FileInputStream(location));
			} catch (IOException IOE) {
			}
		}

		// System.out.println(toGmlString());
	}

	public long getCRC(InputStream i) {
		try {
			CRC32 c = new CRC32();
			byte b[] = new byte[100000];
			while (true) {
				int t = i.read(b);
				if (t == -1) {
					i.close();
					return c.getValue();
				}
				c.update(b, 0, t);
			}
		} catch (IOException IOE) {
			return 0;
		}
	}

	public String toString() {
		return shareAs
				+ " "
				+ (size > 1000000 ? size / 1000000 + "M" : (size > 1000 ? size
						/ 1000 + "K" : size + "B")) + " CRC:" + crc + " "
				+ new Date(date) + " " + desc;
	}

	public String toGmlString() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("shareas", shareAs);
		v.addElement(g);
		g = new GmlPair("location", location);
		v.addElement(g);
		g = new GmlPair("date", date + "");
		v.addElement(g);
		g = new GmlPair("size", size + "");
		v.addElement(g);
		g = new GmlPair("crc", crc + "");
		v.addElement(g);
		if (desc.length() > 0) {
			g = new GmlPair("desc", desc);
			v.addElement(g);
		}
		g = new GmlPair("file", v);
		return g.toString();
	}

	public static FileData parse(GmlPair g) {
		String sa;
		String lo;
		String de;
		long d;
		long c;
		long s;
		GmlPair n[] = g.getAllByName("shareas");
		if (n.length > 0) {
			sa = n[0].getString();
		} else {
			return null;
		}
		n = g.getAllByName("size");
		if (n.length > 0) {
			s = (long) n[0].getDouble();
		} else {
			s = -1;
		}
		n = g.getAllByName("location");
		if (n.length > 0) {
			lo = n[0].getString();
		} else {
			return null;
		}
		n = g.getAllByName("crc");
		if (n.length > 0) {
			c = (long) n[0].getDouble();
		} else {
			c = -1;
		}
		n = g.getAllByName("desc");
		if (n.length > 0) {
			de = n[0].getString();
		} else {
			de = "";
		}
		n = g.getAllByName("date");
		if (n.length > 0) {
			d = (long) n[0].getDouble();
		} else {
			d = -1;
		}
		FileData fd = new FileData(sa, lo, d, c, s, de);
		if (fd.isValid())
			return fd;
		else
			return null;
	}

}

/*
 * search [ id num key word ttl num ] found [ id num name file ] MYNAPERROR
 * SEARCHFOR [ key word1 key word2 key word3] SEARCHFOR [ key word1 size
 * "1000000-" ] SEARCHFOR [ key word1 size "1000000+" ] SEARCHFOR [ key word1
 * size "1000000+" size "2000000-" ] SEARCHFOR [ crc "3525235252" ] REQUESTFILE [
 * name sharename ] SEND [ name sharename port 5235 ] PICKUP [ name sharename
 * port 5352 ] Vector searchResults foundstring Socket
 */

/*
 * GUI sharelist - what I am sharing traffic list - searches and founds relaying
 * (string = 1000 lines max constant string) search results - founds to me
 * (store 10,000 - searches start here) (search or newSearch) transfer list -
 * uploads and downloads in progress (sending %done) (receiveing %done) commands -
 * start search request download active hosts all hosts search request name host
 * search [ id hash ttl 5 key word ] new search [ id oldhash ttl 4 key word ]
 * receive search - check ttl - drop check if id already serviced - drop do
 * internal seach and reply with found save to relay founds relay to all
 * attached hosts new search found [ id searchhash file name crc num size num
 * desc string host name port num ] receive found - if (mysearch) { save result }
 * check if relay found relay to source
 */

/*
 * GUI search ( )
 * 
 * search lists file lists select (send or request)
 * 
 * transfer status sending file %d receiving file %d giving file %d takingfile
 * %d
 * 
 */

/*
 * 
 * class multirequest multirequest(foundReply) { get all CRC that match GmlPair
 * allFound[] long fsize long tsize=fsize/allFound.length+1; long
 * fsize=fsize%tsize; partialRequest pq[]=new partialrequest[allFound.length];
 * for(int a=0;a<allFound;a++) { pq[a]=new
 * partialRequest(,tsize*a,tsize*(a+1),this) }
 * 
 * public void actionPerformed(ActionEvent ae) { if (complete) { verify file
 * length. pq.done==true; } if (error) pq[a]=new Partialrequest(fn,tsize*a, . .. )
 * 
 * if (all complete) { rebuildFinal FileOutputStream fo for (int a=0;a<pq.length;a++) {
 * transalldata(partFail[a],fo); delfile(partfile[a]); } } }
 *  }
 * 
 * 
 * class partialrequest
 * 
 */
