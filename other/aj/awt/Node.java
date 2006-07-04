package aj.awt;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Vector;

import aj.misc.GmlPair;

/**
 * @author judda
 * @created July 21, 2000
 */
public class Node implements DisplayItem {
	String name;

	int id;

	double x, y;

	boolean ready = false;

	Vector all = new Vector();

	static int count = 1;

	public Node(String n, int i) {
		count = Math.max(i + 1, count + 1);
		name = n;
		id = i;
		if (name == null || name.trim().equals("")) {
			name = id + "";
		}
		x = (int) (Math.random() * 400);
		y = (int) (Math.random() * 400);
	}

	public Node(String n) {
		count = count + 1;
		name = n;
		id = count;
		if (name == null || name.trim().equals("")) {
			name = id + "";
		}
		x = (int) (Math.random() * 400);
		y = (int) (Math.random() * 400);
	}

	public void setX(double d, double s) {
		x = d / s;
	}

	public void setY(double d, double s) {
		y = d / s;
	}

	public void setX(double d) {
		x = d;
	}

	public void setY(double d) {
		y = d;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	GmlPair color;

	Color cc;

	public void setColor(GmlPair g) {
		if (g == null) {
			cc = Color.black;
			color = new GmlPair("color", "black");
			return;
		}
		color = g;
		if (g.isString() && g.getString().equalsIgnoreCase("RED"))
			cc = Color.red;
		else if (g.isString() && g.getString().equalsIgnoreCase("BLUE"))
			cc = Color.blue;
		else if (g.isString() && g.getString().equalsIgnoreCase("Green"))
			cc = Color.green;
		else if (g.isString() && g.getString().equalsIgnoreCase("Yellow"))
			cc = Color.yellow;
		else if (g.isString() && g.getString().equalsIgnoreCase("Cyan"))
			cc = Color.cyan;
		else if (g.isString() && g.getString().equalsIgnoreCase("Magenta"))
			cc = Color.magenta;
		else if (g.isString() && g.getString().equalsIgnoreCase("gray"))
			cc = Color.gray;
		else if (g.isString() && g.getString().equalsIgnoreCase("lightgray"))
			cc = Color.lightGray;
		else if (g.isString() && g.getString().equalsIgnoreCase("Darkgray"))
			cc = Color.darkGray;
		else if (g.isList()) {
			GmlPair r = g.getOneByName("Red");
			GmlPair gg = g.getOneByName("Green");
			GmlPair b = g.getOneByName("Blue");
			if (r == null || gg == null || b == null) {
				cc = Color.black;
				color = new GmlPair("color", "black");
			} else {
				cc = new Color((int) (r.getDouble()), (int) (gg.getDouble()),
						(int) (b.getDouble()));
			}
		} else {
			cc = Color.black;
			color = new GmlPair("color", "black");
		}

	}

	public double getX(double s) {
		return x * s;
	}

	public double getY(double s) {
		return y * s;
	}

	public String toString() {
		return toGmlPair().toString();
	}

	public GmlPair toGmlPair() {
		Vector v = new Vector();
		v.addElement(new GmlPair("name", name));
		v.addElement(new GmlPair("id", id + ""));
		v.addElement(new GmlPair("x", x + ""));
		v.addElement(new GmlPair("y", y + ""));
		if (color.isList() || !color.getString().equalsIgnoreCase("Black"))
			v.addElement(color);
		for (int a = 0; a < all.size(); a++)
			v.addElement(all.elementAt(a));
		return new GmlPair("node", v);
	}

	public void display(Graphics g, double s) {
		g.setColor(cc);
		g.drawOval((int) getX(s) - 5, (int) getY(s) - 5, 10, 10);
		g.drawString(name, (int) getX(s) + 5, (int) getY(s));
	}

	public static Node parse(GmlPair g) {
		if (!g.getName().equalsIgnoreCase("NODE"))
			return null;
		String na = null;
		int id = -1;
		GmlPair n = g.getOneByName("NAME");
		if (n != null)
			na = n.getString();
		n = g.getOneByName("ID");
		if (n != null)
			id = (int) n.getDouble();
		Node node = null;
		if (na == null)
			return null;
		if (id == -1)
			node = new Node(na);
		else
			node = new Node(na, id);
		n = g.getOneByName("X");
		if (n != null)
			node.setX(n.getDouble());
		n = g.getOneByName("Y");
		if (n != null)
			node.setY(n.getDouble());
		n = g.getOneByName("color");
		if (n != null)
			node.setColor(n);
		if (n == null)
			node.setColor(null);
		Vector all = g.getListVector();
		all.removeElement(g.getOneByName("NAME"));
		all.removeElement(g.getOneByName("ID"));
		all.removeElement(g.getOneByName("X"));
		all.removeElement(g.getOneByName("Y"));
		all.removeElement(g.getOneByName("color"));
		node.all = all;
		return node;
	}

	public static void main(String s[]) {
		try {
			GmlPair ng = GmlPair.parse("Node []");
			Node n = Node.parse(ng);
			System.out.println(ng + " = " + n);
			ng = GmlPair.parse("Node [name test]");
			n = Node.parse(ng);
			System.out.println(ng + " = " + n);
			ng = GmlPair.parse("Node [name test id 75]");
			n = Node.parse(ng);
			System.out.println(ng + " = " + n);
			ng = GmlPair.parse("Node [name test id 75 x 5 y 6]");
			n = Node.parse(ng);
			System.out.println(ng + " = " + n);
			ng = GmlPair.parse("Node [name test id 75 x 5 y 6 note test]");
			n = Node.parse(ng);
			System.out.println(ng + " = " + n);
		} catch (IOException ioe) {
			System.out.println("MyError: io in parse gml" + ioe);
		}
	}
}
