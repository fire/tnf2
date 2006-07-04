package aj.bitbot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPanel;

public class GridPanel extends JPanel implements MouseListener {

	private int SCALE = 30;

	GridGui parent;

	public GridPanel(GridGui parent) {
		this.parent = parent;
		addMouseListener(this);
	}

	public void paint(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		// g.translate(getWidth() / 2, getHeight() / 2);
		Player p = parent.getPlayer();
		Grid grid = parent.getGrid();
		char gridMap[][] = grid.getGrid();

		g.translate(getWidth() / 2 - (int) p.getX() * SCALE, getHeight() / 2
				- (int) p.getY() * SCALE);

		// double x = p.getX();
		// double y = p.getY();
		// g.translate(-(int) (x * SCALE), -(int) (y * SCALE));
		for (int a = 0; a < gridMap.length; a++) {
			for (int b = 0; b < gridMap[a].length; b++) {
				g.translate(b * SCALE, a * SCALE);
				if (gridMap[a][b] == Grid.SOLID) {
					g.setColor(Color.gray);
					g.fillRect((int) (-SCALE / 2), (int) (-SCALE / 2), SCALE,
							SCALE);
					g.setColor(Color.black);
					g.drawRect((int) (-SCALE / 2), (int) (-SCALE / 2), SCALE,
							SCALE);
				}
				if (gridMap[a][b] == Grid.DOOR) {
					g.drawLine((int) (-SCALE / 2), (int) (-SCALE / 2),
							(int) (+SCALE / 2), (int) (+SCALE / 2));
					g.drawLine((int) (+SCALE / 2), (int) (-SCALE / 2),
							(int) (-SCALE / 2), (int) (+SCALE / 2));
				}
				g.translate(-b * SCALE, -a * SCALE);
			}
		}
		Vector bots = parent.getBots();
		for (int a = 0; a < bots.size(); a++) {
			Bot bot = (Bot) bots.elementAt(a);
			bot.draw(g, SCALE);
		}
		p.draw(g, SCALE);
	}

	public void mouseClicked(MouseEvent e) {
		parent.moveActive = !parent.moveActive;
		Point c = e.getPoint();
		double x = (c.getX() - getWidth() / 2) / 30 + parent.getPlayer().getX();
		double y = (c.getY() - getHeight() / 2) / 30
				+ parent.getPlayer().getY();

		// // System.out.println("raw clicked "+c);
		// c=new
		// Point((int)(c.getX()-getWidth()/2),(int)(c.getY()-getHeight()/2));
		// // System.out.println("screen adjust clicked "+c);
		// c=new
		// Point((int)Math.round(c.getX()/30),(int)Math.round(c.getY()/30));
		// // System.out.println("cell clicked "+c);
		// c=new
		// Point((int)(c.getX()+parent.getPlayer().getX()),(int)(c.getY()+parent.getPlayer().getY()));
		System.out.println("actual clicked " + x + " " + y);
		// Bot bot=new Bot(parent);
		// boolean up=bot.nextSquareOpen(x,y,Bot.UP);
		// boolean down=bot.nextSquareOpen(x,y,Bot.DOWN);
		// boolean left=bot.nextSquareOpen(x,y,Bot.LEFT);
		// boolean right=bot.nextSquareOpen(x,y,Bot.RIGHT);
		for (int a = -1; a < 2; a++) {
			for (int b = -1; b < 2; b++) {
				System.out.print((char) parent.getGrid().getGridValue(
						(int) (x + b), (int) (y + a)));
			}
			System.out.println("");
		}
		// for (int a=-1;a<2;a++) {
		// if (a==0 && left && right ) {
		// System.out.println(" l r ");
		// }
		// else if (a==0 && right ) {
		// System.out.println(" r ");
		// }
		// else if (a==0 && left ) {
		// System.out.println(" l ");
		// }
		// else
		// System.out.println(" ");
		// for (int b=-1;b<2;b++) {
		// if (a==-1 && b==0 && up) System.out.print("u");
		// else if (a==1 && b==0 && down) System.out.print("d");
		// else System.out.print(" ");
		// System.out.print((char)parent.getGrid().getGridValue((int)(x+b),(int)(y+a)));
		// if (a==-1 && b==0 && up) System.out.print("u");
		// else if (a==1 && b==0 && down) System.out.print("d");
		// else System.out.print(" ");
		// }
		// System.out.println("");
		// if (a==0 && left && right ) {
		// System.out.println(" l r ");
		// }
		// else if (a==0 && right ) {
		// System.out.println(" r ");
		// }
		// else if (a==0 && left ) {
		// System.out.println(" l ");
		// }
		// else
		// System.out.println(" ");
		// }

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
