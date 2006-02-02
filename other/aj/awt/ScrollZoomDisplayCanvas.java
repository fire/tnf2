package aj.awt;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class ScrollZoomDisplayCanvas extends Panel implements ActionListener {
	Button zoom, unzoom;
	TextField exactZoom;
	DisplayCanvas disp;
	ScrollPane sp;
	/**
	 *  Constructor for the ScrollZoomDisplayCanvas object 
	 */
	public ScrollZoomDisplayCanvas() {
		super();
		disp = new DisplayCanvas();
		sp = new ScrollPane();
		sp.add (disp);
		exactZoom = new TextField (disp.getScale() + "");
		exactZoom.addActionListener (this);
		zoom = new Button ("Zoom");
		zoom.addActionListener (this);
		unzoom = new Button ("UnZoom");
		unzoom.addActionListener (this);
		Panel P = new Panel (new FlowLayout());
		P.add (zoom);
		P.add (unzoom);
		P.add (exactZoom);
		setLayout (new BorderLayout());
		add ("North", P);
		add ("Center", sp);
	}
	/**
	 *  Sets the PreferredSize attribute of the ScrollZoomDisplayCanvas object 
	 *
	 *@param  x  The new PreferredSize value 
	 *@param  y  The new PreferredSize value 
	 */
	public void setPreferredSize (int x, int y) {
		setPreferredSize (new Dimension (x, y));
	}
	/**
	 *  Sets the PreferredSize attribute of the ScrollZoomDisplayCanvas object 
	 *
	 *@param  D  The new PreferredSize value 
	 */
	public void setPreferredSize (Dimension D) {
		disp.setPreferredSize (D);
	}
	/**
	 *  Sets the Scale attribute of the ScrollZoomDisplayCanvas object 
	 *
	 *@param  d  The new Scale value 
	 */
	public void setScale (double d) {
		disp.setScale (d);
		exactZoom.setText (disp.getScale() + "");
		resize();
		repaint();
	}
	/**
	 *  Gets the PreferredSize attribute of the DisplayCanvas object 
	 *
	 *@return    The PreferredSize value 
	 */
	public Dimension getPreferredSize() {
		return disp.getPreferredSize();
	}
	/**
	 *  Gets the Scale attribute of the ScrollZoomDisplayCanvas object 
	 *
	 *@return    The Scale value 
	 */
	public double getScale() {
		return disp.getScale();
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  d  Description of Parameter 
	 */
	public void add (DisplayItem d) {
		disp.add (d);
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  d  Description of Parameter 
	 */
	public void remove (DisplayItem d) {
		disp.remove (d);
	}
	/**
	 *  Description of the Method 
	 */
	public void removeAllDisplayItems() {
		disp.removeAllDisplayItems();
		repaint();
	}
	/**
	 *  Description of the Method 
	 */
	public void zoom() {
		disp.zoom();
		exactZoom.setText (disp.getScale() + "");
		resize();
		repaint();
	}
	/**
	 *  Description of the Method 
	 */
	public void unzoom() {
		disp.unzoom();
		exactZoom.setText (disp.getScale() + "");
		resize();
		repaint();
	}
	/**
	 *  Description of the Method 
	 */
	public void resize() {
		sp.doLayout();
	}
	/**
	 *  Description of the Method 
	 */
	public void refresh() {
		disp.refresh();
		disp.repaint();
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  AE  Description of Parameter 
	 */
	public void actionPerformed (ActionEvent AE) {
		if (AE.getSource().equals (exactZoom)) {
			setScale (new Double (exactZoom.getText()).doubleValue());
		}
		else if (AE.getSource().equals (zoom)) {
			zoom();
		}
		else if (AE.getSource().equals (unzoom)) {
			unzoom();
		}
	}
}

