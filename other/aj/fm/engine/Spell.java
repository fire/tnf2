package aj.fm.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Spell {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private static String allSpellXml = null;

	private static Vector allSpells = null;

	/**
	 * 
	 * @param spellGesture
	 * @param spellChoiceName
	 * @return
	 */
	public static Spell getSpellFromGesture(String spellGesture,
			String spellChoiceName) {
		if (allSpellXml == null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					Spell.class.getResourceAsStream("spells.xml")));
			StringBuffer all = new StringBuffer();
			while (true) {
				String s;
				try {
					s = br.readLine();
					if (s == null)
						break;
					all.append(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			allSpellXml = all.toString();
			allSpells = null;
		}
		if (allSpells == null) {
			parseAllSpells();
		}

		return null;
	}

	/**
	 * 
	 * 
	 */
	private static void parseAllSpells() {
		allSpells = new Vector();
		while (allSpellXml.indexOf("<spell>") >= 0) {
			break;
		}
		// TODO Auto-generated method stub

	}

}
