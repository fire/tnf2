package aj.misc;

import java.applet.Applet;
import java.awt.BorderLayout;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class ViewerApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *  Description of the Method 
	 */
	public void init() {
		//read params
		String urls = getParameter("files");
		if (urls==null) {
			System.out.println("MyError: no parameter 'files' in applet");
			System.exit(0);
		}

		String all[] = aj.misc.Stuff.getTokens(urls, " ,\t");
		String base = getCodeBase().toString();
		int a;
		for (a = 0; a < all.length; a++) {
			all[a] = all[a].trim();
			if (!all[a].toUpperCase().startsWith("HTTP://")) {
				all[a] = base + all[a];
			}
		}

		//make Viewer with args
		setLayout(new BorderLayout());
		add("Center", new aj.misc.Viewer(all));
	}
}
