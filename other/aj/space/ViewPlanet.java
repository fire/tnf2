package aj.space;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import aj.awt.DPoint;
import aj.misc.GmlPair;

/**
 * Description of the Class
 * 
 * @author judda
 * @created December 13, 2000
 */

public class ViewPlanet {
	ViewPlanet orbitsPlanet = null;

	String name;

	double mass = 1000;

	double radious;

	double orbitRadious;

	double period;

	Color color = Color.blue;

	public ViewPlanet(Vector v, GmlPair n) {
		GmlPair g = n.getOneByName("name");
		if (g != null) {
			name = g.getString();
		} else {
			System.out.println("ERROR in file name");
			name = "BAD NAME";
		}
		g = n.getOneByName("color");
		if (g != null) {
			color = Color.blue;
			if (g.getString() == null) {
				GmlPair gr = g.getOneByName("red");
				GmlPair gg = g.getOneByName("green");
				GmlPair gb = g.getOneByName("blue");
				color = new Color((int) (gr.getDouble()),
						(int) (gg.getDouble()), (int) (gb.getDouble()));
			} else {
				if (g.getString().equalsIgnoreCase("YELLOW"))
					color = Color.yellow;
				if (g.getString().equalsIgnoreCase("RED"))
					color = Color.red;
				if (g.getString().equalsIgnoreCase("BLUE"))
					color = Color.blue;
				if (g.getString().equalsIgnoreCase("GREEN"))
					color = Color.green;
				if (g.getString().equalsIgnoreCase("PINK"))
					color = Color.pink;
				// if (g.getString().equalsIgnoreCase("YELLOW")) color=yellow;
			}
		}

		g = n.getOneByName("mass");
		if (g != null) {
			mass = g.getDouble();
		} else {

			System.out.println("ERROR in file mass");
			mass = 1000;
		}
		g = n.getOneByName("surfaceRadious");
		if (g != null) {

			radious = g.getDouble();
		} else {

			System.out.println("ERROR in file radious");
			radious = 1000;
		}
		g = n.getOneByName("orbitRadious");
		if (g != null) {
			orbitRadious = g.getDouble();
		} else {
			System.out.println("ERROR in file orbitRadious");
			orbitRadious = 1000;
		}

		String oname = "";
		g = n.getOneByName("orbits");
		if (g != null) {
			oname = g.getString();
		}
		for (int a = 0; a < v.size(); a++) {

			ViewPlanet vp = (ViewPlanet) v.elementAt(a);
			if (vp.name.equals(oname)) {
				orbitsPlanet = vp;
			}
		}
		if (!oname.equalsIgnoreCase("na") && orbitsPlanet == null) {
			System.out.println("ERROR in file orbits");

		}

		period = 1;
		if (orbitsPlanet != null) {
			period = 2 * Math.PI * orbitRadious
					/ Math.pow(6.67e-11 * orbitsPlanet.mass / orbitRadious, .5);
		}
		double sa = 4 * Math.PI * Math.pow(radious, 2);
		// int ts=(int)(sa/3.11e10*4);// based on earth 256x64 map (sector
		// size=64) total square = 16384,
		int ts = (int) (sa / 3.11e10 * 4);// based on earth 512x128 map
											// (sector size=128) total square =
											// 16384x4,
		int ss = (int) Math.pow(ts / 4, .5);
		if (ss > 0) {
			System.out.println(name + " Surface area= " + sa);
			System.out.println(name + "         total squares= " + ts);
			System.out.println(name + "         sector size= " + ss);
		}
	}

	public boolean isMoon() {
		return orbitsPlanet != null;
	}

	public DPoint getMax() {
		if (!isMoon()) {
			return new DPoint(0, 0);
		} else {
			DPoint c = orbitsPlanet.getMax();
			return new DPoint(c.getX() + orbitRadious * Math.cos(0), c.getY()
					+ orbitRadious * Math.sin(0));
		}
	}

	public DPoint getMin() {
		if (!isMoon()) {
			return new DPoint(0, 0);
		} else {
			DPoint c = orbitsPlanet.getMin();
			return new DPoint(c.getX() + orbitRadious * Math.cos(180), c.getY()
					+ orbitRadious * Math.sin(180));
		}
	}

	public DPoint getCenter(long time) {
		if (!isMoon()) {
			return new DPoint(0, 0);
		} else {
			DPoint c = orbitsPlanet.getCenter(time);
			double localtime = ((time + name.hashCode() * 5) % (int) period)
					/ period * 2 * Math.PI;
			return new DPoint(c.getX() + orbitRadious * Math.cos(localtime), c
					.getY()
					+ orbitRadious * Math.sin(localtime));
		}
	}

	public void drawOrbit(Graphics g, double dx, double dy, double scale,
			double or, long time) {
		/*
		 * DPoint d = getCenter(time); int x = (int) ((d.getX() - dx) * scale);
		 * int y = (int) ((d.getY() - dy) * scale); int r = Math.max(1, (int)
		 * (or * scale)); g.setColor(Color.lightGray); if (r <= Gravity.MAXOVAL &&
		 * r > 5) { g.drawOval(x - r, y - r, r * 2, r * 2); }
		 * g.setColor(Color.black);
		 */
	}

	public void draw(Graphics g, double dx, double dy, double scale, long time) {
		DPoint d = getCenter(time);
		int x = (int) ((d.getX() - dx) * scale);
		int y = (int) ((d.getY() - dy) * scale);
		int r = Math.max(1, (int) (radious * scale));
		if (orbitsPlanet != null) {
			orbitsPlanet.drawOrbit(g, dx, dy, scale, orbitRadious, time);
		}
		if (x < Gravity.MAXVIEW && y < Gravity.MAXVIEW && x > -Gravity.MAXVIEW
				&& y > -Gravity.MAXVIEW) {
			g.setColor(color);
			g.fillOval(x - r, y - r, r * 2, r * 2);
			g.setColor(Color.gray);
			if (orbitRadious * scale > 10 || orbitRadious == -1) {
				g.drawString(name, x - r, y - r);
			}
			g.setColor(Color.black);
		}
	}
}
