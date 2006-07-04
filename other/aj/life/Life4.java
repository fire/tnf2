package aj.life;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Life4 extends JApplet implements ActionListener, MouseListener,
		MouseMotionListener {

	// add tool (check)
	// infinate (check)
	// multi colors (check)
	// merge colors (check)
	// cut back/size shrink 1.2 (check)
	//
	// load/save 1.1
	// choose draw color 1.3
	// clear/reset 1.4
	// choose speed/skip 1.9
	// check points 2.2
	// var/choose rules 2.3
	// age cells and color 2.5
	// zoom in area (right click box) 2.7
	// choose wraparound 2.9
	// color chooser 3.1
	// color comment file
	// multi-select/color regeion 3.9
	// check point regions 3.8
	// web deply 4.0
	// link web libraries 5.0
	// multi rules - color dependant 6.0
	// 2/23 - red - red orange purple
	// HOW to let edges drift off?
	// count number of areas in border
	// look for empty rows and cut if wide enough

	// file - O.lif

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String s[]) {
		// TODO add read ".lif" file type
		for (int a = 0; a < s.length; a++) {
			// if (s[a].toUpperCase().indexOf("SIZE")>=0
		}
		JFrame F = new JFrame("Life World 4.0");
		F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ld = new Life4Display();
		F.getContentPane().add("Center", ld);
		JPanel p = new JPanel(new FlowLayout());
		p.add(stop);
		p.add(step);
		Life4 l2 = new Life4();
		stop.addActionListener(l2);
		step.addActionListener(l2);
		F.getContentPane().add("North", p);
		ld.addMouseListener(l2);
		ld.addMouseMotionListener(l2);
		F.setVisible(true);
		F.pack();
		new Thread(ld).start();
	}

	public void init() {
		try {
			String s = getParameter("size");
			Life4Display.startMAX = Integer.parseInt(s);
			s = getParameter("colonies");
			Life4Display.COLONIES = Integer.parseInt(s);
			s = getParameter("colsize");
			Life4Display.COLSIZE = Integer.parseInt(s);

		} catch (NumberFormatException nfe) {
		}
		ld = new Life4Display();
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

	static Life4Display ld;

	static JButton stop = new JButton("Stop");

	static JButton step = new JButton("Step");

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == stop) {
			ld.STOP = !ld.STOP;
			if (ld.STOP)
				stop.setText("Start");
			if (!ld.STOP)
				stop.setText("Stop");
		}
		if (ae.getSource() == step) {
			ld.next();
			ld.repaint();
			ld.STOP = true;
			if (ld.STOP)
				stop.setText("Start");
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

class Life4Display extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int COLONIES = 1;

	static int COLSIZE = 24;

	static int startMAX = 23;

	int maxx = startMAX, maxy = startMAX;

	Color map[][];

	boolean STOP = false;

	public Life4Display() {
		map = new Color[startMAX][];
		for (int a = 0; a < startMAX; a++) {
			map[a] = new Color[startMAX];
			for (int b = 0; b < startMAX; b++) {
				map[a][b] = Color.white;
			}
		}
		doRandomSetup();
	}

	Color clickColor = Color.blue;

	public void clickPoint(Point p) {
		double dx = Math.max(1, 1.0 * getWidth() / maxx);
		double dy = Math.max(1, 1.0 * getHeight() / maxy);
		int y = (int) (p.getY() / dy);
		int x = (int) (p.getX() / dx);

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x >= maxx)
			x = maxx - 1;
		if (y >= maxy)
			y = maxy - 1;

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
		double dx = Math.max(1, 1.0 * getWidth() / maxx);
		double dy = Math.max(1, 1.0 * getHeight() / maxy);
		int y = (int) (p.getY() / dy);
		int x = (int) (p.getX() / dx);

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x >= maxx)
			x = maxx - 1;
		if (y >= maxy)
			y = maxy - 1;

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
		boolean morex = false, lessx = false, morey = false, lessy = false;
		boolean cutx = true, cuty = true, cutx2 = true, cuty2 = true;
		Color next[][] = new Color[maxx][];
		for (int a = 0; a < maxx; a++) {
			next[a] = new Color[maxy];
			for (int b = 0; b < maxy; b++) {
				next[a][b] = Color.white;
			}
		}
		int MINBOUND = 3;
		int MAXBOUND = 7;
		for (int a = 0; a < maxx; a++) {
			for (int b = 0; b < maxy; b++) {
				int c = count(a, b);
				if (map[a][b] == Color.white && c == 3) {
					next[a][b] = avColor(a, b);
					if (a <= MINBOUND)
						lessx = true;
					else if (a >= maxx - MINBOUND)
						morex = true;
					if (b < MINBOUND)
						lessy = true;
					else if (b >= maxy - MINBOUND)
						morey = true;
				} else if (map[a][b] != Color.white && (c == 2 || c == 3)) {
					next[a][b] = avColor(a, b);
					if (a < MAXBOUND)
						cutx2 = false;
					else if (a > maxx - MAXBOUND)
						cutx = false;
					if (b < MAXBOUND)
						cuty2 = false;
					else if (b > maxy - MAXBOUND)
						cuty = false;
				}
			}
		}
		if (maxx <= startMAX)
			cutx = cutx2 = false;
		if (maxy <= startMAX)
			cuty = cuty2 = false;
		if (lessy || morey)
			cuty = cuty2 = false;
		if (lessx || morex)
			cutx = cutx2 = false;
		map = next;
		if (morey || lessy || morex || lessx || cutx || cuty || cutx2 || cuty2) {
			// System.out.println("resizing from "+maxx+" "+maxy);
			// System.out.println(""+cutx+" "+cutx2+" "+cuty+" "+cuty2+"
			// "+morey+" "+lessy+" "+morex+" "+lessx);
			// if (cutx && cutx2) cutx2=false;
			// if (cuty && cuty2) cuty2=false;
			int ox = maxx, oy = maxy;
			if (morey)
				maxy++;
			if (lessy)
				maxy++;
			if (morex)
				maxx++;
			if (lessx)
				maxx++;
			if (cutx)
				maxx--;
			if (cutx2)
				maxx--;
			if (cuty)
				maxy--;
			if (cuty2)
				maxy--;

			next = new Color[maxx][];
			for (int a = 0; a < maxx; a++) {
				next[a] = new Color[maxy];
				for (int b = 0; b < maxy; b++) {
					next[a][b] = Color.white;
				}
			}
			// copy

			// ox nx
			// 2 nx=x-1
			// 1 nx=x (stop early)
			// oy ny
			// 2 ny=oy-1
			// 1 ny=y (stop early)
			// System.out.println("change x="+(cutx?1:0)+" y="+(cuty?1:0));
			for (int a = (cutx ? 1 : 0); a < ox && a < maxx; a++) {
				for (int b = (cuty ? 1 : 0); b < oy && b < maxy; b++) {
					next[a + (lessx ? 1 : 0)][b + (lessy ? 1 : 0)] = map[a
							+ (cutx2 ? 1 : 0)][b + (cuty2 ? 1 : 0)];
					// for (int a=0;a<ox && a<maxx;a++) {
					// for (int b=0;b<oy && b<maxy;b++) {
					// next[a+(lessx?1:0)][b+(lessy?1:0)]=map[a][b];
				}
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

	public Color avColor(int x, int y) {
		Color c = Color.black;
		int ccount = 0;

		if (x - 1 >= 0 && y - 1 >= 0 && map[x - 1][y - 1] != Color.white) {
			c = addColor(c, ccount, map[x - 1][y - 1]);
			ccount++;
		}
		if (x - 1 >= 0 && y + 1 < maxy && map[x - 1][y + 1] != Color.white) {
			c = addColor(c, ccount, map[x - 1][y + 1]);
			ccount++;
		}
		if (x + 1 < maxx && y - 1 >= 0 && map[x + 1][y - 1] != Color.white) {
			c = addColor(c, ccount, map[x + 1][y - 1]);
			ccount++;
		}
		if (x + 1 < maxx && y + 1 < maxy && map[x + 1][y + 1] != Color.white) {
			c = addColor(c, ccount, map[x + 1][y + 1]);
			ccount++;
		}
		if (x - 1 >= 0 && map[x - 1][y] != Color.white) {
			c = addColor(c, ccount, map[x - 1][y]);
			ccount++;
		}
		if (x + 1 < maxx && map[x + 1][y] != Color.white) {
			c = addColor(c, ccount, map[x + 1][y]);
			ccount++;
		}
		if (y + 1 < maxy && map[x][y + 1] != Color.white) {
			c = addColor(c, ccount, map[x][y + 1]);
			ccount++;
		}
		if (y - 1 >= 0 && map[x][y - 1] != Color.white) {
			c = addColor(c, ccount, map[x][y - 1]);
			ccount++;
		}

		return c;
	}

	public int count(int x, int y) {
		int total = 0;
		// if (map[y][x]!=Color.white) total++;
		if (x - 1 >= 0 && y - 1 >= 0 && map[x - 1][y - 1] != Color.white)
			total++;
		if (x - 1 >= 0 && y + 1 < maxy && map[x - 1][y + 1] != Color.white)
			total++;
		if (x + 1 < maxx && y - 1 >= 0 && map[x + 1][y - 1] != Color.white)
			total++;
		if (x + 1 < maxx && y + 1 < maxy && map[x + 1][y + 1] != Color.white)
			total++;
		if (x - 1 >= 0 && map[x - 1][y] != Color.white)
			total++;
		if (x + 1 < maxx && map[x + 1][y] != Color.white)
			total++;
		if (y + 1 < maxy && map[x][y + 1] != Color.white)
			total++;
		if (y - 1 >= 0 && map[x][y - 1] != Color.white)
			total++;
		return total;
	}

	public void doRandomSetup() {
		for (int a = 0; a < COLONIES; a++) {
			int y = (int) (Math.random() * startMAX);
			int x = (int) (Math.random() * startMAX);
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
				if (lx >= startMAX)
					lx = startMAX - 1;
				if (ly >= startMAX)
					ly = startMAX - 1;
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
			if (getSize() == null)
				return;
			if (getSize().height < 1 || getSize().width < 1)
				return;
			i = createImage(getSize().width, getSize().height);
		} else if (i.getHeight(null) != getSize().height
				|| i.getWidth(null) != getSize().width) {
			i = createImage(getSize().width, getSize().height);
		}
		Graphics G = i.getGraphics();
		G.clearRect(0, 0, i.getWidth(this), i.getHeight(this));

		double dx = Math.max(1, 1.0 * getWidth() / maxx);
		double dy = Math.max(1, 1.0 * getHeight() / maxy);
		for (int a = 0; a < maxx; a++) {
			for (int b = 0; b < maxy; b++) {
				if (map[a][b] == Color.white)
					continue;
				G.setColor(map[a][b]);
				G.fillRect((int) (dx * a), (int) (dy * b), (int) (dx),
						(int) (dy));
				G.drawRect((int) (dx * a), (int) (dy * b), (int) (dx),
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

