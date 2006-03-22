package aj.testing;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
class state {
	int comb[][] = {{1, 4, 7, 2, 5, 8, 3, 6, 9}, {3, 2, 1, 6, 5, 4, 9, 8, 7}, 
			{3, 6, 9, 2, 5, 8, 1, 4, 7}, {7, 4, 1, 8, 5, 2, 9, 6, 3}, 
			{7, 8, 9, 4, 5, 6, 1, 2, 3}, {9, 8, 7, 6, 5, 4, 3, 2, 1}, 
			{9, 6, 3, 8, 5, 2, 7, 4, 1},};
	int s[];


	/**
	 *  Constructor for the state object 
	 *
	 *@param  t  Description of Parameter 
	 */
	public state(int t) {
		s = new int[9];
		s[0] = t % 3;
		s[1] = t / 3 % 3;
		s[2] = t / 3 / 3 % 3;
		s[3] = t / 3 / 3 / 3 % 3;
		s[4] = t / 3 / 3 / 3 / 3 % 3;
		s[5] = t / 3 / 3 / 3 / 3 / 3 % 3;
		s[6] = t / 3 / 3 / 3 / 3 / 3 / 3 % 3;
		s[7] = t / 3 / 3 / 3 / 3 / 3 / 3 / 3 % 3;
		s[8] = t / 3 / 3 / 3 / 3 / 3 / 3 / 3 / 3 % 3;
	}


	/**
	 *  Constructor for the state object 
	 *
	 *@param  ss  Description of Parameter 
	 */
	public state(int[] ss) {
		s = ss;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public boolean valid() {
		int numx = 0;
		int numo = 0;
		for (int a = 0; a < 9; a++) {
			if (s[a] == 1) {
				numx++;
			}
			else if (s[a] == 2) {
				numo++;
			}
		}
		String m = winner();
		if (m.equals("Both win")) {
			return false;
		}
		return (numx == numo || numx - 1 == numo);
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public state[] permutate() {
		state per[] = new state[7];
		for (int a=0; a < 7; a++) {
			int ss[] = new int[9];
			ss[0] = s[comb[a][0] - 1];
			ss[1] = s[comb[a][1] - 1];
			ss[2] = s[comb[a][2] - 1];
			ss[3] = s[comb[a][3] - 1];
			ss[4] = s[comb[a][4] - 1];
			ss[5] = s[comb[a][5] - 1];
			ss[6] = s[comb[a][6] - 1];
			ss[7] = s[comb[a][7] - 1];
			ss[8] = s[comb[a][8] - 1];
			per[a] = new state(ss);
		}
		return per;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  t  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean equals(state t) {
		return (toString().equals(t.toString()));
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		int num = 0;
		for (int a=0; a < s.length; a++) {
			num += s[a] * (int) Math.pow(3, a);
		}
		return "" + num;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String winner() {
		int wins[][] = {{1, 2, 3}, {1, 4, 7}, {1, 5, 9}, {2, 5, 8}, {3, 6, 9}, {3, 5, 7}, {4, 5, 6}, {7, 8, 9}};
		int xwin = 0;
		int owin = 0;
		for (int a=0; a < wins.length; a++) {
			if (s[wins[a][0] - 1] == s[wins[a][1] - 1] && 
					s[wins[a][0] - 1] == s[wins[a][2] - 1]) {
				if (s[wins[a][1] - 1] == 1) {
					xwin++;
				}
				if (s[wins[a][1] - 1] == 2) {
					owin++;
				}
			}
		}

		int numx = 0;

		int numo = 0;
		for (int a=0; a < 9; a++) {
			if (s[a] == 1) {
				numx++;
			}
			else if (s[a] == 2) {
				numo++;
			}
		}
		if (xwin > 0 && owin > 0) {
			return "Both win";
		}
		if (xwin > 0) {
			return "X wins";
		}
		if (owin > 0) {
			return "O wins";
		}
		if (numx < 5) {
			return "incomplete";
		}
		return "cat game";
	}


	/**
	 *  Description of the Method 
	 */
	public void view() {
		System.out.println("");
		System.out.println("Game #" + toString() + " " + winner());
		for (int a=0; a < 3; a++) {
			for (int b = 0; b < 3; b++) {
				if (s[a * 3 + b] == 1) {
					System.out.print("x");
				}
				else if (s[a * 3 + b] == 2) {
					System.out.print("o");
				}
				else {
					System.out.print(" ");
				}
			}
			System.out.println("");
		}
	}
}

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class tictac {


	Vector all = new Vector();
	Vector any = new Vector();


	/**
	 *  Constructor for the tictac object 
	 */
	public tictac() {
		for (int a=0; a < Math.pow(3, 9); a++) {
			state t = new state(a);
			if (t.valid()) {
				add(t);
			}
		}
		System.out.println("FINAL " + all.size() + " total possible games");
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  t  Description of Parameter 
	 */
	public void add(state t) {
		if (!any.contains(t.toString())) {
			any.addElement(t.toString());
		}

		state tt[] = t.permutate();
		boolean found = false;
		for (int a=0; a < tt.length; a++) {
			if (all.contains(tt[a].toString())) {
				found = true;
			}
			;
		}
		if (all.contains(t.toString())) {
			found = true;
		}

		if (!found) {
			all.addElement(t.toString());
			t.view();
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		new tictac();
	}
}

/*
 * 3^9 = 19683 possible combinations of X/0/- in 9 digits
 * each setting has 8 reflections
 * actuall possible unique combination of x/0/- = 2460.375
 * valid combination have #X==#0 or #X-1==#0  so 1/2 invalid
 * actuall possible unique combintion of x/0/- in valid games <1230
 * 123456789
 * 147258369
 * 321654987
 * 369258147
 * 741852963
 * 789456123
 * 987654321
 * 963852741
 * moves 9 max
 * X0X
 * 0X0
 * X0X
 * unique games =?
 * 1st move 3 possible
 * 2nd move 4/5 possible
 * wins
 * 123
 * 456
 * 789
 * make library
 * x move
 * win
 * cat
 * lose
 * o move
 * win
 * cat
 * lose
 * xmove - id - win
 * xmove - id - cat
 * xmove - id - lose
 */
