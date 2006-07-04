package aj.robot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import aj.misc.Stuff;

/**
 * @author judda
 * @created April 12, 2000
 */
public class Robot extends ArenaItem {
	static int ROBOTDIAMETER = 7, DAMAGEWIDTH = 6;

	int heading, currheading, speed, currspeed, damage;

	String name, id;

	Arena arena;

	public String getName() {
		return name;
	}

	// A hidenseek10000.new 9889 4219 89 89 100 100 226 88
	// B ../robot-obj/rosie 9446 3093 156 156 100 100 80 13 0.40
	public Robot(String s, Arena a) {
		arena = a;
		String t[] = Stuff.getTokens(s);
		name = t[1];
		id = t[0];
		setX(Integer.parseInt(t[2]));
		setY(Integer.parseInt(t[3]));
		heading = Integer.parseInt(t[4]);
		currheading = Integer.parseInt(t[5]);
		speed = Integer.parseInt(t[6]);
		currspeed = Integer.parseInt(t[7]);
		damage = Integer.parseInt(t[9]);
	}

	public String getId() {
		return id;
	}

	public boolean isVisible() {
		double count = arena.getArenaTime();
		if (count < getTime() || count > getTime() + Arena.MAXROBOTAGE) {
			return false;
		}
		return true;
	}

	public void display(Graphics g) {
		double count = arena.getArenaTime();
		int x = (int) (getX(arena.getScale()));
		int y = (int) (getY(arena.getScale()));
		Color c = null;
		if (id.equals("A")) {
			c = Arena.fade(Color.blue, count, getTime(), Arena.MAXROBOTAGE);
			if (c == null)
				return;
			c = Color.blue;
			g.setColor(c);
		} else {
			c = Arena.fade(Color.red, count, getTime(), Arena.MAXROBOTAGE);
			if (c == null)
				return;
			c = Color.red;
			g.setColor(c);
		}
		if (!isVisible()) {
			if (count < getTime())
				g.setColor(Arena.fade(c, 6, 2, 6));
			else
				g.setColor(Arena.fade(c, 5, 2, 6));
			g.drawOval(x, y, 1, 1);
			return;
		}
		// draw robot circle
		g.drawOval(x - ROBOTDIAMETER / 2, y - ROBOTDIAMETER / 2, ROBOTDIAMETER,
				ROBOTDIAMETER);
		// draw speed
		if (currspeed != 0) {
			Arrow AAA = new Arrow(new Point(x, y), new Point(x
					+ (int) (currspeed / 2 * Math.cos(Stuff.dtr(currheading))),
					y
							+ (int) (currspeed / 2 * Math.sin(Stuff
									.dtr(currheading)))), 4, 6);
			AAA.setFill(true);
			AAA.display(g, 1);
		}
		// draw damage
		g.fillRect((int) (x + ROBOTDIAMETER), (int) (y - 100 / 4),
				DAMAGEWIDTH - 2, (int) (damage / 2));
		// draw full damage line
		g.drawRect((int) (x + ROBOTDIAMETER), (int) (y - 100 / 4),
				DAMAGEWIDTH - 2, (int) (100 / 2));
		// draw heading
		Arrow AA = new Arrow(new Point(x, y), new Point(x
				+ (int) (ROBOTDIAMETER * 2 * Math.cos(Stuff.dtr(currheading))),
				y
						+ (int) (ROBOTDIAMETER * 2 * Math.sin(Stuff
								.dtr(currheading)))), 4, 6);
		AA.setFill(true);
		AA.display(g, 1);

		c = Arena.fade(Color.black, count, getTime(), Arena.MAXROBOTAGE);
		if (c == null)
			return;
		g.setColor(c);
		// g.drawLine(x, y, x + (int) (ROBOTDIAMETER *
		// Math.cos(Stuff.dtr(currheading))),
		// y + (int) (ROBOTDIAMETER * Math.sin(Stuff.dtr(currheading))));
		// boundary of arena
	}

	public String toString() {
		int x = (int) (getX(arena.getScale()));
		int y = (int) (getY(arena.getScale()));
		return "Robot " + id + " at " + x + " " + y;
	}
}
