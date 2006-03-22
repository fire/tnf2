package aj.checkers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;


/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class SmartPlayer  {

	Socket NL;
	int playernumber;
	String gname;

	Board b;
	BufferedReader br;

	int moves = 0;
	static boolean verbose = false, wait = false;
	static int TIMELIMIT = 2000, MINTIME = 100;


	/**
	 *  Constructor for the SmartPlayer object 
	 *
	 *@param  nl     Description of Parameter 
	 *@param  gname  Description of Parameter 
	 */
	public SmartPlayer(final Socket nl, String gname) {
		this.gname = gname;
		NL = nl;
		new Thread(){
			public void run() {
				try {
					br=new BufferedReader(new InputStreamReader(nl.getInputStream()));
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				while (true) {
					String s;
					try {
						s = br.readLine();
						if (s==null)break;
						actionPerformed(NL,s);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			}.start();
		send(NL,"__JOIN " + gname);
		br = new BufferedReader(new InputStreamReader(System.in));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public synchronized void actionPerformed(Socket NL,String s){
		if (s.startsWith("__")) {
			//system command
			if (s.toUpperCase().startsWith("__JOINED ")) {
				playernumber = Integer.parseInt(s.substring(s.lastIndexOf(" ")).trim());
				if (s.toUpperCase().indexOf("FULL") > 0) {
					send(NL,"START");
					begin();
				}
			}
			else if (s.toUpperCase().indexOf("FAILED") >= 0) {
				send(NL,"__DESTROY " + gname);
				send(NL,"__CREATE " + gname);
				send(NL,"__JOIN " + gname);
			}
		}
		else if (s.equalsIgnoreCase("START")) {
			begin();
		}
		else if (s.equalsIgnoreCase("DRAW")) {
			System.out.println("Draw");
			b = null;
			begin();
		}
		else if (moves > 300) {
			send(NL,"DRAW");
			System.out.println("Draw");
			b = null;
			begin();
		}
		else {
			b = b.applyMove(s);
			moves++;
			if (verbose) {
				System.out.println("your Move!");
				System.out.println(b.toString());
			}
			if (wait) {
				try {
					br.readLine();
				}
				catch (IOException IOE) {
				}
			}
			makeMove();
			moves++;
			if (verbose) {
				System.out.println("my Move!");
				System.out.println(b.toString());
			}
			if (wait) {
				try {
					br.readLine();
				}
				catch (IOException IOE) {
				}
			}
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void begin() {
		if (b != null) {
			System.out.println("I win! " + moves);
		}
		moves = 0;
		b = Board.newBoard();
		if (playernumber == 1) {
			makeMove();
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void makeMove() {
		if (b.gameOver()) {
			gameOver();
			return;
		}

		long timeout = getTimeOut(TIMELIMIT);
		int level = 0;
		String m = bestMove(b, 0, timeout);
		while (!checkTimeOut(timeout)) {
			level++;
			if (level > 20) {
				break;
			}
			String n = bestMove(b, level, timeout);
			//bbest.getMove();
			if (n == null) {
				break;
			}
			if (!n.equals(m)) {
				m = n;
				System.out.println("best move changed " + m + " level=" + level);
			}
		}
		b = b.applyMove(m);
		System.out.println("t=" + (System.currentTimeMillis() - timeout) + " lv=" + level + " move=" + moves + " " + m);
		send(NL,m);
	}

	public void send(Socket S,String line) {
		line+="\n";
		try {
			OutputStream o=S.getOutputStream();
			o.write(line.getBytes());
			o.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Description of the Method 
	 */
	public void gameOver() {
		System.out.println("I lose " + moves);
		b = null;
		send(NL,"START");
		begin();
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		//    if (s.length==0) {
		//      char m[][]={  {' ',' ',' ','r'},
		//                  {' ',' ','r','r'},
		//                    {' ',' ',' ',' '},
		//                  {' ',' ','W','w'},
		//                    {'R',' ',' ',' '},
		//                  {' ',' ',' ','w'},
		//                    {'w','R','W',' '},
		//                  {' ',' ',' ',' '}};
		//      Board b=new Board(m);
		//      String bm=bestMove(b,5,TIMELIMIT);
		//      System.out.println(b);
		//      System.out.println("bestmove="+bm);
		//      System.out.println(b.applyMove(bm));
		//      return;
		//    }
		try {
			Socket nl = new Socket(s[0], Integer.parseInt(s[1]));
			if (s.length > 3) {
				SmartPlayer.verbose = s[3].toUpperCase().indexOf("V") >= 0;
				SmartPlayer.wait = s[3].toUpperCase().indexOf("W") >= 0;
			}
			new SmartPlayer(nl, s[2]);
		}
		catch (Exception E) {
			System.out.println("FORMAT: java SmartPlayer <host> <port> <gamename> [-vw verbose wait]");
			System.out.println("MyError: " + E);
			System.exit(0);
		}
	}


	/**
	 *  Gets the TimeOut attribute of the SmartPlayer class 
	 *
	 *@param  timeLimit  Description of Parameter 
	 *@return            The TimeOut value 
	 */
	static long getTimeOut(int timeLimit) {
		return System.currentTimeMillis() + timeLimit;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  timeout  Description of Parameter 
	 *@return          Description of the Returned Value 
	 */
	static boolean checkTimeOut(long timeout) {
		return timeout < System.currentTimeMillis();
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  b        Description of Parameter 
	 *@param  level    Description of Parameter 
	 *@param  timeout  Description of Parameter 
	 *@return          Description of the Returned Value 
	 */
	static String bestMove(Board b, int level, long timeout) {
		Vector mvs = b.getMoves();
		//get all moves
		if (mvs.size() == 1) {
			return (String) mvs.elementAt(0);
		}

		Board nextb[] = new Board[mvs.size()];
		int bmove = 0;
		for (int c = 0; c < mvs.size(); c++) {
			nextb[c] = b.applyMove((String) mvs.elementAt(c));
		}
		if (checkTimeOut(timeout)) {
			level = 0;
		}
		for (int a=1; a < level; a++) {
			for (int c = 0; c < nextb.length; c++) {
				if (nextb[c].gameOver()) {
					continue;
				}
				String s = bestMove(nextb[c], level - a, timeout);
				nextb[c] = nextb[c].applyMove(s);
			}
		}
		for (int a=1; a < nextb.length; a++) {
			int as = nextb[a].getScore(b.getNextMove());
			int bs = nextb[bmove].getScore(b.getNextMove());
			if (as >= bs) {
				bmove = a;
			}
		}
		return (String) mvs.elementAt(bmove);
	}

}
