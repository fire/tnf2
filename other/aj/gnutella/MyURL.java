package aj.gnutella;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import aj.misc.Stuff;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class MyURL {

	static Vector additional = new Vector();

	/**
	 * Sets the Additional attribute of the MyURL class
	 * 
	 * @param v
	 *            The new Additional value
	 */
	public static void setAdditional(Vector v) {
		if (v == null) {
			return;
		}
		additional = v;
	}

	/**
	 * Gets the BufferedReader attribute of the MyURL class
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return The BufferedReader value
	 */
	public static BufferedReader getBufferedReader(String s) {
		try {
			if (!s.toUpperCase().startsWith("HTTP://")) {
				return null;
			}
			s = s.substring("HTTP://".length());
			while (s.indexOf("\\") >= 0) {
				s = s.substring(0, s.indexOf("\\")) + "/"
						+ s.substring(s.indexOf("\\") + 1);
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

			// request
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(ss
					.getOutputStream()));
			pw.println("GET /get" + file + " HTTP/1.0");
			System.out.println("GET /get" + file + "/ HTTP/1.0");
			// pw.println("Accept: image/gif, image/x-xbitmap, image/jpeg,
			// image/pjpeg, image/png, */*");
			// pw.println("Accept-Language: en");
			// pw.println("Accept-Charset: iso-8859-1,*,utf-8");
			if (additional != null && additional.size() > 0) {
				for (int a = 0; a < additional.size(); a++) {
					pw.println(additional.elementAt(a));
					System.out.println(additional.elementAt(a));
				}
			}
			pw.println("");
			pw.flush();
			pw = null;

			/*
			 * GET
			 * http://clubs.lycos.com/auth/live/Directory/Photo.asp?ACT=VOTE&CG=llfae5c88va138400188mohh08&AID=61793&PID=412220
			 * HTTP/1.0 Referer:
			 * http://clubs.lycos.com/auth/live/Directory/Photo.asp?CG=llfae5c88va138400188mohh08&PID=412220
			 * Proxy-Connection: Keep-Alive User-Agent: Mozilla/4.73 [en]
			 * (WinNT; U) Host: clubs.lycos.com Accept-Encoding: gzip
			 * Accept-Language: en Accept-Charset: iso-8859-1,*,utf-8 Cookie:
			 * lycos_sso=xyxyx;
			 * VIPAC=71m34t1lccqn0obic4oj6dje60o30d33cop3gcrdds034c1g64o3ec9h64sj8c00ae053t8j44180qf77l25l9gj1g;
			 * XMLSOURCE=FW
			 */

			// read head
			BufferedReader i = new BufferedReader(new InputStreamReader(ss
					.getInputStream()));
			String ls = i.readLine();
			while (ls != null && !ls.equals("")) {
				if (ls.indexOf("HTTP/") >= 0) {
					String t[] = Stuff.getTokens(ls, " \t");
					if (t.length < 2) {
						return null;
					}
				}
				ls = i.readLine();
			}
			return i;
		} catch (Exception E) {
			System.out.println("MyError in MyURL: " + E);
			return null;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 * @exception IOException
	 *                Description of Exception
	 */
	public static void main(String s[]) throws IOException {
		Vector v = new Vector();
		v.addElement("Connection: Keep-Alive");
		v.addElement("Range: bytes=0-");
		v.addElement("User-Agent: Gnutella");
		setAdditional(v);
		if (s.length < 1) {
			System.out
					.println("FORMAT: java aj.net.MyURL <url> [<outputfile>]");
			System.exit(0);
		}
		BufferedReader br = MyURL.getBufferedReader(s[0]);
		if (br == null) {
			System.out.println("MyError: connect fail!");
		}
		if (s.length == 2) {
			FileOutputStream fo = new FileOutputStream(s[1]);
			int c;
			c = br.read();
			while (c != -1) {
				fo.write((byte) c);
				c = br.read();
			}
		} else {
			String S = br.readLine();
			while (S != null) {
				System.out.println(S);
				S = br.readLine();
			}
		}
	}

}
