/*
 * Created on Feb 2, 2006
 *
 */
package aj.man;

import javax.swing.*;
import java.awt.*;


/**
 * @author judda
 *
 */
public class ManMain extends JPanel {

	public static void main(String[] args) {
		JFrame jf=new JFrame();
		ManMain mm=new ManMain();
		jf.getContentPane().add("Center",mm);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(new Dimension(400,400));
		jf.setVisible(true);
	}
	
	public void paint(Graphics g) {
		Man2 m=new Man2();
		g.translate(100,100);
		m.draw(g);
	}

}
/*
 *  * world -
 *   floor
 *   latter
 *   door
 * 
 */
