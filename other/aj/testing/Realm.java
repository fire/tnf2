package aj.testing;

import java.util.Vector;

public class Realm {

	// screen control chars
	public static char esc = (char) 27;

	public static String clr = esc + "[H" + esc + "[J";// clear home

	public static String gotoRowCol(int r, int c) {
		return esc + "[" + r + ";" + c + "H";
	}

	int SIZE = 40;

	char map[][] = new char[SIZE][SIZE];

	public static void main(String s[]) {
		System.out.println(clr);
		System.out.println(gotoRowCol(5, 5) + "Hello");
		System.out.println(gotoRowCol(8, 15) + "There");
	}

	Vector users = new Vector();

	public void run() {
		// for (int a=0;a<SIZE*SIZE;a++)
		// map[a/SIZE][a%SIZE]=(char)Math.random()*
		// read command
		// write command
		// update display
	}

	int WID = 3;

	void updateDisplay(int x, int y) {
		System.out.print(gotoRowCol(0, 0));
		for (int a = -WID; a < WID + 1; a++) {
			for (int b = -WID; b < WID + 1; b++) {
				char disp = ' ';
				if ((a == WID || a == -WID) && (b == WID || b == -WID))
					disp = '+';
				else if (a == -WID || a == WID)
					disp = '|';
				else if (b == -WID || b == WID)
					disp = '-';
				else if (y + a >= 0 && y + a < SIZE && x + b >= 0
						&& x + b < SIZE)
					disp = map[a][b];
				System.out.print(disp);
			}
			System.out.println();
		}
	}
}

class User {
	String name, killer;

	int x, y, dir;
}
