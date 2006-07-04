package aj.chess;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

public class HumanPlayer extends Canvas implements ActionListener,
		MouseMotionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Board b;

	boolean live = false;

	// EVENT FUNCITONS
	int sx, sy, ex, ey;

	boolean down = false;

	String allGame = "";

	Vector list = new Vector();

	Vector moves = new Vector();

	static TextField Move = new TextField(10), Stat = new TextField("Waiting"),
			Last = new TextField(10);

	static boolean viewonly = false;

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

	public void setLive(boolean t) {
		if (t) {
			live = t;
			Move.setText("");
			Move.setEditable(true);
			Move.setBackground(Color.white);
			Stat.setText((b.getNextMove() == Board.BLACK ? "BLACK" : "WHITE")
					+ "Ready.");
		} else {
			live = t;
			Move.setEditable(false);
			Move.setBackground(Color.lightGray);
			Stat.setText("Waiting.");
		}
	}

	public void set(Board b) {
		this.b = b;
		moves = b.getAllMoves();
		repaint();
	}

	public int getSquare(int x, int y) {
		Dimension d = getSize();
		int dw = d.width / 8;
		int dh = d.height / 8;
		x = x / dw;
		y = y / dh;
		// System.out.println("Square ="+(y*8+x));
		return y * 8 + x;
	}

	public Dimension getPreferredSize() {
		return new Dimension(100, 100);
	}

	public synchronized void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		if (ae.getSource() == Move && live) {
			if (moves.contains(s)) {
				doMove(s);
				repaint();
				setLive(false);
			}
		}
	}

	public void paint(Graphics g) {
		// System.out.println("Paint called");
		g.setFont(new Font("TimesRoman", Font.PLAIN, 18));
		Dimension d = getSize();
		int c;
		int dw = d.width / 8;
		int dh = d.height / 8;
		for (int a = 0; a < 8; a++) {
			for (c = 0; c < 8; c++) {
				if ((c % 2 + a % 2) % 2 == 0) {
					g.setColor(Color.lightGray);
				} else {
					g.setColor(Color.red);
				}
				g.fillRect(a * dw, c * dh, dw, dh);
				g.setColor(Color.black);
				g.drawRect(a * dw, c * dh, dw, dh);
			}
		}
		for (int a = 0; a < 64 && b != null; a++) {
			int row = a / 8;
			int col = a % 8;
			String cc = "" + b.getMap(a);
			boolean white = b.isWhite(cc);
			g.setColor((white ? Color.white : Color.black));
			if (cc.equalsIgnoreCase("p"))
				g.drawString(cc, col * dw + dw / 8, row * dh + dh / 2);
			else if (cc.equalsIgnoreCase("r"))
				g.drawString(cc, col * dw + dw / 8, row * dh + dh / 2);
			else if (cc.equalsIgnoreCase("h"))
				g.drawString(cc, col * dw + dw / 8, row * dh + dh / 2);
			else if (cc.equalsIgnoreCase("b"))
				g.drawString(cc, col * dw + dw / 8, row * dh + dh / 2);
			else if (cc.equalsIgnoreCase("q"))
				g.drawString(cc, col * dw + dw / 8, row * dh + dh / 2);
			else if (cc.equalsIgnoreCase("k"))
				g.drawString(cc, col * dw + dw / 8, row * dh + dh / 2);
			else {
				continue;
			}
		}
	}

	public void mouseClicked(MouseEvent ME) {
	}

	public void mouseEntered(MouseEvent ME) {
	}

	public void mouseExited(MouseEvent ME) {
	}

	public void mouseMoved(MouseEvent ME) {
	}

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
			for (int a = 0; a < moves.size(); a++) {
				String m = (String) moves.elementAt(a);
				if (m.startsWith(pos + ":") && m.indexOf(":" + pos2) >= 1) {
					doMove(m);
					repaint();
					return;
				}
			}
		}
	}

	public void mousePressed(MouseEvent ME) {
		ex = sx = ME.getX();
		ey = sy = ME.getY();
		int pos = getSquare(sx, sy);
		if (!live) {
			return;
		}
		for (int a = 0; a < moves.size(); a++) {
			String s = (String) moves.elementAt(a);
			if (s.startsWith(pos + ":")) {
				down = true;
				xodraw();
				break;
			}
		}
	}

	public void mouseDragged(MouseEvent ME) {
		if (!live || !down) {
			return;
		}
		xodraw();
		ex = ME.getX();
		ey = ME.getY();
		xodraw();
	}

	public void xodraw() {
		Graphics g = getGraphics();
		Dimension d = getSize();
		int dw = d.width / 8;
		int dh = d.height / 8;
		g.setXORMode(Color.gray);
		int dx = ex - sx;
		int dy = ey - sy;
		int a = getSquare(sx, sy);
		int row = a / 8;
		int col = a % 8;
		g.setColor(Color.black);
		g.fillOval(col * dw + dx, row * dh + dy, dw, dh);
	}

	public void doMove(String s) {
		for (int a = 0; a < moves.size(); a++) {
			String ss = (String) moves.elementAt(a);
			if (ss.equals(s) || ss.startsWith(s + ":")) {
				s = ss;
				break;
			}
		}
		allGame += s + ";";
		b = b.applyMove(s);
		ActionEvent AE = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, s);
		performAction(AE);
	}

	public void addActionListener(ActionListener AL) {
		if (!list.contains(AL) && AL != null) {
			list.addElement(AL);
		}
	}

	public void removeActionListener(ActionListener AL) {
		list.removeElement(AL);
	}

	public void performAction(ActionEvent AE) {
		Vector l = (Vector) list.clone();
		for (int a = 0; a < l.size(); a++) {
			ActionListener AL = (ActionListener) l.elementAt(a);
			AL.actionPerformed(AE);
		}
	}
}
