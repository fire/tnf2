package aj.smug;

import java.applet.Applet;
import java.awt.BorderLayout;

public class PlanetViewApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PlanetView pv;

	public void init() {
		int size = -1, nummoons = -1, numsats = -1, neb = -1, seed = -1;
		try {
			String tm = getParameter("size");
			if (tm != null)
				size = Integer.parseInt(tm);
			tm = getParameter("nummoons");
			if (tm != null)
				nummoons = Integer.parseInt(tm);
			tm = getParameter("numsats");
			if (tm != null)
				numsats = Integer.parseInt(tm);
			tm = getParameter("neb");
			if (tm != null)
				neb = Integer.parseInt(tm);
			tm = getParameter("seed");
			if (tm != null)
				seed = Integer.parseInt(tm);
		} catch (NumberFormatException NFE) {
			System.out.println("FORMAT: java aj.misc.Bench [<SEC>]");
			System.exit(0);
		}
		pv = new PlanetView(size, nummoons, numsats, neb, seed);
		// read params
		setLayout(new BorderLayout());
		add("Center", pv);
	}

	public void stop() {
		pv.stop();
	}

	public void start() {
		pv.start();
	}
}
