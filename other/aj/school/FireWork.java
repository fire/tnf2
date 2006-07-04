/*
 * Created on Jan 11, 2006
 *
 */
package aj.school;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

/**
 * @author judda
 * 
 */
public class FireWork {
	double x, y, rad;

	double dx, dy;

	double startdy;

	long lastmovetime;

	Color c;

	Color clist[] = { Color.red, Color.blue, Color.green, Color.cyan,
			Color.MAGENTA, Color.pink, Color.orange, Color.white, Color.gray };

	int maxy, maxx;

	double dxmax, dymax;

	int maxRad;

	int decay = -1;

	static boolean recreate = true;

	int MAXPARTS = 30;

	int MINPARTS = 10;

	private boolean hasExploded = false;

	boolean explodedPart = false;

	public static void setRecreate(boolean b) {
		recreate = b;
	}

	public FireWork() {
		lastmovetime = System.currentTimeMillis();
	}

	public void setStart(int x, int y, double dx, double dy, int rad) {
		this.maxy = this.maxx = 0;
		this.dxmax = this.maxy = 0;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.rad = rad;
		c = clist[(int) (Math.random() * clist.length)];
	}

	public void setRandomStart(int maxx, int maxy, double dxmax, double dymax,
			int maxRad) {
		hasExploded = false;
		decay = -1;
		this.maxy = maxy;
		this.maxx = maxx;
		this.dxmax = dxmax;
		this.dymax = dymax;
		this.maxRad = maxRad;
		x = Math.random() * maxx;
		y = maxy;
		dx = Math.random() * dxmax - dxmax / 2;
		dy = -(Math.random() * dymax + dymax * 2 / 3);
		startdy = dy;
		c = clist[(int) (Math.random() * clist.length)];
		rad = (int) (Math.random() * maxRad);
		if (x < maxx / 2 && dx < 0)
			dx = -dx;
		if (x > maxx / 2 && dx > 0)
			dx = -dx;
	}

	public void draw(Graphics g) {
		if (hasExploded())
			return;
		if (decay > 2000)
			return;
		double d = (2000.0 - decay) / 2000;
		g.setColor(new Color((int) (c.getRed() * d), (int) (c.getBlue() * d),
				(int) (c.getGreen() * d)));
		double rad = this.rad;
		if (decay == -1)
			rad = Math.min(rad, 2);
		else
			rad = rad * d + 1;
		g.fillOval((int) (x - rad / 2), (int) (y - rad / 2), (int) rad,
				(int) rad);
	}

	private void startDecay() {
		decay = 1;
	}

	public void move() {
		int dt = (int) (System.currentTimeMillis() - lastmovetime);
		lastmovetime = System.currentTimeMillis();
		dy += TypingFires.gravity * dt;
		x = x + dx * dt;
		y = y + dy * dt;
		if (dy > 0 && decay == -1)
			startDecay();
		if (decay != -1)
			decay += dt;
		if (!isExplodedPart() && outOfBounds()) {
			if (recreate)
				setRandomStart(maxx, maxy, dxmax, dymax, maxRad);
			else
				this.explodedPart = true;
		}
	}

	public boolean hasExploded() {
		return hasExploded;
	}

	public Vector explode() {
		boolean useRingSpeed = Math.random() < .5;
		boolean multiRing = useRingSpeed && Math.random() < .5;
		boolean twistRing = Math.random() < .5;
		boolean twistRing2 = multiRing && Math.random() < .5;

		// int type=(int)(Math.random()*2);
		// int style=(int)(Math.random()*2);
		hasExploded = true;
		int numParts = (int) (Math.random() * (MAXPARTS - MINPARTS) + MINPARTS);
		if (!useRingSpeed)
			numParts *= 2;
		Vector res = new Vector();
		Color newColor = clist[(int) (Math.random() * clist.length)];
		double streach = Math.random() * Math.PI * 2;
		double newExSpeed = Math.random() * (1.0 * startdy / 4);
		int newRad = (int) (rad * Math.random() * .5) + 1;
		for (int a = 0; a < numParts; a++) {
			FireWork fw = new FireWork();
			double ang = Math.random() * Math.PI * 2;
			fw.setCenter(this);
			if (!useRingSpeed) {
				newExSpeed = Math.random() * (1.0 * startdy / 4);
			}
			fw.dx = this.dx + Math.cos(ang) * newExSpeed;
			fw.dy = this.dy + Math.sin(ang) * newExSpeed;
			if (twistRing) {
				fw.dx += Math.cos(streach) * fw.dx;
				fw.dx += Math.sin(streach) * fw.dy;
			}
			fw.c = newColor;
			fw.rad = newRad;
			fw.decay = 1;
			res.addElement(fw);
		}
		newColor = clist[(int) (Math.random() * clist.length)];
		newExSpeed = Math.random() * (1.0 * startdy / 4)
				* (Math.random() * 1 + 2) / 4;
		if (Math.random() * 2 > 1)
			streach = Math.random() * Math.PI * 2;
		newRad = 1;
		for (int a = 0; multiRing && a < numParts / 2; a++) {
			FireWork fw = new FireWork();
			double ang = Math.random() * Math.PI * 2;
			fw.setCenter(this);
			if (!useRingSpeed) {
				newExSpeed = Math.random() * (1.0 * startdy / 4);
			}
			fw.dx = this.dx + Math.cos(ang) * newExSpeed;
			fw.dy = this.dy + Math.sin(ang) * newExSpeed;
			if (twistRing2) {
				fw.dx += Math.cos(streach) * fw.dx;
				fw.dx += Math.sin(streach) * fw.dy;
			}
			fw.c = newColor;
			fw.rad = newRad;
			fw.decay = 1;
			res.addElement(fw);
		}
		return res;
	}

	public boolean isExplodedPart() {
		return explodedPart;
	}

	private boolean outOfBounds() {
		return x < 0 || x > maxx || y > maxy;
	}

	public boolean expired() {
		return isExplodedPart() && outOfBounds();
	}

	private void setCenter(FireWork fw) {
		this.maxy = fw.maxy;
		this.maxx = fw.maxx;
		this.dxmax = fw.dxmax;
		this.dymax = fw.dymax;
		this.maxRad = fw.maxRad;
		this.explodedPart = true;
		this.x = fw.x;
		this.y = fw.y;
		this.rad = 1;
		// double ang=Math.random()*(Math.PI*2.0);
		// System.out.println("ang="+ang+" sin="+Math.sin(ang));
		// System.out.println("dx="+dx+" Dy="+dy);
	}
}
