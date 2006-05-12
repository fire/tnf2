package aj.bitbot;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JPanel;

public class GridPanel extends JPanel {

	private int SCALE = 30;

	GridGui parent;

	public GridPanel(GridGui parent) {
		this.parent = parent;
	}

	public void paint(Graphics g) {
		g.clearRect(0,0,getWidth(),getHeight());
		g.translate(getWidth() / 2, getHeight() / 2);
		Player p = parent.getPlayer();
		Grid grid = parent.getGrid();
		char gridMap[][] = grid.getGrid();
		double x = p.getX();
		double y = p.getY();
		g.translate(-(int) (x * SCALE), -(int) (y * SCALE));
		for (int a = 0; a < gridMap.length; a++) {
			for (int b = 0; b < gridMap[a].length; b++) {
				if (gridMap[a][b] == Grid.SOLID) {
					g.setColor(Color.gray);
					g.fillRect((int) (b * SCALE - SCALE / 2),
							(int) (a * SCALE - SCALE / 2), SCALE, SCALE);
					g.setColor(Color.black);
					g.drawRect((int) (b * SCALE - SCALE / 2),
							(int) (a * SCALE - SCALE / 2), SCALE, SCALE);
				}
				if (gridMap[a][b] == Grid.DOOR) {
					g.drawLine((int) (b * SCALE - SCALE / 2),
							(int) (a * SCALE - SCALE / 2),
							(int) (b * SCALE + SCALE / 2),
							(int) (a * SCALE + SCALE / 2));
					g.drawLine((int) (b * SCALE + SCALE / 2),
							(int) (a * SCALE - SCALE / 2),
							(int) (b * SCALE - SCALE / 2),
							(int) (a * SCALE + SCALE / 2));
				}
			}
		}
		Vector bots=parent.getBots();
		for (int a=0;a<bots.size();a++) {
			Bot bot=(Bot)bots.elementAt(a);
			bot.draw(g,SCALE);
		}
		g.setColor(Color.blue);
		g.fillOval((int) (x * SCALE - SCALE / 4),
				(int) (y * SCALE - SCALE / 4), SCALE / 2, SCALE / 2);
	}
}
