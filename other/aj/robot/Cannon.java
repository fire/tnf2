package aj.robot;

import java.awt.Color;
import java.awt.Graphics;

import aj.misc.Stuff;

/**
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Cannon extends ArenaItem {
	static int SIZE = 10;

	String id;

	int dir, ran;

	int ret;

	Arena arena;

	// B 178.35 cannon( 80,999) returns 1
	public Cannon(String s, Arena a) {
		arena = a;
		String t[] = Stuff.getTokens(s.trim());
		id = t[0];
		setTime(Stuff.parseDouble(t[1]));
		dir = Integer.parseInt(t[3]);
		ran = Integer.parseInt(t[4]);
		ret = Integer.parseInt(t[6]);
	}

	public String getId() {
		return id;
	}

	public void display(Graphics g) {
		double count = arena.getArenaTime();
		if (count < getTime() || count > getTime() + Arena.MAXCANNONAGE) {
			return;
		}
		int x = (int) (getX(arena.getScale()));
		int y = (int) (getY(arena.getScale()));
		int ran = (int) (this.ran * arena.getScale());
		x += (int) (Math.cos(Stuff.dtr(dir)) * ran);
		y += (int) (Math.sin(Stuff.dtr(dir)) * ran);
		if (ret > 0) {
			Color c = Arena.fade(Color.black, count, getTime(),
					this.ran / 1000 + 1);
			if (c == null)
				return;
			g.setColor(c);
			g.drawLine(x - SIZE, y, x + SIZE, y);
			g.drawLine(x, y - SIZE, x, y + SIZE);
		}
	}

	public String toString() {
		int x = (int) (getX(arena.getScale()));
		int y = (int) (getY(arena.getScale()));
		return id + " Cannon at " + x + " " + y + " ran=" + ran + " dir=" + dir
				+ " ret=" + ret;
	}
}
