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
	private static String spellsFileName;

	TextField RHCurr = new TextField(10);

	TextField LHCurr = new TextField(10);

	TextArea RHOptions = new TextArea(8, 25);

	TextArea LHOptions = new TextArea(8, 25);

	Vector allSpells = new Vector();

	public static void main(String s[]) {
		if (s.length == 0) {
			System.out.println("usage: java - aj.fm.Tool <spells.gml>");
		}
		spellsFileName = s[0];
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
				SpellOld sp = (SpellOld) allSpells.elementAt(a);
				if (sp.getStepsFrom(hand) == b) {
					res += sp.summaryString(hand, allSpells) + "\n";
					Vector l = sp.includes(hand, allSpells);
					for (int c = 0; c < l.size(); c++) {
						res += "\tinc="
								+ ((SpellOld) l.elementAt(c)).summaryString(hand,
										allSpells) + "\n";
					}
					l = sp.choice(hand, allSpells);
					for (int c = 0; c < l.size(); c++) {
						res += "\tchoice="
								+ ((SpellOld) l.elementAt(c)).summaryString(hand,
										allSpells) + "\n";
					}
					l = sp.headStart(hand, allSpells);
					for (int d = 0; d < 10; d++) {
						for (int c = 0; c < l.size(); c++) {
							SpellOld hs = (SpellOld) l.elementAt(c);
							if (hs.getStepsFrom(hand + sp.gest) == d) {
								res += "\ths="
										+ ((SpellOld) l.elementAt(c))
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
			GmlPair all = GmlPair.parse(new File(spellsFileName));
			GmlPair gmlSpells[] = all.getAllByName("node");
			for (int a = 0; a < gmlSpells.length; a++) {
				SpellOld sp = SpellOld.parse(gmlSpells[a]);
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

