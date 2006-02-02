package aj.awt;
import java.awt.Graphics;

/**
 *@author     judda 
 *@created    April 12, 2000 
 */
public interface DisplayItem {
	public void display (Graphics g, double scale);
	public double getX (double s);
	public double getY (double s);
	public void setX (double d, double s);
	public void setY (double d, double s);
}

