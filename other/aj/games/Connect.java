package aj.games;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import aj.awt.SimpleWindowManager;

/*
 * Connect lines and make box game.
 */

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Connect extends Canvas implements MouseListener, MouseMotionListener {
	private Vector allLines;
	private Vector allSquares;
	private Vector[] moves;

	private int player = 0;

	private Point start, last;
	/**
	 *  Description of the Field 
	 */
	public static int numPlayers = 4;

	private static int SIZE = 30, OVAL = 4, SQRS = 5;
	private static Color myColors[] = {Color.blue, Color.red, Color.green, Color.yellow};


	/**
	 *  Constructor for the Connect object 
	 */
	public Connect() {
		allLines = new Vector();
		allSquares = new Vector();
		moves = new Vector[numPlayers];
		int a;
		for (a = 0; a < numPlayers; a++) {
			moves[a] = new Vector();
		}
		addMouseListener(this);
		addMouseMotionListener(this);
	}


	/**
	 *  Gets the MinimumSize attribute of the Connect object 
	 *
	 *@return    The MinimumSize value 
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}


	/**
	 *  Gets the MaximumSize attribute of the Connect object 
	 *
	 *@return    The MaximumSize value 
	 */
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}


	/**
	 *  Gets the PreferredSize attribute of the Connect object 
	 *
	 *@return    The PreferredSize value 
	 */
	public Dimension getPreferredSize() {
		return new Dimension(SIZE * (SQRS - 1), SIZE * (SQRS - 1));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void update(Graphics g) {
		paint(g);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void paint(Graphics g) {
		int a;
		int b;
		for (a = 0; a < SQRS; a++) {
			for (b = 0; b < SQRS; b++) {
				g.drawOval(a * SIZE - OVAL / 2, b * SIZE - OVAL / 2, OVAL, OVAL);
			}
		}
		for (a = 0; a < numPlayers; a++) {
			//System.out.println("moves for "+a+" ="+moves[a].size());
			g.setColor(myColors[a]);
			for (b = 0; b < moves[a].size(); b++) {
				MyLine o = (MyLine) moves[a].elementAt(b);
				o.drawSelf(g, SIZE);
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseClicked(MouseEvent ME) {
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseEntered(MouseEvent ME) {
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseExited(MouseEvent ME) {
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseMoved(MouseEvent ME) {
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mousePressed(MouseEvent ME) {
		int x = ME.getX();
		int y = ME.getY();
		x = (x + SIZE / 2) / SIZE;
		y = (y + SIZE / 2) / SIZE;
		last = new Point(x, y);
		start = new Point(x, y);
		xordraw();
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseReleased(MouseEvent ME) {
		int x = ME.getX();
		int y = ME.getY();
		x = (x + SIZE / 2) / SIZE;
		y = (y + SIZE / 2) / SIZE;
		xordraw();
		last = new Point(x, y);
		addMove(start, last);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseDragged(MouseEvent ME) {
		int x = ME.getX();
		int y = ME.getY();
		x = (x + SIZE / 2) / SIZE;
		y = (y + SIZE / 2) / SIZE;
		xordraw();
		last = new Point(x, y);
		xordraw();
	}


	/**
	 *  Description of the Method 
	 */
	public void xordraw() {
		Graphics g = getGraphics();
		g.setColor(myColors[player]);
		g.setXORMode(Color.white);
		g.drawLine(start.x * SIZE, start.y * SIZE, last.x * SIZE, last.y * SIZE);
	}


	/**
	 *  Adds a feature to the Move attribute of the Connect object 
	 *
	 *@param  s  The feature to be added to the Move attribute 
	 *@param  t  The feature to be added to the Move attribute 
	 */
	public void addMove(Point s, Point t) {
		if (s.x == t.x && s.y == t.y) {
			return;
		}
		if (s.x != t.x && s.y != t.y) {
			return;
		}
		if (Math.abs(s.x - t.x) > 1 || Math.abs(s.y - t.y) > 1) {
			return;
		}
		MyLine l = new MyLine(s, t);
		if (find(allLines, l)) {
			return;
		}
		//    System.out.println("new line added");
		allLines.addElement(l);
		moves[player].addElement(l);
		if (!checkFill()) {
			nextPlayer();
		}
		repaint();
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public boolean checkFill() {
		int a;
		int b;
		//    System.out.println("Square test");
		boolean freeturn = false;
		for (a = 0; a < SIZE; a++) {
			for (b = 0; b < SIZE; b++) {
				Point o1 = new Point(a, b);
				Point o2 = new Point(a, b + 1);
				Point o3 = new Point(a + 1, b + 1);
				Point o4 = new Point(a + 1, b);
				MyLine l1 = new MyLine(o1, o2);
				MyLine l2 = new MyLine(o2, o3);
				MyLine l3 = new MyLine(o3, o4);
				MyLine l4 = new MyLine(o4, o1);
				if (find(allLines, l1) && find(allLines, l2) && 
						find(allLines, l3) && find(allLines, l4)) {
					//        System.out.println("Square filled found");
					MyLine sl = new MyLine(o1, o3);
					MyLine s2 = new MyLine(o2, o4);
					if (!find(allSquares, sl) && !find(allSquares, s2)) {
						//          System.out.println("Square filled first fill");
						allSquares.addElement(sl);
						moves[player].addElement(sl);
						freeturn = true;
					}
				}
			}
		}
		return freeturn;
	}


	/**
	 *  Description of the Method 
	 */
	public void nextPlayer() {
		player++;
		if (player >= numPlayers) {
			player = 0;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  v  Description of Parameter 
	 *@param  l  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean find(Vector v, MyLine l) {
		int a;
		for (a = 0; a < v.size(); a++) {
			MyLine ll = (MyLine) v.elementAt(a);
			if (ll.equals(l) || l.equals(ll)) {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		Frame f = new Frame("Connect");
		f.setLayout(new BorderLayout());
		f.add("Center", new Connect());
		f.setSize(200, 200);
		f.setVisible(true);
		f.addWindowListener(new SimpleWindowManager());
	}

}

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
class MyLine {


	Point s, t;


	/**
	 *  Constructor for the MyLine object 
	 *
	 *@param  ss  Description of Parameter 
	 *@param  tt  Description of Parameter 
	 */
	public MyLine(Point ss, Point tt) {
		s = ss;
		t = tt;
		if (s.x < t.x || (s.x == t.x && s.y < t.y)) {
			Point p = s;
			s = t;
			t = p;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  l  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean equals(MyLine l) {
		if ((s.x == l.s.x && s.y == l.s.y && t.y == l.t.y && t.x == l.t.x)) {
			return true;
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g     Description of Parameter 
	 *@param  size  Description of Parameter 
	 */
	public void drawSelf(Graphics g, int size) {
		if (Math.abs(s.x - t.x) + Math.abs(s.y - t.y) == 2) {
			drawSquare(g, size);
		}
		else {
			drawLine(g, size);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g     Description of Parameter 
	 *@param  size  Description of Parameter 
	 */
	public void drawLine(Graphics g, int size) {
		if (s.x != t.x) {
			g.fillRect(t.x * size + 1, t.y * size - 1, size, 3);
		}
		else {
			g.fillRect(t.x * size - 1, t.y * size + 1, 3, size);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g     Description of Parameter 
	 *@param  size  Description of Parameter 
	 */
	public void drawSquare(Graphics g, int size) {
		int x = Math.min(s.x, t.x);
		int y = Math.min(s.y, t.y);
		g.fillRect(x * size + 1, y * size + 1, size - 2, size - 2);
	}
}
