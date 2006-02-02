package aj.combat;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.io.IOException;


/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class PlayerApplet extends Applet {
	/**
	 *  Description of the Method 
	 */
	public void init() {
		resize(Player.ZONESIZE, Player.ZONESIZE);
		setLayout(new BorderLayout());
		Player p;
		String host = "neuron.spawar.navy.mil";
		int port = 5000;
		if (getParameter("host") != null) {
			host = getParameter("host");
		}
		try {
			if (getParameter("port") != null) {
				port = Integer.parseInt(getParameter("port"));
			}
		}
		catch (NumberFormatException NFE) {
		}
			p = new Player();
			requestFocus();
			p.addKeyListener(p);
			addKeyListener(p);
			add("Center", p);
	}


	/**
	 *  Description of the Method 
	 */
	public void start() {
	}
}
