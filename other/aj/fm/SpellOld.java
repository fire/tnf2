package aj.fm;

import java.util.Vector;

import aj.misc.GmlPair;

public 	class SpellOld {
		String name, gest;

		public SpellOld(String n, String g) {
			name = n;
			gest = g;
		}

		public static SpellOld parse(GmlPair g) {
			String name = g.getOneByName("name").getString();
			String gest = g.getOneByName("gesture").getString();
			return new SpellOld(name, gest);
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
				SpellOld sp = (SpellOld) allSpells.elementAt(a);
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
				SpellOld sp = (SpellOld) allSpells.elementAt(a);
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
				SpellOld sp = (SpellOld) allSpells.elementAt(a);
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
					SpellOld t = (SpellOld) vv.elementAt(b);
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
