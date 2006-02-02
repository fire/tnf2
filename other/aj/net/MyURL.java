package aj.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import aj.misc.Stuff;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class MyURL {

	static Vector additional = new Vector();


	/**
	 *  Sets the Additional attribute of the MyURL class 
	 *
	 *@param  v  The new Additional value 
	 */
	public static void setAdditional(Vector v) {
		if (v == null) {
			return;
		}
		additional = v;
	}

	/**
	 *  Gets the InputStream attribute of the MyURL class 
	 *
	 *@param  s  the URL to hock up to.  Must be an HTTP connection
	 *@return    The InputStream value 
	 */
	public static InputStream getInputStream(String s) {
		try {
			if (!s.toUpperCase().startsWith("HTTP://")) {
				return null;
			}
			s = s.substring("HTTP://".length());
			while (s.indexOf("\\") >= 0) {
				s = s.substring(0, s.indexOf("\\")) + "/" + s.substring(s.indexOf("\\") + 1);
			}
			if (s.indexOf("/") < 0) {
				return null;
			}
			String host = s.substring(0, s.indexOf("/"));
			String file = s.substring(s.indexOf("/"));
			int port = 80;
			if (host.indexOf(":") > 0) {
				port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
				host = host.substring(0, host.indexOf(":"));
			}
			Socket ss = new Socket(host, port);

			//request
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(ss.getOutputStream()));
			pw.println("GET " + file + " HTTP/1.0");
			if (additional != null && additional.size() > 0) {
				for (int a = 0; a < additional.size(); a++) {
					pw.println(additional.elementAt(a));
				}
			}
			pw.println("");
			pw.flush();
			pw = null;
			//read head
			InputStream i = ss.getInputStream();
			String ls = myReadLine(i);
			while (ls != null && !ls.trim().equals("")) {
				if (ls.indexOf("HTTP/") >= 0) {
					String t[] = Stuff.getTokens(ls, " \t");
					if (t.length < 2) {
						return null;
					}
				}
				ls = myReadLine(i);
			}
			return i;
		}
		catch (Exception E) {
			System.out.println("MyError in MyURL: " + E);
			return null;
		}
	}
	
	public static String myReadLine(InputStream ii) throws IOException{
		String res="";
		boolean valid=false;
		while (true) {
			int c=ii.read();
			if (c==-1 && valid) return res;
			if (c==-1 && !valid) return null;
			valid=true;
			if (c=='\n') return res;
			res+=(char)c;
		}
	}

	public static void main(String s[]) throws IOException {
		if (s.length < 1) {
			System.out.println("FORMAT: java aj.net.MyURL <url> [<outputfile>]");
			System.exit(0);
		}
		InputStream br = MyURL.getInputStream(s[0]);
		if (br == null) {
			System.out.println("MyError: connect fail!");
		}
		if (s.length == 2) {
			FileOutputStream fo = new FileOutputStream(s[1]);
			int c;
			c = br.read();
			while (c != -1) {
				fo.write((byte)c);
				c = br.read();
			}
		}
		else {
			int c;
			c = br.read();
			while (c != -1) {
				System.out.write((byte)c);
				c = br.read();
			}
		}
	}

}
