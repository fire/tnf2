package aj.iem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import aj.io.MLtoText;
import aj.misc.Stuff;

public class Risk {

	// add userId config
	// add proxy config

	// int numheld=0;
	double totalWin = 0, totalLose = 0, netinv;

	double mwin = 0, mlose = 0, minv = 0;

	static boolean log = false;

	static boolean logSocket = false;

	static boolean quick = false;

	String contName = "";

	boolean winner = false;

	public static void help() {
		System.out
				.println("Format: java aj.iem.Risk Sid Mid <options> [contracts...]");
		System.out.println("  -w<host:port)   proxy");
		System.out.println("  -log  turn logging on");
		System.out.println("  -socketlog  turn socketlogging on");
		System.out.println("  -quick   quick risk only first page");
		System.out.println("");
		System.out.println("noargs use piped input for single text file test");

	}

	public static void main(String s[]) {
		String userId = "AJudd3587";
		// calculate investment, average cost
		// download each history automatically
		// add winning payout of winning and losing payout of losing for each
		// contract
		if (s.length >= 3) {
			String sid = s[0];
			if (sid.length() != "rblzsyehlwtrsll".length()) {
				System.out.println("MyError: Bad SID=" + sid + " wrong length");
				System.exit(0);
			}
			String mid = s[1];
			int midt = 0;
			if (log) {
				System.out.println(";SessionId=" + sid);
				System.out.println(";marketId=" + mid);
			}
			System.out.println("");
			try {
				midt = Integer.parseInt(mid);
			} catch (NumberFormatException nfe) {
				System.out.println("MyError: bad number in params MID=" + mid);
				System.exit(0);
			}
			Vector v = new Vector();
			System.out.print("reading.");
			for (int a = 2; a < s.length; a++) {
				if (s[a].indexOf("-") >= 0 || s[a].indexOf("?") >= 0) {
					if (s[a].toUpperCase().indexOf("-H") >= 0
							|| s[a].indexOf("?") >= 0) {
						help();
					}
					if (s[a].toUpperCase().indexOf("-Q") >= 0) {
						System.out.println("Quick download detected");
						quick = true;
					}
					if (s[a].toUpperCase().indexOf("-L") >= 0) {
						System.out.println("Loggin turned on");
						log = true;
					}
					if (s[a].toUpperCase().indexOf("-S") >= 0) {
						System.out.println("Socket Loggin turned on");
						logSocket = true;
					}
					if (s[a].toUpperCase().startsWith("-W")) {
						IEMTool.proxy = true;
						IEMTool.proxyHost = s[a].substring(2);
						// System.out.println("Proxy found "+proxyHost);
						if (IEMTool.proxyHost.indexOf(":") >= 0) {
							try {
								IEMTool.proxyPort = Integer
										.parseInt(IEMTool.proxyHost
												.substring(IEMTool.proxyHost
														.indexOf(":") + 1));
								IEMTool.proxyHost = IEMTool.proxyHost
										.substring(0, IEMTool.proxyHost
												.indexOf(":"));
							} catch (NumberFormatException nfe) {
								System.out
										.println("MyError proxy config bad number");
								System.exit(0);
							}
						}
						System.out.println("Proxy config found "
								+ IEMTool.proxyHost + " and port="
								+ IEMTool.proxyPort);
					}
					continue;
				}
				System.out.print("" + (s.length - a));
				String aid = s[a];
				try {
					int t = Integer.parseInt(aid);
					if (t - 50 < midt) {
						System.out.println("MyError: bad number in params AID="
								+ aid + " cannot be within 50 of MID");
						System.exit(0);
					}
				} catch (NumberFormatException nfe) {
					System.out.println("MyError: bad number in params AID="
							+ aid);
					System.exit(0);
				}
				Risk r = new Risk(userId, sid, mid, aid);
				v.addElement(r);
			}
			int marketinv = 0;
			for (int a = 0; a < v.size(); a++) {
				Risk r = (Risk) v.elementAt(a);
				System.out.print("Results for Contract " + r.contName);
				double tw = 0;
				double mtw = 0, mtl = 0;
				for (int b = 0; b < v.size(); b++) {
					Risk r2 = (Risk) v.elementAt(b);
					if (r2.minv != 0 && Math.abs(r2.minv) > marketinv)
						marketinv = (int) r2.minv;
					// prof = win-inv
					// prof = lose-inv = .02
					mtw += r2.mwin;
					mtl += r2.mlose;
					if (a == b) {
						tw += r2.totalWin;
					} else {
						tw += r2.totalLose;
					}

				}
				// System.out.println("mtw="+mtw+" mtl="+mtl);
				// System.out.println("marketinv="+marketinv);
				mtw = -mtw + mtl + marketinv;
				// System.out.println(" assets="+r.numheld+" profit if
				// win="+Stuff.trunc(tw,3)+"
				// marketonlyprofit="+Stuff.trunc(mtw,3));
				System.out.println(" Profit if win=" + Stuff.trunc(tw, 3)
						+ " marketonlyprofit=" + Stuff.trunc(mtw, 3) + " "
						+ (r.winner ? "WINNER" : ""));
			}
		} else {
			help();
			new Risk(null, null, null, null);
		}
		// new Risk(uid,sid,mid,aid);
	}

	public Risk(String userId, String sessionId, String marketId, String assetId) {
		// marketId (not bundleId)
		Vector buys = new Vector();
		Vector sells = new Vector();
		// System.out.print("reading");
		System.out.print("+");
		String temp = readAll(userId, sessionId, marketId, assetId);
		if (logSocket)
			System.out.println("preML --begin--");
		if (logSocket)
			System.out.println(temp);
		if (logSocket)
			System.out.println("preML --end--");
		String all = MLtoText.cutMLaddSpaces(temp);
		// if (false)
		while (temp.indexOf("TopDate\" VALUE=\"") >= 0 && !quick) {
			String td = temp.substring(temp.indexOf("TopDate\" VALUE=\""));
			td = td.substring(16, td.indexOf("\">"));
			// System.out.println("TopDate found "+td);
			while (td.indexOf("/") >= 0) {
				td = td.substring(0, td.indexOf("/")) + "%2F"
						+ td.substring(td.indexOf("/") + 1);
			}
			while (td.indexOf(" ") >= 0) {
				td = td.substring(0, td.indexOf(" ")) + "+"
						+ td.substring(td.indexOf(" ") + 1);
			}
			while (td.indexOf(":") >= 0) {
				td = td.substring(0, td.indexOf(":")) + "%3A"
						+ td.substring(td.indexOf(":") + 1);
			}
			// System.out.println("TopDate fixed "+td);

			String request = "USERTYPE=trader" + "&LOGIN=" + userId
					+ "&SESSIONID=" + sessionId + "&LANGUAGE=english"
					+ "&Markets=" + marketId + "&Asset=" + assetId
					+ "&Panel_id=main" + "&TopDate=" + td + "\r\n";
			String header = "POST /webex/WebEx.dll?TradeHistoryHandler  HTTP/1.0\r\n"
					+ "Host: iemweb.biz.uiowa.edu\r\n"
					+ "User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.0.1) Gecko/20021003\r\n"
					+ "Content-Type: application/x-www-form-urlencoded\r\n"
					+ "Content-Length: "
					+ (request.length() - 2)
					+ "\r\n"
					+ "\r\n";

			// System.out.println("Header ---\n"+header);
			// System.out.println("request ---\n"+request);
			System.out.print("+");
			temp = IEMTool.readAllSocket(header, request,
					"iemweb.biz.uiowa.edu", 80);
			// if (log) System.out.println("preML --begin--");
			// if (log) System.out.println(temp);
			// if (log) System.out.println("preML --end--");

			all += MLtoText.cutMLaddSpaces(temp);
		}
		// System.out.println("-");
		// if (log) System.out.println("READALL --begin--");
		// if (log) System.out.println(all);
		// if (log) System.out.println("READALL --end--");
		// all=MLtoText.cutML(all);
		System.out.print("*");
		String contract = "";
		if (all.indexOf(" PM") >= 0)
			contract = all.substring(all.indexOf("PM") + 2,
					all.indexOf("PM") + 102).trim();
		else if (all.indexOf(" AM") >= 0)
			contract = all.substring(all.indexOf("AM") + 2,
					all.indexOf("AM") + 102).trim();
		if (all.indexOf("Liquidation") >= 0) {
			int li = all.indexOf("Liquidation");
			String liq = all.substring(li, all.indexOf("\n", li));
			System.out.println("Liquid found=" + liq);
			String t[] = Stuff.getTokens(Stuff.superTrim(liq), " ");
			if (t[2].startsWith("1."))
				winner = true;
			// /1/19/04 13:18:13 Liquidation 51 0.00000
			System.out.println("winnwer=" + winner);
		}
		if (contract.indexOf("Trade") >= 0)
			contract = contract.substring(0, contract.indexOf("Trade"));
		contName = contract;
		// all=aj.misc.Stuff.superTrim(all);
		// System.out.println("all="+all+"\n");

		// System.out.println("Contract="+contract);
		// System.out.println("\n\n");
		// System.out.println("all.length()="+all.length());
		String holdings = all.substring(all.indexOf("Holdings:")
				+ "Holdings:".length(), all.indexOf("Holdings:")
				+ "Holdings:".length() + 102);
		// System.out.println("holdings.length()="+holdings.length());
		holdings = Stuff.superTrim(holdings);
		if (holdings.indexOf("*") >= 0)
			holdings = holdings.substring(0, holdings.indexOf("*"));
		else if (holdings.indexOf(" ") >= 0)
			holdings = holdings.substring(0, holdings.indexOf(" "));
		if (log)
			System.out.println("Contracts Held=" + holdings);

		Integer.parseInt(holdings.trim());
		// numheld=num;
		int lastbuy = 0;
		// System.out.println(all);
		// System.out.print("*");
		while (all.indexOf(" Buy", lastbuy) >= lastbuy) {
			if (log)
				System.out.print("b");
			String t;
			int nextbuy = all.indexOf(" Buy", lastbuy);
			int uptobuy = all.indexOf(" Buy", nextbuy + 1);
			int uptosell = all.indexOf(" Sell", nextbuy);
			// System.out.println("lastbuy="+lastbuy+" nextbuy="+nextbuy+"
			// uptobuy="+uptobuy);
			if (uptosell > nextbuy && uptobuy > uptosell)
				uptobuy = uptosell;
			if (uptobuy < lastbuy)
				uptobuy = all.length();
			t = all.substring(nextbuy, uptobuy);
			lastbuy = nextbuy + 1;

			// String
			// t=all.substring(all.indexOf("Buy",lastbuy),lastbuy+all.substring(all.indexOf("Buy",lastbuy))
			// if (false) {
			// t=all.substring(all.indexOf("Buy"));
			// if (t.indexOf(" Sell")>=0)
			// t=t.substring(0,t.indexOf(" Sell"));
			// if (t.indexOf(" Buy")>=0)
			// t=t.substring(0,t.indexOf(" Buy"));
			// all=all.substring(0,all.indexOf(t))+all.substring(all.indexOf(t)+t.length());
			// }
			if (t.indexOf("Fixed Price Bundle") >= 0)
				continue;
			if (t.indexOf("Market Prices Bundle") >= 0)
				t = t.substring(0, t.indexOf("Market Prices Bundle"))
						+ t.substring(t.indexOf("Market Prices Bundle")
								+ "Market Prices Bundle".length()) + "M";
			buys.addElement(t.trim());
			if (buys.size() % 100 == 0)
				System.out.print("-");
		}
		if (log)
			System.out.println(".");
		lastbuy = 0;
		while (all.indexOf(" Sell", lastbuy) >= lastbuy) {
			if (log)
				System.out.print("s");
			String t;
			int nextbuy = all.indexOf(" Sell", lastbuy);
			int uptobuy = all.indexOf(" Sell", nextbuy + 1);
			int uptosell = all.indexOf(" Buy", nextbuy);
			// System.out.println("lastbuy="+lastbuy+" nextbuy="+nextbuy+"
			// uptobuy="+uptobuy);
			if (uptosell > nextbuy && uptobuy > uptosell)
				uptobuy = uptosell;
			if (uptobuy < lastbuy)
				uptobuy = all.length();
			t = all.substring(nextbuy, uptobuy);
			lastbuy = nextbuy + 1;

			// String t=all.substring(all.indexOf("Sell"));
			// if (t.indexOf(" Sell")>=0)
			// t=t.substring(0,t.indexOf(" Sell"));
			// if (t.indexOf(" Buy")>=0)
			// t=t.substring(0,t.indexOf(" Buy"));
			// all=all.substring(0,all.indexOf(t))+all.substring(all.indexOf(t)+t.length());
			if (t.indexOf("Fixed Price Bundle") >= 0)
				continue;
			if (t.indexOf("Market Prices Bundle") >= 0)
				t = t.substring(0, t.indexOf("Market Prices Bundle"))
						+ t.substring(t.indexOf("Market Prices Bundle")
								+ "Market Prices Bundle".length()) + "M";
			sells.addElement(t.trim());
			if (sells.size() % 100 == 0)
				System.out.print("-");
		}
		if (log)
			System.out.println(".");
		double cost = 0, earn = 0, netinv = 0;
		double win = 0, lose = 0;
		for (int a = 0; a < buys.size(); a++) {
			String m = (String) buys.elementAt(a);
			boolean market = false;
			if (m.endsWith("M"))
				market = true;
			String t[] = Stuff.getTokens(m, " ");
			int amt = Integer.parseInt(t[1]);
			double v = Double.parseDouble(t[2]) * amt;

			if (market) {
				mwin += v;
				// mlose-=v;
				minv += amt;
				if (log)
					System.out.println("MARKET BUYFOUND " + contract + " "
							+ t[1] + "@" + t[2] + " market inventor=" + minv);
			} else {
				if (log)
					System.out.println("BUYFOUND " + contract + " " + t[1]
							+ "@" + t[2] + " = " + v + " pricePaid to buy="
							+ cost + " netinv=" + netinv + " "
							+ (market ? "M" : ""));
				win += amt - v;
				lose -= v;
				netinv += amt;
				cost += v;
			}
		}
		for (int a = 0; a < sells.size(); a++) {
			String m = (String) sells.elementAt(a);
			boolean market = false;
			if (m.endsWith("M"))
				market = true;
			String t[] = Stuff.getTokens(m, " ");
			int amt = Integer.parseInt(t[1]);
			double v = Double.parseDouble(t[2]) * amt;
			if (market) {
				// mwin-=amt-v;
				mlose += v;
				minv -= amt;
				if (log)
					System.out.println("MARKET SELLFOUND " + contract + " "
							+ t[1] + "@" + t[2] + " market inventor=" + minv);
			} else {
				win -= amt - v;
				lose += v;
				netinv -= amt;
				earn += v;
				if (log)
					System.out.println("SELLFOUND " + contract + " " + t[1]
							+ "@" + t[2] + " = " + v
							+ " cash received from sale=" + earn + " netinv="
							+ netinv + " ");
			}
		}
		// System.out.println("REPORT Total for "+netinv+" of "+contract+"
		// pricePaid to buy = "+Stuff.trunc(cost,3)+" cash Received from
		// sale="+Stuff.trunc(earn,3));
		// System.out.println("REPORT Total if "+contract+"
		// win="+Stuff.trunc(win,3)+" lose="+Stuff.trunc(lose,3));

		totalWin = win;
		totalLose = lose;
	}

	public static String readAll(String userId, String sessionId,
			String marketId, String assetId) {
		if (userId == null) {
			System.out
					.println("NO userId found.  Using System.in to file data");
			return readAll();
		} else
			return IEMTool
					.readAllSocket(
							"GET "
									+ "/webex/WebEx.dll?TradeHistoryHandler?USERTYPE=trader&LOGIN="
									+ userId + "&SESSIONID=" + sessionId
									+ "&LANGUAGE=english&Markets=" + marketId
									+ "&Asset=" + assetId + "&Panel_id=main"
									+ " HTTP/1.0\r\n\r\n", "",
							"iemweb.biz.uiowa.edu", 80);
	}

	public static String readAll() {
		String all = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			while (true) {
				String ss = br.readLine();
				if (ss == null)
					break;
				ss = MLtoText.cutMLaddSpaces(ss);
				all += ss + " ";
			}
			// all=MLtoText.cutML(all);
			all = Stuff.superTrim(all);
		} catch (IOException ioe) {
		}
		return all;
	}
}
