package aj.awt.maze;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

import aj.awt.SimpleWindowManager;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Maze extends Canvas {

	char maze[][];


	/**
	 *  Constructor for the Maze object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public Maze(String s[]) {
		if (s.length!=2) {
			System.out.println("FORMAT: java aj.awt.maze.Maze <width> <height> ");
			System.exit(0);
		}
		try {
			maze = makeMaze(Integer.parseInt(s[0]), Integer.parseInt(s[1]), false);
			for (int a = 0; a < maze.length; a++) {
				for (int b = 0; b < maze[a].length; b++) {
					if (maze[a][b] == 'S' || maze[a][b] == 'E') {
						maze[a][b] = ' ';
						if (a == 1) {
							maze[0][b] = ' ';
						}
						else if (b == 1) {
							maze[a][0] = ' ';
						}
						else if (a == maze[0].length - 2) {
							maze[maze[0].length - 1][b] = ' ';
						}
						else if (b == maze.length - 2) {
							maze[a][maze[a].length - 1] = ' ';
						}
					}
				}
			}

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
		g.translate(15,15);
		int w;
		int h;
		int a;
		int b;
		w = getSize().width-25;
		h = getSize().height-25;
		int mh = maze.length;
		int mw = maze[0].length;
		double hm = h * 1.0 / mh;
		double wm = w * 1.0 / mw;
		for (a = 0; a < mh; a++) {
			for (b = 0; b < mw; b++) {
				char c = maze[a][b];
				if (c != ' ' && c != 'S' && c != 'E') {
					//System.out.println("a="+a+"b="+b);
					//          if (a>0 && maze[a-1][b]!=' ') g.drawLine((int)(b*wm),(int)((a-1)*hm),(int)(b*wm),(int)(a*hm));
					if (a < maze.length - 1 && maze[a + 1][b] != ' ') {
						g.drawLine((int) (b * wm), (int) ((a + 1) * hm), (int) (b * wm), (int) (a * hm));
					}
					//          if (b>0 && maze[a][b-1]!=' ') g.drawLine((int)((b-1)*wm),(int)(a*hm),(int)(b*wm),(int)(a*hm));
					if (b < maze[a].length - 1 && maze[a][b + 1] != ' ') {
						g.drawLine((int) ((b + 1) * wm), (int) (a * hm), (int) (b * wm), (int) (a * hm));
					}
				}
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		Frame f = new Frame();
		f.addWindowListener(new SimpleWindowManager());
		f.add(new Maze(s));
		//    f.pack();
		f.setSize(400, 400);
		f.setVisible(true);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  maz  Description of Parameter 
	 */
	public static void view(char maz[][]) {
		System.out.println();
		int a;
		int b;
		for (a = 0; a < maz.length; a++) {
			for (b = 0; b < maz[a].length; b++) {
				System.out.print(maz[a][b]);
			}
			System.out.println();
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  w         Description of Parameter 
	 *@param  h         Description of Parameter 
	 *@param  connect8  Description of Parameter 
	 *@return           Description of the Returned Value 
	 */
	public static char[][] makeMaze(int w, int h, boolean connect8) {
		char maz[][] = new char[h][w];
		int a;
		int b;
		for (a = 0; a < h; a++) {
			for (b = 0; b < w; b++) {
				if (a == 0 || b == 0 || a == h - 1 || b == w - 1) {
					maz[a][b] = 'B';
				}
				else if (a % 2 == 0 && b % 2 == 0) {
					maz[a][b] = 'C';
				}
				else {
					maz[a][b] = 'X';
				}
			}
		}
		//System.out.println("fresh");
		//    view(maz);
		Vector active = new Vector();
		//choose start
		Point start;
		Point end;
		if (Math.random() * 2 > 1) {
			//top bottem
			if (Math.random() * 2 > 1) {
				//top
				start = new Point((int) (Math.random() * (w / 2)) * 2 + 1, 1);
				end = new Point((int) (Math.random() * (w / 2)) * 2 + 1, h - 2);
			}
			else {
				//bottem
				start = new Point((int) (Math.random() * (w / 2)) * 2 + 1, h - 2);
				end = new Point((int) (Math.random() * (w / 2)) * 2 + 1, 1);
			}
		}
		else {
			//right left
			if (Math.random() * 2 > 1) {
				//right
				start = new Point(w - 2, (int) (Math.random() * (h / 2)) * 2 + 1);
				end = new Point(1, (int) (Math.random() * (h / 2)) * 2 + 1);
			}
			else {
				//left
				start = new Point(1, (int) (Math.random() * (h / 2)) * 2 + 1);
				end = new Point(w - 2, (int) (Math.random() * (h / 2)) * 2 + 1);
			}
		}
		active.addElement(start);
		//make active
		//System.out.println("added start");
		//    view(maz);
		while (active.size() > 0) {
			//      Point p=(Point)active.elementAt((int)(Math.random()*active.size()));//chose random active
			//      Point p=(Point)active.elementAt(0);//(int)(Math.random()*active.size()));//chose random active
			Point p = (Point) active.elementAt(active.size() - 1);
			//chose random active
			//if all dir close remove from active
			if ((p.x > 1 && maz[p.y][p.x - 2] == 'X') || 
					(p.y > 1 && maz[p.y - 2][p.x] == 'X') || 
					(p.x < maz[p.y].length - 2 && maz[p.y][p.x + 2] == 'X') || 
					(p.y < maz.length - 2 && maz[p.y + 2][p.x] == 'X')) {
				//        view(maz);
				//System.out.println("working "+p.x+","+p.y);
				int dir = (int) (Math.random() * 4);
				//chose random dir
				Point p2 = null;
				while (true) {
					//System.out.println("dir="+dir);
					if (dir == 0 && p.y > 1 && maz[p.y - 2][p.x] == 'X') {
						maz[p.y - 1][p.x] = ' ';
						//open and add to active
						maz[p.y - 2][p.x] = ' ';
						//open and add to active
						p2 = new Point(p.x, p.y - 2);
						//            System.out.println("add "+p2.x+","+p2.y+" dir="+dir);
						active.addElement(p2);
						break;
					}
					if (dir == 1 && p.y < maz.length - 2 && maz[p.y + 2][p.x] == 'X') {
						maz[p.y + 1][p.x] = ' ';
						//open and add to active
						maz[p.y + 2][p.x] = ' ';
						//open and add to active
						p2 = new Point(p.x, p.y + 2);
						//            System.out.println("add "+p2.x+","+p2.y+" dir="+dir);
						active.addElement(p2);
						break;
					}
					if (dir == 2 && p.x > 1 && maz[p.y][p.x - 2] == 'X') {
						maz[p.y][p.x - 1] = ' ';
						//open and add to active
						maz[p.y][p.x - 2] = ' ';
						//open and add to active
						p2 = new Point(p.x - 2, p.y);
						//            System.out.println("add "+p2.x+","+p2.y+" dir="+dir);
						active.addElement(p2);
						break;
					}
					if (dir == 3 && p.x < maz[p.y].length - 2 && maz[p.y][p.x + 2] == 'X') {
						maz[p.y][p.x + 1] = ' ';
						//open and add to active
						maz[p.y][p.x + 2] = ' ';
						//open and add to active
						p2 = new Point(p.x + 2, p.y);
						//            System.out.println("add "+p2.x+","+p2.y+" dir="+dir);
						active.addElement(p2);
						break;
					}
					//          System.out.println("blocked");
					dir = (dir + 1) % 4;
				}
				//        view(maz);
			}
			else {
				active.removeElement(p);
				//        view(maz);
				//        System.out.println("remove "+p.x+","+p.y);
			}
		}
		maz[start.y][start.x] = 'S';
		maz[end.y][end.x] = 'E';
		return maz;
	}

}
