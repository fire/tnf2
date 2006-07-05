package aj.iem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

import aj.misc.Stuff;

/*
 ib = initial bid 5000 @ 0
 ia = initial bid 5000 @ 0
 mb = middle bid
 ma = middle ask
 na = nickle ask
 nb = nickle bid
 vb = value bid
 va = value ask
 ds = discount sell (bids > 1000)
 db = discount buy (asks < 1000)
 ss = sell surplus
 bs = sell shortage
 */

public class IEMTool implements Runnable {

	private String defaultFirstMarketId = "8";

	private String iemstatusfile = "iem_status.log";

	String orderscommandfile = "orders.txt.log";

	String ordersoutputlogfile = "orders.log";

	String iemstatusordersfile = "iem_statusorders.log";

	private String iemordersfile = "iem_orders.log";

	private String iemtoolfile = "iem_tool.log";

	private String iemactivefile = "iem_active.log";

	static final String buyAtMarket = "P", sellAtMarket = "S", placeAsk = "A",
			buyFixBundle = "D", sellFixBundle = "C", sellMarkBundle = "M",
			placeBid = "B", buyMarkBundle = "N";

	private int IPSEC = 1;// use Ip security 1=yes,0=no

	static boolean proxy = false;

	static String proxyHost = "localhost";

	static int proxyPort = 80;

	static final public int minute = 1000 * 60, hour = minute * 60,
			day = 24 * hour, FOREVERDELAY = 60 * day, FOURWEEKS = 28 * day;

	// private int SOCKETTIMEOUT = 1 * minute;

	int EXTREAMBIDASKDELAY = 48 * hour;

	int MIDDLEBIDASKDELAY = 24 * hour;

	int NICKLEBIDASKDELAY = 18 * hour;

	final static int LOWESTBIDALLOWED = 10;// under 5 cent don't bid, over 95

	// don't

	// ask

	static double MIDDLEMARGIN = .5;// bid 50 -> mb = 25, bid = 10 ->mb =5

	String localhostId = "";

	private boolean loginEnabled = true;

	String sessionId = "";

	String userId = "AJudd3587";

	private String password = "default";

	int MIN_BUND_SIZE = 5; // default real settings in config file

	int MAX_BUND_SIZE = 20; // default real settings in config file

	int MID_BUND_SIZE = 10;// default real settings in config file

	int NICK_BUND_SIZE = 2;// default real settings in config file

	int EXTREAM_BUND_SIZE = 50;// default real settings in config file

	// private int DISCOUNT_BUND_SIZE = 10;// default real settings in config
	// file

	int MINCASHRESERVE = 4001;

	int cash = 0;

	private long lastLoginTry = -1;

	Vector allowedMarketNames = new Vector();

	Vector allowedBundleNames = new Vector();

	Vector allowedNickleNames = new Vector();

	Vector allowedExtreamNames = new Vector();

	Vector allowedFreeNames = new Vector();

	Vector allowedMiddleNames = new Vector();

	private Vector allowedDiscountNames = new Vector();

	private Vector knownNames = new Vector();

	private int MINLOGINDELAY = 1000 * 60 * 3;

	private int LOGINDELAY = MINLOGINDELAY;

	private int LOGINCHECKDELAY = MINLOGINDELAY;// AKA ORDERS check

	private Vector newMarkets = new Vector();

	private Vector allMarkets = new Vector();

	private String lastActive = "";

	private int reportToolCount = 0;

	private void help() {
		System.err.println("Usage: java aj.iem.IEMTool <options>");
		System.err.println("-u<userid>");
		System.err.println("-p<password>");
		System.err.println("-s<sessionid>");
		System.err.println("-i               ;ipsecurity_off");
		System.err.println("-w<host>:<port>  ;use web proxy");
		System.err.println("? (--help) this menu");
	}

	public IEMTool() {
		try {
			localhostId = new String(InetAddress.getLocalHost()
					.getCanonicalHostName());
		} catch (IOException ioe) {
			System.out.println(localhostId);
		}
		File f = new File("iem_config.txt");
		BufferedReader br = null;
		try {
			String configloc = "default config";
			if (f == null || !f.exists()) {
				URL u = IEMTool.class.getResource("iem_config.txt");
				br = new BufferedReader(new InputStreamReader(u.openStream()));
			} else {
				configloc = "Using local config";
				br = new BufferedReader(new FileReader(f));
			}
			if (br == null) {
				reportTool("No config file found");
				System.out.println("No Config file found");
			}
			// reportOrders("reading config file ");
			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				s = s.trim();
				if (s.trim().length() == 0)
					continue;
				if (s.startsWith("#"))
					continue;
				// reportOrders("found config line "+s);
				if (s.toUpperCase().startsWith("ALLOWEDMARKET")) {
					allowedMarketNames.addElement(s.substring(13).trim());
				} else if (s.toUpperCase().startsWith("STATUSLOGFILE")) {
					iemstatusfile = s.substring(13).trim();
					// System.out.println("iemstatusfile found="+iemstatusfile);
				} else if (s.toUpperCase().startsWith("MIN_CASH")) {
					MINCASHRESERVE = Integer.parseInt(s.substring(8).trim());
					// System.out.println("MINCASHRESERVE
					// found="+MINCASHRESERVE);
				} else if (s.toUpperCase().startsWith("ORDERSCOMMANDFILE")) {
					orderscommandfile = s.substring(17).trim();
					// System.out.println("orderscommandfile
					// found="+orderscommandfile);
				} else if (s.toUpperCase().startsWith("USERID")) {
					userId = s.substring(6).trim();
					// System.out.println("userid found="+userId);
				} else if (s.toUpperCase().startsWith("PASSWORD")) {
					password = s.substring(8).trim();
					// System.out.println("password found="+password);
				} else if (s.toUpperCase().startsWith("ORDERSOUTPUTLOG")) {
					ordersoutputlogfile = s.substring(15).trim();
					// System.out.println("ordersoutputlog
					// found="+ordersoutputlogfile);
				} else if (s.toUpperCase().startsWith("STATUSORDERSLOGFILE")) {
					iemstatusordersfile = s.substring(19).trim();
					// System.out.println("iemstatusordersfile
					// found="+iemstatusordersfile);
				} else if (s.toUpperCase().startsWith("ORDERSLOGFILE")) {
					iemordersfile = s.substring(13).trim();
					// System.out.println("iemordersfile found="+iemordersfile);
				} else if (s.toUpperCase().startsWith("TOOLLOGFILE")) {
					iemtoolfile = s.substring(11).trim();
					reportTool("Using " + configloc);
					// System.out.println("iemtoolfile found="+iemtoolfile);
				} else if (s.toUpperCase().startsWith("ACTIVELOGFILE")) {
					iemactivefile = s.substring(13).trim();
					// System.out.println("activelogfile found="+iemactivefile);
				} else if (s.toUpperCase().startsWith("ALLOWEDBUNDLE")) {
					allowedBundleNames.addElement(s.substring(13).trim());
				} else if (s.toUpperCase().startsWith("ALLOWEDMIDDLE")) {
					allowedMiddleNames.addElement(s.substring(13).trim());
				} else if (s.toUpperCase().startsWith("ALLOWEDNICKLE")) {
					allowedNickleNames.addElement(s.substring(13).trim());
				} else if (s.toUpperCase().startsWith("ALLOWEDEXTREAM")) {
					allowedExtreamNames.addElement(s.substring(14).trim());
				} else if (s.toUpperCase().startsWith("ALLOWEDFREE")) {
					allowedFreeNames.addElement(s.substring(12).trim());
				} else if (s.toUpperCase().startsWith("ALLOWEDDISCOUNT")) {
					allowedDiscountNames.addElement(s.substring(16).trim());
				} else if (s.toUpperCase().startsWith("KNOWN")) {
					knownNames.addElement(s.substring(5).trim());
				} else if (s.toUpperCase().startsWith("PROXY")) {
					proxy = true;
					proxyHost = s.substring(5).trim();
					if (proxyHost.indexOf(":") >= 0) {
						try {
							proxyPort = Integer.parseInt(proxyHost
									.substring(proxyHost.indexOf(":") + 1));
							proxyHost = proxyHost.substring(0, proxyHost
									.indexOf(":"));
						} catch (NumberFormatException nfe) {
							reportTool("MyError proxy config bad number");
							System.exit(0);
						}
					}
					reportTool("Proxy found " + proxyHost + " at " + proxyPort);
				} else if (s.toUpperCase().startsWith("MINBUNDSIZE")) {
					MIN_BUND_SIZE = Integer.parseInt(s.substring(11).trim());
				} else if (s.toUpperCase().startsWith("MAXBUNDSIZE")) {
					MAX_BUND_SIZE = Integer.parseInt(s.substring(11).trim());
				} else if (s.toUpperCase().startsWith("NICK_BUND_SIZE")) {
					NICK_BUND_SIZE = Integer.parseInt(s.substring(14).trim());
				} else if (s.toUpperCase().startsWith("MID_BUND_SIZE")) {
					MID_BUND_SIZE = Integer.parseInt(s.substring(13).trim());
				} else if (s.toUpperCase().startsWith("EXT_BUND_SIZE")) {
					EXTREAM_BUND_SIZE = Integer
							.parseInt(s.substring(13).trim());
					// } else if
					// (s.toUpperCase().startsWith("DISCOUNT_BUND_SIZE")) {
					// DISCOUNT_BUND_SIZE = Integer.parseInt(s.substring(18)
					// .trim());
				} else if (s.toUpperCase().startsWith("ABORT")) {
					reportTool("ABORT FOUND");
					System.out.println("ABORT FOUND in CONFIG");
					System.exit(0);
				}
			}
			reportTool("End config read - Using " + configloc);
		} catch (IOException e) {
			System.out.println("MyError: Cannot read config.txt file");
			e.printStackTrace();
		}
	}

	public static void main(String s[]) {
		IEMTool iem = new IEMTool();
		iem.parseCommandArgs(s);

		new Thread(iem).start();
	}

	private void parseCommandArgs(String[] s) {
		for (int a = 0; a < s.length; a++) {
			if (s[a].toUpperCase().indexOf("?") >= 0
					|| s[a].toUpperCase().indexOf("-HELP") >= 0) {
				help();
				System.exit(0);
			} else if (s[a].toUpperCase().startsWith("-S")) {
				sessionId = s[a].substring(2);
				loginEnabled = false;
				continue;
			} else if (s[a].toUpperCase().startsWith("-U")) {
				userId = s[a].substring(2);
				continue;
			} else if (s[a].toUpperCase().startsWith("-P")) {
				password = s[a].substring(2);
				continue;
			} else if (s[a].toUpperCase().startsWith("-I")) {
				IPSEC = 0;
			} else if (s[a].toUpperCase().startsWith("-W")) {
				proxy = true;
				proxyHost = s[a].substring(2);
				// System.out.println("Proxy found "+proxyHost);
				if (proxyHost.indexOf(":") >= 0) {
					try {
						proxyPort = Integer.parseInt(proxyHost
								.substring(proxyHost.indexOf(":") + 1));
						proxyHost = proxyHost.substring(0, proxyHost
								.indexOf(":"));
					} catch (NumberFormatException nfe) {
						reportTool("MyError proxy config bad number");
						System.exit(0);
					}
				}
			}
		}
	}

	boolean isPract() {
		return userId.equals(password);
	}

	private void doLogin() {
		long delay = LOGINDELAY + lastLoginTry - System.currentTimeMillis();
		lastLoginTry = System.currentTimeMillis();
		if (delay > 0) {
			try {
				Thread.sleep((int) (delay * (Math.random() * .1 + 1)));
			} catch (Exception ei) {
			}
		}
		if (isPract()) {
			defaultFirstMarketId = "3";
		}
		if (!loginEnabled) {
			reportTool("Login disabled");
			return;
		}
		String all = doLoginRequest();
		if (all != null && all.indexOf("InterruptSessionHandler") >= 0) {
			all = doInterruptRequest();
		}
		if (all != null && all.indexOf("SESSIONID") >= 0) {
			sessionId = all.substring(all.indexOf("SESSIONID") + 9);
			sessionId = sessionId.substring(sessionId.indexOf("VALUE=\"") + 7);
			sessionId = sessionId.substring(0, sessionId.indexOf("\""));
			reportTool("Session Id=" + sessionId);
		} else {
			reportTool("Login failure force sleep for "
					+ (LOGINDELAY / 1000 / 60) + " minutes");
			doLogout();
			LOGINDELAY *= 2;
		}
		if (isLoggedIn())
			LOGINDELAY = MINLOGINDELAY;
	}

	private String doInterruptRequest() {
		reportTool("Placing interrupt");
		String request = "USERTYPE=trader&LOGIN=" + userId
				+ "&LANGUAGE=english&IPSecurity=" + IPSEC + "&PASSWORD="
				+ password + "\r\n";
		String header = "POST /webex/WebEx.dll?InterruptSessionHandler HTTP/1.0\r\n"
				+ "Host: "
				+ localhostId
				+ "\r\n"
				+ "User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.0.1) Gecko/20021003\r\n"
				+ "Content-Type: application/x-www-form-urlencoded\r\n"
				+ "Content-Length: " + (request.length() - 2) + "\r\n" + "\r\n";
		return readAllSocket(header, request, "iemweb.biz.uiowa.edu", 80);
	}

	private String doLoginRequest() {
		String request = "Login=" + userId + "&Password=" + password
				+ "&UserType=trader&Language=english&IPSecurity=" + IPSEC
				+ "\r\n";

		String header = "POST /webex/WebEx.dll?LoginHandler HTTP/1.0\r\n"
				+ "Host: "
				+ localhostId
				+ "\r\n"
				+ "User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.0.1) Gecko/20021003\r\n"
				+ "Content-Type: application/x-www-form-urlencoded\r\n"
				+ "Content-Length: " + (request.length() - 2) + "\r\n" + "\r\n";

		reportTool("\nPlacing login");
		return readAllSocket(header, request, "iemweb.biz.uiowa.edu", 80);
	}

	boolean isLoggedIn() {
		return userId != null && sessionId != null
				&& userId.trim().length() > 0 && sessionId.trim().length() > 0;
	}

	synchronized void reportStatus() {
		int liquid = cash;
		int asset = 0;
		String report = "";
		// correct MarketId required for limits. Each market (CompRet, MSFT, FR)
		// has unique MarketID.
		String ordersReport = ";contractId marketid sessionId name bundname bid|mybid* ask|myask* held*|netheld deltaheld\n";
		boolean partial = false;
		int elasp = 0;
		Vector v = getAllMarketsClone();

		for (int a = 0; a < v.size(); a++) {
			Market m = (Market) v.elementAt(a);
			report += m.getReport();
			elasp = Math.max(elasp, m.getElaspedTime());
			if (m.isReady()) {
				liquid += m.getLiquidValue();
				asset += m.getAssetsValue();
			} else
				partial = true;
			ordersReport += m.getOrdersReport();
		}
		if (!partial && reportToolCount < v.size() * 2 - 1) {
			String rv = "";
			reportActive(liquid, asset, rv);
		}
		report = new Date().toString() + (partial ? " *partial*" : "")
				+ " time " + (elasp / 1000.0) + " secs" + "\nCash     ="
				+ (cash < 100000 ? " " : "") + (cash < 10000 ? " " : "")
				+ (cash < 1000 ? " " : "") + cash + "\nBundles  ="
				+ (liquid - cash < 100000 ? " " : "")
				+ (liquid - cash < 10000 ? " " : "")
				+ (liquid - cash < 1000 ? " " : "") + (liquid - cash)
				+ "\nLiquid   =" + (liquid < 100000 ? " " : "")
				+ (liquid < 10000 ? " " : "") + (liquid < 1000 ? " " : "")
				+ liquid + "\nAsset Val=" + (asset < 100000 ? " " : "")
				+ (asset < 10000 ? " " : "") + (asset < 1000 ? " " : "")
				+ asset + "\nGross Val=" + (asset + liquid < 100000 ? " " : "")
				+ (asset + liquid < 10000 ? " " : "")
				+ (asset + liquid < 1000 ? " " : "") + (asset + liquid)
				+ "\n\n" + report;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(iemstatusfile));
			pw.println(report);
			pw.flush();
			pw.close();
			pw = new PrintWriter(new FileWriter(iemstatusordersfile));
			pw.println(ordersReport);
			pw.flush();
			pw.close();
		} catch (IOException ioe) {
			System.err.println("MyError :" + ioe);
		}
	}

	private synchronized void reportActive(int liquid, int assets, String line) {
		if (lastActive.equals(liquid + ""))
			return;
		else
			lastActive = (liquid + "");
		Vector v = getAllMarketsClone();
		for (int a = 0; a < v.size(); a++) {
			Market m = (Market) v.elementAt(a);
			line += m.getQuickSummary() + "";
		}
		String h = "Liq=" + (liquid / 1000.0) + " Val=" + (assets / 1000.0)
				+ "                ";
		line = h.substring(0, 25) + line;
		try {
			RandomAccessFile ra = new RandomAccessFile(iemactivefile, "rw");
			ra.seek(ra.length());
			Calendar calendar = new GregorianCalendar();// tz);
			// String t[]=TimeZone.getAvailableIDs(-6*60*60*1000);
			// for (int a=0;a<t.length;a++) {
			// System.out.println("timezone ="+t[a]);
			// }
			Date todayDate = new Date();
			calendar.setTime(todayDate);
			int mon = calendar.get(Calendar.MONTH) + 1;
			int dom = calendar.get(Calendar.DAY_OF_MONTH);
			int dow = calendar.get(Calendar.DAY_OF_WEEK);
			String doww = "";
			switch (dow) {
			case 1:
				doww = "Su";
				break;
			case 2:
				doww = "Mo";
				break;
			case 3:
				doww = "Tu";
				break;
			case 4:
				doww = "We";
				break;
			case 5:
				doww = "Th";
				break;
			case 6:
				doww = "Fr";
				break;
			default:
				doww = "Sa";
				break;
			}
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int min = calendar.get(Calendar.MINUTE);
			String dateString = "" + (hour < 10 ? "0" : "") + hour + ":"
					+ (min < 10 ? "0" : "") + min + " " + doww + " " + mon
					+ "/" + dom;
			line += dateString;
			ra.writeBytes(line + "\n");
			ra.close();
		} catch (IOException ioe) {
		}
	}

	synchronized void reportTool(String line) {
		try {
			RandomAccessFile ra = new RandomAccessFile(iemtoolfile, "rw");
			ra.seek(ra.length());
			if (line == null) {
				reportToolCount++;
				ra.writeBytes(".");
			} else if (line.length() == 1) {
				reportToolCount++;
				ra.writeBytes(line);
			} else {
				ra.writeBytes(line + " " + new Date().toString() + "\n");
			}
			if (reportToolCount > 60) {
				reportToolCount = 0;
				ra.writeBytes(new Date().toString() + "\n");
			}
			ra.close();
		} catch (IOException ioe) {
		}
	}

	synchronized void reportOrders(String line) {
		try {
			RandomAccessFile ra = new RandomAccessFile(iemordersfile, "rw");
			ra.seek(ra.length());
			ra.writeBytes(line + " " + new Date().toString() + "\n");
			ra.close();
		} catch (IOException ioe) {
		}
	}

	// public static synchronized String readAllSocket(String host, int port,
	// String header,String req) {
	public synchronized static String readAllSocket(String header, String req,
			String host, int port) {
		if (host.equalsIgnoreCase("iemweb.biz.uiowa.edu"))
			host = "128.255.244.60";
		if (host.equalsIgnoreCase("www.biz.uiowa.edu"))
			host = "128.255.244.57";
		String urlstring = "";
		urlstring = "http://" + host + (port != 80 ? ":" + port : "")
				+ header.substring(4, header.indexOf("HTTP/1.0")).trim();

		URL url;
		URLConnection urlConn;
		DataOutputStream printout;
		BufferedReader input;
		try {
			if (!proxy) {
				url = new URL(urlstring);
			} else {
				url = new URL("http", proxyHost, proxyPort, urlstring);
			}
			try {
				urlConn = url.openConnection();
				urlConn.setDoInput(true);
				if (header.indexOf("POST") == 0) {
					urlConn.setDoOutput(true);
				}
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				if (header.indexOf("POST") == 0) {
					// Send POST output.
					printout = new DataOutputStream(urlConn.getOutputStream());
					printout.writeBytes(req);
					printout.flush();
					printout.close();
				}
				// Get response data.
				input = new BufferedReader(new InputStreamReader(urlConn
						.getInputStream()));
				String all = "", str;
				while (null != ((str = input.readLine()))) {
					all += str + "\n";
				}
				input.close();
				return all;
			} catch (IOException e1) {
				// reportTool("MyError io error 15. Read web page from socket.
				// Connect/read failure."
				// + e1);
				e1.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// reportTool("MyError io error 16. Read web page from socket.
			// Connect/read failure."
			// + e);
			e.printStackTrace();
		}
		return null;
	}

	private void doRelogin() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					iemstatusordersfile));
			String s = br.readLine();
			while (s != null && s.startsWith(";") || s.startsWith("#")) {
				s = br.readLine();
			}
			String args[] = Stuff.getTokens(s, " ");
			if (args.length < 2 || args[1] == "null") {
				sessionId = null;
				reportTool("ReLogin failed.  File bad.");
				return;
			}
			sessionId = args[2];
			reportTool("ReLogin Session Id=" + sessionId);
			return;
		} catch (IOException ioe) {
		}
		reportTool("ReLogin failed. No file.");
	}

	public void run() {
		reportTool("IEMTool started " + new Date());
		doRelogin();
		if (!isLoggedIn()) {
			doLogin();
		}
		downloadMarketSeeds();
		startMarkets();
		while (true) {
			reportStatus();
			try {
				Thread
						.sleep((int) (LOGINCHECKDELAY * (Math.random() * .1 + 1)));
			} catch (Exception ei) {
			}
			if (!isLoggedIn()) {
				doLogin();
				downloadMarketSeeds();
				startMarkets();
			} else {
				reportTool("O");
				new Orders(this);
			}
		}
	}

	private Vector getAllMarketsClone() {
		return (Vector) allMarkets.clone();
	}

	public void startMarkets() {
		Vector fix = new Vector();
		Vector newmark = (Vector) newMarkets.clone();
		Vector marketMaster = getAllMarketsClone();
		for (int b = 0; b < newmark.size(); b++) {
			Market mm = (Market) newmark.elementAt(b);
			Vector v = (Vector) marketMaster.clone();
			for (int a = 0; a < v.size(); a++) {
				Market m = (Market) v.elementAt(a);
				if (mm.equals(m)) {
					mm = m;
					v.removeElement(m);
					a--;
				}
			}
			if (!mm.isRunning()) {
				new Thread(mm).start();
			}
			fix.addElement(mm);
		}
		setAllMarkets(fix);
	}

	private void setAllMarkets(Vector v) {
		allMarkets = v;
	}

	private void downloadMarketSeeds() {
		if (!isLoggedIn())
			return;
		newMarkets = new Vector();
		String all = readAllSocket(
				"GET "
						+ "/webex/WebEx.dll?TraderInterfaceHandler?USERTYPE=trader&LOGIN="
						+ userId + "&SESSIONID=" + sessionId
						+ "&LANGUAGE=english&Markets=" + defaultFirstMarketId
						+ "&Panel_id=menu" + " HTTP/1.0\r\n\r\n", "",
				"iemweb.biz.uiowa.edu", 80);
		reportStatus();

		Thread.yield();
		if (all.indexOf("<SELECT NAME=\"Markets\">") >= 0) {
			all = all.substring(all.indexOf("<SELECT NAME=\"Markets\">") + 23);
			if (all.indexOf("</SELECT>") >= 0)
				all = all.substring(0, all.indexOf("</SELECT>"));
			else {
				doLogout();
				return;
			}
			while (all.indexOf("VALUE=\"") >= 0) {
				String mv = all.substring(all.indexOf("VALUE=\"") + 7);
				if (mv.indexOf("\"") >= 0)
					mv = mv.substring(0, mv.indexOf("\""));
				all = all.substring(all.indexOf("VALUE=\"") + 7);
				String mn = "";
				if (all.indexOf(">") >= 0)
					mn = all.substring(all.indexOf(">") + 1);
				if (mn.indexOf("<") >= 0)
					mn = mn.substring(0, mn.indexOf("<"));
				mn = mn.trim();
				Market mm = new Market(this, mn, mv);
				if (!mm.isValidMarket()) {
					reportTool("MyError: market download failed =" + mn + " "
							+ mv);
					doLogout();
					return;
				}
				newMarkets.add(mm);
			}

		} else {
			doLogout();
		}
		reportStatus();
	}

	void doLogout() {
		reportTool("Logout detected");
		if (sessionId != null)
			lastLoginTry = System.currentTimeMillis();
		sessionId = null;
	}

	static void placeMarketOrder(String transType, String price, String quant,
			String assetId, String marketId, IEMTool tool) {
		try {
			double pricev = Double.parseDouble(price);
			pricev = Stuff.trunc(Math.max(0, pricev), 3);
			int quan = Integer.parseInt(quant);
			if (quan < 1) {
				// reportTool("MyError bad quantinty in market order>" + quant);
				System.exit(0);
			}
			if (transType.equals(buyMarkBundle))
				price = "" + Stuff.trunc(Math.min(.999, pricev), 3);
			if (transType.equals(sellMarkBundle))
				price = "" + Stuff.trunc(Math.max(1.001, pricev), 3);
			// if (transType.equals(sellFixBundle)) price="1.000";
			// if (transType.equals(buyFixBundle)) price="1.000";
		} catch (NumberFormatException nfe) {
			// reportTool("MyError numberformat io error 7 in place market order
			// for>"
			// + price);
			return;
		}
		String request = "USERTYPE=trader&LOGIN=" + tool.userId + "&SESSIONID="
				+ tool.sessionId + "&LANGUAGE=english&Markets=" + marketId
				+ "&Panel_id=main&AssetId=" + assetId + "&Quantity=" + quant
				+ "&Price=" + price + "&OrderType=" + transType + "\r\n";
		String header = "POST /webex/WebEx.dll?OrderConfirmHandler HTTP/1.0\r\n"
				+ "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/msword, application/vnd.ms-powerpoint, */*\r\n"
				+ "Accept-Language: en-us\r\n"
				+ "User-Agent: Mozilla/4.0 (flandar@yahoo.com)\r\n"
				+ "Host: "
				+ tool.localhostId
				+ "\r\n"
				+ "Content-Length: "
				+ (request.length() - 2) + "\r\n" + "Pragma: no-cache\r\n\r\n";
		String host = "iemweb.biz.uiowa.edu";
		int port = 80;
		// if (transType.equals(buyMarkBundle) ||
		// transType.equals(sellMarkBundle)) {
		// reportOrders("MarkBund request ="+request);
		// }
		// log market buy and seel request format to see if error causing missed
		// orders

		readAllSocket(header, request, host, port);
		Thread.yield();
	}

	public static void placeLimitOrder(String transType, String price,
			String quant, String assetId, String marketId, long delay,
			IEMTool tool) {
		try {
			double pricev = Double.parseDouble(price);
			int quan = Integer.parseInt(quant);
			if (quan < 1) {
				// reportTool("MyError bad quantinty in limit order>" + quant);
				System.exit(0);
			}
			if (pricev < 0 || pricev > 1) {
				// reportTool("MyError bad price in limit order>" + price);
				System.exit(0);
			}
			if (pricev >= .999 && transType.equals(placeBid)) {
				// reportTool("MyError bid of .999 price in limit order>" +
				// price);
				System.exit(0);
			}
			if (pricev <= .001 && transType.equals(placeAsk)) {
				// reportTool("MyError ask of .001 price in limit order>" +
				// price);
				System.exit(0);
			}
			pricev = Stuff.trunc(Math.max(0, Math.min(pricev, 1)), 3);
			price = pricev + "";
		} catch (NumberFormatException nfe) {
			// reportTool("MyError numberformat io error 17 in place limit order
			// for>"
			// + price);
			return;
		}
		String request = "USERTYPE=trader&LOGIN=" + tool.userId + "&SESSIONID="
				+ tool.sessionId + "&LANGUAGE=english&Markets=" + marketId
				+ "&Panel_id=main&RequestCode=" + transType + assetId
				+ "&LimitOrderPrice=" + price + "&LimitOrderQuantity=" + quant
				+ "&LimitOrderExpirationDate=" + limitTime(delay)
				+ "Immediate=0\r\n";

		String header = "POST /webex/WebEx.dll?LimitOrderHandler HTTP/1.0\r\n"
				+ "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/msword, application/vnd.ms-powerpoint, */*\r\n"
				+ "Accept-Language: en-us\r\n"
				+ "User-Agent: Mozilla/4.0 (flandar@yahoo.com)\r\n" + "Host: "
				+ tool.localhostId + "\r\n" + "Content-Length: "
				+ (request.length() - 2) + "\r\n" + "Pragma: no-cache\r\n\r\n";

		String host = "iemweb.biz.uiowa.edu";
		int port = 80;
		readAllSocket(header, request, host, port);
		Thread.yield();
	}

	static String limitTime(long delay) {
		delay += (int) (delay * Math.random());
		if (delay < 0)
			delay = 20 * 1000 * 60;
		TimeZone tz = TimeZone.getTimeZone("America/Chicago");
		Calendar calendar = new GregorianCalendar(tz);
		// Calendar calendar = new GregorianCalendar();
		Date todayDate = new Date();
		Date endDate = new Date(todayDate.getTime() + delay);
		calendar.setTime(endDate);
		int y = calendar.get(Calendar.YEAR) - 2000;
		int m = calendar.get(Calendar.MONTH) + 1;
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int ap = (h < 12 ? 1 : 0);
		if (h > 12)
			h = h - 12;
		int min = calendar.get(Calendar.MINUTE);
		return (m < 10 ? "0" : "") + m + "%2F" + (d < 10 ? "0" : "") + d
				+ "%2F" + (y < 10 ? "0" : "") + y + "+" + (h < 10 ? "0" : "")
				+ h + "%3A" + (min < 10 ? "0" : "") + min
				+ (ap == 1 ? "AM" : "PM") + "&";
	}

	// comment
}
