package aj.combat;

import java.applet.Applet;
import java.awt.BorderLayout;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class PlayerApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Description of the Method
	 */
	public void init() {
		resize(MapView.ARENASIZE, MapView.ARENASIZE);
		setLayout(new BorderLayout());
		Player p;
		String host = "", port = "";
		if (getParameter("host") != null) {
			host = getParameter("host");
		}
		try {
			if (getParameter("port") != null) {
				port = getParameter("port");
			}
		} catch (NumberFormatException NFE) {
		}
		p = new Player();
		Player.serverHostIP = host;
		Player.serverPortVal = port;
		p.startThreads();
		requestFocus();
		add("Center", p.mapView);
	}

	/**
	 * Description of the Method
	 */
	public void start() {
	}
}
