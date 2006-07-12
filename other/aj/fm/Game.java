package aj.fm;

import java.util.Vector;

public class Game {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	Vector wizardVector = new Vector();

	Vector monsterVector = new Vector();

	public void addNewWizard(Wizard wiz) {
		wizardVector.addElement(wiz);
	}

	public Vector getAllWizards() {
		return (Vector) wizardVector.clone();
	}

	public Vector getAllMonsters() {
		return (Vector) monsterVector.clone();
	}

	public void advanceTurn() {
		Vector activeSpells = new Vector();
		Vector v = getAllWizards();
		for (int a = 0; a < v.size(); a++) {
			Wizard w = (Wizard) v.elementAt(a);
			w.applyNextGestures();
			if (w.getLeftPattern().endsWith(w.getLeftSpell().getGesture())) {
				System.out.println("cast " + w.getLeftSpell().getName());
				activeSpells.addElement(w.getName() + " cast "
						+ w.getLeftSpell() + " at " + w.getLeftTarget());
			}
			if (w.getRightPattern().endsWith(w.getRightSpell().getGesture())) {
				System.out.println("cast " + w.getRightSpell().getName());
				activeSpells.addElement(w.getName() + " cast "
						+ w.getRightSpell() + " at " + w.getRightTarget());
			}
		}
		resolveActiveSpells(activeSpells);

	}

	private void resolveActiveSpells(Vector activeSpells) {
		// TODO Auto-generated method stub

	}

}
