package aj.smug;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import aj.awt.SimpleWindowManager;
import aj.misc.Stuff;

public class MakeGalaxy extends Canvas implements MouseListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int GALSIZ = 150, NUMPLANETS = 300, GALHEI = 50;

	static int dis = 4;

	public static double myatan(int x, int y) {
		if (x == 0 && y > 0)
			return Math.PI / 2;
		if (x == 0 && y < 0)
			return -Math.PI / 2;
		if (x == 0 && y == 0)
			return 1;
		double a = Math.atan(1.0 * y / x);
		if (x < 0)
			a = Math.PI - a;
		return a;
	}

	public static void main(String s[]) {
		int a;
		int x[] = new int[NUMPLANETS];
		int y[] = new int[NUMPLANETS];
		int z[] = new int[NUMPLANETS];
		int r[] = new int[NUMPLANETS];

		for (a = 0; a < NUMPLANETS; a++) {
			r[a] = 0;
			int rad = (int) ((Math.random() + Math.random()) * GALSIZ / 2);
			rad = GALSIZ / 2 - rad;
			if (rad < 0)
				rad = -rad;

			int hei = GALHEI * (GALSIZ / 2 - rad) / GALSIZ * 2;
			int r2 = (int) (Math.random() * hei);
			if (r2 > hei / 2)
				r2 = r2 - hei / 2;
			r2 = hei / 2 - r2;
			int quad = (int) (Math.random() * 4);
			int r3 = (int) (Math.random() * 360);
			int t = (int) (r2 * Math.sin(r3)), tt = (int) (r2 * Math.cos(r3));
			if (quad == 0) {
				x[a] = -rad;
				y[a] = tt;
				z[a] = t;
			}
			if (quad == 1) {
				x[a] = rad;
				y[a] = -tt;
				z[a] = t;
			}
			if (quad == 2) {
				x[a] = -tt;
				y[a] = rad;
				z[a] = t;
			}
			if (quad == 3) {
				x[a] = tt;
				y[a] = -rad;
				z[a] = t;
			}
			double d = Math.sqrt(x[a] * x[a] + y[a] * y[a]);// +z[a]*z[a]);
			double bang = myatan(x[a], y[a]);
			double rot = -Math.PI * Math.sqrt(GALSIZ / 2 - rad)
					/ Math.sqrt(GALSIZ / 2) + bang;
			x[a] = (int) (d * Math.cos(rot));
			y[a] = (int) (d * Math.sin(rot));
			// tip galaxy
			// double len=Math.sqrt(x[a]*x[a]-z[a]*z[a]);
			// bang=myatan(x[a],z[a]);
			// rot=Math.PI/4*(x[a]*4/GALSIZ)+bang;
			// x[a]=(int)(len*Math.sin(rot));z[a]=(int)(len*Math.cos(rot));
		}

		Frame F = new Frame();
		Choice ra = new Choice();
		Button save = new Button("Save");
		MakeGalaxy g = new MakeGalaxy(x, y, z, ra);
		save.addActionListener(g);
		g.addMouseListener(g);
		F.setLayout(new BorderLayout());
		F.add("North", ra);
		F.add("Center", g);
		F.add("South", save);
		F.setSize(500, 500);// pack();
		F.setVisible(true);
		F.addWindowListener(new SimpleWindowManager());
	}

	int x[], y[], z[], r[];

	Choice ra;

	public MakeGalaxy(int x[], int y[], int z[], Choice ra) {
		this.x = x;
		this.y = y;
		this.z = z;
		r = new int[z.length];
		this.ra = ra;
		int a;
		for (a = 0; a < races.length; a++)
			ra.add(races[a]);
	}

	public Color clookup(int a) {
		if (a == 0)
			return Color.black;
		if (a == 1)
			return Color.blue;
		if (a == 2)
			return Color.red;
		if (a == 3)
			return Color.green;
		if (a == 4)
			return Color.pink;
		if (a == 5)
			return Color.orange;
		if (a == 6)
			return Color.magenta;
		if (a == 7)
			return Color.cyan;
		if (a == 8)
			return Color.lightGray;
		else
			return Color.yellow;
	}

	public void paint(Graphics g) {
		int a;
		g.drawRect(0, 0, GALSIZ * dis, GALSIZ * dis);
		for (a = 0; a < x.length; a++) {
			g.setColor(clookup(r[a]));
			g.translate(GALSIZ * dis / 2, GALSIZ * dis / 2);
			g.drawLine(x[a] * dis, y[a] * dis, x[a] * dis, y[a] * dis);
			g.drawRect(x[a] * dis, y[a] * dis, 3, 3);
			g.translate(GALSIZ * dis / 2, 0);
			g.drawLine(z[a] * dis, y[a] * dis, z[a] * dis, y[a] * dis);
			g.translate(-GALSIZ * dis / 2, 0);
			g.translate(0, GALSIZ * dis / 2);
			g.drawLine(x[a] * dis, z[a] * dis, x[a] * dis, z[a] * dis);
			g.translate(0, -GALSIZ * dis / 2);
			g.translate(-GALSIZ * dis / 2, -GALSIZ * dis / 2);
		}
	}

	// EVENT FUNCITONS

	public void mouseClicked(MouseEvent ME) {
		int mx = (ME.getX() / dis - GALSIZ / 2);
		int my = (ME.getY() / dis - GALSIZ / 2);

		int a;
		int count = 0;
		String na = ra.getSelectedItem();
		for (a = 0; a < races.length; a++)
			if (na.equalsIgnoreCase(races[a]))
				count = a;
		boolean one = false;
		for (a = 0; a < NUMPLANETS; a++) {
			int dx = x[a] - mx, dy = y[a] - my;
			if (Math.sqrt(dx * dx + dy * dy) < 1) {
				r[a] = count;
				one = true;
			}
		}
		if (!one)
			for (a = 0; a < NUMPLANETS; a++) {
				int dx = x[a] - mx, dy = y[a] - my;// ,dz=z[a];
				if (Math.sqrt(dx * dx + dy * dy) < 7)
					r[a] = count;
			}
		repaint();
	}

	public void mouseEntered(MouseEvent ME) {
	}

	public void mouseExited(MouseEvent ME) {
	}

	public void mouseReleased(MouseEvent ME) {
	}

	public void mousePressed(MouseEvent ME) {
	}

	String races[] = { "Unknown", "Terran", "Brogians", "Gecko", "Mechbot",
			"Rhesians", "Zephyre", "Piil", "Free", "Anarchy" };

	int ind[] = { 1, 2, 1, 4, 3, 5, 4, 4, 4, 1 }, min[] = { 1, 3, 4, 2, 5, 1,
			3, 4, 4, 1 }, agr[] = { 1, 3, 5, 4, 1, 3, 1, 4, 4, 1 }, tec[] = {
			1, 5, 1, 3, 4, 3, 3, 2, 4, 1 };

	public void actionPerformed(ActionEvent ae) {
		printPlanets();
	}

	public void printPlanets() {
		int a;
		System.out.println("galaxy [");
		for (a = 0; a < x.length; a++) {
			System.out.println("planet [");
			System.out.println("id " + a);
			System.out.println("x " + x[a]);
			System.out.println("y " + y[a]);
			System.out.println("z " + z[a]);
			System.out.println("government " + races[r[a]]);
			System.out.println("law " + Stuff.trunc(Math.random() * 9, 2));
			System.out.println("minerals "
					+ Stuff.trunc(Math.random() * 10 * min[r[a]] / 5, 2));
			System.out.println("technology "
					+ Stuff.trunc(Math.random() * 10 * tec[r[a]] / 5, 2));
			System.out.println("agraculture "
					+ Stuff.trunc(Math.random() * 10 * agr[r[a]] / 5, 2));
			System.out.println("industry "
					+ Stuff.trunc(Math.random() * 10 * ind[r[a]] / 5, 2));
			System.out.println("shops []");
			System.out.println("]");
		}
		System.out.println("]");
	}
}
