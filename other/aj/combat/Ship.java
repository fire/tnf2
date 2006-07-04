package aj.combat;

import java.awt.Color;
import java.awt.Graphics;

import aj.misc.Stuff;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Ship extends Thing implements CombatItem {

	private boolean alive = true;

	private static int shotCount = 0;

	public static String shipType = "S";

	private ShipShape shipShape;

	private String playerName;

	private int colorIndex = 0;

	public static Color shipColors[] = { Color.white, Color.yellow, Color.pink,
			Color.lightGray, Color.BLUE, Color.green, Color.cyan, Color.MAGENTA };

	public static String shipColorNames[] = { "White", "Yello", "Pink",
			"LightGray", "Blue", "Green", "Cyan", "Magenta" };

	private static int explosionCount = 0;

	private static int defaultcolorindex = 0;

	/**
	 * Constructor for the Ship object
	 * 
	 * @param id
	 *            Description of Parameter
	 * @param x
	 *            Description of Parameter
	 * @param y
	 *            Description of Parameter
	 * @param d
	 *            Description of Parameter
	 * @param vx
	 *            Description of Parameter
	 * @param vy
	 *            Description of Parameter
	 */
	public Ship(String id, double x, double y, double d, double vx, double vy,
			int shapeId, String playername, int colorInd) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = d;
		this.size = Player.SHIPSIZE;
		this.vx = vx;
		this.vy = vy;
		this.time = System.currentTimeMillis();
		this.shipShape = new ShipShape(shapeId);
		this.playerName = playername;
		this.colorIndex = colorInd;
	}

	public boolean isAlive() {
		return alive;
	}

	/**
	 * Sets the Alive attribute of the Ship object
	 * 
	 * @param b
	 *            The new Alive value
	 */
	public void setAlive(boolean b) {
		alive = b;
		if (alive) {
			x = Math.random() * MapView.ARENASIZE;
			y = Math.random() * MapView.ARENASIZE;
			vx = 0;
			vy = 0;
			dir = Math.random() * 360;
		}
	}

	public void setRandomShipShape() {
		shipShape = new ShipShape(
				ShipShape.shipTypeSpace[(int) (ShipShape.shipTypeSpace.length * Math
						.random())]);
		colorIndex = (int) (Math.random() * 4);
	}

	public Explosion explode() {
		Explosion e = new Explosion(id + "E" + (explosionCount++), x, y, 0, vx,
				vy);
		return e;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public Shot shootLaser() {
		return new Shot(id + "S" + (shotCount++), x + size * Math.cos(dir), y
				+ size * Math.sin(dir), dir, vx + Player.ACTUALMAXSHOTSPEED
				* Math.cos(dir), vy + Player.ACTUALMAXSHOTSPEED * Math.sin(dir));
	}

	public Missile shootMissile() {
		return new Missile(id + "M" + (shotCount++), x + size * Math.cos(dir),
				y + size * Math.sin(dir), dir, vx + Player.ACTUALMAXSHOTSPEED
						* Math.cos(dir), vy + Player.ACTUALMAXSHOTSPEED
						* Math.sin(dir));
	}

	/**
	 * Description of the Method
	 */
	public void fix() {
		double len = vx * vx + vy * vy;
		len = Math.sqrt(len);
		if (len > Player.MAXSHIPSPEED) {
			vx = vx / len * Player.MAXSHIPSPEED;
			vy = vy / len * Player.MAXSHIPSPEED;
		}
		vx = vx * Player.FRICTION;
		vy = vy * Player.FRICTION;
		if (dir > Math.PI * 2) {
			dir -= Math.PI * 2;
		}
		if (dir < 0) {
			dir += Math.PI * 2;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 */
	public void display(Graphics g) {
		fix();
		g.setColor(Color.gray);
		// g.drawOval((int) (x - size / 2), (int) (y - size / 2), size, size);
		if (!alive) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.white);
			g.setColor(shipColors[colorIndex]);
		}
		shipShape.rotate(dir);
		g.translate((int) x, (int) y);
		g.drawPolygon(shipShape.shape);
		g.drawString(playerName, 10, -10);
		g.translate(-(int) x, -(int) y);

		// Arrow a = new Arrow(new Point((int) (x - mx), (int) (y - my)),
		// new Point((int) (x + mx), (int) (y + my)), size / 4, size, true);
		// a.display(g, 1.0);
	}

	/**
	 * Description of the Method
	 * 
	 * @param id
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Ship rand(String id) {
		double ndir = ((int) (Math.random() * 360));
		int shapeid = ShipShape.shipTypeSpace[(int) (ShipShape.shipTypeSpace.length * Math
				.random())];
		return new Ship(id, Math.random() * MapView.ARENASIZE, Math.random()
				* MapView.ARENASIZE, ndir, 0, 0, shapeid, "", defaultcolorindex);
	}

	public static CombatItem parse(String[] t) {
		double x = Double.parseDouble(t[2]);
		double y = Double.parseDouble(t[3]);
		double dir = Double.parseDouble(t[4]);
		double vx = Double.parseDouble(t[5]);
		double vy = Double.parseDouble(t[6]);
		int shapeId = (int) (Double.parseDouble(t[7]));
		String name = t[8];
		int colorIndex = (int) (Double.parseDouble(t[9]));
		return new Ship(t[1], x, y, dir, vx, vy, shapeId, name, colorIndex);
	}

	public String toString() {
		return shipType + " " + id + " " + Stuff.trunc(x, 1) + " "
				+ Stuff.trunc(y, 1) + " " + Stuff.trunc(dir, 2) + " "
				+ Stuff.trunc(vx, 3) + " " + Stuff.trunc(vy, 3) + " "
				+ shipShape.index + " " + playerName + " " + colorIndex + " ";
	}

	public void copyShipVals(Ship ship) {
		shipShape = ship.shipShape;
		if (ship.playerName != null && ship.playerName.length() != 0)
			playerName = ship.playerName;
		colorIndex = ship.colorIndex;
	}

	public void setPlayerName(String playerDisplayName) {
		playerName = playerDisplayName;
	}

	public void setColorIndex(int selectedItem) {
		colorIndex = selectedItem;
	}

	public int getColorIndex() {
		return colorIndex;
	}

	public String getPlayerName() {
		return playerName;
	}

}
