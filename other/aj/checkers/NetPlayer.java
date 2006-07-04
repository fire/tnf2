package aj.checkers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class NetPlayer implements ActionListener {

	Socket NL;

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

	/**
	 * Constructor for the NetPlayer object
	 * 
	 * @param nl
	 *            Description of Parameter
	 * @param gname
	 *            Description of Parameter
	 * @param brain
	 *            Description of Parameter
	 */
	public NetPlayer(final Socket nl, String gname, String brain) {
		if (brain.toUpperCase().indexOf(SMART) >= 0) {
			brainType = SMART;
		}
		if (brain.toUpperCase().indexOf(RAND) >= 0) {
			brainType = RAND;
		}
		if (brain.toUpperCase().indexOf(HUMAN) >= 0) {
			brainType = HUMAN;
			human = new HumanPlayer();
			human.addActionListener(this);
		}
		if (brain.toUpperCase().indexOf(WATCH) >= 0) {
			brainType = WATCH;
			human = new HumanPlayer();
			human.addActionListener(this);
		}
		this.gname = gname;
		NL = nl;
		new Thread() {
			public void run() {
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(nl
							.getInputStream()));
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				while (true) {
					String s;
					try {
						s = br.readLine();
						if (s == null)
							break;
						actionPerformed(NL, s);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}.start();
		if (brainType == WATCH) {
			send(NL, "__MODIFY name:" + gname + " pass:BLANK max:3 refill:true");
		}
		send(NL, "__JOIN name:" + gname);
	}

	public void send(Socket S, String line) {
		line += "\n";
		try {
			OutputStream o = S.getOutputStream();
			o.write(line.getBytes());
			o.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param ae
	 *            Description of Parameter
	 */
	public synchronized void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		if (ae.getSource() == human) {
			myApply(s);
			send(NL, s);
			return;
		}
	}

	public synchronized void actionPerformed(Socket NL, String s) {

		if (s.startsWith("__")) {
			// system command
			if (s.toUpperCase().startsWith("__JOINED ")) {
				playernumber = Integer.parseInt(s.substring(s.lastIndexOf(" "))
						.trim());
				if (s.toUpperCase().indexOf("FULL") > 0) {
					send(NL, "START");
					begin();
				}
			} else if (s.toUpperCase().indexOf("FAILED") >= 0
					&& s.toUpperCase().indexOf("FULL") < 0) {
				send(NL, "__CREATE name:" + gname
						+ " pass:BLANK  max:2 refill:true");
				send(NL, "__JOIN name:" + gname);
			} else if (s.toUpperCase().indexOf("FAILED") >= 0) {
				System.out.println("Game full.  Try later");
				System.exit(0);
			}
		} else if (s.equalsIgnoreCase("START")) {
			begin();
		} else if (s.equalsIgnoreCase("DRAW")) {
			System.out.println("Draw");
			b = null;
			begin();
		} else if (moves > 300) {
			if (!viewonly) {
				send(NL, "DRAW");
			}
			System.out.println("Draw");
			b = null;
			begin();
		} else {
			if (b.getMoves().contains(s)) {
				myApply(s);
				if (b.gameOver()) {
					gameOver();
				}
				moves++;
				makeMove();
			} else {
				System.out.println("invalid move detected. restart connection");
				send(NL, "START");
				begin();
			}
		}
	}

	/**
	 * Description of the Method
	 */
	public void begin() {
		if (b != null) {
			myApply(null);
			System.out.println("I win! " + moves);
		}
		moves = 0;
		b = Board.newBoard();
		if (human != null) {
			human.set(b);
		}
		if (playernumber == 1) {
			makeMove();
		}
	}

	/**
	 * Description of the Method
	 */
	public void gameOver() {
		System.out.println("I lose " + moves);
		myApply(null);
		b = null;
		begin();
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public void myApply(String s) {
		if (s == null) {
			if (b.getNextMove() == Board.RED) {
				allGame += "RED LOSE";
			}
			if (b.getNextMove() == Board.WHITE) {
				allGame += "WHITE LOSE";
			}
			System.out.println(allGame);
			allGame = "";
			return;
		}
		allGame += s + ";";
		b = b.applyMove(s);
		// update human view
	}

	/**
	 * Description of the Method
	 */
	public void makeMove() {
		if (b.gameOver()) {
			myApply(null);
			return;
		}
		if (human != null) {
			human.set(b);
			human.setLive(true);
		} else if (brainType == SMART) {
			long timeout = SmartPlayer.getTimeOut(TIMELIMIT);
			String m = SmartPlayer.bestMove(b, 0, 0);
			for (int a = 0; !SmartPlayer.checkTimeOut(timeout); a++) {
				m = SmartPlayer.bestMove(b, a, timeout);
			}
			myApply(m);
			send(NL, m);
		} else if (brainType == RAND) {
			Vector v = b.getMoves();
			String m = (String) (v.elementAt((int) (Math.random() * v.size())));
			myApply(m);
			send(NL, m);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public static void main(String s[]) {
		try {
			Socket nl = new Socket(s[0], Integer.parseInt(s[1]));
			new NetPlayer(nl, s[2], s[3]);
		} catch (Exception E) {
			System.out
					.println("FORMAT: java NetPlayer <host> <port> <gamename> <smart,rand,human,watch>");
			System.out.println("MyError: " + E);
			System.exit(0);
		}
	}

}
