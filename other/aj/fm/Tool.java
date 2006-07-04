package aj.fm;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import aj.misc.GmlPair;

public class Tool implements ActionListener {
	TextField RHCurr = new TextField(10);

	TextField LHCurr = new TextField(10);

	TextArea RHOptions = new TextArea(8, 25);

	TextArea LHOptions = new TextArea(8, 25);

	Vector allSpells = new Vector();

	public static void main(String s[]) {
		new Tool();
	}

	public Tool() {
		Frame F = new Frame();
		Panel p = new Panel(new GridLayout());
		Panel pp = new Panel(new BorderLayout());
		RHCurr.addActionListener(this);
		LHCurr.addActionListener(this);
		pp.add("North", RHCurr);
		pp.add("Center", RHOptions);
		p.add(pp);
		pp = new Panel(new BorderLayout());
		pp.add("North", LHCurr);
		pp.add("Center", LHOptions);
		p.add(pp);
		F.add("Center", p);
		readSpells();
		F.pack();
		F.setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		RHOptions.setText(getOptionsFor(RHCurr.getText()));
		LHOptions.setText(getOptionsFor(LHCurr.getText()));
	}

	public String getOptionsFor(String hand) {

		String res = "";
		for (int b = 1; b < 10; b++) {
			// System.out.println("check all "+allSpells.size()+" spells");
			for (int a = 0; a < allSpells.size(); a++) {
				Spell sp = (Spell) allSpells.elementAt(a);
				if (sp.getStepsFrom(hand) == b) {
					res += sp.summaryString(hand, allSpells) + "\n";
					Vector l = sp.includes(hand, allSpells);
					for (int c = 0; c < l.size(); c++) {
						res += "\tinc="
								+ ((Spell) l.elementAt(c)).summaryString(hand,
										allSpells) + "\n";
					}
					l = sp.choice(hand, allSpells);
					for (int c = 0; c < l.size(); c++) {
						res += "\tchoice="
								+ ((Spell) l.elementAt(c)).summaryString(hand,
										allSpells) + "\n";
					}
					l = sp.headStart(hand, allSpells);
					for (int d = 0; d < 10; d++) {
						for (int c = 0; c < l.size(); c++) {
							Spell hs = (Spell) l.elementAt(c);
							if (hs.getStepsFrom(hand + sp.gest) == d) {
								res += "\ths="
										+ ((Spell) l.elementAt(c))
												.summaryString(sp.gest,
														allSpells) + "\n";
							}
						}
					}
				}
			}
		}
		return res;
	}

	public void readSpells() {
		try {
			GmlPair all = GmlPair.parse(new File("spells.gml"));
			GmlPair gmlSpells[] = all.getAllByName("node");
			for (int a = 0; a < gmlSpells.length; a++) {
				Spell sp = Spell.parse(gmlSpells[a]);
				if (sp.name.startsWith("descision"))
					continue;
				allSpells.addElement(sp);
			}
		} catch (IOException ioe) {
			System.out.println("IO problem." + ioe);
			System.exit(0);
		}
	}
}

class Spell {
	String name, gest;

	public Spell(String n, String g) {
		name = n;
		gest = g;
	}

	public static Spell parse(GmlPair g) {
		String name = g.getOneByName("name").getString();
		String gest = g.getOneByName("gesture").getString();
		return new Spell(name, gest);
	}

	// caps check?
	public int getStepsFrom(String curr) {
		for (int a = 0; a < gest.length(); a++) {
			String t = curr + gest.substring(gest.length() - a);
			if (t.endsWith(gest))
				return a;
		}
		return gest.length();
	}

	// caps check?
	public Vector headStart(String curr, Vector allSpells) {
		Vector res = new Vector();
		for (int a = 0; a < allSpells.size(); a++) {
			Spell sp = (Spell) allSpells.elementAt(a);
			if (sp == this)
				continue;
			if (sp.getStepsFrom(curr + gest) < sp.gest.length()
					&& sp.getStepsFrom(curr + gest) != 0 && !res.contains(sp)) {
				res.addElement(sp);
			}
		}
		return res;
	}

	// caps check?
	public Vector choice(String curr, Vector allSpells) {
		Vector res = new Vector();
		for (int a = 0; a < allSpells.size(); a++) {
			Spell sp = (Spell) allSpells.elementAt(a);
			String t = curr + gest;
			if (t.lastIndexOf(sp.gest) > curr.length() && t.endsWith(sp.gest)
					&& !res.contains(sp)) {
				res.addElement(sp);
			}
		}
		return res;
	}

	public Vector includes(String curr, Vector allSpells) {
		Vector res = new Vector();
		String t = curr + gest;
		for (int a = 0; a < allSpells.size(); a++) {
			Spell sp = (Spell) allSpells.elementAt(a);
			if (t.lastIndexOf(sp.gest) > curr.length() && !t.endsWith(sp.gest)
					&& !res.contains(sp)) {
				res.addElement(sp);
			}
		}
		return res;
	}

	public String toString() {
		return name + " " + gest;
	}

	public String summaryString(String curr, Vector allSpells) {
		String res = getStepsFrom(curr) + "," + getStepsFrom("") + ":"
				+ toString() + " i=" + includes(curr, allSpells).size()
				+ " hs=";
		Vector vv = headStart(curr, allSpells);
		for (int a = 1; a < 10; a++) {
			int total = 0;
			for (int b = 0; b < vv.size(); b++) {
				Spell t = (Spell) vv.elementAt(b);
				if (t.getStepsFrom(curr + gest) == a) {
					total++;
					vv.removeElement(t);
				}
			}
			if (vv.size() > 0 && total != 0)
				res += (a != 1 ? "," : "") + total;
		}
		return res;
	}
}
