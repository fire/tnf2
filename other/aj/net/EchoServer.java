package aj.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 *updated 10/29/2005
 */
public class EchoServer {
	boolean debug=false;
	boolean silent=false;
	boolean client=false;
	int localPort=8080;
	
	public void showHelp() {
		System.out.println("Format aj.net.EchoServer [options]");
		System.out.println("  -localport #");
		System.out.println("  -silent  <on/off>   ;echo to client");
		System.out.println("  -console <on/off>   ;echo to console");
		System.out.println("  -debug true/false");
	}

	void setDebug(boolean b) {debug=b;}
	boolean getDebug() {return debug;}
	void setDebug(String s) {
		if (s.toUpperCase().indexOf("YES")>=0) setDebug(true);
		else if (s.toUpperCase().indexOf("NO")>=0) setDebug(false);
		else if (s.toUpperCase().indexOf("ON")>=0) setDebug(true);
		else if (s.toUpperCase().indexOf("OFF")>=0) setDebug(false);
		else if (s.toUpperCase().indexOf("1")>=0) setDebug(true);
		else if (s.toUpperCase().indexOf("0")>=0) setDebug(false);
		else if (s.toUpperCase().indexOf("TRUE")>=0) setDebug(true);
		else if (s.toUpperCase().indexOf("FALSE")>=0) setDebug(false);
		else debug=false;
	}

	void setClient(boolean b) {client=b;}
	boolean getClient() {return client;}
	void setClient(String s) {
		if (s.toUpperCase().indexOf("YES")>=0) setClient(true);
		else if (s.toUpperCase().indexOf("NO")>=0) setClient(false);
		else if (s.toUpperCase().indexOf("ON")>=0) setClient(true);
		else if (s.toUpperCase().indexOf("OFF")>=0) setClient(false);
		else if (s.toUpperCase().indexOf("1")>=0) setClient(true);
		else if (s.toUpperCase().indexOf("0")>=0) setClient(false);
		else if (s.toUpperCase().indexOf("TRUE")>=0) setClient(true);
		else if (s.toUpperCase().indexOf("FALSE")>=0) setClient(false);
		else client=false;
	}

	void setSilent(boolean b) {silent=b;}
	boolean getSilent() {return silent;}
	void setSilent(String s) {
		if (s.toUpperCase().indexOf("YES")>=0) setSilent(true);
		else if (s.toUpperCase().indexOf("NO")>=0) setSilent(false);
		else if (s.toUpperCase().indexOf("1")>=0) setSilent(true);
		else if (s.toUpperCase().indexOf("0")>=0) setSilent(false);
		else if (s.toUpperCase().indexOf("ON")>=0) setSilent(true);
		else if (s.toUpperCase().indexOf("OFF")>=0) setSilent(false);
		else if (s.toUpperCase().indexOf("TRUE")>=0) setSilent(true);
		else if (s.toUpperCase().indexOf("FALSE")>=0) setSilent(false);
		else silent=false;
	}

	void setLocalPort(int x) {localPort=x;}
	int getLocalPort() {return localPort;}
	void setLocalPort(String s) {
		setLocalPort(Integer.parseInt(s));
	}

	public void applyArgs(String args[]) {
		for (int a=0;a<args.length;a++) {
			if (args[a].toUpperCase().startsWith("-D")) {
				if (a<args.length-1) setDebug(args[a+1]);
				else setDebug("TRUE");
			}
			if (a<args.length && args[a].toUpperCase().startsWith("--") ||
					a<args.length && args[a].toUpperCase().startsWith("-HELP") ||
					a<args.length && args[a].toUpperCase().startsWith("-?")) {
				showHelp();System.exit(0);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-L")) {
				setLocalPort(args[a+1]);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-C")) {
				setClient(args[a+1]);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-S")) {
				setSilent(args[a+1]);
			}
		}
	}
	

	boolean isValid() {
		if (debug) System.out.println("localPort="+localPort);
		return localPort>0 && localPort<65536;
	}
	
	public void start() {
		if (!isValid()) {
			System.out.println("Invalid Settings");
		}
		ServerSocket ss=null;
		try {
			ss = new ServerSocket(localPort);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		while (true) {
			try {
				Socket s=ss.accept();
				if (debug) System.out.println("New client connect found at port "+localPort);
				InputStream i=s.getInputStream();
				OutputStream o=s.getOutputStream();
				while (true) {
					byte b[]=new byte[10000];
					int n= i.read(b);
					if (n==-1) break;
					if (!silent) {
						o.write(b,0,n);
						o.flush();
					}
					if (client) {
						System.out.write(b,0,n);
						System.out.flush();
					}
				}
				s.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
		}
	}

	public static void main(String args[]) {
		EchoServer e=new EchoServer(args);
		e.start();
	}
	
	public EchoServer(){};
	public EchoServer(String args[]) {
		applyArgs(args);
	}
}