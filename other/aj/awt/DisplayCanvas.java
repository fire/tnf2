package aj.awt;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class DisplayCanvas extends Canvas implements MouseListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Color back=Color.lightGray;
	Image I;
	boolean redrawFlag = false;
	double scale = 1;
	Dimension d = new Dimension (50, 50);
	Vector v = new Vector();
	Point last=new Point(0,0), start=new Point(0,0);
	boolean down = false;
	DisplayItem xorme;

	public DisplayCanvas() {
		super();
		setBackground(back);
		addMouseListener (this);
		addMouseMotionListener (this);
	}
	public void setScale (double d) {
		scale = d;
		resize();
		refresh();
		repaint();
	}
	public void setPreferredSize (Dimension D) {
		d = D;
		setSize (getPreferredSize());
	}
	public double getScale() {
		return scale;
	}
	public Dimension getPreferredSize() {
		return new Dimension ((int) (d.width * scale), (int) (d.height * scale));
	}
	public void redraw() {
		redrawFlag = true;
	}
	public void refresh() {
		I = null;
	}
	public void update (Graphics g) {
		paint (g);
	}
	public void paint (Graphics g) {
		if (g==null) return;
		if (I == null) {
			I = createImage (getSize().width, getSize().height);
			Graphics G = I.getGraphics();
			G.clearRect (0, 0, getSize().width, getSize().height);
			G.setColor (getForeground());
			int a;
			for (a = 0; a < v.size(); a++) {
				DisplayItem d = (DisplayItem)v.elementAt (a);
				d.display (G, scale);
			}
		}
		if (redrawFlag) {
			Graphics G = I.getGraphics();
			G.clearRect (0, 0, getSize().width, getSize().height);
			G.setColor (getForeground());
			int a;
			for (a = 0; a < v.size(); a++) {
				DisplayItem d = (DisplayItem)v.elementAt (a);
				d.display (G, scale);
			}
		}
		g.drawImage (I, 0, 0, this);
	}
	public void zoom() {
		scale *= 1.5;
		resize();
		refresh();
		repaint();
	}
	public void unzoom() {
		scale /= 1.5;
		resize();
		refresh();
		repaint();
	}
	public void resize() {
		setSize ((int) (d.width * scale), (int) (d.height * scale));
	}
	public void add (DisplayItem D) {
		if ( D!=null && !v.contains (D)) {
			v.addElement (D);
			repaint();
		}
	}
	public void remove (DisplayItem D) {
		if (v.contains (D)) {
			v.removeElement (D);
			repaint();
		}
	}
	public void removeAllDisplayItems() {
		v.removeAllElements();
		repaint();
	}
	public void addAll (Vector V) {
		int a;
		for (a = 0; a < V.size(); a++) {
			if ( V.elementAt(a)!=null && !v.contains (V.elementAt (a))) {
				v.addElement (V.elementAt (a));
			}
		}
	}
	public void mouseClicked (MouseEvent e) {
	}
	public void mousePressed (MouseEvent e) {
		if (v.size() == 0) {
			return;
		}
		last = start = new Point (e.getX(), e.getY());
		down = true;
		int a;
		xorme = (DisplayItem)v.elementAt (0);
		for (a = 1; a < v.size(); a++) {
			DisplayItem n = (DisplayItem)v.elementAt (a);
			double dx = (n.getX (scale) - e.getX());
			double dy = (n.getY (scale) - e.getY());
			double odx = (xorme.getX (scale) - e.getX());
			double ody = (xorme.getY (scale) - e.getY());
			if (Math.sqrt (dx * dx + dy * dy) < Math.sqrt (odx * odx + ody * ody)) {
				xorme = n;
			}
		}
		//Graphics g=getGraphics();
		//g.setXORMode(back);
		//xorme.display(g,scale);
	}
	public void mouseReleased (MouseEvent e) {
		down = false;
		int dx = e.getX() - last.x;
		int dy = e.getY() - last.y;
		xorme.setX (xorme.getX (scale) + dx, scale);
		xorme.setY (xorme.getY (scale) + dy, scale);
		refresh();
		repaint();
	}
	public void mouseEntered (MouseEvent e) {
	}
	public void mouseExited (MouseEvent e) {
	}
	public void mouseDragged (MouseEvent e) {
		if (down) {
			int dx = e.getX() - last.x;
			int dy = e.getY() - last.y;
			last = new Point(e.getX(),e.getY());//start;
			Graphics g = getGraphics();
			g.setXORMode (back);
			xorme.display (g, scale);

			xorme.setX (xorme.getX (scale) + dx, scale);
			xorme.setY (xorme.getY (scale) + dy, scale);
			xorme.display (g, scale);
//
//			xorme.setX (xorme.getX (scale) - dx, scale);
//			xorme.setY (xorme.getY (scale) - dy, scale);
//			dx = e.getX() - start.x;
//			dy = e.getY() - start.y;
//			xorme.setX (xorme.getX (scale) + dx, scale);
//			xorme.setY (xorme.getY (scale) + dy, scale);
//			xorme.display (g, scale);
//
//			xorme.setX (xorme.getX (scale) - dx, scale);
//			xorme.setY (xorme.getY (scale) - dy, scale);
		}
	}
	public void mouseMoved (MouseEvent e) {
	}
}

