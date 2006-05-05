package aj.awt.maze;

import java.awt.Point;
import java.util.Vector;

public class Dungeon {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int size=5;
		new Dungeon(size,size);
	}
	
	char SOLID='X',EMPTY=' ';

	private char grid[][]=null;
	Vector visited=new Vector();
	
	public Dungeon(int x,int y) {
		grid=createSolidGrid(x,y);
		//mark all unvisited
//		Point v=new Point(0,0);
		Point v=new Point((int)(Math.random()*x),(int)(Math.random()*y));
		v=new Point(Math.max(0,v.x+v.x%2-1),Math.max(0,v.y+v.y%2-1));
		
		visited.addElement(v);
		grid[v.x][v.y]=EMPTY;
		while (visited.size()>0) {
			Point p=(Point)visited.elementAt((int)Math.random()*visited.size());
			follow(p);
		}
		show(grid);
		unwind();
		show(grid);
	}


	static int SPARSENESS=1;
	private void unwind() {
		for (int x=0;x<SPARSENESS;x++) {
			Vector ends=new Vector();
			for (int a=0;a<grid.length;a++) {
				for (int b=0;b<grid[a].length;b++) {
					if (grid[a][b]==EMPTY) {
						int count=0;
						if (a>2 && grid[a-2][b]==EMPTY) count++;
						if (a<grid.length-2 && grid[a+2][b]==EMPTY) count++;
						if (b>2 && grid[a][b-2]==EMPTY) count++;
						if (b<grid[a].length-2 && grid[a][b+2]==EMPTY) count++;
						if (count==1) {
							if (a>2 && grid[a-2][b]==EMPTY) {
								ends.addElement(new Point(a,b));
								ends.addElement(new Point(a-1,b));
							}
							if (a<grid.length-2 && grid[a+2][b]==EMPTY){
								ends.addElement(new Point(a,b));
								ends.addElement(new Point(a+1,b));
							}
							if (b>2 && grid[a][b-2]==EMPTY) {
								ends.addElement(new Point(a,b));
								ends.addElement(new Point(a,b-1));
							}
							if (b<grid[a].length-2 && grid[a][b+2]==EMPTY) {
								ends.addElement(new Point(a,b));
								ends.addElement(new Point(a,b+1));
							}
						}
					}
				}
			}
			System.out.println("ends="+ends.size());
			for (int a=0;a<ends.size();a++) {
				Point p=(Point)ends.elementAt(a);
				grid[p.y][p.x]=SOLID;
			}			
		}
	}

	static double randomness=1;
	
	public void follow(Point p) {
		grid[p.y][p.x]=EMPTY;
		int count=0;
		int dir=(int)(Math.random()*4);

		while (true) {
			count++;
			dir++;
			dir%=4;
			
			if (count>6) {
				break;				
			}

			if (dir==0) {
				if (p.x>2 && grid[p.y][p.x-2]!=EMPTY) {
					grid[p.y][p.x-1]=EMPTY;
					grid[p.y][p.x-2]=EMPTY;
					p=new Point(p.x-2,p.y);
					visited.addElement(p);
					count=0;
					if (Math.random()<randomness) dir=(int)(Math.random()*4);
					continue;
				}
			}
			else if (dir==1) {
				if (p.x<grid[p.y].length-2 && grid[p.y][p.x+2]!=EMPTY) {
					grid[p.y][p.x+1]=EMPTY;
					grid[p.y][p.x+2]=EMPTY;
					p=new Point(p.x+2,p.y);
					visited.addElement(p);
					count=0;
					if (Math.random()<randomness) dir=(int)(Math.random()*4);

					continue;
				}
			}
			else if (dir==2) {
				if (p.y>2 && grid[p.y-2][p.x]!=EMPTY) {
					grid[p.y-1][p.x]=EMPTY;
					grid[p.y-2][p.x]=EMPTY;
					p=new Point(p.x,p.y-2);
					visited.addElement(p);
					count=0;
					if (Math.random()<randomness) dir=(int)(Math.random()*4);

					continue;
				}
			}
			else if (dir==3) {
				if (p.y<grid.length-2 && grid[p.y+2][p.x]!=EMPTY) {
					grid[p.y+1][p.x]=EMPTY;
					grid[p.y+2][p.x]=EMPTY;
					p=new Point(p.x,p.y+2);
					visited.addElement(p);
					count=0;
					if (Math.random()<randomness) dir=(int)(Math.random()*4);

					continue;
				}
			}
		}
		visited.removeElement(p);
		return;
	}
	
	private void show(char[][] grid2) {
			for (int a=0;a<grid2.length;a++) {
				for (int b=0;b<grid2[a].length;b++) {
					System.out.print(grid[a][b]);
				}
				System.out.println("");
			}
		
	}

	private char[][] createSolidGrid(int x,int y) {
		char t[][]=new char[x][];
		for (int a=0;a<x;a++) {
			t[a]=new char[y];
			for (int b=0;b<y;b++) {
				t[a][b]=SOLID;
			}
		}
		return t;
	}
}
