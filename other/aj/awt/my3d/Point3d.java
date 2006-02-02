package aj.awt.my3d;


/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class Point3d {
	MyVector point;
	MyVector normal;


	/**
	 *  Constructor for the Point3d object 
	 *
	 *@param  p  Description of Parameter 
	 *@param  n  Description of Parameter 
	 */
	public Point3d(MyVector p, MyVector n) {
		point = new MyVector(p);
		normal = new MyVector(n);
	}


	/**
	 *  Constructor for the Point3d object 
	 *
	 *@param  p3  Description of Parameter 
	 */
	public Point3d(Point3d p3) {
		point = new MyVector(p3.point);
		normal = new MyVector(p3.normal);
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		return "Point3d: point<" + point + "> <" + normal + ">";
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  p  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Point3d makeNormal(Point3d p) {
		//System.out.println("before point="+p.point);
		MyVector pp = MyVector.normalize(p.point);
		//System.out.println("after point="+pp);
		return new Point3d(pp, MyVector.unitize(p.normal));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  p   Description of Parameter 
	 *@param  dx  Description of Parameter 
	 *@param  dy  Description of Parameter 
	 *@param  dz  Description of Parameter 
	 *@return     Description of the Returned Value 
	 */
	public static Point3d trans(Point3d p, double dx, double dy, double dz) {
		double val[] = new double[4];
		val[0] = p.point.val[0] + dx;
		val[1] = p.point.val[1] + dy;
		val[2] = p.point.val[2] + dz;
		return new Point3d(new MyVector(val), new MyVector(p.normal));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  m  Description of Parameter 
	 *@param  p  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Point3d apply(Matrix m, Point3d p) {
		Point3d t = new Point3d(p);
		//
		//System.out.println("point3d apply #1"+t);
		t.point = MyVector.apply(m, p.point);
		t.normal = MyVector.apply(m, p.normal);
		//System.out.println("point3d apply #2"+t);
		t = makeNormal(t);
		//System.out.println("point3d apply #3"+t);
		return t;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		Point3d p = new Point3d(new MyVector(1, 2, 3), new MyVector(1, 2, 3));
		System.out.println(p);
		p = makeNormal(p);
		System.out.println("normal " + p);
		p.point.val[3] = 1.5;
		p.normal.val[3] = 1.5;
		System.out.println(p);
		p = makeNormal(p);
		System.out.println("normal " + p);

	}

}
