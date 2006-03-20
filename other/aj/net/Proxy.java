/*
 * Created on Oct 29, 2005
 *
 
 */
package aj.net;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author judda
 *
 */
public class Proxy {

	public void showHelp() {
		System.out.println("Format aj.net.Proxy [options]");
		System.out.println("  -localport #");
		System.out.println("  -remoteport #");
		System.out.println("  -targethost ip");
		System.out.println("  -clientSaveFile name   default system.in");
		System.out.println("  -serverSaveFile name  default system.out");
		System.out.println("  -debug true/false");
	}
	
	public static void main(String[] args)  {
		try {
			new Proxy(args).start();
		} catch (Exception ioe) {
			ioe.printStackTrace();					
		}
	}

	int localPort=-1,remotePort=-1;
	String targetHost=null;
	OutputStream fromClient,fromServer;
	Socket clientSocket,serverSocket;
	InputStream ci,si;
	OutputStream co,so;

	boolean debug=true;

	boolean isValid() {
		if (debug) System.out.println("localPort="+localPort);
		if (debug) System.out.println("remotePort="+remotePort);
		if (debug) System.out.println("targetHost="+targetHost);
		return localPort>0 && localPort<65536 && remotePort>0 && remotePort<65536 && targetHost!=null;
	}
	
	void setDebug(String s) {
		if (s.toUpperCase().indexOf("YES")>=0) debug=true;
		else if (s.toUpperCase().indexOf("NO")>=0) debug=false;
		else if (s.toUpperCase().indexOf("ON")>=0) debug=true;
		else if (s.toUpperCase().indexOf("OFF")>=0) debug=false;
		else if (s.toUpperCase().indexOf("1")>=0) debug=true;
		else if (s.toUpperCase().indexOf("0")>=0) debug=false;
		else if (s.toUpperCase().indexOf("TRUE")>=0) debug=true;
		else if (s.toUpperCase().indexOf("FALSE")>=0) debug=false;
		else debug=false;
	}
	void setLocalPort(String s) {
		localPort=Integer.parseInt(s);
	}
	void setRemotePort(String s) {
		remotePort=Integer.parseInt(s);
	}
	void setTargetHost(String s) {
		targetHost=s;
	}
	void setClientStream(String fileName) throws FileNotFoundException {
		setClientStream(new FileOutputStream(fileName));
	}
	void setClientStream(OutputStream i) {
		fromClient=i;
	}
	void setServerStream(String fileName) throws FileNotFoundException {
		setServerStream(new FileOutputStream(fileName));
	}
	void setServerStream(OutputStream i) {
		fromServer=i;
	}
	
	public void applyArgs(String args[]) throws FileNotFoundException  {
		for (int a=0;a<args.length;a++) {
			if (args[a].toUpperCase().startsWith("-D")) {
				if (a<args.length-1) setDebug(args[a+1]);
				else setDebug("TRUE");
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("--")) {
				showHelp();System.exit(0);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-HELP")) {
				showHelp();System.exit(0);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-?")) {
				showHelp();System.exit(0);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-L")) {
				setLocalPort(args[a+1]);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-R")) {
				setRemotePort(args[a+1]);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-T")) {
				setTargetHost(args[a+1]);
			}
		}
		setClientStream(System.out);
		setServerStream(System.out);
		for (int a=0;a<args.length;a++) {
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-C")) {
				setClientStream(args[a+1]);
			}
			if (a<args.length-1 && args[a].toUpperCase().startsWith("-S")) {
				setServerStream(args[a+1]);
			}
		}
	}
	
	public Proxy() {}
	public Proxy(String args[]) {
		if (args.length==0) showHelp();
		try {
			applyArgs(args);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	void closeSocket() {
		if (debug) System.out.println("closing sockets");
		try {
			clientSocket.close();
			serverSocket.close();
		}catch (IOException ioe) {
			ioe.printStackTrace();					
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void start()  {
		if (!isValid()) {
			System.out.println("Invalid Settings");
			System.exit(0);
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
			clientSocket=ss.accept();
			if (debug) System.out.println("new client connect found at port "+localPort);
			ci=clientSocket.getInputStream();
			co=clientSocket.getOutputStream();
			if (debug) System.out.println("make remote connection to "+targetHost+" at port "+remotePort);
			serverSocket=new Socket(targetHost,remotePort);
			si=serverSocket.getInputStream();
			so=serverSocket.getOutputStream();
			if (debug) System.out.println("streams setup");
			new Thread() {
				public void run() {
					try {
						while (true) {
							byte b[]=new byte[10000];
							int n=ci.read(b);
							if (n==-1) break;
							fromClient.write(b,0,n);
							fromClient.flush();
							so.write(b,0,n);
							so.flush();
						}
						fromClient.close();
						closeSocket();
					} catch (Exception ioe) {
						ioe.printStackTrace();					
					}
					closeSocket();
				}
			}.start();
			new Thread() {
				public void run() {
					try {
						while (true) {
							byte b[]=new byte[10000];
							int n= si.read(b);
							if (n==-1) break;
							fromServer.write(b,0,n);
							fromServer.flush();
							co.write(b,0,n);
							co.flush();
						}
						closeSocket();
						co.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					closeSocket();
				}
			}.start();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
