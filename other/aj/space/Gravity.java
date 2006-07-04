package aj.space;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import aj.awt.DPoint;
import aj.awt.SimpleWindowManager;
import aj.misc.GmlPair;
import aj.misc.Stuff;

/**
 * Description of the Class
 * 
 * @author judda
 * @created December 12, 2000
 */
public class Gravity extends Canvas implements MouseListener,
		MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Vector allBodies = new Vector();

	Image I = null;

	ViewPlanet centerPlanet = null;

	DPoint centerPoint = new DPoint(0.0, 0.0);

	double viewSize = 1.5e13;

	boolean down = false;

	Point downP;

	Point offP;

	boolean downButton3 = false, downButton1 = false;

	static int MAXOVAL = 20000;

	static int MAXCANVAS = 2000;

	static int PREFSIZE = 200;

	static int MAXVIEW = 10000;

	static double REALTIME = .001;

	static double TIMESCALE = REALTIME * 3000;

	public Gravity() {
		setBackground(Color.white);
		String file = "planet_data.gml";
		try {
			GmlPair g = null;
			File f = new File(file);
			if (f.canRead())
				g = GmlPair.parse(new File(file));
			else {
				Class aClass = getClass();
				InputStream IN = aClass.getResourceAsStream(file);
				g = GmlPair.parse(IN);
			}
			GmlPair n[] = g.getAllByName("planet");
			for (int a = 0; a < n.length; a++) {
				ViewPlanet vp = new ViewPlanet(allBodies, n[a]);
				if (vp.orbitsPlanet == null) {
					centerPlanet = vp;
				}
				allBodies.addElement(vp);
			}
			g = g.getOneByName("time");
			if (g != null)
				TIMESCALE = REALTIME * g.getDouble();
			if (centerPlanet == null) {
				centerPoint = new DPoint(0, 0);
			}
		} catch (IOException IOE) {
			System.out.println("Bad planet file " + file);
			System.exit(0);
		}
		addMouseMotionListener(this);
		addMouseListener(this);
		// Clock c = new Clock(REFRESHRATE);
		// c.addActionListener(this);
		new Thread() {
			public void run() {
				while (true) {
					long t = System.currentTimeMillis();
					repaint();
					long elaps = System.currentTimeMillis() - t;
					// System.out.println("elap="+elaps);
					long delay = 50 - elaps;
					// System.out.println("sleep"+delay);
					if (delay > 0)
						try {
							// Thread.sleep(100);
							Thread.sleep(delay);
						} catch (Exception e) {
						}
				}
			}
		}.start();
	}

	/*
	 * double dx=(dloc.getX()-dl.getX()); double dy=(dloc.getY()-dl.getY());
	 * double d=Math.pow((dx*dx)+(dy*dy),.5); int
	 * t=(int)(Math.pow(d/.0098,.5)*4)*1000; t=(int)Math.max(t,2*DAY); return t;
	 * 
	 * //earth to moon= 3 days //earth to mars = 12-20 days t=2 days= 60*60*24*2
	 * =172800 d=4.5e8 a=? d=.5at^2 4.5e8*2/t^2
	 * 
	 * 4.5e8*2/172800^2
	 * 
	 * 
	 * 
	 * d*2/a=t^2 (d*2/a)^.5=t
	 * 
	 * int t=(int)(Math.pow((d*2/9.8),.5)); int d=t/60/60/24;
	 * 
	 * d = .5a*t^2 d*2/t^2= a
	 * 
	 * 
	 * g=.12 == eath -> mo= 2 day
	 * 
	 * top speed = 300000 mps
	 * 
	 * d earth - moon = 4.5e8 m d earth - sun = 3.16e10
	 * 
	 * 
	 * v=a*t
	 * 
	 * max accell time = 300000/a s
	 * 
	 * 
	 */

	public Dimension getPreferredSize() {
		return new Dimension(PREFSIZE, PREFSIZE);
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (I == null) {
			I = createImage(getSize().width, getSize().height);
		} else if (I.getHeight(null) != getSize().height
				|| I.getWidth(null) != getSize().width) {
			I = createImage(getSize().width, getSize().height);
		}

		setBackground(Color.black);
		Graphics G = I.getGraphics();
		G.clearRect(0, 0, getSize().width, getSize().height);
		long useTime = (long) (System.currentTimeMillis() * TIMESCALE);
		if (centerPlanet != null) {
			centerPoint = centerPlanet.getCenter(useTime);
		}
		double scale = Math.min(getSize().width, getSize().height) / viewSize;
		G.drawString("view ("
				+ Stuff.trunc(Math.log(viewSize) / Math.log(10), 2) + ")", 0,
				getSize().height - 5);
		G.translate((int) (getSize().width / 2), (int) (getSize().height / 2));
		for (int a = 0; a < allBodies.size(); a++) {
			ViewPlanet v = (ViewPlanet) allBodies.elementAt(a);
			v.draw(G, centerPoint.getX(), centerPoint.getY(), scale, useTime);
		}

		g.drawImage(I, 0, 0, this);
		if (down) {
			g.setXORMode(Color.gray);
			int minx = (int) Math.min(downP.getX() - offP.getX(), downP.getX()
					+ offP.getX());
			int miny = (int) Math.min(downP.getY() - offP.getY(), downP.getY()
					+ offP.getY());
			// int maxx = (int) Math.max(downP.getX() + offP.getX(),
			// downP.getX() + offP.getX());
			// int maxy = (int) Math.max(downP.getY() + offP.getX(),
			// downP.getY() + offP.getY());
			g.drawRect(minx, miny, Math.abs((int) offP.getX() * 2), Math
					.abs((int) offP.getY() * 2));
		}
	}

	public void mouseEntered(MouseEvent mouseEvent) {
	}

	public void mouseExited(MouseEvent mouseEvent) {
	}

	public void mouseMoved(MouseEvent mouseEvent) {
	}

	public void centerAtScreenPos(Point p) {
		// System.out.println("Center to screen");
		double scale = Math.min(getSize().width, getSize().height) / viewSize;
		double mx = p.getX();
		double my = p.getY();
		double ax = (mx - getSize().width / 2 + centerPoint.getX() * scale);
		double ay = (my - getSize().height / 2 + centerPoint.getY() * scale);
		double rx = ax / scale;
		double ry = ay / scale;
		centerPoint = new DPoint(rx, ry);
		centerPlanet = null;
	}

	public void centerAtNearestPlanetToScreenPos(Point p) {
		// System.out.println("Center to planet");
		double scale = Math.min(getSize().width, getSize().height) / viewSize;
		double mx = p.getX();
		double my = p.getY();
		double ax = (mx - getSize().width / 2 + centerPoint.getX() * scale);
		double ay = (my - getSize().height / 2 + centerPoint.getY() * scale);
		double rx = ax / scale;
		double ry = ay / scale;
		centerPoint = new DPoint(rx, ry);

		DPoint cpc = null;
		double cpd = 0;
		long useTime = (long) (System.currentTimeMillis() * TIMESCALE);
		if (allBodies.size() > 0) {
			centerPlanet = (ViewPlanet) allBodies.elementAt(0);
			cpc = centerPlanet.getCenter(useTime);
			double dx = rx - cpc.getX();
			double dy = ry - cpc.getY();
			cpd = dx * dx + dy * dy;
		}
		for (int a = 0; a < allBodies.size(); a++) {
			ViewPlanet v = (ViewPlanet) allBodies.elementAt(a);
			DPoint vc = v.getCenter(useTime);
			double dx = rx - vc.getX();
			double dy = ry - vc.getY();
			double d = dx * dx + dy * dy;
			if (d < cpd) {
				centerPlanet = v;
				cpd = d;
			}
		}
	}

	public void mouseClicked(MouseEvent mouseEvent) {
		// System.out.println("CLICK");
		if ((mouseEvent.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			centerAtScreenPos(new Point(mouseEvent.getX(), mouseEvent.getY()));
			// System.out.println("Button3");
		} else if ((mouseEvent.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			centerAtNearestPlanetToScreenPos(new Point(mouseEvent.getX(),
					mouseEvent.getY()));
			// System.out.println("Button1");
		}
		repaint();
	}

	public void mousePressed(MouseEvent me) {
		if ((me.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			downButton3 = true;
			downButton1 = false;
		}
		if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			downButton1 = true;
			downButton3 = false;
		}

		down = true;
		downP = new Point(me.getX(), me.getY());
		offP = new Point(0, 0);
		Graphics G = getGraphics();
		G.setXORMode(Color.gray);
		G.drawRect((int) downP.getX(), (int) downP.getY(), 0, 0);
	}

	public void mouseDragged(MouseEvent me) {
		if (!down) {
			return;
		}
		Graphics G = getGraphics();
		G.setXORMode(Color.gray);
		int minx = (int) Math.min(downP.getX() - offP.getX(), downP.getX()
				+ offP.getX());
		int miny = (int) Math.min(downP.getY() - offP.getY(), downP.getY()
				+ offP.getY());
		// int maxx = (int) Math.max(downP.getX() + offP.getX(), downP.getX() +
		// offP.getX());
		// int maxy = (int) Math.max(downP.getY() + offP.getX(), downP.getY() +
		// offP.getY());
		G.drawRect(minx, miny, Math.abs((int) offP.getX() * 2), Math
				.abs((int) offP.getY() * 2));
		offP = new Point((int) Math.abs(me.getX() - downP.getX()), (int) Math
				.abs(me.getY() - downP.getY()));
		offP = new Point((int) Math.max(offP.getX(), offP.getY()
				* getSize().width / getSize().height), (int) Math.max(offP
				.getY(), offP.getX() / getSize().width * getSize().height));
		minx = (int) Math.min(downP.getX() - offP.getX(), downP.getX()
				+ offP.getX());
		miny = (int) Math.min(downP.getY() - offP.getY(), downP.getY()
				+ offP.getY());
		// maxx = (int) Math.max(downP.getX() + offP.getX(), downP.getX() +
		// offP.getX());
		// maxy = (int) Math.max(downP.getY() + offP.getX(), downP.getY() +
		// offP.getY());
		G.drawRect(minx, miny, Math.abs((int) offP.getX() * 2), Math
				.abs((int) offP.getY() * 2));
	}

	public void mouseReleased(MouseEvent me) {
		if (!down) {
			return;
		}
		Graphics G = getGraphics();
		G.setXORMode(Color.gray);
		int minx = (int) Math.min(downP.getX() - offP.getX(), downP.getX()
				+ offP.getX());
		int miny = (int) Math.min(downP.getY() - offP.getY(), downP.getY()
				+ offP.getY());
		// int maxx = (int) Math.max(downP.getX() + offP.getX(), downP.getX() +
		// offP.getX());
		// int maxy = (int) Math.max(downP.getY() + offP.getX(), downP.getY() +
		// offP.getY());
		G.drawRect(minx, miny, Math.abs((int) offP.getX() * 2), Math
				.abs((int) offP.getY() * 2));
		offP = new Point((int) Math.abs(me.getX() - downP.getX()), (int) Math
				.abs(me.getY() - downP.getY()));
		offP = new Point((int) Math.max(offP.getX(), offP.getY()
				* getSize().width / getSize().height), (int) Math.max(offP
				.getY(), offP.getX() / getSize().width * getSize().height));
		if (offP.getX() > 4 && offP.getY() > 4) {
			if (downButton3) {
				centerAtScreenPos(new Point((int) downP.getX(), (int) downP
						.getY()));
			} else if (downButton1) {
				centerAtNearestPlanetToScreenPos(new Point((int) downP.getX(),
						(int) downP.getY()));
			}
			viewSize = Math.abs(viewSize / getSize().width * offP.getX() * 2);
		}
		down = false;
	}

	public static void main(String s[]) {
		Frame f = new Frame();
		f.add("Center", new Gravity());
		f.setVisible(true);
		f.addWindowListener(new SimpleWindowManager());
		f.pack();
	}
}
