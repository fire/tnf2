package aj.awt.my3d;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class MyVector {
	double val[];


	/**
	 *  Constructor for the MyVector object 
	 *
	 *@param  v  Description of Parameter 
	 */
	public MyVector(MyVector v) {
		val = v.val;
	}


	/**
	 *  Constructor for the MyVector object 
	 *
	 *@param  v  Description of Parameter 
	 */
	public MyVector(double v[]) {
		val = v;
	}


	/**
	 *  Constructor for the MyVector object 
	 *
	 *@param  x  Description of Parameter 
	 *@param  y  Description of Parameter 
	 *@param  z  Description of Parameter 
	 */
	public MyVector(double x, double y, double z) {
		val = new double[4];
		val[0] = x;
		val[1] = y;
		val[2] = z;
		val[3] = 1;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public double len() {
		double l = 0;
		for (int a = 0; a < 4; a++) {
			l += val[a] * val[a];
		}
		return Math.sqrt(l);
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		return "MyVector (" + val[0] + "," + val[1] + "," + val[2] + "," + val[3] + ")";
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  one  Description of Parameter 
	 *@param  two  Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public static MyVector cross(MyVector one, MyVector two) {
		//cross product
		double x;
		//cross product
		double y;
		//cross product
		double z;
		x = one.val[1] * two.val[2] - one.val[2] * two.val[1];
		y = one.val[2] * two.val[0] - one.val[0] * two.val[2];
		z = one.val[0] * two.val[1] - one.val[1] * two.val[0];
		return new MyVector(x, y, z);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  one  Description of Parameter 
	 *@param  two  Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public static double dot(MyVector one, MyVector two) {
		//dot product
		double temp = 0;
		for (int a = 0; a < 3; a++) {
			temp += one.val[a] * two.val[a];
		}
		return temp / (one.len() * two.len());
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  v  Description of Parameter 
	 *@param  l  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static MyVector scale(MyVector v, double l) {
		double va[] = new double[4];
		for (int a = 0; a < 4; a++) {
			va[a] = v.val[a] * l;
		}
		return new MyVector(va);
	}


	//normal =1
	/**
	 *  Description of the Method 
	 *
	 *@param  v  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static MyVector normalize(MyVector v) {
		double va[] = new double[4];
		for (int a = 0; a < 4; a++) {
			if (v.val[3] != 0) {
				va[a] = v.val[a] / v.val[3];
			}
			else {
				va[a] = v.val[a];
			}
		}
		return new MyVector(va);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  v  Description of Parameter 
	 *@param  y  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static MyVector add(MyVector v, MyVector y) {
		double va[] = new double[4];
		for (int a = 0; a < 4; a++) {
			va[a] = v.val[a] + y.val[a];
		}
		return new MyVector(va);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  v  Description of Parameter 
	 *@param  y  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static MyVector diff(MyVector v, MyVector y) {
		double va[] = new double[4];
		for (int a = 0; a < 4; a++) {
			va[a] = v.val[a] - y.val[a];
		}
		return new MyVector(va);
	}


	//len = 1
	/**
	 *  Description of the Method 
	 *
	 *@param  v  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static MyVector unitize(MyVector v) {
		double va[] = new double[4];
		double l = v.len();
		if (l != 0) {
			for (int a = 0; a < 4; a++) {
				va[a] = v.val[a] / l;
			}
		}
		return new MyVector(va);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  m  Description of Parameter 
	 *@param  v  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static MyVector apply(Matrix m, MyVector v) {
		double val[] = new double[4];
		for (int a = 0; a < 4; a++) {
			for (int b = 0; b < 4; b++) {
				val[a] += v.val[b] * m.val[a][b];
			}
		}
		return new MyVector(val);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		MyVector d = new MyVector(1, 0, 0);
		MyVector a = new MyVector(0, 1, 0);
		MyVector c;
		c = cross(d, a);
		System.out.println(c);
		MyVector p = new MyVector(3, 5, 2);
		System.out.println("p=" + p);
		p = normalize(p);
		System.out.println("normalized p=" + p);
		p = new MyVector(3, 5, 2);
		p.val[3] = 1.5;
		System.out.println("p=" + p);
		p = MyVector.normalize(p);
		System.out.println("normalize " + p);
		p = new MyVector(3, 5, 2);
		System.out.println("p=" + p);
		p = unitize(p);
		System.out.println("unitize " + p);
		p = new MyVector(3, 5, 2);
		p.val[3] = 1.5;
		System.out.println("p=" + p);
		p = unitize(p);
		System.out.println("unitize " + p);
	}

}
