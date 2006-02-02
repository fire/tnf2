package aj.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class LogServer implements Runnable {

	static int port;

	public static void main(String s[]) {
		if (s.length==0) {
			System.out.println("Usage: java aj.net.LogServer <port>");
			return;
		}
		try {
			LogServer.port=Integer.parseInt(s[0]);	
		} catch (NumberFormatException nfe) {
			System.out.println("Usage: java aj.net.LogServer <port>");
			return;
		}
		try {
			ServerSocket ss=new ServerSocket(port);
			while (true) {
				Socket ssss=ss.accept();
				System.out.println("LogServer connected");
				new Thread(new LogServer(ssss)).start();
			}
		} catch (IOException ioe) {
			System.out.println("LOGSERVER: connection lost");
		}
	}

	Socket S;
	public LogServer(Socket ssss) {
		S=ssss;
	}

	public void run() {
		System.out.println("Loggin traffic");
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(S.getInputStream()));
			while (true) {
				String s=br.readLine();
				if (s==null) return;
				System.out.println(s);
			}
		} catch (IOException ioe) {
			System.out.println("LOGSERVER: connection lost");
		}
	}

	
}

