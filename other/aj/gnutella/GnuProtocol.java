package aj.gnutella;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import aj.misc.Stuff;

public class GnuProtocol implements Runnable {

	int MINPINGDELAY = 15 * 1000;// variable and coutns up

	static double MAXOUTBANDWIDTH = 1.5;

	static int MAXPINGDELAY = 10 * 60 * 1000;

	static int MAXTIMEOUT = 15 * 1000;// connect timeout forced

	static int TRAFFICTIME = 2 * 1000;// time between calcuating bandwidth

	static int MAXPAYLOAD = 51000;// block huge payload

	static int TTL = 7;// time between calcuating bandwidth

	Vector knownPings;

	Vector knownSearches;

	Vector knownHosts;

	String connectVersion = "";

	int totalTraffic = 0;

	double totalTrafficRate = 0;

	long lastTrafficTime = 0;

	int lastTraffic = 0;

	double lastTrafficRate = 0;

	int outMessages = 0;

	int inMessages = 0;

	int totalOutTraffic = 0;

	double totalOutTrafficRate = 0;

	long lastOutTrafficTime = 0;

	int lastOutTraffic = 0;

	double lastOutTrafficRate = 0;

	boolean active = false;

	long connectTime = 0;

	long tryconnectTime = 0;

	MyHost mh;

	String ip;

	int port;

	Socket s = null;

	InputStream ii = null;

	OutputStream oo = null;

	MyGnutella mg;

	int pingCount = 0, pongCount = 0, queryCount = 0;

	int pushCount = 0, queryHitCount = 0, unknownCount = 0;

	int myPongCount = 0, myQueryHitCount = 0, myPushCount = 0;

	int dropTTL = 0, dropLoop = 0, dropUnseen = 0, dropSentBefore = 0,
			dropBand = 0;

	int filterPort = 0;

	boolean bad = false;

	byte pongSend[] = new byte[36];

	int lastMyPongCount = 0;

	long lastPingTime = 0;

	public void countOutTraffic(int x) {
		totalOutTraffic += x;
		lastOutTraffic += x;
		long currtime = System.currentTimeMillis();
		if (lastOutTrafficTime + TRAFFICTIME < currtime) {
			long elapstime = currtime - lastOutTrafficTime;
			lastOutTrafficRate = 1.0 * lastOutTraffic / elapstime;
			lastOutTraffic = x;
			lastOutTrafficTime = currtime;
		}
		long elapstime = currtime - connectTime;
		totalOutTrafficRate = 1.0 * totalOutTraffic / elapstime;
	}

	public double getOutBandwidth() {
		long elapstime = System.currentTimeMillis() - lastOutTrafficTime;
		return 1.0 * lastOutTraffic / elapstime;
	}

	public void countTraffic(int x) {
		totalTraffic += x;
		lastTraffic += x;
		long currtime = System.currentTimeMillis();
		if (lastTrafficTime + TRAFFICTIME < currtime) {
			long elapstime = currtime - lastTrafficTime;
			lastTrafficRate = 1.0 * lastTraffic / elapstime;
			lastTraffic = x;
			lastTrafficTime = currtime;
		}
		long elapstime = currtime - connectTime;
		totalTrafficRate = 1.0 * totalTraffic / elapstime;
	}

	public double getInBandwidth() {
		long elapstime = System.currentTimeMillis() - lastTrafficTime;
		return 1.0 * lastTraffic / elapstime;
	}

	public void sendPing() {
		if (lastPingTime + MINPINGDELAY >= System.currentTimeMillis())
			return;
		recoverCount = 0;
		lastPingTime = System.currentTimeMillis();
		lastMyPongCount = myPongCount;
		MINPINGDELAY = Math.min(MAXPINGDELAY, MINPINGDELAY * 3 / 2);
		myPongCount = 0;
		byte pingSend[] = new byte[23];
		String myid = mg.getDescriptorId();
		for (int a = 0; a < 16; a++)
			pingSend[a] = (byte) myid.charAt(a);
		pingSend[16] = 0;// payload descriptor or type
		pingSend[17] = (byte) TTL;// ttl
		pingSend[18] = 0;// hops
		pingSend[19] = 0;
		pingSend[20] = 0;
		pingSend[21] = 0;
		pingSend[22] = 0;
		try {
			oo.write(pingSend);
			outMessages++;
			oo.flush();
			countOutTraffic(pingSend.length);
		} catch (IOException ioe) {
			// System.out.println("MyError: cannot send ping close connection
			// "+ioe);
			killSelf(true);
		}
	}

	// HERE
	public void sendQueryHit(String servantId, Vector fileInfo) {
		outMessages++;
	}

	public void sendPong(String desId) {
		if (MyGnutella.NOPONG)
			return;
		byte pongSend[] = new byte[23];
		for (int a = 0; a < 16; a++)
			pongSend[a] = (byte) desId.charAt(a);
		pongSend[16] = 1;// payload descriptor type
		pongSend[17] = 7;// ttl
		pongSend[18] = 0;// hops
		pongSend[19] = 14;
		pongSend[20] = 0;
		pongSend[21] = 0;
		pongSend[22] = 0;
		byte pongPayload[] = new byte[14];
		String tt[] = aj.misc.Stuff.getTokens(MyGnutella.myip, ".");
		// int port;//2 bytes
		pongPayload[0] = (byte) (MyGnutella.serverport % 256);
		pongPayload[1] = (byte) (MyGnutella.serverport / 256);
		// String ip;//4 bytes
		pongPayload[2] = (byte) Integer.parseInt(tt[0]);
		pongPayload[3] = (byte) Integer.parseInt(tt[1]);
		pongPayload[4] = (byte) Integer.parseInt(tt[2]);
		pongPayload[5] = (byte) Integer.parseInt(tt[3]);
		// int numshareFiles;//4 bytes
		int numshareFiles = mg.numshareFiles;
		int numshareKilobytes = mg.numshareKilobytes;
		if (MyGnutella.LIESHARE) {
			numshareFiles = 93;
			numshareKilobytes = 115803287;
		}
		pongPayload[6] = (byte) (numshareFiles % 256);
		pongPayload[7] = (byte) ((numshareFiles / 256) % 256);
		pongPayload[8] = (byte) (((numshareFiles / 256 / 256) % 256));
		pongPayload[9] = (byte) (((numshareFiles / 256 / 256 / 256)));
		// int numshareKilobytes;//4 bytes in kbytes
		pongPayload[10] = (byte) (numshareKilobytes % 256);
		pongPayload[11] = (byte) ((numshareKilobytes / 256) % 256);
		pongPayload[12] = (byte) (((numshareKilobytes / 256 / 256) % 256));
		pongPayload[13] = (byte) (((numshareKilobytes / 256 / 256 / 256)));
		// System.out.println("Sending Ping/Pong reply to
		// "+s.getRemoteSocketAddress().toString());
		if (MyGnutella.otherpong) {
			if (mg.otherPongSave == null)
				return;// dont send one, don't have one yet
			pongPayload = mg.otherPongSave.payload;
		}
		try {
			oo.write(pongSend);
			oo.write(pongPayload);
			outMessages++;
			oo.flush();
			countOutTraffic(pongSend.length);
			countOutTraffic(pongPayload.length);
		} catch (IOException ioe) {
			// System.out.println("MyError: cannot send pong close connection
			// "+ioe);
			killSelf(true);
		}
	}

	boolean isServer = false;

	public GnuProtocol(Socket s, MyGnutella mg) {
		this.s = s;
		this.mg = mg;
		mh = new MyHost(s.getInetAddress().toString(), s.getPort(), null);
		this.ip = mh.getIp();
		this.port = mh.port;
		isServer = true;
	}

	public GnuProtocol(MyHost mh, MyGnutella mg) {
		this.mh = mh;
		this.ip = mh.getIp();
		this.port = mh.port;
		this.mg = mg;
		isServer = false;
	}

	public void send(Header h) {
		if (h != null && !bad && oo != null) {
			if (getOutBandwidth() > MAXOUTBANDWIDTH) {
				if (h instanceof Query || h instanceof Ping) {
					dropBand++;
					return;
				}
			}
			if (h.ttl < 1) {
				dropTTL++;
				return;
			}
			try {
				oo.write(h.b);
				oo.write(h.payload);
				outMessages++;
				oo.flush();
				countOutTraffic(h.b.length);
				countOutTraffic(h.payload.length);
			} catch (IOException e) {
				System.out.println("MyError: Unable to write " + ip + ":"
						+ port + " connection closed " + e);
				killSelf(true);
			}
		} else if (oo == null) {
			killSelf(true);
		}
	}

	/*
	 * CLIENT sends 0.6 GNUTELLA CONNECT/0.6<cr><lf> User-Agent: BearShare<cr><lf>
	 * <cr><lf>
	 * 
	 * SERVER sends 0.6 GNUTELLA/0.6 200 OK<cr><lf> User-Agent: BearShare<cr><lf>
	 * <cr><lf>
	 * 
	 * client sends GNUTELLA/0.6 200 OK<cr><lf> <cr><lf>
	 */

	/*
	 * client sends 0.4 GNUTELLA CONNECT/0.4<cr><lf> <cr><lf>
	 * 
	 * server sends 0.6 GNUTELLA OK<cr><lf>
	 * 
	 */
	public void connectServer() {
		if (MyGnutella.logconnect)
			System.out.println("Try connect v0.6 from " + ip + ":" + port);
		try {
			tryconnectTime = connectTime = System.currentTimeMillis();
			ii = s.getInputStream();
			oo = s.getOutputStream();
			if (ii == null || oo == null) {
				killSelf(false);
				return;
			}
			Vector tt = new Vector();
			mh.goodConnect();
			active = true;
			double ver = 0.4;
			while (true) {
				String t = myReadLine(ii);
				if (t == null) {
					killSelf(false);
					return;
				}
				if (t.trim().equalsIgnoreCase(""))
					break;
				tt.addElement(t);
				countTraffic(t.length());
			}
			if (tt.size() > 0) {
				String log = ((String) tt.elementAt(0)).trim();
				if (!log.toUpperCase().startsWith("GNUTELLA CONNECT/")) {
					oo
							.write("GNUTELLA/0.6 400 UNKNOWN PROTOCOL\n\n"
									.getBytes());
					killSelf(true);
					return;
				}
				log = log.substring("GNUTELLA CONNECT/".length());
				try {
					ver = Double.parseDouble(log);
					connectVersion = log;
				} catch (NumberFormatException nfe) {
					oo
							.write("GNUTELLA/0.6 400 UNKNOWN PROTOCOL\n\n"
									.getBytes());
					killSelf(true);
					return;
				}

			} else {
				killSelf(false);
				return;
			}
			if (ver == .4) {
				oo.write("GNUTELLA OK\n\n".getBytes());
			} else {
				oo.write("GNUTELLA/0.6 200 OK\n".getBytes());
				oo.write("User-Agent: MyGnutella\n\n".getBytes());
				oo.flush();
				while (true) {
					String t = myReadLine(ii);
					if (t == null) {
						killSelf(false);
						return;
					}
					if (t.trim().toUpperCase().startsWith("GNUTELLA/")
							&& t.trim().endsWith("200 OK")) {
					}
					if (t.trim().equals(""))
						return;
					tt.addElement(t);
				}
			}
		} catch (IOException ioe) {
			killSelf(false);
		}
	}

	public void connectClientv6() {
		if (MyGnutella.logconnect)
			System.out.println("Try connect v0.6 to " + ip + ":" + port);
		try {
			tryconnectTime = System.currentTimeMillis();
			s = new Socket(ip, port);

			ii = s.getInputStream();
			oo = s.getOutputStream();
			if (ii == null || oo == null) {
				killSelf(false);
				return;
			}
			oo.write("GNUTELLA CONNECT/0.6\n".getBytes());
			oo.write("User-Agent: MyGnutella\n\n".getBytes());
			oo.flush();
			Vector tt = new Vector();
			while (true) {
				String t = myReadLine(ii);
				active = true;
				if (t == null) {
					killSelf(false);
					return;
				}
				if (t.trim().equals(""))
					break;
				countTraffic(t.length());
				tt.addElement(t.trim());
			}
			if (tt.size() < 1) {
				killSelf(false);
				return;
			}
			String log = (String) tt.elementAt(0);
			if (log.toUpperCase().trim().startsWith("GNUTELLA/")
					&& log.toUpperCase().trim().endsWith("200 OK")) {
				connectVersion = log.trim().substring("GNUTELLA/".length());
				connectVersion = connectVersion.substring(0,
						connectVersion.indexOf(" OK")).trim();
				oo.write("GNUTELLA/0.6 200 OK\n\n".getBytes());
				oo.flush();
				sendPing();
				mh.goodConnect();
				mg.writeHostList();
				if (MyGnutella.logconnect)
					System.out.println("Good v0.6 Connect to " + ip + ":"
							+ port + " total=" + mg.connectedList.size());
			} else if (log.toUpperCase().trim().equals("GNUTELLA OK")) {
				connectVersion = "0.4b";
				sendPing();
				mh.goodConnect();
				mg.writeHostList();
				if (MyGnutella.logconnect)
					System.out.println("Lazy v0.4 Connect to " + ip + ":"
							+ port + " total=" + mg.connectedList.size());
			} else if (log.indexOf("HTTP/") >= 0) {
				killSelf(false);
				mg.removeHost(mh);
			} else {
				if (MyGnutella.logconnect)
					System.out.println("Bad v0.6 Connect to " + ip + ":" + port
							+ " reply of " + log);
				s.close();
				connectClientv4();
				return;
			}
		} catch (IOException ioe) {
			// System.out.println("MyError: IOError in connection. Bad Connect
			// to "+ip+":"+port+" dropping: "+ioe);
			killSelf(false);
		}
	}

	/*
	 * client sends GNUTELLA CONNECT/0.4<lf><lf>
	 * 
	 * server sends GNUTELLA OK
	 */
	public void connectClientv4() {
		if (MyGnutella.logconnect)
			System.out.println("Try connect v0.4 to " + ip + ":" + port);
		try {
			tryconnectTime = System.currentTimeMillis();
			s = new Socket(ip, port);
			ii = s.getInputStream();
			oo = s.getOutputStream();
			if (ii == null || oo == null) {
				killSelf(false);
				return;
			}
			oo.write("GNUTELLA CONNECT/0.4\n\n".getBytes());
			oo.flush();
			byte b[] = new byte[13];
			int check = ii.read(b);
			while (check != b.length && check > 0) {
				check += ii.read(b, check, b.length - check);
				// System.out.println("WARNING read mismatch "+b.length+" !=
				// "+check);
			}
			countTraffic(b.length);
			active = true;
			if (!new String(b).toUpperCase().trim().equals("GNUTELLA OK")) {
				killSelf(true);
				return;
			} else {
				connectVersion = "0.4";
				sendPing();
				if (ii != null && oo != null) {
					mh.goodConnect();
					mg.writeHostList();
				} else {
					killSelf(true);
				}
				if (MyGnutella.logconnect)
					System.out.println("Good Connect to " + ip + ":" + port
							+ " total=" + mg.connectedList.size());
			}
		} catch (IOException ioe) {
			killSelf(false);
		}
	}

	public void startProtocol() {
		if (isServer) {
			connectServer();
		} else {
			connectClientv6();
		}
	}

	public void killSelf(boolean lost) {
		if (!bad) {
			if (MyGnutella.logconnect && lost)
				System.out.println("Connection lost " + ip + ":" + port);
			else if (MyGnutella.logconnect)
				System.out.println("Connection failed " + ip + ":" + port);
			bad = true;
			if (!lost) {
				mh.failedConnect();
			} else {
				mh.connectLost();
			}
			if (s != null) {
				try {
					s.close();
					s = null;
					if (oo != null) {
						oo.close();
					}
					if (ii != null) {
						ii.close();
					}
				} catch (IOException ioe) {
				}
			}
			mg.removeConnect(this);
			mg.writeHostList();
		}
	}

	public void checkTimeOut() {
		if (connectTime == 0
				&& System.currentTimeMillis() - tryconnectTime > MAXTIMEOUT) {
			killSelf(false);
		}
	}

	public void run() {
		startProtocol();
		if (bad)
			return;
		mh.goodConnect();
		try {
			while (!bad) {
				byte b[] = new byte[23];
				int check = ii.read(b);
				while (check != b.length && check > 0) {
					check += ii.read(b, check, b.length - check);
					// System.out.println("WARNING read mismatch "+b.length+" !=
					// "+check);
				}
				countTraffic(b.length);
				active = true;
				int l = (b[19] < 0 ? 256 + b[19] : b[19])
						+ (b[20] < 0 ? 256 + b[20] : b[20]) * 256
						+ (b[21] < 0 ? 256 + b[21] : b[21]) * 256 * 256
						+ (b[22] < 0 ? 256 + b[22] : b[22]) * 256 * 256 * 256;
				if (l < 0 || l > MAXPAYLOAD) {// avoid hug payload memory
												// leaks
					String type = "UNK";
					if (b[16] == 0x00)
						type = "PING";
					if (b[16] == 0x01)
						type = "PONG";
					if (b[16] == 0x40)
						type = "PUSH";
					if (b[16] == 0x80)
						type = "QUERY";
					if (b[16] == 0x81)
						type = "QUERYHIT";
					System.out.println("WARNING : Bad payload length "
							+ type
							+ " found "
							+ l
							+ " "
							+ b[19]
							+ " "
							+ b[20]
							+ " "
							+ b[21]
							+ " "
							+ b[22]
							+ " on "
							+ ip
							+ ":"
							+ port
							+ " b=0x"
							+ aj.io.Encode.encodeString(new String(b),
									"0123456789ABCDEF").substring(17));

					recover(ii);// try recover
					// killSelf(true);return;
					continue;
				}
				byte payload[] = new byte[l];
				check = ii.read(payload);
				while (check != payload.length && check > 0) {
					check += ii.read(payload, check, payload.length - check);
					// System.out.println("WARNING read mismatch
					// "+payload.length+" != "+check);
				}
				inMessages++;
				countTraffic(payload.length);
				if (b[16] == 0x00 && payload.length == 0) {// ping
					if (payload.length != 0) {
						System.out.println("Improper payload in Ping");
						recover(ii);// try recover
						// killSelf(true);return;
						continue;
					}
					Ping p = new Ping(b, payload, this);
					sendPong(p.desId);
					mg.forward(p, this);
				} else if (b[16] == 0x01) {// pong
					if (payload.length != 14) {
						System.out.println("Improper payload in Pong");
						recover(ii);// try recover
						// killSelf(true);return;
						continue;
					}
					Pong p = new Pong(b, payload, this);
					mg.logPong(p);
					// System.out.println("Pong ="+p.toHex());
					mg.forward(p, this);
				} else if (b[16] == 0x40) {// push
					if (payload.length != 26) {
						System.out.println("Improper payload in Push");
						recover(ii);// try recover
						// killSelf(true);return;
						continue;
					}
					Push p = new Push(b, payload);
					mg.forward(p, this);
				} else if (b[16] == (byte) 0x80) {// query
					Query p = new Query(b, payload, this);
					mg.forward(p, this);
				} else if (b[16] == (byte) 0x81) {// queryhit
					QueryHit p = new QueryHit(b, payload, this);
					mg.forward(p, this);
				} else {
					unknownCount++;
					String pay = new String(payload);
					if (pay.length() > 80)
						pay = pay.substring(80) + "...trunc";
					System.out.println("MyError: Unknown type found: b=0x"
							+ aj.io.Encode.encodeString(new String(b),
									"0123456789ABCDEF").substring(17)
							+ " payload=0x"
							+ aj.io.Encode.encodeString(new String(payload),
									"0123456789ABCDEF").substring(17));
					recover(ii);// try recover
				}
			}
		} catch (IOException ioe) {
			System.out.println("MyError: Unable to read " + ip + ":" + port
					+ " protocol lost " + ioe);
			killSelf(true);
		}
	}

	public String toString() {
		String res = ""
				+ myTime((System.currentTimeMillis() - connectTime) / 1000);
		if (!active || bad) {
			if (tryconnectTime == 0)
				res = "0";
			else
				res = myTime((System.currentTimeMillis() - tryconnectTime) / 1000);
		}
		if (isServer)
			res += " Called...";
		else if (active && !bad)
			res += " Active..v" + connectVersion;
		else if (!bad)
			res += " Waiting.";
		else
			res += " Failed..";
		res += " " + ip + ":" + port;
		if (active && !bad) {
			res += " ping:" + pingCount + " pong:" + pongCount;
			if (myPongCount > 0 || lastMyPongCount > 0) {
				res += "(" + Math.max(myPongCount, lastMyPongCount) + ")";
			}
			res += " query:" + queryCount + " qhit:" + queryHitCount + " push:"
					+ pushCount;
			res += " unk:" + unknownCount;
			res += " drop (ttl:" + dropTTL + " lp:" + dropLoop + " un:"
					+ dropUnseen + " dup:" + dropSentBefore + " band:"
					+ dropBand + " port:" + filterPort + ")";
			res += " I(" + inMessages + " " + Stuff.trunc(lastTrafficRate, 1)
					+ "/" + Stuff.trunc(totalTrafficRate, 1) + ")";
			res += " O(" + outMessages + " "
					+ Stuff.trunc(lastOutTrafficRate, 1) + "/"
					+ Stuff.trunc(totalOutTrafficRate, 1) + ")";
		}
		return res;
	}

	public String myReadLine(InputStream ii) throws IOException {
		String res = "";
		while (true) {
			int c = ii.read();
			countTraffic(1);
			if (tryconnectTime > 0 && connectTime == 0)
				connectTime = System.currentTimeMillis();
			if (c == -1)
				break;
			res += (char) c;
			if (c == '\n')
				return res;
		}
		if (res.length() == 0)
			return null;
		return res;
	}

	public static String myTime(long x) {
		String res = "";
		if (x > 60 * 60 * 24) {
			int y = (int) (x / (3600 * 24));
			res += y + " ";
			x = x % (3600 * 24);
		}
		if (x > 60 * 60) {
			int y = (int) x / (3600);
			x = x % 3600;
			if (res.length() > 0 && y < 10)
				res += "0";
			res += y + ":";
		}
		if (x > 60) {
			int y = (int) x / 60;
			x = x % 60;
			if (res.length() > 0 && y < 10)
				res += "0";
			res += y + ":";
		}
		if (res.length() > 0 && x < 10)
			res += "0";
		res = res + x;
		return res;
	}

	int recoverCount = 0;

	static int MAXRECOVERATTEMPTS = 15;

	public void recover(InputStream ii) {
		recoverCount++;
		if (recoverCount > MAXRECOVERATTEMPTS) {
			killSelf(true);
			return;
		}
		System.out.println("Trying to recover " + recoverCount);
		byte b[] = new byte[24];
		int numread = 0;
		while (true) {
			try {
				int c = ii.read();
				countTraffic(1);
				if (c == -1)
					break;
				for (int a = 0; a < b.length - 1; a++) {
					b[a] = b[a + 1];
				}
				b[b.length - 1] = (byte) c;
			} catch (IOException ioe) {
				killSelf(true);
				return;
			}

			numread++;
			if (numread > 4000) {
				killSelf(true);
			}
			if (numread >= 24) {
				if (b[23] == 0 && b[22] == 0 && b[21] == 0 && b[20] == 0
						&& b[19] == 0 && b[18] == 14 && b[17] < Header.MAXTTL
						&& b[16] < Header.MAXTTL) {
					System.out.println("Recover Pong Guessed");
					try {
						b = new byte[14 - 2];
						int check = ii.read(b);
						while (check != b.length && check > 0) {
							check += ii.read(b, check, b.length - check);
							// System.out.println("WARNING read mismatch
							// "+b.length+" != "+check);
						}
						countTraffic(b.length);
					} catch (IOException ioe) {
						killSelf(true);
						return;
					}
					return;
				}
				if (b[23] == 0 && b[22] == 0 && b[21] == 0 && b[20] == 0
						&& b[19] < Header.MAXTTL && b[18] < Header.MAXTTL) {
					System.out.println("Recover Ping Guessed");
					return;
				}
				if (b[23] == 0 && b[22] == 0 && b[21] == 0 && b[20] == 0
						&& b[19] == 0 && b[18] > 2 && b[17] < Header.MAXTTL
						&& b[16] < Header.MAXTTL) {
					System.out.println("Recover Query Guessed");
					try {
						b = new byte[b[18] - 2];
						int check = ii.read(b);
						while (check != b.length && check > 0) {
							check += ii.read(b, check, b.length - check);
							// System.out.println("WARNING read mismatch
							// "+b.length+" != "+check);
						}
						countTraffic(b.length);
					} catch (IOException ioe) {
						killSelf(true);
						return;
					}
					return;
				}
			}
		}
	}

}
