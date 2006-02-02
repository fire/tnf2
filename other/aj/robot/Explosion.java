package aj.robot;

import java.awt.Color;
import java.awt.Graphics;

import aj.misc.Stuff;


/**
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Explosion extends ArenaItem {
	static int SIZE = 50, DIR = 20, WIDE = 4;

	String id;
	Arena arena;


	//  A hidenseek10000.new 9889 4219  89   89 100  100 226  88
	//  B ../robot-obj/rosie 9446 3093 156  156 100  100  80  13  0.40

	//  B Explosion at 9755,3996
	public Explosion(String s,Arena a) {
		arena=a;
		String t[] = Stuff.getTokens(s);
		id = t[0];
		setX(Integer.parseInt(t[3]));
		setY(Integer.parseInt(t[4]));
	}

	public void display(Graphics g) {
		double count=arena.getArenaTime();
		if (count<getTime() || count>getTime()+Arena.MAXEXPLOSIONAGE) {
			return;
		}
		int x = (int) (getX(arena.getScale()));
		int y = (int) (getY(arena.getScale()));
		int a;
		Color c=Arena.fade(Color.red,count,getTime(),Arena.MAXEXPLOSIONAGE);
		if (c==null) return;
		g.setColor(c);
		for (a = 0; a < 4; a++) {
			int b = (int) Math.pow(2, a);
			g.drawOval((int) ((x - b * SIZE * arena.getScale() / 2)  ), (int) ((y - b * SIZE  * arena.getScale()/ 2)  ), (int) (b * SIZE  * arena.getScale() ), (int) (b * SIZE * arena.getScale() ));
		}
	}

	public String toString() {
		int x = (int) (getX(arena.getScale()));
		int y = (int) (getY(arena.getScale()));
		return "Explosion at " + x + " " + y;
	}
}


