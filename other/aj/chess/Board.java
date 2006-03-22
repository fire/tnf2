package aj.chess;

import java.util.Vector;

public class Board {

	public static int BLACK = 0, WHITE = 1;
	public static char EMPTY=' ';
	public static char BLOCK='#';
	public static char BLACKPAWN= 'p', BLACKKING = 'k', BLACKKNIGHT='h', BLACKBISHOP='b',BLACKROOK='r',BLACKQUEEN='q';
	public static char WHITEPAWN= 'P', WHITEKING = 'K', WHITEKNIGHT='H', WHITEBISHOP='B',WHITEROOK='R',WHITEQUEEN='Q';

	static String blackfriend="pkqhrb";
	String friend=blackfriend,targ=blackfriend.toUpperCase();

	private char map[][];
	private int nextToMove = WHITE;

	boolean gameover=false;
	boolean inCheck=false;
	Vector allMoves=new Vector();


	boolean scoredone = false;
	public int getScore(int a) {
		if (a==BLACK) return getScore();
		else return -1*getScore();
	}

	public int getScore() {
		int tot=0;
		for (int a=0;a<8;a++) {
			for (int b=0;b<8;b++) {
				String c=""+getMap(a*8+b);
				if (c.toUpperCase().equals("P")) tot+=(isWhite(c)?-1:1)*10;
				if (c.toUpperCase().equals("B")) tot+=(isWhite(c)?-1:1)*100;
				if (c.toUpperCase().equals("H")) tot+=(isWhite(c)?-1:1)*120;
				if (c.toUpperCase().equals("R")) tot+=(isWhite(c)?-1:1)*150;
				if (c.toUpperCase().equals("Q")) tot+=(isWhite(c)?-1:1)*650;
				if (c.toUpperCase().equals("K")) tot+=(isWhite(c)?-1:1)*1000;
			}
		}
		return tot;
	}

	public int getNextMove() {return nextToMove;}

	public Board() {
		map=new char[8][8];
		nextToMove=WHITE;
		if (nextToMove==WHITE) {friend=blackfriend.toUpperCase();targ=blackfriend;}
		if (nextToMove==BLACK) {friend=blackfriend;targ=blackfriend.toUpperCase();}
	}
	public Board(char[][] t) {
		map = t;
		nextToMove=WHITE;
		if (nextToMove==WHITE) {friend=blackfriend.toUpperCase();targ=blackfriend;}
		if (nextToMove==BLACK) {friend=blackfriend;targ=blackfriend.toUpperCase();}
	}

	public Board(Board b) {
		map = new char[8][8];
		for (int a = 0; a < 8; a++)for (int c = 0; c < 8; c++) map[a][c] = b.map[a][c];
		nextToMove=b.nextToMove;
		if (nextToMove==WHITE) {friend=blackfriend.toUpperCase();targ=blackfriend;}
		if (nextToMove==BLACK) {friend=blackfriend;targ=blackfriend.toUpperCase();}
	}

	public void toggleNextToMove() {
		nextToMove = (nextToMove==WHITE?BLACK:WHITE);
		if (nextToMove==WHITE) {friend=blackfriend.toUpperCase();targ=blackfriend;}
		if (nextToMove==BLACK) {friend=blackfriend;targ=blackfriend.toUpperCase();}
	}

	public void setMap(int pos, char c) {
		int row = pos/8;
		int col = pos%8;
		if (row < 0 || row > 7 || col < 0 || col > 7) {
			return;
		}
		map[row][col] = c;
	}


//pos:pos
//pos:pos:r
	public void calculateMoves(){
//System.out.println("calculating all possible moves");
		allMoves=new Vector();
		for (int a=0;a<64;a++) {
			char c=getMap(a);
			if (friend.indexOf(c+"")>=0) {
				Vector v=getAllMoves(a,c+"");
				for (int b=0;b<v.size();b++)
					allMoves.addElement(v.elementAt(b));
			}
		}
	}

	public void validMoves() {
//System.out.println("cutting in valid moves");
		for (int a=0;a<allMoves.size();a++) {
			String m=(String)allMoves.elementAt(a);
			Board b=applyMove(m);
//must not getAllMoves or infinate loop
			b.calculateMoves();
			for (int c=0;c<b.allMoves.size();c++) {
				String m2=(String)b.allMoves.elementAt(c);	
				if (m2.toUpperCase().endsWith("K")) {
//System.out.println("move "+m+" creates invalid state "+m2+" removing");
					allMoves.removeElementAt(a);
					a--;
					break;
				}
			}
		}
	}

	public Vector getAllMoves() {
//System.out.println("getting valid moves");
		calculateMoves();
		validMoves();
		return allMoves;
	}

	public boolean isBlack(String s) {
		return !isWhite(s);
	}
	public boolean isWhite(String s) {
		return s.toUpperCase().equals(s);
	}
	public boolean isTargetAt(int a) {
		return targ.indexOf(getMap(a))>=0;
	}
	public boolean isOpenAt(int a) {
		return (getMap(a)+"").equals(EMPTY+"");
	}
	public boolean isBlockedAt(int a) {
		return (getMap(a)+"").equals(BLOCK+"");
	}
	public boolean isFriendlyAt(int a) {
		return friend.indexOf(getMap(a))>=0;
	}
	public Vector getAllMoves(int pos,String c) {
		Vector v=new Vector();
		if (c.toUpperCase().equals("R")){
			boolean done=false;
			int count=1;
			while (!done) {
				if (pos/8>count-1 && isOpenAt(pos-8*count)) v.addElement(pos+":"+(pos-8*count));
				if (pos/8>count-1 && isTargetAt(pos-8*count)) {
					v.addElement(pos+":"+(pos-8*count)+":"+(pos-8*count));	
					done=true;
				}
				if (pos/8>count-1 && isFriendlyAt(pos-8*count)) done=true;
				if (pos-8*count<0 || pos-8*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos%8>count-1 && isOpenAt(pos-1*count)) v.addElement(pos+":"+(pos-1*count));
				if (pos%8>count-1 && isTargetAt(pos-1*count)) {
					v.addElement(pos+":"+(pos-1*count)+":"+getMap(pos-1*count));
					done=true;
				}
				if (pos%8>count-1 && isFriendlyAt(pos-1*count)) done=true;
				if (pos-1*count<0 || pos-1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8<8-count && isOpenAt(pos+8*count)) v.addElement(pos+":"+(pos+8*count));
				if (pos/8<8-count && isTargetAt(pos+8*count)) {
					v.addElement(pos+":"+(pos+8*count)+":"+getMap(pos+8*count));
					done=true;
				}
				if (pos/8<8-count && isFriendlyAt(pos+8*count)) done=true;
				if (pos+8*count<0 || pos+8*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos%8<8-count && isOpenAt(pos+1*count)) v.addElement(pos+":"+(pos+1*count));
				if (pos%8<8-count && isTargetAt(pos+1*count)) {
					v.addElement(pos+":"+(pos+1*count)+":"+getMap(pos+1*count));
					done=true;
				}
				if (pos%8<8-count && isFriendlyAt(pos+1*count)) done=true;
				if (pos+1*count<0 || pos+1*count>64) done=true;
				count++;
			}
		}
		if (c.toUpperCase().equals("B")){
			boolean done=false;
			int count=1;
			while (!done) {
				if (pos/8>count-1 && pos%8>count-1 && isOpenAt(pos-8*count-1*count)) v.addElement(pos+":"+(pos-8*count-1*count));
				if (pos/8>count-1 && pos%8>count-1 && isTargetAt(pos-8*count-1*count)) {
					v.addElement(pos+":"+(pos-8*count-1*count)+":"+getMap(pos-8*count-1*count));
					done=true;
				}
				if (pos/8>count-1 && pos%8>count-1 && isFriendlyAt(pos-8*count-1*count)) done=true;
				if (pos-8*count-1*count<0 || pos-8*count-1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8<8-count && pos%8>count-1 && isOpenAt(pos+8*count-1*count)) v.addElement(pos+":"+(pos+8*count-1*count));
				if (pos/8<8-count && pos%8>count-1 && isTargetAt(pos+8*count-1*count)){
					v.addElement(pos+":"+(pos+8*count-1*count)+":"+getMap(pos+8*count-1*count));
					done=true;
				}
				if (pos/8<8-count && pos%8>count-1 && isFriendlyAt(pos+8*count-1*count)) done=true;
				if (pos+8*count-1*count<0 || pos+8*count-1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8<8-count && pos%8<8-count && isOpenAt(pos+8*count+1*count)) v.addElement(pos+":"+(pos+8*count+1*count));
				if (pos/8<8-count && pos%8<8-count && isTargetAt(pos+8*count+1*count)) {
					v.addElement(pos+":"+(pos+8*count+1*count)+":"+getMap(pos+8*count+1*count));
					done=true;
				}
				if (pos/8<8-count && pos%8<8-count && isFriendlyAt(pos+8*count+1*count)) done=true;
				if (pos+8*count+1*count<0 || pos+8*count+1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8>count-1 && pos%8<8-count && isOpenAt(pos-8*count+1*count)) v.addElement(pos+":"+(pos-8*count+1*count));
				if (pos/8>count-1 && pos%8<8-count && isTargetAt(pos-8*count+1*count)) {
					v.addElement(pos+":"+(pos-8*count+1*count)+":"+getMap(pos-8*count+1*count));
					done=true;
				}
				if (pos/8>count-1 && pos%8<8-count && isFriendlyAt(pos-8*count+1*count)) done=true;
				if (pos-8*count+1*count<0 || pos-8*count+1*count>64) done=true;
				count++;
			}
		}
		if (c.toUpperCase().equals("Q")){
			boolean done=false;
			int count=1;
			while (!done) {
				if (pos/8>count-1 && isOpenAt(pos-8*count)) v.addElement(pos+":"+(pos-8*count));
				if (pos/8>count-1 && isTargetAt(pos-8*count)) {
					v.addElement(pos+":"+(pos-8*count)+":"+(pos-8*count));	
					done=true;
				}
				if (pos/8>count-1 && isFriendlyAt(pos-8*count)) done=true;
				if (pos-8*count<0 || pos-8*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8>count-1 && pos%8>count-1 && isOpenAt(pos-8*count-1*count)) v.addElement(pos+":"+(pos-8*count-1*count));
				if (pos/8>count-1 && pos%8>count-1 && isTargetAt(pos-8*count-1*count)) {
					v.addElement(pos+":"+(pos-8*count-1*count)+":"+getMap(pos-8*count-1*count));
					done=true;
				}
				if (pos/8>count-1 && pos%8>count-1 && isFriendlyAt(pos-8*count-1*count)) done=true;
				if (pos-8*count-1*count<0 || pos-8*count-1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos%8>count-1 && isOpenAt(pos-1*count)) v.addElement(pos+":"+(pos-1*count));
				if (pos%8>count-1 && isTargetAt(pos-1*count)) {
					v.addElement(pos+":"+(pos-1*count)+":"+getMap(pos-1*count));
					done=true;
				}
				if (pos%8>count-1 && isFriendlyAt(pos-1*count)) done=true;
				if (pos-1*count<0 || pos-1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8<8-count && pos%8>count-1 && isOpenAt(pos+8*count-1*count)) v.addElement(pos+":"+(pos+8*count-1*count));
				if (pos/8<8-count && pos%8>count-1 && isTargetAt(pos+8*count-1*count)){
					v.addElement(pos+":"+(pos+8*count-1*count)+":"+getMap(pos+8*count-1*count));
					done=true;
				}
				if (pos/8<8-count && pos%8>count-1 && isFriendlyAt(pos+8*count-1*count)) done=true;
				if (pos+8*count-1*count<0 || pos+8*count-1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8<8-count && isOpenAt(pos+8*count)) v.addElement(pos+":"+(pos+8*count));
				if (pos/8<8-count && isTargetAt(pos+8*count)) {
					v.addElement(pos+":"+(pos+8*count)+":"+getMap(pos+8*count));
					done=true;
				}
				if (pos/8<8-count && isFriendlyAt(pos+8*count)) done=true;
				if (pos+8*count<0 || pos+8*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8<8-count && pos%8<8-count && isOpenAt(pos+8*count+1*count)) v.addElement(pos+":"+(pos+8*count+1*count));
				if (pos/8<8-count && pos%8<8-count && isTargetAt(pos+8*count+1*count)) {
					v.addElement(pos+":"+(pos+8*count+1*count)+":"+getMap(pos+8*count+1*count));
					done=true;
				}
				if (pos/8<8-count && pos%8<8-count && isFriendlyAt(pos+8*count+1*count)) done=true;
				if (pos+8*count+1*count<0 || pos+8*count+1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos%8<8-count && isOpenAt(pos+1*count)) v.addElement(pos+":"+(pos+1*count));
				if (pos%8<8-count && isTargetAt(pos+1*count)) {
					v.addElement(pos+":"+(pos+1*count)+":"+getMap(pos+1*count));
					done=true;
				}
				if (pos%8<8-count && isFriendlyAt(pos+1*count)) done=true;
				if (pos+1*count<0 || pos+1*count>64) done=true;
				count++;
			}
			count=1;done=false;
			while (!done) {
				if (pos/8>count-1 && pos%8<8-count && isOpenAt(pos-8*count+1*count)) v.addElement(pos+":"+(pos-8*count+1*count));
				if (pos/8>count-1 && pos%8<8-count && isTargetAt(pos-8*count+1*count)) {
					v.addElement(pos+":"+(pos-8*count+1*count)+":"+getMap(pos-8*count+1*count));
					done=true;
				}
				if (pos%8>count-1 && pos%8<8-count && isFriendlyAt(pos-8*count+1*count)) done=true;
				if (pos-8*count+1*count<0 || pos-8*count+1*count>64) done=true;
				count++;
			}
		}
		if (c.toUpperCase().equals("K")){
			if (pos/8>0 && isOpenAt(pos-8)) v.addElement(pos+":"+(pos-8));
			if (pos/8>0 && pos%8>0 && isOpenAt(pos-8-1)) v.addElement(pos+":"+(pos-8-1));
			if (pos%8>0 && isOpenAt(pos-1)) v.addElement(pos+":"+(pos-1));
			if (pos/8<7 && pos%8>0 && isOpenAt(pos+8-1)) v.addElement(pos+":"+(pos+8-1));
			if (pos/8<7 && isOpenAt(pos+8)) v.addElement(pos+":"+(pos+8));
			if (pos/8<7 && pos%8<7 && isOpenAt(pos+8+1)) v.addElement(pos+":"+(pos+8+1));
			if (pos%8<7 && isOpenAt(pos+1)) v.addElement(pos+":"+(pos+1));
			if (pos/8>0 && pos%8<7 && isOpenAt(pos-8+1)) v.addElement(pos+":"+(pos-8+1));

			if (pos/8>0 && isTargetAt(pos-8)) v.addElement(pos+":"+(pos-8)+":"+getMap(pos-8));
			if (pos/8>0 && pos%8>0 && isTargetAt(pos-8-1)) v.addElement(pos+":"+(pos-8-1)+":"+getMap(pos-8-1));
			if (pos%8>0 && isTargetAt(pos-1)) v.addElement(pos+":"+(pos-1)+":"+getMap(pos-1));
			if (pos/8<7 && pos%8>0 && isTargetAt(pos+8-1)) v.addElement(pos+":"+(pos+8-1)+":"+getMap(pos+8-1));
			if (pos/8<7 && isTargetAt(pos+8)) v.addElement(pos+":"+(pos+8)+":"+getMap(pos+8));
			if (pos/8<7 && pos%8<7 && isTargetAt(pos+8+1)) v.addElement(pos+":"+(pos+8+1)+":"+getMap(pos+8+1));
			if (pos%8<7 && isTargetAt(pos+1)) v.addElement(pos+":"+(pos+1)+":"+getMap(pos+1));
			if (pos/8>0 && pos%8<7 && isTargetAt(pos-8+1)) v.addElement(pos+":"+(pos-8+1)+":"+getMap(pos-8+1));
		}
		if (c.toUpperCase().equals("H")){
			if (pos/8>0) {
				if (pos%8>1 && isOpenAt(pos-8-2)) v.addElement(pos+":"+(pos-8-2));
				if (pos%8>1 && isTargetAt(pos-8-2)) v.addElement(pos+":"+(pos-8-2)+":"+getMap(pos-8-2));
				if (pos%8<6 && isOpenAt(pos-8+2)) v.addElement(pos+":"+(pos-8+2));
				if (pos%8<6 && isTargetAt(pos-8+2)) v.addElement(pos+":"+(pos-8+2)+":"+getMap(pos-8+2));
			}
			if (pos/8>1) {
				if (pos%8>0 && isOpenAt(pos-16-1)) v.addElement(pos+":"+(pos-16-1));
				if (pos%8>0 && isTargetAt(pos-16-1)) v.addElement(pos+":"+(pos-16-1)+":"+getMap(pos-16-1));
				if (pos%8<7 && isOpenAt(pos-16+1)) v.addElement(pos+":"+(pos-16+1));
				if (pos%8<7 && isTargetAt(pos-16+1)) v.addElement(pos+":"+(pos-16+1)+":"+getMap(pos-16+1));
			}
			if (pos/8<7) {
				if (pos%8>1 && isOpenAt(pos+8-2)) v.addElement(pos+":"+(pos+8-2));
				if (pos%8>1 && isTargetAt(pos+8-2)) v.addElement(pos+":"+(pos+8-2)+":"+getMap(pos+8-2));
				if (pos%8<6 && isOpenAt(pos+8+2)) v.addElement(pos+":"+(pos+8+2));
				if (pos%8<6 && isTargetAt(pos+8+2)) v.addElement(pos+":"+(pos+8+2)+":"+getMap(pos+8+2));
			}
			if (pos/8<6) {
				if (pos%8>0 && isOpenAt(pos+16-1)) v.addElement(pos+":"+(pos+16-1));
				if (pos%8>0 && isTargetAt(pos+16-1)) v.addElement(pos+":"+(pos+16-1)+":"+getMap(pos+16-1));
				if (pos%8<7 && isOpenAt(pos+16+1)) v.addElement(pos+":"+(pos+16+1));
				if (pos%8<7 && isTargetAt(pos+16+1)) v.addElement(pos+":"+(pos+16+1)+":"+getMap(pos+16+1));
			}
//36 -8+2 -8-2
//36 -16-1 16+1
//36 +16-1 +16+1
//36 +8+2 -8-2
		}
		if (c.toUpperCase().equals("P")){
			if (pos/8>0 && isWhite(c)) {
				if (pos%8>0 && isTargetAt(pos-9)) v.addElement(pos+":"+(pos-9)+":"+getMap(pos-9));
				if (pos%8<7 && isTargetAt(pos-7)) v.addElement(pos+":"+(pos-7)+":"+getMap(pos-7));
				if (isOpenAt(pos-8)) v.addElement(pos+":"+(pos-8));
				if (isOpenAt(pos-16) && isOpenAt(pos-8) && pos/8==6) v.addElement(pos+":"+(pos-16));
			}
			else if (pos/8<7 && !isWhite(c)) {
				if (pos%8>0 && isTargetAt(pos+7)) v.addElement(pos+":"+(pos+7)+":"+getMap(pos+7));
				if (pos%8<7 && isTargetAt(pos+9)) v.addElement(pos+":"+(pos+9)+":"+getMap(pos+9));
				if (isOpenAt(pos+8)) v.addElement(pos+":"+(pos+8));
				if (isOpenAt(pos+16) && isOpenAt(pos+8) && pos/8==1) v.addElement(pos+":"+(pos+16));
			}
		}
		return v;
	}


	public char getMap(int pos) {
		if (pos < 0 || pos > 64) return BLOCK;
		int row = (pos/8);
		int col = pos%8;
		if (row < 0 || row > 7 || col < 0 || col > 7) return BLOCK;
		return map[row][col];
	}

	public Board applyMove(String s) {
		try {
			int first=Integer.parseInt(s.substring(0,s.indexOf(":")));
			s=s.substring(s.indexOf(":")+1);
			if (s.indexOf(":")>0) s=s.substring(0,s.indexOf(":"));
			int next=Integer.parseInt(s);
			Board b=new Board(this);
			b.setMap(next,getMap(first));
			b.setMap(first,EMPTY);
			b.toggleNextToMove();
			return b;
		} catch (NumberFormatException nfe) {
			System.out.println("MyError:  Bad move in apply move");
		}
		return null;
	}

	public boolean gameOver() {
		return getAllMoves().size() == 0;
	}



	public String toString() {
		String s="";
		for (int a=0;a<8;a++) {
			s+="-------------------------\n|";
			for (int b=0;b<8;b++) {
				if (getMap(a*8+b)!=EMPTY) 
					s+=" "+getMap(a*8+b)+"|";
				else {
					s+="  |";
					//s+=(a*8+b<10?" ":"")+(a*8+b)+"|";
				}
				
			}
			s+="\n";
		}
		s+="-------------------------\n";
		s+=(nextToMove==WHITE?"White to move":"Black to move")+"\n";
		return s;
	}


	public static Board newBoard() {
		char m[][] = {{BLACKROOK,BLACKKNIGHT,BLACKBISHOP,BLACKKING,BLACKQUEEN,BLACKBISHOP,BLACKKNIGHT,BLACKROOK},
				{BLACKPAWN,BLACKPAWN,BLACKPAWN,BLACKPAWN,BLACKPAWN,BLACKPAWN,BLACKPAWN,BLACKPAWN}, 
				{EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY}, 
				{EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY}, 
				{EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY}, 
				{EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY}, 
				{WHITEPAWN,WHITEPAWN,WHITEPAWN,WHITEPAWN,WHITEPAWN,WHITEPAWN,WHITEPAWN,WHITEPAWN},
				{WHITEROOK,WHITEKNIGHT,WHITEBISHOP,WHITEKING,WHITEQUEEN,WHITEBISHOP,WHITEKNIGHT,WHITEROOK}};
		Board b=new Board(m);
		b.nextToMove=WHITE;
		return b;
	}


	public static Board randBoard() {
		Board b = Board.newBoard();
		b.nextToMove=(Math.random()>.5?WHITE:BLACK);
		for (int a = 0; a < 64; a++) {
			b.setMap(a, EMPTY);
		}
		for (int a = 0; a < 32; a++) {
			int pos = (int) (Math.random() * 64) + 1;
			char c=EMPTY;
			if (a<8) c=BLACKPAWN;
			if (a==8 || a==9) c=BLACKROOK;
			if (a==10 || a==11) c=BLACKKNIGHT;
			if (a==12 || a==13) c=BLACKBISHOP;
			if (a==14 ) c=BLACKQUEEN;
			if (a==15 ) c=BLACKKING;
			if (a==16 || a==17) c=WHITEROOK;
			if (a==18 || a==19) c=WHITEKNIGHT;
			if (a==20 || a==21) c=WHITEBISHOP;
			if (a==22 ) c=WHITEQUEEN;
			if (a==23 ) c=WHITEKING;
			if (a>=24) c=WHITEPAWN;
			b.setMap(pos, c);
		}
		return b;
	}


	public static void main(String s[]) {
		//Board b = Board.newBoard();
		Board b=Board.randBoard();
		Vector v = b.getAllMoves();
		System.out.println(b.toString());
		System.out.println("");
		for (int a=0;a<v.size();a++){
			String header="";
			try {
				String ss=(String)v.elementAt(a);
				int first=Integer.parseInt(ss.substring(0,ss.indexOf(":")));
				header+=b.getMap(first);
			} catch (NumberFormatException nfe) {
				System.out.println("MyError:  Bad move in apply move");
			}
			System.out.println("move "+header+" "+v.elementAt(a));
		}
		if (v.size() == 0) {
			System.out.println("GAME OVER!");
			System.exit(0);
		}
		b = b.applyMove((String) v.elementAt(0));
		System.out.println(b.toString());
	}
}

