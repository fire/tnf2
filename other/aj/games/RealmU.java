package aj.games;

import java.net.Socket;


public class RealmU {
	String name;
	int x, y;
	char dir;
	int status;// 0=dead,1=alive,
	long lastMoveTime;
	int turnNumber;
	Socket nl;
	
	public RealmU() {
	}
	
	public RealmU (String namee, int xx, int yy, char dirr, int ss) {
		name = namee;
		x = xx;
		y = yy;
		dir = dirr;
		status = ss;
		if (name.length() > 6)name = name.substring (0, 6);
	}
	
	public String toString() {
		return dir + name + "_" + x + "_" + y + "_" + status;
	}
	
	static public RealmU parse (String s) {
		s = s.trim();
		if (s.startsWith ("USER "))s = s.substring (5).trim();
		String name = "EMPTY";
		int x = 0, y = 0, st = 0;
		char d = '>';
		try {
			if (s.length() > 0) {
				d = s.charAt (0);
				s = s.substring (1);
			}
			if (s.indexOf ("_") >= 0) {
				name = s.substring (0, s.indexOf ("_"));
				s = s.substring (s.indexOf ("_") + 1);
			}
			if (s.indexOf ("_") >= 0) {
				x = Integer.parseInt (s.substring (0, s.indexOf ("_")));
				s = s.substring (s.indexOf ("_") + 1);
			}
			if (s.indexOf ("_") >= 0) {
				y = Integer.parseInt (s.substring (0, s.indexOf ("_")));
				s = s.substring (s.indexOf ("_") + 1);
			}
			if (s.length() >= 0) {
				st = Integer.parseInt (s);
			}
		}
		catch (NumberFormatException nfe) {
			System.out.println ("MyError: cannnot parse RealmU");
			return null;
		}
		return new RealmU (name, x, y, d, st);
	}
	
	public static void main (String s[]) {
		RealmU u = new RealmU ("tom", 5, 5, '>', 1);
		System.out.println ("RealmU=" + u);
		System.out.println ("RealmU toString=" + u.toString());
		String t = u.toString();
		System.out.println ("RealmU reparsed=" + RealmU.parse (t));
		RealmU ruu = RealmU.parse (t);
		if (ruu != null) {
			System.out.println ("RealmU reparsed=" + ruu.toString());
		}
		else {
			System.out.println ("MyError: cannot parse to realmU");
		}
		System.out.println ("name=" + ruu.name);
		System.out.println ("dir=" + ruu.dir);
		System.out.println ("x=" + ruu.x);
		System.out.println ("y=" + ruu.y);
		System.out.println ("status=" + ruu.status);
	}
}

