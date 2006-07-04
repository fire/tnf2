package aj.awt;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Arrow implements DisplayItem {

	private Polygon p;

	private double dx, dy;

	private double fx, fy;

	private Point fp, tp;

	private int wid;

	private int hei;

	private boolean fill = false;

	/**
	 * Constructor for the Arrow object
	 * 
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param x2
	 *            Description of Parameter
	 * @param y2
	 *            Description of Parameter
	 */
	public Arrow(double x, double y, double x2, double y2) {
		Point from = fp = new Point((int) x, (int) y);
		Point to = tp = new Point((int) x2, (int) y2);
		wid = 5;
		hei = 10;
		fill = true;
		dx = from.x - to.x;
		dy = from.y - to.y;
		double len = Math.sqrt(dx * dx + dy * dy);
		dx = dx / len;
		dy = dy / len;
		fx = dy;
		fy = dx;
	}

	/**
	 * Constructor for the Arrow object
	 * 
	 * @param from
	 *            Description of Parameter
	 * @param to
	 *            Description of Parameter
	 * @param wid
	 *            Description of Parameter
	 */
	public Arrow(Point from, Point to, int wid) {
		fp = from;
		tp = to;
		this.wid = wid;
		this.hei = wid;
		dx = from.x - to.x;
		dy = from.y - to.y;
		double len = Math.sqrt(dx * dx + dy * dy);
		dx = dx / len;
		dy = dy / len;
		fx = dy;
		fy = dx;
	}

	/**
	 * Constructor for the Arrow object
	 * 
	 * @param from
	 *            Description of Parameter
	 * @param to
	 *            Description of Parameter
	 * @param wid
	 *            Description of Parameter
	 * @param hei
	 *            Description of Parameter
	 */
	public Arrow(Point from, Point to, int wid, int hei) {
		tp = to;
		fp = from;
		this.wid = wid;
		this.hei = hei;
		dx = from.x - to.x;
		dy = from.y - to.y;
		double len = Math.sqrt(dx * dx + dy * dy);
		dx = dx / len;
		dy = dy / len;
		fx = dy;
		fy = dx;
	}

	/**
	 * Constructor for the Arrow object
	 * 
	 * @param from
	 *            Description of Parameter
	 * @param to
	 *            Description of Parameter
	 * @param wid
	 *            Description of Parameter
	 * @param hei
	 *            Description of Parameter
	 * @param f
	 *            Description of Parameter
	 */
	public Arrow(Point from, Point to, int wid, int hei, boolean f) {
		fill = f;
		tp = to;
		fp = from;
		this.wid = wid;
		this.hei = hei;
		dx = from.x - to.x;
		dy = from.y - to.y;
		double len = Math.sqrt(dx * dx + dy * dy);
		dx = dx / len;
		dy = dy / len;
		fx = dy;
		fy = dx;
	}

	/**
	 * Sets the Fill attribute of the Arrow object
	 * 
	 * @param b
	 *            The new Fill value
	 */
	public void setFill(boolean b) {
		fill = b;
	}

	/**
	 * Sets the Fill attribute of the Arrow object
	 * 
	 * @return Description of the Returned Value
	 */
	public boolean setFill() {
		return fill;
	}

	/**
	 * Sets the X attribute of the Arrow object
	 * 
	 * @param d
	 *            The new X value
	 * @param s
	 *            The new X value
	 */
	public void setX(double d, double s) {
		tp = new Point((int) (d / s), tp.y);
		fp = new Point((int) (fp.x - tp.x + d / s), fp.y);
	}

	/**
	 * Sets the Y attribute of the Arrow object
	 * 
	 * @param d
	 *            The new Y value
	 * @param s
	 *            The new Y value
	 */
	public void setY(double d, double s) {
		tp = new Point(tp.x, (int) (d / s));
		fp = new Point(fp.x, (int) (fp.y - fp.y + d / s));
	}

	/**
	 * Gets the X attribute of the Arrow object
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return The X value
	 */
	public double getX(double s) {
		return tp.x * s;
	}

	/**
	 * Gets the Y attribute of the Arrow object
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return The Y value
	 */
	public double getY(double s) {
		return tp.y * s;
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param s
	 *            Description of Parameter
	 */
	public void display(Graphics g, double s) {
		p = new Polygon();
		int cx1 = (int) (tp.x * s + dx * hei + fx * wid);
		int cy1 = (int) (tp.y * s + dy * hei - fy * wid);
		int cx2 = (int) (tp.x * s + dx * hei - fx * wid);
		int cy2 = (int) (tp.y * s + dy * hei + fy * wid);
		p.addPoint((int) (tp.x * s), (int) (tp.y * s));
		p.addPoint(cx1, cy1);
		p.addPoint(cx2, cy2);
		p.addPoint((int) (s * tp.x), (int) (s * tp.y));
		if (fill) {
			g.fillPolygon(p);
		}
		g.drawPolygon(p);
	}
}
