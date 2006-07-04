package aj.gnutella;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyGnutellaServer implements Runnable {

	MyGnutella mg;

	public MyGnutellaServer(MyGnutella mg) {
		this.mg = mg;
	}

	public void run() {
		try {
			ServerSocket ss = new ServerSocket(MyGnutella.serverport);
			System.out.println("Server open on " + MyGnutella.serverport);
			while (true) {
				Socket s = ss.accept();
				GnuProtocol gp = new GnuProtocol(s, mg);

				Thread ttt = new Thread(gp);
				ttt.start();
				mg.connectedList.addElement(gp);
				mg.connectedThreadList.addElement(ttt);

				System.out.println("New remote connect established ");
			}
		} catch (IOException ioe) {
			System.out.println("Server down");
		}
	}
}
