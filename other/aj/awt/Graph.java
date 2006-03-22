package aj.awt;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import aj.misc.GmlPair;
import aj.misc.Stuff;
import aj.misc.Tree;
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    June 21, 2000 
 */
public class Graph extends ScrollZoomDisplayCanvas implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int CENTER = 300;
	String mainFile;
	
	static MenuBar mb = new MenuBar();
	
	static Menu f = new Menu ("File");
	
	static MenuItem save = new MenuItem ("Save");
	
	static Menu m = new Menu ("Layout");
	
	static MenuItem rand = new MenuItem ("Random");
	
	static MenuItem tree = new MenuItem ("Tree");
	
	static MenuItem circ = new MenuItem ("Tree2");
	
	static MenuItem circ2 = new MenuItem ("Circle2");
	
	static Vector nodes = new Vector();
	
	static Vector links = new Vector();
	
	public Graph (String s[]) {
		try {
			mainFile = s[0];
			GmlPair g = GmlPair.parse (new File (s[0]));
			//      System.out.println("GML PARSED");
			GmlPair n[] = g.getAllByName ("node");
			GmlPair l[] = g.getAllByName ("link");

			//System.out.println(n.length+"");
			for (int a = 0; a < n.length; a++) {
				Node node=Node.parse(n[a]);
				if (node==null) {
					System.out.println("MyError: bad node"+n[a]);
				}
				else {
					nodes.addElement (node);
					add (node);
					//System.out.println("Node read"+node.getId());
				}
/*
				GmlPair nn[] = n[a].getAllByName ("name");
				GmlPair ii[] = n[a].getAllByName ("id");
				Node nnn = null;
				String name = "";
				if (nn.length > 0) {
					name = nn[0].getString();
				}
				if (ii.length > 0) {
					nnn = new Node (name, (int)ii[0].getDouble());
				}
				else {
					nnn = new Node (name);
				}
				nn = n[a].getAllByName ("x");
				if (nn.length > 0) {
					nnn.setX ((int) (nn[0].getDouble()));
				}
				nn = n[a].getAllByName ("y");
				if (nn.length > 0) {
					nnn.setY ((int) (nn[0].getDouble()));
				}
				nodes.addElement (nnn);
				add (nnn);
*/
			}
			//System.out.println("nodes done");
			//System.out.println(n.length+"");
			for (int a=0; a < l.length; a++) {
				Link lll=Link.parse(l[a],nodes);
				if (lll==null) continue;
				links.addElement (lll);
				add (lll);
				//System.out.println("Link read ");

/*
				Link lll = makeLink (l[a]);
				if (lll==null) continue;
				links.addElement (lll);
				add (lll);
*/
			}
			//System.out.println("links done");
			System.out.println (nodes.size() + " " + links.size() + " things read");
		}
		catch (IOException IOE) {
			System.out.println ("MyError: " + IOE);
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public void actionPerformed (ActionEvent ae) {
		super.actionPerformed (ae);
		if (ae.getSource() == rand) {
			layoutRandom();
			refresh();
		}
		else if (ae.getSource() == tree) {
			layoutTree();
			refresh();
		}
		else if (ae.getSource() == circ) {
			layoutGraph2();//layoutCircle();
			refresh();
		}
		else if (ae.getSource() == circ2) {
			layoutCircle2();
			refresh();
		}
		else if (ae.getSource() == save) {
			save();
		}
	}
	/**
	 *  Description of the Method 
	 */
	public void save() {
		try {
			Vector v = new Vector();
			for (int a=0; a < nodes.size(); a++) {
				Node n = (Node)nodes.elementAt (a);
				v.addElement (n.toGmlPair());
			}
			for (int a=0; a < links.size(); a++) {
				Link l = (Link)links.elementAt (a);
				v.addElement (l.toGmlPair());
			}
			GmlPair g = new GmlPair ("graph", v);
			PrintWriter pw = new PrintWriter (new FileWriter (mainFile));
			String all = g.prettyPrint ("");
			while (all.indexOf ("\n") >= 0) {
				pw.println (all.substring (0, all.indexOf ("\n")));
				all = all.substring (all.indexOf ("\n") + 1);
			}
			pw.println (all);
			pw.flush();
			pw.close();
		}
		catch (IOException IOE) {
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  l  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Link makeLink (GmlPair l) {
		GmlPair ff[] = l.getAllByName ("fromnode");
		GmlPair tt[] = l.getAllByName ("tonode");
		GmlPair cc[] = l.getAllByName ("color");
		Color C = Color.black;
		if (cc.length > 0) {
			if (cc[0].isList()) {
				GmlPair r = cc[0].getAllByName ("red")[0];
				GmlPair g = cc[0].getAllByName ("green")[0];
				GmlPair bl = cc[0].getAllByName ("blue")[0];
				C = new Color ((int) (r.getDouble()), (int) (g.getDouble()), (int) (bl.getDouble()));
			}
			else {
				if (cc[0].getString().equalsIgnoreCase ("RED")) {
					C = Color.red;
				}
				if (cc[0].getString().equalsIgnoreCase ("BLUE")) {
					C = Color.blue;
				}
				if (cc[0].getString().equalsIgnoreCase ("YELLOW")) {
					C = Color.yellow;
				}
				if (cc[0].getString().equalsIgnoreCase ("GREEN")) {
					C = Color.green;
				}
				if (cc[0].getString().equalsIgnoreCase ("CYAN")) {
					C = Color.cyan;
				}
			}
		}
		if (ff.length>0 && tt.length>0 && ff[0].isDouble() && tt[0].isDouble()) {
			Node fff = getNode ((int)ff[0].getDouble());
			Node ttt = getNode ((int)tt[0].getDouble());
			return new Link (fff, ttt, C);
		}
		else if (ff.length>0 && tt.length>0 && ff[0].isString() && tt[0].isString()) {
			Vector to = new Vector();
			Vector from = new Vector();
			String f[] = Stuff.getTokens (ff[0].getString(), " ,\t");
			String t[] = Stuff.getTokens (tt[0].getString(), " ,\t");
			for (int a=0; a < f.length; a++) {
				Node FN = Graph.getNode (f[a]);
				if (FN != null) {
					from.addElement (FN);
				}
				else {
					System.out.println ("MyError: no from in link" + f[a]);
				}
			}
			for (int a=0; a < t.length; a++) {
				Node TN = Graph.getNode (t[a]);
				if (TN != null) {
					to.addElement (TN);
				}
				else {
					System.out.println ("MyError: no to in link" + t[a]);
				}
			}
			return new Link (from, to, C);
		}
		else {
			System.out.println ("MyError: bad link parsed "+l);
		}
		return null;
	}
	/**
	 *  Description of the Method 
	 */
	public void layoutRandom() {
		Vector v = (Vector)nodes.clone();
		for (int a=0; a < v.size(); a++) {
			Node n = (Node)v.elementAt (a);
			n.x = (int) (Math.random() * CENTER);
			n.y = (int) (Math.random() * CENTER);
		}
	}
	/**
	 *  Description of the Method 
	 */
	public void layoutCircle2() {
		int avg = 80;
		Node n;
		Dimension d = new Dimension (avg, avg);
		double ang = Math.PI * 2 / nodes.size();
		for (int a=0; a < nodes.size(); a++) {
			n = (Node)nodes.elementAt (a);
			n.x = (int) (d.width * Math.cos (a * ang) + avg);
			n.y = (int) (d.width * Math.sin (a * ang) + avg);
		}
	}

//better layout needed
//all sub graphs
//  add children to parent subgraph
//    repeat until all graph done
//  layout each parent graph gets equal shar of screen
//     parents give equal shar to children

//ignore non-connected nodes.
//sort all roots
//  build trees from each root
//  if trees merge, then put root in inviislbe parent root

//use netpars algorythms
	public void layoutGraph2() {
                Vector roots = (Vector)nodes.clone();
                Vector l = (Vector)links.clone();
                Vector n = (Vector)nodes.clone();
                for (int a=0; a < l.size(); a++) {
                        Link t = (Link) l.elementAt(a);
                        for (int b=0;b<t.to.size();b++) {
				Node child =(Node) t.to.elementAt(b);
                        	roots.removeElement(child);
			}
                }
                Tree T = new Tree("Base");
                for (int a=0; a < roots.size(); a++) {
                        Node rt = (Node) roots.elementAt(a);
                        T.addChild(rt);
                        n.removeElement(rt);
                }
		while (n.size() > 0) {
                        for (int a=0; a < l.size(); a++) {
                                Link t = (Link) l.elementAt(a);
                                Vector par = t.from;//getParentNode(t);//t.getParentNodes();
                                Vector child = t.to;//getChildNode(t);//t.getChildNode();
                                for (int b=0;b<child.size();b++) {
					if (T.contains(child.elementAt(b))) continue;
					for (int c=0;c<par.size();c++) {
						if (T.contains(par.elementAt(c))) {
                                                	T.addChildOf(par.elementAt(c), child.elementAt(b));
                                                	n.removeElement(child.elementAt(b));
                                                	break;
						}
					}
				}
                        }
                }
                layoutTree(T, 0, 0, 400, 400);
        }


        public void layoutTree(Tree T, int minx, int miny, int maxx, int maxy) {
                int cx = (maxx - minx) / 2 + minx;
                if (T.getRoot() instanceof Node) {
                        Node n = (Node) T.getRoot();
                        n.setX(cx);
			n.setY(miny);//n.setCenter(cx, miny);
                }
                Vector v = T.getChildren();
                double dx = (maxx - minx) / T.getWidth();
                int dy = (maxy - miny) / T.getDepth();
                miny += dy;
                for (int a=0; a < v.size(); a++) {
                        Tree t = (Tree) v.elementAt(a);
                        layoutTree(t, minx, miny, (int) (minx + dx * t.getWidth()), maxy);
                        minx += dx * t.getWidth();
                }
        }




	public void layoutTree() {
		Vector all = new Vector();
		Vector root = (Vector)nodes.clone();
		while (root.size() > 0) {
			Vector f = new Vector();
			for (int a = 0; a < root.size(); a++) {
				Node n = (Node)root.elementAt (a);
				Vector v = linkedNodesFrom (n);
				if (v.size() == 0) {
					continue;
				}
				for (int b = 0; b < v.size(); b++) {
					Node nn = (Node)v.elementAt (b);
					if (root.contains (nn) && !f.contains (nn)) {
						f.addElement (nn);
					}
				}
			}
			if (f.size() == root.size()) {
				f.removeElementAt (0);
			}
			for (int b = 0; b < f.size(); b++) {
				root.removeElement (f.elementAt (b));
			}
			if (f.size() > 0 && root.size() > 0) {
				all.addElement (root);
				root = f;
			}
			else {
				all.addElement (root);
				break;
			}
		}
		int hei = all.size();
		int wid = 0;
		for (int a = 0; a < all.size(); a++) {
			Vector v = (Vector)all.elementAt (a);
			wid = Math.max (wid, v.size());
		}
		for (int a = 0; a < all.size(); a++) {
			Vector v = (Vector)all.elementAt (a);
			for (int b = 0; b < v.size(); b++) {
				Node n = (Node)v.elementAt (b);
				int x = b * 500 / v.size() + 20 + (v.size() < hei?10 * (b % 4):0);
				int y = a * 500 / hei + 20 + (v.size() > hei?10 * (b % 4):0);
				n.setX (x);
				n.setY (y);
			}
		}
	}
	/**
	 *  Description of the Method 
	 */
	public void layoutCircle() {
		Vector inner = (Vector)nodes.clone();
		Vector outer = new Vector();
		int radious = 40;
		int steps = 0;
		while (inner.size() > 0 && steps < 6) {
			steps++;
			for (int a=0; a < inner.size(); a++) {
				Node n = (Node)inner.elementAt (a);
				Vector v = linkedNodesFrom (n);
				for (int b = 0; b < v.size(); b++) {
					Node t = (Node)v.elementAt (b);
					if (inner.contains (t) && !outer.contains (n) && t != n) {
						outer.addElement (t);
					}
				}
			}
			for (int a=0; a < outer.size(); a++) {
				inner.removeElement (outer.elementAt (a));
			}
			while (inner.size() > radious / 4) {
				outer.addElement (inner.elementAt (0));
				inner.removeElementAt (0);
			}
			double ra = Math.random() * Math.PI * 2;
			for (int a=0; a < inner.size(); a++) {
				Node n = (Node)inner.elementAt (a);
				n.x = (int) (Math.cos (2 * Math.PI * a / inner.size() + ra) * radious) + CENTER;
				n.y = (int) (Math.sin (2 * Math.PI * a / inner.size() + ra) * radious) + CENTER;
			}
			inner = outer;
			outer = new Vector();
			radious += 40;
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  n  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	private Vector linkedNodesFrom (Node n) {
		Vector v = new Vector();
		for (int a=0; a < links.size(); a++) {
			Link l = (Link)links.elementAt (a);
			if (l.from.contains (n)) {
				Vector t = l.to;
				for (int b = 0; b < t.size(); b++) {
					if ( !v.contains (t.elementAt (b))) {
						v.addElement (t.elementAt (b));
					}
				}
			}
		}
		return v;
	}
	/**
	 *  Gets the Node attribute of the Graph class 
	 *
	 *@param  i  Description of Parameter 
	 *@return    The Node value 
	 */
	public static Node getNode (int i) {
		for (int a=0; a < nodes.size(); a++) {
			Node n = (Node)nodes.elementAt (a);
			if (n.id == i) {
				return n;
			}
		}
		return null;
	}
	/**
	 *  Gets the Node attribute of the Graph class 
	 *
	 *@param  s  Description of Parameter 
	 *@return    The Node value 
	 */
	public static Node getNode (String s) {
		for (int a=0; a < nodes.size(); a++) {
			Node n = (Node)nodes.elementAt (a);
			if (n.name.trim().equalsIgnoreCase (s.trim())) {
				return n;
			}
		}
		try {
			int i = (int) (Integer.parseInt (s.trim()));
			for (int a=0; a < nodes.size(); a++) {
				Node n = (Node)nodes.elementAt (a);
				if (n.id == i) {
					return n;
				}
			}
		}
		catch (NumberFormatException NFE) {
		}
		return null;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main (String s[]) {
		if (s.length == 0) {
			System.out.println ("FORMAT: java aj.awt.Graph <gmlfile> [-BYID]");
			System.out.println ("  -BYID save links with node ID instead of name");
			System.exit (0);
		}
		for (int a=0;a<s.length;a++) {
			if (s[a].equalsIgnoreCase("-BYID")) Link.BYID=true;
		}
		mb.add (f);
		f.add (save);
		mb.add (m);
		m.add (rand);
		m.add (tree);
		m.add (circ);
		m.add (circ2);
		Frame f = new Frame();
		Graph g = new Graph (s);
		rand.addActionListener (g);
		tree.addActionListener (g);
		circ.addActionListener (g);
		circ2.addActionListener (g);
		save.addActionListener (g);
		g.setPreferredSize (600, 600);
		f.add ("Center", g);
		f.setMenuBar (mb);
		f.setVisible (true);
		f.addWindowListener (new SimpleWindowManager());
		f.pack();
	}
}

