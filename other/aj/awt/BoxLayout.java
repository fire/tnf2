package aj.awt;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.TextField;
import java.util.Vector;
/*
 * Each row and col are sized to the minimum possible.  Works correctly.
 */
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class BoxLayout implements LayoutManager {
	private int mastercol, masterrow;
	private int col, row;
	private boolean colfirst;
	private Vector vlist;
	private Component clist[][];
	private int rpheight[], rmheight[];
	private int cpwidth[], cmwidth[];
	private int srpheight[], srmheight[];
	private int scpwidth[], scmwidth[];

	/**
	 *  Constructor for the BoxLayout object 
	 *
	 *@param  x  Description of Parameter 
	 *@param  y  Description of Parameter 
	 */

	public BoxLayout (int x, int y) {
		masterrow = Math.max (x, 1);
		mastercol = Math.max (y, 1);
		colfirst = y > x;
		vlist = new Vector();
	}
	/**
	 *  Adds a feature to the LayoutComponent attribute of the BoxLayout object 
	 *
	 *@param  s  The feature to be added to the LayoutComponent attribute 
	 *@param  c  The feature to be added to the LayoutComponent attribute 
	 */
	
	public void addLayoutComponent (String s, Component c) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  c  Description of Parameter 
	 */
	public void removeLayoutComponent (Component c) {
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  C  Description of Parameter 
	 */
	public void layoutContainer (Container C) {
		int a;
		int b;
		makeLookup (C);
		Dimension d = C.getSize();
		double heightuse = 0;
		double widthuse = 0;
		heightuse = Math.max (d.height * 1.0 / srmheight[row - 1], 0);
		widthuse = Math.max (d.width * 1.0 / scmwidth[col - 1], 0);
		for (a = 0; a < row; a++) {
			rmheight[a] = (int) (rmheight[a] * heightuse);
			srmheight[a] = (int) (srmheight[a] * heightuse);
		}
		for (b = 0; b < col; b++) {
			cmwidth[b] = (int) (cmwidth[b] * widthuse);
			scmwidth[b] = (int) (scmwidth[b] * widthuse);
		}
		for (a = 0; a < row; a++) {
			for (b = 0; b < col; b++) {
				if (clist[a][b] == null) {
					continue;
				}
				//        int x,y;
				clist[a][b].setLocation (scmwidth[b] - cmwidth[b], srmheight[a] - rmheight[a]);
				clist[a][b].setSize (new Dimension ((int) (cmwidth[b]), (int) (rmheight[a])));
			}
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  c  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Dimension minimumLayoutSize (Container c) {
		makeLookup (c);
		return new Dimension (scmwidth[col - 1], srmheight[row - 1]);
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  c  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Dimension preferredLayoutSize (Container c) {
		makeLookup (c);
		return new Dimension (scpwidth[col - 1], srpheight[row - 1]);
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  C  Description of Parameter 
	 */
	private void makeLookup (Container C) {
		vlist = new Vector();
		Component[]CCC = C.getComponents();
		int a;
		int b;
		for (a = 0; a < CCC.length; a++) {
			vlist.addElement (CCC[a]);
		}
		if (colfirst) {
			col = mastercol;
			row = vlist.size() / col + 1;
		}
		else {
			row = masterrow;
			col = vlist.size() / row + 1;
		}
		clist = new Component[row][col];
		rpheight = new int[row];
		rmheight = new int[row];
		srpheight = new int[row];
		srmheight = new int[row];
		cpwidth = new int[col];
		cmwidth = new int[col];
		scpwidth = new int[col];
		scmwidth = new int[col];
		for (a = 0; a < vlist.size(); a++) {
			int r;
			int c;
			if (colfirst) {
				r = a / col;
				c = a % col;
			}
			else {
				r = a % row;
				c = a / row;
			}
			clist[r][c] = (Component)vlist.elementAt (a);
		}
		for (a = 0; a < row; a++) {
			for (b = 0; b < col; b++) {
				if (clist[a][b] != null) {
					srpheight[a] = rpheight[a] = Math.max (rpheight[a], clist[a][b].getPreferredSize().height);
					srmheight[a] = rmheight[a] = Math.max (rmheight[a], clist[a][b].getMinimumSize().height);
					scpwidth[b] = cpwidth[b] = Math.max (cpwidth[b], clist[a][b].getPreferredSize().width);
					scmwidth[b] = cmwidth[b] = Math.max (cmwidth[b], clist[a][b].getMinimumSize().width);
				}
			}
		}
		for (a = 1; a < row; a++) {
			srpheight[a] = srpheight[a - 1] + rpheight[a];
			srmheight[a] = srmheight[a - 1] + rmheight[a];
		}
		for (a = 1; a < col; a++) {
			scpwidth[a] = scpwidth[a - 1] + cpwidth[a];
			scmwidth[a] = scmwidth[a - 1] + cmwidth[a];
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main (String s[]) {
		if (s.length == 0) {
			System.out.println ("BoxLayout wid cols [<lableL> <textT> <buttonB> <ibuttonIB.gif>] ");
		}
		try {
			Frame F = new Frame();
			BoxLayout bl = new BoxLayout (Integer.parseInt (s[0]), Integer.parseInt (s[1]));
			Panel P = new Panel (bl);
			int a;
			for (a = 2; a < s.length; a++) {
				Component c;
				if (s[a].toLowerCase().startsWith ("button")) {
					System.out.println ("button made");
					c = new Button (s[a].substring (6));
				}
				else if (s[a].toLowerCase().startsWith ("text")) {
					System.out.println ("text made");
					c = new TextField (s[a].substring (4));
				}
				else if (s[a].toLowerCase().startsWith ("ibutton")) {
					System.out.println ("making Ibutton");
					IButton ib = new IButton (s[a].substring (7));
					if (ib.getImage() == null) {
						System.out.println ("ibutton label made");
						c = new Label (s[a].substring (7));
					}
					else {
						System.out.println ("ibutton made");
						c = ib;
					}
				}
				else if (s[a].toLowerCase().startsWith ("label")) {
					System.out.println ("label made");
					c = new Label (s[a].substring (5));
				}
				else {
					System.out.println ("label made");
					c = new Label (s[a]);
				}
				P.add (c);
			}
			F.add (P);
			F.pack();
			F.addWindowListener (new SimpleWindowManager());
			F.setVisible (true);
		}
		catch (NumberFormatException NFE) {
		}
	}
}

