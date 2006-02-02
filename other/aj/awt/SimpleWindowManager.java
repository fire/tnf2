package aj.awt;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class SimpleWindowManager implements WindowListener {
	boolean autoDispose = true;
	boolean exitOnClosing = true;
	
	public SimpleWindowManager() {
		exitOnClosing = true;
	}
	
	public SimpleWindowManager (boolean ad) {
		exitOnClosing = false;
		autoDispose = ad;
	}
	
	public void windowClosing (WindowEvent WE) {
		Window w = WE.getWindow();
		if (exitOnClosing)System.exit (1);
		else if (autoDispose) {
			w.dispose();
		}
		else {
			w.setVisible(false);//();
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  WE  Description of Parameter 
	 */
	public void windowActivated (WindowEvent WE) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  WE  Description of Parameter 
	 */
	public void windowClosed (WindowEvent WE) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  WE  Description of Parameter 
	 */
	public void windowDeactivated (WindowEvent WE) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  WE  Description of Parameter 
	 */
	public void windowDeiconified (WindowEvent WE) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  WE  Description of Parameter 
	 */
	public void windowIconified (WindowEvent WE) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  WE  Description of Parameter 
	 */
	public void windowOpened (WindowEvent WE) {
	}
	/**
	 *  A test method for the Simple window manager 
	 *
	 *@param  args  
	 */
	public static void main (String args[]) {
		Frame F = new Frame();
		F.addWindowListener (new SimpleWindowManager());
		F.setSize (100, 100);
		F.setVisible (true);
	}
}

