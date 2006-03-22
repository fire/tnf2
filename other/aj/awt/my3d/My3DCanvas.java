package aj.awt.my3d;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import aj.awt.SimpleWindowManager;
import aj.misc.GmlPair;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class My3DCanvas extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//direction of point light
	MyVector lightSource = new MyVector(0.6, 0.2, 0.7);
	//  #color of source light (where applicapal)
	Color sourceLightColor = new Color(210, 210, 50);
	// #point ambiant reflect?
	Color ambiantLightColor = new Color(210, 50, 0);
	// #view point
	MyVector viewPoint = new MyVector(0, 0, 0);
	// #view ref point
	MyVector viewRefPoint = new MyVector(0, 0, 2);
	MyVector upRef = new MyVector(0, 1, 0);

	Vector allPoly = new Vector();
	double zbuff[][] = new double[size][size];
	static int size = 400;


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void paint(Graphics g) {
		MyVector vp = viewPoint;
		//=new MyVector(0, 0, -20);
		MyVector vrp = viewRefPoint;
		//=new MyVector(0, 0, -10);
		MyVector up = upRef;
		//=new MyVector(0,1,0);
		Matrix m = new Matrix();
		//generate transform matrix
		System.out.println(m);
		m = Matrix.trans(m, MyVector.scale(vp, -1.0));
		//move to view point
		System.out.println("move to view point" + vp + "\n" + m);
		MyVector v1;
		MyVector v2;
		MyVector v3;
		v1 = MyVector.unitize(up);
		//unit(v1,up);
		v2 = MyVector.unitize(MyVector.cross(v1, MyVector.diff(vrp, vp)));
		//unit(v2,cross(v1,vrp-vp));
		v3 = MyVector.unitize(MyVector.cross(v1, v2));
		//unit(v3,cross(v1,v2));
		Matrix t = new Matrix(v1.val[0], v1.val[1], v1.val[2], v2.val[0], v2.val[1], v2.val[2], v3.val[0], v3.val[1], v3.val[2]);
		m = Matrix.mul(t, m);
		//rotate2(v1,v2,v3);//change cordinate system
		//System.out.println("change corinate system"+"\n"+m);
		t = new Matrix();
		double d = Math.abs(vrp.val[2] - vp.val[2]);
		//float d=abs(vrp.val[2]-vp.val[2]);
		t.val[3][2] = -1.0 / d;
		m = Matrix.mul(t, m);
		//get prospective
		System.out.println("change to prospective " + "\n" + m);

		d = Math.max(Math.abs(vrp.val[2] - vp.val[2]), Math.max(Math.abs(vrp.val[1] - vp.val[1]), Math.abs(vrp.val[0] - vp.val[0])));

		v1 = new MyVector(1.0 * size / d / 2, 1.0 * size / d / 2, 1.0 * size / d / 2);
		//??  v1.val[0]=v1.val[1]=v1.val[2]=MAXW/d;
		v2 = new MyVector(0, 0, 0);
		//??  v2.val[0]=v2.val[1]=v2.val[2]=0;
		m = Matrix.scale(m, v1, v2);
		//???  scale(v1,v2); //scale from delta d to screen size
		m = Matrix.trans(m, new MyVector(1.0 * size / 2, 1.0 * size / 2, 0));
		//trans(MAXW/2,MAXW/2,0);//center on screen
		System.out.println("center in screen" + "\n" + m);

		System.out.println("final matrix\n" + m);

		//transform all
		Vector work = new Vector();
		for (int a = 0; a < allPoly.size(); a++) {
			Poly3d p = (Poly3d) allPoly.elementAt(a);
			Poly3d p2 = Poly3d.apply(m, p);
			work.addElement(p2);
		}
		//display work
		for (int a = 0; a < work.size(); a++) {
			Poly3d p = (Poly3d) work.elementAt(a);
			p.display(g, this);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 *@param  p  Description of Parameter 
	 *@param  c  Description of Parameter 
	 */
	public void drawPoint(Graphics g, Point3d p, Color c) {
		//check z buffer
		//get lighting effect
		g.drawLine((int) (p.point.val[0]), (int) (p.point.val[1]), (int) (p.point.val[0]), (int) (p.point.val[1]));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  fn  Description of Parameter 
	 */
	public void readFile(String fn) {
		try {
			GmlPair g = GmlPair.parse(new File(fn));
			GmlPair n[] = g.getAllByName("poly");
			for (int a=0; a < n.length; a++) {
				//System.out.println("reading poly");
				GmlPair p[] = n[a].getAllByName("point");
				Vector points = new Vector();
				for (int b = 0; b < p.length; b++) {
					//System.out.println("reading lines");
					double x = (p[b].getAllByName("x"))[0].getDouble();
					double y = (p[b].getAllByName("y"))[0].getDouble();
					double z = (p[b].getAllByName("z"))[0].getDouble();
					double nx = (p[b].getAllByName("nx"))[0].getDouble();
					double ny = (p[b].getAllByName("ny"))[0].getDouble();
					double nz = (p[b].getAllByName("nz"))[0].getDouble();
					points.addElement(new Point3d(new MyVector(x, y, z), new MyVector(nx, ny, nz)));
				}
				Color c = Color.gray;
				p = n[a].getAllByName("color");
				if (p.length > 0) {
					//System.out.println("reading color");
					int re = (int) ((p[0].getAllByName("r"))[0].getDouble());
					int gr = (int) ((p[0].getAllByName("g"))[0].getDouble());
					int bl = (int) ((p[0].getAllByName("b"))[0].getDouble());
					c = new Color(re % 255, gr % 255, bl % 255);
				}
				Poly3d ta = new Poly3d(points, c);
				allPoly.addElement(ta);
			}
			n = g.getAllByName("viewpoint");
			if (n.length != 0) {
				double x = (n[0].getAllByName("x"))[0].getDouble();
				double y = (n[0].getAllByName("y"))[0].getDouble();
				double z = (n[0].getAllByName("z"))[0].getDouble();
				viewPoint = new MyVector(x, y, z);
			}
			n = g.getAllByName("viewrefpoint");
			if (n.length != 0) {
				double x = (n[0].getAllByName("x"))[0].getDouble();
				double y = (n[0].getAllByName("y"))[0].getDouble();
				double z = (n[0].getAllByName("z"))[0].getDouble();
				viewRefPoint = new MyVector(x, y, z);
			}
			n = g.getAllByName("upref");
			if (n.length != 0) {
				double x = (n[0].getAllByName("x"))[0].getDouble();
				double y = (n[0].getAllByName("y"))[0].getDouble();
				double z = (n[0].getAllByName("z"))[0].getDouble();
				upRef = new MyVector(x, y, z);
			}

		}
		catch (IOException ioe) {
		}
		System.out.println("read " + allPoly.size() + " polys!");
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		if (s.length == 0) {
			System.out.println("FORMAT: java aj.awt.3d.My3DCanvas <file>");
			System.exit(0);
		}
		Frame f = new Frame("My3dCanvas");
		f.setLayout(new BorderLayout());
		My3DCanvas m3d = new My3DCanvas();
		m3d.readFile(s[0]);
		f.add("Center", m3d);
		//    f.pack();
		f.setSize(new Dimension(size, size));
		f.setVisible(true);
		f.addWindowListener(new SimpleWindowManager());
	}

}

