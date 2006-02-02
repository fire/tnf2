package aj.games;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.Socket;

public class RealmAp extends Applet {
	
	public void init() {
		setLayout (new BorderLayout());
		try {
			System.out.println ("code base=" + getCodeBase());
			String host = "" + getCodeBase();
			if (host.indexOf ("://") >= 0)host = host.substring (host.indexOf ("://") + 3).trim();
			if (host.indexOf (":") >= 0)host = host.substring (0, host.indexOf (":")).trim();
			rc = new RealmC (new Socket (host, RealmC.DEFAULTSERVERPORT), "User" + (int) (Math.random() * 100), this);
		}
		catch (IOException ioe) {
			System.out.println ("ioe=" + ioe);
		}
		catch (NumberFormatException nfe) {
			System.out.println ("nfe=" + nfe);
		}
	}
	RealmC rc = null;
	
	public void stop() {
		rc.logout();
	}
	
	public void start() {
		rc.relogin();
	}
}

