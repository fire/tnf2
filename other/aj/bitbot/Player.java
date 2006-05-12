package aj.bitbot;

public class Player {

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
	
}
