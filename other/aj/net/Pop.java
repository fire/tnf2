package aj.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.Date;

import aj.misc.GmlPair;
import aj.misc.Stuff;

/**
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Pop {
	static boolean ECHO=true;//show forwarded program output to stdout
	static boolean DEBUG=false;
	static boolean VERBOSE=false;

	static String activity = null, requireText = null;

	static int count = 1;
	static String defaultFileName = "pop";

	static boolean deleteOkay=true;
	static boolean authenticOnly=false;

	String host, user, pass;
	int port;

	Socket s;
	boolean stop=false;

	public Pop(String config) throws IOException {
		GmlPair g = GmlPair.parse(new File(config));
		GmlPair n[] = g.getAllByName("VERBOSE");
		if (n.length!=0) {
			VERBOSE=n[0].getDouble()!=0;
		}
		n = g.getAllByName("HOST");
		if (n.length != 0) {
			host = n[0].getString();
		}
		if (VERBOSE) System.out.println("host=" + host);
		n = g.getAllByName("PORT");
		if (n.length != 0) {
			port = (int) (n[0].getDouble());
		}
		if (VERBOSE) System.out.println("port=" + port);
		n = g.getAllByName("USER");
		if (n.length != 0) {
			user = n[0].getString();
		}
		if (VERBOSE) System.out.println("user=" + user);
		n = g.getAllByName("PASS");
		if (n.length != 0) {
			pass = n[0].getString();
		}
		if (VERBOSE) System.out.println("pass=" + pass);
		n = g.getAllByName("ACTIVITY");
		if (n.length != 0) {
			activity = n[0].getString();
		}
		if (VERBOSE) System.out.println("activity=" + activity);
		n = g.getAllByName("REQUIRETEXT");
		if (n.length != 0) {
			requireText = n[0].getString();
		}
		n= g.getAllByName("DELETEOKAY");
		if (n.length !=0 ) {
			if (n[0].getString().equalsIgnoreCase("FALSE") || n[0].getString().equalsIgnoreCase("NO") || n[0].getString().equals("0")) deleteOkay=false;
			else deleteOkay=true;
		}
		if (VERBOSE) System.out.println("deleteokay="+deleteOkay);
		if (VERBOSE) System.out.println("making mail connection");
		s = new Socket(host, port);
		BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		String t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
		send(s,"USER " + user);
		t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
		send(s,"PASS " + pass);
		t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
		send(s,"STAT");
		t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
	}
	
	public void send(Socket S,String line) {
		try {
			line+="\n";
			OutputStream o=S.getOutputStream();
			o.write(line.getBytes());
			o.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	public Pop(String h, int p, String u, String pa,boolean au) throws IOException {
		if (VERBOSE) System.out.println("making authentication mail connection");
		host = h;
		port = p;
		user = u;
		pass = pa;
		s = new Socket(host, port);
		BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		String t = br.readLine();
		if (t.startsWith("-ERR")) stop(t);
		send(s,"USER " + user);
		t = br.readLine();
		if (t.startsWith("-ERR")) stop(t);
		send(s,"PASS " + pass);
		t = br.readLine();
		if (VERBOSE) System.out.println("closing mail");
		if (t.startsWith("-ERR")) stop(t);
		send(s,"QUIT");
		t = br.readLine();
		if (t.startsWith("-ERR")) stop(t);
		s.close();
	}

	public Pop(String h, int p, String u, String pa) throws IOException {
		if (VERBOSE) System.out.println("making mail connection");
		host = h;
		port = p;
		user = u;
		pass = pa;
		s = new Socket(host, port);
		BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));

		String t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
		send(s,"USER " + user);
		t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
		send(s,"PASS " + pass);
		t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
		send(s,"STAT");
		t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
	}


	public void process(String line) throws IOException {
		if (stop) return;
		//read stat
		if (VERBOSE) System.out.println("read stat");
		send(s,"STAT");
		BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));

		String t = br.readLine();
		if (VERBOSE) System.out.println("read "+t);
		if (t.startsWith("-ERR")) stop(t);
		String tm[] = Stuff.getTokens(t, " \t");
		if (tm.length > 1) {
			//get number of messages
			int num = 0;
			try {
				num=Integer.parseInt(tm[1]);
			} catch (NumberFormatException nfe) {
				System.out.println("MyError: bad response to STAT.  Received "+t);
				System.exit(0);
			}

			for (int a = 0; a < num; a++) {
				//read message
				String all = readMessage(a + 1);
				if (VERBOSE) System.out.println("message read");
				if (requireText == null || all.toUpperCase().indexOf(requireText.toUpperCase()) < 0) {
					if (VERBOSE) System.out.println("Message doesn't meed required context.  Do not process or delete");
					continue;
				}
				if (deleteOkay) {
					if (VERBOSE) System.out.println("Message being deleted");
					deleteMessage(a + 1);
				}
				else {
					if (VERBOSE) System.out.println("Message saved.  Delete disabled.");
				}
				//process message
				if (line == null) {
					if (VERBOSE) System.out.println("Message dumped to file.");
					makeFile(all);
				}
				else if (line.indexOf("@") < line.indexOf(".") && line.indexOf("@") > 0) {
					if (VERBOSE) System.out.println("Message forwarding to email "+line);
					forwardTo(line, all);
				}
				else {
					if (VERBOSE) System.out.println("Message forwarding to program "+line);
					execute(line, all);
				}
			}
			if (VERBOSE) System.out.println("message done");
		}
	}


	public void makeFile(String s) {
		File f = new File(defaultFileName + "" + count + ".txt");
		while (f.exists()) {
			count++;
			f = new File(defaultFileName + "" + count + ".txt");
		}
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			pw.println(s);
			pw.close();
		}
		catch (IOException IOE) {
			System.out.println("MyError: unknown file writing error" + IOE);
		}
	}

	public void forwardTo(String s, String all) {
		try {
			all = "TO " + s + "\n" + all;
			Send send = new Send();
			send.read(new BufferedReader(new StringReader(all)));
			send.deliver();
		}
		catch (IOException IOE) {
			System.out.println("MyError: cannot forward mail " + all);
		}
	}


	public void execute(String s, String all) {
		String tar = s;
		Runtime t = Runtime.getRuntime();
		try {
			Process p = t.exec(tar);
			PrintWriter o = new PrintWriter(new OutputStreamWriter(p.getOutputStream()));
			while (all.indexOf("\n")>0) {
				o.println(all.substring(0,all.indexOf("\n")));
				all=all.substring(all.indexOf("\n")+1);
			}
			o.println(all);
			o.flush();
			o.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while (true) {
				String tt = br.readLine();
				if (tt == null) {
					break;
				}
				if (ECHO) System.out.println(tt);
			}
			br.close();
		}
		catch (Exception E) {
			System.out.println("MyError: Runtime Error?" + E);
		}
		if (VERBOSE) System.out.println("done execute");

	}


	public void close() throws IOException {
		if (VERBOSE) System.out.println("closing mail");
		send(s,"QUIT");
		BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));

		String t = br.readLine();
		if (VERBOSE) System.out.println(t);
		if (t.startsWith("-ERR")) stop(t);
		s.close();
	}

	public void stop(String ss) throws IOException {
		System.out.println("MyError: error received "+ss);
		stop=true;
		s.close();
	}


	public String readMessage(int a) throws IOException {
		if (VERBOSE) System.out.println("receiving message " + a);
		String all = "";
		send(s,"RETR " + a);
		BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));

		String t = br.readLine();
		if (VERBOSE) System.out.println(t);
		boolean done = false;
		while (!done) {
			t = br.readLine();
			if (t == null) {
				break;
			}
			if (t.equals(".")) {
				break;
			}
			all += t + "\n";
		}
		return all;
	}


	public void deleteMessage(int a) throws IOException {
		if (VERBOSE) System.out.println("deleting message " + a);
		send(s,"DELE " + a);
		BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		String t = br.readLine();
		if (VERBOSE) System.out.println(t);
	}


	public static void main(String s[]) {
		if (s.length != 5 && s.length != 4 && s.length != 1) {
			System.out.println("FORMAT: java aj.net.Pop <host> <port> <user> <pass> [<command>]");
			System.out.println("FORMAT: java aj.net.Pop <pop.cfg file>");
			System.out.println("   command = email or program else makes files");
			System.exit(0);
		}
System.out.println("Pop running on "+new Date());
		Pop p = null;
		try {
			if (s.length != 1) {
				p = new Pop(s[0], Integer.parseInt(s[1]), s[2], s[3]);
			}
			else {
				p = new Pop(s[0]);
			}
		}
		catch (IOException IOE) {
			if (s.length>=4)
				System.out.println("MyError: unable to connect host (" + s[0] + ") port (" + s[1] + ") user (" + s[2] + ") pass (" + s[3] + ")");
			else 
				System.out.println("MyError: unable to connect host defined in file " + s[0] );
			System.out.println("FORMAT: java aj.net.Pop <host> <port> <user> <pass> [<command>]");
			System.out.println("FORMAT: java aj.net.Pop <pop.cfg file>");
			System.out.println("   command = email or program else makes files");
			System.exit(0);
		}
		catch (NumberFormatException NFE) {
			System.out.println("MyError: bad port number (" + s[1] + ")");
			System.out.println("FORMAT: java aj.net.Pop <host> <port> <user> <pass> [<command>]");
			System.out.println("   command = email or program else makes files");
			System.exit(0);
		}
		activity = (s.length == 5 ? s[4] : activity);
		try {
			p.process(activity);
			p.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Pop done "+new Date());
		System.out.println("");
	}
}

