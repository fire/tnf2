/*
 * Created on May 1, 2006
 *
 */
package aj.bitbot;

public class LogicPuzzle {

	private static final String FREE = "   ";

	public static final String CLOSED = "|| ", NEWOPEN = "  >", OPEN = "---",
			MERGETOP = "-\\ ", MERGEBOT = "-/ ", BRANCH = "<  ",
			OPENINGTOP = " /-", OPENINGBOT = " \\-";

	private static final int EMPTY = 0, CONNECTED = 1, BROKEN = 2;

	public static final String AND = " A>", OR = " O>", XOR = "XO>",
			NAND = "NA>", NOR = "NO>";

	public String gateList[] = { AND, OR, XOR, NAND, NOR };

	public String[][] getGrid() {
		return grid;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogicPuzzle lp = new LogicPuzzle(20, 5, 20);
		// lp.generateRandomLogicPuzzle();

		lp.connectReport();
		lp.markConnections();
		lp.setGates();
		lp.show();
	}

	public LogicPuzzle(int height, int width, int gates) {
		generateRandomLogicPuzzle(height, width, gates);

	}

	private void connectReport() {
		int connected = 0, empty = 0, broken = 0;
		for (int a = 0; a < grid.length; a++) {
			int x = getStatus(a, grid[a].length - 1);

			if (x == CONNECTED) {
				connected++;
			}
			if (x == EMPTY) {
				empty++;
			}
			if (x == BROKEN) {
				broken++;
			}
		}

		System.out.print("Report con=" + connected + " broken=" + broken
				+ " empty=" + empty);
	}

	public void markConnections() {
		for (int a = 0; a < grid.length; a++) {
			int x = getStatus(a, grid[a].length - 1);

			if (x == CONNECTED) {
				grid[a][grid[a].length - 1] += "C";
			}
			if (x == EMPTY) {
				grid[a][grid[a].length - 1] += "E";
			}
			if (x == BROKEN) {
				grid[a][grid[a].length - 1] += "B";
			}
		}
	}

	private int getStatus(int a, int i) {
		if (i == -1)
			return CONNECTED;
		if (grid[a][i] == FREE && i == grid[a].length - 1)
			return EMPTY;
		if (grid[a][i] == FREE && i != grid[a].length - 1)
			return BROKEN;
		if (grid[a][i] == OPEN)
			return getStatus(a, i - 1);
		if (grid[a][i] == BRANCH)
			return BROKEN;
		if (grid[a][i] == CLOSED)
			return BROKEN;
		if (grid[a][i] == NEWOPEN) {
			int x = getStatus(a - 1, i - 1);
			int y = getStatus(a + 1, i - 1);
			if (x == CONNECTED || y == CONNECTED)
				return CONNECTED;
			return BROKEN;
		}
		if (grid[a][i] == OPENINGBOT)
			return getStatus(a - 1, i - 1);
		if (grid[a][i] == OPENINGTOP)
			return getStatus(a + 1, i - 1);
		return BROKEN;
	}

	String grid[][] = {};

	public void show() {
		System.out.println("Grid layout display");
		for (int a = 0; a < grid.length; a++) {
			for (int b = 0; b < grid[a].length; b++) {
				System.out.print(grid[a][b]);
			}
			System.out.println();
		}
	}

	public void setGates() {
		for (int aa = 0; aa < grid.length; aa++) {
			for (int b = 0; b < grid[aa].length; b++) {
				if (grid[aa][b] == NEWOPEN) {
					int gate = (int) (Math.random() * gateList.length);
					grid[aa][b] = gateList[gate];
				}
			}
		}
	}

	private void generateRandomLogicPuzzle(int height, int width, int gates) {
		grid = new String[height][];
		for (int a = 0; a < height; a++) {
			grid[a] = new String[width];
			for (int b = 0; b < width; b++) {
				grid[a][b] = FREE;
			}
		}
		for (int a = 0; a < gates; a++) {
			boolean isBranch = Math.random() < .5;
			int nextHeight = (int) (Math.random() * (height - 2));
			int nextWidth = (int) ((Math.random() * (width - 2)) + 1);
			// if (!isBranch) nextHeight=(int) (Math.random() * (height - 3));
			if (!isBranch && grid[nextHeight][nextWidth - 1] == FREE
					&& grid[nextHeight + 1][nextWidth - 1] == FREE
					&& grid[nextHeight + 2][nextWidth - 1] == FREE
					&& grid[nextHeight][nextWidth] == FREE
					&& grid[nextHeight + 1][nextWidth] == FREE
					&& grid[nextHeight + 2][nextWidth] == FREE

			) {
				grid[nextHeight + 1][nextWidth - 1] = CLOSED;
				grid[nextHeight + 1][nextWidth - 1] = OPEN;
				grid[nextHeight + 1][nextWidth - 1] = CLOSED;
				grid[nextHeight][nextWidth] = MERGETOP;
				grid[nextHeight + 1][nextWidth] = NEWOPEN;
				grid[nextHeight + 2][nextWidth] = MERGEBOT;
			} else if (isBranch && grid[nextHeight][nextWidth - 1] == FREE
					&& grid[nextHeight + 1][nextWidth - 1] == FREE
					&& grid[nextHeight + 2][nextWidth - 1] == FREE
					&& grid[nextHeight][nextWidth] == FREE
					&& grid[nextHeight + 1][nextWidth] == FREE
					&& grid[nextHeight + 2][nextWidth] == FREE) {
				grid[nextHeight][nextWidth - 1] = CLOSED;
				grid[nextHeight + 1][nextWidth - 1] = OPEN;
				grid[nextHeight + 2][nextWidth - 1] = CLOSED;
				grid[nextHeight][nextWidth] = OPENINGTOP;
				grid[nextHeight + 1][nextWidth] = BRANCH;
				grid[nextHeight + 2][nextWidth] = OPENINGBOT;
			} else {
				continue;

			}
			// a--;
		}
		for (int aa = 0; aa < grid.length; aa++) {
			for (int b = 0; b < grid[aa].length; b++) {
				if (grid[aa][b] == FREE && b == 0) {
					grid[aa][b] = OPEN;
				} else if (grid[aa][b] == FREE && grid[aa][b - 1] == OPEN) {
					grid[aa][b] = OPEN;
				} else if (grid[aa][b] == FREE && grid[aa][b - 1] == OPENINGBOT) {
					grid[aa][b] = OPEN;
				} else if (grid[aa][b] == FREE && grid[aa][b - 1] == OPENINGTOP) {
					grid[aa][b] = OPEN;
				} else if (grid[aa][b] == FREE && grid[aa][b - 1] == NEWOPEN) {
					grid[aa][b] = OPEN;
				} else if (grid[aa][b] == CLOSED && b == 0) {
					grid[aa][b] = FREE;
				} else if (grid[aa][b] == CLOSED && grid[aa][b - 1] == BRANCH) {
					grid[aa][b] = FREE;
					// } else if (grid[aa][b] == CLOSED && grid[aa][b - 1] ==
					// OPEN) {
					// grid[aa][b] = FREE;
				} else if (grid[aa][b] == CLOSED && grid[aa][b - 1] == MERGEBOT) {
					grid[aa][b] = FREE;
				} else if (grid[aa][b] == CLOSED && grid[aa][b - 1] == MERGETOP) {
					grid[aa][b] = FREE;
				}
			}
		}

	}

}
