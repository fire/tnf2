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
		String host="",port="";
		if (getParameter("host") != null) {
			host=getParameter("host");
		}
		try {
			if (getParameter("port") != null) {
				port=getParameter("port");
			}
		}
		catch (NumberFormatException NFE) {
		}
			p = new Player();
			p.startThreads(host,port);
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
