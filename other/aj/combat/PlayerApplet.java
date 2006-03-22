package aj.combat;

import java.applet.Applet;
import java.awt.BorderLayout;


/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class PlayerApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *  Description of the Method 
	 */
	public void init() {
		resize(Player.ZONESIZE, Player.ZONESIZE);
		setLayout(new BorderLayout());
		Player p;
		if (getParameter("host") != null) {
		}
		try {
			if (getParameter("port") != null) {
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
