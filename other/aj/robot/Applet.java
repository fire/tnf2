package aj.robot;

import javax.swing.JApplet;
import javax.swing.JPanel;

public class Applet extends JApplet {
	public void init() {
		Arena arena=new Arena();
		//arena.readFile(sss[0]);
		JPanel jp=arena.setUpGUI();
		getContentPane().add("Center",jp);
		new Thread(arena).start();
	}
}
