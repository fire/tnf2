package aj.chess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;


public class NetPlayer implements ActionListener {

	int playernumber;
	String gname;
	Board b;
	int moves = 0;
	long time;
	String allGame = "";
	String SMART = "SMART", RAND = "RAND", HUMAN = "HUMAN", WATCH = "WATCH";
	String brainType = SMART;
	HumanPlayer human = null;
	static boolean viewonly = false;
	static boolean verbose = false;
	static int TIMELIMIT = 2000;

	BufferedReader br;
	OutputStream os;

	public NetPlayer(String host,int port, String gname, String brain) throws UnknownHostException, IOException {
		Socket s=new Socket(host,port);
		br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		os=s.getOutputStream();
		if (brain.toUpperCase().indexOf(SMART) >= 0) {
			brainType = SMART;
		}
		else if (brain.toUpperCase().indexOf(RAND) >= 0) {
			brainType = RAND;
		}
		else if (brain.toUpperCase().indexOf(HUMAN) >= 0) {
			brainType = HUMAN;
			human = new HumanPlayer();
			human.addActionListener(this);
		}
		else if (brain.toUpperCase().indexOf(WATCH) >= 0) {
			brainType = WATCH;
			human = new HumanPlayer();
			human.addActionListener(this);
		}
		else {
			System.out.println("bad brain");
			System.exit(0);
		}
		this.gname = gname;
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
		if (brainType == WATCH) {
			send("__MODIFY name:" + gname + " pass:BLANK max:3 refill:true");
		}
		send("__JOIN name:" + gname);
	}

	public void send(String s) {
		s+="\n";
		try {
			os.write(s.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void actionPerformed(ActionEvent ae) {
		String s=ae.getActionCommand();
		if (ae.getSource() == human) {
			human.setLive(false);
			myApply(s);
			send(s);
			return;
		}
	}
	
	public synchronized void actionPerformed(String s) {
		if (s.startsWith("__")) {
			//system command
			if (s.toUpperCase().startsWith("__JOINED ")) {
				playernumber = Integer.parseInt(s.substring(s.lastIndexOf(" ")).trim());
//System.out.println("Player number="+playernumber);
				if (s.toUpperCase().indexOf("FULL") > 0) {
					send("START");
					begin();
				}
			}
			else if (s.toUpperCase().indexOf("FAILED") >= 0 && s.toUpperCase().indexOf("FULL") < 0) {
				send("__CREATE name:" + gname + " pass:BLANK  max:2 refill:true");
				send("__JOIN name:" + gname);
			}
			else if (s.toUpperCase().indexOf("FAILED") >= 0) {
				System.out.println("Game full.  Try later");
				System.exit(0);
			}
		}
		else if (s.equalsIgnoreCase("START")) {
			System.out.println("start found");
			begin();
		}
		else if (s.equalsIgnoreCase("DRAW")) {
			System.out.println("Draw");
			b = null;
			begin();
		}
		else if (moves > 300) {
			if (!viewonly) {
				send("DRAW");
			}
			System.out.println("Draw");
			b = null;
			begin();
		}
		else {
//System.out.println("move received="+s);
			if (b.getAllMoves().contains(s)) {
//System.out.println("valid move applying="+s);
				myApply(s);
				if (b.gameOver()) {
					gameOver();
				}
				moves++;
				makeMove();
			}
			else {
				System.out.println("MyError: invalid move detected. restart connection");
				send("START");
				begin();
			}
		}
	}


	public void begin() {
//System.out.println("game begin found");
		if (b != null) {
			myApply(null);
			System.out.println("I win! " + moves);
		}
		moves = 0;
		b = Board.newBoard();
		b.getAllMoves();
		if (playernumber == 1) {
			if (human != null) {
				human.set(b);
			}
			makeMove();
		}
		if (human != null) {
			human.set(b);
		}
	}


	public void gameOver() {
		System.out.println("I lose " + moves);
		myApply(null);
		b = null;
		begin();
	}


	public void myApply(String s) {
		//System.out.println("my apply " + s);
		//System.out.println("next to move ="+(b.getNextMove()==b.WHITE?"WHITE":"BLACK"));
		if (s == null) {
			System.out.println("game over found");
			if (b.getNextMove() == Board.BLACK) {
				allGame += "BLACK LOSE";
			}
			if (b.getNextMove() == Board.WHITE) {
				allGame += "WHITE LOSE";
			}
			System.out.println(allGame);
			allGame = "";
			return;
		}
		if (s.trim().length()==0) {
			System.out.println("MyError: applying blank command");
			return;
		}
		allGame += s + ";";
		b = b.applyMove(s);
		b.getAllMoves();
		//System.out.println("new next to move ="+(b.getNextMove()==b.WHITE?"WHITE":"BLACK"));
	}


	public void makeMove() {
		//System.out.println("make move");
		if (b.gameOver()) {
			//System.out.println("game over.  do restart");
			myApply(null);
			return;
		}
		if (human != null) {
			//System.out.println("make move for human, waiting");
			human.set(b);
			human.setLive(true);
		}
		else if (brainType == SMART) {
			//System.out.println("make move for comp, calculating");

			long timeout = getTimeOut(TIMELIMIT);
			String m = bestMove(b, 0, 0);
			for (int a = 0; !checkTimeOut(timeout); a++) {
				m = bestMove(b, a, timeout);
			}
			//System.out.println("make move for comp="+m);
			myApply(m);
			send(m);
		}
		else if (brainType == RAND) {
			//System.out.println("make move for rand");
			Vector v = b.getAllMoves();
			String m = null;
			if (v.size()!=0) m=(String) (v.elementAt((int) (Math.random() * v.size())));
			System.out.println("make move for rand="+m);
			myApply(m);
			send(m);
		}
		else {
			System.out.println("unknown make move");
		}
	}


	public static void main(String s[]) {
		if (s.length==0) {
			System.out.println("FORMAT: java NetPlayer <host> <port> <gamename> <smart,rand,human,watch>");
			System.exit(0);
		}
		try {
			new NetPlayer(s[0],Integer.parseInt(s[1]), s[2], s[3]);
		}
		catch (Exception E) {
			System.out.println("FORMAT: java NetPlayer <host> <port> <gamename> <smart,rand,human,watch>");
			System.out.println("MyError: " + E);
			System.exit(0);
		}
	}


	long timeLimit=1000;

	static long getTimeOut(int timeLimit) {
		return System.currentTimeMillis() + timeLimit;
	}


	static boolean checkTimeOut(long timeout) {
		return timeout < System.currentTimeMillis();
	}


	static String bestMove(Board b, int level, long timeout) {
		Vector mvs = b.getAllMoves();
		//get all moves
		if (mvs.size()==0) return null;
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
