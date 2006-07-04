/*
 * Created on Dec 10, 2005
 *
 */
package aj.gems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

/**
 * @author judda
 * 
 */
public class Score {
	String user;

	long date;

	String gameType;

	int score;

	int time;

	int colors;

	int cells;

	String other;

	static Vector all = null;// new Vector();

	static File file = new File(Gems.scoreFile);

	public static Vector getColNames() {
		Vector v = new Vector();
		v.add("Name");
		// v.add("gameType");
		v.add("date");
		v.add("score");
		v.add("time");
		v.add("colors");
		v.add("cells");
		// v.add("other");
		return v;
	}

	private static Vector sort(Vector v, int index) {
		for (int a = 0; a < v.size(); a++) {
			Vector t1 = (Vector) v.elementAt(a);
			for (int b = a + 1; b < v.size(); b++) {
				Vector t2 = (Vector) v.elementAt(b);
				if (t2.elementAt(index) instanceof Integer) {
					Integer i2 = (Integer) t2.elementAt(index);
					Integer i1 = (Integer) t1.elementAt(index);
					if (i1.intValue() < i2.intValue()) {
						swapVectors(t1, t2);
						a--;
						break;
					}
				} else if (t2.elementAt(index) instanceof Double) {
					Double i2 = (Double) t2.elementAt(index);
					Double i1 = (Double) t1.elementAt(index);
					if (i1.doubleValue() < i2.doubleValue()) {
						swapVectors(t1, t2);
						a--;
						break;
					}
				} else if (t2.elementAt(index) instanceof Date) {
					Date i2 = (Date) t2.elementAt(index);
					Date i1 = (Date) t1.elementAt(index);
					if (i1.before(i2)) {
						swapVectors(t1, t2);
						a--;
						break;
					}
				}
			}
		}
		return v;
	}

	private static void swapVectors(Vector t1, Vector t2) {
		Vector tt = new Vector();
		tt.addAll(t1);
		t1.clear();
		t1.addAll(t2);
		t2.clear();
		t2.addAll(tt);
		tt.clear();
	}

	public static String getRank(String gameName, String name, int score) {
		String res = "";
		Vector v = getAllData(gameName);
		int found = v.size();
		for (int a = 0; a < v.size(); a++) {
			Vector s = (Vector) v.elementAt(a);
			Integer i = (Integer) s.elementAt(2);
			found = a;
			if (i.intValue() > score)
				continue;
			break;
		}
		found += 1;
		String place = ((found - 1 % 10) == 0 ? "st" : "")
				+ ((found - 2 % 10) == 0 ? "nd" : "")
				+ ((found - 3 % 10) == 0 ? "rd" : "");
		if (place.equals(""))
			place = "th";
		res += found + place + " ";
		v = getUserData(gameName, name);
		found = v.size();
		for (int a = 0; a < v.size(); a++) {
			Vector s = (Vector) v.elementAt(a);
			Integer i = (Integer) s.elementAt(2);
			found = a;
			if (i.intValue() > score)
				continue;
			break;
		}
		found += 1;
		place = ((found - 1 % 10) == 0 ? "st" : "")
				+ ((found - 2 % 10) == 0 ? "nd" : "")
				+ ((found - 3 % 10) == 0 ? "rd" : "");
		if (place.equals(""))
			place = "th";
		res += found + place;
		return res;
	}

	public static Vector getUserData(String gametype, String name) {
		Vector v = new Vector();
		for (int a = 0; a < all.size(); a++) {
			Vector m = new Vector();
			Score s = (Score) all.elementAt(a);
			m.add(s.user);
			// m.add(s.gameType);
			m.add(new Date(s.date));
			m.add(new Integer(s.score));
			m.add(new Double(s.time / 1000.0));
			m.add(new Integer(s.colors));
			m.add(new Integer(s.cells));
			// m.add(s.other);
			if (s.user.equalsIgnoreCase(name) && s.gameType.equals(gametype))
				v.add(m);
		}
		v = sort(v, 2);
		return v;
	}

	public static Vector getAllData(String gametype) {
		Vector v = new Vector();
		for (int a = 0; a < all.size(); a++) {
			Vector m = new Vector();
			Score s = (Score) all.elementAt(a);
			m.add(s.user);
			// m.add(s.gameType);
			m.add(new Date(s.date));
			m.add(new Integer(s.score));
			m.add(new Double(s.time / 1000.0));
			m.add(new Integer(s.colors));
			m.add(new Integer(s.cells));
			// m.add(s.other);
			if (s.gameType.equals(gametype))
				v.add(m);
		}
		v = sort(v, 2);
		return v;
	}

	// public static void main(String s[]) {
	// Score.readFile();
	// Score.show();
	// Score.saveFile();
	//		
	// }

	public static Vector getKnownUsers() {
		if (all == null)
			readFile();
		Vector v = new Vector();
		for (int a = 0; a < all.size(); a++) {
			Score s = (Score) all.elementAt(a);
			if (!v.contains(s.user))
				v.addElement(s.user);
		}
		return v;
	}

	public Score() {
	}

	public static void create(String user, long date, String gameType,
			int score, int time, int colors, int cells, String other) {
		if (all == null)
			readFile();
		Score s = new Score();
		s.user = user;
		s.date = date;
		s.gameType = gameType;
		s.score = score;
		s.time = time;
		s.colors = colors;
		s.cells = cells;
		s.other = other;
		all.addElement(s);
	}

	static void show() {
		if (all == null)
			readFile();
		System.out.println("showing all " + all.size() + " scores");
		for (int a = 0; a < all.size(); a++) {
			Score s = (Score) all.elementAt(a);
			System.out.println(s.toString());
		}
	}

	public String toString() {
		return saveScoreString();
	}

	public static void saveFile() {
		if (all == null)
			readFile();
		if (all.size() == 0)
			return;
		String res = "";
		for (int a = 0; a < all.size(); a++) {
			res += ((Score) all.elementAt(a)).saveScoreString() + "\n";
		}
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(file);
			fo.write(res.getBytes());
			fo.flush();
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("file saved");
	}

	public static void readFile() {
		all = new Vector();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				Score S = Score.readScore(s);
				all.addElement(S);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Score readScore(String s) {
		Score S = new Score();
		S.user = parseToken(s, "user=");
		S.other = parseToken(s, "other=");
		S.date = (long) Double.parseDouble(parseToken(s, "date="));
		S.gameType = parseToken(s, "game=");
		S.score = (int) Double.parseDouble(parseToken(s, "score="));
		S.time = (int) Double.parseDouble(parseToken(s, "time="));
		S.colors = (int) Double.parseDouble(parseToken(s, "colors="));
		S.cells = (int) Double.parseDouble(parseToken(s, "cells="));
		return S;
	}

	private static String parseToken(String temp, String token) {
		if (temp.indexOf(token) >= 0) {
			temp = temp.substring(temp.indexOf(token) + token.length());
			if (temp.indexOf(" ") >= 0)
				temp = temp.substring(0, temp.indexOf(" "));
		}
		return temp;
	}

	public String saveScoreString() {
		String res = "";
		res += "user=" + user + " ";
		res += "date=" + date + " ";
		res += "game=" + gameType + " ";
		res += "score=" + score + " ";
		res += "time=" + time + " ";
		res += "colors=" + colors + " ";
		res += "cells=" + cells + " ";
		res += "other=" + other + " ";
		return res;
	}
}
