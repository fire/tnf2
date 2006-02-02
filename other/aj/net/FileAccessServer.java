package aj.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import aj.misc.Stuff;

/*
 * This server listens to a port for file write commands.
 * append <file> <line>
 * remove <file> <line>  //all <line> removed
 * remove_one <file> <line> //only first <line> removed
 * Preceding and trailing spaces in <line> and line in file are ignored.
 * Remove is case sensitave.
 * file cannot contain ".." to access parent directory if root is defined.
 */

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class FileAccessServer {
	/**
	 *  Description of the Field 
	 */
	public static String root = "";
	static int port;
	static String options = "";


	/**
	 *  Constructor for the FileAccessServer object 
	 */
	public FileAccessServer() {
		ServerSocket SS = null;
		try {
			SS = new ServerSocket(port);
		}
		catch (IOException IOE) {
			System.out.println("Server port busy!");
			System.exit(0);
		}
		while (true) {
			try {
				final Socket s = SS.accept();
				if (reportSource()) {
					System.out.println("connect from  host:" + s.getInetAddress() + "  on " + (new java.util.Date()).toString());
				}

				new Thread() {
					public void run() {
						BufferedReader br;
						try {
							br = new BufferedReader(new InputStreamReader(s.getInputStream()));
							while(true) {
								String ss;
								try {
									ss = br.readLine();
									if (s==null) break;
									actionPerformed(s,ss);
								} catch (IOException e) {
									e.printStackTrace();
									return;
								}
								}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						}
					}.start();
			}
			catch (IOException IOE8) {
				System.out.println("bad connect to incomming socket " + IOE8);
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public boolean reportAccess() {
		return options.indexOf("A") >= 0;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public boolean reportSource() {
		return options.indexOf("S") >= 0;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public boolean reportFile() {
		return options.indexOf("F") >= 0;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public void actionPerformed(Socket S,String c) {
//		String c = ae.getActionCommand();
		if (c.equalsIgnoreCase("Connection_closed")) {
			return;
		}
		//get file lock?
		if (reportAccess()) {
			System.out.println("access line <" + c + ">");
		}
		String s[] = Stuff.getTokens(c, " \t");
		if (s.length < 3) {
			System.out.println("unknown file action " + c);
			return;
		}
		if (s[1].indexOf("..") >= 0 && !root.equals("")) {
			System.out.println("MyError: attempt to access file in parent!");
			return;
		}
		if (reportFile()) {
			System.out.println("file requested <" + s[1] + "> = " + root + s[1]);
		}
		File f = new File(root + s[1]);
		String line = c.substring(c.indexOf(s[1]) + s[1].length()).trim();
		if (s[0].equalsIgnoreCase("remove") || s[0].equalsIgnoreCase("remove_one")) {
			boolean ONE = s[0].equalsIgnoreCase("remove_one");
			try {
				Vector v = new Vector();
				BufferedReader br = new BufferedReader(new FileReader(f));
				String g = br.readLine();
				boolean found = false;
				//System.out.println("line "+g);
				while (g != null) {
					//System.out.println("line "+g);
					g = g.trim();
					if (g.equals(line) && !found) {
						found = true;
					}
					else if (!g.equals(line) || (g.equals(line) && found && ONE)) {
						v.addElement(g);
					}
					g = br.readLine();
				}
				if (!found) {
					System.out.println("remove not found <" + line + ">");
					return;
				}
				br.close();
				PrintWriter pw = new PrintWriter(new FileWriter(f));
				int a;
				for (a = 0; a < v.size(); a++) {
					pw.println((String) v.elementAt(a));
				}
				pw.close();
			}
			catch (IOException IOE7) {
				System.out.println("remove file access error " + c);
				return;
			}
		}
		else if (s[0].equalsIgnoreCase("append")) {
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				raf.seek(raf.length());
				raf.writeBytes(line + "\n");
				raf.close();
			}
			catch (IOException IOE3) {
				System.out.println("error in append " + IOE3);
			}
		}
		else {
			System.out.println("unknown file action " + c);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		try {
			port = Integer.parseInt(s[0]);
			if (s.length > 1) {
				root = s[1];
			}
			if (s.length == 3) {
				options = s[2].toUpperCase().trim();
			}
			new FileAccessServer();
		}
		catch (Exception E) {
			System.out.println("Format: FileAccessServer <port> [root [-afs]]");
			System.out.println("-s souce of connect");
			System.out.println("-f file accessed");
			System.out.println("-a actual line received");
		}
	}



	/**
	 *  Description of the Method 
	 *
	 *@param  c  Description of Parameter 
	 */
	public static void actionPerformed(String c) {
		//get file lock?
		String s[] = Stuff.getTokens(c, " \t");
		if (s.length < 3) {
			System.out.println("unknown file action " + c);
			return;
		}
		if (s[1].indexOf("..") >= 0 && !root.equals("")) {
			System.out.println("MyError: attempt to access file in parent!");
			return;
		}
		File f = new File(root + s[1]);
		String line = c.substring(c.indexOf(s[1]) + s[1].length()).trim();
		if (s[0].equalsIgnoreCase("remove") || s[0].equalsIgnoreCase("remove_one")) {
			boolean ONE = s[0].equalsIgnoreCase("remove_one");
			try {
				Vector v = new Vector();
				BufferedReader br = new BufferedReader(new FileReader(f));
				//System.out.println("root +s[1]="+root+s[1]);
				String g = br.readLine();
				boolean found = false;
				//System.out.println("line ="+g);
				while (g != null) {
					//System.out.println("line ="+g);
					g = g.trim();
					if (g.equals(line) && !found) {
						found = true;
					}
					else if (!g.equals(line) || (g.equals(line) && found && ONE)) {
						v.addElement(g);
					}
					g = br.readLine();
				}
				if (!found) {
					System.out.println("remove not found <" + line + ">");
					return;
				}
				br.close();
				PrintWriter pw = new PrintWriter(new FileWriter(f));
				int a;
				for (a = 0; a < v.size(); a++) {
					pw.println((String) v.elementAt(a));
				}
				pw.close();
			}
			catch (IOException IOE7) {
				System.out.println("remove file access error " + IOE7 + " line " + c);
				return;
			}
		}
		else if (s[0].equalsIgnoreCase("append")) {
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				raf.seek(raf.length());
				raf.writeBytes(line + "\n");
				raf.close();
			}
			catch (IOException IOE3) {
				System.out.println("error in append " + IOE3);
			}
		}
		else {
			System.out.println("unknown file action " + c);
		}
	}

}
