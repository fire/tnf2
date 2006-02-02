package aj.awt;
/**
 *  A double Point -last modify 1/7/98 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Point3d {
	
	public int x, y, z;
	//CONSTRUCTORS
	/**
	 *  Constructor for the Point3d object 
	 *
	 *@param  x  Description of Parameter 
	 *@param  y  Description of Parameter 
	 */
	public Point3d (int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	/**
	 *  Constructor for the Point3d object 
	 *
	 *@param  p  Description of Parameter 
	 */
	public Point3d (Point3d p) {
		x = p.x;
		z = p.z;
		y = p.y;
	}
}

