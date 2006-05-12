package aj.awt.maze;

import java.awt.Point;
import java.util.Random;
import java.util.Vector;

public class Dungeon {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int width=81,height=31;
		int sparceness=12;
		double deadendchance=1;
		double doorchange=0;
		int numrooms=width*height/200;
		int seed=(int)(Math.random()*99999);
		double randomness=.5;
		Dungeon d=Dungeon.createDungeon(seed,new char[height][width],sparceness,deadendchance,randomness,numrooms,doorchange,6,12,4,8);
//		createDungeon(new char[width][height],5,0,1,1,5,5,5,5);
//		Dungeon d=new Dungeon(61, 21);
		System.out.println("");
		d.show();
	}
	public static final char ROOM = '.', SOLID = '#', EMPTY = ' ', DOOR = '+';

	private static  int DEFAULT_MINROOMWIDTH = 7, DEFAULT_MAXROOMWIDTH = 13, DEFAULT_MINROOMHEIGHT = 5,
			DEFAULT_MAXROOMHEIGHT = 9,DEFAULT_SPARCENESS=12;
	private static double DEFAULT_RANDOMNESS = 1;

	private char grid[][] = null;

	private Vector visited = new Vector();
	private Random random;

	/**
	 * @param seed the random seed
	 * @param c grid of chars to use  Must be all SOLID or EMPTY
	 * @param sparceNess number of steps to backup from deadends
	 * @param deadEndChance chance to reconnect dead ends (0=leave all, 1=connect all)
	 * @param numRooms number of rooms to add
	 * @param doorChance chance to make door into room (0=no doors, 1=all doors)
	 * @param minroomwidth
	 * @param maxroomwidth
	 * @param minroomheight
	 * @param maxroomheight
	 * @return
	 */
	public static Dungeon createDungeon(int seed,char c[][],int sparceNess,double deadEndChance,double randomness,int numRooms,double doorChance,int minroomwidth,int maxroomwidth,int minroomheight,int maxroomheight) {
		Dungeon d=new Dungeon();
		d.random=new Random(seed);
		d.grid=c;
		d.validateGrid();
		d.findInitialVisited();
		d.followAllVisited(randomness);
		d.unwind(sparceNess);
		d.addRooms(numRooms, minroomwidth, maxroomwidth, minroomheight, maxroomheight);
		d.addDoors(doorChance);
		return d;
	}
	
	/**
	 * Sets all grid to start value of SOLID or EMPTY only
	 *
	 */
	private void validateGrid() {
		for (int a=0;a<grid.length;a++) {
			for (int b=0;b<grid[a].length;b++) {
				if (grid[a][b]!=SOLID && grid[a][b]!=EMPTY) grid[a][b]=SOLID;
			}
		}		
	}

	/**
	 * Initilizes the visited list with all EMPTy seqares
	 *
	 */
	private void findInitialVisited() {
		visited=new Vector();
		for (int a=0;a<grid.length;a++) {
			for (int b=0;b<grid[a].length;b++) {
				if (a%2==0 || b%2==0) continue;
				if (grid[a][b]==EMPTY) {
					visited.addElement(new Point(b,a));
				}
			}
		}
		
		if (visited.size()==0) {
			int x=(int)(random.nextDouble()*grid[0].length);
			int y=(int)(random.nextDouble()*grid.length);
			x=x+x%2-1;
			y=y+y%2-1;
			if (x<0) x=1;
			if (y<0) y=1;
			visited.addElement(new Point(x,y));
		}
	}
	
	/**
	 * internal constructor for static create
	 *
	 */
	private Dungeon() {
	}
	
	/**
	 * follow each visited square until all visited squares are complete and removed
	 *
	 */
	private void followAllVisited(double randomness) {
		while (visited.size() > 0) {
			int next=((int)(random.nextDouble()*(visited.size()-1)));
			Point p = (Point) visited.elementAt(next);
			follow(p,randomness);
		}
	}
	/**
	 * Creates a dungeon 
	 * 
	 * requires odd by odd dimensions
	 * 
	 * @param x
	 * @param y
	 */
	public Dungeon(int x, int y) {
		grid = createSolidGrid(x, y);
		Point v = new Point((int) (random.nextDouble() * x),
				(int) (random.nextDouble() * y));

		v = new Point(Math.max(1, v.x + v.x % 2 - 1), Math.max(1, v.y + v.y % 2
				- 1));
		grid[v.y][v.x] = EMPTY;

		findInitialVisited();
		followAllVisited(DEFAULT_RANDOMNESS);
		unwind(DEFAULT_SPARCENESS);
		removeDeadEnds(1);
		addRooms(10, DEFAULT_MINROOMWIDTH, DEFAULT_MAXROOMWIDTH, DEFAULT_MINROOMHEIGHT, DEFAULT_MAXROOMHEIGHT);
		addDoors(.9);

	}

	/**
	 * 
	 * @param d chance to add door on wall not already connected
	 */
	private void addDoors(double d) {
		for (int a = 1; a < grid.length; a += 2) {
			for (int b = 1; b < grid[a].length; b += 2) {

				if (b > 0 && b < grid[a].length - 2 && grid[a][b]==EMPTY && grid[a][b+1] == EMPTY
						&& grid[a][b + 2] == ROOM) {
					grid[a][b+1] = DOOR;
				}
				if (b > 1 && b < grid[a].length - 1 && grid[a][b]==EMPTY && grid[a][b-1] == EMPTY
						&& grid[a][b - 2] == ROOM) {
					grid[a][b-1] = DOOR;
				}
				if (a > 0 && a < grid.length - 2 && grid[a][b]==EMPTY && grid[a+1][b] == EMPTY
						 && grid[a + 2][b] == ROOM) {
					grid[a+1][b] = DOOR;
				}
				if (a > 1 && a < grid.length - 1 && grid[a][b]==EMPTY && grid[a-1][b] == EMPTY
						&& grid[a - 2][b] == ROOM) {
					grid[a-1][b] = DOOR;
				}
				if (b > 0 && b < grid[a].length - 2 && grid[a][b]==ROOM && grid[a][b+1] == EMPTY
						&& grid[a][b + 2] == ROOM) {
					grid[a][b+1] = DOOR;
				}
				if (b > 1 && b < grid[a].length - 1 && grid[a][b]==ROOM && grid[a][b-1] == EMPTY
						&& grid[a][b - 2] == ROOM) {
					grid[a][b-1] = DOOR;
				}
				if (a > 0 && a < grid.length - 2 && grid[a][b]==ROOM && grid[a+1][b] == EMPTY
						 && grid[a + 2][b] == ROOM) {
					grid[a+1][b] = DOOR;
				}
				if (a > 1 && a < grid.length - 1 && grid[a][b]==ROOM && grid[a-1][b] == EMPTY
						&& grid[a - 2][b] == ROOM) {
					grid[a-1][b] = DOOR;
				}
			}
		}
	}

	/**
	 * 
	 * @param i number of rooms to add
	 * @param minwid (must be odd)
	 * @param maxwid (must be odd)
	 * @param minhei (must be odd)
	 * @param maxhei (must be odd)
	 */
	private void addRooms(int i, int minwid, int maxwid, int minhei, int maxhei) {
		for (int a = 0; a < i; a++) {
			int bestx = 0, besty = 0, bestScore = 999999;
			int rw = (int) (random.nextDouble() * (maxwid - minwid) + minwid);
			rw +=rw%2-1;//(int)(rw / 2) + (int)(rw / 2) + 1;
			int rh = (int) (random.nextDouble() * (maxhei - minhei) + minhei);
			rh +=rh%2-1;//= (int)(rh / 2) + (int)(rh / 2) + 1;
			for (int y = 1; y < grid.length; y += 2) {
				for (int x = 1; x < grid[y].length; x += 2) {
					int s = getRoomScore(x, y, rw, rh);
					if (s < bestScore && s>0) {
						bestScore = s;
						bestx = x;
						besty = y;
					}
				}
			}
			for (int y = besty; y < besty + rh; y++) {
				for (int x = bestx; x < bestx + rw; x++) {
					if (x>=0 && y>=0 && y<grid.length-1 && x<grid[y].length-1)
					grid[y][x] = ROOM;// (char)('A'+a);
				}
			}
		}
	}

	/**
	 * Returns score 0 being no connect, 999999 being out of bounds, lower number is better
	 * @param x
	 * @param y
	 * @param rw
	 * @param rh
	 * @return score 0-999999
	 */
	private int getRoomScore(int x, int y, int rw, int rh) {
		int score = 0;
		if (y < 0 || y + rh > grid.length)
			return 999999;
		if (x < 0 || x + rw > grid[y].length)
			return 999999;

		// check for overlap EMPTY +3 or ROOM +100
		for (int dy = y; dy < y + rh; dy++) {
			for (int dx = x; dx < x + rw; dx++) {
				if (grid[dy][dx] == EMPTY)
					score += 3;
				if (grid[dy][dx] != EMPTY && grid[dy][dx] != SOLID)
					score += 100;
			}
		}

		// //check for adjacent ROOM EMPTY
		for (int a = y - 1; a < rh + y + 1; a++) {
			// if (x-1>1 && grid[a][x-1]==EMPTY) score-=1;
			// if (x+1<grid[a].length-1 && grid[a][x+1]==EMPTY) score-=1;
		}
		for (int a = x - 1; a < rw + x + 1; a++) {
			// if (y-1>grid.length && grid[y-1][a]==EMPTY) score-=1;
			// if (y+1>grid.length-1 && grid[y-1][a]==EMPTY) score-=1;
		}
		return score;
	}

	/**
	 * Finds all deadends and converts %percentDeadEndConnect back to other halls
	 * 
	 * @param percentDeadEndConnect
	 */
	private void removeDeadEnds(double percentDeadEndConnect) {
		Vector ends = new Vector();
		for (int y = 1; y < grid.length; y += 2) {
			for (int x = 1; x < grid[y].length; x += 2) {
				if (grid[y][x] == EMPTY) {
					int count = 0;
					if (x > 1 && grid[y][x - 1] == EMPTY) {
						count++;
					}
					if (x < grid[y].length - 1 && grid[y][x + 1] == EMPTY) {
						count++;
					}
					if (y > 1 && grid[y - 1][x] == EMPTY) {
						count++;
					}
					if (y < grid.length - 1 && grid[y + 1][x] == EMPTY) {
						count++;
					}
					if (count == 1) {
						ends.addElement(new Point(x, y));
					}
				}
			}
		}
		for (int a = 0; a < ends.size(); a++) {
			if (random.nextDouble() < percentDeadEndConnect) {
				connect((Point) ends.elementAt(a));
			}
		}
	}

	/**
	 * THis method starts at a visite point, then travels until it finds a new
	 * visited points and joins them
	 * 
	 * @param point
	 */
	private void connect(Point p) {
		int count = 0;
		int dir = (int) (random.nextDouble() * 4);
		while (true) {
			count++;
			if (count > 6) {
				break;
			}
			if (dir == 0 && p.x > 2 && grid[p.y][p.x - 1] != EMPTY) {
				if (grid[p.y][p.x - 2] != EMPTY) {
					grid[p.y][p.x - 1] = EMPTY;
					grid[p.y][p.x - 2] = EMPTY;
					p = new Point(p.x - 2, p.y);
					count = 0;
					if (random.nextDouble() < DEFAULT_RANDOMNESS)
						dir = (int) (random.nextDouble() * 4);
					continue;
				} else {
					grid[p.y][p.x - 1] = EMPTY;
					break;
				}
			} else if (dir == 1 && p.x < grid[p.y].length - 2
					&& grid[p.y][p.x + 1] != EMPTY) {
				if (grid[p.y][p.x + 2] != EMPTY) {
					grid[p.y][p.x + 1] = EMPTY;
					grid[p.y][p.x + 2] = EMPTY;
					p = new Point(p.x + 2, p.y);
					count = 0;
					if (random.nextDouble() < DEFAULT_RANDOMNESS)
						dir = (int) (random.nextDouble() * 4);
					continue;
				} else {
					grid[p.y][p.x + 1] = EMPTY;
					break;
				}
			} else if (dir == 2 && p.y > 2 && grid[p.y - 1][p.x] != EMPTY) {
				if (grid[p.y - 2][p.x] != EMPTY) {
					grid[p.y - 1][p.x] = EMPTY;
					grid[p.y - 2][p.x] = EMPTY;
					p = new Point(p.x, p.y - 2);
					count = 0;
					if (random.nextDouble() < DEFAULT_RANDOMNESS)
						dir = (int) (random.nextDouble() * 4);
					continue;
				} else {
					grid[p.y - 1][p.x] = EMPTY;
					break;
				}
			} else if (dir == 3 && p.y < grid.length - 2
					&& grid[p.y + 1][p.x] != EMPTY) {
				if (grid[p.y + 2][p.x] != EMPTY) {
					grid[p.y + 1][p.x] = EMPTY;
					grid[p.y + 2][p.x] = EMPTY;
					p = new Point(p.x, p.y + 2);
					count = 0;
					if (random.nextDouble() < DEFAULT_RANDOMNESS)
						dir = (int) (random.nextDouble() * 4);
					continue;
				} else {
					grid[p.y + 1][p.x] = EMPTY;
					break;
				}
			}
			dir++;
			dir %= 4;
		}
	}

	/**
	 * Backs off all deadends by sparce spaces creating empty area in map
	 * 
	 * @param localSparceNess
	 */
	private void unwind(int localSparceNess) {
		for (int sparce = 0; sparce < localSparceNess; sparce++) {
			Vector ends = new Vector();
			for (int y = 1; y < grid.length-2; y++) {
				for (int x = 1; x < grid[y].length-2; x++) {
					if (grid[y][x] == EMPTY) {
						int count = 0;
						if (x > 0 && grid[y][x - 1] == EMPTY) {
							count++;
						}
						if (x < grid[y].length - 1 && grid[y][x + 1] == EMPTY) {
							count++;
						}
						if (y > 0 && grid[y - 1][x] == EMPTY) {
							count++;
						}
						if (y < grid.length - 1 && grid[y + 1][x] == EMPTY) {
							count++;
						}
						if (count == 1) {
							ends.addElement(new Point(x, y));
						}
					}
				}
			}
			for (int a = 0; a < ends.size(); a++) {
				Point p = (Point) ends.elementAt(a);
				grid[p.y][p.x] = SOLID;
			}
		}
	}

/**
 * Uses visited list to create maze.
 * 
 * @param p
 */
	private void follow(Point p,double randomness) {
		if (p.y<0 || p.x<0 || p.y>grid.length-1 || p.x>grid[p.y].length-1) return;
		grid[p.y][p.x] = EMPTY;
		int count = 0;
		int dir = (int) (random.nextDouble() * 4);

		while (true) {
			count++;
			if (count > 6) {
				break;
			}
			if (dir == 0) {
				if (p.x > 2 && grid[p.y][p.x - 2] != EMPTY) {
					grid[p.y][p.x - 1] = EMPTY;
					grid[p.y][p.x - 2] = EMPTY;
					p = new Point(p.x - 2, p.y);
					visited.addElement(p);
					count = 0;
					if (random.nextDouble() < randomness)
						dir = (int) (random.nextDouble() * 4);
					continue;
				}
			} else if (dir == 1) {
				if (p.x < grid[p.y].length - 2 && grid[p.y][p.x + 2] != EMPTY) {
					grid[p.y][p.x + 1] = EMPTY;
					grid[p.y][p.x + 2] = EMPTY;
					p = new Point(p.x + 2, p.y);
					visited.addElement(p);
					count = 0;
					if (random.nextDouble() < randomness)
						dir = (int) (random.nextDouble() * 4);

					continue;
				}
			} else if (dir == 2) {
				if (p.y > 2 && grid[p.y - 2][p.x] != EMPTY) {
					grid[p.y - 1][p.x] = EMPTY;
					grid[p.y - 2][p.x] = EMPTY;
					p = new Point(p.x, p.y - 2);
					visited.addElement(p);
					count = 0;
					if (random.nextDouble() < randomness)
						dir = (int) (random.nextDouble() * 4);

					continue;
				}
			} else if (dir == 3) {
				if (p.y < grid.length - 2 && grid[p.y + 2][p.x] != EMPTY) {
					grid[p.y + 1][p.x] = EMPTY;
					grid[p.y + 2][p.x] = EMPTY;
					p = new Point(p.x, p.y + 2);
					visited.addElement(p);
					count = 0;
					if (random.nextDouble() < randomness)
						dir = (int) (random.nextDouble() * 4);

					continue;
				}
			}
			dir++;
			dir %= 4;

		}
		visited.removeElement(p);
		return;
	}
/**
 * Displays the dungeon to System.out
 *
 */
	private void show() {
		for (int a = 0; a < grid.length; a++) {
			for (int b = 0; b < grid[a].length; b++) {
				System.out.print(grid[a][b]);
			}
			System.out.println("");
		}
	}
/**
 * Initilizes the dungeon to solid in preperation of digging out
 * 
 * @param x
 * @param y
 * @return char[][] of SOLIDs
 */
	private char[][] createSolidGrid(int x, int y) {
		char t[][] = new char[y][];
		for (int a = 0; a < y; a++) {
			t[a] = new char[x];
			for (int b = 0; b < x; b++) {
				t[a][b] = SOLID;
			}
		}
		return t;
	}

	/**
	 * Returns the grid generated.
	 * 
	 * @return the char[][] of grid date of the dungeon
	 */
	public char[][] getGrid() {
		return grid;
	}
}
