package aj.testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
public class Rose extends Frame implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int count = 0;

	/**
	 * Description of the Method
	 * 
	 * @param e
	 *            Description of Parameter
	 */
	public void keyPressed(KeyEvent e) {
		count++;
		if (count > 100) {
			count = 0;
			repaint();
		}
		e.consume();
		Graphics g = getGraphics();
		Dimension d = getSize();
		int x = myrand(d.width);
		int y = myrand(d.height);
		int maxsize = Math.min(d.width, d.height) / 4;
		int size = myrand(maxsize) + maxsize;
		g.setColor(new Color(myrand(255), myrand(255), myrand(255)));
		switch (myrand(6)) {
		case 0:
			drawSquare(g, x, y, size);
			break;
		case 1:
			drawRect(g, x, y, size);
			break;
		case 2:
			drawCircle(g, x, y, size);
			break;
		case 3:
			drawTriangle(g, x, y, size);
			break;
		case 4:
			drawLetter(g, x, y, size);
			break;
		case 5:
			drawNumber(g, x, y, size);
			break;
		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param e
	 *            Description of Parameter
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Description of the Method
	 * 
	 * @param e
	 *            Description of Parameter
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Description of the Method
	 * 
	 * @param d
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public int myrand(int d) {
		return (int) (Math.random() * d);
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param size
	 *            Description of Parameter
	 */
	public void drawSquare(Graphics g, int x, int y, int size) {
		g.fillRect(x - size / 2, y - size / 2, size, size);
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param size
	 *            Description of Parameter
	 */
	public void drawRect(Graphics g, int x, int y, int size) {
		if (myrand(2) == 0) {
			g.fillRect(x - size / 2, y - size / 2, size, size / 2);
		} else {
			g.fillRect(x - size / 2, y - size / 2, size / 2, size);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param size
	 *            Description of Parameter
	 */
	public void drawCircle(Graphics g, int x, int y, int size) {
		g.fillOval(x - size / 2, y - size / 2, size, size);
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param size
	 *            Description of Parameter
	 */
	public void drawLetter(Graphics g, int x, int y, int size) {
		Font F = g.getFont();
		Font Fnew = new Font(F.getName(), Font.BOLD, 12);
		g.setFont(Fnew);
		int h = g.getFontMetrics().getHeight() - 2;
		Fnew = new Font(F.getName(), Font.BOLD, (int) (12 * (1.0 * size / h)));
		g.setFont(Fnew);
		String c = "" + (char) (myrand(26) + 'A');
		g.drawString(c, x, y);
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param size
	 *            Description of Parameter
	 */
	public void drawNumber(Graphics g, int x, int y, int size) {
		Font F = g.getFont();
		Font Fnew = new Font(F.getName(), Font.BOLD, 12);
		g.setFont(Fnew);
		int h = g.getFontMetrics().getHeight() - 2;
		Fnew = new Font(F.getName(), Font.BOLD, (int) (12 * (1.0 * size / h)));
		g.setFont(Fnew);
		String c = "" + (char) (myrand(10) + '0');
		g.drawString(c, x, y);
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param size
	 *            Description of Parameter
	 */
	public void drawTriangle(Graphics g, int x, int y, int size) {
		Polygon p = new Polygon();
		p.addPoint(0, -8);
		p.addPoint(10, 8);
		p.addPoint(-10, 8);
		p = scale(p, 1.0 * size / 20);
		p = trans(p, x, y);
		g.fillPolygon(p);
	}

	/**
	 * Description of the Method
	 * 
	 * @param p
	 *            Description of Parameter
	 * @param s
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Polygon scale(Polygon p, double s) {
		for (int a = 0; a < p.npoints; a++) {
			p.xpoints[a] = (int) (p.xpoints[a] * s);
			p.ypoints[a] = (int) (p.ypoints[a] * s);
		}
		return p;
	}

	/**
	 * Description of the Method
	 * 
	 * @param p
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Polygon trans(Polygon p, int x, int y) {
		for (int a = 0; a < p.npoints; a++) {
			p.xpoints[a] += x;
			p.ypoints[a] += y;
		}
		return p;
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param size
	 *            Description of Parameter
	 */
	public void drawDiamond(Graphics g, int x, int y, int size) {
		Polygon p = new Polygon();
		p.addPoint(0, 1);
		p.addPoint(1, 0);
		p.addPoint(0, -1);
		p.addPoint(-1, 0);
		p.addPoint(0, 1);
		p = scale(p, size / 2);
		p = trans(p, x, y);
		g.fillPolygon(p);
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param size
	 *            Description of Parameter
	 */
	public void drawStar(Graphics g, int x, int y, int size) {
		Polygon p = new Polygon();
		for (int a = 1; a < 6; a++) {
			int xx2 = (int) (-Math.sin(144 * a * Math.PI / 180) * 20);
			int yy2 = (int) (+Math.cos(144 * a * Math.PI / 180) * 20);
			p.addPoint(xx2, yy2);
		}
		p = scale(p, size / 20);
		p = trans(p, x, y);
		g.fillPolygon(p);
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		Rose r = new Rose();
		r.setVisible(true);
		r.setSize(100, 100);
		r.addKeyListener(r);
		r.addWindowListener(new aj.awt.SimpleWindowManager());
	}
}
