package aj.awt.my3d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

/**
 * Description of the Class
 * 
 * @author judda
 * @created July 21, 2000
 */
public class Poly3d {
	Vector point3dList;

	Color color = Color.gray;

	/**
	 * Constructor for the Poly3d object
	 * 
	 * @param p
	 *            Description of Parameter
	 * @param c
	 *            Description of Parameter
	 */
	public Poly3d(Vector p, Color c) {
		point3dList = p;
		color = c;
	}

	/**
	 * Constructor for the Poly3d object
	 * 
	 * @param p
	 *            Description of Parameter
	 */
	public Poly3d(Poly3d p) {
		point3dList = p.point3dList;
		color = p.color;
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param c
	 *            Description of Parameter
	 */
	public void display(Graphics g, My3DCanvas c) {
		// System.out.println("draw poly "+point3dList.size()+" lines");
		for (int a = 0; a < 2; a++) {
			// point3dList.size()-1;a++) {
			Point3d one = (Point3d) point3dList.elementAt(a);
			Point3d two = (Point3d) point3dList.elementAt(a + 1);
			draw3dLine(g, c, one, two);
		}
		if (point3dList.size() > 0) {
			Point3d one = (Point3d) point3dList.elementAt(0);
			Point3d two = (Point3d) point3dList
					.elementAt(point3dList.size() - 1);
			draw3dLine(g, c, one, two);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 * @param c
	 *            Description of Parameter
	 * @param one
	 *            Description of Parameter
	 * @param two
	 *            Description of Parameter
	 */
	public void draw3dLine(Graphics g, My3DCanvas c, Point3d one, Point3d two) {
		// System.out.println("draw line "+one.point+" , "+two.point);
		int dx = (int) (one.point.val[0] - two.point.val[0]);
		int dy = (int) (one.point.val[1] - two.point.val[1]);
		int dz = (int) (one.point.val[2] - two.point.val[2]);
		int step = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
		Point3d w = new Point3d(two);
		c.drawPoint(g, w, color);
		for (int a = 0; a < step; a++) {
			Point3d t = Point3d.trans(w, 1.0 * dx / step * a, 1.0 * dy / step
					* a, 1.0 * dz / step * a);
			c.drawPoint(g, t, color);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param m
	 *            Description of Parameter
	 * @param p
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Poly3d apply(Matrix m, Poly3d p) {
		Vector v = new Vector();
		for (int a = 0; a < p.point3dList.size(); a++) {
			Point3d pp = (Point3d) p.point3dList.elementAt(a);
			v.addElement(Point3d.apply(m, pp));
		}
		return new Poly3d(v, p.color);
	}

}
