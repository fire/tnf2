package aj.awt;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

//icon display in JPanel
//minimal icon image display and update image
public class eric {
	Image imageList[] = new Image[2];

	boolean showGui = true;// false;

	ImageIcon imageIcon;

	public static void main(String s[]) {
		eric e = new eric();
		e.imageList[0] = new ImageIcon("pawn.gif").getImage();
		e.imageList[1] = new ImageIcon("queen.gif").getImage();
		e.doUpdates();
	}

	public eric() {
		if (showGui) {
			imageIcon = new ImageIcon("pawn.gif");
			JFrame jframe = new JFrame();
			jframe.getContentPane().add(new JLabel(imageIcon));
			jframe.setSize(new Dimension(400, 600));
			jframe.setVisible(true);
		}
	}

	public void doUpdates() {
		while (true) {
			try {
				Thread.sleep(1000);
				if (imageList[0] != null)
					imageIcon.setImage(imageList[0]);
				Thread.sleep(1000);
				if (imageList[1] != null)
					imageIcon.setImage(imageList[1]);
			} catch (Exception e) {
			}
		}
	}

}
