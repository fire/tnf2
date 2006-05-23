package aj.bitbot;

import java.awt.Color;
import java.awt.Graphics;

public class Player {

	private static final double PLAYERSIZE = 1;



	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	private double x,y;
	private double dir;
	

	
	public Player(double x,double y,double dir) {
		this.x=x;
		this.y=y;
		this.dir=dir;
	}
	public double getX() {return x;}
	public double getY() {return y;}
	public double getDir() {return dir;}
	public void draw(Graphics g, int scale) {
		g.translate((int)(x*scale),(int)(y*scale));
		g.setColor(Color.blue);
		g.fillOval((int) (PLAYERSIZE* scale/2 ),
				(int) (PLAYERSIZE* scale/2 ),
				(int) (PLAYERSIZE*scale),(int)(PLAYERSIZE*scale));

		// TODO Auto-generated method stub
		
	}
	
}
