package aj.nf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;

/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class Main {
	static boolean DEBUG=false;
	static boolean TIMING=false;

	public static String SYSTEMEMAIL;
	public static String SYSTEMCOMMAND;
	public static String NONGAMEEMAIL;
	public static String MESSAGE;

	static boolean nomail = false;

	static String DIRDATA="../data/";
	static String DIRORDERS=DIRDATA+"orders/";
	static String DIRREPORTS=DIRDATA+"reports/";
	static String DIRBODIES=DIRDATA+"planets/";
	static String noticeFileName = "notice.txt";
	static String auctionFileName = "DataAuction.gml";
	static String marketFileName = "DataMarket.gml";
	static String bodyDataFileName = "DataBodies.gml";
	static String NFObjectDataFileName = "DataNFObjects.gml";
	static String ITThingDataFileName = "DataITThings.gml";
	static String corpDataFileName = "DataCorps.gml";
	static String universeDataFileName = "Config.gml";
	//static String emailConfigFileName = "EmailConfig.gml";

	public static void setDataDir(String s) {
		DIRDATA=s;
		DIRORDERS=DIRDATA+"orders/";
		DIRREPORTS=DIRDATA+"reports/";
		DIRBODIES=DIRDATA+"planets/";
	}


	//vars needed for Processing orders and replying
	Vector reply = new Vector();
	String replyTo = null;
	String login = null;

	Universe universe = new Universe();


	public Main() {
		if (DEBUG) System.out.println("Init Main object");
		try {
			System.out.println("parse "+Main.DIRDATA+Main.universeDataFileName);
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA+Main.universeDataFileName));
			//GmlPair g = GmlPair.parse(new File(Main.DIRDATA+emailConfigFileName));
			GmlPair n[] = g.getAllByName("SYSTEMCOMMAND");
			if (n.length > 0) {
				SYSTEMCOMMAND = n[0].getString();
			}
			n = g.getAllByName("SYSTEMEMAIL");
			if (n.length > 0) {
				SYSTEMEMAIL = n[0].getString();
			}
			n = g.getAllByName("NONGAMEEMAIL");
			if (n.length > 0) {
				NONGAMEEMAIL = n[0].getString();
			}
			n = g.getAllByName("MESSAGE");
			if (n.length > 0) {
				MESSAGE = n[0].getString();
			}
			if (DEBUG) System.out.println("done parse");
		}
		catch (IOException IOE) {
			System.out.println("MyError: cannot configure E-mail");
			System.exit(0);
		}
	}


	public void executeTurn() {
		if (DEBUG) System.out.println("executing turn "+new Date());
		Universe.load();
		logTiming("LOAD universe done");
		Universe.executeCorpCommands();
		logTiming("executing corpcommands done");
		Universe.executeProgramables();
		logTiming("executing programables done");
		Universe.save();
		logTiming("SAVE universe done");
		logTiming("clear orders done");
		Universe.makeTurnReports();
		logTiming("build turn reports done");
		Universe.sendTurnReports();
		logTiming("sending turn reports done");
		logTiming("all done finished");
		logTiming(null);
	}


	public void processOrders() {
		//System.out.println("Processing order "+new Date());
		String t[] = readLines();
		if (login==null){
			reply.addElement("Unable to find @LOGIN or @END");
			send(reply, replyTo, "Unable to login");
			return;
		}
		String tt[]=Stuff.getTokens(login);
		String cname=null;
		String cpass=null;
		String type=null;
//@login SIGNUP cname pass
		if (tt.length>0) type=tt[0].toUpperCase();//type
		if (tt.length>1) cname=tt[1];//corp name
		if (tt.length>2) cpass=tt[2];//pass
		System.out.println("type ="+type+" cname="+cname+" pass="+cpass);
		if (cname==null || type==null) {
			reply.addElement("Use the following format");
			reply.addElement("@LOGIN SIGNUP NEW");
			reply.addElement("@LOGIN ORDERS <corpname> <password>");
			reply.addElement("@LOGIN REPORT <corpname> <password>");
			reply.addElement("@LOGIN UPDATEREPORT <corpname> <password>");
			reply.addElement("@LOGIN MAIL <corpname> <password>");
			reply.addElement("@LOGIN LASTORDERS <corpname> <password>");
			send(reply, replyTo, "mail Error.");
			return;
		}
		if (!type.startsWith("SIGN") && !type.startsWith("ORD") && 
			!type.startsWith("LAS") && !type.startsWith("REP") && 
			!type.startsWith("MAI") && !type.startsWith("UPD")) {
			reply.addElement("Use the following format");
			reply.addElement("@LOGIN SIGNUP NEW");
			reply.addElement("@LOGIN ORDERS <corpname> <password>");
			reply.addElement("@LOGIN REPORT <corpname> <password>");
			reply.addElement("@LOGIN UPDATEREPORT <corpname> <password>");
			reply.addElement("@LOGIN MAIL <corpname> <password>");
			reply.addElement("@LOGIN LASTORDERS <corpname> <password>");
			send(reply, replyTo, "mail Error.");
			return;
		}
		if (tt.length==2 && !tt[0].toUpperCase().startsWith("SIGN")) {
			reply.addElement("Use the following format");
			reply.addElement("@LOGIN SIGNUP NEW");
			reply.addElement("@LOGIN ORDERS <corpname> <password>");
			reply.addElement("@LOGIN REPORT <corpname> <password>");
			reply.addElement("@LOGIN UPDATEREPORT <corpname> <password>");
			reply.addElement("@LOGIN MAIL <corpname> <password>");
			reply.addElement("@LOGIN LASTORDERS <corpname> <password>");
			send(reply, replyTo, "mail Error.");
			return;
		}
		if (tt.length==3 && tt[0].toUpperCase().startsWith("SIGN")) {
			reply.addElement("Use the following format");
			reply.addElement("@LOGIN SIGNUP NEW");
			reply.addElement("@LOGIN ORDERS <corpname> <password>");
			reply.addElement("@LOGIN REPORT <corpname> <password>");
			reply.addElement("@LOGIN UPDATEREPORT <corpname> <password>");
			reply.addElement("@LOGIN MAIL <corpname> <password>");
			reply.addElement("@LOGIN LASTORDERS <corpname> <password>");
			send(reply, replyTo, "mail Error.");
			return;
		}
		Universe.load();

		Corp corp = Universe.loginCorp(cname,cpass);
		if (type.startsWith("SIGN") && cname.equalsIgnoreCase("NEW")) {
			System.out.println("Signup found");
			String ans = Universe.newCorp(replyTo, t);
			if (ans.toUpperCase().indexOf("COMPLETE")>=0) {
				reply.addElement(ans);
				send(reply, replyTo, "Signup receipt");
				System.out.println("Signup good");
			}
			else {
				reply.addElement("Signup error.\n" + ans);
				send(reply, replyTo, "Signup error");
				System.out.println("Signup bad");
			}
		}
		else if (corp == null) {
			System.out.println("Bad login");
			reply.addElement("unable to login: " + login);
			send(reply, replyTo, "Login error");
		}
		else if (type.startsWith("UPD")) {
			System.out.println("Good login update turn requested");
			corp.makeTurnReport(false);
			corp.sendTurnReport();
			reply.addElement("Updated turnreport sent to "+replyTo);
			send(reply, replyTo, "Updated Turnreport and resent");
		}
		else if (type.startsWith("ORD")) {
			if (t.length<1) {
				System.out.println("Good orders login");
				reply.addElement("No orders found");
				send(reply, replyTo, "Orders error");
				return;
			}
			System.out.println("Good login orders");
			boolean prog=false;
			for (int a = 0; a < t.length; a++) {
				if (prog) {
					if (t[a].trim().toUpperCase().startsWith("END")) {
						prog=false;
						reply.addElement(t[a]);
						continue;
					}
					reply.addElement(Active.validCommand(t[a]));
				}
				else{
					reply.addElement(corp.validOrder(t[a]));
					if (t[a].trim().toUpperCase().startsWith("PROGRAM")) {
						prog=true;
					}
				}
			}
			corp.saveOrders(t);
			send(reply, replyTo, "Orders receipt");
		}
		else if (type.startsWith("LAS")) {
			System.out.println("Good login last order requested");
			corp.sendLastOrders(replyTo);
			reply.addElement("last orders sent to "+replyTo);
			send(reply, replyTo, "last orders resent");
		}
		else if (type.startsWith("REP")) {
			System.out.println("Good login report requested");
			corp.sendTurnReport(replyTo);
			reply.addElement("Turn Report resent to "+replyTo);
			send(reply, replyTo, "Resend Turn receipt");
		}
		else if (type.startsWith("MAI")) {
			System.out.println("Good login mail requested");
			String result=blindMail(corp,t);
			reply.addElement(result);
			send(reply, replyTo, "Mail receipt");
		}
		else {
			System.out.println("Good login unknown request");
			reply.addElement("Unkown message type "+type);
			send(reply, replyTo, "Unknown type receipt");
		}
	}

	public String blindMail(Corp c,String all[]) {
		Vector mail=new Vector();
		String mailto=null;
		String subject="";
		Corp tar=null;
		if (all.length<2) {
			return "Cannot read mail destination or body.";
		}
		for (int a=0;a<all.length;a++) {
			if (all[a].toUpperCase().trim().startsWith("TO:")) {
				mailto=all[a].trim().substring(3).trim();
				tar=Universe.getCorpByTick(mailto);
			}
			else if (all[a].toUpperCase().trim().startsWith("SUBJECT:")) {
				subject=all[a].trim().substring(8).trim();
			}
		}
		if (mailto==null) {
			return "Cannot find line TO: <corpid>";
		}
		if (tar==null) {
			return "Cannot find corporation with matching id of "+mailto;
		}
		for (int a=0;a<all.length;a++) {
			if (!all[a].toUpperCase().trim().startsWith("TO:") && !all[a].toUpperCase().trim().startsWith("SUBJECT:")) {
				mail.addElement(all[a]);
			}
		}
		sendBlind(mail,tar.email,tar.getName()+" ("+tar.getTick()+")",c.getName()+" ("+c.getTick()+")",subject);
		return "Mail sent to "+tar.getTick()+" "+mail.size()+" lines";
	}

	public String[] readLines() {
		boolean started = false;
		boolean stopped = false;
		Vector v = new Vector();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String s = br.readLine();
				if (s == null) {
					break;
				}
				System.out.println("REC>"+s);
				if (!started && s.trim().toUpperCase().startsWith("FROM:")) {
					replyTo = s.substring(5).trim();
					if (replyTo.indexOf("<") >= 0 && replyTo.indexOf(">") > replyTo.indexOf("<")){
						 replyTo = replyTo.substring (replyTo.indexOf ("<") + 1, replyTo.indexOf (">"));
					}
					if (replyTo!=null && 
					(replyTo.indexOf("@")<0 || replyTo.lastIndexOf(".")<=replyTo.indexOf("@"))) {
						System.out.println("MyError: Bad from email found:"+replyTo);
						replyTo=null;
					}
					if (replyTo!=null && replyTo.toUpperCase().indexOf(SYSTEMEMAIL.toUpperCase())>=0) {
						System.out.println("MyError:  Email loop found");
						replyTo=null;
					}
				}
				if (stopped) {
					continue;
				}
				if (s.toUpperCase().trim().startsWith("@END")) {
					stopped = true;
				}
				if (started && !stopped) {
					v.addElement(s);
				}
				else if (login==null && s.toUpperCase().trim().startsWith("@LOGIN") && !stopped) {
					login = s.substring(s.toUpperCase().indexOf("@LOGIN") + 6).trim();
					started = true;
				}
			}
			System.out.println("REC>end");
		}
		catch (IOException IOE) {
			System.out.println("MyError: Cannot read email");
			System.exit(0);
		}
		if (!stopped) {
			return new String[0];
		}
		String t[] = new String[v.size()];
		v.copyInto(t);
		return t;
	}

	boolean readingProg=false;
	public String validOrder(Corp c, String s) {
		String dis=s;
		s = s.trim();
		if (s.startsWith("#") || s.startsWith(";") || s.length()==0) {
			return dis;
		}  
		else if (readingProg || s.toUpperCase().startsWith("PROGRAM ")) {
			readingProg=true;
			if (s.toUpperCase().equals("END")) {
			}
			return dis;
		}
		else if (
			s.toUpperCase().equals("NOOP") || s.toUpperCase().equals("QUIT") ||
			s.toUpperCase().startsWith("RESET ") || s.toUpperCase().startsWith("INIT ") ||
			s.toUpperCase().startsWith("SETLOG ") ||
			s.toUpperCase().startsWith("SETTECH ") || 
			s.toUpperCase().startsWith("UPLOAD ") || s.toUpperCase().startsWith("BUY ") ||
			s.toUpperCase().startsWith("SELL ") || s.toUpperCase().startsWith("CONSIGN ") ||
			s.toUpperCase().startsWith("BID ") || s.toUpperCase().startsWith("DEFINE ") ||
			s.toUpperCase().startsWith("LEASE ") || s.toUpperCase().startsWith("PARTNER ") ||
			s.toUpperCase().startsWith("GIVE ") || 
			s.toUpperCase().startsWith("PRODUCE ") || s.toUpperCase().startsWith("SETMAIL ") || 
			s.toUpperCase().startsWith("PURCHASE ") ||s.toUpperCase().startsWith("RENAME ") ||
			s.toUpperCase().startsWith("SETPASSWORD ") || s.toUpperCase().startsWith("RESEARCH ") || 
			s.toUpperCase().startsWith("PAY ") || s.toUpperCase().startsWith("NICK ") ||
			s.toUpperCase().startsWith("DESIGN ")  || s.toUpperCase().startsWith("RELEASE")) {
			return dis;
		}
		else {
			return ";***INVALID ORDER <" + s + ">";
		}
	}


	public static void main(String s[]) {
		String help = "FORMAT: Main ExecuteTurn [nomail] [debug] [data<dir>]\n" + 
			      "FORMAT: Main ProcessOrders [nomail]  [debug] [data<dir>]<inputstream";
		if (s.length == 0) {
			System.out.println(help);
			System.exit(0);
		}
		String action = "none";
		for (int a = 0; a < s.length; a++) {
			if (s[a].startsWith("-")) s[a]=s[a].substring(1);
			if (s[a].toUpperCase().startsWith("EXEC")) {
				action = "ExecuteTurn";
			}
			if (s[a].toUpperCase().startsWith("DATA")) {
				setDataDir(s[a].substring(4));
				System.out.println("DIRDATA = "+DIRDATA);
			}
			if (s[a].toUpperCase().startsWith("PROC")) {
				action = "ProcessOrders";
			}
			if (s[a].toUpperCase().indexOf("NOMAIL") >= 0) {
				Main.nomail = true;
			}
			if (s[a].toUpperCase().indexOf("TIMING") >= 0) {
				System.out.println("TIMING ON");
				Main.TIMING = true;
				logTiming(null);
			}
			if (s[a].toUpperCase().indexOf("DEBUG") >= 0) {
				System.out.println("DEBUG ON");
				Main.DEBUG = true;
			}
			if (s[a].toUpperCase().indexOf("?") >= 0) {
				System.out.println(help);
				System.exit(0);
			}
		}
		if (action.equalsIgnoreCase("NONE")) {
			System.out.println(help);
			System.exit(0);
		}
		else if (action.equalsIgnoreCase("ExecuteTurn")) {
			System.out.println("Running Turn on "+new Date());
			Main m = new Main();
			m.executeTurn();
			System.out.println("done Running Turn on "+new Date());
			System.out.println("");
		}
		else if (action.equalsIgnoreCase("ProcessOrders")) {
			System.out.println("Processing orders on "+new Date());
			Main m = new Main();
			m.processOrders();
			System.out.println("done "+new Date());
			System.out.println("");
			System.out.println("Done processing orders on "+new Date());
		}
	}


	public static void sendBlind(Vector v, String toEmail, String toCorp, String fromCorp, String subject) {
		if (toEmail==null) {
                        System.out.println("MyError: Unable to send message no reply found");
                        System.out.println("ERR>"+subject);
                        for (int a = 0; a < v.size(); a++) {
                                System.out.println("ERR>"+ v.elementAt(a));
                        }
                        return;
                }
if (DEBUG) System.out.println("Sending via blind mailer to "+toEmail);
                StringBuffer all =new StringBuffer();
                all .append( "Sender: "+ NONGAMEEMAIL+ "\n");
                all .append( "Reply-To: "+ SYSTEMEMAIL + "\n");
                all .append( "From: \"New Frontiers Mail\"<" + SYSTEMEMAIL + ">\n");
                all .append( "To:  " + toEmail + "\n");
                all .append( "Subject: TNF2 Blind mail. "+subject+" \n");
                all .append( "\n");
		all .append( "To Corp: "+toCorp+" \n");
		all .append( "From Corp: "+fromCorp+" \n");
		if (subject.length()>0)
                	all .append( "Subject: "+subject+" \n");

                for (int a = 0; a < v.size(); a++) {
                        all .append( v.elementAt(a).toString() + "\n");
                }
		all .append( "\n");
//real mail below here
		sendFormatedMail(all.toString());
	}



	public static void send(Vector v, String replyTo, String subject) {
		if (replyTo==null) {
			System.out.println("MyError: Unable to send message no reply found");
			System.out.println(subject);
			for (int a = 0; a < v.size(); a++) {
                                System.out.println("ERR>"+ v.elementAt(a));
			}
			return;
		}
if (DEBUG) System.out.println("Sending orders mailer to "+replyTo);
		StringBuffer all=new StringBuffer();
                all .append( "Sender: "+ NONGAMEEMAIL+ "\n");
                all .append( "Reply-To: "+ SYSTEMEMAIL + "\n");
                all .append( "From: \"New Frontiers Mail\"<" + SYSTEMEMAIL + ">\n");
		all .append( "To:  " + replyTo + "\n");
		all .append( "Subject: TNF2 " + subject + " \n");
		all .append( "\n");
		all .append( "Mail Orders to " + SYSTEMEMAIL + "\n");
		all .append( "To report Bugs or non-orders mail to " + NONGAMEEMAIL + "\n");
		all .append( MESSAGE + "\n");
		all .append( "-----------------------\n");

		for (int a = 0; a < v.size(); a++) {
			all .append( v.elementAt(a).toString() + "\n");
		}
		all .append( "\n");
		sendFormatedMail(all.toString());
	}

	public static void sendFormatedMail(String all) {
		if (nomail) {
			System.out.println("NOMAIL:");
			System.out.println(all);
			return;
		}
		String tar=SYSTEMCOMMAND;
		if (DEBUG) System.out.println("piping mail through "+tar);

                Runtime t = Runtime.getRuntime();
                try {
                        Process p = t.exec(tar);
			if (DEBUG) System.out.println("process open");
                        PrintWriter o = new PrintWriter(new OutputStreamWriter(p.getOutputStream()));
			if (DEBUG) System.out.print("writing.");
                        o.println(all);
                        System.out.println(all);
			if (DEBUG) System.out.println("flushing");
                        o.flush();
			if (DEBUG) System.out.println("closing");
                        o.close();
			if (DEBUG) System.out.println("done");
			p.waitFor();
                }
                catch (Exception E) {
                        System.out.println("MyError: Runtime Error?" + E);
			System.exit(0);
                }
	}

	public static long ttime=-1;//time between events
	public static long tttime=-1;//time from start to end
	public static void logTiming(String s) {
		if (ttime==-1) {
			tttime=ttime=System.currentTimeMillis();
			if (Main.TIMING) System.out.println("TIMING: begin "+(System.currentTimeMillis()-tttime)+" at "+new Date());
			return;
		}
		if (Main.TIMING) System.out.println("TIMING: "+(System.currentTimeMillis()-ttime)+" "+s);
		if (Main.TIMING && s==null) System.out.println("TIMING: end "+(System.currentTimeMillis()-tttime)+" at "+new Date());
		ttime=System.currentTimeMillis();
	}

}
