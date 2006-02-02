package aj.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *  Description of the Class 
 *
 *  Telnet type client.   Sends all input to server.  Displays all output from server.
 * 
 *@author     judda 
 *@created    April 12, 2000 
 */
public class PortTalk  {


	/**
	 *  Constructor for the PortTalk object 
	 *
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public PortTalk(String host,int port) throws UnknownHostException, IOException {
		Socket s=new Socket(host,port);
		final BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		new Thread(){
			public void run() {
					String line="";
					while(true) {
						try {
							line = br.readLine();
							if (line==null) break;
							System.out.println(line);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
			}}.start();
		BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));
		OutputStream os=s.getOutputStream();
		try {
			while (true) {
				String ss = BR.readLine();
				if (ss == null) break;
				ss+="\n";
				os.write(ss.getBytes());os.flush();
			}
		}
		catch (IOException IOE) {
		}
	}



	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		if (s.length==0) {
			System.out.println("FORMAT: java aj.net.PortTalk <host> <port>");
			System.exit(0);
		}
		try {
			String h = s[0];
			int p = Integer.parseInt(s[1]);
			new PortTalk(h,p);
		}
		catch (NumberFormatException FNE) {
			System.out.println("MyError: bad port number");
			System.exit(0);
		}
		catch (IOException E) {
			System.out.println("MyError: unable to open port");
			System.exit(0);
		}
	}

}
