package aj.testing;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.FilteredImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import aj.awt.SimpleWindowManager;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class FadeCanvas2 extends Canvas implements ActionListener {
	Image Show, One, Two;
	int mx, my;
	int pixel_data[];
	Vector FadeList;
	MediaTracker MT;

	FadeFilter2 c2;

	int count = 0;
	static int STEPS = 50, DELAY = 20;

	static Button blend = new Button("Blend it");


	/**
	 *  Constructor for the FadeCanvas2 object 
	 *
	 *@param  Fname1  Description of Parameter 
	 *@param  Fname2  Description of Parameter 
	 */
	public FadeCanvas2(String Fname1, String Fname2) {
		try {
			MT = new MediaTracker(this);
			setSize(new Dimension(300, 300));
			One = loadImage(Fname1);
			if (One == null) {
				System.out.println("bad file read " + Fname1);
				System.exit(0);
			}
			MT.addImage(One, 0);
			MT.waitForID(0);
			Show = One;
			mx = Show.getWidth(this);
			my = Show.getHeight(this);
			Two = loadImage(Fname2);
			if (Two == null) {
				System.out.println("bad file read " + Fname2);
				System.exit(0);
			}
			MT.addImage(Two, 0);
			MT.waitForID(0);
		}
		catch (InterruptedException IE) {
			System.out.println("Something wrong.  Interrupted?? HELP!");
			System.exit(0);
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void setup() {
		int tx = One.getWidth(this);
		int ty = One.getHeight(this);
		int ttx = Two.getWidth(this);
		int tty = Two.getHeight(this);
		if (tx != ttx || ty != tty) {
			Image tempi = createImage(tx, ty);
			if (tempi == null) {
				System.out.println("bad image cannot create yet!");
				System.exit(0);
			}
			Graphics g = tempi.getGraphics();
			g.drawImage(Two, 0, 0, tx, ty, this);
			Two = tempi;
		}

		pixel_data = new int[mx * my];
		int a;
		FadeList = new Vector();
		PixelGrabber p2;
		p2 = new PixelGrabber(Two, 0, 0, mx, my, pixel_data, 0, mx);
		try {
			p2.grabPixels();
		}
		catch (Exception E) {
			System.out.println("interupted!!me");
		}

		for (a = 1; a < STEPS; a++) {
			System.out.print(a * 100 / STEPS + " ");
			Image t = Fade(One, Two, a * 100 / STEPS);
			MT.addImage(t, a);
			FadeList.addElement(t);
		}
		System.out.println("Faded All!");
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Image loadImage(String s) {
		byte file_data[];
		InputStream IN;
		try {
			IN = new FileInputStream(s);
			file_data = new byte[IN.available()];
			IN.read(file_data);
			IN.close();
			return getToolkit().createImage(file_data);
		}
		catch (IOException IOE) {
			System.out.println("Error in file Input" + s);
		}
		return null;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  One  Description of Parameter 
	 *@param  Two  Description of Parameter 
	 *@param  f    Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public Image Fade(Image One, Image Two, int f) {
		FadeFilter2 c2 = new FadeFilter2(pixel_data, mx, f);
		Image t = createImage(new FilteredImageSource(One.getSource(), c2));
		return t;
	}


	/**
	 *  Description of the Method 
	 */
	public void doBlend4() {
		Thread T = Thread.currentThread();
		int a;
		int b;
		Graphics g = getGraphics();
		if (Show == One) {
			for (a = 0; a < FadeList.size(); a++) {
				try {
					Thread.sleep(DELAY);
				}
				catch (InterruptedException IE) {
				}
				Image I = (Image) FadeList.elementAt(a);
				MT.addImage(I, 101);
				try {
					MT.waitForID(101);
				}
				catch (InterruptedException IE) {
				}
				g.drawImage(I, 0, 0, mx, my, this);
			}
			Show = Two;
			g.drawImage(Show, 0, 0, mx, my, this);
		}
		else {
			for (a = FadeList.size() - 1; a > -1; a--) {
				try {
					Thread.sleep(DELAY);
				}
				catch (InterruptedException IE) {
				}
				Image I = (Image) FadeList.elementAt(a);
				MT.addImage(I, 101);
				try {
					MT.waitForID(101);
				}
				catch (InterruptedException IE) {
				}
				g.drawImage(I, 0, 0, mx, my, this);
			}
			Show = One;
			g.drawImage(Show, 0, 0, mx, my, this);
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void doBlend1() {
		int BLOCK = 3;
		int a;
		int b;
		Vector list = new Vector(mx * my / (BLOCK * BLOCK));
		Graphics g = getGraphics();
		Image I;
		if (Show == One) {
			I = Show = Two;
		}
		else {
			I = Show = One;
		}

		for (a = 0; a < mx; a += BLOCK) {
			for (b = 0; b < my; b += BLOCK) {
				list.addElement(new Point(a, b));
			}
		}
		while (list.size() > 0) {
			a = (int) (Math.random() * list.size());
			Point p = (Point) list.elementAt(a);
			list.removeElementAt(a);
			g.drawImage(I, p.x, p.y, p.x + BLOCK, p.y + BLOCK, p.x, p.y, p.x + BLOCK, p.y + BLOCK, this);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void paint(Graphics g) {
		g.drawImage(Show, 0, 0, mx, my, this);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  E  Description of Parameter 
	 */
	public void actionPerformed(ActionEvent E) {
		if (E.getSource() == blend) {
			//          if (count%2==0)  doBlend1();
			//          if (count%2==1)  doBlend4();
			doBlend4();
			count++;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		if (s.length < 2 || s.length > 4) {
			System.out.println("Format: java FadeCanvas2 <img1> <img2> [STEPS] [DELAY]");
			System.exit(0);
		}
		if (s.length >= 3) {
			try {
				STEPS = Integer.parseInt(s[2]);
			}
			catch (NumberFormatException NFE) {
				System.out.println("bad number in args");
			}
		}
		if (s.length == 4) {
			try {
				DELAY = Integer.parseInt(s[3]);
			}
			catch (NumberFormatException NFE) {
				System.out.println("bad number in args");
			}
		}

		Frame f = new Frame("Image Morfer");
		f.addWindowListener(new SimpleWindowManager());
		f.setSize(new Dimension(400, 400));
		f.setLayout(new BorderLayout());
		f.add("North", blend);
		f.setVisible(true);

		FadeCanvas2 mi = new FadeCanvas2(s[0], s[1]);
		f.add("Center", mi);
		mi.setup();

		blend.addActionListener(mi);
	}
}


/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
class FadeFilter2 extends RGBImageFilter {


	int data[];
	int fade;
	int mx;


	/**
	 *  Constructor for the FadeFilter2 object 
	 *
	 *@param  d  Description of Parameter 
	 *@param  x  Description of Parameter 
	 *@param  f  Description of Parameter 
	 */
	public FadeFilter2(int d[], int x, int f) {
		data = d;
		fade = f;
		mx = x;
		canFilterIndexColorModel = false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  x    Description of Parameter 
	 *@param  y    Description of Parameter 
	 *@param  rgb  Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public int filterRGB(int x, int y, int rgb) {
		if (y * mx + x >= data.length) {
			return rgb;
		}
		int rgb2 = data[y * mx + x];
		int r1;
		int r2;
		int g1;
		int g2;
		int b1;
		int b2;
		int r;
		int g;
		int b;
		r1 = rgb & 0xff0000;
		g1 = rgb & 0xff00;
		b1 = rgb & 0xff;
		r2 = rgb2 & 0xff0000;
		g2 = rgb2 & 0xff00;
		b2 = rgb2 & 0xff;
		r = r2 - r1;
		g = g2 - g1;
		b = b2 - b1;
		r = r1 + (r * fade / 100) & 0x00ff0000;
		g = g1 + (g * fade / 100) & 0x0000ff00;
		b = b1 + (b * fade / 100) & 0x000000ff;
		return (0xff000000 | r | g | b);
	}
}
