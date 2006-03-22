package aj.awt.mand;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import aj.awt.BoxLayout;
import aj.awt.SimpleWindowManager;
import aj.misc.GmlPair;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
/**
 *
 *@author     judda 
 *@created    April 11, 2000 
 */
public class Mand extends Canvas implements MouseListener, MouseMotionListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage I;
	boolean mustRedraw;
	int defaultSize = 200, defaultFocus = 1, defaultDepth = 500, defaultColors = 256;
	int size = defaultSize, height = size, width = size, iterationLimit = defaultDepth, currentFocusLimit = defaultFocus;
	double currentRealMin = - 2.0, currentRealMax = 0.50, currentImagMin = - 1.25, currentImagMax = 1.25;
	int Sx, Sy, Ex, Ey;
	int maxColors = defaultColors;
	ScrollPane SP;
	Thread updateThread;
	Frame settingFrame = new Frame ("Change settings");
	Frame positionFrame = new Frame ("Change position");
	TextField tfFocus = new TextField (4), tfDepth = new TextField (4), tfSize = new TextField (5), tfColors = new TextField (4);
	TextField tfRMin = new TextField (12), tfRMax = new TextField (12), tfIMin = new TextField (12), tfIMax = new TextField (12);
	TextField TXstatus = new TextField (7);
	
	public Mand (int size, int foc, int dep, int col, ScrollPane sp) {
		SP = sp;
		this.size = size;
		height = size;
		width = size;
		currentFocusLimit = foc;
		iterationLimit = dep;
		maxColors = col;
		setupGui();
	}
	
	public Mand (ScrollPane sp) {
		SP = sp;
		setupGui();
	}
	
	public void setupGui() {
		mustRedraw = true;
		addMouseListener (this);
		addMouseMotionListener (this);
		settingFrame.addWindowListener (new SimpleWindowManager (false));
		positionFrame.addWindowListener (new SimpleWindowManager (false));
		//layout settingFrame
		settingFrame.setLayout (new BorderLayout());
		Panel p = new Panel (new BoxLayout (0, 2));
		p.add (new Label ("Size", Label.RIGHT));
		p.add (tfSize);
		p.add (new Label ("Depth", Label.RIGHT));
		p.add (tfDepth);
		p.add (new Label ("Focus", Label.RIGHT));
		p.add (tfFocus);
		p.add (new Label ("Colors", Label.RIGHT));
		p.add (tfColors);
		settingFrame.add ("Center", p);
		settingFrame.pack();
		tfFocus.addActionListener (this);
		tfDepth.addActionListener (this);
		tfSize.addActionListener (this);
		tfColors.addActionListener (this);
		//layout positionFrame
		positionFrame.setLayout (new BorderLayout());
		p = new Panel (new BoxLayout (0, 2));
		p.add (new Label ("MinX", Label.RIGHT));
		p.add (tfRMin);
		p.add (new Label ("MaxX", Label.RIGHT));
		p.add (tfRMax);
		p.add (new Label ("MinY", Label.RIGHT));
		p.add (tfIMin);
		p.add (new Label ("MaxY", Label.RIGHT));
		p.add (tfIMax);
		positionFrame.add ("Center", p);
		positionFrame.pack();
		tfRMin.addActionListener (this);
		tfRMax.addActionListener (this);
		tfIMin.addActionListener (this);
		tfIMax.addActionListener (this);
	}
	
	public String getGml() {
		return"mand [ " + " minx \"" + currentRealMin + "\"" + " maxx \"" + currentRealMax + "\"" + " miny \"" + currentImagMin + "\"" + " maxy \"" + currentImagMax + "\"" + " depth " + iterationLimit + " colors " + maxColors + " centerx \"" + (currentRealMin + currentRealMax) / 2 + "\"" + " centery \"" + (currentImagMin + currentImagMax) / 2 + "\"" + " widthx \"" + Math.abs (currentRealMin - currentRealMax) + "\"" + " widthy \"" + Math.abs (currentImagMin - currentImagMax) + "\"" + " ]";
	}
	
	public void loadFile (String s) {
		try {
			GmlPair g = GmlPair.parse (new File (s));
			GmlPair minx = g.getOneByName ("minx");
			GmlPair maxx = g.getOneByName ("maxx");
			GmlPair miny = g.getOneByName ("miny");
			GmlPair maxy = g.getOneByName ("maxy");
			GmlPair fcolors = g.getOneByName ("colors");
			GmlPair fdepth = g.getOneByName ("depth");
			if (minx != null)currentRealMin = minx.getDouble();
			if (maxx != null)currentRealMax = maxx.getDouble();
			if (miny != null)currentImagMin = miny.getDouble();
			if (maxy != null)currentImagMax = maxy.getDouble();
			if (fcolors != null)maxColors = (int) (fcolors.getDouble());
			if (fdepth != null)iterationLimit = (int) (fdepth.getDouble());
		}
		catch (IOException IOE) {
System.out.println("MyError: Gml parse error: "+IOE);
		}
		mustRedraw = true;
		repaint();
	}
	
	public void setDefaultPosition() {
		setAll (size, currentFocusLimit, iterationLimit, maxColors, - 2.0, 0.50, - 1.25, 1.25);
	}
	
	public void setAll (int i, int j, int k, int col, double minx, double maxx, double miny, double maxy) {
		this.size = i;
		width = i;
		height = i;
		currentFocusLimit = j;
		iterationLimit = k;
		maxColors = col;
		currentRealMin = minx;
		currentRealMax = maxx;
		currentImagMin = miny;
		currentImagMax = maxy;
		mustRedraw = true;
		repaint();
	}
	
	public int getDepth() {
		return iterationLimit;
	}
	
	public int getFocus() {
		return currentFocusLimit;
	}
	
	public int getImageSize() {
		return width;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension (width, height);
	}
	
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public void encode (OutputStream out)throws IOException {
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder (out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam (I);
		param.setQuality ((float).85, true);
		encoder.encode (I, param);
	}
	
	public void mouseClicked (MouseEvent mouseEvent) {
	}
	
	public void mouseDragged (MouseEvent mouseEvent) {
		int i1 = mouseEvent.getX();
		int j1 = mouseEvent.getY();
		Graphics g = getGraphics();
		g.setXORMode (new Color (255, 255, 255));
		int k = Math.min (Sx, Ex);
		int i2 = Math.min (Sy, Ey);
		int j2 = Math.max (Math.abs (Sx - Ex), Math.abs (Sy - Ey));
		g.drawRect (k, i2, j2, j2);
		Ex = i1;
		Ey = j1;
		k = Math.min (Sx, Ex);
		i2 = Math.min (Sy, Ey);
		j2 = Math.max (Math.abs (Sx - Ex), Math.abs (Sy - Ey));
		g.drawRect (k, i2, j2, j2);
		g.setPaintMode();
	}
	
	public void mouseEntered (MouseEvent mouseEvent) {
	}
	
	public void mouseExited (MouseEvent mouseEvent) {
	}
	
	public void mouseMoved (MouseEvent mouseEvent) {
	}
	
	public void mousePressed (MouseEvent mouseEvent) {
		Sx = Ex = mouseEvent.getX();
		Sy = Ey = mouseEvent.getY();
		Graphics g = getGraphics();
		g.setXORMode (new Color (255, 255, 255));
		g.drawRect (Sx, Sy, 0, 0);
		g.setPaintMode();
	}
	
	public void mouseReleased (MouseEvent mouseEvent) {
		int i1 = mouseEvent.getX();
		int j1 = mouseEvent.getY();
		Graphics g = getGraphics();
		if (Math.abs (Sx - Ex) < 2 && Math.abs (Sy - Ey) < 2) {
			return;
		}
		g.setXORMode (new Color (255, 255, 255));
		int k1 = Math.min (Sx, Ex);
		int i2 = Math.min (Sy, Ey);
		int j2 = Math.max (Math.abs (Sx - Ex), Math.abs (Sy - Ey));
		g.drawRect (k1, i2, j2, j2);
		Ex = i1;
		Ey = j1;
		if (Sx > Ex) {
			int k2 = Sx;
			Sx = Ex;
			Ex = k2;
		}
		if (Sy > Ey) {
			int i3 = Sy;
			Sy = Ey;
			Ey = i3;
		}
		if (j2 > Math.abs (Sx - Ex)) {
			Ex = Ey - Sy + Sx;
		}
		else {
			Ey = Ex - Sx + Sy;
		}
		double d1 = (currentRealMax - currentRealMin) / width;
		double d2 = (currentImagMax - currentImagMin) / height;
		currentRealMax = currentRealMax - (double) (width - Math.max (Sx, Ex)) * d1;
		currentRealMin = currentRealMin + (double)Math.min (Sx, Ex) * d1;
		currentImagMax = currentImagMax - (double) (height - Math.max (Sy, Ey)) * d2;
		currentImagMin = currentImagMin + (double)Math.min (Sy, Ey) * d2;
		mustRedraw = true;
		repaint();
		Sx = Ex = i1;
		Sy = Ey = j1;
	}
	
	public void update (Graphics g) {
		settingFrameUpdate();
		positionFrameUpdate();
		paint (g);
	}
	
	public void paint (Graphics g) {
		if (mustRedraw) {
			TXstatus.setText ("Drawing");
			I = new BufferedImage (width, height, BufferedImage.TYPE_3BYTE_BGR);
			SP.doLayout();
			recalc();
			mustRedraw = false;
			TXstatus.setText ("Ready");
		}
		else g.drawImage (I, 0, 0, width, height, Color.red, this);
	}
	
	public void recalc() {
		final Mand top1 = this;
		updateThread = new Thread() {
			Mand top = top1;
			int iterationLimit = top.iterationLimit;
			int width = top.width, height = top.height;
			double currentRealMax = top.currentRealMax;
			double currentRealMin = top.currentRealMin;
			double currentImagMax = top.currentImagMax;
			public Color doCalc (double Nx, double Ny) {
				double Wx = Nx;
				double Wy = Ny;
				double Ty;
				double Tx;
				int cnt = 0;
				do {
					Tx = Wx * Wx;
					Ty = Wy * Wy;
					Wy = 2 * Wx * Wy + Ny;
					//y=2*x*y+y.init
					Wx = Tx - Ty + Nx;
					//x=x*x-y*y+x.init
					cnt++;
				}
				while (cnt < iterationLimit && Tx + Ty < 4.0);
				//while (x*x+y*y<4.0)
				return ColorVal (cnt);
			}
			
			public void run() {
				long begintime=System.currentTimeMillis();
				Graphics g = top.I.getGraphics();
				Graphics G = top.getGraphics();
				double realInc = (currentRealMax - currentRealMin) * 1.0 / width;
				double imagInc = (currentImagMax - currentImagMin) * 1.0 / height;
				int x;
				int y;
				double Nx;
				double Ny;
				Color c = doCalc (currentRealMin, currentImagMin);
				g.setColor (c);
				G.setColor (c);
				g.fillRect (1, 1, width, height);
				G.fillRect (1, 1, width, height);
				int localFocus;
				G.clipRect (0, 0, width, height);
				int localFocusLimit = top.currentFocusLimit / 2;
				int donefocus = Math.max (1, localFocusLimit * 2);
				double doneval = 1.0 / (donefocus * donefocus);
				for (localFocus = 128; localFocus > localFocusLimit; localFocus /= 2) {
					int localFocus2x = localFocus * 2;
					double xstep = realInc * localFocus;
					double ystep = imagInc * localFocus;
					double lastval = 1.0 / (localFocus2x * localFocus2x);
					double currval = 1.0 / (localFocus * localFocus) - lastval;
					for (x = 0, Nx = currentRealMin; x < width; x += localFocus, Nx += xstep) {
						double currwork = (1.0 * x / width);
						int amtdon = (int) ((currwork * currval + lastval) / doneval * 100);
						String newtext = amtdon + "% Drawing";
						if ( !TXstatus.getText().equals (newtext)) {
							TXstatus.setText (newtext);
						}
						Thread.yield();
						if (top.updateThread != this) {
							return;
						}
						for (y = 0, Ny = currentImagMin; y < height; y += localFocus, Ny += ystep) {
							if ((y) % localFocus2x == 0 && (x) % localFocus2x == 0) {
								continue;
							}
							c = doCalc (Nx, Ny);
							g.setColor (c);
							G.setColor (c);
							if (localFocus != 1) {
								g.fillRect (x, y, localFocus, localFocus);
								G.fillRect (x, y, localFocus, localFocus);
							}
							else {
								g.drawLine (x, y, x, y);
								G.drawLine (x, y, x, y);
							}
						}
					}
					long endtime=System.currentTimeMillis();
					long elapstime=(endtime-begintime);
					TXstatus.setText ("100% Done "+aj.misc.Stuff.trunc(elapstime/1000.0,2)+" secs");
				}
			}
		}
;
		updateThread.start();
	}
	Color ColorVal (int k) {
		if (k == iterationLimit) {
			return Color.black;
		}
		k = k % maxColors;
		Color set[] = {
			//Color.white,new Color(216,212,8),Color.white,//Gold
			//Color.lightGray,new Color(189,192,199),Color.white//Silver
			//,new Color (189,192,199),new Color (189,192,199),Color.white
			Color.red, Color.yellow, Color.green, Color.cyan, Color.blue, Color.magenta
		};
		int color_per_set = maxColors / set.length;
		int sind = (k / color_per_set) % set.length;
		Color s1 = set[sind];
		Color s2 = set[ (sind + 1 < set.length?sind + 1:0)];
		double blend = (k % color_per_set) / 1.0 / color_per_set;
		int nr = (int) (s1.getRed() * (1 - blend) + s2.getRed() * blend);
		int ng = (int) (s1.getGreen() * (1 - blend) + s2.getGreen() * blend);
		int nb = (int) (s1.getBlue() * (1 - blend) + s2.getBlue() * blend);
		return new Color (nr, ng, nb);
	}
	
	public void actionPerformed (ActionEvent actionEvent) {
		if (actionEvent.getSource() == tfSize) {
			try {
				size = width = height = Integer.parseInt (tfSize.getText());
				mustRedraw = true;
				repaint();
			}
			catch (NumberFormatException NFE) {
			}
		}
		if (actionEvent.getSource() == tfDepth) {
			try {
				iterationLimit = Integer.parseInt (tfDepth.getText());
				mustRedraw = true;
				repaint();
			}
			catch (NumberFormatException NFE) {
			}
		}
		if (actionEvent.getSource() == tfFocus) {
			try {
				currentFocusLimit = Integer.parseInt (tfFocus.getText());
				mustRedraw = true;
				repaint();
			}
			catch (NumberFormatException NFE) {
			}
		}
		if (actionEvent.getSource() == tfColors) {
			try {
				maxColors = Integer.parseInt (tfColors.getText());
				mustRedraw = true;
				repaint();
			}
			catch (NumberFormatException NFE) {
			}
		}
		if (actionEvent.getSource() == tfRMin || actionEvent.getSource() == tfRMax || actionEvent.getSource() == tfIMin || actionEvent.getSource() == tfIMax) {
			try {
				currentRealMin = Double.parseDouble (tfRMin.getText());
				currentRealMax = Double.parseDouble (tfRMax.getText());
				currentImagMin = Double.parseDouble (tfIMin.getText());
				currentImagMax = Double.parseDouble (tfIMax.getText());
				mustRedraw = true;
				repaint();
			}
			catch (NumberFormatException NFE) {
			}
		}
	}
	
	public void settingFrameUpdate() {
		tfSize.setText (size + "");
		tfDepth.setText (iterationLimit + "");
		tfColors.setText (maxColors + "");
		tfFocus.setText (currentFocusLimit + "");
	}
	
	public void positionFrameUpdate() {
		tfRMin.setText (currentRealMin + "");
		tfRMax.setText (currentRealMax + "");
		tfIMin.setText (currentImagMin + "");
		tfIMax.setText (currentImagMax + "");
	}
}
//mandelbrot function (Z is complex number with X and Y*i)
//Z(n)=Z(n-1)*Z(n-1)+Z(c)
//
//Z(c) = is inital constand value
//complet number squared Zx+Zy = Zx=Zx*Zx+Zy*Zy Zy=2*Zx*Zy
