package aj.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class HTTPRelay implements Runnable {

	boolean text = false;
	boolean get = true;

	Socket cs;
	Socket ss;
	InputStream cis;
	OutputStream cos;
	InputStream sis;
	OutputStream sos;

	String rhost = null;
	int rport;
	String rdir = "";
	String req = "";
	static boolean nocookies = true;
	static boolean nojava = true;
	static boolean raw = false;
	static boolean logTrans = false;
	static boolean logHeader = false;
	static boolean logBody = false;
	static boolean logInput = false;
	static boolean logOutput = false;

	static boolean completeOneAtTime = false;

	static String localhost;
	static int localport;

	static String test[] = {
			"href=www.yahoo.com", "href=\"www.yahoo.com\"", "href=\'www.yahoo.com\'", "href = www.yahoo.com> ", 
			"href=www.yahoo.com:8088/r/s.html", "href=\"www.yahoo.com:8080/r\"", "href=www.yahoo.com  href=www.yahoo.com", 
			"href = \"www.yahoo.com:8080/r\""};


	/**
	 *  Constructor for the HTTPRelay object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public HTTPRelay(Socket cs) {
		this.cs = cs;
		//System.out.println("Relay created");
	}


	/**
	 *  Main processing method for the HTTPRelay object 
	 */
	public void run() {
		//System.out.println("Relay running");
		try {
			cis=cs.getInputStream();
			cos=cs.getOutputStream();
			String headerRead="";
//System.out.println("cis="+cis);
			while (true) {
				String l = myReadLine(cis);
				if (l == null) {
					break;
				}
				if (l.equals("")) break;
				processHeaderLine(l);
				headerRead+=l+"\r\n";
			}
			headerRead+="\r\n";
			if (rhost == null || rport == 0) {
				System.out.println("MyError: bad host port request");
				close();
				return;
			}
			else {
				try {
					ss = new Socket(rhost, rport);
					//System.out.println("Relay socket created");
					sis = ss.getInputStream();
					sos = ss.getOutputStream();
					//System.out.println("Relay streams created");
				}
				catch (UnknownHostException UHE) {
					System.out.println("MyError: bad host request cannot make socket to "+rhost+" on port "+rport);
					close();
					return;
				}
				sos.write(headerRead.getBytes());
System.out.println("HEADERLOG>"+headerRead);
				sos.flush();
				new Thread(new RelayThread(sis,cos)).start();
				new Thread(new RelayThread(cis,sos)).start();
				//while (true) {
					//int c=cis.read();
					//if (c==-1) break;
					//sos.write(c);
					//sos.flush();
				//}
			}
		}
		catch (IOException IOE10) {
			System.out.println("MyError: " + IOE10);
			close();
		}
	}



	/**
	 *  Description of the Method 
	 *
	 *@param  i                Description of Parameter 
	 *@return                  Description of the Returned Value 
	 *@exception  IOException  Description of Exception 
	 */
	public String myReadLine(InputStream i) throws IOException {
		String s = null;
		while (true) {
			int c = i.read();
			if (c == -1 && s != null) {
				return s;
			}
			else if (c == -1) {
				return null;
			}
			if (s == null) {
				s = "";
			}
			if ((char) c == '\n') {
				return s;
			}
			if ((char) c == '\r') {
				i.read();
				return s;
			}
			s += "" + (char) c;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String fixHead(String s) {
		if (raw) return s;
		String fix = s;
		if (s.toUpperCase().indexOf("REFERER: ") >= 0) {
			fix = "Referer: " + s.substring(s.toUpperCase().lastIndexOf("HTTP://"));
		}
		if (s.toUpperCase().indexOf("HTTP://") > 0 && s.toUpperCase().indexOf(" HTTP/") > s.toUpperCase().indexOf(" HTTP://")) {
			fix = (get ? "GET" : "POST") + " " + req + s.substring(s.indexOf(" HTTP/"));
		}
		return fix;
	}



	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public void processHeaderLine(String s) {
		s = s.trim();
		String portstring = "";

		while (s.indexOf("\\") >= 0) {
			s = s.substring(0, s.indexOf("\\")) + "/" + s.substring(s.indexOf("\\") + 1);
		}

		if (s.toUpperCase().indexOf("HTTP://") >= 0  || s.toUpperCase().indexOf("HTTP:\\\\") >= 0 ) {
			if (s.toUpperCase().startsWith("GET ")) {
				get = true;
				s = s.substring(3).trim();
			}
			else if (s.toUpperCase().startsWith("POST ")) {
				get = false;
				s = s.substring(4).trim();
			}
			else return;

			if (s.toUpperCase().indexOf("HTTP://")>=0) {
				s = s.substring(s.toUpperCase().lastIndexOf("HTTP://")+7).trim();
			}
			if (s.toUpperCase().indexOf("HTTP:\\\\")>=0) {
				s = s.substring(s.toUpperCase().lastIndexOf("HTTP:\\\\")+7).trim();
			}
			if (s.toUpperCase().indexOf("HTTP//")>=0) {
				s = s.substring(s.toUpperCase().lastIndexOf("HTTP//")+6).trim();
			}
			if (s.toUpperCase().indexOf("HTTP\\")>=0) {
				s = s.substring(s.toUpperCase().lastIndexOf("HTTP\\")+6).trim();
			}
			rhost = s.substring(0,s.indexOf(" "));
			if (logTrans) System.out.println("rhost1="+rhost);

			req = "/";
			if (rhost.indexOf("/") >= 0) {
				req=rhost.substring(rhost.indexOf("/"));
				rhost = rhost.substring(0, rhost.indexOf("/"));
				if (logTrans) System.out.println("rhost2="+rhost);
			}
			if (!req.startsWith("/")) {
				req = "/" + req;
			}

			rport = 80;
			if (rhost.indexOf(":") >= 0) {
				try {
					portstring = rhost.substring(rhost.indexOf(":") + 1);
					rport = Integer.parseInt(portstring);
				}
				catch (NumberFormatException NFE) {
					System.out.println("MyError: bad number in host port request "+portstring);
					close();
				}
				rhost = rhost.substring(0, rhost.indexOf(":"));
			}

			rdir = req.substring(1);
			//cut '/' in /r/s.html
			if (rdir.indexOf("/") >= 0) {
				rdir = rdir.substring(0, rdir.lastIndexOf("/") + 1);
			}

		}
	}



	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String fixRelay(String s) {
		if (raw) return s;
		String correcthttp = "http://" + localhost + (localport == 80 ? "" : ":" + localport) + "/http://";
		String JUNK = myrand() + "" + s.hashCode();
		s = replaceSubIgnoreCase(s, "/HTTP://", JUNK);
		s = replaceSubIgnoreCase(s, "HTTP://", correcthttp);
		s = replaceSubIgnoreCase(s, JUNK, "/HTTP://");
		//s = subfix2(s, "archive");
		//s = subfix2(s, "href");
		//s = subfix2(s, "src");

		if (nojava) {
			s = replaceToken(s, "SCRIPT", "disable_SCRIPT");
			s = replaceToken(s, "APPLET", "disable_APPLET");
		}

		return s;
	}



	//find LOOK= and replace with LOOK=CORRECTHTTP
	//used of href=\ and src=\
	/**
	 *  Description of the Method 
	 *
	 *@param  org   Description of Parameter 
	 *@param  look  Description of Parameter 
	 *@return       Description of the Returned Value 
	 */
	public String subfix2(String org, String look) {
		String JUNK = myrand() + "" + org.hashCode();
		//"HGSGSDH$#@";
		String correcthttp = "http://" + localhost + (localport == 80 ? "" : ":" + localport);
		String cap = org.toUpperCase();
		look = look.toUpperCase();
		String ref = null;
		//System.out.println("line ="+org);
		while (cap.indexOf(look) >= 0) {
			int loc = cap.indexOf(look) + look.length();
			ref = stringAfterEqual(org.substring(loc));
			//System.out.println("ref="+ref);
			if (ref == null || ref.toUpperCase().startsWith("HTTP://")) {
				org = org.substring(0, cap.indexOf(look)) + JUNK + org.substring(cap.indexOf(look) + look.length());
				ref = null;
			}
			else {
				int end = loc + org.substring(loc).indexOf(ref) + ref.length();
				if (org.substring(end).startsWith("\"")) {
					end++;
				}
				if (org.substring(end).startsWith("\'")) {
					end++;
				}

				//        ref=replaceSubExclusiveIgnoreCase(ref,look,JUNK);
				ref = replaceSubIgnoreCase(ref, look, JUNK);
				if (ref.startsWith("\\") || ref.startsWith("/")) {
					ref = correcthttp + "/HTTP://" + rhost + (rport == 80 ? "" : ":" + rport) + ref;
				}
				else {
					ref = correcthttp + "/HTTP://" + rhost + (rport == 80 ? "" : ":" + rport) + "/" + rdir + ref;
				}
				org = org.substring(0, cap.indexOf(look)) + JUNK + "=\"" + ref + "\"" + org.substring(end);
			}
			cap = org.toUpperCase();
		}
		while (org.indexOf(JUNK) >= 0) {
			org = org.substring(0, org.indexOf(JUNK)) + look + org.substring(org.indexOf(JUNK) + JUNK.length());
		}
		return org;
	}


	//returns the argument after an equal sign in a string
	//arguments ended by a space,tab or eol.  Quoted strings
	//using ' or " are a argument and of higher priority
	/**
	 *  Description of the Method 
	 *
	 *@param  org  Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public String stringAfterEqual(String org) {
		org = org.trim();
		if (!org.startsWith("=")) {
			return null;
		}
		org = org.substring(1).trim();
		if (org.startsWith("\"")) {
			org = org.substring(1);
			if (org.indexOf("\"") >= 0) {
				return org.substring(0, org.indexOf("\""));
			}
			else {
				return null;
			}
		}
		if (org.startsWith("\'")) {
			org = org.substring(1);
			if (org.indexOf("\'") >= 0) {
				return org.substring(0, org.indexOf("\'"));
			}
			else {
				return null;
			}
		}
		else {
			int a = org.length();
			if (org.indexOf(" ") >= 0) {
				a = Math.min(a, org.indexOf(" "));
			}
			if (org.indexOf("\t") >= 0) {
				a = Math.min(a, org.indexOf("\t"));
			}
			if (org.indexOf(">") >= 0) {
				a = Math.min(a, org.indexOf(">"));
			}
			if (org.indexOf("\n") >= 0) {
				a = Math.min(a, org.indexOf("\n"));
			}
			return org.substring(0, a);
		}
	}


	//replaces FIND with REP int ORG but will not effect existing REP
	//
	//replaces all occurance of FIND in ORG with REP
	//if FIND is a subset of ORG then no change.
	//
	//ie  find=find  rep=finding
	//     org=find finding fining
	//     new=finding finding     not=finding findinging
	/**
	 *  Description of the Method 
	 *
	 *@param  org   Description of Parameter 
	 *@param  find  Description of Parameter 
	 *@param  rep   Description of Parameter 
	 *@return       Description of the Returned Value 
	 */
	public String replaceSubExclusiveIgnoreCase(String org, String find, String rep) {
		String JUNK = myrand() + "" + org.hashCode();
		while (org.indexOf(rep) >= 0) {
			org = org.substring(0, org.indexOf(rep)) + JUNK + org.substring(org.indexOf(rep) + rep.length());
		}
		while (org.toUpperCase().indexOf(find.toUpperCase()) >= 0) {
			org = org.substring(0, org.toUpperCase().indexOf(find.toUpperCase())) + JUNK + org.substring(org.toUpperCase().indexOf(find.toUpperCase()) + find.length());
		}
		while (org.indexOf(JUNK) >= 0) {
			org = org.substring(0, org.indexOf(JUNK)) + rep + org.substring(org.indexOf(JUNK) + JUNK.length());
		}
		return org;
	}


	//replace all occurances of FIND with REP in string ORG, IGNOREING CASE.
	/**
	 *  Description of the Method 
	 *
	 *@param  org   Description of Parameter 
	 *@param  find  Description of Parameter 
	 *@param  rep   Description of Parameter 
	 *@return       Description of the Returned Value 
	 */
	public String replaceSubIgnoreCase(String org, String find, String rep) {
		String JUNK = myrand() + "" + org.hashCode();
		while (org.toUpperCase().indexOf(find.toUpperCase()) >= 0) {
			org = org.substring(0, org.toUpperCase().indexOf(find.toUpperCase())) + JUNK + org.substring(org.toUpperCase().indexOf(find.toUpperCase()) + find.length());
		}
		while (org.indexOf(JUNK) >= 0) {
			org = org.substring(0, org.indexOf(JUNK)) + rep + org.substring(org.indexOf(JUNK) + JUNK.length());
		}
		return org;
	}


	/**
	 *  Description of the Method 
	 */
	public void close() {
		if (true) return;
		try {if (cis!=null) cis.close();}catch (IOException ioe) {}
		try {if (cos!=null) cos.close();}catch (IOException ioe) {}
		try {if (sis!=null) sis.close();}catch (IOException ioe) {}
		try {if (sos!=null) sos.close();}catch (IOException ioe) {}
		try {if (ss!=null) ss.close();}catch (IOException IOE1) {}
		try {if (cs!=null) cs.close();}catch (IOException IOE1) {}
	}


	//replace all occurances of FIND with REP in string ORG.
	// find = FIN rep= FIND
	// org = fin FIN FIND  new = fin FIND FINDD
	/**
	 *  Description of the Method 
	 *
	 *@param  org   Description of Parameter 
	 *@param  find  Description of Parameter 
	 *@param  rep   Description of Parameter 
	 *@return       Description of the Returned Value 
	 */
	public static String replaceSub(String org, String find, String rep) {
		String JUNK = myrand() + "" + org.hashCode();
		//"!@#SHS*D&S(D";
		//    String JUNK="!@#SHS*D&S(D";
		while (org.indexOf(find) >= 0) {
			org = org.substring(0, org.indexOf(find)) + JUNK + org.substring(org.indexOf(find) + find.length());
		}
		while (org.indexOf(JUNK) >= 0) {
			org = org.substring(0, org.indexOf(JUNK)) + rep + org.substring(org.indexOf(JUNK) + JUNK.length());
		}
		return org;
	}



	/**
	 *  Description of the Method 
	 *
	 *@param  org   Description of Parameter 
	 *@param  find  Description of Parameter 
	 *@param  rep   Description of Parameter 
	 *@return       Description of the Returned Value 
	 */
	public static String replaceToken(String org, String find, String rep) {
		String JUNK = myrand() + "" + org.hashCode();
		//":AS(@&SG:S";
		String JUNK2 = myrand() + "FK%*SF(@";
		String repl = org;
		while (repl.toUpperCase().indexOf(find.toUpperCase()) >= 0) {
			int bi = repl.toUpperCase().indexOf(find.toUpperCase());
			String b = repl.substring(0, bi).trim();
			if (b.endsWith("<") || 
					(b.endsWith("/") && b.substring(0, b.length() - 1).trim().endsWith("<"))) {
				repl = repl.substring(0, bi) + JUNK + repl.substring(bi + find.length());
			}
			else {
				String f = repl.substring(bi, bi + find.length());
				f = f.substring(0, f.length() / 2) + JUNK2 + f.substring(f.length() / 2);
				repl = repl.substring(0, bi) + f + repl.substring(bi + find.length());
			}
		}
		repl = replaceSub(repl, JUNK, rep);
		repl = replaceSub(repl, JUNK2, "");
		return repl;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		if (s.length == 0) {
			System.out.println("FORMAT: java aj.net.HTTPRelay <port> [options]");
			System.out.println("  -c  cookies_on");
			System.out.println("  -f  complete one page at time");
			System.out.println("  -j  java_on");
			System.out.println("  -r  raw proxy");
			System.out.println("  -h  logHeaders");
			System.out.println("  -b  logBodyMessage");
			System.out.println("  -t  logTrans connect etc");
			System.out.println("  -w  logWrites");
			System.out.println("  -i  logWrites");
			System.exit(0);
		}
		try {
			localport = Integer.parseInt(s[0]);
			ServerSocket S = new ServerSocket(localport);
			//System.out.println("Main Server started");
			for (int a=1;a<s.length;a++) {
				if (s[a].toUpperCase().indexOf("C")>=0) {
					HTTPRelay.nocookies = false;
				}
				if (s[a].toUpperCase().indexOf("F")>=0) {
					HTTPRelay.completeOneAtTime = true;
				}
				if (s[a].toUpperCase().indexOf("J")>=0) {
					HTTPRelay.nojava = false;
				}
				if (s[a].toUpperCase().indexOf("H")>=0) {
					HTTPRelay.logHeader = true;
				}
				if (s[a].toUpperCase().indexOf("T")>=0) {
					HTTPRelay.logTrans = true;
				}
				if (s[a].toUpperCase().indexOf("B")>=0) {
					HTTPRelay.logBody = true;
				}
				if (s[a].toUpperCase().indexOf("R")>=0) {
					HTTPRelay.raw = true;
				}
				if (s[a].toUpperCase().indexOf("W")>=0) {
					HTTPRelay.logOutput = true;
				}
				if (s[a].toUpperCase().indexOf("I")>=0) {
					HTTPRelay.logInput = true;
				}
			}
			if (logTrans) {
				System.out.println("Trans>HTTPRelay working");
			}
			while (true) {
				Socket mm = S.accept();
				//System.out.println("New Relay connected");
				HTTPRelay httprelay = new HTTPRelay(mm);
				new Thread(httprelay).start();
			}
		}
		catch (IOException E) {
			System.out.println("MyError: " + E);
			System.out.println("FORMAT: java aj.net.HTTPRelay <port> [options]");
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public static String myrand() {
		int i = (int) (Math.random() * 10000);
		return i + "";
	}

}
class RelayThread implements Runnable {
	InputStream i;
	OutputStream o;

	public RelayThread(InputStream i, OutputStream o) {
		//System.out.println("RelayThread created");
		this.i=i;
		this.o=o;
		//System.out.println("i="+i);
		//System.out.println("o="+o);
	}
	public void run() {
		//System.out.println("RelayThread started");
		try {
			//byte b[]=new byte[1000];
		//System.out.println("i="+i);
		//System.out.println("o="+o);
			while (true) {
		//		Thread.currentThread().yield();
				//if (i.available()>0) {
				int c=i.read();
				if (c==-1) break;
				System.out.print((char)c);
				o.write(c);
				o.flush();
				//o.flush();
				//}
				//int c=i.read(b);
				//if (c<=0) break;
				//else o.write(b,0,c);
			}
			o.flush();
		} catch (IOException IOE) {
		}
		try {i.close();} catch (IOException ioe) {}
		try {o.close();} catch (IOException ioe) {}
	}
}
