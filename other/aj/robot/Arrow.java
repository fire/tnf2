package aj.robot;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Arrow { //implements DisplayItem {
	private Polygon p;
	private double dx, dy;
	private double fx, fy;
	private Point fp, tp;
	private int wid;
	private int hei;
	private boolean fill = false;

	public Arrow (Point from, Point to, int wid, int hei) {
		tp = to;
		fp = from;
		this.wid = wid;
		this.hei = hei;
		dx = from.x - to.x;
		dy = from.y - to.y;
		double len = Math.sqrt (dx * dx + dy * dy);
		if (len==0) len=1;
		dx = dx / len;
		dy = dy / len;
		fx = dy;
		fy = dx;
	}

	public void setFill (boolean b) {
		fill = b;
	}

	public boolean setFill() {
		return fill;
	}

	public void setX (double d, double s) {
		tp = new Point ((int) (d / s), tp.y);
		fp = new Point ((int) (fp.x - tp.x + d / s), fp.y);
	}

	public void setY (double d, double s) {
		tp = new Point (tp.x, (int) (d / s));
		fp = new Point (fp.x, (int) (fp.y - fp.y + d / s));
	}

	public double getX (double s) {
		return tp.x * s;
	}

	public double getY (double s) {
		return tp.y * s;
	}


	public void display (Graphics g, double s) {
		p = new Polygon();
		int cx1 = (int) (tp.x * s + dx * hei + fx * wid);
		int cy1 = (int) (tp.y * s + dy * hei - fy * wid);
		int cx2 = (int) (tp.x * s + dx * hei - fx * wid);
		int cy2 = (int) (tp.y * s + dy * hei + fy * wid);
		p.addPoint ((int) (tp.x * s), (int) (tp.y * s));
		p.addPoint (cx1, cy1);
		p.addPoint (cx2, cy2);
		p.addPoint ((int) (s * tp.x), (int) (s * tp.y));
		if (fill) {
			g.fillPolygon (p);
		}
		g.drawPolygon (p);
	}
}

