package aj.awt;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.Vector;
/**
 *  ChoiceField is a TextField that has a list of possible vaules. When you 
 *  change the text in the field, the ChoiceField automatically finds the 
 *  closest match of possible choices and updates it self. When the choice field 
 *  is changed it generates an ActionEvent. 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class ChoiceField extends TextField implements TextListener {
	String list[];
	String base[] = {
		"empty!!!"
	};
	String last = "";
	Vector aeList = new Vector();
	/**
	 *  Constructor for the ChoiceField object 
	 */
	public ChoiceField() {
		list = base;
		addTextListener (this);
		setText ((String)list[0]);
	}
	/**
	 *  Constructor for the ChoiceField object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public ChoiceField (Vector s) {
		list = new String[s.size()];
		int a;
		for (a = 0; a < s.size(); a++) {
			list[a] = (String)s.elementAt (a);
		}
		if (list.length == 0) {
			list = base;
		}
		addTextListener (this);
		setText ((String)list[0]);
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public void set (Vector s) {
		String l[] = new String[s.size()];
		int a;
		for (a = 0; a < s.size(); a++) {
			l[a] = (String)s.elementAt (a);
		}
		if (l.length == 0) {
			l = base;
		}
		list = l;
		setText ((String)list[0]);
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  TE  Description of Parameter 
	 */
	public void textValueChanged (TextEvent TE) {
		int CP = getCaretPosition();
		String lookup;
		String best;
		if (CP == 0) {
			lookup = getText();
		}
		else {
			lookup = getText().substring (0, CP);
		}
		best = closest (lookup);
		if (best.equals (last) && best.equals (getText())) {
			return;
		}
		else {
			setText (best);
			last = best;
			setCaretPosition (CP);
			performAction (new ActionEvent (this, 0, getText()));
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String closest (String s) {
		s = s.toUpperCase();
		if (s.length() == 0) {
			return list[0];
		}
		int a;
		int b;
		for (a = 0; a < list.length; a++) {
			if (list[a].toUpperCase().equals (s)) {
				return list[a];
			}
		}
		String best = null;
		a = s.length();
		while (best == null) {
			for (b = 0; b < list.length; b++) {
				if (list[b].toUpperCase().startsWith (s.substring (0, a))) {
					best = list[b];
				}
			}
			a--;
		}
		if (best == null) {
			return list[0];
		}
		return best;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public void performAction (ActionEvent ae) {
		Vector v = (Vector)aeList.clone();
		int a;
		for (a = 0; a < v.size(); a++) {
			ActionListener AL = (ActionListener)aeList.elementAt (a);
			AL.actionPerformed (ae);
		}
	}
	/**
	 *  Adds a feature to the ActionListener attribute of the ChoiceField object 
	 *
	 *@param  AE  The feature to be added to the ActionListener attribute 
	 */
	public void addActionListener (ActionListener AE) {
		if ( !aeList.contains (AE)) {
			aeList.addElement (AE);
		}
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  AE  Description of Parameter 
	 */
	public void removeActionListener (ActionListener AE) {
		aeList.removeElement (AE);
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public void add (String s) {
		if (list == null) {
			list = base;
		}
		if (list.length == 0) {
			list = base;
		}
		String l[];
		if (list == base) {
			l = new String[1];
			l[0] = s;
		}
		else {
			int a;
			l = new String[list.length + 1];
			for (a = 0; a < list.length; a++) {
				l[a] = list[a];
			}
			l[a] = s;
		}
		list = l;
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main (String s[]) {
		String list[] = {
			"aaron", "aboard", "about", "above", "across", "along", "amid", "after", "arrow", "asexual", "another", "antidote", "all", "alot", "any", "about", "again", "abcde", "atlast"
		};
		Vector v = new Vector();
		int a;
		for (a = 0; a < list.length; a++) {
			v.addElement (list[a]);
		}
		Frame f = new Frame();
		f.setLayout (new BorderLayout());
		f.add ("North", new ChoiceField (v));
		f.pack();
		f.setVisible (true);
		f.addWindowListener (new SimpleWindowManager());
	}
}

