package aj.robot;

import java.awt.Color;
import java.awt.Graphics;

import aj.misc.Stuff;

/**
 * @author judda
 * @created April 12, 2000
 */
public class Scan extends ArenaItem {
	static int SIZE = 50, DIR = 20, WIDE = 4;

	String id;

	int dir, width;

	int ran;

	Arena arena;

	// A 178.42 scan(226,10) returns 0
	public Scan(String s, Arena a) {
		arena = a;
		String t[] = Stuff.getTokens(s);
		id = t[0];
		setTime(Stuff.parseDouble(t[1]));
		dir = Integer.parseInt(t[3]);
		width = Integer.parseInt(t[4]);
		ran = Integer.parseInt(t[6]);
	}

	public String getId() {
		return id;
	}

	public void display(Graphics g) {
		double count = arena.getArenaTime();
		if (count < getTime() || count > getTime() + Arena.MAXSCANAGE) {
			return;
		}
		int x = (int) (getX(arena.getScale()));
		int y = (int) (getY(arena.getScale()));
		int ran = (int) (this.ran * arena.getScale());
		Color c = Arena.fade(Color.green, count, getTime(), Arena.MAXSCANAGE);
		if (id.equals("B")) {
			c = Arena.fade(Color.red, count, getTime(), Arena.MAXSCANAGE);
		}
		if (c == null)
			return;
		g.setColor(c);
		if (ran > 0) {
			g.drawArc(x - ran, y - ran, ran * 2, ran * 2, (360 - dir) - width,
					width * 2);
			g.setColor(Color.black);
			g.drawString("" + width, (int) (x + Math
					.cos(Stuff.dtr(dir - width))
					* ran), (int) (y + Math.sin(Stuff.dtr(dir - width)) * ran));
			g.setColor(c);
		}
		// 360-dir for inverted y down canvas
		g.drawLine(x, y, x
				+ (int) (10000 * arena.getScale() * Math.cos(Stuff.dtr(dir
						- width))), y
				+ (int) (10000 * arena.getScale() * Math.sin(Stuff.dtr(dir
						- width))));
		g.drawLine(x, y, x
				+ (int) (10000 * arena.getScale() * Math.cos(Stuff.dtr(dir
						+ width))), y
				+ (int) (10000 * arena.getScale() * Math.sin(Stuff.dtr(dir
						+ width))));
	}

	public String toString() {
		int x = (int) (getX(arena.getScale()));
		int y = (int) (getY(arena.getScale()));
		return "Scan at " + x + " " + y + " ran=" + ran + " dir=" + dir
				+ " wid=" + width;
	}

}
