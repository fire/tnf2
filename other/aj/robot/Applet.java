package aj.robot;

import javax.swing.JApplet;
import javax.swing.JPanel;

public class Applet extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() {
		Arena arena = new Arena();
		// arena.readFile(sss[0]);
		JPanel jp = arena.setUpGUI();
		getContentPane().add("Center", jp);
		new Thread(arena).start();
	}
}
