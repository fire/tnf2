package aj.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

public class Neb extends Canvas implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Color backcolor = Color.black;

	int screenSize = 100;

	int step = 4;

	int ops = 10000;

	boolean symV = false, symH = false;

	Random R = null;

	public static void main(String s[]) {
		new Neb(s);
	}

	public Neb(String s[]) {
		addMouseListener(this);
		try {
			if (s.length == 0) {
				System.out
						.println("FORMAT: java aj.awt.Neb <size> <step> <ops> <1=v 2=h 3=vh 0=non> <seed>");
			}
			if (s.length > 0)
				screenSize = Integer.parseInt(s[0]);
			if (s.length > 1)
				step = Math.max(Integer.parseInt(s[1]), 2);
			if (s.length > 2)
				ops = Integer.parseInt(s[2]);
			if (s.length > 3) {
				symV = (Integer.parseInt(s[3]) & 1) == 1;
				symH = (Integer.parseInt(s[3]) & 2) == 2;
			}
			if (s.length > 4)
				R = new Random(Integer.parseInt(s[4]));
			if (s[4].equals("0")) {
				int c = (int) (Math.random() * Integer.MAX_VALUE);
				System.out.println("using seed:" + c);
				R = new Random(c);
			}
		} catch (Exception e) {
			System.out
					.println("FORMAT: java aj.awt.Neb <size> <step> <ops> <1=v 2=h 3=vh 0=non> <seed>");
		}
		setBackground(backcolor);
		Frame f = new Frame();
		f.add("Center", this);
		f.setSize(new Dimension(screenSize, screenSize));
		f.setVisible(true);
		f.addWindowListener(new SimpleWindowManager());
	}

	Image i = null;

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (i == null) {
			i = createImage(screenSize, screenSize);
			Graphics G = i.getGraphics();
			G.clearRect(0, 0, screenSize, screenSize);
			int r = (int) (100 + 155 * Math.random());
			int gg = (int) (100 + 155 * Math.random());
			int b = (int) (100 + 155 * Math.random());
			Color c = new Color(r, gg, b);
			build(G, c, screenSize, step, ops, symV, symH, R);
		}
		g.drawImage(i, 0, 0, this);
	}

	public static void build(Graphics G, Color c, int ss, int step, int ops,
			boolean symV, boolean symH, Random R) {
		int x = ss / 2, y = ss / 2;
		for (int a = 0; a < ops; a++) {
			G.setColor(c);
			G.drawLine(x, y, x, y);
			if (symV) {
				G.drawLine(ss - x, y, ss - x, y);
				if (symH) {
					G.drawLine(ss - x, ss - y, ss - x, ss - y);
				}
			}
			if (symH) {
				G.drawLine(x, ss - y, x, ss - y);
			}
			double n1 = (R != null ? R.nextDouble() : Math.random()), n2 = (R != null ? R
					.nextDouble()
					: Math.random());
			x = x + (int) ((n1 * 2 - 1) * step);
			y = y + (int) ((n2 * 2 - 1) * step);
			if (x > ss)
				x = ss - x;
			if (y > ss)
				y = ss - y;
			if (x < 0)
				y = ss + x;
			if (y < 0)
				y = ss + y;
		}
	}

	public void mouseClicked(MouseEvent e) {
		int c = (int) (Math.random() * Integer.MAX_VALUE);
		System.out.println("using seed:" + c);
		R = new Random(c);
		i = null;
		repaint();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
