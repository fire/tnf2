package aj.bitbot;

import java.util.Random;

import aj.awt.maze.Dungeon;

public class Grid {

	public static final char EMPTY = Dungeon.EMPTY;
	public static final char SOLID = Dungeon.SOLID;
	public static final char DOOR = Dungeon.DOOR;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numgrids=5;
		Grid g[][]=new Grid[numgrids][numgrids];
		int mag=21;
		int lx=(int)(Math.random()*500);
		int ly=(int)(Math.random()*500);
		for (int a=0;a<g.length;a++) {
			for (int b=0;b<g[a].length;b++) {
				g[a][b]=new Grid(1,lx+b,ly+a,mag);
			}
		}
		System.out.println(showMulti(g));
	}

	private char grid[][] = new char[0][0];

/**
 * Displays multi arry of grid to System.out
 * 
 * @param g
 * @return
 */
	public static String showMulti(Grid g[][]) {
		String res="";
		for (int a=0;a<g.length;a++) {
			for (int c=0;c<g[a][0].grid.length;c++) {
				for (int b=0;b<g[a].length;b++) { 
						res+=g[a][b].getRow(c);					
//						res+=g[a][b].getRow(c)+"-";					
					}
					res+="\n";				
				}
//			res+="-----\n";
		}
		return res;
	}
	
	/**
	 * Used to display multi grid rows adjacently
	 * 
	 * @param i
	 * @return
	 */
	private String getRow(int i) {
		String res="";
		for (int b = 0; b < grid.length; b++) {
			res += grid[i][b];
		}
		return res;
	}

/**
 * 
 * @param level
 * @param x supergid location
 * @param y supergid location
 * @param magnificaiton (size of gid, must be odd)
 */
	public Grid(int level,int x, int y, int magnificaiton) {
		grid = new char[magnificaiton][];
		for (int a = 0; a < grid.length; a++) {
			grid[a] = new char[magnificaiton];
			for (int b=0;b<grid[a].length;b++) {
				grid[a][b]=Dungeon.SOLID;//allChars[(int)(Math.random()*allChars.length)];
			}
		}
		Random top = new Random(("L"+level+x + ":" + (y - 1) + ":" + y).hashCode());
		Random bot = new Random(("L"+level+x + ":" + y + ":" + (y + 1)).hashCode());
		Random right = new Random(("L"+level+x + ":" + (x + 1) + ":" + y).hashCode());
		Random left = new Random(("L"+level+(x - 1) + ":" + (x) + ":" + y).hashCode());
		for (int a = 1; a < grid[0].length-1; a+=2) {
			grid[0][a] = getRandomChar(top.nextDouble() * 4);
		}
		for (int a = 1; a < grid[grid.length - 1].length; a+=2) {
			grid[grid.length - 1][a] = getRandomChar(bot.nextDouble() * 4);
		}
		for (int a = 1; a < grid.length-1; a+=2) {
			grid[a][0] = getRandomChar(left.nextDouble() * 4);
		}
		for (int a = 1; a < grid.length - 1; a+=2) {
			grid[a][grid.length-1] = getRandomChar(right.nextDouble() * 4);
		}
//		System.out.println(this.toString());
		int sparceness=6;
		double deadendrate=1;
		int numrooms=magnificaiton*magnificaiton/150;
		double doorrate=1;
		double randomness=.5;
		Dungeon d=Dungeon.createDungeon(("L"+level+":"+x+":"+y).hashCode(),grid,sparceness,deadendrate,randomness,numrooms,doorrate,4,8,4,8);
		grid=d.getGrid();
	}

	private char getRandomChar(double d) {
		if (d<1) return Dungeon.SOLID;
		else if (d>=1 && d<2) return Dungeon.EMPTY;
		else return Dungeon.SOLID;
	}

	public char[][] getGrid() {
		return grid;
	}
	
	public String toString() {
		String res = "";
		for (int a = 0; a < grid.length; a++) {
			res+=getRow(a)+"\n";
		}
		return res;
	}
}
