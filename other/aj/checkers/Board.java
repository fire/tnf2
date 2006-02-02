package aj.checkers;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Board {

	boolean scoredone = false;
	private char map[][];
	//8x4
	private int nextMove = RED;
	private Vector moves;
	//String of jumps or moves red and black
	private int score = 0;
	/**
	 *  Description of the Field 
	 */
	public static int RED = 0, WHITE = 1;

	/**
	 *  Description of the Field 
	 */
	public static char REDPAWN = 'r', REDKING = 'R', WHITEPAWN = 'w', WHITEKING = 'W', BLOCK = '#', EMPTY = ' ';


	/**
	 *  Constructor for the Board object 
	 *
	 *@param  t  Description of Parameter 
	 */
	public Board(char[][] t) {
		map = t;
	}


	/**
	 *  Constructor for the Board object 
	 *
	 *@param  b  Description of Parameter 
	 */
	public Board(Board b) {
		map = new char[8][4];
		int a;
		int c;
		for (a = 0; a < 8; a++) {
			for (c = 0; c < 4; c++) {
				map[a][c] = b.map[a][c];
			}
		}
		nextMove = b.nextMove;
	}


	/**
	 *  Sets the NextMove attribute of the Board object 
	 *
	 *@param  p  The new NextMove value 
	 */
	public void setNextMove(int p) {
		nextMove = p;
	}


	/**
	 *  Sets the Map attribute of the Board object 
	 *
	 *@param  pos  The new Map value 
	 *@param  c    The new Map value 
	 */
	public void setMap(int pos, char c) {
		int row = (pos - 1) / 4;
		int col = pos - row * 4 - 1;
		if (row < 0 || row > 8 || col < 0 || col > 4) {
			return;
		}
		map[row][col] = c;
	}


	/**
	 *  Gets the NextMove attribute of the Board object 
	 *
	 *@return    The NextMove value 
	 */
	public int getNextMove() {
		return nextMove;
	}


	/**
	 *  Gets the Moves attribute of the Board object 
	 *
	 *@return    The Moves value 
	 */
	public Vector getMoves() {
		if (moves != null) {
			return moves;
		}
		moves = new Vector();
		int a;
		int e;
		boolean nextMoveRed = (nextMove == RED);
		//look for jumps
		for (a = 1; a < 33; a++) {
			char c = getMap(a);
			if (c == EMPTY) {
				continue;
			}
			boolean redmov = (c == REDPAWN || c == REDKING || c == WHITEKING);
			boolean whitemov = (c == WHITEPAWN || c == WHITEKING || c == REDKING);
			boolean isred = (c == REDPAWN || c == REDKING);
			boolean iswhi = (c == WHITEPAWN || c == WHITEKING);
			if ((isred && !nextMoveRed) || (iswhi && nextMoveRed)) {
				continue;
			}
			findJump(0, "", a, redmov, whitemov, isred);
		}
		if (moves.size() > 0) {
			return moves;
		}
		//look for moves
		for (a = 1; a < 33; a++) {
			char c = getMap(a);
			if (c == EMPTY) {
				continue;
			}
			boolean redmov = (c == REDPAWN || c == REDKING || c == WHITEKING);
			boolean whitemov = (c == WHITEPAWN || c == WHITEKING || c == REDKING);
			boolean isred = (c == REDPAWN || c == REDKING);
			boolean iswhi = (c == WHITEPAWN || c == WHITEKING);
			if ((isred && !nextMoveRed) || (iswhi && nextMoveRed)) {
				continue;
			}
			addMove(a, redmov, whitemov);
		}
		//    if (moves.size()==0)
		//      System.out.println("no moves found");
		return moves;
	}


	/**
	 *  Gets the Score attribute of the Board object 
	 *
	 *@param  red  Description of Parameter 
	 *@return      The Score value 
	 */
	public int getScore(int red) {
		if (red == RED) {
			return getScore();
		}
		if (red == WHITE) {
			return -getScore();
		}
		return 0;
	}


	/**
	 *  Gets the Score attribute of the Board object 
	 *
	 *@return    The Score value 
	 */
	public int getScore() {
		if (gameOver()) {
			return (getNextMove() == RED ? -99999 : 99999);
		}
		score = 0;
		int pawnval = 100;
		int kingval = 180;
		int rowval = 2;
		int walval = 1;
		int jumpval = 100;
		int kinblock = 2;
		int a;
		for (a = 1; a < 33; a++) {
			boolean red;
			boolean pawn;
			if (getMap(a) == REDPAWN) {
				red = true;
				pawn = true;
			}
			else if (getMap(a) == WHITEPAWN) {
				red = false;
				pawn = true;
			}
			else if (getMap(a) == REDKING) {
				red = true;
				pawn = false;
			}
			else if (getMap(a) == WHITEKING) {
				red = false;
				pawn = false;
			}
			else {
				continue;
			}
			score += (red ? 1 : -1) * (pawn ? 100 : 180);
			//pawn 100 king 180
			//count moves not pawns and kings?
			int row = (a - 1) / 4;
			//pawn 100 king 180
			//count moves not pawns and kings?
			int col = (a - 1) % 4 * 2 + (row % 2 == 0 ? 1 : 0);
			if (pawn && red) {
				score += sum(row) / 2;
			}
			//red pawn advance good
			if (pawn && !red) {
				score -= sum(7 - row) / 2;
			}
			//white pawn advance good
			if (pawn && row == 0 && red) {
				score += 2;
			}
			//red king row blocked
			if (pawn && row == 8 && !red) {
				score -= 2;
			}
			//white king row blocked
			if (!pawn && row < 7 && row > 0 && col < 7 && col > 0) {
				score += (red ? 3 : -3);
			}
			//king away for edge
			if (!pawn && row < 6 && row > 1 && col < 6 && col > 1) {
				score += (red ? 1 : -1);
			}
			//king away for edge
			if (pawn && (col == 0 || col == 7)) {
				score += (red ? 1 : -1);
			}
			//pawn against wall good
		}
		Vector v = getMoves();
		int hop = 0;
		for (a = 0; a < v.size(); a++) {
			String mv = (String) v.elementAt(a);
			hop += numHops(mv);
		}
		score += hop * 30 * (getNextMove() == RED ? 1 : -1);
		score += (getNextMove() == RED ? 1 : -1) * 2 * v.size();
		//next move has advantage
		return score;
	}


	/**
	 *  Gets the Map attribute of the Board object 
	 *
	 *@param  pos  Description of Parameter 
	 *@return      The Map value 
	 */
	public char getMap(int pos) {
		if (pos < 1 || pos > 32) {
			return BLOCK;
		}
		int row = (pos - 1) / 4;
		int col = pos - row * 4 - 1;
		if (row < 0 || row > 8 || col < 0 || col > 4) {
			return BLOCK;
		}
		return map[row][col];
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public int numHops(String s) {
		if (s.indexOf("-") < 0) {
			return -1;
		}
		int f;
		int e = Integer.parseInt(s.substring(0, s.indexOf("-")));
		s = s.substring(s.indexOf("-") + 1);
		int count = 0;
		while (!s.equals("")) {
			f = e;
			if (s.indexOf("-") > 0) {
				e = Integer.parseInt(s.substring(0, s.indexOf("-")));
				s = s.substring(s.indexOf("-") + 1);
			}
			else {
				e = Integer.parseInt(s);
				s = "";
			}
			if (Math.abs(e - f) > 6) {
				count++;
			}
		}
		return count;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Board applyMove(String s) {
		if (s == null || s.equals("") || s.indexOf("-") <= 0 || !getMoves().contains(s)) {
			System.out.println("bad move applied (" + s + ")");
			System.exit(0);
		}
		//    System.out.println("move applyed "+s);
		Board b = new Board(this);
		int e = Integer.parseInt(s.substring(0, s.indexOf("-")));
		s = s.substring(s.indexOf("-") + 1);
		while (!s.equals("")) {
			int f = e;
			if (s.indexOf("-") > 0) {
				e = Integer.parseInt(s.substring(0, s.indexOf("-")));
				s = s.substring(s.indexOf("-") + 1);
			}
			else {
				e = Integer.parseInt(s);
				s = "";
			}
			int frow = (f - 1) / 4;
			int fcol = f - frow * 4 - 1;
			int erow = (e - 1) / 4;
			int ecol = e - erow * 4 - 1;
			char c = b.getMap(f);
			b.setMap(f, EMPTY);
			b.setMap(e, c);
			if (Math.abs(e - f) > 6) {
				int mpos;
				if (frow % 2 == 1) {
					if (e - f == 9) {
						mpos = f + 4;
					}
					else if (e - f == -9) {
						mpos = f - 5;
					}
					else if (e - f == 7) {
						mpos = f + 3;
					}
					else {
						//if (e-f==-7)
						mpos = f - 4;
					}
				}
				else {
					//(frow%2==0) {
					if (e - f == 9) {
						mpos = f + 5;
					}
					else if (e - f == -9) {
						mpos = f - 4;
					}
					else if (e - f == 7) {
						mpos = f + 4;
					}
					else {
						//if (e-f==-7)
						mpos = f - 3;
					}
				}
				b.setMap(mpos, EMPTY);
			}
		}
		if (nextMove == RED) {
			b.setNextMove(WHITE);
		}
		else {
			b.setNextMove(RED);
		}
		b.applyCrowns();
		return b;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public boolean gameOver() {
		return getMoves().size() == 0;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  r  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public int sum(int r) {
		if (r == 1) {
			return 1;
		}
		if (r == 2) {
			return 3;
		}
		if (r == 3) {
			return 6;
		}
		if (r == 4) {
			return 10;
		}
		if (r == 5) {
			return 15;
		}
		if (r == 6) {
			return 21;
		}
		if (r == 7) {
			return 28;
		}
		return 0;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  level  Description of Parameter 
	 *@param  child  Description of Parameter 
	 *@param  pos    Description of Parameter 
	 *@param  rmove  Description of Parameter 
	 *@param  wmove  Description of Parameter 
	 *@param  imRed  Description of Parameter 
	 */
	public void findJump(int level, String child, int pos, boolean rmove, boolean wmove, boolean imRed) {
		boolean haveJump = false;
		int jr;
		int jl;
		int i;
		if (rmove) {
			jr = jumpRight(RED, pos, imRed);
			if (jr != -1) {
				haveJump = true;
				i = moveRight(RED, pos);
				char c = getMap(i);
				char m = getMap(pos);
				setMap(i, EMPTY);
				setMap(pos, EMPTY);
				setMap(jr, m);
				if (level == 0) {
					findJump(level + 1, pos + "-" + jr, jr, rmove, wmove, imRed);
				}
				else {
					findJump(level + 1, child + "-" + jr, jr, rmove, wmove, imRed);
				}
				setMap(i, c);
				setMap(pos, m);
				setMap(jr, EMPTY);
			}
			jl = jumpLeft(RED, pos, imRed);
			if (jl != -1) {
				haveJump = true;
				i = moveLeft(RED, pos);
				char c = getMap(i);
				char m = getMap(pos);
				setMap(i, EMPTY);
				setMap(pos, EMPTY);
				setMap(jl, m);
				if (level == 0) {
					findJump(level + 1, pos + "-" + jl, jl, rmove, wmove, imRed);
				}
				else {
					findJump(level + 1, child + "-" + jl, jl, rmove, wmove, imRed);
				}
				setMap(i, c);
				setMap(pos, m);
				setMap(jl, EMPTY);
			}
		}
		if (wmove) {
			jr = jumpRight(WHITE, pos, imRed);
			if (jr != -1) {
				haveJump = true;
				i = moveRight(WHITE, pos);
				char c = getMap(i);
				char m = getMap(pos);
				setMap(i, EMPTY);
				setMap(pos, EMPTY);
				setMap(jr, m);
				if (level == 0) {
					findJump(level + 1, pos + "-" + jr, jr, rmove, wmove, imRed);
				}
				else {
					findJump(level + 1, child + "-" + jr, jr, rmove, wmove, imRed);
				}
				setMap(i, c);
				setMap(pos, m);
				setMap(jr, EMPTY);
			}
			jl = jumpLeft(WHITE, pos, imRed);
			if (jl != -1) {
				haveJump = true;
				i = moveLeft(WHITE, pos);
				char c = getMap(i);
				char m = getMap(pos);
				setMap(i, EMPTY);
				setMap(pos, EMPTY);
				setMap(jl, m);
				if (level == 0) {
					findJump(level + 1, pos + "-" + jl, jl, rmove, wmove, imRed);
				}
				else {
					findJump(level + 1, child + "-" + jl, jl, rmove, wmove, imRed);
				}
				setMap(i, c);
				setMap(pos, m);
				setMap(jl, EMPTY);
			}
		}
		if (haveJump == false && level > 0) {
			moves.addElement(child);
		}
	}



	/**
	 *  Adds a feature to the Move attribute of the Board object 
	 *
	 *@param  pos        The feature to be added to the Move attribute 
	 *@param  moveRed    The feature to be added to the Move attribute 
	 *@param  moveWhite  The feature to be added to the Move attribute 
	 */
	public void addMove(int pos, boolean moveRed, boolean moveWhite) {
		int e;
		if (moveRed) {
			e = moveLeft(RED, pos);
			if (getMap(e) == EMPTY) {
				moves.addElement(pos + "-" + e);
			}
			e = moveRight(RED, pos);
			if (getMap(e) == EMPTY) {
				moves.addElement(pos + "-" + e);
			}
		}
		if (moveWhite) {
			e = moveLeft(WHITE, pos);
			if (getMap(e) == EMPTY) {
				moves.addElement(pos + "-" + e);
			}
			e = moveRight(WHITE, pos);
			if (getMap(e) == EMPTY) {
				moves.addElement(pos + "-" + e);
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  red    Description of Parameter 
	 *@param  pos    Description of Parameter 
	 *@param  imRed  Description of Parameter 
	 *@return        Description of the Returned Value 
	 */
	public int jumpRight(int red, int pos, boolean imRed) {
		int e = moveRight(red, pos);
		char t = getMap(e);
		int e2 = moveRight(red, e);
		boolean vt = ((imRed && (t == WHITEPAWN || t == WHITEKING)) || 
				(!imRed && (t == REDPAWN || t == REDKING)));
		char t2 = getMap(e2);
		if (t2 == EMPTY && vt) {
			return e2;
		}
		else {
			return -1;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  red    Description of Parameter 
	 *@param  pos    Description of Parameter 
	 *@param  imRed  Description of Parameter 
	 *@return        Description of the Returned Value 
	 */
	public int jumpLeft(int red, int pos, boolean imRed) {
		int e = moveLeft(red, pos);
		char t = getMap(e);
		int e2 = moveLeft(red, e);
		boolean vt = ((imRed && (t == WHITEPAWN || t == WHITEKING)) || 
				(!imRed && (t == REDPAWN || t == REDKING)));
		char t2 = getMap(e2);
		if (t2 == EMPTY && vt) {
			return e2;
		}
		else {
			return -1;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  red  Description of Parameter 
	 *@param  pos  Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public int moveRight(int red, int pos) {
		if (pos % 4 == 0 && (pos - 1) / 4 % 2 == 0) {
			return -1;
		}
		return pos + (red == 0 ? 5 : -3) - (((pos - 1) / 4) % 2);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  red  Description of Parameter 
	 *@param  pos  Description of Parameter 
	 *@return      Description of the Returned Value 
	 */
	public int moveLeft(int red, int pos) {
		if (pos % 4 == 1 && (pos - 1) / 4 % 2 == 1) {
			return -1;
		}
		return pos + (red == 0 ? 4 : -4) - (((pos - 1) / 4) % 2);
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String s = "" + BLOCK;
		int a;
		for (a = 1; a < 33; a++) {
			s += "" + getMap(a);
			if (a % 4 != 0) {
				s += "" + BLOCK;
			}
			if ((a - 1) / 4 % 2 == 1 && a % 4 == 0 && a != 32) {
				s += BLOCK + "\n" + BLOCK;
			}
			if ((a - 1) / 4 % 2 == 0 && a % 4 == 0) {
				s += "" + "\n";
			}
			if (a == 32) {
				s += BLOCK + " " + getScore(nextMove) + (nextMove == RED ? "RED" : "WHITE") + "\n";
			}
		}
		return s;
	}


	/**
	 *  Description of the Method 
	 */
	private void applyCrowns() {
		int a;
		for (a = 1; a < 5; a++) {
			char c = getMap(a);
			if (c == WHITEPAWN) {
				setMap(a, WHITEKING);
			}
			c = getMap(33 - a);
			if (c == REDPAWN) {
				setMap(33 - a, REDKING);
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public static Board newBoard() {
		char m[][] = {{REDPAWN, REDPAWN, REDPAWN, REDPAWN}, 
				{REDPAWN, REDPAWN, REDPAWN, REDPAWN}, 
				{REDPAWN, REDPAWN, REDPAWN, REDPAWN}, 
				{EMPTY, EMPTY, EMPTY, EMPTY}, 
				{EMPTY, EMPTY, EMPTY, EMPTY}, 
				{WHITEPAWN, WHITEPAWN, WHITEPAWN, WHITEPAWN}, 
				{WHITEPAWN, WHITEPAWN, WHITEPAWN, WHITEPAWN}, 
				{WHITEPAWN, WHITEPAWN, WHITEPAWN, WHITEPAWN}};
		return new Board(m);
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public static Board randBoard() {
		Board b = Board.newBoard();
		int a;
		for (a = 1; a < 33; a++) {
			b.setMap(a, EMPTY);
		}
		for (a = 0; a < 24; a++) {
			int pos = (int) (Math.random() * 32) + 1;
			char c;
			if (a < 12) {
				c = REDPAWN;
			}
			else {
				c = WHITEPAWN;
			}
			b.setMap(pos, c);
		}
		b.applyCrowns();
		return b;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		Board b = Board.randBoard();
		Vector v = b.getMoves();
		System.out.println(b.toString());
		int a;
		System.out.println("");
		if (v.size() == 0) {
			System.out.println("GAME OVER!");
			System.exit(0);
		}
		b = b.applyMove((String) v.elementAt(0));
		System.out.println(b.toString());

	}
}

/*
 * red player1  (1-12) ie (12 peices each)
 * white player2 (21-32)
 * king crown ends turn
 * #1 # 2# 3# 4
 * 5# 6# 7# 8#
 * # 9#10#11#12
 * 13#14#15#16#
 * #17#18#19#20
 * 21#22#23#24#
 * #25#26#27#28
 * 29#30#31#32#
 */
