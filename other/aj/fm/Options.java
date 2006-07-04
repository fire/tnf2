package aj.fm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import aj.misc.GmlPair;

public class Options {
	public static void main(String s[]) {
		String rh = null, lh = null;
		GmlPair allSpells[] = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			rh = br.readLine().toUpperCase();
			lh = br.readLine().toUpperCase();
			GmlPair all = GmlPair.parse(new File("spells.gml"));
			allSpells = all.getAllByName("spell");
		} catch (IOException ioe) {
			System.out.println("IO problem." + ioe);
			System.exit(0);
		}
		// find lowercases
		// all c=lowercase
		// ending "w" to lower
		while (rh.indexOf("C") >= 0) {
			rh = rh.substring(0, rh.indexOf("C")) + "c"
					+ rh.substring(rh.indexOf("C") + 1);
		}
		while (lh.indexOf("C") >= 0) {
			lh = lh.substring(0, lh.indexOf("C")) + "c"
					+ lh.substring(lh.indexOf("C") + 1);
		}
		int MAXSPELLSIZE = 10;
		for (int a = 1; a < MAXSPELLSIZE; a++) {
			for (int b = 0; b < allSpells.length; b++) {
				String spellGest = allSpells[b].getOneByName("gesture")
						.getString();
				if (spellGest.length() < a)
					continue;
				String spellGestEnd = spellGest.substring(spellGest.length()
						- a);
				String spellGestFR = spellGest.substring(0, spellGest
						.lastIndexOf(spellGestEnd));
				if (spellGestFR.length() == 0)
					continue;
				if (rh.endsWith("W") && lh.endsWith("W")
						&& spellGestFR.endsWith("w")) {
					spellGestFR = spellGestFR.substring(0,
							spellGestFR.length() - 1)
							+ "W";
				}
				String nnn = rh + spellGestEnd;
				if (nnn.toUpperCase().endsWith(
						(spellGestFR + spellGestEnd).toUpperCase())) {
					System.out.println("RH) " + rh + "_" + spellGestEnd
							+ " \t "
							+ allSpells[b].getOneByName("name").getString()
							+ "(" + spellGestFR.length() + " of "
							+ spellGest.length() + ")");
				}
				nnn = lh + spellGestEnd;
				if (nnn.toUpperCase().endsWith(
						(spellGestFR + spellGestEnd).toUpperCase())) {
					System.out.println("LH) " + lh + "_" + spellGestEnd
							+ " \t "
							+ allSpells[b].getOneByName("name").getString()
							+ "(" + spellGestFR.length() + " of "
							+ spellGest.length() + ")");
				}
			}
		}
		for (int a = 1; a < MAXSPELLSIZE; a++) {
			for (int b = 0; b < allSpells.length; b++) {
				String spellGest = allSpells[b].getOneByName("gesture")
						.getString();
				if (spellGest.length() < a)
					continue;
				String spellGestEnd = spellGest.substring(spellGest.length()
						- a);
				String spellGestFR = spellGest.substring(0, spellGest
						.lastIndexOf(spellGestEnd));
				if (rh.endsWith("W") && lh.endsWith("W")
						&& spellGestFR.endsWith("w")) {
					spellGestFR = spellGestFR.substring(0,
							spellGestFR.length() - 1)
							+ "W";
				}
				String nnn = rh + spellGestEnd;
				if (spellGestFR.length() != 0)
					continue;
				if (nnn.toUpperCase().endsWith(
						(spellGestFR + spellGestEnd).toUpperCase())) {
					System.out.println("RH) " + rh + "_" + spellGestEnd
							+ " \t "
							+ allSpells[b].getOneByName("name").getString()
							+ "(" + spellGest.length() + ")");
				}
				nnn = lh + spellGestEnd;
				if (nnn.toUpperCase().endsWith(
						(spellGestFR + spellGestEnd).toUpperCase())) {
					System.out.println("LH) " + lh + "_" + spellGestEnd
							+ " \t "
							+ allSpells[b].getOneByName("name").getString()
							+ "(" + spellGest.length() + ")");
				}
			}
		}
	}
}
