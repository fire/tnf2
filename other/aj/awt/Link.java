package aj.awt;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Vector;

import aj.misc.GmlPair;
import aj.misc.Stuff;

/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class Link implements DisplayItem {
	String fname, tname;
	Vector from = new Vector(), to = new Vector();
	Color color = Color.black;
	Vector all=new Vector();

	static boolean BYID=false;

	public Link (Node f, Node t, Color c) {
		from.addElement (f);
		to.addElement (t);
		color = c;
	}

	public Link (Vector f, Vector t, Color c) {
		to = (Vector)t.clone();
		from = (Vector)f.clone();
		color = c;
	}

	public void setX (double d, double s) {}
	public void setY (double d, double s) {}
	public double getX (double s) {
		return 0;
	}
	public double getY (double s) {
		return 0;
	}
	public String toString () {return ""+toGmlPair();}
	public GmlPair toGmlPair() {
		Vector v = new Vector();
		String f = "";
		for (int a = 0; a < from.size(); a++) {
			Node fn = (Node)from.elementAt (a);
			if (!BYID) f += fn.getName() + "";
			else f += fn.getId() + "";
			if (a < from.size() - 1) {
				f += ",";
			}
		}
		String t = "";
		for (int a = 0; a < to.size(); a++) {
			Node tn = (Node)to.elementAt (a);
			if (!BYID) t += tn.getName() + "";
			else t += tn.getId() + "";
			if (a < to.size() - 1) {
				t += ",";
			}
		}
		v.addElement (new GmlPair ("fromnode", f));
		v.addElement (new GmlPair ("tonode", t));
		Vector c = new Vector();
		if (color.equals (Color.red)) {
			v.addElement (new GmlPair ("color", "red"));
		}
		else if (color.equals (Color.blue)) {
			v.addElement (new GmlPair ("color", "blue"));
		}
		else if (color.equals (Color.green)) {
			v.addElement (new GmlPair ("color", "green"));
		}
		else if (color.equals (Color.yellow)) {
			v.addElement (new GmlPair ("color", "yellow"));
		}
		else if (color.equals (Color.cyan)) {
			v.addElement (new GmlPair ("color", "cyan"));
		}
		else if (color.equals (Color.white)) {
			v.addElement (new GmlPair ("color", "white"));
		}
		else if (color.equals (Color.black)) {
			v.addElement (new GmlPair ("color", "black"));
		}
		else {
			c.addElement (new GmlPair ("red", color.getRed()));
			c.addElement (new GmlPair ("green", color.getGreen()));
			c.addElement (new GmlPair ("blue", color.getBlue()));
			v.addElement (new GmlPair ("color", c));
		}
		for (int a=0;a<all.size();a++) {
			v.addElement(all.elementAt(a));
		}
		return new GmlPair ("link", v);
	}

	public void display (Graphics g, double s) {
		int a;
		int b;
		g.setColor (color);
		if (from.size() == 0 || to.size() == 0) {
			System.out.println ("MyError: bad link nodes not found.  from:"+from+" to:"+to);
		}
		//System.out.println(from.size()+" "+to.size());
		for (a = 0; a < from.size(); a++) {
			for (b = 0; b < to.size(); b++) {
				Node f = (Node)from.elementAt (a);
				Node t = (Node)to.elementAt (b);
				if (f==t) continue;
				g.drawLine ((int)f.getX (s), (int)f.getY (s), (int)t.getX (s), (int)t.getY (s));
				Arrow A = new Arrow ((int)f.getX (s), (int)f.getY (s), (int)t.getX (s), (int)t.getY (s));
				A.display (g, 1);
			}
		}
		g.setColor (Color.black);
	}

	public static Link parse(GmlPair g,Vector nodes) {
		if (!g.getName().equalsIgnoreCase("LINK")) return null;
		Vector fn=new Vector();
		Vector tn=new Vector();
		GmlPair fngml[]=g.getAllByName("FROMNODE");
		GmlPair tngml[]=g.getAllByName("TONODE");
		GmlPair cgml=g.getOneByName("COLOR");
		Color color=Color.black;
		if (cgml!=null && cgml.isList()) {
			GmlPair r=cgml.getOneByName("red");
			GmlPair gr=cgml.getOneByName("green");
			GmlPair b=cgml.getOneByName("blue");
			if (r==null || gr==null || b==null) color=Color.black;
			else {
				color=new Color((int)(r.getDouble())%256,(int)(gr.getDouble())%256,(int)(b.getDouble())%256);
			}
		}
		else if (cgml!=null) {
			String cna=cgml.getString();
			if (cna.equalsIgnoreCase("BLACK")) color=Color.black;
			else if (cna.equalsIgnoreCase("WHITE")) color=Color.white;
			else if (cna.equalsIgnoreCase("red")) color=Color.red;
			else if (cna.equalsIgnoreCase("blue")) color=Color.blue;
			else if (cna.equalsIgnoreCase("green")) color=Color.green;
			else if (cna.equalsIgnoreCase("yellow")) color=Color.yellow;
			else if (cna.equalsIgnoreCase("cyan")) color=Color.cyan;
			else color=Color.black;
		}
		for (int a=0;a<fngml.length;a++) {
			String nlist[]=Stuff.getTokens(fngml[a].getString()," ,\t");
			for (int c=0;c<nlist.length;c++) {
				for (int b=0;b<nodes.size();b++) {
					Node nnn=(Node)nodes.elementAt(b);
					try {
						int test=(int)Double.parseDouble(nlist[c].trim());
						if (test==nnn.getId()) {
							fn.addElement(nnn);
							break;
						}
					} catch (NumberFormatException nfe) {
					}
 					if (nlist[c].equalsIgnoreCase(nnn.getName())) {
						fn.addElement(nnn);
						break;
					}
					if (b+1==nodes.size())
						System.out.println("MyError: No fromnode found with id "+nlist[c]+".  line ="+fngml[a]);
				}
			}
		}
		for (int a=0;a<tngml.length;a++) {
			String nlist[]=Stuff.getTokens(tngml[a].getString()," ,\t");
			for (int c=0;c<nlist.length;c++) {
				for (int b=0;b<nodes.size();b++) {
					Node nnn=(Node)nodes.elementAt(b);
					try {
						int test=(int)Double.parseDouble(nlist[c].trim());
						if (test==nnn.getId()) {
							tn.addElement(nnn);
							break;
						}
					} catch (NumberFormatException nfe) {}
					if (nlist[c].equalsIgnoreCase(nnn.getName())) {
						tn.addElement(nnn);
						break;
					}
					if (b+1==nodes.size())
						System.out.println("MyError: No tonode found with id "+nlist[c]+".  line ="+tngml[a]);
				}
			}
		}
		Link ln=new Link(fn,tn,color);
		Vector all=g.getListVector();
		all.removeElement(cgml);
		for (int a=0;a<fngml.length;a++) {
			all.removeElement(fngml[a]);
		}
		for (int a=0;a<tngml.length;a++) {
			all.removeElement(tngml[a]);
		}
		ln.all=all;
		return ln;
	}

	public static void main(String s[]) {
		Vector nodes=new Vector();
		Node n=new Node("test");
		System.out.println(n);
		nodes.addElement(n);
		n=new Node("test2");
		System.out.println(n);
		nodes.addElement(n);
		n=new Node("test3");
		System.out.println(n);
		nodes.addElement(n);
		try {
		GmlPair g=GmlPair.parse("Link []");
		Link l=Link.parse(g,nodes);
		System.out.println(g+" = "+l);
		g=GmlPair.parse("Link [fromnode test tonode test]");
		l=Link.parse(g,nodes);
		System.out.println(g+" = "+l);
		g=GmlPair.parse("Link [fromnode test tonode test2]");
		l=Link.parse(g,nodes);
		System.out.println(g+" = "+l);
		g=GmlPair.parse("Link [fromnode test tonode test2 note test]");
		l=Link.parse(g,nodes);
		System.out.println(g+" = "+l);
		g=GmlPair.parse("Link [fromnode \"test,test2\" tonode \"test3,test2\" note test]");
		l=Link.parse(g,nodes);
		System.out.println(g+" = "+l);
		} catch (IOException ioe) {
			System.out.println("MyError: ioerror in main gml. "+ioe);
		}
	}
}

