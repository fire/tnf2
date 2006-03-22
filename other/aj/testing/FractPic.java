/*
 * Random fractal images
 */
package aj.testing;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import aj.awt.SimpleWindowManager;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class FractPic extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int SIZE = 400;
	int FOCUSLIMIT = 1;

	double r, g, b;
	long rs, gs, bs;

	Image I = null;


	/**
	 *  Constructor for the FractPic object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public FractPic(String s[]) {
		Random x = new Random();
		rs = x.nextLong();
		gs = x.nextLong();
		bs = x.nextLong();
		r = Math.random();
		g = Math.random();
		b = Math.random();
		try {
			r = Double.parseDouble(s[0]);
			g = Double.parseDouble(s[1]);
			b = Double.parseDouble(s[2]);
			rs = (long) Double.parseDouble(s[3]);
			gs = (long) Double.parseDouble(s[4]);
			bs = (long) Double.parseDouble(s[5]);
		}
		catch (Exception NFE) {
		}
	}


	/**
	 *  Gets the MinimumSize attribute of the FractPic object 
	 *
	 *@return    The MinimumSize value 
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}


	/**
	 *  Gets the MaximumSize attribute of the FractPic object 
	 *
	 *@return    The MaximumSize value 
	 */
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}


	/**
	 *  Gets the PreferredSize attribute of the FractPic object 
	 *
	 *@return    The PreferredSize value 
	 */
	public Dimension getPreferredSize() {
		return new Dimension(SIZE, SIZE);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  G  Description of Parameter 
	 */
	public void paint(Graphics G) {
		if (I == null) {
			recalc(G);
		}
		G.drawImage(I, 0, 0, this);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  x  Description of Parameter 
	 *@param  y  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Color calcPos(int x, int y) {
		int maxx = SIZE;
		int maxy = SIZE;
		int minx = 0;
		int miny = 0;
		double cr = r;
		double cg = g;
		double cb = b;
		boolean found = false;
		while (!found) {
			int dx = maxx - minx;
			int dy = maxy - miny;
			Random rr = new Random(minx * miny * maxx * maxy + rs);
			Random rb = new Random(minx * miny * maxx * maxy + gs);
			Random rg = new Random(minx * miny * maxx * maxy + bs);
			if (x > minx + dx / 2 && y > miny + dy / 2) {
				minx += dx / 2 + 1;
				miny += dy / 2 + 1;
				skip(0, rr, rg, rb);
			}
			else if (x <= minx + dx / 2 && y > miny + dy / 2) {
				maxx -= dx / 2;
				miny += dy / 2 + 1;
				skip(1, rr, rg, rb);
			}
			else if (x > minx + dx / 2 && y <= miny + dy / 2) {
				minx += dx / 2 + 1;
				maxy -= dy / 2;
				skip(2, rr, rg, rb);
			}
			else if (x <= minx + dx / 2 && y <= miny + dy / 2) {
				maxx -= dx / 2;
				maxy -= dy / 2;
				skip(3, rr, rg, rb);
			}

			cr = Math.min(Math.max(0, cr + rr.nextGaussian() / 4), 1);
			cg = Math.min(Math.max(0, cg + rg.nextGaussian() / 4), 1);
			cb = Math.min(Math.max(0, cb + rb.nextGaussian() / 4), 1);

			if (Math.abs(maxx - minx) < 2 && Math.abs(maxy - miny) < 2) {
				found = true;
			}
		}
		return new Color((float) cr, (float) cg, (float) cb);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	private void recalc(Graphics g) {
		I = createImage(SIZE, SIZE);
		Graphics G = I.getGraphics();
		/*
		 * int a,b;
		 * for (a=0;a<SIZE;a++)
		 * for (b=0;b<SIZE;b++) {
		 * G.setColor(calcPos(a,b));
		 * G.drawLine(a,b,a,b);
		 * g.setColor(calcPos(a,b));
		 * g.drawLine(a,b,a,b);
		 * }
		 */
		int width = SIZE;
		/*
		 * int a,b;
		 * for (a=0;a<SIZE;a++)
		 * for (b=0;b<SIZE;b++) {
		 * G.setColor(calcPos(a,b));
		 * G.drawLine(a,b,a,b);
		 * g.setColor(calcPos(a,b));
		 * g.drawLine(a,b,a,b);
		 * }
		 */
		int height = SIZE;
		int x;
		int y;
		int FOCUS;
		int count = 255;
		for (FOCUS = 128; FOCUS * FOCUS > FOCUSLIMIT; FOCUS /= 2) {

			count -= 15;
			for (x = 0; x < width; x += FOCUS) {

				for (y = 0; y < height; y += FOCUS) {

					if ((y) % (FOCUS * 2) == 1 && (x) % (FOCUS * 2) == 1) {
						continue;
					}

					Color c = calcPos(x, y);
					G.setColor(c);
					g.setColor(c);
					if (FOCUS != 1) {
						G.fillRect(x, y, FOCUS, FOCUS);
						g.fillRect(x, y, FOCUS, FOCUS);
					}
					else {
						G.drawLine(x, y, x, y);
						g.drawLine(x, y, x, y);
					}
				}
			}
		}
	}

	/**
	 *  Description of the Method 
	 *
	 *@param  i   Description of Parameter 
	 *@param  r   Description of Parameter 
	 *@param  r2  Description of Parameter 
	 *@param  r3  Description of Parameter 
	 */
	private void skip(int i, Random r, Random r2, Random r3) {
		for (int a = 0; a < i; a++) {
			r.nextGaussian();
			r2.nextGaussian();
			r3.nextGaussian();
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
		f.setLayout(new BorderLayout());
		f.add("Center", new FractPic(s));
		f.pack();
		f.setVisible(true);
	}


}
/*
 * r float long
 * g float long
 * b float long
 */
