package aj.loan;

//update start time and start
//offical
//see  http://www.publicdebt.treas.gov/opd/opdpenny.htm
//private
//http://www.brillig.com/debt_clock/
//
//configured to offical data below
//11/20/2000 5,699,552,938,717.19
//rate by average of past 4 years (9/30/97 - 9/30/93)= 686066526
//using longs for speed.
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.util.Date;

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
public class DebtClock extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static boolean APPLET = false;

	/**
	 * Description of the Method
	 */
	public void init() {
		setLayout(new BorderLayout());
		Counter c = new Counter();
		add("Center", c);
		new Thread(c).start();
	}

	/**
	 * Description of the Method
	 * 
	 * @param argv
	 *            Description of Parameter
	 */
	public static void main(String argv[]) {
		Counter c = new Counter();
		Frame F = new Frame();
		Panel P = new Panel(new BorderLayout());
		P.add("Center", c);
		F.add("Center", P);
		F.pack();
		F.setVisible(true);
		new Thread(c).start();
	}
}

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
class Counter extends Canvas implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Date starttime = new Date(974848592358L);// TUE Nov 21, 2000 1 day off

	long start = (long) ((double) 5699552.93871719 * 1000000);

	long rate = (long) ((double) 686066526);

	Image I = null;

	/**
	 * Gets the PreferredSize attribute of the Counter object
	 * 
	 * @return The PreferredSize value
	 */
	public Dimension getPreferredSize() {
		if (getGraphics() == null) {
			return new Dimension(0, 0);
		}
		FontMetrics F = getGraphics().getFontMetrics();
		return new Dimension(F.stringWidth(getCurrDebt()), F.getHeight());
	}

	/**
	 * Gets the MinimumSize attribute of the Counter object
	 * 
	 * @return The MinimumSize value
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * Gets the MaximumSize attribute of the Counter object
	 * 
	 * @return The MaximumSize value
	 */
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	/**
	 * Gets the CurrDebt attribute of the Counter object
	 * 
	 * @return The CurrDebt value
	 */
	public String getCurrDebt() {
		Date D = new Date();
		long newstart = start + rate * (D.getTime() - starttime.getTime())
				/ 86400000;
		String curr = "$" + numcoma(newstart + "");
		return curr;
	}

	public boolean running = true;

	/**
	 * Main processing method for the Counter object
	 */
	public void run() {
		System.out.println("Run called");
		while (running) {
			repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException IOE) {
			}
		}
		System.out.println("Run stopped");
	}

	public void start() {
		running = true;
		System.out.println("Starting");
		// run();
	}

	public void stop() {
		System.out.println("Stopping");
		running = false;
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 */
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 */
	public void paint(Graphics g) {
		Dimension D = getPreferredSize();
		if (I == null) {
			I = createImage(D.width, D.height);
		}
		Graphics G = I.getGraphics();
		G.clearRect(0, 0, D.width, D.height);
		G.drawString(getCurrDebt(), 0, D.height - 2);
		g.drawImage(I, 0, 0, D.width, D.height, this);
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public String numcoma(String S) {
		String R = "";
		int count = 0;
		while (S.length() > 0) {
			R = S.substring(S.length() - 1) + R;
			S = S.substring(0, S.length() - 1);
			count++;
			if (count % 3 == 0 && count != 0 && S.length() != 0) {
				R = "," + R;
			}
		}
		return R;
	}
}
