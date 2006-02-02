package aj.net;

import java.util.Vector;

import aj.misc.GmlPair;

public class Mimic {
	public static void main(String s[]) {
		System.out.println("Format: java aj.net.Mimic [options]");
		System.out.println("-c<configfile>");
		System.out.println("-h<host>:<port>   begin learning from talk");
		System.out.println("-o<OSname>     OSname to mimic or to learn from");
		new Mimic(s);
	}

	String configFile="mimic.cfg";
	GmlPair mimic=new GmlPair("mimic",new Vector());
	int port;
	String host=null;
	public Mimic(String s[]) {
		for (int a=0;a<s.length;a++) {
			if (s[a].startsWith("-")) {
				s[a]=s[a].substring(1);
				if (s[a].startsWith("h")) {
					if (s[a].indexOf(":")<0) {
						System.out.println("MyError: bad host:port format "+s[a]);
					}
					else {
						host=s[a].substring(1,host.indexOf(":"));
						try {
							port=Integer.parseInt(s[a].substring(s[a].indexOf(":")+1));
						} catch (NumberFormatException NFE) {
							System.out.println("MyError: bad port in host:port "+s[a]);
						}
					}
				}
			}
			else {
				System.out.println("MyError: bad parameter "+s[a]);	
			}
		}
		readConfig();
		if (host==null) {
			doListen();
		}
		else {
			launchServers();
		}
	}

	public void readConfig() {
	}

	public void doListen(){
		//try {
			//open connect
			//read all ->banner
			//read line ->command [ in "line" out "line" ]
			//read all -> out
//state 0,1,2,3 ...
//
		//} catch (IOException ioe) {
	}

	public void launchServers(){
	}
}


//program run
//  host port
//  logs banner
//  logs responds to commands entered by System.in for replay
//  adds logs to config file for target OS

//mimic run
//   read config file
//   attaches to all mimic ports
//   listens and connects from all ports mimics
//   replay based on config file mimic knowlege
