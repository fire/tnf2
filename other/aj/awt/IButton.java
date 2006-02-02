package aj.awt;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;
/**
 *  An Image button. Load image from file (gif or jpg only). Image button 
 *  remains porportional by default, and can have a fixed size. 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class IButton extends Component implements MouseListener, MouseMotionListener {
	Image i = null;
	int height = 0, width = 0;
	int iheight = 0, iwidth = 0;
	Vector list = new Vector();
	
	private boolean pressed = false;
	
	private boolean sizeFixed = false;
	
	private boolean porportional = true;
	
	private boolean started = false;
	/**
	 *  Constructs an empty IButton object 
	 */
	public IButton() {
		addMouseListener (this);
		addMouseMotionListener (this);
	}
	/**
	 *  Constructs an IButton object with the images set to the value of the 
	 *  parameter either an image location or a string. 
	 *
	 *@param  s  the location of the image or the text of the button if no such 
	 *      location or image exists 
	 */
	public IButton (String s) {
		setImage (s);
		addMouseListener (this);
		addMouseMotionListener (this);
	}
	/**
	 *  Constructs an IButton object with the images set to the value of I. 
	 *
	 *@param  I  The image to be on the button 
	 */
	public IButton (Image I) {
		setImage (I);
		addMouseListener (this);
		addMouseMotionListener (this);
	}
	/**
	 *  Sets the Image attribute of the IButton object 
	 *
	 *@param  s  The new Image value. Either the location s or the text s if the 
	 *      location doesn't exist 
	 */
	public void setImage (String s) {
		try {
			if (i != null) {
				i.flush();
			}
			i = readImageFile (s);
			if (i == null) {
				return;
			}
			iheight = height = i.getHeight (this);
			iwidth = width = i.getWidth (this);
			repaint();
		}
		catch (IOException IOE) {
			i = null;
		}
	}
	/**
	 *  Sets the Image attribute of the IButton object 
	 *
	 *@param  I  The new Image value 
	 */
	public void setImage (Image I) {
		if (i != null) {
			i.flush();
		}
		i = I;
		if (i == null) {
			return;
		}
		iheight = height = i.getHeight (this);
		iwidth = width = i.getWidth (this);
		repaint();
	}
	/**
	 *  Sets the SizeFixed attribute of the IButton object 
	 *
	 *@param  b  The new SizeFixed value 
	 */
	public void setSizeFixed (boolean b) {
		sizeFixed = b;
		if (sizeFixed) {
			height = iheight;
			width = iwidth;
		}
		repaint();
	}
	/**
	 *  Sets the Porportional attribute of the IButton object 
	 *
	 *@param  b  The new Porportional value 
	 */
	public void setPorportional (boolean b) {
		porportional = b;
		repaint();
	}
	/**
	 *  Sets the Size attribute of the IButton object 
	 *
	 *@param  D  The new Size value 
	 */
	public void setSize (Dimension D) {
		setSize (D.width, D.height);
	}
	/**
	 *  Sets the Size attribute of the IButton object 
	 *
	 *@param  x  The new Size value 
	 *@param  y  The new Size value 
	 */
	public void setSize (int x, int y) {
		super.setSize (x, y);
		if (sizeFixed) {
			return;
		}
		if (x < 1) {
			x = iwidth + 1;
		}
		if (y < 1) {
			y = iheight + 1;
		}
		if (porportional && 1.0 * iwidth / iheight != 1.0 * x / y) {
			if ((int) (1.0 * iwidth / iheight * y) < x) {
				x = (int) (1.0 * iwidth / iheight * y);
			}
			else if ((int) (1.0 * iheight / iwidth * x) < y) {
				y = (int) (1.0 * iheight / iwidth * x);
			}
		}
		width = x - 1;
		height = y - 1;
		repaint();
	}
	/**
	 *  Sets the Bounds attribute of the IButton object 
	 *
	 *@param  r  The new Bounds value 
	 */
	public void setBounds (Rectangle r) {
		setBounds (r.x, r.y, r.width, r.height);
	}
	/**
	 *  Sets the Bounds attribute of the IButton object 
	 *
	 *@param  x  The new Bounds value 
	 *@param  y  The new Bounds value 
	 *@param  w  The new Bounds value 
	 *@param  h  The new Bounds value 
	 */
	public void setBounds (int x, int y, int w, int h) {
		super.setBounds (x, y, w, h);
		if (sizeFixed) {
			return;
		}
		if (h < 1) {
			h = iheight + 1;
		}
		if (w < 1) {
			w = iwidth + 1;
		}
		if (porportional && 1.0 * iwidth / iheight != 1.0 * w / h) {
			if ((int) (1.0 * iwidth / iheight * h) < w) {
				w = (int) (1.0 * iwidth / iheight * h);
			}
			else if ((int) (1.0 * iheight / iwidth * w) < h) {
				h = (int) (1.0 * iheight / iwidth * w);
			}
		}
		height = h - 1;
		width = w - 1;
		repaint();
	}
	/**
	 *  Gets the Image attribute of the IButton object 
	 *
	 *@return    The Image value 
	 */
	public Image getImage() {
		return i;
	}
	/**
	 *  Gets the SizeFixed attribute of the IButton object 
	 *
	 *@return    The SizeFixed value 
	 */
	public boolean getSizeFixed() {
		return sizeFixed;
	}
	/**
	 *  Gets the Porportional attribute of the IButton object 
	 *
	 *@return    The Porportional value 
	 */
	public boolean getPorportional() {
		return porportional;
	}
	/**
	 *  Gets the Size attribute of the IButton object 
	 *
	 *@return    The Size value 
	 */
	public Dimension getSize() {
		return new Dimension (width + 1, height + 1);
	}
	/**
	 *  Gets the MinimumSize attribute of the IButton object 
	 *
	 *@return    The MinimumSize value 
	 */
	public Dimension getMinimumSize() {
		if (sizeFixed) {
			return getPreferredSize();
		}
		else {
			return new Dimension (iwidth + 1, iheight + 1);
		}
	}
	/**
	 *  Gets the MaximumSize attribute of the IButton object 
	 *
	 *@return    The MaximumSize value 
	 */
	public Dimension getMaximumSize() {
		if (sizeFixed) {
			return getPreferredSize();
		}
		else {
			return new Dimension (1000, 1000);
		}
	}
	/**
	 *  Gets the PreferredSize attribute of the IButton object 
	 *
	 *@return    The PreferredSize value 
	 */
	public Dimension getPreferredSize() {
		if (sizeFixed) {
			return new Dimension (width + 1, height + 1);
		}
		return new Dimension (iwidth + 1, iheight + 1);
	}
	/**
	 *  Updates the image with out clearing the old one. 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void update (Graphics g) {
		paint (g);
	}
	/**
	 *  Repaints the image or text of the IButton. 
	 *
	 *@param  g  Description of Parameter 
	 */
	public void paint (Graphics g) {
		int w;
		int h;
		w = width;
		h = height;
		if (porportional && 1.0 * iwidth / iheight != 1.0 * w / h) {
			if ((int) (1.0 * iwidth / iheight * h) < w) {
				w = (int) (1.0 * iwidth / iheight * h);
			}
			if ((int) (1.0 * iheight / iwidth * w) < h) {
				h = (int) (1.0 * iheight / iwidth * w);
			}
		}
		if (i != null) {
			g.drawImage (i, (pressed?1:0), (pressed?1:0), w, h, getBackground(), this);
			//use below line to disable animated gifs and jpgs
			//g.drawImage(i,(pressed ? 1 : 0),(pressed ? 1 : 0),w,h,getBackground(),null);
		}
		g.setColor (( !pressed?getForeground():getBackground()));
		g.drawLine (0, 0, w, 0);
		g.drawLine (0, 0, 0, h);
		g.setColor (( !pressed?getForeground().brighter().brighter().brighter():getBackground().darker()));
		g.drawLine (1, 1, w - 2, 1);
		g.drawLine (1, 1, 1, h - 2);
		g.setColor ((pressed?getForeground():getBackground()));
		g.drawLine (0, h, w, h);
		g.drawLine (w, 0, w, h);
		g.setColor ((pressed?getForeground().brighter().brighter().brighter():getBackground().darker()));
		g.drawLine (1, h - 1, w - 2, h - 1);
		g.drawLine (w - 1, 1, w - 1, h - 2);
	}
	/**
	 *  Adds a feature to the ActionListener attribute of the IButton object 
	 *
	 *@param  AL  The feature to be added to the ActionListener attribute 
	 */
	public void addActionListener (ActionListener AL) {
		if ( !list.contains (AL) && AL != null) {
			list.addElement (AL);
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  AL  Description of Parameter 
	 */
	public void removeActionListener (ActionListener AL) {
		list.removeElement (AL);
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public void click (String s) {
		Vector l = (Vector)list.clone();
		ActionEvent AE = new ActionEvent (this, ActionEvent.ACTION_PERFORMED, s);
		int a;
		for (a = 0; a < l.size(); a++) {
			ActionListener AL = (ActionListener)l.elementAt (a);
			AL.actionPerformed (AE);
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseDragged (MouseEvent ME) {
		boolean inside = (ME.getX() - 0 > 0 && ME.getX() - 0 < width && ME.getY() - 0 > 0 && ME.getY() - 0 < height);
		if (inside && started && !getPressed()) {
			setPressed (true);
		}
		else if ( !inside && getPressed()) {
			setPressed (false);
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseMoved (MouseEvent ME) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseClicked (MouseEvent ME) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseEntered (MouseEvent ME) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseExited (MouseEvent ME) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mousePressed (MouseEvent ME) {
		if (ME.getX() - 0 > 0 && ME.getX() - 0 < width && ME.getY() - 0 > 0 && ME.getY() - 0 < height) {
			started = true;
			setPressed (true);
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ME  Description of Parameter 
	 */
	public void mouseReleased (MouseEvent ME) {
		if (getPressed()) {
			setPressed (false);
		}
		if (ME.getX() - 0 > 0 && ME.getX() - 0 < width && ME.getY() - 0 > 0 && ME.getY() - 0 < height && started) {
			click ("Clicked");
		}
		started = false;
	}
	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public boolean valid() {
		return i != null;
	}
	/**
	 *  Sets the Pressed attribute of the IButton object 
	 *
	 *@param  b  The new Pressed value 
	 */
	private void setPressed (boolean b) {
		pressed = b;
		repaint();
	}
	/**
	 *  Gets the Pressed attribute of the IButton object 
	 *
	 *@return    The Pressed value 
	 */
	private boolean getPressed() {
		return pressed;
	}
	/**
	 *  
	 *
	 *@param  s                Description of Parameter 
	 *@return                  Description of the Returned Value 
	 *@exception  IOException  Description of Exception 
	 */
	private Image readImageFile (String s)throws IOException {
		Image i = null;
		byte file_data[];
		InputStream IN;
		MediaTracker MT;
		try {
			IN = open (s);
		}
		catch (Exception e) {
			return null;
		}
		if (IN == null) {
			return null;
		}
		//		file_data = new byte[IN.available()];
		//		IN.read(file_data);
		Vector v = new Vector();
		int c;
		while (true) {
			c = IN.read();
			if (c == - 1)break;
			v.addElement (new Integer (c));
		}
		IN.close();
		file_data = new byte[v.size()];
		for (c = 0; c < file_data.length; c++) {
			file_data[c] = (byte) ((Integer)v.elementAt (c)).intValue();
		}
		i = getToolkit().createImage (file_data);
		MT = new MediaTracker (this);
		MT.addImage (i, 0);
		try {
			MT.waitForID (0);
		}
		catch (InterruptedException IE) {
			i = null;
		}
		return i;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	private InputStream open (String s) {
		if (s == null) {
			return null;
		}
		if (s.trim() == "") {
			return null;
		}
		try {
			File F = new File (s);
			if (F.exists() && F.isFile() && F.canRead()) {
				return new FileInputStream (F);
			}
		}
		catch (Exception E) {
			//      System.out.println("Bad file"+E);
		}
		try {
			URL U = new URL (s);
			return U.openStream();
		}
		catch (Exception EE) {
			//System.out.println("Bad URL"+EE);
		}
		return null;
	}
	//started click down
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main (String s[]) {
		Frame F = new Frame ("Test MyButton");
		F.setLayout (new BorderLayout());
		F.add ("North", new IButton (s[0]));
		F.add ("Center", new IButton (s[0]));
		F.add ("East", new IButton (s[0]));
		F.add ("West", new IButton (s[0]));
		F.add ("South", new IButton (s[0]));
		F.addWindowListener (new SimpleWindowManager());
		F.pack();
		F.setVisible (true);
	}
}

