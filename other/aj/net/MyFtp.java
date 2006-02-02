package aj.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

import aj.misc.Stuff;

/**
 *  Description of the Class FTP SERVER 
 *
 *@author     judda 
 *@created    August 28, 2000 
 */
public class MyFtp {
	int port = 21;
	static Vector login = new Vector();
	static String root = "/";
	static boolean log = false;

      static String version="Version 1.0";
      static String localHost="";

      public static String getBanner() {
if (localHost.indexOf("/")>0) localHost=localHost.substring(0,localHost.indexOf("/")).trim();
if (localHost.indexOf(".")>0) localHost=localHost.substring(0,localHost.indexOf(".")).trim();
        return "220 " + localHost + " aj.net.MyFtp FTP server "+version+" ready.";
//"220 "+localHost+" FTP server (SunOS 4.1) ready."
//"220 "+localHost+" FTP server (UNIX(r) System V Release 4.0) ready."
      }


	/**
	 *  Constructor for the MyFtp object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public MyFtp(String s[]) {
		if (s.length == 0) {
			System.out.println("FORMAT: java aj.net.MyFtp [options]");
			System.out.println(" -user:name:pass[:cwd]");
			System.out.println(" -root:c:\\");
			System.out.println(" -port:21");
			System.out.println(" -log");
		}

		for (int a = 0; a < s.length; a++) {
			if (s[a].toUpperCase().indexOf("USER:") >= 0 && s[a].indexOf(":") < s[a].lastIndexOf(":")) {
//user:j:p:r:RW
				String[] l = new String[3];
				s[a] = s[a].substring(s[a].toUpperCase().indexOf("USER:") + 5);
				l[0] = s[a];
				l[2] = root;
                        if (s[a].indexOf(":")>0) {
                           l[0]=s[a].substring(0, s[a].indexOf(":"));
  				   s[a] = s[a].substring(s[a].indexOf(":") + 1);
				   l[1] = s[a];
                        }
                        if (s[a].indexOf(":")>0) {
                           l[1]=s[a].substring(0, s[a].indexOf(":"));
  				   s[a] = s[a].substring(s[a].indexOf(":") + 1);
				   l[2] = s[a];
                        }

				login.addElement(l);
				//        System.out.println("user found " + l[0] + "," + l[1]);
			}
			else if (s[a].toUpperCase().indexOf("ROOT:") >= 0) {
				root = s[a].substring(s[a].toUpperCase().indexOf("ROOT:") + 5);
			}
			else if (s[a].toUpperCase().indexOf("LOG") >= 0) {
				log = true;
			}
			else if (s[a].toUpperCase().indexOf("PORT:") >= 0) {
				try {
					port = Integer.parseInt(s[a].substring(s[a].toUpperCase().indexOf("PORT:") + 5));
				}
				catch (NumberFormatException NFE) {
					System.out.println("MyError: Bad port " + s[a]);
					System.exit(0);
				}
			}
		}
		try {
			ServerSocket S = new ServerSocket(port);
			while (true) {
				Socket SS = S.accept();
                        localHost=SS.getLocalAddress().toString();
				new Thread(new FtpConnection(SS)).start();
			}
		}
		catch (IOException IOE2) {
			System.out.println("MyError: cannot connect server busy!");
			System.exit(0);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  name  Description of Parameter 
	 *@param  host  Description of Parameter 
	 *@param  port  Description of Parameter 
	 *@param  nl    Description of Parameter 
	 *@param  b     Description of Parameter 
	 */
	public static void get(String name, String host, int port, Socket nl, boolean b) {
		//ascii vs binnary
		try {
			Socket s = new Socket(host, port);
			get(name,s,nl,b);
		}
		catch (Exception e) {
			send(nl,"500 Transfer error");
		}
	}
	
	static public void send(Socket s,String line) {
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
	
	public static void get(String name, Socket s, Socket nl, boolean b) {
		send(nl,"150 Opening " + (b ? "BINARY" : "ASCII") + " socket for " + name);
		try {
			InputStream i = s.getInputStream();
			FileOutputStream f = new FileOutputStream(name);
			if (!b) {
				BufferedReader br = new BufferedReader(new InputStreamReader(i));
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(f));
				while (true) {
					String ts = br.readLine();
					if (ts == null) {
						break;
					}
					pw.println(ts);
				}
				pw.flush();
				pw.close();
				s.close();
				send(nl,"226 Transfer complete");
				return;
			}
			while (true) {
				byte ca[] = new byte[100000];
				int c = i.read(ca);
				if (c == -1) {
					break;
				}
				else {
					f.write(ca, 0, c);
				}
			}
			f.flush();
			f.close();
			s.close();
			send(nl,"226 Transfer complete");
		}
		catch (Exception e) {
			send(nl,"500 Transfer error");
		}
	}


	/**
	 *  Gets the CWD attribute of the MyFtp class 
	 *
	 *@param  u  Description of Parameter 
	 *@return    The CWD value 
	 */
	public static String getCWD(String u) {
		for (int a = 0; a < login.size(); a++) {
			String l[] = (String[]) login.elementAt(a);
			if (l[0].equals(u) && l.length == 3) {
				return l[2];
			}
		}
		return root;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		new MyFtp(s);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  u  Description of Parameter 
	 *@param  p  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static boolean login(String u, String p,FtpConnection fc) {
		if (login.size() == 0) {
			return true;
		}
		for (int a = 0; a < login.size(); a++) {
			String l[] = (String[]) login.elementAt(a);
			if (l[0].equals(u) && l[1].equals(p)) {
				System.out.println(fc.socket.getInetAddress()+" "+new Date()+":LOGIN: " + u);
				return true;
			}
		}
		if (MyFtp.log) {
			System.out.println(fc.socket.getInetAddress()+" "+new Date()+":login fail: " + u + " " + p);
		}
		return false;
	}



	/**
	 *  Description of the Method 
	 *
	 *@param  name     Description of Parameter 
	 *@param  host     Description of Parameter 
	 *@param  port     Description of Parameter 
	 *@param  nl       Description of Parameter 
	 *@param  b        Description of Parameter 
	 *@param  details  Description of Parameter 
	 */
	public static void send(String name, String host, int port, Socket nl, boolean b, boolean details) {
				//    System.out.println("sending" + name + " " + host + " " + port + " " + b);
		//ascii vs binnary
		Socket s = null;
		try {
			s = new Socket(host, port);
			send(name,s,nl,b,details);
		}
		catch (Exception e) {
			System.out.println("myerror: " + e);
			try {
				s.close();
			}
			catch (Exception e2) {}
			send(nl,"500 Transfer error");
		}
	}
	public static void send(String name, Socket s, Socket nl, boolean b, boolean details) {
		send(nl,"150 Opening " + (b ? "BINARY" : "ASCII") + " socket for " + name);
		OutputStream o = null;
		try {
			o = s.getOutputStream();

			String look = "*";
			if (name.indexOf("*") >= 0 || name.indexOf("?") >= 0) {
				//  System.out.println("Wild card found!");
				if (name.indexOf("*") >= 0) {
					look = name.substring(name.indexOf("*"));
				}
				if (name.indexOf("?") >= 0 && name.indexOf("?") > name.indexOf("*")) {
					look = name.substring(name.indexOf("?"));
				}
				look = name.substring(name.substring(0, name.length() - look.length()).lastIndexOf("/") + 1);
				name = name.substring(0, name.length() - look.length());
				//  System.out.println("Wild card found! "+look);
				//  System.out.println("Wild card found! "+name);
			}
			File F = new File(name);
			if (F.isDirectory()) {
				String all = "";
				File dir[] = F.listFiles();
				for (int a = 0; a < dir.length; a++) {
					if (MyFtp.patternMatch(dir[a].getName(), look)) {
						all += dir[a].getName() + (dir[a].isDirectory() ? "/" : (details ? "    \t" + dir[a].length() + "" : "") ) + "\n";
					}

				}
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(o));
				while (all.indexOf("\n") >= 0) {
					pw.println(all.substring(0, all.indexOf("\n")));
					all = all.substring(all.indexOf("\n") + 1);
				}
				pw.println(all);
				pw.flush();
				pw.close();
				s.close();
				//        System.out.println("trans done");
				send(nl,"226 Transfer complete");
			}
			else if (!b) {
				BufferedReader br = new BufferedReader(new FileReader(name));
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(o));
				while (true) {
					String ts = br.readLine();
					if (ts == null) {
						break;
					}
					pw.println(ts);
				}
				pw.flush();
				pw.close();
				br.close();
				s.close();
				send(nl,"226 Transfer complete");
				return;
			}
			else {
				InputStream f = new FileInputStream(name);
				while (true) {
					byte ca[] = new byte[100000];
					int c = f.read(ca);
					if (c ==-1) {
						break;
					}
					else {
						o.write(ca, 0, c);
					}
				}
				f.close();
				o.flush();
				s.close();
				//        System.out.println("trans done");
				send(nl,"226 Transfer complete");
			}
		}
		catch (Exception e) {
			System.out.println("myerror: " + e);
			try {
				s.close();
			}
			catch (Exception e2) {}
			send(nl,"500 Transfer error");
		}
	}



	/**
	 *  Description of the Method 
	 *
	 *@param  name  Description of Parameter 
	 *@param  pat   Description of Parameter 
	 *@return       Description of the Returned Value 
	 */
	public static boolean patternMatch(String name, String pat) {
		//System.out.println("match test "+name+" "+pat);
		if (pat.indexOf("*") < 0 && pat.indexOf("?") < 0) {
			return name.equals(pat);
		}
		if (pat.indexOf("?") >= 0) {
			if (name.length() != pat.length()) {
				return false;
			}
			if (name.startsWith(pat.substring(0, pat.indexOf("?"))) && 
					name.endsWith(pat.substring(pat.indexOf("?") + 1))) {
				return true;
			}
		}
		if (pat.indexOf("*") >= 0) {
			if (name.startsWith(pat.substring(0, pat.indexOf("*"))) && 
					name.endsWith(pat.substring(pat.indexOf("*") + 1))) {
				return true;
			}
		}
		return false;
	}

}

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 28, 2000 
 */
class FtpConnection implements Runnable {


	Socket nl;
	Socket socket;

	String user, pass;
	boolean loggedIn = false;

	String host;
	int port;
	boolean binary = true;
	String cwd = "/";


	/**
	 *  Constructor for the FtpConnection object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public FtpConnection(Socket s) {
		socket = s;
		if (MyFtp.log) {
			System.out.println(socket.getInetAddress()+" "+new Date()+" CONNECTION");
		}
	}

	static public void send(Socket s,String line) {
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

	/**
	 *  Main processing method for the FtpConnection object 
	 */
	public void run() {
		
			nl = socket;
		
		send(nl,MyFtp.getBanner());//
		BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(nl.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		while (true) {
			if (nl.isClosed()) {
				break;
			}
			String cmd=null;//.receive();
			try {
				cmd = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (cmd == null) {
				break;
			}
			if (cmd.toUpperCase().startsWith("NOOP")) {
				//        System.out.println("noop found" + cmd);
			}
			else if (cmd.toUpperCase().indexOf("SYST")>=0) {
				if (!loggedIn) {
					user = cmd.substring(4).trim();
					send(nl,"331 Password required for user " + user);
				}
				else {
					send(nl,"215 UNKNOWN Type");
//215 UNIX Type: L8
				}
			}
			else if (cmd.toUpperCase().indexOf("PASV")>=0) {
				passiveConnect(nl);
			}
			else if (cmd.toUpperCase().startsWith("USER")) {
				//        System.out.println("user found" + cmd);
				if (!loggedIn) {
					user = cmd.substring(4).trim();
					send(nl,"331 Password required for user " + user);
				}
				else {
					send(nl,"500 already logged in command ignored");
				}
			}
			else if (cmd.toUpperCase().startsWith("PWD") || cmd.toUpperCase().startsWith("XPWD")) {
				send(nl,"257 " + cwd + " is current directory.");
			}
			else if (cmd.toUpperCase().startsWith("CWD")) {
				String old = cwd;
				cmd = cmd.substring(3).trim();
				if (cmd.indexOf("~" + "/") >= 0) {
					cwd = MyFtp.getCWD(user) + cmd.substring(cmd.lastIndexOf("~" + "/") + 1);
				}
				else if (cmd.startsWith("/")) {
					cwd = cmd;
				}
				else if (cmd.indexOf(":") > 0) {
					cwd = cmd;
				}
				else {
					cwd += cmd;
				}
				if (cwd.indexOf("..") >= 0) {
					File f = new File(cwd);
					cwd = f.toString();
				}
				if (!cwd.endsWith("/")) {
					cwd += "/";
				}
				File F = new File(cwd);
				if (F.exists() && F.isDirectory()) {
					send(nl,"250 CWD command successful.");
				}
				else {
					send(nl,"550 CWD No such file or directory. " + cwd);
					cwd = old;
				}
			}
			else if (cmd.toUpperCase().startsWith("PASS")) {
				//        System.out.println("pass found" + cmd);
				if (!loggedIn) {
					pass = cmd.substring(4).trim();
					loggedIn = MyFtp.login(user, pass,this);
					if (loggedIn) {
						send(nl,"230 User " + user + " logged in.");
						cwd = MyFtp.getCWD(user);
					}
					else {
						send(nl,"500 login fail bad user or password");
					}
				}
				else {
					send(nl,"500 already logged in command ignored");
				}
			}
			else if (cmd.toUpperCase().startsWith("LIST")) {
				//        System.out.println("list found" + cmd);
				cmd = cmd.substring(4).trim();
				if (passive) 
					MyFtp.send(cwd + cmd, passiveSocket, nl, binary, true);
				else 
					MyFtp.send(cwd + cmd, host, port, nl, binary, true);
			}
			else if (cmd.toUpperCase().startsWith("NLST")) {
				//        System.out.println("nlst found" + cmd);
				cmd = cmd.substring(4).trim();
				if (passive) 
					MyFtp.send(cwd + cmd, passiveSocket, nl, binary, false);
				else 
					MyFtp.send(cwd + cmd, host, port, nl, binary, false);
			}
			else if (cmd.toUpperCase().startsWith("RETR")) {
				//        System.out.println("retr found" + cmd);
				cmd = cmd.substring(4).trim();
				if (cmd.startsWith("/")) {
					if (passive) 
						MyFtp.send(cmd, passiveSocket, nl, binary, false);
					else 
						MyFtp.send(cmd, host, port, nl, binary, false);
					if (MyFtp.log) {
						System.out.println(socket.getInetAddress()+" "+new Date()+":"+user + " RETR " + cmd);
					}
				}
				else {
					if (passive) 
						MyFtp.send(cwd + cmd, passiveSocket, nl, binary, false);
					else 
						MyFtp.send(cwd + cmd, host, port, nl, binary, false);
					if (MyFtp.log) {
						System.out.println(socket.getInetAddress()+" "+new Date()+":"+user + " RETR " + cwd + cmd);
					}
				}
				//download to client
			}
			else if (cmd.toUpperCase().startsWith("STOR")) {
				//        System.out.println("stor found" + cmd);
				cmd = cmd.substring(4).trim();
				if (passive) 
					MyFtp.get(cwd + cmd, passiveSocket, nl, binary);
				else 
					MyFtp.get(cwd + cmd, host, port, nl, binary);
				if (MyFtp.log) {
					System.out.println(socket.getInetAddress()+" "+new Date()+":"+user + " STOR " + cwd + cmd);
				}
				//upload to local
			}
			else if (cmd.toUpperCase().startsWith("PORT")) {
				//        System.out.println("port found" + cmd);
				if (loggedIn) {
					passive=false;
					cmd = cmd.substring(4).trim();
					String t[] = Stuff.getTokens(cmd, ",");
					if (t.length < 6) {
						send(nl,"500 bad PORT command.");
					}
					else {
						try {
							host = t[0] + "." + t[1] + "." + t[2] + "." + t[3];
							port = Integer.parseInt(t[4]) * 256 + Integer.parseInt(t[5]);
							send(nl,"200 PORT command successful.");
						}
						catch (NumberFormatException nfe2) {
							send(nl,"500 bad PORT command.");
						}
					}
				}
				else {
					send(nl,"500 must login first.");
				}
			}
			else if (cmd.toUpperCase().startsWith("TYPE")) {
				cmd = cmd.substring(4).trim();
				if (cmd.equals("A")) {
					binary = false;
					send(nl,"200 Type set to A.");
				}
				else if (cmd.equals("I")) {
					binary = true;
					send(nl,"200 Type set to I.");
				}
				else {
					send(nl,"500 Type unknown.");
				}
			}
			else if (cmd.toUpperCase().startsWith("QUIT")) {
				send(nl,"221 Goodbye.");
				try {
					nl.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				return;
			}
			else {
				send(nl,"500 command (" + cmd + ") not understoood");
			}
		}
	}

	boolean passive=false;
	Socket passiveSocket=null;
	static int tempPortTimeOut=1000;//for pasive connection
	static int sportmax=18292,sportmin=15632;
	static int sport=sportmin;
	public void passiveConnect(Socket nl) {
		passive=true;
		try {if (passiveSocket!=null) passiveSocket.close();} catch (IOException IOE) {}

		ServerSocket SS=null;
		try {
			while (SS==null) {
				try {SS=new ServerSocket(sport);}catch (Exception E) {}
				sport+=(int)(Math.random()*100);
				if (sport>sportmax) sport=sportmin;
			}	
			SS.setSoTimeout(tempPortTimeOut);
			int repport = SS.getLocalPort();
			String host=InetAddress.getLocalHost().getHostAddress();
			String hosts[]=Stuff.getTokens(host,". \t");
			send(nl,"227 Entering Passive Mode ("+hosts[0]+","+hosts[1]+","+hosts[2]+","+hosts[3]+","+(repport/256)+","+(repport%256)+")");
			passiveSocket=SS.accept();
			SS.close();
		} catch (IOException E) {
			passive=false;
			send(nl,"500 PASV connection failed");
		}
	}

}

/*
 * Type: ASCII Non-print, IMAGE, LOCAL 8
 * Mode: Stream
 * Structure: File, Record
 * Commands:  (DONE)
 * (USER), (PASS), ACCT,
 * (PORT), (PASV),
 * TYPE, MODE, STRU,
 * (RETR), (STOR), APPE,
 * RNFR, RNTO, DELE,
 * (CWD),  CDUP, RMD,  MKD,  (PWD),
 * (LIST), (NLST),
 * (SYST), STAT,
 * HELP, (NOOP), (QUIT).
 * todo HELP, MKD, RMD, DELE
 */
