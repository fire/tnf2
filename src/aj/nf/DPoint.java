package aj.nf;

import java.awt.*;

/**
 *  A double Point -last modify 1/7/98 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class DPoint {
	
	private double x;
	
	private double y;
	//CONSTRUCTORS
	/**
	 *  Constructor for the DPoint object 
	 *
	 *@param  x  Description of Parameter 
	 *@param  y  Description of Parameter 
	 */
	public DPoint (double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 *  Constructor for the DPoint object 
	 *
	 *@param  p  Description of Parameter 
	 */
	public DPoint (DPoint p) {
		x = p.getX();
		y = p.getY();
	}
	/**
	 *  Sets the X attribute of the DPoint object 
	 *
	 *@param  x  The new X value 
	 */
	public void setX (double x) {
		this.x = x;
	}
	/**
	 *  Sets the Y attribute of the DPoint object 
	 *
	 *@param  y  The new Y value 
	 */
	public void setY (double y) {
		this.y = y;
	}
	/**
	 *  Gets the X attribute of the DPoint object 
	 *
	 *@return    The X value 
	 */
	public double getX() {
		return x;
	}
	/**
	 *  Gets the Y attribute of the DPoint object 
	 *
	 *@return    The Y value 
	 */
	public double getY() {
		return y;
	}
}

