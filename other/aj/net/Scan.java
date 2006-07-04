package aj.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Description of the Class
 * 
 * @author judda
 * @created July 21, 2000
 */
public class Scan implements Runnable, ActionListener {

	static int procs = 50;

	boolean done = false;

	String host = "127.0.0.1";

	int min = 0;

	int max = 0;

	static String result = "";

	// static String prob="";

	public Scan() {
	}

	public Scan(String h, int mi, int ma) {
		System.out.println("scan dispatched for " + mi + " to " + ma);
		host = h;
		min = mi;
		max = ma;
	}

	public void actionPerformed(ActionEvent ae) {
		// prob+=ae.getActionCommand()+"\n";
	}

	public void run() {
		int range = max - min;
		if (range < 0)
			return;
		done = false;
		for (int a = min; a <= max; a++) {
			try {
				Socket S = new Socket(host, a);
				if (S == null) {
					throw new IOException();
				} else {
					result += a + ",";
				}
				S.setSoTimeout(500);
				InputStream i = S.getInputStream();
				OutputStream o = S.getOutputStream();
				o.write("\nGET / HTTP/1.1\n\n".getBytes());
				o.flush();
				String ans = "";
				try {
					while (true) {
						int c = i.read();
						if (c == -1)
							break;
						ans += (char) c;
					}
				} catch (IOException IOE) {
				}
				System.out.println("port:" + a + ":" + ans);
				S.close();
			} catch (IOException IOE) {
			}
		}
		done = true;
	}

	public static void main(String s[]) {
		String host;
		int min = 0, max = 100;
		if (s.length != 3) {
			System.out
					.println("Usage: java aj.net.ServerScan <host> <minport> <maxport>");
			System.exit(0);
		}
		host = s[0];
		try {
			min = Integer.parseInt(s[1]);
			max = Integer.parseInt(s[2]);
		} catch (Exception E) {
			System.out.println("Usage: java aj.net.SelfScan minport maxport");
			System.exit(0);
		}

		if (max - min < 0) {
			System.out.println("invalid scan " + min + " to " + max);
			return;
		} else if (max - min == 0) {
			procs = 1;
		} else if (max - min < procs)
			procs = max - min;

		int range = (max - min) / procs + 1;
		Scan sss[] = new Scan[procs];
		for (int pos = 0; pos < procs; pos++) {
			int mymin = min + pos * range;
			int mymax = mymin + range - 1;
			if (mymin > max)
				break;
			if (mymax > max)
				mymax = max;
			sss[pos] = new Scan(host, mymin, mymax);
			new Thread(sss[pos]).start();
		}
		while (true) {
			boolean done = true;
			int a;
			for (a = 0; a < sss.length; a++) {
				if (!sss[a].done)
					done = false;
			}
			if (done)
				break;
			// System.out.println("waiting!");
			try {
				Thread.sleep(1000);
			} catch (Exception E) {
			}
		}
		System.out.println(result);
		// System.out.println(prob);

	}
}
