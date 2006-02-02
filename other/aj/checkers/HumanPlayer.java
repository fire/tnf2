package aj.checkers;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import aj.awt.SimpleWindowManager;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class HumanPlayer extends Canvas implements ActionListener, MouseMotionListener, MouseListener {
	Board b;
	boolean live = false;

	//EVENT FUNCITONS
	int sx, sy, ex, ey;
	boolean down = false;

	String allGame = "";

	Vector list = new Vector();

	static TextField Move = new TextField(10), 
			Stat = new TextField("Waiting"), 
			Last = new TextField(10);

	static boolean viewonly = false;


	/**
	 *  Constructor for the HumanPlayer object 
	 */
	public HumanPlayer() {
		live = false;
		addMouseMotionListener(this);
		addMouseListener(this);
		Move.addActionListener(this);
		Stat.setEditable(false);
		Stat.setBackground(Color.lightGray);
		Last.setEditable(false);
		Last.setBackground(Color.lightGray);
		Panel p = new Panel(new FlowLayout());
		p.add(Move);
		p.add(Last);
		p.add(Stat);
		Frame f = new Frame();
		f.add("Center", this);
		f.add("South", p);
		f.pack();
		f.setVisible(true);
		f.addWindowListener(new SimpleWindowManager());
	}


	/**
	 *  Sets the Live attribute of the HumanPlayer object 
	 *
	 *@param  t  The new Live value 
	 */
	public void setLive(boolean t) {
		if (t) {
			live = t;
			Move.setText("");
			Move.setEditable(true);
			Move.setBackground(Color.white);
			Stat.setText((b.getNextMove() == Board.RED ? "RED" : "WHITE") + "Ready.");
		}
		else {
			live = t;
			Move.setEditable(false);
			Move.setBackground(Color.lightGray);
			Stat.setText("Waiting.");
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  b  Description of Parameter 
	 */
	public void set(Board b) {
		this.b = b;
		repaint();
	}


	/**
	 *  Gets the Square attribute of the HumanPlayer object 
	 *
	 *@param  x  Description of Parameter 
	 *@param  y  Description of Parameter 
	 *@return    The Square value 
	 */
	public int getSquare(int x, int y) {
		Dimension d = getSize();
		int a;
		int c;
		int dw = d.width / 8;
		int dh = d.height / 8;
		x = x / dw;
		y = y / dh;
		return y * 4 + x / 2 + 1;
	}


	/**
	 *  Gets the PreferredSize attribute of the HumanPlayer object 
	 *
	 *@return    The PreferredSize value 
	 */
	public Dimension getPreferredSize() {
		return new Dimension(100, 100);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public synchronized void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		if (ae.getSource() == Move && live) {
			if (b.getMoves().contains(s)) {
				doMove(s);
				repaint();
				setLive(false);
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void paint(Graphics g) {
		Dimension d = getSize();
		int a;
		int c;
		int dw = d.width / 8;
		int dh = d.height / 8;
		for (a = 0; a < 8; a++) {
			for (c = 0; c < 8; c++) {
				if ((c % 2 + a % 2) % 2 == 0) {
					g.setColor(Color.black);
				}
				else {
					g.setColor(Color.red);
				}
				g.fillRect(a * dw, c * dh, dw, dh);
				g.setColor(Color.black);
				g.drawRect(a * dw, c * dh, dw, dh);
			}
		}
		for (a = 1; a < 33 && b != null; a++) {
			boolean red;
			boolean pawn;
			if (b.getMap(a) == Board.REDPAWN) {
				red = true;
				pawn = true;
			}
			else if (b.getMap(a) == Board.WHITEPAWN) {
				red = false;
				pawn = true;
			}
			else if (b.getMap(a) == Board.REDKING) {
				red = true;
				pawn = false;
			}
			else if (b.getMap(a) == Board.WHITEKING) {
				red = false;
				pawn = false;
			}
			else {
				continue;
			}
			int row = (a - 1) / 4;
			int col = (a - 1) % 4 * 2 + (row % 2 == 0 ? 1 : 0);
			g.setColor((red ? Color.red : Color.white));
			g.fillOval(col * dw + dw / 8, row * dh + dh / 8, dw - dw / 4, dh - dh / 4);
			g.setColor(Color.black);
			g.drawOval(col * dw + dw / 8, row * dh + dh / 8, dw - dw / 4, dh - dh / 4);
			if (!pawn) {
				g.drawOval(col * dw + dw / 4, row * dh + dh / 4, dw - dw / 2, dh - dh / 2);
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
	public void mouseReleased(MouseEvent ME) {
		if (!live || !down) {
			return;
		}
		xodraw();
		ex = ME.getX();
		ey = ME.getY();
		if (down) {
			down = false;
			int pos = getSquare(sx, sy);
			int pos2 = getSquare(ex, ey);
			int a;
			for (a = 0; a < b.getMoves().size(); a++) {
				String m = (String) b.getMoves().elementAt(a);
				if (m.startsWith(pos + "-") && m.endsWith("-" + pos2)) {
					doMove(m);
					repaint();
				}
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mousePressed(MouseEvent ME) {
		ex = sx = ME.getX();
		ey = sy = ME.getY();
		int pos = getSquare(sx, sy);
		if (!live) {
			return;
		}
		Vector v = b.getMoves();
		int a;
		for (a = 0; a < v.size(); a++) {
			String s = (String) v.elementAt(a);
			if (s.startsWith("" + pos)) {
				down = true;
				xodraw();
				break;
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseDragged(MouseEvent ME) {
		if (!live || !down) {
			return;
		}
		xodraw();
		ex = ME.getX();
		ey = ME.getY();
		xodraw();
	}


	/**
	 *  Description of the Method 
	 */
	public void xodraw() {
		Graphics g = getGraphics();
		Dimension d = getSize();
		int dw = d.width / 8;
		int dh = d.height / 8;
		g.setXORMode(Color.gray);
		int dx = ex - sx;
		int dy = ey - sy;
		int a = getSquare(sx, sy);
		int row = (a - 1) / 4;
		int col = (a - 1) % 4 * 2 + (row % 2 == 0 ? 1 : 0);
		g.setColor(Color.black);
		g.fillOval(col * dw + dx, row * dh + dy, dw, dh);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public void doMove(String s) {
		allGame += s + ";";
		b = b.applyMove(s);
		repaint();
		ActionEvent AE = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, s);
		performAction(AE);
	}


	/**
	 *  Adds a feature to the ActionListener attribute of the HumanPlayer object 
	 *
	 *@param  AL  The feature to be added to the ActionListener attribute 
	 */
	public void addActionListener(ActionListener AL) {
		if (!list.contains(AL) && AL != null) {
			list.addElement(AL);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  AL  Description of Parameter 
	 */
	public void removeActionListener(ActionListener AL) {
		list.removeElement(AL);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  AE  Description of Parameter 
	 */
	public void performAction(ActionEvent AE) {
		Vector l = (Vector) list.clone();
		int a;
		for (a = 0; a < l.size(); a++) {
			ActionListener AL = (ActionListener) l.elementAt(a);
			AL.actionPerformed(AE);
		}
	}

}
