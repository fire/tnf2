package aj.life;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*
 * Verison 2.0
 * static screen size
 * colors merge causing brown
 * semi-interactive
 * start stop capable
 * */
public class Life2 extends Applet implements ActionListener, MouseListener,
		MouseMotionListener {

	// speed
	// draw color
	// rules

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String s[]) {
		for (int a = 0; a < s.length; a++) {
			// if (s[a].toUpperCase().indexOf("SIZE")>=0
		}
		Frame F = new Frame("Life World 1.0");
		ld = new Life2Display();
		F.add("Center", ld);
		Panel p = new Panel(new FlowLayout());
		p.add(stop);
		p.add(step);
		Life2 l2 = new Life2();
		stop.addActionListener(l2);
		step.addActionListener(l2);
		F.add("North", p);
		ld.addMouseListener(l2);
		ld.addMouseMotionListener(l2);
		F.setVisible(true);
		F.pack();
		new Thread(ld).start();
	}

	public void init() {
		try {
			String s = getParameter("size");
			Life2Display.MAX = Integer.parseInt(s);
			s = getParameter("colonies");
			Life2Display.COLONIES = Integer.parseInt(s);
			s = getParameter("colsize");
			Life2Display.COLSIZE = Integer.parseInt(s);
			s = getParameter("minborn");
			Life2Display.MINBORN = Integer.parseInt(s);
			s = getParameter("maxordie");
			Life2Display.MAXORDIE = Integer.parseInt(s);

		} catch (NumberFormatException nfe) {
		}
		ld = new Life2Display();
		// this.setLayoutManager(new BorderLayout());
		this.add("Center", ld);
		Panel p = new Panel(new FlowLayout());
		p.add(stop);
		p.add(step);
		stop.addActionListener(this);
		step.addActionListener(this);
		this.add("North", p);
		ld.addMouseListener(this);
		ld.addMouseMotionListener(this);
		new Thread(ld).start();
	}

	static Life2Display ld;

	static Button stop = new Button("Stop");

	static Button step = new Button("Step");

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == stop) {
			ld.STOP = !ld.STOP;
			if (ld.STOP)
				stop.setLabel("Start");
			if (!ld.STOP)
				stop.setLabel("Stop");
		}
		if (ae.getSource() == step) {
			ld.next();
			ld.repaint();
			ld.STOP = true;
			if (ld.STOP)
				stop.setLabel("Start");
		}
	}

	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		ld.clickPoint(p);
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		ld.movePoint(p);
	}

	public void mouseMoved(MouseEvent e) {
	}

}

class Life2Display extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int COLONIES = 13;

	static int COLSIZE = 15;

	static int MINBORN = 2;

	static int MAXORDIE = 3;

	static int MAX = 53;

	Color map[][];

	boolean STOP = false;

	public Life2Display() {
		map = new Color[MAX][];
		for (int a = 0; a < MAX; a++) {
			map[a] = new Color[MAX];
			for (int b = 0; b < MAX; b++) {
				map[a][b] = Color.white;
			}
		}
		doRandomSetup();
	}

	Color clickColor = Color.blue;

	public void clickPoint(Point p) {
		double dx = Math.max(1, 1.0 * getWidth() / MAX);
		double dy = Math.max(1, 1.0 * getHeight() / MAX);
		int y = (int) (p.getY() / dy);
		int x = (int) (p.getX() / dx);

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x >= MAX)
			x = MAX - 1;
		if (y >= MAX)
			y = MAX - 1;

		if (map[x][y] == Color.white) {
			map[x][y] = clickColor;
		} else {
			clickColor = map[x][y];
			map[x][y] = Color.white;
		}
		repaint();
	}

	int moveX, moveY;

	public void movePoint(Point p) {
		double dx = Math.max(1, 1.0 * getWidth() / MAX);
		double dy = Math.max(1, 1.0 * getHeight() / MAX);
		int y = (int) (p.getY() / dy);
		int x = (int) (p.getX() / dx);

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x >= MAX)
			x = MAX - 1;
		if (y >= MAX)
			y = MAX - 1;

		if (moveX == x && moveY == y)
			return;
		moveX = x;
		moveY = y;
		if (map[x][y] == Color.white) {
			map[x][y] = clickColor;
		} else {
			map[x][y] = Color.white;
		}
		repaint();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
				if (STOP)
					continue;
				next();
				repaint();
			} catch (Exception ei) {
			}
		}
	}

	public void next() {
		Color next[][] = new Color[MAX][];
		for (int a = 0; a < MAX; a++) {
			next[a] = new Color[MAX];
			for (int b = 0; b < MAX; b++) {
				next[a][b] = Color.white;
			}
		}
		for (int a = 0; a < MAX; a++) {
			for (int b = 0; b < MAX; b++) {
				int c = count(a, b);
				if (map[a][b] == Color.white && c == 3) {
					next[a][b] = avColor(a, b);
				} else if (map[a][b] != Color.white && (c == 2 || c == 3)) {
					next[a][b] = avColor(a, b);
				}

				// else if ()
				// else next[a][b]=Color.white;

			}
		}
		map = next;
	}

	public Color addColor(Color c, int ccount, Color m) {
		if (ccount == 0)
			return m;

		int red = c.getRed() * ccount + m.getRed();
		int green = c.getGreen() * ccount + m.getGreen();
		int blue = c.getBlue() * ccount + m.getBlue();
		ccount++;
		red = (red / ccount) % 256;
		green = (green / ccount) % 256;
		blue = (blue / ccount) % 256;
		return new Color(red, green, blue);
	}

	public Color avColor(int y, int x) {
		Color c = Color.black;
		int ccount = 0;

		if (x - 1 >= 0 && y - 1 >= 0 && map[y - 1][x - 1] != Color.white) {
			c = addColor(c, ccount, map[y - 1][x - 1]);
			ccount++;
		}
		if (x - 1 >= 0 && y + 1 < MAX && map[y + 1][x - 1] != Color.white) {
			c = addColor(c, ccount, map[y + 1][x - 1]);
			ccount++;
		}
		if (x + 1 < MAX && y - 1 >= 0 && map[y - 1][x + 1] != Color.white) {
			c = addColor(c, ccount, map[y - 1][x + 1]);
			ccount++;
		}
		if (x + 1 < MAX && y + 1 < MAX && map[y + 1][x + 1] != Color.white) {
			c = addColor(c, ccount, map[y + 1][x + 1]);
			ccount++;
		}
		if (x - 1 >= 0 && map[y][x - 1] != Color.white) {
			c = addColor(c, ccount, map[y][x - 1]);
			ccount++;
		}
		if (x + 1 < MAX && map[y][x + 1] != Color.white) {
			c = addColor(c, ccount, map[y][x + 1]);
			ccount++;
		}
		if (y + 1 < MAX && map[y + 1][x] != Color.white) {
			c = addColor(c, ccount, map[y + 1][x]);
			ccount++;
		}
		if (y - 1 >= 0 && map[y - 1][x] != Color.white) {
			c = addColor(c, ccount, map[y - 1][x]);
			ccount++;
		}

		return c;
	}

	public int count(int y, int x) {
		int total = 0;
		// if (map[y][x]!=Color.white) total++;
		if (x - 1 >= 0 && y - 1 >= 0 && map[y - 1][x - 1] != Color.white)
			total++;
		if (x - 1 >= 0 && y + 1 < MAX && map[y + 1][x - 1] != Color.white)
			total++;
		if (x + 1 < MAX && y - 1 >= 0 && map[y - 1][x + 1] != Color.white)
			total++;
		if (x + 1 < MAX && y + 1 < MAX && map[y + 1][x + 1] != Color.white)
			total++;
		if (x - 1 >= 0 && map[y][x - 1] != Color.white)
			total++;
		if (x + 1 < MAX && map[y][x + 1] != Color.white)
			total++;
		if (y + 1 < MAX && map[y + 1][x] != Color.white)
			total++;
		if (y - 1 >= 0 && map[y - 1][x] != Color.white)
			total++;
		return total;
	}

	public void doRandomSetup() {
		for (int a = 0; a < COLONIES; a++) {
			int y = (int) (Math.random() * MAX);
			int x = (int) (Math.random() * MAX);
			byte t = (byte) (Math.random() * 3 + 1);
			Color ct = Color.blue;
			if (t == 2)
				ct = Color.red;
			if (t == 3)
				ct = Color.green;
			int num = (byte) (Math.random() * COLSIZE / 2 + COLSIZE / 2);
			for (int b = 0; b < num; b++) {
				int lx = x
						+ (int) (Math.random() * Math.pow(COLSIZE, .9) - (Math
								.pow(COLSIZE, .9) / 2));
				int ly = y
						+ (int) (Math.random() * Math.pow(COLSIZE, .9) - (Math
								.pow(COLSIZE, .9) / 2));
				if (lx < 0)
					lx = 0;
				if (ly < 0)
					ly = 0;
				if (lx >= MAX)
					lx = MAX - 1;
				if (ly >= MAX)
					ly = MAX - 1;
				map[ly][lx] = ct;
			}
		}
	}

	Image i;

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (g == null || map == null)
			return;

		if (i == null) {
			i = createImage(getSize().width, getSize().height);
		} else if (i.getHeight(null) != getSize().height
				|| i.getWidth(null) != getSize().width) {
			i = createImage(getSize().width, getSize().height);
		}
		Graphics G = i.getGraphics();
		G.clearRect(0, 0, i.getWidth(this), i.getHeight(this));

		double dx = Math.max(1, 1.0 * getWidth() / MAX);
		double dy = Math.max(1, 1.0 * getHeight() / MAX);
		for (int a = 0; a < MAX; a++) {
			for (int b = 0; b < MAX; b++) {
				if (map[a][b] == Color.white)
					continue;
				G.setColor(map[a][b]);
				G.fillRect((int) (dx * a), (int) (dy * b), (int) (dx),
						(int) (dy));
			}
		}
		g.drawImage(i, 0, 0, this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}
}

// rules - life born 23/3 liv/born
// high life 23/36
// lacy /234
// amazing 12345/3
// rats 1234/37
// coagulation 125678/367
// coral 45678/3
// 235678/3678

