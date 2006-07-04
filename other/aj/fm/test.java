package aj.fm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumn;

public class test implements ActionListener {
	static JFrame F = new JFrame();

	JTextArea fullTurn = new JTextArea(20, 20);

	static String turn = "none";

	// JDialog turnInput=new JDialog();

	public static void main(String s[]) {
		new test();
	}

	public test() {
		new Tool2();
		guiSetup();
		Tool2.F.setVisible(false);
	}

	public void actionPerformed(ActionEvent ae) {
	}

	public void guiSetup() {
		JPanel ft = new JPanel(new BorderLayout());
		Vector headerVector = new Vector();
		headerVector.addElement("name");
		headerVector.addElement("next");
		headerVector.addElement("steps");
		headerVector.addElement("inc");
		headerVector.addElement("head");
		headerVector.addElement("totalsteps");
		String curr = "WFDS";
		Vector opt = getOptionsFor(curr);
		Vector cellVector = new Vector();
		for (int a = 0; a < opt.size(); a++) {
			Spell2 sp = (Spell2) opt.elementAt(a);
			Vector m = new Vector();
			m.addElement(sp.getCleanName());
			m.addElement("" + sp.getRemainingGest(curr));
			m.addElement(new Integer(sp.getStepsFrom(curr)));
			String inc = "";
			Vector mmm = sp.includes(curr, sp.getRemainingGest(curr));
			for (int b = 0; b < mmm.size(); b++) {
				Spell2 sppp = (Spell2) mmm.elementAt(b);
				inc += sppp.getCleanName() + " " + sppp.gest + "\n";
			}
			// m.addElement(""+sp.includes(curr).size());
			m.addElement(inc);
			m.addElement("" + sp.headStart(curr).size());
			m.addElement(new Integer(sp.gest.length()));
			cellVector.addElement(m);
		}
		JTable spellTable = new JTable(cellVector, headerVector);

		TableColumn column = null;
		for (int i = 0; i < 6; i++) {
			column = spellTable.getColumnModel().getColumn(i);
			if (i == 1) {
				column.setPreferredWidth(10);
			} else if (i == 2) {
				column.setPreferredWidth(10);
			} else if (i == 3) {
				column.setPreferredWidth(10);
			} else {
				column.setPreferredWidth(50);
			}
		}

		ft.add("Center", new JScrollPane(spellTable));
		F.getContentPane().add(ft);
		F.pack();
		F.setVisible(true);
	}

	public String readInputStream(InputStream i) {
		String all = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(i));
			while (true) {
				String t = br.readLine();
				if (t == null)
					break;
				all += t + "\n";
			}
		} catch (IOException ieo) {
			System.out.println("MyError: reading mages io error" + ieo);
		}
		return all;
	}

	public static String getSpellDiscription(Spell2 s) {
		return s.getDiscription();
	}

	public static Spell2 getSpellBySummary(String val) {
		if (val == null) {
			return null;
		}
		for (int a = 0; a < Tool2.allSpells.size(); a++) {
			Spell2 sp = (Spell2) Tool2.allSpells.elementAt(a);
			if (val.indexOf(sp.name) >= 0 && val.indexOf(sp.gest) >= 0)
				return sp;
		}
		return null;
	}

	public static String getCastingDetails(String hand, Spell2 s) {
		String res = "";
		Spell2 sp = s;
		res += sp.summaryString(hand) + "\n";
		Vector l = sp.includes(hand, sp.getRemainingGest(hand));
		for (int c = 0; c < l.size(); c++) {
			res += "\tinc=" + ((Spell2) l.elementAt(c)).summaryString(hand)
					+ "\n";
		}
		l = sp.choice(hand);
		for (int c = 0; c < l.size(); c++) {
			Spell2 sp2 = (Spell2) l.elementAt(c);
			if (sp2 == s)
				continue;
			res += "\tchoice=" + sp2.summaryString(hand) + "\n";
		}
		l = sp.headStart(hand);
		for (int d = 1; d < 10; d++) {
			for (int c = 0; c < l.size(); c++) {
				Spell2 hs = (Spell2) l.elementAt(c);
				if (hs.getStepsFrom(hand + sp.gest) == d) {
					res += "\ths="
							+ ((Spell2) l.elementAt(c)).summaryString(sp.gest)
							+ "\n";
				}
			}
		}
		return res;
	}

	public static Vector getOptionsFor(String hand) {
		Vector temp = new Vector();
		Vector res = new Vector();
		for (int b = 1; b < 10; b++) {
			for (int a = 0; a < Tool2.allSpells.size(); a++) {
				Spell2 sp = (Spell2) Tool2.allSpells.elementAt(a);
				if (sp.getStepsFrom(hand) == b && b != sp.gest.length()
						&& !temp.contains(sp)) {
					res.addElement(sp);
					temp.addElement(sp);
				}
			}
		}
		for (int b = 1; b < 10; b++) {
			for (int a = 0; a < Tool2.allSpells.size(); a++) {
				Spell2 sp = (Spell2) Tool2.allSpells.elementAt(a);
				if (sp.getStepsFrom(hand) == b && !temp.contains(sp)) {
					res.addElement(sp);
					temp.addElement(sp);
				}
			}
		}
		return res;
	}
}
