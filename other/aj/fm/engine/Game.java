package aj.fm.engine;

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
		return (Vector)wizardVector.clone();
	}

}
