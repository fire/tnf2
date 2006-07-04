package aj.awt.my3d;

/**
 * Description of the Class
 * 
 * @author judda
 * @created July 21, 2000
 */
public class Matrix {
	double val[][] = new double[4][4];

	/**
	 * Constructor for the Matrix object
	 */
	public Matrix() {
		val[0][0] = 1;
		val[1][1] = 1;
		val[2][2] = 1;
		val[3][3] = 1;
		// for (int a=0;a<4;a++)
		// for (int b=0;b<4;b++)
		// if (a==b) val[a][b]=1;
		// else val[a][b]=0;
	}

	/**
	 * Constructor for the Matrix object
	 * 
	 * @param v
	 *            Description of Parameter
	 */
	public Matrix(double v[][]) {
		val = v;
	}

	/**
	 * Constructor for the Matrix object
	 * 
	 * @param old
	 *            Description of Parameter
	 */
	public Matrix(Matrix old) {
		for (int a = 0; a < 4; a++) {
			for (int b = 0; b < 4; b++) {
				val[a][b] = old.val[a][b];
			}
		}
	}

	/**
	 * Constructor for the Matrix object
	 * 
	 * @param x1
	 *            Description of Parameter
	 * @param x2
	 *            Description of Parameter
	 * @param x3
	 *            Description of Parameter
	 * @param y1
	 *            Description of Parameter
	 * @param y2
	 *            Description of Parameter
	 * @param y3
	 *            Description of Parameter
	 * @param z1
	 *            Description of Parameter
	 * @param z2
	 *            Description of Parameter
	 * @param z3
	 *            Description of Parameter
	 */
	public Matrix(double x1, double x2, double x3, double y1, double y2,
			double y3, double z1, double z2, double z3) {
		val[0][0] = x1;
		val[0][1] = x2;
		val[0][2] = x3;
		val[0][3] = 0;
		val[1][0] = y1;
		val[1][1] = y2;
		val[1][2] = y3;
		val[1][3] = 0;
		val[2][0] = z1;
		val[2][1] = z2;
		val[2][2] = z3;
		val[2][3] = 0;
		val[3][0] = 0;
		val[3][1] = 0;
		val[3][2] = 0;
		val[3][3] = 1;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		return "Matrix \n(" + val[0][0] + "," + val[0][1] + "," + val[0][2]
				+ "," + val[0][3] + ")\n," + "(" + val[1][0] + "," + val[1][1]
				+ "," + val[1][2] + "," + val[1][3] + ")\n," + "(" + val[2][0]
				+ "," + val[2][1] + "," + val[2][2] + "," + val[2][3] + ")\n,"
				+ "(" + val[3][0] + "," + val[3][1] + "," + val[3][2] + ","
				+ val[3][3] + ")";
	}

	/**
	 * Description of the Method
	 * 
	 * @param one
	 *            Description of Parameter
	 * @param two
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Matrix mul(Matrix one, Matrix two) {
		double val[][] = new double[4][4];
		for (int a = 0; a < 4; a++) {
			for (int b = 0; b < 4; b++) {
				val[a][b] = 0;
				//
				for (int c = 0; c < 4; c++) {
					//
					val[a][b] += one.val[a][c] * two.val[c][b];
				}
			}
		}
		return new Matrix(val);
	}

	/**
	 * Description of the Method
	 * 
	 * @param m
	 *            Description of Parameter
	 * @param tv
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Matrix trans(Matrix m, MyVector tv) {
		Matrix t = new Matrix();
		t.val[0][3] = tv.val[0];
		t.val[1][3] = tv.val[1];
		t.val[2][3] = tv.val[2];
		t = mul(t, m);
		return t;
	}

	/**
	 * Description of the Method
	 * 
	 * @param m
	 *            Description of Parameter
	 * @param sv
	 *            Description of Parameter
	 * @param c
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Matrix scale(Matrix m, MyVector sv, MyVector c) {
		Matrix t = new Matrix();
		// m,t;
		t.val[0][0] = sv.val[0];
		t.val[0][3] = (1 - sv.val[0]) * c.val[0];
		t.val[1][1] = sv.val[1];
		t.val[1][3] = (1 - sv.val[1]) * c.val[1];
		t.val[2][2] = sv.val[2];
		t.val[2][3] = (1 - sv.val[2]) * c.val[2];
		return Matrix.mul(t, m);
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		MyVector p1 = new MyVector(0, 0, 0);
		MyVector p2 = new MyVector(1, 1, 1);
		Matrix a = new Matrix();
		System.out.println("init matrix" + a.toString());
		Matrix b = Matrix
				.scale(a, new MyVector(5, 1, 1), new MyVector(2, 2, 2));
		System.out.println("matrix scale Xx5,Yx1,Zx1 about 0,0,0?");
		System.out.println(b.toString());
		System.out.println("apply p1 " + p1 + "=" + MyVector.apply(b, p1));
		System.out.println("apply p2 " + p2 + "=" + MyVector.apply(b, p2));
		// Matrix b= Matrix.trans(m,new MyVector(5,1,1));
		// System.out.println("matrix scale Xx5,Yx1,Zx1?" + b.toString());
		// System.out.println("p orig=" + p);
		// p = MyVector.apply(a, p);
		// System.out.println("p after matrix=" + p);
		// Point3d p3=new Point3d(new MyVector(1,1,1),new MyVector(0,0,1));
		// System.out.println("point3 before="+p3.point);
		// p3=Point3d.apply(a,p3);
		// System.out.println("point3 after="+p3.point);
		// Vector v=new Vector();
		// v.addElement(new Point3d(new MyVector(1,2,3),new MyVector(0,0,1)));
		// v.addElement(new Point3d(new MyVector(2,3,4),new MyVector(0,0,1)));
		// v.addElement(new Point3d(new MyVector(4,5,6),new MyVector(0,0,1)));
		// Poly3d pl=new Poly3d(v,Color.blue);
		// System.out.println("pl befroe"+pl);
		// pl=Poly3d.apply(a,pl);
		// System.out.println("pl after"+pl);
	}

}
