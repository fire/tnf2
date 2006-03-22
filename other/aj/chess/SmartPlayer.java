package aj.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;


public class SmartPlayer  {

	int playernumber;
	String gname;

	Board b;

	int moves = 0;
	static boolean verbose = false, wait = false;
	static int TIMELIMIT = 2000, MINTIME = 100;

	BufferedReader br;
	OutputStream os;

	public SmartPlayer(String host,int port, String gname) throws UnknownHostException, IOException {
		this.gname = gname;
		Socket s=new Socket(host,port);
		br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		new Thread(){
			public void run() {
				while (true) {
					String ss;
					try {
						ss = br.readLine();
						if (ss==null) break;
						actionPerformed(ss);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		os=s.getOutputStream();
		send("__JOIN " + gname);
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	private void send(String s) {
		s+="\n";
		try {
			os.write(s.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void actionPerformed(String s)  {
		if (s.startsWith("__")) {
			//system command
			if (s.toUpperCase().startsWith("__JOINED ")) {
				playernumber = Integer.parseInt(s.substring(s.lastIndexOf(" ")).trim());
				if (s.toUpperCase().indexOf("FULL") > 0) {
					send("START");
					begin();
				}
			}
			else if (s.toUpperCase().indexOf("FAILED") >= 0) {
				send("__DESTROY " + gname);
				send("__CREATE " + gname);
				send("__JOIN " + gname);
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
			send("DRAW");
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


	public void begin()   {
		if (b != null) {
			System.out.println("I win! " + moves);
		}
		moves = 0;
		b = Board.newBoard();
		if (playernumber == 1) {
			makeMove();
		}
	}


	public void makeMove()   {
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
			//bbest.getAllMove();
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
		send(m);
	}


	public void gameOver()   {
		System.out.println("I lose " + moves);
		b = null;
		send("START");
		begin();
	}


	public static void main(String s[]) {
		if (s.length==0) {
			System.out.println("FORMAT: java SmartPlayer <host> <port> <gamename> [-vw verbose wait]");
			System.exit(0);
		}
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
			if (s.length > 3) {
				SmartPlayer.verbose = s[3].toUpperCase().indexOf("V") >= 0;
				SmartPlayer.wait = s[3].toUpperCase().indexOf("W") >= 0;
			}
			new SmartPlayer(s[0],Integer.parseInt(s[1]), s[2]);
		}
		catch (Exception E) {
			System.out.println("FORMAT: java SmartPlayer <host> <port> <gamename> [-vw verbose wait]");
			System.out.println("MyError: " + E);
			System.exit(0);
		}
	}


	static long getTimeOut(int timeLimit) {
		return System.currentTimeMillis() + timeLimit;
	}


	static boolean checkTimeOut(long timeout) {
		return timeout < System.currentTimeMillis();
	}


	static String bestMove(Board b, int level, long timeout) {
		Vector mvs = b.getAllMoves();
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
