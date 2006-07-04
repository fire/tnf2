package aj.fm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import aj.misc.GmlPair;

public class Tool2 implements ActionListener {
	static JFrame F = new JFrame();

	static Vector allSpells = new Vector();

	static Vector allMages = new Vector();

	JTextArea fullTurn = new JTextArea(20, 20);

	JButton update = new JButton("Update");

	static String turn = "none";

	// JDialog turnInput=new JDialog();

	public static void main(String s[]) {
		new Tool2();
	}

	public Tool2() {
		readSpells();
		// readReport(readInputStream(System.in));
		guiSetup();
	}

	public void actionPerformed(ActionEvent ae) {
		// System.out.println("do update");
		if (ae.getSource() == update) {
			readReport(fullTurn.getText());
			F.getContentPane().removeAll();
			F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			guiSetup();
		}
		System.out.println("done");
	}

	public void guiSetup() {
		JTabbedPane jp = new JTabbedPane();
		JPanel ft = new JPanel(new BorderLayout());
		ft.add("Center", new JScrollPane(fullTurn));
		update.addActionListener(this);
		ft.add("South", update);
		jp.add("Full Turn", new JScrollPane(ft));
		for (int a = 0; a < allMages.size(); a++) {
			Mage m = (Mage) allMages.elementAt(a);
			jp.add(m.getTitle(), m.getGUI());
		}
		F.getContentPane().add(jp);
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

	public void readReport(String all) {
		// Standard Duel Status for Peltor (game 3723, turn 7):
		fullTurn.setText(all);
		String rawHeader = "Duel Status for";
		String activeName = "";
		if (all.indexOf(rawHeader) >= 0) {
			all = all.substring(all.indexOf(rawHeader) + rawHeader.length());
			String head = "(game ", tail = "):";
			if (all.indexOf(head) >= 0
					&& all.indexOf(tail) >= all.indexOf(head)) {
				activeName = all.substring(0, all.indexOf(head)).trim();
				String title = all.substring(all.indexOf(head) + 1);
				all = all.substring(all.indexOf(tail) + tail.length());
				title = title.substring(0, title.indexOf(tail));
				F.setTitle(title);
				if (title.indexOf("turn ") >= 0) {
					turn = title.substring(title.indexOf("turn ") + 5).trim();
					if (turn.indexOf(")") >= 0)
						turn = turn.substring(turn.indexOf(")")).trim();
				}
			}
		}

		allMages = new Vector();
		while (true) {
			all = all.trim();
			String right = "Right: ";
			if (all.indexOf(right) >= 0) {
				String h = all
						.substring(0, all.indexOf(right) + right.length());
				all = all.substring(all.indexOf(right) + right.length());
				if (all.indexOf("\n") >= 0) {
					h = h + all.substring(0, all.indexOf("\n"));
					all = all.substring(all.indexOf("\n")).trim();
				}
				Mage m = new Mage(h);
				m.setActiveIfName(activeName);
				allMages.addElement(m);
			} else
				break;
		}
	}

	public void readSpells() {
		try {

			GmlPair all = GmlPair.parse(this.getClass().getResource(
					"spells.gml"));
			// GmlPair all=GmlPair.parse(new File("spells.gml"));
			GmlPair gmlSpells[] = all.getAllByName("node");
			for (int a = 0; a < gmlSpells.length; a++) {
				Spell2 sp = Spell2.parse(gmlSpells[a]);
				if (sp.name.startsWith("descision"))
					continue;
				allSpells.addElement(sp);
			}
		} catch (IOException ioe) {
			System.out.println("IO problem." + ioe);
			System.exit(0);
		}
	}

	public static String getSpellDiscription(Spell2 s) {
		return s.getDiscription();
	}

	public static Spell2 getSpellBySummary(String val) {
		if (val == null) {
			return null;
		}
		for (int a = 0; a < allSpells.size(); a++) {
			Spell2 sp = (Spell2) allSpells.elementAt(a);
			if (val.indexOf(sp.name) >= 0 && val.indexOf(sp.gest) >= 0)
				return sp;
		}
		return null;
	}

	public static String getCastingDetails(String hand, Spell2 sp) {
		String res = "";
		res += sp.summaryString(hand) + "\n";
		Vector l = sp.includes(hand, sp.gest);
		for (int c = 0; c < l.size(); c++) {
			res += "\tinc=" + ((Spell2) l.elementAt(c)).summaryString(hand)
					+ "\n";
		}
		l = sp.choice(hand);
		for (int c = 0; c < l.size(); c++) {
			Spell2 sp2 = (Spell2) l.elementAt(c);
			if (sp2 == sp)
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

	public static Vector getOptionsStringFor(String hand) {
		Vector v = getOptionsFor(hand);
		Vector r = new Vector();
		for (int a = 0; a < v.size(); a++) {
			Spell2 sp = (Spell2) v.elementAt(a);
			r.insertElementAt(sp.summaryString(hand), r.size());
		}
		return r;
	}

	public static Vector getOptionsFor(String hand) {
		System.out.println("\n\n\nspell options from " + hand);

		Vector res = new Vector();
		// Vector tmp=new Vector();
		for (int b = 1; b < 10; b++) {
			for (int a = 0; a < allSpells.size(); a++) {
				Spell2 sp = (Spell2) allSpells.elementAt(a);
				if (sp.stepsToCast(hand, b) && b < sp.getGest().length()) {
					res.addElement(sp);
					// tmp.addElement(sp);
				} else {
				}
			}
		}
		// tmp=new Vector();
		for (int b = 1; b < 10; b++) {
			for (int a = 0; a < allSpells.size(); a++) {
				Spell2 sp = (Spell2) allSpells.elementAt(a);
				// if ((sp.getStepsFrom(hand)==b || sp.getGest().length()==b )&&
				// !tmp.contains(sp)) {
				if (sp.getGest().length() == b) {
					res.addElement(sp);
					// tmp.addElement(sp);
				}
			}
		}
		return res;
	}
}
