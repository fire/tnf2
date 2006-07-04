package aj.gnubi;

/*
 * Created on Jun 29, 2003
 *
 * @author winme
 *
 */
import java.util.Vector;

public class Control implements Runnable {

	Vector movables = new Vector();

	Player player;

	static int minDelay = 50;

	public Control(MapCell mc, Player p) {
		player = p;
	}

	public void run() {
		while (true) {
			long ts = System.currentTimeMillis();
			// do all moves
			Gnubi.player.doMove();
			// do redraws
			Gnubi.dis.redraw();
			Gnubi.dis.repaint();
			long delay = (ts + minDelay) - System.currentTimeMillis();
			// System.out.println("Min sl="+minDelay+" sleep="+(delay));
			if (delay > 0) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
