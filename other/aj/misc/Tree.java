package aj.misc;

import java.util.Vector;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Tree {
	Object root;

	Vector children;

	/**
	 * Constructor for the Tree object
	 * 
	 * @param r
	 *            Description of Parameter
	 */
	public Tree(Object r) {
		root = r;
		children = new Vector();
	}

	/**
	 * Gets the Width attribute of the Tree object
	 * 
	 * @return The Width value
	 */
	public int getWidth() {
		int wid = 1;
		wid = Math.max(wid, children.size());
		for (int a = 0; a < children.size(); a++) {
			Tree t = (Tree) children.elementAt(a);
			if (t.getWidth() > 1) {
				wid += t.getWidth() - 1;
			}
		}
		return wid;
	}

	/**
	 * Gets the Depth attribute of the Tree object
	 * 
	 * @return The Depth value
	 */
	public int getDepth() {
		int h = 1;
		int mx = 0;
		for (int a = 0; a < children.size(); a++) {
			Tree t = (Tree) children.elementAt(a);
			mx = Math.max(mx, t.getDepth());
		}
		h += mx;
		return h;
	}

	/**
	 * Gets the Size attribute of the Tree object
	 * 
	 * @return The Size value
	 */
	public int getSize() {
		int s = 1;
		int sx = 0;
		for (int a = 0; a < children.size(); a++) {
			Tree t = (Tree) children.elementAt(a);
			sx = sx + t.getSize();
		}
		s += sx;
		return s;
	}

	/**
	 * Gets the Root attribute of the Tree object
	 * 
	 * @return The Root value
	 */
	public Object getRoot() {
		return root;
	}

	/**
	 * Gets the Children attribute of the Tree object
	 * 
	 * @return The Children value
	 */
	public Vector getChildren() {
		return (Vector) children.clone();
	}

	/**
	 * Adds a feature to the Child attribute of the Tree object
	 * 
	 * @param o
	 *            The feature to be added to the Child attribute
	 */
	public void addChild(Object o) {
		children.addElement(new Tree(o));
	}

	/**
	 * Adds a feature to the Tree attribute of the Tree object
	 * 
	 * @param t
	 *            The feature to be added to the Tree attribute
	 */
	public void addTree(Tree t) {
		children.addElement(t);
	}

	/**
	 * Description of the Method
	 * 
	 * @param o
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean contains(Object o) {
		if (o.equals(root)) {
			return true;
		}
		for (int a = 0; a < children.size(); a++) {
			Tree t = (Tree) children.elementAt(a);
			if (t.contains(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a feature to the ChildOf attribute of the Tree object
	 * 
	 * @param o
	 *            The feature to be added to the ChildOf attribute
	 * @param n
	 *            The feature to be added to the ChildOf attribute
	 * @return Description of the Returned Value
	 */
	public boolean addChildOf(Object o, Object n) {
		if (root.equals(o)) {
			addChild(n);
			return true;
		} else {
			for (int a = 0; a < children.size(); a++) {
				Tree t = (Tree) children.elementAt(a);
				if (t.addChildOf(o, n)) {
					return true;
				}
			}
			return false;
		}
	}

	// first remove tree if it is child
	// next remove first occurance in children
	/**
	 * Description of the Method
	 * 
	 * @param tt
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean removeTree(Tree tt) {
		for (int a = 0; a < children.size(); a++) {
			Tree t = (Tree) children.elementAt(a);
			if (t == tt) {
				children.removeElementAt(a);
				return true;
			}
		}
		for (int a = 0; a < children.size(); a++) {
			Tree t = (Tree) children.elementAt(a);
			if (t.removeTree(tt)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Description of the Method
	 * 
	 * @param SS
	 *            Description of Parameter
	 */
	public static void main(String SS[]) {
		Tree t = new Tree("hello");
		t.addChild("there");
		t.addChild("is");
		t.addChild("next");
		t.addChildOf("there", "bot");
		Tree s = new Tree("testing");
		s.addChild("1");
		s.addChild("2");
		s.addChild("3");
		t.addTree(s);
		t.addChildOf("3", "deep!");
		System.out.println("wid = " + t.getWidth());
		System.out.println("size = " + t.getSize());
		System.out.println("depth = " + t.getDepth());
	}
}
