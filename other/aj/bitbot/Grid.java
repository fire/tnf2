package aj.bitbot;

import java.util.Random;

public class Grid {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numgrids=5;
		Grid g[][]=new Grid[numgrids][numgrids];
		int mag=13;
		int lx=(int)(Math.random()*500);
		int ly=(int)(Math.random()*500);
		for (int a=0;a<g.length;a++) {
			for (int b=0;b<g[a].length;b++) {
				g[a][b]=new Grid(1,lx+b,ly+a,mag);
			}
		}
		System.out.println(showMulti(g));
//		int mag=23;
//		int level=1;
//		Grid g = new Grid(level,x,y,mag);
//		System.out.println(g.toString());
//		Grid gg = new Grid(level,x+1,y,mag);
//
//		for (int a=0;a<mag;a++) {
//			System.out.println(g.getRow(a)+gg.getRow(a));			
//		}
//		g = new Grid(level,x,y+1,mag);
//		gg = new Grid(level,x+1,y+1,mag);
//		for (int a=0;a<mag;a++) {
//			System.out.println(g.getRow(a)+gg.getRow(a));			
//		}
	}
	
	public static String showMulti(Grid g[][]) {
		String res="";
		for (int a=0;a<g.length;a++) {
			for (int c=0;c<g[a][0].grid.length;c++) {
				for (int b=0;b<g[a].length;b++) { 
						res+=""+g[a][b].getRow(c);					
					}
					res+="\n";				
				}
		}
		return res;
	}
	
	private String getRow(int i) {
		String res="";
		for (int b = 0; b < grid.length; b++) {
			res += grid[i][b];
		}
		return res;
	}

	private char grid[][] = new char[0][0];

	public static final char SOLID = 'X', EMPTY = ' ', DOOR = 'D';
//	private static final char allChars[]={SOLID,EMPTY,DOOR};

	public Grid(int level,int x, int y, int magnificaiton) {
		grid = new char[magnificaiton][];
		for (int a = 0; a < grid.length; a++) {
			grid[a] = new char[magnificaiton];
			for (int b=0;b<grid[a].length;b++) {
				grid[a][b]=SOLID;//allChars[(int)(Math.random()*allChars.length)];
			}
		}
		Random top = new Random(("L"+level+x + ":" + (y - 1) + ":" + y).hashCode());
		Random bot = new Random(("L"+level+x + ":" + y + ":" + (y + 1)).hashCode());
		Random right = new Random(("L"+level+x + ":" + (x + 1) + ":" + y).hashCode());
		Random left = new Random(("L"+level+(x - 1) + ":" + (x) + ":" + y).hashCode());
		for (int a = 1; a < grid[0].length-1; a+=2) {
			grid[0][a] = getRandomChar(top.nextGaussian() * 4);
		}
		for (int a = 1; a < grid[grid.length - 1].length; a+=2) {
			grid[grid.length - 1][a] = getRandomChar(bot.nextGaussian() * 4);
		}
		for (int a = 1; a < grid.length-1; a+=2) {
			grid[a][0] = getRandomChar(left.nextGaussian() * 4);
		}
		for (int a = 1; a < grid.length - 1; a+=2) {
			grid[a][grid.length-1] = getRandomChar(right.nextGaussian() * 4);
		}
//		Random ulcorn=new Random(("L"+level+(x-1) + ":" + (y - 1) + ":" + x+":"+y).hashCode());
//		Random urcorn=new Random(("L"+level+(x) + ":" + (y - 1) + ":" + (x+1)+":"+y).hashCode());
//		Random llcorn=new Random(("L"+level+(x-1) + ":" + (y) + ":" + x+":"+(y+1)).hashCode());
//		Random lrcorn=new Random(("L"+level+(x) + ":" + (y) + ":" + (x+1)+":"+(y+1)).hashCode());
//		grid[0][0]= getRandomChar(ulcorn.nextGaussian() * 4);
//		grid[0][grid.length-1]= getRandomChar(urcorn.nextGaussian() * 4);
//		grid[grid.length-1][0]= getRandomChar(llcorn.nextGaussian() * 4);
//		grid[grid.length-1][grid.length-1]= getRandomChar(lrcorn.nextGaussian() * 4);
		digMap(magnificaiton/2-magnificaiton%2,magnificaiton/2-magnificaiton%2);
	}

	private void digMap(int x,int y) {
		int visited=0;
		if (x>1 && grid[x-1][y]==EMPTY) visited++;
		if (x<grid.length-2 && grid[x+1][y]==EMPTY) visited++;
		if (y>1 && grid[x][y-1]==EMPTY) visited++;
		if (y<grid.length-2 && grid[x][y+1]==EMPTY) visited++;
		if (visited>1) return;
		grid[x][y]=EMPTY;
		int count=0;
		int dir=(int)(Math.random()*4);
		while (count<4) {
			dir++;
			dir=dir%4;
			if (dir==0 && x>2 && grid[x-2][y]==SOLID) {
				grid[x-1][y]=EMPTY;
				digMap(x-2,y);
			}
			if (dir==1 && x<grid.length-3 && grid[x+2][y]==SOLID) {
				grid[x+1][y]=EMPTY;
				digMap(x+2,y);
			}
			if (dir==2 && y>2 && grid[x][y-2]==SOLID) {
				grid[x][y-1]=EMPTY;
				digMap(x,y-2);
			}
			if (dir==3 && y<grid.length-3 && grid[x][y+2]==SOLID) {
				grid[x][y+1]=EMPTY;
				digMap(x,y+2);
			}
			count++;
		}
		
		// TODO Auto-generated method stub
		
	}

	private char getRandomChar(double d) {
		byte dd=(byte)d;
		if (dd==0) return SOLID;
		else if (dd==1) return EMPTY;
//		else if (dd==2) return DOOR;
		else return SOLID;
	}

	public String toString() {
		String res = "";
		for (int a = 0; a < grid.length; a++) {
			res+=getRow(a)+"\n";
		}
		return res;
	}
}
