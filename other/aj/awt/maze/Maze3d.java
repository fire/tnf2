package aj.awt.maze;

import java.awt.Canvas;
import java.awt.Graphics;
import java.util.Vector;

import aj.awt.Point3d;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Maze3d extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	char maze[][][];


	/**
	 *  Constructor for the Maze3d object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public Maze3d(String s[]) {
		if (s.length!=3) {
			System.out.println("FORMAT: java aj.awt.maze.Maze3d <depth> <width> <height> ");
			System.exit(0);
		}
		try {
			maze = makeMaze(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), false);
			view(maze);
		}
		catch (NumberFormatException NFE) {
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void paint(Graphics g) {
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		//      maze=makeMaze(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2]),false);
		//      view(maze);
		//    Frame f=new Frame();
		//    f.addWindowListener(new SimpleWindowManager());
		//   f.add(new Maze(s));
		//    f.setSize(400,400);
		//    f.setVisible(true);
		new Maze3d(s);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  maz  Description of Parameter 
	 */
	public static void view(char maz[][][]) {
		System.out.println();
		for (int a=0; a < maz.length; a++) {
			for (int b = 0; b < maz[a].length; b++) {
				for (int c = 0; c < maz[a][b].length; c++) {
					System.out.print(maz[a][b][c]);
				}
				System.out.println();
			}
			System.out.println();
		}
		for (int a=0; a < maz.length; a++) {
			for (int b = 0; b < maz[a].length; b++) {
				for (int c = 0; c < maz[a][b].length; c++) {
					if (maz[a][b][c]==' ' && a<maz.length-1 && maz[a+1][b][c]==' ')
						System.out.println("Transform { translation "+c*2.5+" "+a*-2.5+" "+b*2.5+" children [ USE MyOpenSlab ]},");
					else if (maz[a][b][c]==' ')
//					if (maz[a][b][c]==' ')
						System.out.println("Transform { translation "+c*2.5+" "+a*-2.5+" "+b*2.5+" children [ USE MySlab ]},");
				}
			}
		}


	}
/*
starb x y z stare
wallb x y z walle

xxxx
xxxx  = wall
xxxx
xxxx

  xx
  xx
xxxx = star
xxxx

 x
  x
   x
*/

	/**
	 *  Description of the Method 
	 *
	 *@param  maz  Description of Parameter 
	 */
	public static void newView(char maz[][][]) {
		//find all hroizonal planes   slab X Y Z
		//find all up stairs  stairway X Y Z
		//stair way = 3/4 roof and steps
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  d         Description of Parameter 
	 *@param  w         Description of Parameter 
	 *@param  h         Description of Parameter 
	 *@param  connect8  Description of Parameter 
	 *@return           Description of the Returned Value 
	 */
	public static char[][][] makeMaze(int d, int w, int h, boolean connect8) {
		char maz[][][] = new char[d][h][w];
		//clear maze
		//clear maze
		//clear maze
		for (int a=0; a < h; a++) {
			for (int b = 0; b < w; b++) {
				for (int c = 0; c < d; c++) {
					maz[c][b][a] = 'X';
				}
			}
		}
		//System.out.println("fresh");
		//    view(maz);
		Vector active = new Vector();
		int tz = (int) (Math.random() * d);
		int ty = (int) (Math.random() * h);
		int tx = (int) (Math.random() * w);
		active.addElement(new Point3d(tx, ty, tz));
		//make active
		maz[tz][ty][tx] = ' ';

		while (active.size() > 0) {
			//      Point3d p=(Point3d)active.elementAt((int)(Math.random()*active.size()));//chose random active
			//      Point3d p=(Point3d)active.elementAt(0);//(int)(Math.random()*active.size()));//chose random active
			Point3d p = (Point3d) active.elementAt(active.size() - 1);
			//chose random active
			//System.out.println(active.size()+"to do.  Working from "+p.x+" "+p.y+ " "+p.z);
			//if all dir close remove from active
			if (canMove(maz, p)) {
				//System.out.println("can move! begin");
				int dir = (int) (Math.random() * 6);
				while (true) {
					if (dir == 0 && p.x > 0 && adjacent(maz, new Point3d(p.x - 1, p.y, p.z)) < 2 && maz[p.z][p.y][p.x - 1] != ' ') {
						maz[p.z][p.y][p.x - 1] = ' ';
						active.add(new Point3d(p.x - 1, p.y, p.z));
						break;
					}
					if (dir == 1 && p.x < maz[p.z][p.y].length - 1 && adjacent(maz, new Point3d(p.x + 1, p.y, p.z)) < 2 && maz[p.z][p.y][p.x + 1] != ' ') {
						maz[p.z][p.y][p.x + 1] = ' ';
						active.add(new Point3d(p.x + 1, p.y, p.z));
						break;
					}
					if (dir == 2 && p.y > 0 && adjacent(maz, new Point3d(p.x, p.y - 1, p.z)) < 2 && maz[p.z][p.y - 1][p.x] != ' ') {
						maz[p.z][p.y - 1][p.x] = ' ';
						active.add(new Point3d(p.x, p.y - 1, p.z));
						break;
					}
					if (dir == 3 && p.y < maz[p.z].length - 1 && adjacent(maz, new Point3d(p.x, p.y + 1, p.z)) < 2 && maz[p.z][p.y + 1][p.x] != ' ') {
						maz[p.z][p.y + 1][p.x] = ' ';
						active.add(new Point3d(p.x, p.y + 1, p.z));
						break;
					}
					if (dir == 4 && p.z > 0 && adjacent(maz, new Point3d(p.x, p.y, p.z - 1)) < 2 && maz[p.z - 1][p.y][p.x] != ' ') {
						maz[p.z - 1][p.y][p.x] = ' ';
						active.add(new Point3d(p.x, p.y, p.z - 1));
						break;
					}
					if (dir == 5 && p.z < maz.length - 1 && adjacent(maz, new Point3d(p.x, p.y, p.z + 1)) < 2 && maz[p.z + 1][p.y][p.x] != ' ') {
						maz[p.z + 1][p.y][p.x] = ' ';
						active.add(new Point3d(p.x, p.y, p.z + 1));
						break;
					}
					dir++;
					dir = dir % 6;
				}
				//Point3d p3=(Point3d)active.elementAt(active.size()-1);
				//System.out.println("do move from "+p.x+" "+p.y+" "+p.z+" to "+p3.x+" "+p3.y+" "+p3.z+" dir="+dir);
				//view (maz);
			}
			else {
				//System.out.println("blocked!");
				active.removeElement(p);
			}
		}
		return maz;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  maz  Description of Parameter 
	 *@param  p    Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public static boolean canMove(char[][][] maz, Point3d p) {
		//System.out.println("check can move to "+p.x+" "+p.y+" "+p.z);
		if (p.x > 0 && adjacent(maz, new Point3d(p.x - 1, p.y, p.z)) < 2 && maz[p.z][p.y][p.x - 1] != ' ') {
			return true;
		}
		if (p.x < maz[p.z][p.y].length - 1 && adjacent(maz, new Point3d(p.x + 1, p.y, p.z)) < 2 && maz[p.z][p.y][p.x + 1] != ' ') {
			return true;
		}
		if (p.y > 0 && adjacent(maz, new Point3d(p.x, p.y - 1, p.z)) < 2 && maz[p.z][p.y - 1][p.x] != ' ') {
			return true;
		}
		if (p.y < maz[p.z].length - 1 && adjacent(maz, new Point3d(p.x, p.y + 1, p.z)) < 2 && maz[p.z][p.y + 1][p.x] != ' ') {
			return true;
		}
		if (p.z > 0 && adjacent(maz, new Point3d(p.x, p.y, p.z - 1)) < 2 && maz[p.z - 1][p.y][p.x] != ' ') {
			return true;
		}
		if (p.z < maz.length - 1 && adjacent(maz, new Point3d(p.x, p.y, p.z + 1)) < 2 && maz[p.z + 1][p.y][p.x] != ' ') {
			return true;
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  maz  Description of Parameter 
	 *@param  p    Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public static int adjacent(char[][][] maz, Point3d p) {
		int num = 0;
		if (p.x > 0 && maz[p.z][p.y][p.x - 1] == ' ') {
			num++;
		}
		if (p.x < maz[p.z][p.y].length - 1 && maz[p.z][p.y][p.x + 1] == ' ') {
			num++;
		}
		if (p.y > 0 && maz[p.z][p.y - 1][p.x] == ' ') {
			num++;
		}
		if (p.y < maz[p.z].length - 1 && maz[p.z][p.y + 1][p.x] == ' ') {
			num++;
		}
		if (p.z > 0 && maz[p.z - 1][p.y][p.x] == ' ') {
			num++;
		}
		if (p.z < maz.length - 1 && maz[p.z + 1][p.y][p.x] == ' ') {
			num++;
		}
		//System.out.println("adjacent "+p.x+" "+p.y+" "+p.z+" ="+num);
		return num;
	}

}


/*
 * static char horz='-',vert='I',wall='H';
 * static stst1='>',st2='<',st3='/',st4='\\',st5='^',st6='V';
 * static char st[]={'H','I','-','>',']','<','[','V','v','^','`','/','\\'};
 * static char[][] follow={{'H^`Vv>]<['},{'I/\\>]<['},{'-\\/>]<['},
 * {'>]<IH'},{']H>[I'},{'<H>[I'},{'[<]HI'},
 * {'VvH-^'},{'vVH-V'},{'^`VH-'},{'`^vH-'},
 * {'/\\I-'},{'\\/I-'}};

6 wall types (w or w/o door or window)
6 stair types (w or w/o rail)

room =  6 sides cube
  path f/b, r/l, u/d
  open (no walls)
  
  path down = front blocked

 *  *  */



