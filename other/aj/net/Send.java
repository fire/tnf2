package aj.net;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import aj.misc.Stuff;

/**
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Send {
	public boolean verbose = false;

	boolean STDIN = false;
	boolean bad = false;
	long time=0;
	int priority=3;//1=high,2=moderate,3=normal,4=low,5=lowest

	Vector message, body;
	//6/20/2000
	String pophost = null;
	String popname = null;
	String poppass = null;
	int popport=110;
	int toport=25;

	String tohost = null;
	//connect host
	String touser = null;
	//connect receipt
	String to = null;
	//message receipt <default connect>
	String fromhost = null;
	//connect from host for hello to tohost
	String fromuser = null;
	//connect sender name
	String from = null;
	//message sender name <default fromuser and fromhost>
	String subject = null;
	//subject
	String USAGE = "Usuage: java aj.net.Send [<file_name>] [-verbose]\n" + 
			"   <file_name> file must have a TO: <email_address> line\n" + 
			"   ?, -? /? \\? help = this information\n" + 
			"HEADER INPUT LINES:\n" + 
			"   *POP: user:pass@host:port\n" + 
			"   **FROMUSER: sending users. authorized for sending on TOHOST machine used in MAIL FROM: command.\n" + 
			"   **FROMHOST: host claimed to be connection from in HELO command TOHOST.\n" + 
			"   *FROM: name to use in message (also default FROMUSER and FROMHOST).\n" + 
			"   *FROM: <name>@<host> name to use in message (also default FROMUSER and FROMHOST).\n" + 
			"   **TOHOST: host to make socket connection to.\n" + 
			"   **TOHOST: <host>:<port> host to make socket connection to with port .\n" + 
			"   **TOUSER: actual delievery user name.  Used in RCPT command.\n" + 
			"   TO: name to use in message (also default TOUSER and TOHOST).\n" + 
			"   *TIME: <long>  timestamp "+System.currentTimeMillis()+"="+new Date()+".\n"+
			"   *PRIORITY: <1-5>  1==high 5=lowest, 3=normal.\n"+
			"   *SUBJECT:\n"+
			"   *=optional, **=optional with default from pervious entry\n"+
			"   (Default FROM=anonymous@nowhere.net";
	private String SendVersion = "1.2.1";


	public Send() {
	}



	public void read(BufferedReader BR) {
		if (STDIN) {
			System.out.println(USAGE);
			System.out.println("End with . or ^D");
		}
		message = new Vector();
		body = new Vector();
		try {
			String t = BR.readLine();
			boolean readingHeader = true;
			while (t != null && readingHeader) {
				t = t.trim();
				if (t.toUpperCase().startsWith("POP")) {
//*POP: user:pass@host:port
					t = t.substring(t.indexOf("POP") + 3);
					t = t.trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					popname=t;
					if (popname.indexOf("@")>0) {
						pophost=popname.substring(popname.indexOf("@")+1);
						popname=popname.substring(0,popname.indexOf("@"));
					}
					if (popname.indexOf(":")>0) {
						poppass=popname.substring(popname.indexOf(":")+1);
						popname=popname.substring(0,popname.indexOf(":"));
					}
					if (pophost!=null && pophost.indexOf(":")>0) {
						try {	
							popport=Integer.parseInt(pophost.substring(pophost.indexOf(":")+1));
						} catch (NumberFormatException NFE) {}
						pophost=pophost.substring(0,pophost.indexOf(":"));
					}

				}
				else if (t.toUpperCase().startsWith("FROMHOST") || t.toUpperCase().startsWith("FROM HOST")) {
					t = t.substring(t.toUpperCase().indexOf("HOST") + 4);
					t = t.trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					fromhost = t;
				}
				else if (t.toUpperCase().startsWith("FROMUSER") || t.toUpperCase().startsWith("FROM USER")) {
					t = t.substring(t.toUpperCase().indexOf("USER") + 4);
					t = t.trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					fromuser = t;
				}
				else if (t.toUpperCase().startsWith("FROM")) {
					t = t.substring(t.toUpperCase().indexOf("FROM") + 4);
					t = t.trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					from = t;
				}
				else if (t.toUpperCase().startsWith("TOHOST") || t.toUpperCase().startsWith("TO HOST")) {
					t = t.substring(t.toUpperCase().indexOf("HOST") + 4);
					t = t.trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					tohost = t;
					if (tohost.indexOf(":")>0) {
						try {	
							toport=Integer.parseInt(tohost.substring(tohost.indexOf(":")+1));
						} catch (NumberFormatException NFE) {}
						tohost=tohost.substring(0,tohost.indexOf(":"));
					}
				}
				else if (t.toUpperCase().startsWith("TOUSER") || t.toUpperCase().startsWith("TO USER")) {
					t = t.substring(t.toUpperCase().indexOf("USER") + 4);
					t = t.trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					touser = t;
				}
				else if (t.toUpperCase().startsWith("TO")) {
					t = t.substring(t.toUpperCase().indexOf("TO") + 2);
					t = t.trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					to = t;
				}
				else if (t.toUpperCase().startsWith("SUBJECT")) {
					t = t.substring(t.toUpperCase().indexOf("SUBJECT") + 7);
					t = t.trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					subject = t;
				}
				else if (t.toUpperCase().startsWith("PRIORITY")) {
					t=t.substring(8).trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					try {
						priority=Integer.parseInt(t);
					} catch (NumberFormatException NFE) {}
				}
				else if (t.toUpperCase().startsWith("TIME")) {
					t=t.substring(4).trim();
					if (t.startsWith(":")) {
						t = t.substring(1).trim();
					}
					if (t.equals("")) {
						continue;
					}
					try {
						time=Long.parseLong(t);
					} catch (NumberFormatException NFE) {
						time=0;
					}
				}
				else {
					readingHeader = false;
					body.addElement(t);
				}
				t = BR.readLine();
			}
			while (t != null) {
				if (t.equals(".")) {
					break;
				}
				body.addElement(t);
				t = BR.readLine();
			}
			if (fromhost == null) {
				if (from != null && from.indexOf("@") > 0) {
					fromhost = from.substring(from.indexOf("@") + 1);
				}
				else {
					fromhost = InetAddress.getLocalHost().getHostName();
				}
			}
			if (fromuser == null) {
				if (from!=null && from.indexOf("@") > 0) {
					fromuser = from;
				}
				else {
					fromuser="anonymous@nowhere.net";
				}
			}
			if (from == null) {
				from = "anonymous@nowhere.net";
			}
			if (to == null && touser == null) {
				bad = true;
				return;
			}
			if (tohost == null) {
				if (touser != null && touser.indexOf("@") > 0) {
					tohost = touser.substring(touser.indexOf("@") + 1).trim();
				}
				else if (to != null && to.indexOf("@") > 0) {
					tohost = to.substring(to.indexOf("@") + 1).trim();
				}
				else {
					bad = true;
					return;
				}
			}
			if (touser == null && to != null) {
				touser = to;
			}
			if (to == null) {
				to = "";
			}

			message.addElement("HELO " + fromhost);
			message.addElement("RSET");
			message.addElement("MAIL FROM: " + fromuser);//old was fromuser
			message.addElement("RCPT TO: " + touser);//old was touser
			message.addElement("DATA");

			Calendar calendar = new GregorianCalendar();
			Date trialTime = new Date();
			if (time!=0) trialTime=new Date(time);
			calendar.setTime(trialTime);
			String myDate = "" + (calendar.get(Calendar.YEAR));
			String tt = (calendar.get(Calendar.MONTH) + 1) + "";
			if (tt.length() < 2) {
				tt = "0" + tt;
			}
			myDate += tt;
			tt = (calendar.get(Calendar.DAY_OF_MONTH)) + "";
			if (tt.length() < 2) {
				tt = "0" + tt;
			}
			myDate += tt;
			tt = (calendar.get(Calendar.HOUR_OF_DAY)) + "";
			if (tt.length() < 2) {
				tt = "0" + tt;
			}
			myDate += tt;
			tt = (calendar.get(Calendar.MINUTE)) + "";
			if (tt.length() < 2) {
				tt = "0" + tt;
			}
			myDate += tt;
			tt = (calendar.get(Calendar.SECOND)) + "";
			if (tt.length() < 2) {
				tt = "0" + tt;
			}
			myDate += tt;
			String num = ((int) (Math.random() * 100000000)) + "";
			while (num.length() < 8) {
				num = "0" + num;
			}
			String localhost = InetAddress.getLocalHost().getHostAddress();
			message.addElement("Message-Id: <" + SendVersion + "." + myDate + "." + num + "@" + localhost + ">");

			message.addElement("X-Sender: " + from);
			message.addElement("X-Mailer: aj.net.Send for GalaxyChart version " + SendVersion);

			String ls[] = Stuff.getTokens(new java.util.Date() + "", " ");
			if (time!=0) ls= Stuff.getTokens(new java.util.Date(time) + "", " ");
			String tz = "" + (calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000) * 100);
			String ld = ls[0] + ", " + ls[2] + " " + ls[1] + " " + ls[5] + " " + ls[3] + " " + tz + " (" + ls[4] + ")";
			message.addElement("Date: " + ld);
			message.addElement("To: " + to);
			//To: batch@ns2.dtai.com
			message.addElement("From: " + from);
			//From: Aaron Judd <judda@spawar.navy.mil>
			if (subject != null) {
				message.addElement("Subject: " + subject);
			}
			if (priority!=3) {
				if (priority==1) {
					message.addElement("Importance: High");
					message.addElement("X-Priority: 1 ");
					message.addElement("X-MSMail-Priority: High");
				}
				if (priority==2) {
					message.addElement("Importance: Moderate");
					message.addElement("X-Priority: 2 ");
					message.addElement("X-MSMail-Priority: Moderate");
				}
				if (priority==4) {
					message.addElement("Importance: Low");
					message.addElement("X-Priority: 4 ");
					message.addElement("X-MSMail-Priority: Low");
				}
				if (priority==5) {
					message.addElement("Importance: Lowest");
					message.addElement("X-Priority: 5 ");
					message.addElement("X-MSMail-Priority: Lowest");
				}

			}
			message.addElement("Mime-Version: 1.0");
			message.addElement("Content-Type: text/plain; charset=\"us-ascii\"");
			message.addElement("");
			for (int a = 0; a < body.size(); a++) {
				message.addElement(body.elementAt(a));
			}
			message.addElement(".");
			message.addElement("QUIT");
		}
		catch (IOException IE) {
			System.out.println("Bad file read unable to delever");
			bad = true;
		}
	}


	/**
	 *  This method actually processes the delevery. It opens the socket and sends 
	 *  the message. 
	 *
	 *@exception  IOException  when any delivery error has occured. This includes 
	 *      bad to address and bad to hosts. 
	 */
	public void deliver() throws IOException {
		if (bad) {
			throw new IOException("Bad FROM or TO header info.");
		}
		if (pophost!=null && popname!=null && poppass!=null) {
			if (verbose) {
				System.out.println("Pop authenticating " + pophost + " at port "+popport+" user "+popname+" and pass "+poppass);
			}
			new Pop(pophost,popport,popname,poppass,true);
		}
		if (verbose) {
			System.out.println("connect to " + tohost + " at port "+toport);
		}
		Socket S = new Socket(tohost, toport);
		OutputStream PR=S.getOutputStream();
		//PrintWriter PR = new PrintWriter(S.getOutputStream(),true);
		BufferedReader BR = new BufferedReader(new InputStreamReader(S.getInputStream()));
		int a;
		if (verbose) {
			System.out.println("SMTP>>" + BR.readLine());
		}
		for (a = 0; a < message.size(); a++) {
			String sss=(String)message.elementAt(a);
			PR.write((sss+"\r\n").getBytes());
			PR.flush();
			//PR.println(sss);
			if (verbose) {
				System.out.println("SENT<<" + sss);
			}
			if (verbose && (message.elementAt(a).toString().startsWith("HELO") || 
					message.elementAt(a).toString().startsWith("RSET") || 
					message.elementAt(a).toString().startsWith("MAIL") || 
					message.elementAt(a).toString().startsWith("RCPT") || 
					message.elementAt(a).toString().startsWith("DATA") || 
					message.elementAt(a).toString().equals(".") || 
					message.elementAt(a).toString().startsWith("QUIT")) ) {
				String s = BR.readLine();
				if (s==null) {
					throw new IOException("connection closed early at: "+sss);
				}
				if (s!=null && s.trim().startsWith("5")) {
					if (verbose) {
						System.out.println("RECV>>" + s);
					}
					throw new IOException("protocol error:"+s);
				}
				if (verbose) {
					System.out.println("RECV>>" + s);
				}
			}
		}
		while (BR.ready()) {
			String s = BR.readLine();
			if (s==null) break;
			if (verbose) {
				System.out.println("EX_RECV>>" + s);
			}
			if (s.trim().startsWith("5")) {
				throw new IOException("protocol error:"+s);
			}
		}
		PR.close();
	}



	public String[] checkOptions(String s[]) {
		Vector v = new Vector();
		int a;
		for (a = 0; a < s.length; a++) {
			if (s[a].toUpperCase().startsWith("-V")) {
				verbose = true;
			}
			else if (s[a].indexOf("?") >= 0 || s[a].equalsIgnoreCase("HELP")) {
				System.out.println(USAGE);
				System.exit(0);
			}
			else {
				v.addElement(s[a]);
			}
		}
		s = new String[v.size()];
		for (a = 0; a < v.size(); a++) {
			s[a] = (String) v.elementAt(a);
		}
		return s;
	}


	public static void main(String s[]) {
		Send IT = new Send();
		IT.checkOptions(s);
		if (s.length == 0) {
			IT.STDIN = true;
			IT.read(new BufferedReader(new InputStreamReader(System.in)));
		}
		else {
			try {
				IT.read(new BufferedReader(new FileReader(s[0])));
			}
			catch (IOException IOE) {
				System.out.println("Delever error: canceled bad file.");
			}
		}
		try {
			IT.deliver();
			System.out.println("Delivery Successful.");
		}
		catch (IOException IEO3) {
			System.out.println("Deliver error: canceled."+IEO3);
		}
	}

}

/*
 * notes
 * sendln("HELO "+localHostName);
 * sendln("MAIL FROM:"+msg.getSource());
 * sendln("RCPT TO:"+msg.getDestination());
 * sendln("DATA");
 * sendln("Subject: "+msg.getSubject());
 * sendln(".");
 * sendln("QUIT");
 */
