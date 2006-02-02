package aj.robot;

import java.awt.Graphics;

public abstract class ArenaItem {
	double x,y;
	double _time = 0;

	public ArenaItem() {}

	public double getX(double scale) {return x*scale;}
	public double getY (double scale) {return y*scale;}
	public void setX (double d) {x=d;}
	public void setY (double d) {y=d;}
	public void setTime (double d) {_time = d;}
	public double getTime() {return _time;}
	public abstract void display (Graphics g) ;
}
