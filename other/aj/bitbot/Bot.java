package aj.bitbot;

import java.awt.Graphics;

public class Bot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	private double x, y;

	private long lastMoveTime = -1;

	double dir;

	double speed;

	private GridGui parent;

	static double UP = Math.PI * 3 / 2;

	static double DOWN = Math.PI / 2, RIGHT = 0, LEFT = Math.PI;

	static private double dirChoice[] = { UP, DOWN, LEFT, RIGHT };

	public Bot(GridGui p) {
		parent = p;
		dir = dirChoice[(int) (Math.random() * 4)];
		speed = Math.random() * 4 + 1;
		Grid grid = parent.getGrid();
		char gridmap[][] = grid.getGrid();
		while (gridmap[(int) y][(int) x] != Grid.EMPTY) {
			this.x = (int) (Math.random() * parent.getCellSize());
			this.y = (int) (Math.random() * parent.getCellSize());
		}
		getNewDir();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void move() {
		if (lastMoveTime == -1)
			lastMoveTime = System.currentTimeMillis();
		double dt = System.currentTimeMillis() - lastMoveTime;
		lastMoveTime = System.currentTimeMillis();
		double dx = speed * dt * Math.cos(dir) / 1000;
		double dy = speed * dt * Math.sin(dir) / 1000;
		if (dir == RIGHT) {
			if (canMoveToGridPos(x + dx + 1, y)) {
				x += dx;
			} else {
				x = (int) (x + 1);
				getNewDir();
			}
		} else if (dir == LEFT) {
			if (canMoveToGridPos(x + dx, y)) {
				x += dx;
			} else {
				x = (int) x;
				getNewDir();
			}
		} else if (dir == DOWN) {
			if (canMoveToGridPos(x, y + dy + 1)) {
				y += dy;
			} else {
				y = (int) (y + 1);
				getNewDir();
			}
		} else if (dir == UP) {
			if (canMoveToGridPos(x, y + dy)) {
				y += dy;
			} else {
				y = (int) y;
				getNewDir();
			}
		}
	}

	private boolean canMoveToGridPos(double xx, double yy) {
		Grid grid = parent.getGrid();
		return canMoveToGridValue(grid.getGridValue((int) xx, (int) yy));
	}

	private boolean canMoveToGridValue(char c) {
		// System.out.println("can move check="+c);
		if (c == Grid.SOLID || c == Grid.ERROR)
			return false;
		return true;
	}

	private void getNewDir() {
		x = (int) x;
		y = (int) y;
		// System.out.println("new dir called x="+x+" y="+y);
		double lastDir = dir;
		boolean canGoUp = false, canGoDown = false, canGoLeft = false, canGoRight = false;

		if (canMoveToGridPos(x + 1, y)) {
			canGoRight = true;
		}
		if (canMoveToGridPos(x - 1, y)) {
			canGoLeft = true;
		}
		if (canMoveToGridPos(x, y + 1)) {
			canGoDown = true;
		}
		if (canMoveToGridPos(x, y - 1)) {
			canGoUp = true;
		}

		if (dir == RIGHT) {
			if (canGoUp && canGoDown)
				dir = (Math.random() < .5 ? UP : DOWN);
			else if (canGoUp)
				dir = UP;
			else if (canGoDown)
				dir = DOWN;
			else
				dir = LEFT;
		} else if (dir == LEFT) {
			if (canGoUp && canGoDown)
				dir = (Math.random() < .5 ? UP : DOWN);
			else if (canGoUp)
				dir = UP;
			else if (canGoDown)
				dir = DOWN;
			else
				dir = RIGHT;
		} else if (dir == UP) {
			if (canGoRight && canGoLeft)
				dir = (Math.random() < .5 ? RIGHT : LEFT);
			else if (canGoRight)
				dir = RIGHT;
			else if (canGoLeft)
				dir = LEFT;
			else
				dir = DOWN;
		} else if (dir == DOWN) {// going right
			if (canGoRight && canGoLeft)
				dir = (Math.random() < .5 ? RIGHT : LEFT);
			else if (canGoRight)
				dir = RIGHT;
			else if (canGoLeft)
				dir = LEFT;
			else
				dir = UP;
		}
		String res = "";
		if (lastDir == RIGHT)
			res += "from=right ";
		if (lastDir == LEFT)
			res += "from=left ";
		if (lastDir == UP)
			res += "from=up ";
		if (lastDir == DOWN)
			res += "from=down ";
		if (dir == RIGHT)
			res += " to=RIGHT ";
		if (dir == LEFT)
			res += " to=LEFT ";
		if (dir == UP)
			res += " to=UP ";
		if (dir == DOWN)
			res += " to=DOWN ";
		res += " (";
		if (canGoUp)
			res += "up,";
		if (canGoDown)
			res += "down,";
		if (canGoRight)
			res += "right,";
		if (canGoLeft)
			res += "left";
		res += ") ";
		// System.out.println(" u="+canGoUp+" d="+canGoDown+" l="+canGoLeft+"
		// r="+canGoRight+" "+res);
		if (dir != lastDir) {
			// System.out.println("dirChange "+res);
		}
	}

	public void draw(Graphics g, int scale) {
		g.translate((int) (x * scale), (int) (y * scale));
		g.drawOval(-scale / 2, -scale / 2, scale, scale);
		g.translate(-(int) (x * scale), -(int) (y * scale));
	}

	public void skipMove() {
		if (lastMoveTime == -1)
			lastMoveTime = System.currentTimeMillis();
		lastMoveTime = System.currentTimeMillis();
	}
}
